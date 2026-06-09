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
package uia.sample.samples;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import at.bestsolution.uia.ControlType;
import at.bestsolution.uia.ITextAttributeSupport;
import at.bestsolution.uia.ITextProvider;
import at.bestsolution.uia.ITextRangeProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.SupportedTextSelection;
import at.bestsolution.uia.TextAttributeValue;
import at.bestsolution.uia.TextPatternRangeEndpoint;
import at.bestsolution.uia.TextUnit;
import at.bestsolution.uia.UIA;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import uia.sample.Sample;
import uia.sample.text.LayoutHelper;
import uia.sample.text.LayoutHelper.Embedded;

public class SimpleTextProviderWithChildren implements Sample {

    // class TextPart implements TextSpan {
    //     Font font;
    //     String content;
    //     Color foreground;

    //     TextPart(String content, Font font, Color foreground) {
    //         this.content = content;
    //         this.font = font;
    //         this.foreground = foreground;
    //     }

    //     @Override
    //     public RectBounds getBounds() {
    //         return null;
    //     }

    //     @Override
    //     @SuppressWarnings("deprecation")
    //     public Object getFont() {
    //         return font.impl_getNativeFont();
    //     }

    //     @Override
    //     public String getText() {
    //         return content;
    //     }

    //     @Override
    //     public String toString() {
    //         return "Text("+content+")";
    //     }
    // }

    // class ImageTextPart extends TextPart {

    //     Image image;
    //     RectBounds bounds;

    //     public ImageTextPart(Image image, double baseline) {
    //         super("\uFFFC", null, null);

    //         this.image = image;

    //         float minX = 0f;
    //         float minY = (float) -baseline;
    //         float maxX = minX + (float) image.getWidth();
    //         float maxY = minY + (float) image.getHeight();
    //         bounds = new RectBounds(minX, minY, maxX, maxY);
    //     }

    //     @Override
    //     public RectBounds getBounds() {
    //         return bounds;
    //     }

    //     @Override
    //     public Object getFont() {
    //         return null;
    //     }

    //     @Override
    //     public String toString() {
    //         return "Image()";
    //     }
    // }

    // List<TextPart> content = Arrays.asList(
    //     new TextPart("Hello", Font.font(12), Color.BLACK),
    //     new TextPart(" ", Font.font(12), Color.BLACK),
    //     new TextPart("World", Font.font(16), Color.RED),
    //     new TextPart(" ", Font.font(12), Color.BLACK),
    //     new ImageTextPart(new Image("/smiley.png"), 27),
    //     new TextPart("\n, this is ", Font.font(12), Color.BLACK),
    //     new TextPart("UIA", Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14), Color.BLUE)
    // );

    LayoutHelper helper = new LayoutHelper();
    {
        helper.addText("Hello", Font.font(12), Color.BLACK);
        helper.addText(" ", Font.font(12), Color.BLACK);
        helper.addText("World", Font.font(16), Color.RED);
        helper.addText(" ", Font.font(12), Color.BLACK);
        helper.addImage(new Image("/face-smile-32.png"), 27);
        helper.addText(",\nthis is ", Font.font(12), Color.BLACK);
        helper.addText("UIA", Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 14), Color.BLUE);
        helper.addText("\nLorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.", Font.font(10), Color.DARKGOLDENROD);

        helper.setWrapWidth(300);

        helper.init();
    }

    // private TextLayout LAYOUT;

    // private void layoutContent() {
        // LAYOUT = Toolkit.getToolkit().getTextLayoutFactory().createLayout();
        // LAYOUT.setContent(content.toArray(new TextSpan[]{}));
    // }

    float baseX = 30;
    float baseY = 30;
    javafx.geometry.Point2D base = new javafx.geometry.Point2D(baseX, baseY);

    private void draw(GraphicsContext ctx) {
        helper.render(ctx, base);
    }

    private Bounds[] getBounds(int begin, int end) {
        return helper.getBounds(begin, end);
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
            List<Integer> foundIdx = new ArrayList<>();

            helper.iterateRange(start, end, (nfo) -> {
                double s = nfo.font == null ? 0 : nfo.font.getSize();
                final double epsilon = 0.000001d;
                if (Math.abs(fontSize - s) < epsilon) {
                    foundIdx.add(nfo.index);
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

            if (first != null && last != null) {
                if (backward) {
                    return new SimpleRange(textSupplier, last, first);
                } else {
                    return new SimpleRange(textSupplier, first, last + 1);
                }
            } else {
                return null;
            }


        }

        private TextAttributeValue<Double> getFontSize() {
            // this is a very simple implementation which only checks on the identity of the font object
            Set<Font> fonts = new HashSet<>();

            helper.iterateRange(start, end, nfo -> {
                fonts.add(nfo.font);
            });

            return toSingleValue(fonts, Font::getSize);
        }

        private TextAttributeValue<Integer> getFontWeight() {
            // TODO howto get the font weight from a javafx font?
            return TextAttributeValue.notSupported();
        }

        private TextAttributeValue<String> getFontName() {
            Set<String> fontNames = new HashSet<>();

            helper.iterateRange(start, end, nfo -> {
                fontNames.add(nfo.font == null ? "<none>" : nfo.font.getFamily());
            });

            return toSingleValue(fontNames);
        }

        private TextAttributeValue<Color> getForegroundColor() {
            Set<Color> fg = new HashSet<>();
            helper.iterateRange(start, end, nfo -> {
                fg.add(nfo.color);
            });
            return toSingleValue(fg);
        }

        private TextAttributeValue<Color> getBackgroundColor() {
            return TextAttributeValue.value(Color.WHITE);
        }

        private <U, T> TextAttributeValue<U> toSingleValue(Set<T> found, Function<T, U> map) {
            if (found.size() == 1) {
                T result = found.iterator().next();
                if (result == null) {
                    return TextAttributeValue.notSupported();
                } else {
                    U mappedResult = map.apply(result);
                    if (mappedResult == null) {
                        return TextAttributeValue.notSupported();
                    } else {
                        return TextAttributeValue.value(mappedResult);
                    }
                }
            } else {
                return TextAttributeValue.mixed();
            }
        }

        private <T> TextAttributeValue<T> toSingleValue(Set<T> found) {
            if (found.size() == 1) {
                T result = found.iterator().next();
                if (result == null) {
                    return TextAttributeValue.notSupported();
                } else {
                    return TextAttributeValue.value(result);
                }
            } else {
                return TextAttributeValue.mixed();
            }
        }

        private Supplier<String> textSupplier;

        private int start;
        private int end;

        public SimpleRange(Supplier<String> textSupplier, int start, int end) {
            this.textSupplier = textSupplier;
            this.start = start;
            this.end = end;
        }

        @Override
        public void AddToSelection() {
        }

        @Override
        public ITextRangeProvider Clone() {
            return new SimpleRange(textSupplier, start, end);
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
            return bi.isBoundary(offset) && (text.charAt(offset) == '\uFFFC' || Character.isLetterOrDigit(text.charAt(offset)));
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

            SimpleRange result = new SimpleRange(textSupplier, start + index, start + index + text.length());
            return result;
        }

        @Override
        public Bounds[] GetBoundingRectangles() {
            return getBoundsInScreen(start, end);
        }

        @Override
        public IUIAElement[] GetChildren() {
            return SimpleTextProviderWithChildren.this.helper.getEmbedded(start, end).map(embedded -> childEls.computeIfAbsent(embedded, ChildElement::new)).toArray(size -> new IUIAElement[size]);
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

                    bi.setText(text); //.replaceAll("\uFFFC", " "));
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

    class ChildElement implements /*IUIAElement,*/ IUIAVirtualElement {

        private Embedded embedded;

        public ChildElement(Embedded embedded) {
            this.embedded = embedded;
        }

        public ControlType getControlType() {
            return ControlType.UIA_ImageControlTypeId;
        }

        @Override
        public void initialize(at.bestsolution.uia.IInitContext init) {
            init.addNameProperty(() -> "Smiley Emoji");
            init.addIsControlElementProperty(() -> true);
            init.addIsContentElementProperty(() -> true);
        }

        @Override
        public IUIAElement getParent() {
            return element;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public Bounds getBounds() {
            Bounds localBounds =  helper.getBounds(embedded);

            return canvas.localToScreen(moveBounds(localBounds, base));
        }

        @Override
        public void SetFocus() {
        }

    }

    private Bounds moveBounds(Bounds bounds, javafx.geometry.Point2D delta) {
        return new BoundingBox(delta.getX() + bounds.getMinX(), delta.getY() + bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    private Map<Embedded, ChildElement> childEls = new HashMap<>();

    class TextElement implements /*IUIAElement,*/ IUIAVirtualRootElement, ITextProvider {
        Supplier<String> textSupplier;

        public TextElement(Supplier<String> textSupplier) {
            this.textSupplier = textSupplier;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return helper.getEmbedded(0, helper.getText().length())
            .map(embedded -> childEls.computeIfAbsent(embedded, ChildElement::new))
            .collect(Collectors.toList());
        }

        @Override
        public IUIAElement getChildFromPoint(javafx.geometry.Point2D point) {
            // TODO picking
            return null;
        }

        @Override
        public ITextRangeProvider RangeFromChild(IUIAElement childElement) {
            if (childElement instanceof ChildElement) {
                ChildElement child = (ChildElement) childElement;

                int start = SimpleTextProviderWithChildren.this.helper.getStart(child.embedded);
                int end =  SimpleTextProviderWithChildren.this.helper.getEnd(child.embedded);

                return new SimpleRange(textSupplier, start, end);
            }
            return null;
        }

        @Override
        public ControlType getControlType() {
            return ControlType.UIA_TextControlTypeId;
        }

        @Override
        public ITextRangeProvider get_DocumentRange() {
            return new SimpleRange(textSupplier, 0, helper.getText().length());
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
        public ITextRangeProvider RangeFromPoint(javafx.geometry.Point2D point) {
            // javafx.geometry.Point2D pickPoint = canvas.screenToLocal(point);

            // javafx.scene.text.HitInfo hitInfo =  SimpleTextProviderWithChildren.this.helper.pick(pickPoint, new javafx.geometry.Point2D(baseX, baseY));
            // if (hitInfo != null) {
            //     int idx = hitInfo.getCharIndex();
            //     idx = Math.min(helper.getText().length()-1, idx);
            //     if (!hitInfo.isLeading()) {
            //         idx+=1;
            //     }
            //     return new SimpleRange(textSupplier, idx, idx);
            // } else {
            //     return null;
            // }
            return null;
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
    public SimpleTextProviderWithChildren() {

        element = new TextElement(helper::getText);

        canvas = new Canvas(400, 200) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return element;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };

        //layoutContent();

        GraphicsContext ctx = canvas.getGraphicsContext2D();
        draw(ctx);




        desc = new Label("Simple ITextProvider & ITextRangeProvider sample with children");
        desc.setWrapText(true);
    }


    @Override
    public String getName() {
        return "ITextProvider w/ Children";
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
