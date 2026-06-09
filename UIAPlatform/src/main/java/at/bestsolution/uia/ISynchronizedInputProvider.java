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
 * Enables Microsoft UI Automation client applications to direct the mouse or keyboard input to a specific UI element.
 */
public interface ISynchronizedInputProvider extends IInitable {

	/**
	 * context object for ISynchronizedInputProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class SynchronizedInputProviderContext {
		public final IEvent InputReachedTarget;
		public final IEvent InputReachedOtherElement;
		public final IEvent InputDiscarded;

		public SynchronizedInputProviderContext(IInitContext init, ISynchronizedInputProvider provider) {
			InputReachedTarget = init.addEvent(StandardEventIds.UIA_InputReachedTargetEventId);
			InputReachedOtherElement = init.addEvent(StandardEventIds.UIA_InputReachedOtherElementEventId);
			InputDiscarded = init.addEvent(StandardEventIds.UIA_InputDiscardedEventId);
		}
	}

	/**
	 * Cancels listening for input.
	 * <p>
	 * If the provider is currently listening for input, it should revert to normal operation.
	 * </p>
	 */
	void Cancel();

	/**
	 * Starts listening for input of the specified type.
	 * <p>
	 * When it finds matching input, the provider checks if the target UI Automation element matches the current element. If they match, the provider raises the UIA_InputReachedTargetEventId event; otherwise, it raises the UIA_InputReachedOtherElementEventId or UIA_InputDiscardedEventId event. The UI Automation provider must discard the input if it is for an element other than this one.
	 * This is a one-shot method; after receiving input, the provider stops listening and continues normally.
	 * This method returns E_INVALIDOPERATION if the provider is already listening for input.
	 * </p>
	 * @param inputType The type of input that is requested to be synchronized.
	 */
	void StartListening(SynchronizedInputType inputType);
}
