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

import com.sun.glass.ui.uia.glass.WinVariant;

public class Variant {

    private WinVariant winVariant;

    private Variant(WinVariant ww) {
        this.winVariant = ww;
    }

    public WinVariant toWinVariant() {
        return winVariant;
    }

    public static Variant vt_empty() {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_EMPTY;
        return new Variant(v);
    }

    public static Variant vt_null() {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_NULL;
        return new Variant(v);
    }

    public static Variant vt_i2(short value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_I2;
        v.iVal = value;
        return new Variant(v);
    }

    public static Variant vt_i4(int value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_I4;
        v.lVal = value;
        return new Variant(v);
    }

    public static Variant vt_r4(float value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_R4;
        v.fltVal = value;
        return new Variant(v);
    }

    public static Variant vt_r8(double value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_R8;
        v.dblVal = value;
        return new Variant(v);
    }

    public static Variant vt_bool(boolean value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_BOOL;
        v.boolVal = value;
        return new Variant(v);
    }

    public static Variant vt_bstr(String value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_BSTR;
        v.bstrVal = value;
        return new Variant(v);
    }

    public static Variant vt_unknown(long value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_UNKNOWN;
        v.punkVal = value;
        return new Variant(v);
    }

    public static Variant vt_r8_array(double[] value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_R8 | WinVariant.VT_ARRAY;
        v.pDblVal = value;
        return new Variant(v);
    }

    public static Variant vt_r4_array(float[] value) {
        WinVariant v = new WinVariant();
        v.vt = WinVariant.VT_R4 | WinVariant.VT_ARRAY;
        v.pFltVal = value;
        return new Variant(v);
    }

}
