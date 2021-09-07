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
 * Extends the ISelectionItemProvider interface to provide information about selected items.
 */
public interface ISelectionProvider2 extends ISelectionProvider {
    
    /**
	 * context object for ISelectionProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class SelectionProvider2Context extends SelectionProviderContext {
        public final IProperty<IUIAElement> CurrentSelectedItem;
        public final IProperty<IUIAElement> FirstSelectedItem;
        public final IProperty<IUIAElement> LastSelectedItem;
        public final IProperty<Integer> ItemCount;

		public SelectionProvider2Context(IInitContext init, ISelectionProvider2 provider) {
            super(init, provider);
            CurrentSelectedItem = init.addProperty(StandardPropertyIds.UIA_Selection2CurrentSelectedItemPropertyId, provider::get_CurrentSelectedItem, StandardVariantConverters.UNKNOWN_IUIAElement);
            FirstSelectedItem = init.addProperty(StandardPropertyIds.UIA_Selection2FirstSelectedItemPropertyId, provider::get_FirstSelectedItem, StandardVariantConverters.UNKNOWN_IUIAElement);
            LastSelectedItem = init.addProperty(StandardPropertyIds.UIA_Selection2LastSelectedItemPropertyId, provider::get_LastSelectedItem, StandardVariantConverters.UNKNOWN_IUIAElement);
            ItemCount = init.addProperty(StandardPropertyIds.UIA_Selection2ItemCountPropertyId, provider::get_ItemCount, StandardVariantConverters.I4_Integer);
        }
	}

    /**
     * Gets the currently selected item.
     * @return the current selected item
     */
    IUIAElement get_CurrentSelectedItem();

    /**
     * Gets the first item in a group of selected items.
     * @retur the first selected item
     */
    IUIAElement get_FirstSelectedItem();

    /**
     * Gets the last item in a group of selected items.
     * @return the last selected item
     */
    IUIAElement get_LastSelectedItem();

    /**
     * Gets the number of selected items.
     * @return the item count
     */
    int get_ItemCount();

}
