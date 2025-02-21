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
package at.bestsolution.uia.provider;

import at.bestsolution.uia.ProxyAccessible;
import at.bestsolution.uia.ProxyAccessibleRegistry;
import at.bestsolution.uia.javafx.uia.ITextProvider;
import at.bestsolution.uia.javafx.uia.ITextRangeProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import javafx.geometry.Point2D;

public class TextProviderAdapter extends BaseAdapter<ITextProvider> implements NativeITextProvider {

    public TextProviderAdapter(ProxyAccessible accessible, ITextProvider provider) {
        super(accessible, provider);
    }


    @Override
    public long get_DocumentRange() {
        return wrapNative(provider.get_DocumentRange());
    }

    @Override
    public int get_SupportedTextSelection() {
        return provider.get_SupportedTextSelection().getNativeValue();
    }

    @Override
    public long[] GetSelection() {
        return wrapNative(provider.GetSelection());
    }

    @Override
    public long[] GetVisibleRanges() {
        return wrapNative(provider.GetVisibleRanges());
    }

    @Override
    public long RangeFromChild(long childElement) {
        ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().getByNative(childElement);
        IUIAElement el = acc.getUIAElement();

        return wrapNative(provider.RangeFromChild(el));
    }

    @Override
    public long RangeFromPoint(double x, double y) {
        ITextRangeProvider range = provider.RangeFromPoint(new Point2D(x, y));
        if (range != null) {
            return wrapNative(range);
        } else {
            return 0L;
        }
    }



}
