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
import java.util.function.Supplier;

import at.bestsolution.uia.ControlType;
import at.bestsolution.uia.ITextProvider;
import at.bestsolution.uia.ITextRangeProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.SupportedTextSelection;
import at.bestsolution.uia.TextPatternRangeEndpoint;
import at.bestsolution.uia.TextUnit;
import at.bestsolution.uia.UIA;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import uia.sample.Sample;

public class SimpleTextProvider implements Sample {

    class TextHelper {

        private Supplier<String> text;

        public TextHelper(Supplier<String> text) {
            this.text = text;
        }

        String getText() {
            return text.get();
        }

    }

    class SimpleRange implements ITextRangeProvider {
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

            String text = helper.getText();
            if (text == null) return null;
            int length = text.length();

            /* Narrator will not focus an empty text control if the bounds are NULL */
            if (length == 0) return new Bounds[0];
            int endOffset = end;
            if (endOffset > 0 && endOffset > start && text.charAt(endOffset - 1) == '\n') {
                endOffset--;
            }
            if (endOffset > 0 && endOffset > start && text.charAt(endOffset - 1) == '\r') {
                endOffset--;
            }
            if (endOffset > 0 && endOffset > start && endOffset == length) {
                endOffset--;
            }
            // for now use java fx a11y query to get this data
            return (Bounds[]) field.queryAccessibleAttribute(AccessibleAttribute.BOUNDS_FOR_RANGE, start, endOffset);
        }

        @Override
        public IUIAElement[] GetChildren() {
            return new IUIAElement[] {};
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
            field.selectRange(start, end);
        }

    }

    private TextElement element;
    private TextField field;

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
            return SupportedTextSelection.Single;
        }

        @Override
        public ITextRangeProvider[] GetSelection() {
            return new ITextRangeProvider[] { new SimpleRange(helper, field.getSelection().getStart(), field.getSelection().getEnd()) };
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
        public ITextRangeProvider RangeFromPoint(Point2D point) {
            return null;
        }

        @Override
        public Bounds getBounds() {
            return field.localToScreen(field.getLayoutBounds());
        }

        @Override
        public void SetFocus() {
            field.requestFocus();
        }

    }
    BorderPane area;
    public SimpleTextProvider() {



        field = new TextField("Hello UIA World") {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {

                if (UIA.isUIAQuery(attribute, parameters)) {
                    return element;
                }

                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        element = new TextElement(new TextHelper(field::getText));

        area = new BorderPane(field);
        area.setStyle("-fx-padding: 20;");

        desc = new Label("Simple ITextProvider & ITextRangeProvider sample");
    }


    @Override
    public String getName() {
        return "ITextProvider";
    }

    @Override
    public Node getDescription() {

        return desc;
    }

    @Override
    public Node getSample() {
        return area;
    }

    @Override
    public Node getControls() {
        return null;
    }

}
