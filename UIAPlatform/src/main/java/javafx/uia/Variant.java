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

public class Variant {
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


    @Override
    public String toString() {
        String value = "";
        switch (vt) {
            case VT_EMPTY: value = "<empty>";
            break;
            case VT_NULL: value = "<null>"; 
            break;
            case VT_I2: value += iVal; 
            break;
            case VT_I4: value += iVal; 
            break;
            case VT_R4: value += fltVal; 
            break;
            case VT_R8: value += dblVal; 
            break;
            case VT_BOOL: value += boolVal; 
            break;
            case VT_BSTR: value += bstrVal; 
            break;
            case VT_ARRAY: value = "<array> " + punkVal; 
            break;
            case VT_UNKNOWN: value = "<unknown> " + punkVal;
            break;

        }
        return "Variant[" + value + "]";
    }
}
