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

import com.sun.glass.ui.uia.ProxyAccessibleRegistry;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCombination;

/**
 * provides standard variant converters
 */
public class StandardVariantConverters {
    
    public static final IVariantConverter<Boolean> BOOL = value -> Variant.vt_bool(value);

    public static final IVariantConverter<String> BSTR = value -> Variant.vt_bstr(value);

    public static final IVariantConverter<KeyCombination> BSTR_KeyCombination = value -> Variant.vt_bstr(value.toString()); // TODO create UIA compatible Key String from KeyCombination

    public static final IVariantConverter<Bounds> R8Array_Bounds = value -> 
        Variant.vt_r8_array(new double[] {value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight()});
    ;

    public static final IVariantConverter<Point2D> R8Array_Point2D = value -> Variant.vt_r8_array(new double[] { value.getX(), value.getY() });

    public static final IVariantConverter<IUIAElement> UNKNOWN_IUIAElement = value -> Variant.vt_unknown(ProxyAccessibleRegistry.getInstance().findAccessible(value).getNativeAccessible());

    public static final <T extends INativeEnum> IVariantConverter<T> I4_INativeEnum() {
        return value -> Variant.vt_i4(value.getNativeValue());
    }
}
