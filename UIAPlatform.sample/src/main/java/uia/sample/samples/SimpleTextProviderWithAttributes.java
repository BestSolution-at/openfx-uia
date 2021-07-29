package uia.sample.samples;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.Point2D;
import com.sun.javafx.geom.RectBounds;
import com.sun.javafx.scene.text.GlyphList;
import com.sun.javafx.scene.text.TextLayout;
import com.sun.javafx.scene.text.TextSpan;
import com.sun.javafx.tk.Toolkit;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.uia.ControlType;
import javafx.uia.ITextAttributeSupport;
import javafx.uia.ITextProvider;
import javafx.uia.ITextRangeProvider;
import javafx.uia.IUIAElement;
import javafx.uia.SupportedTextSelection;
import javafx.uia.TextAttributeValue;
import javafx.uia.TextPatternRangeEndpoint;
import javafx.uia.TextUnit;
import javafx.uia.UIA;
import uia.sample.Sample;

@SuppressWarnings("restriction")
public class SimpleTextProviderWithAttributes implements Sample { 

    class TextPart implements TextSpan {
        Font font;
        String content;
        Color foreground;

        TextPart(String content, Font font, Color foreground) {
            this.content = content;
            this.font = font;
            this.foreground = foreground;
        }

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

 
    }

    List<TextPart> content = Arrays.asList(
        new TextPart("Hello", Font.font(12), Color.BLACK),
        new TextPart(" ", Font.font(12), Color.BLACK),
        new TextPart("World", Font.font(16), Color.RED),
        new TextPart(", this is ", Font.font(12), Color.BLACK),
        new TextPart("UIA", Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14), Color.BLUE)
    );

    private TextLayout LAYOUT;

    private void layoutContent() {
        LAYOUT = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        LAYOUT.setContent(content.toArray(new TextSpan[]{}));
    }

    float baseX = 30;
    float baseY = 30;

    private void draw(GraphicsContext ctx) {
        for (GlyphList gl : LAYOUT.getRuns()) {
            TextPart part = (TextPart)gl.getTextSpan();
            ctx.setFill(part.foreground);
            ctx.setFont(part.font);
            Point2D loc = gl.getLocation();
            
            ctx.fillText(part.getText(), baseX + loc.x, baseY + loc.y);
        }
    }

    private float findXOffset(int index) {
        int idx = 0;
        for (GlyphList gl : LAYOUT.getRuns()) {
            Point2D loc = gl.getLocation();
            for (int g = 0; g < gl.getGlyphCount(); g++) {
                if (idx == index) {
                    return loc.x + gl.getPosX(g);
                }
                idx++;
            }
        }

        return LAYOUT.getBounds().getWidth();
    }

    private Bounds[] getBounds(int begin, int end) {

        BaseBounds fullBounds = LAYOUT.getBounds();

        float xBegin = findXOffset(begin);
        float xEnd = findXOffset(end);

        return new Bounds[] { new BoundingBox(xBegin, fullBounds.getMinY(), xEnd - xBegin, fullBounds.getHeight() )};

    }

    private void collect(int begin, int end, BiConsumer<Integer, TextPart> collector) {
        int index = 0;
        for (TextPart part : content) {

            for (int glyph = 0; glyph < part.getText().length(); glyph++) {
                if (index >= begin && index < end) {
                    //System.err.println("collect " + part.content.charAt(Math.min(part.content.length()-1, glyph)));
                    collector.accept(index, part);
                }
                index++;
            }
        }
    }

    private Set<Font> collectFont(int begin, int end) {
        Set<Font> result = new HashSet<>();
        collect(begin, end, (idx, part) -> result.add(part.font));
        return result;
    }

    private Bounds[] getBoundsInScreen(int begin, int end) {
        Bounds[] b = getBounds(begin, end);
        Bounds[] result = new Bounds[b.length];
        for (int i = 0; i < b.length; i++) {
            result[i] = new BoundingBox(baseX + b[i].getMinX(), baseY + b[i].getMinY(), b[i].getWidth(), b[i].getHeight());
            result[i] = canvas.localToScreen(result[i]);
        }
        return result;
    }

    class TextHelper {

        String getText() {
            return content.stream().map(part -> part.content).collect(Collectors.joining());
        }

    }

    class SimpleRange implements ITextRangeProvider {


        @Override
        public void initialize(ITextAttributeSupport context) {
            context.addFontSizeAttribute(this::getFontSize, this::findFontSizeAttribute);
            context.addFontWeightAttribute(this::getFontWeight, null);
            context.addFontNameAttribute(this::getFontName, null);
            context.addForegroundColorAttribute(this::getForegroundColor, null);
            context.addBackgroundColorAttribute(this::getBackgroundColor, null);
        }

        private ITextRangeProvider findFontSizeAttribute(boolean backward, double fontSize) {
            System.err.println("TextRange " + start + " -> " + end);
            System.err.println("findFontSizeAttribute " + backward + ", " + fontSize);

            List<Integer> foundIdx = new ArrayList<>();

            collect(start, end, (idx, part) -> {
                System.err.println("colloctor " + idx);
                final double epsilon = 0.000001d;
                if (Math.abs(fontSize - part.font.getSize()) < epsilon) {
                    foundIdx.add(idx);
                }
            });

            Integer first = null;
            Integer last = null;

            int next = 1;

            if (backward) {
                Collections.reverse(foundIdx);
                next = -1;
            }

            for (Integer fIdx : foundIdx) {
                if (first == null) {
                    first = fIdx;
                    last = fIdx;
                    continue;
                }

                if (last != null && last+next == fIdx) {
                    last = fIdx;
                    continue;
                }
            }

            System.err.println("first = " + first + " last = " + last);

            if (first != null && last != null) {
                if (backward) {
                    System.err.println("=> TextRange " + last + " -> " + first);
                    return new SimpleRange(helper, last, first);
                } else {
                    System.err.println("=> TextRange " + first + " -> " + (last+1));
                    return new SimpleRange(helper, first, last + 1);
                }
            } else {
                return null;
            }


        }

        private TextAttributeValue<Double> getFontSize() {
            Set<Font> fonts = collectFont(start, end);
            if (fonts.size() == 1) {
                return TextAttributeValue.value(fonts.iterator().next().getSize());
            } else {
                return TextAttributeValue.mixed();
            }
        }
        private TextAttributeValue<Integer> getFontWeight() {
            // TODO howto get the font weight from a javafx font?
            return TextAttributeValue.notSupported();
        }
        private TextAttributeValue<String> getFontName() {
            Set<String> fontNames = new HashSet<>();
            collect(start, end, (idx, part) -> fontNames.add(part.font.getFamily()));
            if (fontNames.size() == 1) {
                return TextAttributeValue.value(fontNames.iterator().next());
            } else {
                return TextAttributeValue.mixed();
            }
        }
        private TextAttributeValue<Color> getForegroundColor() {
            Set<Color> fg = new HashSet<>();
            collect(start, end, (idx, part) -> fg.add(part.foreground));
            if (fg.size() == 1) {
                return TextAttributeValue.value(fg.iterator().next());
            } else {
                return TextAttributeValue.mixed();
            }
        }

        private TextAttributeValue<Color> getBackgroundColor() {
            return TextAttributeValue.value(Color.WHITE);
        }


        private TextHelper helper;

        private int start;
        private int end;

        public SimpleRange(TextHelper helper, int start, int end) {
            this.helper = helper;
            this.start = start;
            this.end = end;
        }

        @Override
        public void AddToSelection() {
        }

        @Override
        public ITextRangeProvider Clone() {
            return new SimpleRange(helper, start, end);
        }

        private SimpleRange cast(ITextRangeProvider range) {
            return (SimpleRange) range;
        }

        @Override
        public boolean Compare(ITextRangeProvider other) {
            SimpleRange o = (SimpleRange) other;
            return o.start == start && o.end == end;
        }

        @Override
        public int CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange, TextPatternRangeEndpoint targetEndpoint) {
            int offset = endpoint == TextPatternRangeEndpoint.Start ? start : end;
            int targetOffset = targetEndpoint == TextPatternRangeEndpoint.Start ? cast(targetRange).start : cast(targetRange).end;
            return offset - targetOffset;
        }


        private boolean isWordStart(BreakIterator bi, String text, int offset) {
            if (offset == 0) return true;
            if (offset == text.length()) return true;
            if (offset == BreakIterator.DONE) return true;
            return bi.isBoundary(offset) && Character.isLetterOrDigit(text.charAt(offset));
        }

        @Override
        public void ExpandToEnclosingUnit(TextUnit unit) {
            String text = helper.getText();
            if (text == null) return;
            int length = text.length();
            if (length == 0) return;

            switch (unit) {
                case Character: {
                    if (start == length) start--;
                    end = start + 1;
                    break;
                }
                case Format:
                case Word: {
                    BreakIterator bi = BreakIterator.getWordInstance();
                    bi.setText(text);
                    if (!isWordStart(bi, text, start)) {
                        int offset = bi.preceding(start);
                        while (!isWordStart(bi, text, offset)) {
                            offset = bi.previous();
                        }
                        start = offset != BreakIterator.DONE ? offset : 0;
                    }
                    if (!isWordStart(bi, text, end)) {
                        int offset = bi.following(end);
                        while (!isWordStart(bi, text, offset)) {
                            offset = bi.next();
                        }
                        end = offset != BreakIterator.DONE ? offset : length;
                    }
                    break;
                }
                case Line: {
                    break;
                }
                case Paragraph: {
                    break;
                }
                case Page:
                case Document: {
                    start = 0;
                    end = length;
                    break;
                }
            }

            /* Always ensure range consistency */
            start = Math.max(0, Math.min(start, length));
            end = Math.max(start, Math.min(end, length));
        }

        @Override
        public ITextRangeProvider FindText(String text, boolean backward, boolean ignoreCase) {
            String fullText = helper.getText();
            String rangeText = fullText.substring(start, end);
            if (ignoreCase) {
                rangeText = rangeText.toLowerCase();
                text = text.toLowerCase();
            }
            int index = -1;
            if (backward) {
                index = rangeText.lastIndexOf(text);
            } else {
                index = rangeText.indexOf(text);
            }
            if (index == -1) return null;

            SimpleRange result = new SimpleRange(helper, start + index, start + index + text.length());
            return result;
        }

        @Override
        public Bounds[] GetBoundingRectangles() {
            return getBoundsInScreen(start, end);
        }

        @Override
        public IUIAElement[] GetChildren() {
           
            return null;
        }

        @Override
        public IUIAElement GetEnclosingElement() {
            return element;
        }

        @Override
        public String GetText(int maxLength) {
            int endOffset = maxLength != -1 ? Math.min(end, start + maxLength) : end;
            return helper.getText().substring(start, endOffset);
        }

        @Override
        public int Move(TextUnit unit, int requestedCount) {
            if (requestedCount == 0) return 0;
            String text = helper.getText();
            if (text == null) return 0;
            int length = text.length();
            if (length == 0) return 0;
    
            int actualCount = 0;
            switch (unit) {
                case Character: {
                    int oldStart = start;
                    start = Math.max(0, Math.min(start + requestedCount, length - 1));
                    end = start + 1;
                    actualCount = start - oldStart;
                    break;
                }
                case Format:
                case Word: {
                    BreakIterator bi = BreakIterator.getWordInstance();
                    bi.setText(text);
                    int offset = start;
                    while (!isWordStart(bi, text, offset)) {
                        offset = bi.preceding(start);
                    }
                    while (offset != BreakIterator.DONE && actualCount != requestedCount) {
                        if (requestedCount > 0) {
                            offset = bi.following(offset);
                            while (!isWordStart(bi, text, offset)) {
                                offset = bi.next();
                            }
                            actualCount++;
                        } else {
                            offset = bi.preceding(offset);
                            while (!isWordStart(bi, text, offset)) {
                                offset = bi.previous();
                            }
                            actualCount--;
                        }
                    }
                    if (actualCount != 0) {
                        if (offset != BreakIterator.DONE) {
                            start = offset;
                        } else {
                            start = requestedCount > 0 ? length : 0;
                        }
                        offset = bi.following(start);
                        while (!isWordStart(bi, text, offset)) {
                            offset = bi.next();
                        }
                        end = offset != BreakIterator.DONE ? offset : length;
                    }
                    break;
                }
                case Line: {
                    // Integer lineIndex = (Integer)getAttribute(LINE_FOR_OFFSET, start);
                    // if (lineIndex == null) return 0;
                    // int step = requestedCount > 0 ? + 1 : -1;
                    // while (requestedCount != actualCount) {
                    //     if (getAttribute(LINE_START, lineIndex + step) == null) break;
                    //     lineIndex += step;
                    //     actualCount += step;
                    // }
                    // if (actualCount != 0) {
                    //     Integer lineStart = (Integer)getAttribute(LINE_START, lineIndex);
                    //     Integer lineEnd = (Integer)getAttribute(LINE_END, lineIndex);
                    //     if (lineStart == null || lineEnd == null) return 0;
                    //     start = lineStart;
                    //     end = lineEnd;
                    // }
                    break;
                }
                case Paragraph: {
                    BreakIterator bi = BreakIterator.getSentenceInstance();
                    bi.setText(text);
                    int offset = bi.isBoundary(start) ? start : bi.preceding(start);
                    while (offset != BreakIterator.DONE && actualCount != requestedCount) {
                        if (requestedCount > 0) {
                            offset = bi.following(offset);
                            actualCount++;
                        } else {
                            offset = bi.preceding(offset);
                            actualCount--;
                        }
                    }
                    if (actualCount != 0) {
                        start = offset != BreakIterator.DONE ? offset : 0;
                        offset = bi.following(start);
                        end = offset != BreakIterator.DONE ? offset : length;
                    }
                    break;
                }
                case Page:
                case Document: {
                    /* Move not allowed/implemented - do not alter the range */
                    return 0;
                }
            }
    
            /* Always ensure range consistency */
            start = Math.max(0, Math.min(start, length));
            end = Math.max(start, Math.min(end, length));
            return actualCount;
        }

        @Override
        public void MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange, TextPatternRangeEndpoint targetEndpoint) {
            String text = helper.getText();
            if (text == null) return;
            int length = text.length();
    
            int offset = targetEndpoint == TextPatternRangeEndpoint.Start ? cast(targetRange).start : cast(targetRange).end;
            if (endpoint == TextPatternRangeEndpoint.Start) {
                start = offset;
            } else {
                end = offset;
            }
            if (start > end) {
                start = end = offset;
            }
    
            /* Always ensure range consistency */
            start = Math.max(0, Math.min(start, length));
            end = Math.max(start, Math.min(end, length));
        }

        @Override
        public int MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int requestedCount) {
            if (requestedCount == 0) return 0;
            String text = helper.getText();
            if (text == null) return 0;
            int length = text.length();
    
            int actualCount = 0;
            int offset = endpoint == TextPatternRangeEndpoint.Start ? start : end;
            switch (unit) {
                case Character: {
                    int oldOffset = offset;
                    offset = Math.max(0, Math.min(offset + requestedCount, length));
                    actualCount = offset - oldOffset;
                    break;
                }
                case Format:
                case Word: {
                    BreakIterator bi = BreakIterator.getWordInstance();
                    bi.setText(text);
                    while (offset != BreakIterator.DONE && actualCount != requestedCount) {
                        if (requestedCount > 0) {
                            offset = bi.following(offset);
                            while (!isWordStart(bi, text, offset)) {
                                offset = bi.next();
                            }
                            actualCount++;
                        } else {
                            offset = bi.preceding(offset);
                            while (!isWordStart(bi, text, offset)) {
                                offset = bi.previous();
                            }
                            actualCount--;
                        }
                    }
                    if (offset == BreakIterator.DONE) {
                        offset = requestedCount > 0 ? length : 0;
                    }
                    break;
                }
                case Line: {
                    // Integer lineIndex = (Integer)getAttribute(LINE_FOR_OFFSET, offset);
                    // Integer lineStart = (Integer)getAttribute(LINE_START, lineIndex);
                    // Integer lineEnd = (Integer)getAttribute(LINE_END, lineIndex);
                    // if (lineIndex == null || lineStart == null || lineEnd == null) {
                    //     /* Text field - move within the text */
                    //     offset = requestedCount > 0 ? length : 0;
                    //     break;
                    // }
                    // int step = requestedCount > 0 ? + 1 : -1;
                    // int endOffset = requestedCount > 0 ? lineEnd : lineStart;
                    // if (offset != endOffset) {
                    //     /* Count current line when not traversal start in middle of the line */
                    //     actualCount += step;
                    // }
                    // while (requestedCount != actualCount) {
                    //     if (getAttribute(LINE_START, lineIndex + step) == null) break;
                    //     lineIndex += step;
                    //     actualCount += step;
                    // }
                    // if (actualCount != 0) {
                    //     lineStart = (Integer)getAttribute(LINE_START, lineIndex);
                    //     lineEnd = (Integer)getAttribute(LINE_END, lineIndex);
                    //     if (lineStart == null || lineEnd == null) return 0;
                    //     offset = requestedCount > 0 ? lineEnd : lineStart;
                    // }
                    break;
                }
                case Paragraph: {
                    BreakIterator bi = BreakIterator.getSentenceInstance();
                    bi.setText(text);
                    while (offset != BreakIterator.DONE && actualCount != requestedCount) {
                        if (requestedCount > 0) {
                            offset = bi.following(offset);
                            actualCount++;
                        } else {
                            offset = bi.preceding(offset);
                            actualCount--;
                        }
                    }
                    if (offset == BreakIterator.DONE) {
                        offset = requestedCount > 0 ? length : 0;
                    }
                    break;
                }
                case Page:
                case Document: {
                    /* Move not allowed/implemented - do not alter the range */
                    return 0;
                }
            }
            if (endpoint == TextPatternRangeEndpoint.Start) {
                start = offset;
            } else {
                end = offset;
            }
            if (start > end) {
                start = end = offset;
            }
    
            /* Always ensure range consistency */
            start = Math.max(0, Math.min(start, length));
            end = Math.max(start, Math.min(end, length));
            return actualCount;
        }

        @Override
        public void RemoveFromSelection() {
            
        }

        @Override
        public void ScrollIntoView(boolean alignToTop) {
            
        }

        @Override
        public void Select() {
            
        }

    }

    private TextElement element;

    private Label desc;

    class TextElement implements IUIAElement, ITextProvider {
        TextHelper helper;

        public TextElement(TextHelper helper) {
            this.helper = helper;
        }

        @Override
        public ControlType getControlType() {
            return ControlType.UIA_TextControlTypeId;
        }

        @Override
        public ITextRangeProvider get_DocumentRange() {
            return new SimpleRange(helper, 0, helper.getText().length());
        }

        @Override
        public SupportedTextSelection get_SupportedTextSelection() {
            return SupportedTextSelection.None;
        }

        @Override
        public ITextRangeProvider[] GetSelection() {
            return new ITextRangeProvider[] { };
        }

        @Override
        public ITextRangeProvider[] GetVisibleRanges() {
            return new ITextRangeProvider[] { get_DocumentRange() };
        }

        @Override
        public ITextRangeProvider RangeFromChild(IUIAElement childElement) {
            return null;
        }

        @Override
        public ITextRangeProvider RangeFromPoint(javafx.geometry.Point2D point) {
            javafx.geometry.Point2D pickPoint = canvas.screenToLocal(point);
            com.sun.javafx.scene.text.HitInfo hitInfo = LAYOUT.getHitInfo((float) pickPoint.getX() - baseX, (float) pickPoint.getY() - baseY);
            System.err.println("hitInfo: " + hitInfo);
            if (hitInfo != null) {
                int idx = hitInfo.getCharIndex();
                idx = Math.min(helper.getText().length()-1, idx);
                if (!hitInfo.isLeading()) {
                    idx+=1;
                }
                return new SimpleRange(helper, idx, idx);
            } else {
                return null;
            }
        }

        @Override
        public Bounds getBounds() {
            return canvas.localToScreen(canvas.getLayoutBounds());
        }

        @Override
        public void SetFocus() {
            canvas.requestFocus();            
        }

    }
    BorderPane area;
    Canvas canvas;
    public SimpleTextProviderWithAttributes() {

        TextElement el = new TextElement(new TextHelper());

        canvas = new Canvas(400, 50) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return el;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };

        layoutContent();

        GraphicsContext ctx = canvas.getGraphicsContext2D();
        draw(ctx);




        desc = new Label("Simple ITextProvider & ITextRangeProvider sample with attributes. Also FindAttribute is implemented for font size. The sample uses the font sizes 12, 14 and 16.");
        desc.setWrapText(true);
    }


    @Override
    public String getName() {
        return "ITextProvider w/ Attributes";
    }

    @Override
    public Node getDescription() {
       
        return desc;
    }

    @Override
    public Node getSample() {
        return canvas;
    }

    @Override
    public Node getControls() {
        return null;
    }

}