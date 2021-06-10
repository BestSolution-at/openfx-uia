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

public interface IWindowProvider {
	
	interface IWindowProviderEvents {
		void notifyCanMaximizeChanged(boolean oldValue, boolean newValue);
		void notifyCanMinimizeChanged(boolean oldValue, boolean newValue);
		void notifyIsModalChanged(boolean oldValue, boolean newValue);
		void notifyIsTopmostChanged(boolean oldValue, boolean newValue);
		void notifyWindowInteractionStateChanged(WindowInteractionState oldValue, WindowInteractionState newValue);
		void notifyWindowVisualStateChanged(WindowVisualState oldValue, WindowVisualState newValue);
		
		void notifyWindowOpened();
		void notifyWindowClosed();
	}
	
	void Close();
	boolean get_CanMaximize();
	boolean get_CanMinimize();
	boolean get_IsModal();
	boolean get_IsTopmost();
	WindowInteractionState get_WindowInteractionState();
	WindowVisualState get_WindowVisualState();
	void SetVisualState(WindowVisualState state);
	boolean WaitForInputIdle(int milliseconds);
	
	
	void initialize(IWindowProviderEvents events);
	
}
