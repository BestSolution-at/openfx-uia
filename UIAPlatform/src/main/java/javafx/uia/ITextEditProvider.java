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
 * Extends the ITextProvider interface to enable Microsoft UI Automation providers to expose programmatic text-edit actions.
 * <p>
 * Call the UiaRaiseTextEditTextChangedEvent function to raise the UI Automation events that notify clients of changes. Use values of TextEditChangeType to describe the change. Follow the guidance given in TextEdit Control Pattern that describes when to raise the events and what payload the events should pass to UI Automation.
 * </p>
 */
public interface ITextEditProvider extends ITextProvider {
    
    class TextEditProviderContext {
        public final IEvent ConversionTargetChanged;
        public final IEvent TextChanged;
        public final ITextEditTextChangedEvent TextEditTextChanged;
		public TextEditProviderContext(IInitContext init, ITextEditProvider provider) {
            ConversionTargetChanged = init.addEvent(StandardEventIds.UIA_TextEdit_ConversionTargetChangedEventId);
            TextChanged = init.addEvent(StandardEventIds.UIA_TextEdit_TextChangedEventId);
            TextEditTextChanged = init.addTextEditTextChangedEvent();
		}
	}

    /**
     * Returns the active composition.
     * <p>
     * Follow the guidance given in TextEdit Control Pattern that describes how to implement this method and how to raise the related notification events.
     * </p>
     * @return Pointer to the range of the current conversion (none if there is no conversion).
     */
    ITextRangeProvider GetActiveComposition();

    /**
     * Returns the current conversion target range.
     * <p>
     * Follow the guidance given in TextEdit Control Pattern that describes how to implement this method and how to raise the related notification events.
     * </p>
     * @return Pointer to the conversion target range (none if there is no conversion).
     */
    ITextRangeProvider GetConversionTarget();


}
