package uia.sample.samples.model;

import java.text.BreakIterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import at.bestsolution.uia.javafx.uia.ITextAttributeSupport;
import at.bestsolution.uia.javafx.uia.ITextRangeProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.TextPatternRangeEndpoint;
import at.bestsolution.uia.javafx.uia.TextUnit;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

public class UIATextRange implements ITextRangeProvider {




        @Override
        public void initialize(ITextAttributeSupport context) {
            // context.addFontSizeAttribute(this::getFontSize, this::findFontSizeAttribute);
            // context.addFontWeightAttribute(this::getFontWeight, null);
            // context.addFontNameAttribute(this::getFontName, null);
            // context.addForegroundColorAttribute(this::getForegroundColor, null);
            // context.addBackgroundColorAttribute(this::getBackgroundColor, null);
        }

        // private ITextRangeProvider findFontSizeAttribute(boolean backward, double fontSize) {
        //     List<Integer> foundIdx = new ArrayList<>();

        //     iterateRange(start, end, (nfo) -> {
        //         double s = nfo.font == null ? 0 : nfo.font.getSize();
        //         final double epsilon = 0.000001d;
        //         if (Math.abs(fontSize - s) < epsilon) {
        //             foundIdx.add(nfo.index);
        //         }
        //     });

        //     Integer first = null;
        //     Integer last = null;

        //     int next = 1;

        //     if (backward) {
        //         Collections.reverse(foundIdx);
        //         next = -1;
        //     }

        //     for (Integer fIdx : foundIdx) {
        //         if (first == null) {
        //             first = fIdx;
        //             last = fIdx;
        //             continue;
        //         }

        //         if (last != null && last+next == fIdx) {
        //             last = fIdx;
        //             continue;
        //         }
        //     }

        //     if (first != null && last != null) {
        //         if (backward) {
        //             return new UIATextRange(root, last, first);
        //         } else {
        //             return new UIATextRange(root, first, last + 1);
        //         }
        //     } else {
        //         return null;
        //     }


        // }

        // private TextAttributeValue<Double> getFontSize() {
        //     // this is a very simple implementation which only checks on the identity of the font object
        //     Set<Font> fonts = new HashSet<>();

        //     iterateRange(start, end, nfo -> {
        //         fonts.add(nfo.font);
        //     });

        //     return toSingleValue(fonts, Font::getSize);
        // }

        // private TextAttributeValue<Integer> getFontWeight() {
        //     // TODO howto get the font weight from a javafx font?
        //     return TextAttributeValue.notSupported();
        // }

        // private TextAttributeValue<String> getFontName() {
        //     Set<String> fontNames = new HashSet<>();

        //     iterateRange(start, end, nfo -> {
        //         fontNames.add(nfo.font == null ? "<none>" : nfo.font.getFamily());
        //     });

        //     return toSingleValue(fontNames);
        // }

        // private TextAttributeValue<Color> getForegroundColor() {
        //     Set<Color> fg = new HashSet<>();
        //     iterateRange(start, end, nfo -> {
        //         fg.add(nfo.color);
        //     });
        //     return toSingleValue(fg);
        // }

        // private TextAttributeValue<Color> getBackgroundColor() {
        //     return TextAttributeValue.value(Color.WHITE);
        // }

        // private <U, T> TextAttributeValue<U> toSingleValue(Set<T> found, Function<T, U> map) {
        //     if (found.size() == 1) {
        //         T result = found.iterator().next();
        //         if (result == null) {
        //             return TextAttributeValue.notSupported();
        //         } else {
        //             U mappedResult = map.apply(result);
        //             if (mappedResult == null) {
        //                 return TextAttributeValue.notSupported();
        //             } else {
        //                 return TextAttributeValue.value(mappedResult);
        //             }
        //         }
        //     } else {
        //         return TextAttributeValue.mixed();
        //     }
        // }

        // private <T> TextAttributeValue<T> toSingleValue(Set<T> found) {
        //     if (found.size() == 1) {
        //         T result = found.iterator().next();
        //         if (result == null) {
        //             return TextAttributeValue.notSupported();
        //         } else {
        //             return TextAttributeValue.value(result);
        //         }
        //     } else {
        //         return TextAttributeValue.mixed();
        //     }
        // }

        private UIADocument root;
        private int start;
        private int end;

        public UIATextRange(UIADocument root, int start, int end) {
            this.root = root;
            this.start = start;
            this.end = end;
        }

        @Override
        public void AddToSelection() {
        }

        @Override
        public ITextRangeProvider Clone() {
            return new UIATextRange(root, start, end);
        }

        private UIATextRange cast(ITextRangeProvider range) {
            return (UIATextRange) range;
        }

        @Override
        public boolean Compare(ITextRangeProvider other) {
            UIATextRange o = (UIATextRange) other;
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
            String text = root.getContent();
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
                case Line:
                case Paragraph: {
                    BreakIterator bi = BreakIterator.getLineInstance();
                    bi.setText(text);

                    start = bi.preceding(start);
                    end = bi.following(end);
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
            String fullText = root.getContent();
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

            UIATextRange result = new UIATextRange(root, start + index, start + index + text.length());
            return result;
        }

        @Override
        public Bounds[] GetBoundingRectangles() {

            if (start == end) {

                Optional<Bounds> glyph = root.streamGlyphs().filter(g -> g.index == start)
                .map(g -> new BoundingBox(g.x, g.y, g.w, g.h))
                .map(root.canvas::localToScreen)
                .findFirst();
                //System.err.println("degenerate " + glyph);
                if (glyph.isPresent()) {
                    return new Bounds[]{glyph.get()};
                }
            }

            List<Bounds> perGlyph = root.streamGlyphs().filter(g -> g.index >= start && g.index < end).map(g -> new BoundingBox(g.x, g.y, g.w, g.h)).collect(Collectors.toList());

            List<Bounds> merged = BaseModel.merge(perGlyph);
            Bounds[] result = merged.stream().map(root.canvas::localToScreen).toArray(size -> new Bounds[size]);

            // if we return the caret range it has a width of 0, which seems not to work -> so we set it to one
            if (result.length == 1) {
                if (result[0].getWidth() == 0) {
                    result[0] = new BoundingBox(result[0].getMinX(), result[0].getMinY(), 1, result[0].getHeight());
                }
                //System.err.println("single bounds " + result[0]);
            }
            //System.err.println("Bounds: " + Arrays.toString(result));
            return result;
/*
            Bounds[] result = root.getTextBounds(start, end).stream().map(b -> root.canvas.localToScreen(b)).toArray(size -> new Bounds[size]);
            if (result.length == 1) {
                if (result[0].getWidth() == 0) {
                    result[0] = new BoundingBox(result[0].getMinX(), result[0].getMinY(), 1, result[0].getHeight());
                }
            }
            System.err.println("GetBoundingRectangles() -> " + Arrays.toString(result));
            return result;
            */
        }

        @Override
        public IUIAElement[] GetChildren() {
            return root.getTextChildren(start, end).stream().toArray(size -> new IUIAElement[size]);
        }

        @Override
        public IUIAElement GetEnclosingElement() {
            IModel enclosing = root.getTextEnclosing(start, end);
            if (enclosing == null) { // maybe not needed?
                enclosing = root;
            }
            return (IUIAElement) enclosing;
        }

        @Override
        public String GetText(int maxLength) {
            int endOffset = maxLength != -1 ? Math.min(end, start + maxLength) : end;
            return root.getContent().substring(start, endOffset);
        }

        @Override
        public int Move(TextUnit unit, int requestedCount) {
            if (requestedCount == 0) return 0;
            String text = root.getContent();
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
                    BreakIterator bi = BreakIterator.getLineInstance();
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
            String text = root.getContent();
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
            String text = root.getContent();
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
                    BreakIterator bi = BreakIterator.getLineInstance();
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
            root.getControl().selectionBeginProperty().set(start);
            root.getControl().selectionEndProperty().set(end);
            root.getControl().caretOffsetProperty().set(end);
            root.getControl().requestLayout(); // <- repaint
        }

        @Override
        public String toString() {
            return "UIATextRange " + start + " -> " + end;
        }

}
