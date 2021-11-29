package uia.sample.samples.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javafx.geom.RectBounds;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uia.sample.samples.model.TextSupport.Glyph;
import uia.sample.samples.model.TextSupport.GlyphData;


@SuppressWarnings("restriction")
public class Text extends BaseModel {

    public static interface IFragment {
        String getContent();
        com.sun.javafx.scene.text.TextSpan getTextSpan();
    }

    public static class TextFragment implements IFragment {
        protected String content;
        protected Font font;
        protected Color color;

        protected int begin;
        protected int end;

        class Span implements com.sun.javafx.scene.text.TextSpan {
            @Override
            public RectBounds getBounds() {
                return null;
            }

            @Override
            @SuppressWarnings("deprecation")
            public Object getFont() {
                return font.impl_getNativeFont();
            }

            @Override
            public String getText() {
                return content;
            }
            public TextFragment getFragment() {
                return TextFragment.this;
            }
        }

        private Span span = new Span();

        public TextFragment(String content, Font font, Color color) {
            this.content = Objects.requireNonNull(content);
            this.font = Objects.requireNonNull(font);
            this.color = Objects.requireNonNull(color);
        }

        @Override
        public String getContent() {
            return content;
        }

        @Override
        public com.sun.javafx.scene.text.TextSpan getTextSpan() {
            return span;
        }
    }

    public static class EmbedFragment implements IFragment {
        protected IModel embed;

        protected int begin;
        protected int end;

        class Span implements com.sun.javafx.scene.text.TextSpan {
            @Override
            public RectBounds getBounds() {
                return convert(embed.getLocalBounds());
            }
            @Override
            public Object getFont() {
                return null;
            }
            @Override
            public String getText() {
                return "\uFFFC";
            }
            public EmbedFragment getFragment() {
                return EmbedFragment.this;
            }
        }

        private Span span = new Span();

        public EmbedFragment(IModel embed) {
            this.embed = embed;
        }

        @Override
        public String getContent() {
            return embed.getContent();
        }

        @Override
        public com.sun.javafx.scene.text.TextSpan getTextSpan() {
            return span;
        }
    }

    private List<IFragment> content = new ArrayList<>();

    public void addText(String text, Font font, Color color) {
        content.add(new TextFragment(text, font, color));
    }
    public void addEmbed(IModel model) {
        content.add(new EmbedFragment(model));
        addChild(model);
    }

    @Override
    public int computeIndices(int curBegin) {
        int length = 0;
        begin = curBegin;

        for (IFragment fragment : content) {
            if (fragment instanceof TextFragment) {
                TextFragment textFragment = (TextFragment) fragment;
                textFragment.begin = begin + length;
                length += textFragment.content.length();
                textFragment.end = begin + length;
            } else if (fragment instanceof EmbedFragment) {
                EmbedFragment embedFragment = (EmbedFragment) fragment;
                length += embedFragment.embed.computeIndices(begin + length);
            }
        }

        end = begin + length;

        return length;
    }

    private List<Glyph> localGlyphs = new ArrayList<>();
    
    @Override
    public int computeGlyphs(int beginIndex) {
        int length = 0;
        begin = beginIndex;

       

        int index = beginIndex;

        for (IFragment fragment : content) {
            int fragmentLength = 0;

            if (fragment instanceof TextFragment) {
                TextFragment textFragment = (TextFragment) fragment;
                
                textFragment.begin = begin + length;
                fragmentLength += textFragment.content.length();
                textFragment.end = begin + length;

                List<GlyphData> data = textSupport.getFragmentData(textFragment);

                Glyph lastGlyph = null;
                //String source = textFragment.getContent();
                for (int sourceIdx = 0; sourceIdx < textFragment.content.length(); sourceIdx ++) {
                    if (sourceIdx < data.size()) {
                        GlyphData tg = data.get(sourceIdx);
                        Glyph g = new Glyph();
                        g.x = tg.x;
                        g.y = tg.y;
                        g.w = tg.w;
                        g.h = tg.h;
                        g.content = textFragment.content.substring(sourceIdx, sourceIdx + 1);
    
                        g.render = tg.render;
                        g.index = index;
                        localGlyphs.add(g);
    
                        index ++;

                        lastGlyph = g;

                    } else if (lastGlyph != null) {
                        Glyph g = new Glyph();
                        g.x = lastGlyph.x + lastGlyph.w;
                        g.y = lastGlyph.y;
                        g.w = 1;
                        g.h = lastGlyph.h;
                        g.content = textFragment.content.substring(sourceIdx, sourceIdx + 1);
    
                        g.render = (gc, color) -> {};
                        g.index = index;
                        localGlyphs.add(g);
    
                        index ++;
                    } else {
                        index ++;
                    }

                }

            } else if (fragment instanceof EmbedFragment) {
                EmbedFragment embedFragment = (EmbedFragment) fragment;
                fragmentLength += embedFragment.embed.computeGlyphs(begin + length);
                
                index += fragmentLength;
            }

            length += fragmentLength;
        }

        end = begin + length;

        return length;
    }

    @Override
    public Stream<Glyph> streamGlyphs() {
        return Stream.concat(localGlyphs.stream(),
        content.stream().filter(c -> c instanceof EmbedFragment)
            .map(c -> (EmbedFragment) c)
            .flatMap(f -> f.embed.streamGlyphs()));
    }

    private TextSupport textSupport = new TextSupport();
    
    @Override
    public void layout() {
        // first layout embeds
        content.stream()
            .filter(fragment -> fragment instanceof EmbedFragment)
            .map(fragment -> (EmbedFragment) fragment)
            .forEach(embed -> embed.embed.layout());
        

        double baseX = computeParentLayoutX() + layoutX;
        double baseY = computeParentLayoutY() + layoutY;

        textSupport = new TextSupport();
        textSupport.layout(getBegin(), baseX, baseY, layoutW, content);

        // re-layout embeds 
        content.stream()
            .filter(fragment -> fragment instanceof EmbedFragment)
            .map(fragment -> (EmbedFragment) fragment)
            .forEach(embed -> embed.embed.layout());
       
        layoutW = textSupport.getWidth();
        layoutH = textSupport.getHeight();
    }

    @Override
    public void render(GraphicsContext gc) {
        try {
            //gc.setStroke(Color.RED);
            //gc.strokeRect(layoutX, layoutY, layoutW, layoutH);
            //gc.strokeRect(layoutX + bounds.getMinX(), layoutY + bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    
            // render text
            //renderText(gc);
            textSupport.render(gc);
    
           

            // render embeds
            getModelChildren().forEach(child -> child.render(gc));
    
            renderBounds(gc); 

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 

    @Override
    public int pickText(double x, double y) {
        return textSupport.pickText(x, y);
    }
    
/*
    private void renderText(GraphicsContext ctx) {
        for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
            for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                com.sun.javafx.geom.Point2D loc = run.getLocation();

                if (run.getTextSpan() instanceof TextFragment.Span) {
                    TextFragment textFragment = ((TextFragment.Span) run.getTextSpan()).getFragment();
                    ctx.setFill(textFragment.color);
                    com.sun.javafx.font.PGFont font = (com.sun.javafx.font.PGFont) run.getTextSpan().getFont();
                    com.sun.javafx.font.FontStrike strike = font.getStrike(com.sun.javafx.geom.transform.BaseTransform.getTranslateInstance(0, 0));

                    float ascent = strike.getMetrics().getAscent();

                    for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {
                        float posX = run.getPosX(glyphIndex);
                        float posY = run.getPosY(glyphIndex);
                        int glyphCode = run.getGlyphCode(glyphIndex);

                        com.sun.javafx.font.Glyph glyph = strike.getGlyph(glyphCode);
                        com.sun.javafx.geom.Shape shape = glyph.getShape();
                        
                        com.sun.javafx.geom.PathIterator it = shape.getPathIterator(com.sun.javafx.geom.transform.BaseTransform.IDENTITY_TRANSFORM);
                        
                        float[] buf = new float[6];

                        double glyphX = computeParentLayoutX() + layoutX + loc.x + posX;
                        double glyphY = computeParentLayoutY() + layoutY + loc.y + posY - ascent;
                        
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

                        ctx.setFill(textFragment.color);
                        ctx.fill();
                    }
                    
    
                }
                // if (run.getTextSpan() instanceof ImageFragment) {
                //     ImageFragment fragment = (ImageFragment) run.getTextSpan();
    
                //     ctx.drawImage(fragment.image, base.getX() + loc.x + fragment.getBounds().getMinX(), base.getY()+ loc.y + fragment.getBounds().getMinY());
                // }

                // if (run.getTextSpan() instanceof DrawableFragment) {
                //     DrawableFragment fragment = (DrawableFragment) run.getTextSpan();

                //     fragment.getDrawable().layout(base.getX() + loc.x + fragment.getBounds().getMinX(), base.getY()+ loc.y + fragment.getBounds().getMinY());
                //     fragment.getDrawable().render(ctx);
                // }
            }
        }
    }
*/

    @Override
    public String getContent() {
        return content.stream().map(IFragment::getContent).collect(Collectors.joining());
    }



    private static RectBounds convert(Bounds bounds) {
        return new RectBounds((float) bounds.getMinX(), (float) bounds.getMinY(), (float) bounds.getMaxX(), (float) bounds.getMaxY());
    }
    public List<Glyph> getGlyphList() {
        return textSupport.getGlyphList();
    }




    

/*
    @Override
    public int pickText(double x, double y) {
        double localX = x - computeParentLayoutX() - layoutX;
        double localY = y - computeParentLayoutY() - layoutY;
        System.err.println("pickText in " + this + " @ " + localX + ", " + localY);

        double baseY = computeParentLayoutY() + layoutY - getLocalBounds().getMinY();
        double baseX = computeParentLayoutX() + layoutX;

        double lineY = 0;
        double lineHeight;

        TextFragment lastLineRun = null;

        TextFragment lastRun = null;
        com.sun.javafx.text.TextRun lastRun2 = null;
        int sameRunBuf = 0;

        for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
            RectBounds lineBounds = line.getBounds();
            lineHeight = lineBounds.getHeight();
            float lineX = lineBounds.getMinX();

            if (localY >= lineY && localY < lineY + lineHeight) {
                // hit line
                System.err.println("hit line @" + line.getStart());

                for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                    com.sun.javafx.text.TextRun run1 = (com.sun.javafx.text.TextRun) run;   

                    if (run.getTextSpan() instanceof TextFragment.Span) {
                        TextFragment text = ((TextFragment.Span) run.getTextSpan()).getFragment();
                        System.err.println("RUN " + text + " / " + lastRun);
                        if (text == lastRun) {
                            sameRunBuf += lastRun2.getLength();
                        } else {
                            sameRunBuf = 0;
                        }



                        int beforeInLine =  (lastRun != null ? lastRun2.getLength() : 0);

                        for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {
                            int realIdx = sameRunBuf + text.begin + glyphIndex; // + line.getStart();
                            
                            if (text == lastLineRun) {
                                realIdx += line.getStart();
                            }

                            int lineGlyphIndex = glyphIndex + line.getStart();
                            
                            float lineBeginX = run.getLocation().x;

                            float glyphX = lineBeginX + run1.getXAtOffset(glyphIndex , true);
                            float advance = run1.getAdvance(glyphIndex);
                            System.err.println("GLYPH " + glyphIndex + ": " + glyphX + "("+advance+")" + " vs " + localX);
                            
                            if (localX >= glyphX && localX < glyphX + advance / 2d) {
                                // first half of glyph

                                System.err.println("sameRunBuf: " + sameRunBuf);
                                System.err.println("text.begin: " + text.begin);
                                System.err.println("glyphIndex: " + glyphIndex);
                                System.err.println("line.getStart(): " + line.getStart());

                                return realIdx;
                            }
                            if (localX >= glyphX + advance / 2d && localX < glyphX + advance) {
                                // second half of glyph
                                return realIdx + 1;
                            }
                        }

                        lastRun = text;
                        lastRun2 = run1;

                        lastLineRun = text;
                    }

                    if (run.getTextSpan() instanceof EmbedFragment.Span) {
                        // TODO
                    }


                }


            }
            lineY += lineHeight;
        }

        return -1;
    }
*/

    @Override
    public List<Bounds> getTextBounds(int begin, int end) {
        List<Bounds> perGlyph = textSupport.getTextBounds(begin, end);
        return merge(perGlyph);
    }

/*
    @Override
    public List<Bounds> getTextBounds(int begin, int end) {
        if (getBegin() <= end && getEnd() >= begin) {
            List<Bounds> result = new ArrayList<>();

            //System.err.println("OVERLAP " + getContent());

            double baseY = computeParentLayoutY() + layoutY - getLocalBounds().getMinY();
            double baseX = computeParentLayoutX() + layoutX;

            double lineY = 0;
            double lineHeight;

            TextFragment lastRun = null;
            com.sun.javafx.text.TextRun lastRun2 = null;
            int sameRunBuf = 0;

            for (com.sun.javafx.scene.text.TextLine line : textLayout.getLines()) {
                RectBounds lineBounds = line.getBounds();
                lineHeight = lineBounds.getHeight();
                float lineX = lineBounds.getMinX();
                for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                    com.sun.javafx.text.TextRun run1 = (com.sun.javafx.text.TextRun) run;   
                    float ascent = run1.getAscent();   

                    if (run.getTextSpan() instanceof TextFragment.Span) {
                        TextFragment text = ((TextFragment.Span) run.getTextSpan()).getFragment();
                        //System.err.println("text: " + text + ", lastRun = " + lastRun);
                        if (text == lastRun) {
                            sameRunBuf += lastRun2.getLength();
                        } else {
                            sameRunBuf = 0;
                        }
                      
                        float left = -1;
                        float right = -1;

                        for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {
                            int realIdx = sameRunBuf + text.begin + glyphIndex;
                            if (realIdx >= begin && realIdx <= end) {
                                // add glyph
                                if (left == -1) left = lineX + run1.getXAtOffset(glyphIndex, true);
                                right = lineX + run1.getXAtOffset(glyphIndex, true);
                            }
                            
                        }

                        if (sameRunBuf + text.begin + run.getGlyphCount() <= end) {
                            right = lineX + run1.getWidth(); 
                        }


                        if (left != -1 && right != -1) {
                            result.add(new BoundingBox(baseX + left, baseY + lineY, right - left, lineHeight));
                        }


                        lastRun = text;
                        lastRun2 = run1;
                    }

                    if (run.getTextSpan() instanceof EmbedFragment.Span) {
                        EmbedFragment embed = ((EmbedFragment.Span) run.getTextSpan()).getFragment();
                        embed.embed.getBegin();
                        embed.embed.getEnd();
                        //System.err.println(" >> ");
                        result.addAll(embed.embed.getTextBounds(begin, end));
                    }

                    lineX += run1.getWidth();
                }
                lineY += lineHeight;
            }

            return result;
        }
        return Collections.emptyList();
    }
*/

/*
    public List<com.sun.javafx.geom.RectBounds> getRange(int start, int end) {
        float x = 0;
        float y = 0;
        float spacing = 0;

        int lineCount = textLayout.getLines().length;
        List<com.sun.javafx.geom.RectBounds> result = new ArrayList<>();
        float lineY = 0;

        for  (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {
            
            com.sun.javafx.text.TextLine line = (com.sun.javafx.text.TextLine) textLayout.getLines()[lineIndex];
            com.sun.javafx.geom.RectBounds lineBounds = line.getBounds();
            int lineStart = line.getStart();
            if (lineStart >= end) break;
            int lineEnd = lineStart + line.getLength();
            if (start > lineEnd) {
                lineY += lineBounds.getHeight() + spacing;
                continue;
            }

            /* The list of runs in the line is visually ordered.
            * Thus, finding the run that includes the selection end offset
            * does not mean that all selected runs have being visited.
            * Instead, this implementation first computes the number of selected
            * characters in the current line, then iterates over the runs consuming
            * selected characters till all of them are found.
            *//*
            com.sun.javafx.text.TextRun[] runs = line.getRuns();
            int count = Math.min(lineEnd, end) - Math.max(lineStart, start);
            int runIndex = 0;
            float left = -1;
            float right = -1;
            float lineX = lineBounds.getMinX();
            while (count > 0 && runIndex < runs.length) {
                com.sun.javafx.text.TextRun run = runs[runIndex];
                int runStart = run.getStart();
                int runEnd = run.getEnd();
                float runWidth = run.getWidth();
                int clmapStart = Math.max(runStart, Math.min(start, runEnd));
                int clampEnd = Math.max(runStart, Math.min(end, runEnd));
                int runCount = clampEnd - clmapStart;
                if (runCount != 0) {
                    boolean ltr = run.isLeftToRight();
                    float runLeft;
                    if (runStart > start) {
                        runLeft = ltr ? lineX : lineX + runWidth;
                    } else {
                        runLeft = lineX + run.getXAtOffset(start - runStart, true);
                    }
                    float runRight;
                    if (runEnd < end) {
                        runRight = ltr ? lineX + runWidth : lineX;
                    } else {
                        runRight = lineX + run.getXAtOffset(end - runStart, true);
                    }
                    if (runLeft > runRight) {
                        float tmp = runLeft;
                        runLeft = runRight;
                        runRight = tmp;
                    }
                    count -= runCount;
                    float top = 0, bottom = 0;

                    //TYPE_TEXT
                //    top = lineY;
                //    bottom = lineY + lineBounds.getHeight();

                    top = lineY + lineBounds.getMinY();
                    bottom = lineY + lineBounds.getMinY() + lineBounds.getHeight();
                    
                    // Merge continuous rectangles
                    if (runLeft != right) {
                        if (left != -1 && right != -1) {
                            float l = left, r = right;
                            // if (isMirrored()) {
                            //     float width = getMirroringWidth();
                            //     l = width - l;
                            //     r = width - r;
                            // }

                            result.add(new com.sun.javafx.geom.RectBounds(x + l, y + top, x + r, y + bottom));
                        }
                        left = runLeft;
                        right = runRight;
                    }
                    right = runRight;
                    if (count == 0) {
                        float l = left, r = right;
                        // if (isMirrored()) {
                        //     float width = getMirroringWidth();
                        //     l = width - l;
                        //     r = width - r;
                        // }
                        
                        result.add(new com.sun.javafx.geom.RectBounds(x + l, y + top, x + r, y + bottom));
                    }
                }
                lineX += runWidth;
                runIndex++;
            }
            lineY += lineBounds.getHeight() + spacing;
        }
        return result;
    }
    */
}
