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
package javafx.uia;

import javafx.geometry.Bounds;

/** Provides access to a span of continuous text in a text container that implements ITextProvider or ITextProvider2. */
public interface ITextRangeProvider {
    
    /** Adds the text range to the collection of selected text ranges in a control that supports multiple, disjoint spans of selected text. */
    void AddToSelection();
    
    /** 
     * Returns a new ITextRangeProvider identical to the original ITextRangeProvider and inheriting all properties of the original. 
     * @return Receives a pointer to the copy of the text range. A null reference is never returned. This parameter is passed uninitialized.
     */
    ITextRangeProvider Clone();
    
    /** 
     * Retrieves a value that specifies whether this text range has the same endpoints as another text range.
     * @param other The text range to compare with this one.
     * @return Receives TRUE if the text ranges have the same endpoints, or FALSE if they do not.
     */
    boolean Compare(ITextRangeProvider other);

    /**
     * Returns a value that specifies whether two text ranges have identical endpoints.
     * <p>
     * Returns a negative value if the caller's endpoint occurs earlier in the text than the target endpoint.

Returns zero if the caller's endpoint is at the same location as the target endpoint.

Returns a positive value if the caller's endpoint occurs later in the text than the target endpoint.
</p>
     * @param endpoint
     * @param targetRange The text range to be compared.
     * @param targetEndpoint
     * @return Receives a value that indicates whether the two text ranges have identical endpoints. This parameter is passed uninitialized.
     */
    int CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange, TextPatternRangeEndpoint targetEndpoint);

    /**
     * Normalizes the text range by the specified text unit. The range is expanded if it is smaller than the specified unit, or shortened if it is longer than the specified unit.
     * @param unit The type of text units, such as character, word, paragraph, and so on.
     */
    void ExpandToEnclosingUnit(TextUnit unit);

    ITextRangeProvider FindAttribute(ITextAttributeId attributeId, Variant val, boolean backward);

    ITextRangeProvider FindText(String text, boolean backward, boolean ignoreCase);

    Variant GetAttributeValue(ITextAttributeId attributeId);


    /**
     * 
     * @return Receives a pointer to one of the following.

    An array of bounding rectangles for each full or partial line of text in a text range.
    An empty array for a degenerate range.
    An empty array for a text range that has screen coordinates placing it completely off-screen, scrolled out of view, or obscured by an overlapping window.

This parameter is passed uninitialized. 
     */
    Bounds[] GetBoundingRectangles();

    /**
     * Retrieves a collection of all elements that are both contained (either partially or completely) within the specified text range, and are child elements of the enclosing element for the specified text range.
     * @return An array of pointers to the IRawElementProviderSimple interfaces for all child elements that are enclosed by the text range (sorted by the Start endpoint of their ranges).

If the text range does not include any child elements, an empty collection is returned.

This parameter is passed uninitialized.
     */
    IRawElementProviderSimple[] GetChildren();

    IRawElementProviderSimple GetEnclosingElement();

    String GetText(int maxLength);

    int Move(TextUnit unit, int count);

    void MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange, TextPatternRangeEndpoint targetEndpoint);

    /**
     * 
     * @param endpoint
     * @param unit
     * @param count The number of units to move. A positive value moves the endpoint forward. A negative value moves backward. A value of 0 has no effect.
     * @return Receives the number of units actually moved, which can be less than the number requested if moving the endpoint runs into the beginning or end of the document.
     */
    int MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count);

    void RemoveFromSelection();

    void ScrollIntoView(boolean alignToTop);

    /**
     * Selects the span of text that corresponds to this text range, and removes any previous selection.
     */
    void Select();
}
