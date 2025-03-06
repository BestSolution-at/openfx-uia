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
 * Provides access to individual child controls of containers that implement IScrollProvider.
 * <p>
 * Implemented on a Microsoft UI Automation provider that must support the ScrollItem control pattern.
 * </p>
 */
public interface IScrollItemProvider extends IInitable {

	/**
	 * context object for IScrollItemProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ScrollItemProviderContext {

		public ScrollItemProviderContext(IInitContext init, IScrollItemProvider provider) {
			// no properties
		}
	}

	/**
	 * Scrolls the content area of a container object in order to display the control within the visible region (viewport) of the container.
	 * <p>
	 * This method will not guarantee the position of the control within the visible region (viewport) of the container.
	 * </p>
	 */
	void ScrollIntoView();

}
