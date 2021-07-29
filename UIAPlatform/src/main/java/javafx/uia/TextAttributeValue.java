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

public class TextAttributeValue<Type> {

    private Type value;
    private boolean mixed;
    private boolean notSupported;

    private TextAttributeValue(Type value) {
        this.value = value;
    }

    private TextAttributeValue(boolean mixed, boolean notSupported) {
        this.mixed = mixed;
        this.notSupported = notSupported;
    }

    public boolean isNotSupported() {
        return notSupported;
    }

    public boolean isMixed() {
        return mixed;
    }

    public Type getValue() {
        return value;
    }

    public static <Type> TextAttributeValue<Type> value(Type value) {
        return new TextAttributeValue<>(value);
    }

    public static <Type> TextAttributeValue<Type> mixed() {
        return new TextAttributeValue<>(true, false);
    }

    public static <Type> TextAttributeValue<Type> notSupported() {
        return new TextAttributeValue<>(false, true);
    }

}