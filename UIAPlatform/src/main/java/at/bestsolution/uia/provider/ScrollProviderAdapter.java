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
import at.bestsolution.uia.javafx.uia.IScrollProvider;
import at.bestsolution.uia.javafx.uia.ScrollAmount;

public class ScrollProviderAdapter extends BaseAdapter<IScrollProvider> implements NativeIScrollProvider {

    public ScrollProviderAdapter(ProxyAccessible accessible, IScrollProvider provider) {
        super(accessible, provider);
    }

    @Override
    public boolean get_HorizontallyScrollable() {
        return provider.get_HorizontallyScrollable();
    }

    @Override
    public double get_HorizontalScrollPercent() {
        return provider.get_HorizontalScrollPercent();
    }

    @Override
    public double get_HorizontalViewSize() {
        return provider.get_HorizontalViewSize();
    }

    @Override
    public boolean get_VerticallyScrollable() {
        return provider.get_VerticallyScrollable();
    }

    @Override
    public double get_VerticalScrollPercent() {
        return provider.get_VerticalScrollPercent();
    }

    @Override
    public double get_VerticalViewSize() {
        return provider.get_VerticalViewSize();
    }

    @Override
    public void Scroll(int horizontalAmount, int verticalAmount) {
        provider.Scroll(ScrollAmount.fromNativeValue(horizontalAmount).orElse(ScrollAmount.NoAmount), ScrollAmount.fromNativeValue(verticalAmount).orElse(ScrollAmount.NoAmount));
    }

    @Override
    public void SetScrollPercent(double horizontalPercent, double verticalPercent) {
        provider.SetScrollPercent(horizontalPercent, verticalPercent);
    }

}
