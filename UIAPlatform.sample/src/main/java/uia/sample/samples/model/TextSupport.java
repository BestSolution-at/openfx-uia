package uia.sample.samples.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sun.javafx.geom.RectBounds;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import uia.sample.samples.model.Text.EmbedFragment;
import uia.sample.samples.model.Text.IFragment;
import uia.sample.samples.model.Text.TextFragment;

public class TextSupport {
    
    @FunctionalInterface
    public static interface RenderGlyph {
        void render(GraphicsContext gc, Color color);
    }

    public static class Glyph {
        public int index;
        public double x;
        public double y;
        public double w;
        public double h;
        public String content;

        public TextFragment text;

        public RenderGlyph render;

        public boolean contains(double x, double y) {
            return x >= this.x && x < this.x + this.w &&
            y >= this.y && y < this.y + this.h;
        }
    }

    public static class GlyphData {
        public double x;
        public double y;
        public double w;
        public double h;
        public TextFragment text;
        public RenderGlyph render;
    }

    private Map<TextFragment, List<GlyphData>> glyphData = new HashMap<>();

    private List<Glyph> glyphList = new ArrayList<>();
    private com.sun.javafx.scene.text.TextLayout textLayout;

    public void layout(int beginIndex, double baseX, double baseY, double layoutW, List<IFragment> content) {

        if (content.isEmpty()) {
            System.err.println("Cannot layout nothing");
            return;
        }

        System.err.println("Layout text " + this);
        textLayout = com.sun.javafx.tk.Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        if (layoutW != 0) {
            textLayout.setWrapWidth((float) layoutW);
        }
        textLayout.setContent(content.stream().map(IFragment::getTextSpan).toArray(size -> new com.sun.javafx.scene.text.TextSpan[size]));

        computeGlyphData(baseX, baseY);

        glyphList.clear();
        glyphList.addAll(computeGlyphList(beginIndex, baseX, baseY));

        // position embeds
        for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
            com.sun.javafx.text.TextLine intLine = (com.sun.javafx.text.TextLine) line;
            for (com.sun.javafx.text.TextRun run : intLine.getRuns()) {
                if (run.getTextSpan() instanceof EmbedFragment.Span) {
                    EmbedFragment frag = ((EmbedFragment.Span) run.getTextSpan()).getFragment();
                    com.sun.javafx.geom.Point2D loc = run.getLocation();
                    ((BaseModel) frag.embed).layoutX = loc.x;
                    ((BaseModel) frag.embed).layoutY = loc.y;
                }
            }
        }

    }

    public void render(GraphicsContext gc) {
        //System.err.println("render " + this + ": " + glyphList.size() + " glyphs");
        for (Glyph g : glyphList) {
            g.render.render(gc, null);
        }   
    }

    public double getWidth() {
        return textLayout.getBounds().getWidth();
    }

    public double getHeight() {
        return textLayout.getBounds().getHeight();
    }

    public List<Bounds> getTextBounds(int begin, int end) {
        return glyphList.stream().filter(g -> g.index >= begin && g.index <= end)
        .map(g -> new BoundingBox(g.x, g.y, g.w, g.h)).collect(Collectors.toList());
    }

    public int pickText(double x, double y) {
        Optional<Glyph> glyph = glyphList.stream().filter(g -> g.contains(x, y)).findFirst();
        if (glyph.isPresent()) {
            return glyph.get().index;
        } else {
            return -1;
        }
    }

    private void computeGlyphData(double baseX, double baseY) {
        double lineY = 0;
        double lineHeight;
        for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
            RectBounds lineBounds = line.getBounds();
            lineHeight = lineBounds.getHeight();
            float lineX = lineBounds.getMinX();
            double lineXOffset = 0;
            for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                com.sun.javafx.text.TextRun run1 = (com.sun.javafx.text.TextRun) run;
                com.sun.javafx.geom.Point2D loc = run.getLocation();

                if (run.getTextSpan() instanceof TextFragment.Span) {
                    TextFragment text = ((TextFragment.Span) run.getTextSpan()).getFragment();
                    List<GlyphData> fragmentGlyphs = glyphData.computeIfAbsent(text, t -> new ArrayList<>());

                    com.sun.javafx.font.PGFont font = (com.sun.javafx.font.PGFont) run.getTextSpan().getFont();
                    com.sun.javafx.font.FontStrike strike = font.getStrike(com.sun.javafx.geom.transform.BaseTransform.getTranslateInstance(0, 0));
                    float ascent = strike.getMetrics().getAscent();

                    for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {

                        final GlyphData data = new GlyphData();
                        data.y = baseY + lineY;
                        data.h = lineHeight;

                        data.x = baseX + lineXOffset + run1.getXAtOffset(glyphIndex, true);
                        data.w = run1.getAdvance(glyphIndex);

                        data.text = text;


                        float posX = run.getPosX(glyphIndex);
                        float posY = run.getPosY(glyphIndex);
                        int glyphCode = run.getGlyphCode(glyphIndex);

                        com.sun.javafx.font.Glyph glyph = strike.getGlyph(glyphCode);
                        com.sun.javafx.geom.Shape shape = glyph.getShape();
                        
                        
                        float[] buf = new float[6];


                        double glyphX = baseX + loc.x + posX;
                        double glyphY = baseY + loc.y + posY - ascent;
                        
                        data.render = (ctx, color) -> {
                            com.sun.javafx.geom.PathIterator it = shape.getPathIterator(com.sun.javafx.geom.transform.BaseTransform.IDENTITY_TRANSFORM);
                        
                            ctx.beginPath();
    
                            if (it.getWindingRule() == com.sun.javafx.geom.PathIterator.WIND_NON_ZERO) {
                                ctx.setFillRule(FillRule.NON_ZERO);
                            } else {
                                ctx.setFillRule(FillRule.EVEN_ODD);
                            }
    
                            while (!it.isDone()) {
                               
                                int type = it.currentSegment(buf);
                                switch (type) {
    
                                    case com.sun.javafx.geom.PathIterator.SEG_MOVETO:
                                    ctx.moveTo(glyphX + buf[0], glyphY + buf[1]);
    
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_LINETO:
                                    ctx.lineTo(glyphX + buf[0], glyphY + buf[1]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_QUADTO:
                                    ctx.quadraticCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_CUBICTO:
                                    ctx.bezierCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3], glyphX + buf[4], glyphY + buf[5]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_CLOSE:
                                    ctx.closePath();
                                    break;
    
                                    
                                }
    
                                it.next();
                            }
    
                            if (color != null) {
                                ctx.setFill(color);
                            } else {
                                ctx.setFill(text.color);
                            }
                            ctx.fill();
                        };

                        fragmentGlyphs.add(data);

                    }
                }
                lineXOffset += run.getWidth();
            }

            lineY += lineHeight;
        }

    }


    private List<Glyph> computeGlyphList(int beginIndex, double baseX, double baseY) {
        List<Glyph> glyphs = new ArrayList<>();

        double lineY = 0;
        double lineHeight;

        int index = beginIndex;

        for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
            RectBounds lineBounds = line.getBounds();
            lineHeight = lineBounds.getHeight();
            float lineX = lineBounds.getMinX();

            double lineXOffset = 0;

            for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                com.sun.javafx.text.TextRun run1 = (com.sun.javafx.text.TextRun) run;
                com.sun.javafx.geom.Point2D loc = run.getLocation();


                if (run.getTextSpan() instanceof TextFragment.Span) {
                    TextFragment text = ((TextFragment.Span) run.getTextSpan()).getFragment();

                    com.sun.javafx.font.PGFont font = (com.sun.javafx.font.PGFont) run.getTextSpan().getFont();
                    com.sun.javafx.font.FontStrike strike = font.getStrike(com.sun.javafx.geom.transform.BaseTransform.getTranslateInstance(0, 0));
                    float ascent = strike.getMetrics().getAscent();

                    for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {
                        //int realIdx = sameRunBuf + text.begin + glyphIndex; // + line.getStart();

                        final Glyph emit = new Glyph();
                        emit.index = index;

                        emit.y = baseY + lineY;
                        emit.h = lineHeight;

                        emit.x = baseX + lineXOffset + run1.getXAtOffset(glyphIndex, true);
                        emit.w = run1.getAdvance(glyphIndex);

                        emit.text = text;


                        float posX = run.getPosX(glyphIndex);
                        float posY = run.getPosY(glyphIndex);
                        int glyphCode = run.getGlyphCode(glyphIndex);

                        com.sun.javafx.font.Glyph glyph = strike.getGlyph(glyphCode);
                        com.sun.javafx.geom.Shape shape = glyph.getShape();
                        
                        
                        float[] buf = new float[6];


                        double glyphX = baseX + loc.x + posX;
                        double glyphY = baseY + loc.y + posY - ascent;
                        
                        emit.render = (ctx, color) -> {
                            com.sun.javafx.geom.PathIterator it = shape.getPathIterator(com.sun.javafx.geom.transform.BaseTransform.IDENTITY_TRANSFORM);
                        
                            ctx.beginPath();
    
                            if (it.getWindingRule() == com.sun.javafx.geom.PathIterator.WIND_NON_ZERO) {
                                ctx.setFillRule(FillRule.NON_ZERO);
                            } else {
                                ctx.setFillRule(FillRule.EVEN_ODD);
                            }
    
                            while (!it.isDone()) {
                               
                                int type = it.currentSegment(buf);
                                switch (type) {
    
                                    case com.sun.javafx.geom.PathIterator.SEG_MOVETO:
                                    ctx.moveTo(glyphX + buf[0], glyphY + buf[1]);
    
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_LINETO:
                                    ctx.lineTo(glyphX + buf[0], glyphY + buf[1]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_QUADTO:
                                    ctx.quadraticCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_CUBICTO:
                                    ctx.bezierCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3], glyphX + buf[4], glyphY + buf[5]);
                                    break;
    
                                    case com.sun.javafx.geom.PathIterator.SEG_CLOSE:
                                    ctx.closePath();
                                    break;
    
                                    
                                }
    
                                it.next();
                            }
    
                            if (color != null) {
                                ctx.setFill(color);
                            } else {
                                ctx.setFill(text.color);
                            }
                            ctx.fill();
                        };


                        glyphs.add(emit);

                        index++;
                    }

                }

                lineXOffset += run.getWidth();
            }

            lineY += lineHeight;
        }




        return glyphs;
    }

	public List<Glyph> getGlyphList() {
		return glyphList;
	}

    public List<GlyphData> getFragmentData(TextFragment textFragment) {
        return glyphData.get(textFragment);
    }
}
