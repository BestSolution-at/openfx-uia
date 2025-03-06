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

import java.util.Optional;
import java.util.stream.Stream;

/** Contains values that specify the OverlineStyle, StrikethroughStyle, and UnderlineStyle text attributes. */
public enum TextDecorationLineStyle implements INativeEnum {
    /** No line style. */
    None(0),
    /** A single solid line. */
    Single(1),
    /** Only words (not spaces) are underlined. */
    WordsOnly(2),
    /** A double line. */
    Double(3),
    /** A dotted line. */
    Dot(4),
    /** A dashed line. */
    Dash(5),
    /** Alternating dashes and dots. */
    DashDot(6),
    /** A dash followed by two dots. */
    DashDotDot(7),
    /** A wavy line. */
    Wavy(8),
    /** A thick single line. */
    ThickSingle(9),
    /* A double wavy line. */
    DoubleWavy(11),
    /** A thick wavy line. */
    ThickWavy(12),
    /** Long dashes. */
    LongDash(13),
    /** A thick dashed line. */
    ThickDash(14),
    /** Thick dashes alternating with thick dots. */
    ThickDashDot(15),
    /** A thick dash followed by two thick dots. */
    ThickDashDotDot(16),
    /** A thick dotted line. */
    ThickDot(17),
    /** Thick long dashes. */
    ThickLongDash(18),
    /** A line style not represented by another value. */
    Other(-1);

    private final int value;
    private TextDecorationLineStyle(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "TextDecorationLineStyle_" + name();
    }

    public static Optional<TextDecorationLineStyle> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
