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
     * @param endpoint the endpoint
     * @param targetRange The text range to be compared.
     * @param targetEndpoint the target endpoint
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
     * @return An array of pointers to the IUIAElement interfaces for all child elements that are enclosed by the text range (sorted by the Start endpoint of their ranges).
               If the text range does not include any child elements, an empty collection is returned.
               This parameter is passed uninitialized.
     */
    IUIAElement[] GetChildren();

    /**
     * Returns the innermost element that encloses the specified text range.
     * @return The UI Automation provider of the innermost element that encloses the specified ITextRangeProvider.
     * <p>Note: The enclosing element can span more than just the specified ITextRangeProvider.</p>
     * If no enclosing element is found, the ITextProvider parent of the ITextRangeProvider is returned.
     */
    IUIAElement GetEnclosingElement();

    /**
     * Retrieves the plain text of the range.
     * @param maxLength The maximum length of the string to return. Use -1 if no limit is required.
     * @return Receives the plain text of the text range, possibly truncated at the specified maximum length. This parameter is passed uninitialized.
     */
    String GetText(int maxLength);

    /**
     * Moves the text range forward or backward by the specified number of text units.
     * @param unit the unit
     * @param count The number of text units to move. A positive value moves the text range forward.
                    A negative value moves the text range backward. Zero has no effect.
     * @return The number of text units actually moved. This can be less than the number requested if either of the new text range endpoints is greater than or less than the endpoints retrieved by the ITextProvider::DocumentRange method. This value can be negative if navigation is happening in the backward direction.
     */
    int Move(TextUnit unit, int count);

    /**
     * Moves one endpoint of the current text range to the specified endpoint of a second text range.
     * 
     * <p>
     * If the endpoint being moved crosses the other endpoint of the same text range, that other endpoint is moved also, resulting in a degenerate (empty) range and ensuring the correct ordering of the endpoints (that is, the start is always less than or equal to the end).
     * </p>
     * 
     * @param endpoint the endpoint
     * @param targetRange A second text range from the same text provider as the current text range.
     * @param targetEndpoint the target endpoint
     */
    void MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange, TextPatternRangeEndpoint targetEndpoint);

    /**
     * <p>The endpoint is moved forward or backward, as specified, to the next available unit boundary. If the original <b>endpoint</b> was at the boundary of the specified text unit, the <b>endpoint</b> is moved to the next available text unit boundary, as shown in the following illustration.</p>
          <img alt="Illustration showing endpoints of a text range moving" src="https://docs.microsoft.com/en-us/windows/win32/api/uiautomationcore/images/moveendpointbyunit.gif">
          If the endpoint being moved crosses the other <b>endpoint</b> of the same text range, the other <b>endpoint</b> is also moved, resulting in a degenerate range and ensuring the correct ordering of the <b>endpoint</b> (that is, that the start is always less than or equal to the end).
          <p><b>ITextRangeProvider::MoveEndpointByUnit</b> deprecates up to the next supported text unit if the given text unit is not supported by the control.</p>
          <p>The order, from smallest unit to largest, is listed here.</p>
          <ul>
          <li><i>Character</i></li>
          <li><i>Format</i></li>
          <li><i>Word</i></li>
          <li><i>Line</i></li>
          <li><i>Paragraph</i></li>
          <li><i>Page</i></li>
          <li><i>Document</i></li>
          </ul>
          <h3>Range behavior when <i>unit</i> is <code>TextUnit::Format</code></h3>
          <code>TextUnit::Format</code> as a <i>unit</i> value positions the boundary of a text range to expand or move the range based on shared text attributes (format) of the text within the range. However, using the format text unit should not move or expand a text range across the boundary of an embedded object, such as an image or hyperlink. For more info, see UI Automation Text Units or Text and TextRange Control Patterns.

     * 
     * @param endpoint the endpoint
     * @param unit the unit
     * @param count The number of units to move. A positive value moves the endpoint forward. A negative value moves backward. A value of 0 has no effect.
     * @return Receives the number of units actually moved, which can be less than the number requested if moving the endpoint runs into the beginning or end of the document.
     */
    int MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count);

    /**
     * Removes the text range from the collection of selected text ranges in a control that supports multiple, disjoint spans of selected text.
     */
    void RemoveFromSelection();

    /**
     * Causes the text control to scroll vertically until the text range is visible in the viewport.
     * <p>
     * ITextRangeProvider::ScrollIntoView respects both hidden and visible text.
     * If the text range is hidden, the text control will scroll only if the hidden text has an anchor in the viewport.
     * </p>
     * @param alignToTop TRUE if the text control should be scrolled so the text range is flush with the top of the viewport; FALSE if it should be flush with the bottom of the viewport.
     */
    void ScrollIntoView(boolean alignToTop);

    /**
     * Selects the span of text that corresponds to this text range, and removes any previous selection.
     */
    void Select();
}
