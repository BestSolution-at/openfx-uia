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
 * Provides access to controls that initiate or perform a single, unambiguous action and do not maintain state when activated.
 * <p>
 * Implemented on a Microsoft UI Automation provider that must support the Invoke control pattern.
 * </p>
 * <p>
 * Controls implement IInvokeProvider if the same behavior is not exposed through another control pattern provider. For example, if the Invoke method of a control performs the same action as the IExpandCollapseProvider::Expand or Collapse method, the control should not also implement IInvokeProvider.
 * </p>
 */
public interface IInvokeProvider extends IInitable {

	/**
	 * context object for IInvokeProvider
	 */
	class InvokeProviderContext {
		public InvokeProviderContext(IInitContext init, IInvokeProvider provider) {
			// empty
		}
	}

	/**
	 * Sends a request to activate a control and initiate its single, unambiguous action.
	 * <p>
	 * IInvokeProvider::Invoke is an asynchronous call and must return immediately without blocking.
	 * </p>
	 * <p>
	 * Note  This is particularly critical for controls that, directly or indirectly, launch a modal dialog when invoked. Any Microsoft UI Automation client that instigated the event will remain blocked until the modal dialog is closed. 
	 * </p>
	 * <p>
	 * IInvokeProvider::Invoke raises the Invoked event after the control has completed its associated action, if possible. 
	 * </p>
	 * <p>
	 * The event should be raised before servicing the Invoke request in the following scenarios:
	 * </p>
	 * <ul>
	 * <li>It is not possible or practical to wait until the action is complete.</li>
	 * <li>The action requires user interaction.</li>
	 * <li>The action is time-consuming and will cause the calling client to block for a significant length of time.
	 * </li>
	 * </ul>
	 */
	void Invoke();

}
