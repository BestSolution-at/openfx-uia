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

import java.util.Arrays;
import java.util.Optional;

import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;

import javafx.uia.ISelectionProvider;

public class SelectionProviderAdapter extends BaseAdapter<ISelectionProvider> implements NativeISelectionProvider {

    public SelectionProviderAdapter(ProxyAccessible accessible, ISelectionProvider provider) {
        super(accessible, provider);
    }

    @Override
    public boolean get_CanSelectMultiple() {
        return provider.get_CanSelectMultiple();
    }

    @Override
    public boolean get_IsSelectionRequired() {
        return provider.get_IsSelectionRequired();
    }

    @Override
    public long[] GetSelection() {
        return
        Arrays.stream(provider.GetSelection())
        .mapToLong(element -> {
            return Optional.ofNullable(element)
            .map(el -> {
                ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().findFXAccessible(el);
                if (acc == null) {
                    acc = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, el);
                }
                return acc;
            })
            .map(ProxyAccessible::getNativeAccessible)
            .orElse(0L);
        })
        .filter(value -> value != 0)
        .toArray();
    }
    
    
}
