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

/**
 * Provides access to individual, selectable child controls of containers that implement ISelectionProvider.
 */
public interface ISelectionItemProvider extends IInitable {
    
    /**
	 * context object for ISelectionItemProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class SelectionItemProviderContext {

        public final IProperty<Boolean> IsSelected;
        public final IProperty<IUIAElement> SelectionContainer;

		public SelectionItemProviderContext(IInitContext init, ISelectionItemProvider provider) {
            IsSelected = init.addProperty(StandardPropertyIds.UIA_SelectionItemIsSelectedPropertyId, provider::get_IsSelected, StandardVariantConverters.BOOL_Boolean);
            SelectionContainer = init.addProperty(StandardPropertyIds.UIA_SelectionItemSelectionContainerPropertyId, provider::get_SelectionContainer, StandardVariantConverters.UNKNOWN_IUIAElement);
        }
	}

    /**
     * Adds the current element to the collection of selected items.
     */
    void AddToSelection();
    /**
     * Removes the current element from the collection of selected items.
     */
    void RemoveFromSelection();
    /**
     * Deselects any selected items and then selects the current element.
     */
    void Select();

    /**
     * Indicates whether an item is selected.
     * @return true if the item is selected
     */
    boolean get_IsSelected();

    /**
     * Specifies the provider that implements ISelectionProvider and acts as the container for the calling object.
     * @return the selection provider element
     */
    IUIAElement get_SelectionContainer();

}
