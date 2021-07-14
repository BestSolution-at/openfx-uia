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
package com.sun.glass.ui.uia;

import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.geometry.Bounds;
import javafx.uia.ITextAttributeId;
import javafx.uia.ITextRangeProvider;
import javafx.uia.ITextRangeProvider2;
import javafx.uia.TextPatternRangeEndpoint;
import javafx.uia.TextUnit;
import javafx.uia.Variant;

public class ProxyTextRangeProvider {
    
    private native static void _initIDs();
    static {
        _initIDs();
    }

    /* Creates a GlassTextRangeProvider linked to the caller (GlobalRef) */
    private native long _createTextRangeProvider(long accessible);

    /* Releases the GlassTextRangeProvider and deletes the GlobalRef */
    private native void _destroyTextRangeProvider(long textRangeProvider);


    private ProxyAccessible accessible;
    private long peer;

    private ITextRangeProvider impl;


    private static int idCount = 1;
    private int id;

    ProxyTextRangeProvider(ProxyAccessible accessible, ITextRangeProvider impl) {
        this.accessible = accessible;
        this.impl = impl;
        peer = _createTextRangeProvider(accessible.getNativeAccessible());
        id = idCount++;
    }

    long getNativeProvider() {
        return peer;
    }

    void dispose() {
        _destroyTextRangeProvider(peer);
        peer = 0L;
    }


    /***********************************************/
    /*            ITextRangeProvider               */
    /***********************************************/
    private long Clone() {
        ITextRangeProvider clone = impl.Clone();
        
        ProxyTextRangeProvider cloneProxy = new ProxyTextRangeProvider(accessible, clone);
        return cloneProxy.getNativeProvider();

        /* Note: Currently Clone() natively does not call AddRef() on the returned object.
         * This mean JFX does not keep a reference to this object, consequently it does not
         * need to free it.
         */
    }

    private boolean Compare(ProxyTextRangeProvider range) {
        return impl.Compare(range.impl);
    }

    private int CompareEndpoints(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
        return impl.CompareEndpoints(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.impl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
    }

    private void ExpandToEnclosingUnit(int unit) {
        impl.ExpandToEnclosingUnit(TextUnit.fromNativeValue(unit).get());
    }

    private long FindAttribute(int attributeId, WinVariant val, boolean backward) {
        ITextRangeProvider range = impl.FindAttribute(ITextAttributeId.fromNativeValue(attributeId), convert(val), backward);

        ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, range);
        return proxy.getNativeProvider();
    }

    private long FindText(String text, boolean backward, boolean ignoreCase) {
        ITextRangeProvider range = impl.FindText(text, backward, ignoreCase);
        ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, range);
        return proxy.getNativeProvider();
    }

    private Variant convert(WinVariant variant) {
        // TODO
        /*
        Variant result = new Variant();
        result.vt = variant.vt;
        result.boolVal = variant.boolVal;
        result.bstrVal = variant.bstrVal;
        result.dblVal = variant.dblVal;
        result.fltVal = variant.fltVal;
        result.iVal = variant.iVal;
        result.lVal = variant.lVal;
        result.pDblVal = variant.pDblVal;
        result.punkVal = variant.punkVal;
        */
        return Variant.vt_empty();
    }
    private WinVariant convert(Variant variant) {
        return variant.toWinVariant();
    }

    private WinVariant GetAttributeValue(int attributeId) {
        Variant result = impl.GetAttributeValue(ITextAttributeId.fromNativeValue(attributeId));
        return convert(result);
    }

    private double[] GetBoundingRectangles() {
        Bounds[] bounds = impl.GetBoundingRectangles();

        // TODO move bounds conversion code
        if (bounds != null) {
            double[] result = new double[bounds.length * 4];
            int index = 0;
            for (int i = 0; i < bounds.length; i++) {
                Bounds b = bounds[i];
                result[index++] = b.getMinX();
                result[index++] = b.getMinY();
                result[index++] = b.getWidth();
                result[index++] = b.getHeight();
            }
            return result;
        }
        return null;
    }

    private long GetEnclosingElement() {
        // TODO ????
        return accessible.getNativeAccessible();
    }

    private String GetText(int maxLength) {
        return impl.GetText(maxLength);
    }

    private int Move(int unit, final int requestedCount) {
        return impl.Move(TextUnit.fromNativeValue(unit).get(), requestedCount);
    }

    private int MoveEndpointByUnit(int endpoint, int unit, final int requestedCount) {
        return impl.MoveEndpointByUnit(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), TextUnit.fromNativeValue(unit).get(), requestedCount);
    }

    private void MoveEndpointByRange(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
        impl.MoveEndpointByRange(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.impl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
    }

    private void Select() {
        impl.Select();
    }

    private void AddToSelection() {
        impl.AddToSelection();
    }

    private void RemoveFromSelection() {
        impl.RemoveFromSelection();
    }

    private void ScrollIntoView(boolean alignToTop) {
        impl.ScrollIntoView(alignToTop);
    }

    private long[] GetChildren() {
        impl.GetChildren(); // TODO ??? IRaw stuff
        /* Not embedded object support currently */
        return new long[0];
    }

    /***********************************************/
    /*            ITextRangeProvider2              */
    /***********************************************/
    private void ShowContextMenu() {
        // TODO disable ITextRangeProvider2 interface in IUnknown casting if not available
        if (impl instanceof ITextRangeProvider2) {
            ((ITextRangeProvider2) impl).ShowContextMenu();
        }
    }

}
