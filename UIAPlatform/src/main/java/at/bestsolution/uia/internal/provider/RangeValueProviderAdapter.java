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

import at.bestsolution.uia.IRangeValueProvider;
import at.bestsolution.uia.internal.ProxyAccessible;

public class RangeValueProviderAdapter extends BaseAdapter<IRangeValueProvider> implements NativeIRangeValueProvider {

    public RangeValueProviderAdapter(ProxyAccessible accessible, IRangeValueProvider provider) {
        super(accessible, provider);
    }

    @Override
    public boolean get_IsReadOnly() {
        return provider.get_IsReadOnly();
    }

    @Override
    public double get_Value() {
        return provider.get_Value();
    }

    @Override
    public void SetValue(double value) {
        provider.SetValue(value);
    }

    @Override
    public double get_LargeChange() {
        return provider.get_LargeChange();
    }

    @Override
    public double get_SmallChange() {
        return provider.get_SmallChange();
    }

    @Override
    public double get_Maximum() {
        return provider.get_Maximum();
    }

    @Override
    public double get_Minimum() {
        return provider.get_Minimum();
    }

}
