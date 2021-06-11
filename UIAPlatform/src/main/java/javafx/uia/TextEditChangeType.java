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

import java.util.Optional;
import java.util.stream.Stream;

/** Describes the text editing change being performed by controls when text-edit events are raised or handled. */
public enum TextEditChangeType implements INativeEnum {
    /** Not related to a specific change type. */
    None(0),
    /** Change is from an auto-correct action performed by a control. */
    AutoCorrect(1),
    /** Change is from an IME active composition within a control. */
    Composition(2),
    /** Change is from an IME composition going from active to finalized state within a control.
     * <p>Note  The finalized string may be empty if composition was canceled or deleted.</p>
     */
    CompositionFinalized(3),
    /** no doc @ microsoft */
    AutoComplete(4);

    private int value;
    private TextEditChangeType(int value) {
        this.value = value;
    }
    @Override
    public int getNativeValue() {
        return value;
    }
    @Override
    public String getConstantName() {
        return "TextEditChangeType_" + name();
    }

    public static Optional<TextEditChangeType> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
