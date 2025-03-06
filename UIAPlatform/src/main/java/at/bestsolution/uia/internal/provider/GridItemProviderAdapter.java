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

import java.util.Optional;

import at.bestsolution.uia.IGridItemProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.internal.Logger;
import at.bestsolution.uia.internal.LoggerFactory;
import at.bestsolution.uia.internal.ProxyAccessible;
import at.bestsolution.uia.internal.ProxyAccessibleRegistry;

public class GridItemProviderAdapter extends BaseAdapter<IGridItemProvider> implements NativeIGridItemProvider {

  private final static Logger LOG = LoggerFactory.create(GridItemProviderAdapter.class);

    public GridItemProviderAdapter(ProxyAccessible accessible, IGridItemProvider provider) {
        super(accessible, provider);
    }

    @Override
    public int get_Column() {
        return provider.get_Column();
    }

    @Override
    public int get_ColumnSpan() {
        return provider.get_ColumnSpan();
    }

    @Override
    public int get_Row() {
        return provider.get_Row();
    }

    @Override
    public int get_RowSpan() {
        return provider.get_RowSpan();
    }

    @Override
    public long get_ContainingGrid() {
        IUIAElement grid = provider.get_ContainingGrid();
        return Optional.ofNullable(grid)
        .map(el -> {
            ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().findAccessible(el);
            if (acc == null) {
                LOG.debug(this, () -> "Creating virtual for GridItem#ContainingGrid!!!");
                acc = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, el);
            }
            return acc;
        })
        .map(ProxyAccessible::getNativeAccessible)
        .orElse(0L);
    }
}
