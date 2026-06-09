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
 * Provides access to controls that act as containers for a collection of individual, selectable child items. The children of this control must implement ISelectionItemProvider.
 */
public interface ISelectionProvider extends IInitable {

    /**
	 * context object for ISelectionProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class SelectionProviderContext {

        public final IProperty<Boolean> CanSelectMultiple;
        public final IProperty<Boolean> IsSelectionRequried;
        public final IProperty<IUIAElement[]> Selection;

		public SelectionProviderContext(IInitContext init, ISelectionProvider provider) {
            CanSelectMultiple = init.addProperty(StandardPropertyIds.UIA_SelectionCanSelectMultiplePropertyId, provider::get_CanSelectMultiple, StandardVariantConverters.BOOL_Boolean);
            IsSelectionRequried = init.addProperty(StandardPropertyIds.UIA_SelectionIsSelectionRequiredPropertyId, provider::get_CanSelectMultiple, StandardVariantConverters.BOOL_Boolean);
            Selection = init.addProperty(StandardPropertyIds.UIA_SelectionSelectionPropertyId, provider::GetSelection, StandardVariantConverters.UNKNOWNArray_IUIAElementArray);
        }
	}

    /**
     * Indicates whether the Microsoft UI Automation provider allows more than one child element to be selected concurrently.
     * <p>
     * This property may be dynamic. For example, in rare cases a control might allow multiple items to be selected on initialization but subsequently allow only single selections to be made.
     * </p>
     * @return true if multiple items can be selected
     */
    boolean get_CanSelectMultiple();

    /**
     * Indicates whether the Microsoft UI Automation provider requires at least one child element to be selected.
     * <p>
     * This property can be dynamic. For example, the initial state of a control might not have any items selected by default, meaning that ISelectionProvider::IsSelectionRequired is FALSE. However, after an item is selected the control must always have at least one item selected.
     * </p>
     * @return true if selection is required
     */
    boolean get_IsSelectionRequired();

    /**
     * Retrieves a Microsoft UI Automation provider for each child element that is selected.
     * @return the selected elements
     */
    IUIAElement[] GetSelection();
}
