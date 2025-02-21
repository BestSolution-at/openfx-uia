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
import at.bestsolution.uia.javafx.uia.IMultipleViewProvider;

public class MultipleViewProviderAdapter extends BaseAdapter<IMultipleViewProvider> implements NativeIMultipleViewProvider {

    public MultipleViewProviderAdapter(ProxyAccessible accessible, IMultipleViewProvider provider) {
        super(accessible, provider);
    }

    @Override
    public int get_CurrentView() {
        return provider.get_CurrentView();
    }

    @Override
    public int[] GetSupportedViews() {
        return provider.GetSupportedViews();
    }

    @Override
    public String GetViewName(int viewId) {
        return provider.GetViewName(viewId);
    }

    @Override
    public void SetCurrentView(int viewId) {
        provider.SetCurrentView(viewId);
    }

}
