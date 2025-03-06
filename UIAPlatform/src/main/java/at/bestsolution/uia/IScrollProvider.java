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
package at.bestsolution.uia;

/**
 * Provides access to controls that act as scrollable containers for a collection of child objects.
 * The children of this control must implement IScrollItemProvider.
 * <p>
 * Implemented on a Microsoft UI Automation provider that must support the Scroll control pattern.
 * </p>
 */
public interface IScrollProvider extends IInitable {

	/**
	 * context object for IScrollProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ScrollProviderContext {
		public final IProperty<Boolean> HorizontallyScrollable;
		public final IProperty<Double> HorizontalScrollPercent;
		public final IProperty<Double> HorizontalViewSize;
		public final IProperty<Boolean> VerticallyScrollable;
		public final IProperty<Double> VerticalScrollPercent;
		public final IProperty<Double> VerticalViewSize;

		public ScrollProviderContext(IInitContext init, IScrollProvider provider) {
			HorizontallyScrollable = init.addProperty(StandardPropertyIds.UIA_ScrollHorizontallyScrollablePropertyId, provider::get_HorizontallyScrollable, StandardVariantConverters.BOOL_Boolean);
			HorizontalScrollPercent = init.addProperty(StandardPropertyIds.UIA_ScrollHorizontalScrollPercentPropertyId, provider::get_HorizontalScrollPercent, StandardVariantConverters.R8_Double);
			HorizontalViewSize = init.addProperty(StandardPropertyIds.UIA_ScrollHorizontalViewSizePropertyId, provider::get_HorizontalViewSize, StandardVariantConverters.R8_Double);
			VerticallyScrollable = init.addProperty(StandardPropertyIds.UIA_ScrollVerticallyScrollablePropertyId, provider::get_VerticallyScrollable, StandardVariantConverters.BOOL_Boolean);
			VerticalScrollPercent = init.addProperty(StandardPropertyIds.UIA_ScrollVerticalScrollPercentPropertyId, provider::get_VerticalScrollPercent, StandardVariantConverters.R8_Double);
			VerticalViewSize = init.addProperty(StandardPropertyIds.UIA_ScrollVerticalViewSizePropertyId, provider::get_VerticalViewSize, StandardVariantConverters.R8_Double);
		}
	}

	/**
	 * Indicates whether the control can scroll horizontally.
	 * @return horzontal scrollable
	 */
	boolean get_HorizontallyScrollable();

	/**
	 * Specifies the horizontal scroll position.
	 * <p>
	 * The horizontal scroll position can be reported as UIA_ScrollPatternNoScroll if no valid position is available; for example, if the window does not have a horizontal scroll bar.
	 * </p>
	 * @return the horzontal scroll percent
	 */
	double get_HorizontalScrollPercent();


	/**
	 * Specifies the horizontal size of the viewable region.
	 * @return the horizontal view size
	 */
	double get_HorizontalViewSize();

	/**
	 * Indicates whether the control can scroll vertically.
	 * @return vertically scrollable
	 */
	boolean get_VerticallyScrollable();

	/**
	 * Specifies the vertical scroll position.
	 * <p>
	 * The vertical scroll position can be reported as UIA_ScrollPatternNoScroll if no valid position is available; for example, if the window does not have a vertical scroll bar.
	 * </p>
	 * @return the vertical scroll percent
	 */
	double get_VerticalScrollPercent();

	/**
	 * Specifies the vertical size of the viewable region.
	 * @return the vertical view size
	 */
	double get_VerticalViewSize();


	/**
	 * Scrolls the visible region of the content area horizontally and vertically.
	 * @param horizontalAmount the horzontal amount
	 * @param verticalAmount the vertical amount
	 */
	void Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount);

	/**
	 * Sets the horizontal and vertical scroll position as a percentage of the total content area within the control.
	 * <p>
	 * This method is only useful when the content area of the control is larger than the visible region.
	 * </p>
	 * @param horizontalPercent The horizontal position as a percentage of the content area's total range, or UIA_ScrollPatternNoScroll if there is no horizontal scrolling.
	 * @param verticalPercent The vertical position as a percentage of the content area's total range, or UIA_ScrollPatternNoScroll if there is no vertical scrolling.
	 */
	void SetScrollPercent(double horizontalPercent, double verticalPercent);

}
