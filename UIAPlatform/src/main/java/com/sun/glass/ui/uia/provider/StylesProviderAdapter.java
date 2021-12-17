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

import javafx.uia.IStylesProvider;
import javafx.uia.StandardVariantConverters;

public class StylesProviderAdapter extends BaseAdapter<IStylesProvider> implements NativeIStylesProvider {

    public StylesProviderAdapter(ProxyAccessible accessible, IStylesProvider provider) {
        super(accessible, provider);
    }

    @Override
    public String get_ExtendedProperties() {
        return provider.get_ExtendedProperties();
    }

    @Override
    public int get_FillColor() {
        return StandardVariantConverters.I4_Color.toVariant(provider.get_FillColor()).getLVal();
    }

    @Override
    public int get_FillPatternColor() {
        return StandardVariantConverters.I4_Color.toVariant(provider.get_FillPatternColor()).getLVal();
    }

    @Override
    public String get_FillPatternStyle() {
        return provider.get_FillPatternStyle();
    }

    @Override
    public String get_Shape() {
        return provider.get_Shape();
    }

    @Override
    public int get_StyleId() {
        return provider.get_StyleId();
    }

    @Override
    public String get_StyleName() {
        return provider.get_StyleName();
    }

}
