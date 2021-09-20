/*
 * Copyright (c) 2013, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.glass.ui.uia.glass;

import java.util.Arrays;

public final class WinVariant {
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

    public short vt;

    public short iVal;
    public int lVal;
    public float fltVal;
    public double dblVal;
    public boolean boolVal;
    public String bstrVal;

    public double[] pDblVal;
    public long punkVal;

    // this is not really correct, pDblVal and pFltVal are not intended to be mapped to safearray, but we follow here the javafx way of handling R8|ARRAY via pDblVal
    public float[] pFltVal;

    public long[] pPunkVal; // used for safe array of long (IUnknown pointers)
    public int[] pLVal; // used for safe array of int

    @Override
    public String toString() {
        String value = "?";
        switch (vt) {
            case VT_EMPTY: value = "<empty>";
            break;
            case VT_NULL: value = "<null>"; 
            break;
            case VT_I2: value = "" + iVal; 
            break;
            case VT_I4: value = "" + lVal; 
            break;
            case VT_I4 | VT_ARRAY: value = Arrays.toString(pLVal);
            break;
            case VT_R4: value = "" + fltVal; 
            break;
            case VT_R4 | VT_ARRAY: value = Arrays.toString(pFltVal);
            break;
            case VT_R8: value = "" + dblVal;
            break;
            case VT_R8 | VT_ARRAY: value = Arrays.toString(pDblVal);
            break;
            case VT_BOOL: value = "" + boolVal; 
            break;
            case VT_BSTR: value = bstrVal;  
            break;
            case VT_UNKNOWN: value = "<IUnknown> " + punkVal;
            break;
            case VT_UNKNOWN | VT_ARRAY: value = Arrays.toString(pPunkVal);
            break;

        }
        return "WinVariant[" + value + "]";
    }
}
