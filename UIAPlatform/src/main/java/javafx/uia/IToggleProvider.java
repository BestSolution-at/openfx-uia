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
 * Provides access to controls that can cycle through a set of states and maintain a state after it is set.
 */
public interface IToggleProvider extends IInitable {

	/**
	 * context object for IToggleProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ToggleProviderContext {
		public final IProperty<ToggleState> ToggleState;

		public ToggleProviderContext(IInitContext init, IToggleProvider toggleProvider) {
			ToggleState = init.addProperty(StandardPropertyIds.UIA_ToggleToggleStatePropertyId, toggleProvider::get_ToggleState, StandardVariantConverters.I4_INativeEnum());
		}
	}
	
	/**
	 * Cycles through the toggle states of a control.
	 * <p>A control must cycle through its ToggleState in this order: ToggleState_On, ToggleState_Off and, if supported, ToggleState_Indeterminate.</p>
	 */
	void Toggle();

	/**
	 * Specifies the toggle state of the control.
	 * <p>A control must cycle through its ToggleState in this order: ToggleState_On, ToggleState_Off and, if supported, ToggleState_Indeterminate.</p>
	 * @return the toggle state
	 */
	ToggleState get_ToggleState();

}
