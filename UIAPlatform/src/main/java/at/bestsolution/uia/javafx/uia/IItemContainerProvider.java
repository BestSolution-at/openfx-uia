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
 * Provides access to controls that act as containers of other controls, such as a virtual list-view.
 * <p>
 * The ItemContainer control pattern allows a container object to efficiently lookup an item by a specified automation element property, such as Name, AutomationId, or IsSelected state. While this control pattern is introduced with a view to being used by virtualized containers, it can be implemented by any container that provides name lookup, independently of whether that container uses virtualization.
 * </p>
 */
public interface IItemContainerProvider extends IInitable {

	/**
	 * context object for IItemContainerProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ItemContainerProviderContext {

		public ItemContainerProviderContext(IInitContext init, IItemContainerProvider provider) {
			// no properties
		}
	}

	/**
	 * Retrieves an element within a containing element, based on a specified property value.
	 * @param startAfter The UI Automation provider of the element after which the search begins, or NULL to search all elements.
	 * @param propertyId The property identifier. For a list of property IDs, see Property Identifiers.
	 * @param value The value of the property.
	 * @return Receives a pointer to the UI Automation provider of the element.
	 */
	IUIAElement FindItemByProperty(IUIAElement startAfter, IPropertyId propertyId, Variant value);

}
