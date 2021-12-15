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
import com.sun.glass.ui.uia.ProxyAccessible.NCaretRangeResult;
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;
import com.sun.glass.ui.uia.ProxyTextRangeProvider;

import javafx.uia.ITextProvider2;
import javafx.uia.ITextRangeProvider;
import javafx.uia.IUIAElement;

public class TextProviderAdapter2 extends BaseAdapter<ITextProvider2> implements NativeITextProvider2 {

    public TextProviderAdapter2(ProxyAccessible accessible, ITextProvider2 provider) {
        super(accessible, provider);
    }

    private long toNative(ITextRangeProvider provider) {
        ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, provider);
        return proxy.getNativeProvider();
    }

    @Override
    public NCaretRangeResult GetCaretRange() {
        ITextProvider2.CaretRangeResult result = provider.GetCaretRange();
        NCaretRangeResult r = new NCaretRangeResult();
        r.isActive = result.isActive;
        r.range = toNative(result.range);
        return r;
    }

    @Override
    public long RangeFromAnnotation(long annotationElement) {
        ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().getByNative(annotationElement);
        IUIAElement el = acc.getUIAElement();

        ITextRangeProvider range = provider.RangeFromAnnotation(el);
        return toNative(range);
    }

}
