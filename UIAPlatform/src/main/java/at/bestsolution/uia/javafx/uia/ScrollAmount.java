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

/** Contains values that specify the direction and distance to scroll. */
public enum ScrollAmount implements INativeEnum {
    /** Scrolling is done in large decrements, equivalent to pressing the PAGE UP key or clicking on a blank part of a scroll bar. If one page up is not a relevant amount for the control and no scroll bar exists, the value represents an amount equal to the current visible window. */
    LargeDecrement(0),
    /** Scrolling is done in small decrements, equivalent to pressing an arrow key or clicking the arrow button on a scroll bar. */
    SmallDecrement(1),
    /** No scrolling is done. */
    NoAmount(2),
    /** Scrolling is done in large increments, equivalent to pressing the PAGE DOWN or PAGE UP key or clicking on a blank part of a scroll bar. If one page is not a relevant amount for the control and no scroll bar exists, the value represents an amount equal to the current visible window. */
    LargeIncrement(3),
    /** Scrolling is done in small increments, equivalent to pressing an arrow key or clicking the arrow button on a scroll bar. */
    SmallIncrement(4);

    private final int value;
    private ScrollAmount(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "ScrollAmount_" + name();
    }

    public static Optional<ScrollAmount> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}

}
