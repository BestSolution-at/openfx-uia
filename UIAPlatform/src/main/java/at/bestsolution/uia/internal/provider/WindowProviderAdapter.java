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
package at.bestsolution.uia.internal.provider;

import at.bestsolution.uia.IWindowProvider;
import at.bestsolution.uia.WindowVisualState;
import at.bestsolution.uia.internal.ProxyAccessible;

public class WindowProviderAdapter extends BaseAdapter<IWindowProvider> implements NativeIWindowProvider {

    public WindowProviderAdapter(ProxyAccessible accessible, IWindowProvider provider) {
        super(accessible, provider);
    }

    @Override
    public void Close() {
        provider.Close();
    }

    @Override
    public boolean get_CanMaximize() {
        return provider.get_CanMaximize();
    }

    @Override
    public boolean get_CanMinimize() {
        return provider.get_CanMinimize();
    }

    @Override
    public boolean get_IsModal() {
        return provider.get_IsModal();
    }

    @Override
    public boolean get_IsTopmost() {
        return provider.get_IsTopmost();
    }

    @Override
    public int get_WindowInteractionState() {
        return Convert.convertNativeEnum(provider.get_WindowInteractionState());
    }

    @Override
    public int get_WindowVisualState() {
        return Convert.convertNativeEnum(provider.get_WindowVisualState());
    }

    @Override
    public void SetVisualState(int state) {
        WindowVisualState.fromNativeValue(state).ifPresent(provider::SetVisualState);
    }

    @Override
    public boolean WaitForInputIdle(int milliseconds) {
        return provider.WaitForInputIdle(milliseconds);
    }

}
