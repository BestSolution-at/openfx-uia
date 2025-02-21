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

/**
 * represents a uia annotation type id
 */
public interface IAnnotationTypeId extends INativeConstant {

    /**
     * the native value
     * @return native value
     */
    int getNativeValue();


    static IAnnotationTypeId fromNativeValue(int nativeValue) {
        return StandardAnnotationTypeIds.fromNativeValue(nativeValue).orElse(new IAnnotationTypeId(){
            @Override
            public int getNativeValue() {
                return nativeValue;
            }
            @Override
            public String getConstantName() {
                return "AnnotationTypeId_NOT_MAPPED("+nativeValue+")";
            }
        });
    }

}
