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
public interface IToggleProvider {

	/** Event support for IToggleProvider */
	interface IToggleProviderEvents {
		/** notifies UIA that the toggle state has changed 
		 * @param oldValue the old value
		 * @param newValue the new value
		*/
		void notifyToggleStateChanged(ToggleState oldValue, ToggleState newValue);
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

	/**
	 * initializes the event support
	 * <p>clients must implement this and store the events object to be able to dispatch events</p>
	 * @param events the event support
	 */
	void initialize(IToggleProviderEvents events);
}
