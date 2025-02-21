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

import java.util.stream.Stream;

public interface ITextAttributeId extends INativeConstant {

    int getNativeValue();

    static ITextAttributeId fromNativeValue(int attributeId) {
        return Stream.of(StandardTextAttributeIds.values())
        .filter(value -> value.getNativeValue() == attributeId)
        .map(v -> (ITextAttributeId)v).findFirst()
        .orElse(new ITextAttributeId() {
            @Override
            public int getNativeValue() {
                return attributeId;
            }
            @Override
            public String getConstantName() {
                return "ITextAttributeId_NOT_MAPPED("+attributeId+")";
            }
            @Override
            public String toString() {
                return "ITextAttributeId("+attributeId+")";
            }
        });
    }
}
