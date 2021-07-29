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

import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.ProxyTextRangeProvider;

import javafx.geometry.Point2D;
import javafx.uia.ITextProvider;
import javafx.uia.ITextRangeProvider;

public class TextProviderAdapter extends BaseAdapter<ITextProvider> implements NativeITextProvider {

    public TextProviderAdapter(ProxyAccessible accessible, ITextProvider provider) {
        super(accessible, provider);
    }

    private long toNative(ITextRangeProvider provider) {
        ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, provider);
        return proxy.getNativeProvider();
    }

    private long[] toNative(ITextRangeProvider[] providers) {
        long result[] = new long[providers.length];
        for (int i = 0; i < providers.length; i++) {
            result[i] = toNative(providers[i]);
        }
        return result;
    }



    @Override
    public long get_DocumentRange() {
        System.err.println("#get_DocumentRange()");
        ITextRangeProvider range = provider.get_DocumentRange();
        return toNative(range);
    }

    @Override
    public int get_SupportedTextSelection() {
        System.err.println("#get_SupportedTextSelection()");
        return provider.get_SupportedTextSelection().getNativeValue();
    }

    @Override
    public long[] GetSelection() {
        System.err.println("#GetSelection()");
        ITextRangeProvider[] ranges = provider.GetSelection();
        return toNative(ranges);
    }

    @Override
    public long[] GetVisibleRanges() {
        System.err.println("#GetVisibleRanges()");
        ITextRangeProvider[] ranges = provider.GetVisibleRanges();
        return toNative(ranges);
    }

    @Override
    public long RangeFromChild(long childElement) {
        System.err.println("#RangeFromChild()");
        return 0;
    }

    @Override
    public long RangeFromPoint(double x, double y) {
        System.err.println("#RangeFromPoint("+x+", "+y+")");
        ITextRangeProvider range = provider.RangeFromPoint(new Point2D(x, y));
        if (range != null) {
            return toNative(range);
        } else {
            return 0L;
        }
    }

  

}
