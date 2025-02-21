/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package uia.sample.text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.sun.javafx.geom.PathIterator;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.text.TextRun;

import at.bestsolution.uia.javafx.uia.IUIAElement;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.FillRule;
import javafx.scene.text.Font;
import uia.sample.samples.model.IDrawable;


// this class uses the javafx internal TextLayout
@SuppressWarnings("restriction")
public class LayoutHelper {

    public static interface Embedded {
        //Image getImage();

        IUIAElement getElement();
    }

    static abstract class Fragment implements com.sun.javafx.scene.text.TextSpan {
        String content;

        public Fragment(String content) {
            this.content = content;
        }

        @Override
        public String getText() {
            return content;
        }
    }
    static class TextFragment extends Fragment {

        Font font;
        Color color;

        public TextFragment(String content, Font font, Color color) {
            super(content);
            this.font = font;
            this.color = color;
        }

        @Override
        public com.sun.javafx.geom.RectBounds getBounds() {
            return null;

        }

        @Override
        @SuppressWarnings("deprecation")
        public Object getFont() {
            // return font.impl_getNativeFont();
            return com.sun.javafx.scene.text.FontHelper.getNativeFont(font);
        }

    }
    static class ImageFragment extends Fragment implements Embedded {
        Image image;
        double baseline;
        com.sun.javafx.geom.RectBounds bounds;

        ImageFragment(Image image, double baseline) {
            super("\uFFFC");
            this.image = image;
            this.baseline = baseline;

            float minX = 0f;
            float minY = (float) -baseline;
            float maxX = minX + (float) image.getWidth();
            float maxY = minY + (float) image.getHeight();
            this.bounds = new com.sun.javafx.geom.RectBounds(minX, minY, maxX, maxY);
        }

        //@Override
        public Image getImage() {
            return image;
        }

        @Override
        public com.sun.javafx.geom.RectBounds getBounds() {
            return bounds;
        }

        @Override
        public Object getFont() {
            return null;
        }

        @Override
        public IUIAElement getElement() {
            return null;
        }
    }
    static class DrawableFragment extends Fragment implements Embedded {

        IDrawable drawable;

        DrawableFragment(IDrawable drawable) {
            super("\uFFFC");
            this.drawable = drawable;
        }

        public IDrawable getDrawable() {
            return drawable;
        }

        @Override
        public RectBounds getBounds() {
            Bounds bounds = drawable.getRenderBounds();
            return new RectBounds((float) bounds.getMinX(), (float) bounds.getMinY(), (float) bounds.getMaxX(), (float) bounds.getMaxY());
        }

        @Override
        public Object getFont() {
            return null;
        }

        @Override
        public IUIAElement getElement() {
            return (IUIAElement) drawable;
        }

    }

    private List<Fragment> content;
    private com.sun.javafx.scene.text.TextLayout layout;

    public LayoutHelper() {
        this.content = new ArrayList<>();
        this.layout = com.sun.javafx.tk.Toolkit.getToolkit().getTextLayoutFactory().createLayout();
    }

    // public javafx.scene.text.HitInfo pick(javafx.geometry.Point2D point, Point2D base) {
    //     return layout.getHitInfo((float) (point.getX() - base.getX()), (float) (point.getY() - base.getY()), "foo", 0, 0);
    // }

    public String getText() {
        return content.stream().map(part -> part.content).collect(Collectors.joining());
    }

    public void addText(String content, Font font, Color color) {
        this.content.add(new TextFragment(content, font, color));
    }

    public void addImage(Image image, double baseline) {
        this.content.add(new ImageFragment(image, baseline));
    }

    public void addDrawable(IDrawable drawable, boolean inline) {
        if (inline) {
            this.content.add(new DrawableFragment(drawable));
        } else {
            this.content.add(new TextFragment("\n", Font.getDefault(), Color.BLACK));
            this.content.add(new DrawableFragment(drawable));
            this.content.add(new TextFragment("\n", Font.getDefault(), Color.BLACK));
        }

    }

    public void setWrapWidth(double width) {
        layout.setWrapWidth((float) width);
    }

    public void init(){
        this.content.add(new TextFragment("\n", Font.getDefault(), Color.TRANSPARENT));

        layout.setContent(content.stream().toArray(size -> new com.sun.javafx.scene.text.TextSpan[size]));


    }

    public Bounds getLayoutBounds() {
        com.sun.javafx.geom.BaseBounds r = layout.getBounds();
        return new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    public static class GlyphNfo {
        public int index;
        public Font font;
        public Color color;
        public boolean embed;
    }

    public void iterateRange(int start, int end, Consumer<GlyphNfo> consumer) {

        int index = 0;

        for (com.sun.javafx.scene.text.GlyphList gl : layout.getRuns()) {
            com.sun.javafx.text.TextRun run = (com.sun.javafx.text.TextRun) gl;

            com.sun.javafx.scene.text.TextSpan span = run.getTextSpan();

            Font font = null;
            Color color = null;
            boolean embed = false;
            if (span instanceof TextFragment) {
                font = ((TextFragment)span).font;
                color = ((TextFragment)span).color;
            } else if (span instanceof ImageFragment) {
                embed = true;
            } else if (span instanceof DrawableFragment) {
                embed = true;
            }
            for (int runIdx = 0; runIdx < run.getLength(); runIdx++) {

                if (start <= index && end > index) {
                    GlyphNfo cur = new GlyphNfo();
                    cur.index = index;
                    cur.font = font;
                    cur.color = color;
                    cur.embed = embed;
                    consumer.accept(cur);
                }

                index += 1;
            }
        }

    }

    public List<com.sun.javafx.geom.RectBounds> getRange(int start, int end) {
        float x = 0;
        float y = 0;
        float spacing = 0;

        int lineCount = layout.getLines().length;
        List<com.sun.javafx.geom.RectBounds> result = new ArrayList<>();
        float lineY = 0;

        for  (int lineIndex = 0; lineIndex < lineCount; lineIndex++) {

            com.sun.javafx.text.TextLine line = (com.sun.javafx.text.TextLine) layout.getLines()[lineIndex];
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
            */
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

                    /* Merge continuous rectangles */
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


    public void render(GraphicsContext ctx, Point2D base) {

        for (com.sun.javafx.scene.text.TextLine line : layout.getLines()) {
            for (com.sun.javafx.scene.text.GlyphList run : line.getRuns()) {
                com.sun.javafx.geom.Point2D loc = run.getLocation();

                if (run.getTextSpan() instanceof TextFragment) {
                    TextFragment textFragment = (TextFragment) run.getTextSpan();
                    ctx.setFill(textFragment.color);
                    com.sun.javafx.font.PGFont font = (com.sun.javafx.font.PGFont) textFragment.getFont();
                    com.sun.javafx.font.FontStrike strike = font.getStrike(BaseTransform.getTranslateInstance(0, 0));

                    for (int glyphIndex = 0; glyphIndex < run.getGlyphCount(); glyphIndex++) {


                        float posX = run.getPosX(glyphIndex);
                        float posY = run.getPosY(glyphIndex);
                        int glyphCode = run.getGlyphCode(glyphIndex);

                        com.sun.javafx.font.Glyph glyph = strike.getGlyph(glyphCode);
                        com.sun.javafx.geom.Shape shape = glyph.getShape();

                        com.sun.javafx.geom.PathIterator it = shape.getPathIterator(BaseTransform.IDENTITY_TRANSFORM);

                        float[] buf = new float[6];

                        double glyphX = base.getX() + loc.x + posX;
                        double glyphY = base.getY() + loc.y + posY;

                        ctx.beginPath();

                        if (it.getWindingRule() == PathIterator.WIND_NON_ZERO) {
                            ctx.setFillRule(FillRule.NON_ZERO);
                        } else {
                            ctx.setFillRule(FillRule.EVEN_ODD);
                        }

                        while (!it.isDone()) {

                            int type = it.currentSegment(buf);
                            switch (type) {

                                case PathIterator.SEG_MOVETO:
                                ctx.moveTo(glyphX + buf[0], glyphY + buf[1]);

                                break;

                                case PathIterator.SEG_LINETO:
                                ctx.lineTo(glyphX + buf[0], glyphY + buf[1]);
                                break;

                                case PathIterator.SEG_QUADTO:
                                ctx.quadraticCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3]);
                                break;

                                case PathIterator.SEG_CUBICTO:
                                ctx.bezierCurveTo(glyphX + buf[0], glyphY + buf[1], glyphX + buf[2], glyphY + buf[3], glyphX + buf[4], glyphY + buf[5]);
                                break;

                                case PathIterator.SEG_CLOSE:
                                ctx.closePath();
                                break;


                            }

                            it.next();
                        }

                        ctx.setFill(textFragment.color);
                        ctx.fill();
                    }


                }
                if (run.getTextSpan() instanceof ImageFragment) {
                    ImageFragment fragment = (ImageFragment) run.getTextSpan();

                    ctx.drawImage(fragment.image, base.getX() + loc.x + fragment.getBounds().getMinX(), base.getY()+ loc.y + fragment.getBounds().getMinY());
                }

                if (run.getTextSpan() instanceof DrawableFragment) {
                    DrawableFragment fragment = (DrawableFragment) run.getTextSpan();

                    fragment.getDrawable().layout(base.getX() + loc.x + fragment.getBounds().getMinX(), base.getY()+ loc.y + fragment.getBounds().getMinY());
                    fragment.getDrawable().render(ctx);
                }
            }
        }

    }

    public Bounds[] getBounds(int start, int end) {
        List<com.sun.javafx.geom.RectBounds> range = getRange(start, end);
        return range.stream().map(r -> new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight())).toArray(size -> new Bounds[size]);
    }

    public int getStart(Embedded embedded) {
        return
        Arrays.stream(layout.getRuns()).filter(run -> run.getTextSpan() == embedded).findFirst()
        .map(gl -> (TextRun) gl)
        .map(run -> run.getStart())
        .orElse(-1);
    }
    public int getEnd(Embedded embedded) {
        return
        Arrays.stream(layout.getRuns()).filter(run -> run.getTextSpan() == embedded).findFirst()
        .map(gl -> (TextRun) gl)
        .map(run -> run.getEnd())
        .orElse(-1);
    }

    public Bounds getBounds(Embedded embedded) {

        return
        Arrays.stream(layout.getRuns()).filter(run -> run.getTextSpan() == embedded).findFirst()
        .map(gl -> (TextRun) gl)
        .map(run -> {
            Bounds[] b = getBounds(run.getStart(), run.getEnd());
            if (b.length > 0) {
                return b[0];
            } else {
                return null;
            }
        })
        .orElse(null);
    }

    public Optional<Embedded> findEmbedded(IUIAElement el) {
        return content.stream()
            .filter(fragment -> fragment instanceof Embedded)
            .map(fragment -> (Embedded) fragment)
            .filter(emb -> emb.getElement() == el)
            .findFirst();
    }

    public Stream<Embedded> getEmbedded(int start, int end) {


        List<Embedded> result = new ArrayList<>();

        for (com.sun.javafx.scene.text.GlyphList gl : layout.getRuns()) {
            TextRun ts = (TextRun) gl;

            if (ts.getStart() >= start && ts.getEnd() <= end) {
                if (ts.isEmbedded()) {
                    if (ts.getTextSpan() instanceof ImageFragment) {
                        ImageFragment img = (ImageFragment) ts.getTextSpan();
                        result.add(img);
                    } else if (ts.getTextSpan() instanceof DrawableFragment) {
                        DrawableFragment dr = (DrawableFragment) ts.getTextSpan();
                        result.add(dr);
                    }
                }
            }

        }

        return result.stream();
    }

}
