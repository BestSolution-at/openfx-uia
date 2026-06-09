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

public interface IWindowProvider extends IInitable {

	/**
	 * context object for IWindowProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class WindowProviderContext {
		public final IProperty<Boolean> CanMaximize;
		public final IProperty<Boolean> CanMinimize;
		public final IProperty<Boolean> IsModal;
		public final IProperty<Boolean> IsTopmost;
		public final IProperty<WindowVisualState> WindowVisualState;
		public final IProperty<WindowInteractionState> WindowInteractionState;

		public final IEvent Closed;
		public final IEvent Opened;

		public WindowProviderContext(IInitContext init, IWindowProvider windowProvider) {
			CanMaximize = init.addProperty(StandardPropertyIds.UIA_WindowCanMaximizePropertyId, windowProvider::get_CanMaximize, StandardVariantConverters.BOOL);
			CanMinimize = init.addProperty(StandardPropertyIds.UIA_WindowCanMinimizePropertyId, windowProvider::get_CanMinimize, StandardVariantConverters.BOOL);
			IsModal = init.addProperty(StandardPropertyIds.UIA_WindowIsModalPropertyId, windowProvider::get_IsModal, StandardVariantConverters.BOOL);
			IsTopmost = init.addProperty(StandardPropertyIds.UIA_WindowIsTopmostPropertyId, windowProvider::get_IsTopmost, StandardVariantConverters.BOOL);
			WindowVisualState = init.addProperty(StandardPropertyIds.UIA_WindowWindowVisualStatePropertyId, windowProvider::get_WindowVisualState, StandardVariantConverters.I4_INativeEnum(at.bestsolution.uia.WindowVisualState::fromNativeValue));
			WindowInteractionState = init.addProperty(StandardPropertyIds.UIA_WindowWindowInteractionStatePropertyId, windowProvider::get_WindowInteractionState, StandardVariantConverters.I4_INativeEnum(at.bestsolution.uia.WindowInteractionState::fromNativeValue));

			Closed = init.addEvent(StandardEventIds.UIA_Window_WindowClosedEventId);
			Opened = init.addEvent(StandardEventIds.UIA_Window_WindowOpenedEventId);
		}
	}

	boolean get_CanMaximize();
	boolean get_CanMinimize();
	boolean get_IsModal();
	boolean get_IsTopmost();
	WindowInteractionState get_WindowInteractionState();
	WindowVisualState get_WindowVisualState();

	void Close();
	void SetVisualState(WindowVisualState state);
	boolean WaitForInputIdle(int milliseconds);

}
