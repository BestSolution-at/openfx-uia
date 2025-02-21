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

/**
 * Provides access to virtualized items, which are items that are represented by placeholder automation elements in the Microsoft UI Automation tree.
 * <p>
 * A virtualized item is typically an item in a virtual list; that is, a list that does not manage its own data. When an application retrieves an IUIAutomationElement for a virtualized item by using FindItemByProperty, UI Automation calls the provider's implementation of FindItemByProperty, where the provider may return a placeholder element that also implements IVirtualizedItemProvider. On a call to Realize, the provider's implementation of Realize returns a full UI Automation element reference and may also scroll the item into view.
 * </p>
 */
public interface IVirtualizedItemProvider extends IInitable {

    /**
	 * context object for IVirtualizedItemProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class VirtualizedItemProviderContext {

		public VirtualizedItemProviderContext(IInitContext init, IVirtualizedItemProvider provider) {
            // empty
		}
	}
    /**
     * Makes the virtual item fully accessible as a UI Automation element.
     * <p>
     * When an item is obtained from a virtual list, it is only a placeholder. Use this method to convert it to a full reference to UI Automation element.
     * </p>
     */
    void Realize();

}
