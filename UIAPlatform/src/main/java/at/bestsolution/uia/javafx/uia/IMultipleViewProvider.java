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
 * Provides access to controls that provide, and are able to switch between, multiple representations of the same set of information or child controls.
 * <p>
 * Implemented on a Microsoft UI Automation provider that must support the MultipleView control pattern.
 * </p>
 */
public interface IMultipleViewProvider extends IInitable {

	/**
	 * context object for IMultipleViewProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class MultipleViewProviderContext {
		public final IProperty<Integer> CurrentView;
		public final IProperty<int[]> SupportedViews;

		public MultipleViewProviderContext(IInitContext init, IMultipleViewProvider provider) {
			CurrentView = init.addProperty(StandardPropertyIds.UIA_MultipleViewCurrentViewPropertyId, provider::get_CurrentView, StandardVariantConverters.I4_Integer);
			SupportedViews = init.addProperty(StandardPropertyIds.UIA_MultipleViewSupportedViewsPropertyId, provider::GetSupportedViews, StandardVariantConverters.I4Array_IntegerArray);
		}
	}

	/**
	 * Identifies the current view that the control is using to display information or child controls.
	 * <p>
	 * The collection of view identifiers must be identical for all instances of a control.
	 * </p>
	 * @return the current view identifier
	 */
	int get_CurrentView();

	/**
	 * Retrieves a collection of control-specific view identifiers.
	 * <p>
	 * An empty array is returned by UIAutoCore.dll if the provider does not supply any view identifiers.
	 * The collection of view identifiers must be identical for all instances of a control.
	 * View identifier values can be passed to IMultipleViewProvider::GetViewName.
	 * </p>
	 * @return Receives a collection of control-specific integer values that identify the views available for a UI Automation element.
	 */
	int[] GetSupportedViews();

	/**
	 * Retrieves the name of a control-specific view.
	 * <p>
	 * View identifiers can be retrieved by using IMultipleViewProvider::GetSupportedViews.
	 * The collection of view identifiers must be identical for all instances of a control.
	 * View names must be suitable for use in text-to-speech, Braille, and other accessible applications.
	 * </p>
	 * @param viewId A view identifier.
	 * @return Receives a localized name for the view. This parameter is passed uninitialized.
	 */
	String GetViewName(int viewId);

	/**
	 * Sets the current control-specific view.
	 * <p>
	 * View identifiers can be retrieved by using IMultipleViewProvider::GetSupportedViews.
	 * The collection of view identifiers must be identical for all instances of a control.
	 * </p>
	 * @param viewId A view identifier.
	 */
	void SetCurrentView(int viewId);
}
