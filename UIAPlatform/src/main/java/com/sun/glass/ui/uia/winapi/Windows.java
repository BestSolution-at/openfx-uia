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
package com.sun.glass.ui.uia.winapi;

import java.util.Arrays;

import com.sun.glass.ui.uia.ProxyAccessible;

public class Windows {

    static {
        // load lib
        ProxyAccessible.requireLibrary();
    }

    public static final int VT_EMPTY = 0;
    public static final int VT_NULL  = 1;
    public static final int VT_I2    = 2;
    public static final int VT_I4    = 3;
    public static final int VT_R4    = 4;
    public static final int VT_R8    = 5;
    public static final int VT_BOOL  = 11;
    public static final int VT_BSTR  = 8;
    public static final int VT_ARRAY = 0x2000;
    public static final int VT_UNKNOWN = 13;
    
    public static native long VariantInit();
    public static native int VariantClear(long variant);
    public static native void VariantSetVt(long variant, short vt);
    public static native short VariantGetVt(long variant);
    public static native void VariantSetIVal(long variant, short iVal);
    public static native short VariantGetIVal(long variant);
    public static native void VariantSetLVal(long variant, int lVal);
    public static native int VariantGetLVal(long variant);
    public static native void VariantSetFltVal(long variant, float fltVal);
    public static native float VariantGetFltVal(long variant);
    public static native void VariantSetDblVal(long variant, double dblVal);
    public static native double VariantGetDblVal(long variant);
    public static native void VariantSetBoolVal(long variant, boolean boolVal);
    public static native boolean VariantGetBoolVal(long variant);
    public static native void VariantSetBstrVal(long variant, String bstrVal);
    public static native String VariantGetBstrVal(long variant);
    public static native void VariantSetPunkVal(long variant, long punkVal);
    public static native long VariantGetPunkVal(long variant);
    public static native void VariantSetFltSafeArray(long variant, float[] fltArray);
    public static native float[] VariantGetFltSafeArray(long variant);
    public static native void VariantSetDblSafeArray(long variant, double[] dblArray);
    public static native double[] VariantGetDblSafeArray(long variant);
    public static native void VariantSetPunkSafeArray(long variant, long[] punkArray);
    public static native long[] VariantGetPunkSafeArray(long variant);

    public static native void VariantDebugOutput(long variant);

    
    public static native long UiaGetReservedMixedAttributeValue();
    public static native long UiaGetReservedNotSupportedValue();

    public static native boolean UiaClientsAreListening();
    public static native int UiaRaiseAutomationEvent(long accessible, int eventId);
    public static native int UiaRaiseAutomationPropertyChangedEvent(long accessible, int propertyId, long variantOld, long variantNew);


    public static void main(String[] args) {
        {
            long variant = VariantInit();
            VariantDebugOutput(variant);
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_NULL);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_I2);
            VariantSetIVal(variant, (short) 123);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetIVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_I4);
            VariantSetLVal(variant, 123);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetLVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_R4);
            VariantSetFltVal(variant, 14.15f);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetFltVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_R8);
            VariantSetDblVal(variant, 14.15);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetDblVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_BOOL);
            VariantSetBoolVal(variant, true);
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetBoolVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) VT_BSTR);
            VariantSetBstrVal(variant, "Hello World");
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(VariantGetBstrVal(variant));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) (VT_R4 | VT_ARRAY));
            VariantSetFltSafeArray(variant, new float[] {1.0f, 1.1f, 1.2f, 1.4f});
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(Arrays.toString(VariantGetFltSafeArray(variant)));
            VariantClear(variant);
        }
        {
            long variant = VariantInit();
            VariantSetVt(variant, (short) (VT_R8 | VT_ARRAY));
            VariantSetDblSafeArray(variant, new double[] {1.0, 1.1, 1.2, 1.4});
            VariantDebugOutput(variant);
            System.err.println(VariantGetVt(variant));
            System.err.println(Arrays.toString(VariantGetDblSafeArray(variant)));
            VariantClear(variant);
        }

        System.err.println(UiaGetReservedMixedAttributeValue());
        System.err.println(UiaGetReservedNotSupportedValue());

    }
}
