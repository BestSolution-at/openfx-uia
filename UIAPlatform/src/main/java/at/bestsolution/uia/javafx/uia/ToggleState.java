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

import java.util.Optional;
import java.util.stream.Stream;

/** Contains values that specify the toggle state of a Microsoft UI Automation element that implements the Toggle control pattern. */
public enum ToggleState implements INativeEnum {
    /** The UI Automation element is not selected, checked, marked or otherwise activated. */
    Off(0),
    /** The UI Automation element is selected, checked, marked or otherwise activated. */
    On(1),
    /** The UI Automation element is in an indeterminate state.  */
    Indeterminate(2);

    private final int value;
    private ToggleState(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "ToggleState_" + name();
    }

    public static Optional<ToggleState> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}

}
