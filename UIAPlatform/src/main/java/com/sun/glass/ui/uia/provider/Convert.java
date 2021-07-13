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
package com.sun.glass.ui.uia.provider;

import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.geometry.Bounds;
import javafx.uia.INativeEnum;

public class Convert {
    
    public static int convertNativeEnum(INativeEnum value) {
        return value.getNativeValue();
    }

    public static int convertBoolean(boolean value) {
        return value ? 1 : 0;
    }
    public static boolean convertBoolean(int value) {
        return value != 0;
    }

    public static WinVariant variantBoolean(boolean value) {
        WinVariant variant = new WinVariant();
        variant.vt = WinVariant.VT_BOOL;
        variant.boolVal = value;
        return variant;
    }
    public static boolean variantBoolean(WinVariant value) {
        return value.boolVal;
    }
    public static WinVariant variantString(String value) {
        WinVariant variant = new WinVariant();
        variant.vt = WinVariant.VT_BSTR;
        variant.bstrVal = value;
        return variant;
    }
    public static WinVariant convertInt(int value) {
        WinVariant variant = new WinVariant();
        variant.vt = WinVariant.VT_I4;
        variant.lVal = value;
        return variant;
    }
    public static WinVariant variantEnum(INativeEnum value) {
        if (value == null) {
            WinVariant variant = new WinVariant();
            variant.vt = WinVariant.VT_NULL;
            return variant;
        }
        WinVariant variant = new WinVariant();
        variant.vt = WinVariant.VT_I4;
        variant.lVal = value.getNativeValue();
        return variant;
    }

    public static float[] convertBounds(Bounds bounds) {
        if (bounds != null) {
            return new float[] {(float)bounds.getMinX(), (float)bounds.getMinY(),
                                (float)bounds.getWidth(), (float)bounds.getHeight()};
        }
        return null;
    }

    public static WinVariant variantBounds(Bounds bounds) {
        // TODO find out how to pass a float array through Variant?
        WinVariant variant = new WinVariant();
        variant.vt = WinVariant.VT_ARRAY | WinVariant.VT_R4;
        //variant.pDblVal = convertBounds(bounds);
        return variant;
    }
   
}
