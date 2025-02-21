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

/** Contains values that specify the location of a docking window represented by the Dock control pattern. */
public enum DockPosition implements INativeEnum {
    /** The window is docked at the top. */
    Top(0),
    /** The window is docked at the left. */
    Left(1),
    /** The window is docked at the bottom. */
    Bottom(2),
    /** The window is docked at the right. */
    Right(3),
    /** The window is docked on all four sides. */
    Fill(4),
    /** The window is not docked. */
    None(5);

    private int value;
    private DockPosition(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "DockPosition_" + name();
    }

    public static Optional<DockPosition> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
