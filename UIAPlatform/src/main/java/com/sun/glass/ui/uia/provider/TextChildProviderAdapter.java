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
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;

import javafx.uia.ITextChildProvider;
import javafx.uia.IUIAElement;

public class TextChildProviderAdapter extends BaseAdapter<ITextChildProvider> implements NativeITextChildProvider {

    public TextChildProviderAdapter(ProxyAccessible accessible, ITextChildProvider provider) {
        super(accessible, provider);
    }

    @Override
    public long get_TextContainer() {
        IUIAElement container = provider.get_TextContainer();
        // TODO do we need to create it ???
        return ProxyAccessibleRegistry.getInstance().findAccessible(container).getNativeAccessible();
    }

    @Override
    public long get_TextRange() {
        return wrapNative(provider.get_TextRange());
    }

}
