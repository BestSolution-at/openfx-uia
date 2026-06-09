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
package at.bestsolution.uia.internal.winapi;

import java.util.Arrays;

import at.bestsolution.uia.internal.ProxyAccessible;

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
    public static native void VariantSetLSafeArray(long pVariant, int[] lArray);
    public static native int[] VariantGetLSafeArray(long pVariant);
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

    public static native long UiaRaiseActiveTextPositionChangedEvent(long pProvider, long textRange);
    public static native long UiaRaiseAsyncContentLoadedEvent(long pProvider, int asyncContentLoadedState, double percentComplete);
    public static native long UiaRaiseAutomationEvent(long pProvider, int eventId);
    public static native long UiaRaiseAutomationPropertyChangedEvent(long pProvider, int propertyId, long variantOld, long variantNew);
    public static native long UiaRaiseChangesEvent(long pProvider, int eventIdCount, long pUiaChanges);
    public static native long UiaRaiseNotificationEvent(long pProvider, int notificationKind, int notificationProcessing, String displayString, String activityId);
    public static native long UiaRaiseStructureChangedEvent(long pProvider, int structureChangeType, int[] pRuntimeId);
    public static native long UiaRaiseTextEditTextChangedEvent(long pProvider, int textEditChangeType, long pChangedData);


    public static native long SafeArrayCreateVector(short vt, int lLbound, int cElements);
    public static native long SafeArrayDestroy(long psa);
    public static native long SafeArrayPutElement(long psa, int index, long pValue);
    public static native long SafeArrayGetElement(long psa, int index);

    public static native long SysAllocString(String string);
    public static native void SysFreeString(long pBstr);

    public static native void DebugOutputBSTR(long pBstr);

    public static void main(String[] args) {
        ProxyAccessible.requireLibrary();
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


        System.err.println("-----------------------");

        long test = SysAllocString("test");
        DebugOutputBSTR(test);
        SysFreeString(test);

        long mySa = SafeArrayCreateVector((short) VT_BSTR, 0, 3);

        long zero = SysAllocString("zero");
        SafeArrayPutElement(mySa, 0, zero);
        SysFreeString(zero);

        long one = SysAllocString("one");
        SafeArrayPutElement(mySa, 1, one);
        SysFreeString(one);

        long two = SysAllocString("two");
        SafeArrayPutElement(mySa, 2, two);
        SysFreeString(two);

        long hr = SafeArrayDestroy(mySa);

        long readZero = SafeArrayGetElement(mySa, 0);
        System.err.println("zero: " + readZero);
        DebugOutputBSTR(readZero);
        long readOne = SafeArrayGetElement(mySa, 1);
        System.err.println("one: " + readOne);
        DebugOutputBSTR(readOne);

        System.err.println("destroy = 0x" + Long.toHexString(hr));
    }

}
