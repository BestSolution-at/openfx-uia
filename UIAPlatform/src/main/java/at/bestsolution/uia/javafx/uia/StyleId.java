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

/** This set of constants describes the named constants used to identify the visual style of text in a document. */
public enum StyleId implements INativeEnum {

    /** A list with bulleted items. Supported starting with Windows 8.1. */
    BulletedList(70015),
    /** A custom style. */
    Custom(70000),
    /** Text that is emphasized. */
    Emphasis(70013),
    /** A first level heading. */
    Heading1(70001),
    /** A second level heading. */
    Heading2(70002),
    /** A thrid level heading. */
    Heading3(70003),
    /** A fourth level heading. */
    Heading4(70004),
    /** A fifth level heading. */
    Heading5(70005),
    /** A sixth level heading. */
    Heading6(70006),
    /** A seventh level heading. */
    Heading7(70007),
    /** A eighth level heading. */
    Heading8(70008),
    /** A nineth level heading. */
    Heading9(70009),
    /** Normal style. */
    Normal(70012),
    /** A list with numbered items. Supported starting with Windows 8.1. */
    NumberedList(70016),
    /** A quotation. */
    Quote(70014),
    /** A subtitle. */
    Subtitle(70011),
    /** A title. */
    Title(70010)
    ;

    private int nativeValue;

    private StyleId(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int getNativeValue() {
        return nativeValue;
    }

    @Override
    public String getConstantName() {
        return name();
    }

    public static Optional<StyleId> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
