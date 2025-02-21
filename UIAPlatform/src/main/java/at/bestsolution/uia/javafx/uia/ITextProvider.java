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
package at.bestsolution.uia.javafx.uia;

import javafx.geometry.Point2D;

/**
 * Provides access to controls that contain text.
 */
public interface ITextProvider {

    class TextProviderContext {
        public final IEvent TextSelectionChangedEvent;
		public TextProviderContext(IInitContext init, ITextProvider provider) {
            TextSelectionChangedEvent = init.addEvent(StandardEventIds.UIA_Text_TextSelectionChangedEventId);
		}
	}

    /**
     * Retrieves a text range that encloses the main text of a document.
     * @return the document range
     */
    ITextRangeProvider get_DocumentRange();
    /**
     * Retrieves a value that specifies the type of text selection that is supported by the control.
     * @return the supported text selection
     */
    SupportedTextSelection get_SupportedTextSelection();
    /**
     * Retrieves a collection of text ranges that represents the currently selected text in a text-based control.
     * @return the currently selected ranges
     */
    ITextRangeProvider[] GetSelection();
    /**
     * Retrieves an array of disjoint text ranges from a text-based control where each text range represents a contiguous span of visible text.
     * @return the visible text ranges
     */
    ITextRangeProvider[] GetVisibleRanges();
    /**
     * Retrieves a text range enclosing a child element such as an image, hyperlink, or other embedded object.
     * @param childElement the child
     * @return the range
     */
    ITextRangeProvider RangeFromChild(IUIAElement childElement);
    /**
     * Returns the degenerate (empty) text range nearest to the specified screen coordinates.
     * @param point the coordinates in screen space
     * @return the range
     */
    ITextRangeProvider RangeFromPoint(Point2D point);

}
