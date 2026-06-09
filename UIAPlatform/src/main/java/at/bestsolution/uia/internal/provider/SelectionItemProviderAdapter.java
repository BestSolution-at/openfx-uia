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



import at.bestsolution.uia.ISelectionItemProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.internal.Logger;
import at.bestsolution.uia.internal.LoggerFactory;
import at.bestsolution.uia.internal.ProxyAccessible;
import at.bestsolution.uia.internal.ProxyAccessibleRegistry;

public class SelectionItemProviderAdapter extends BaseAdapter<ISelectionItemProvider> implements NativeISelectionItemProvider {

    private final static Logger LOG = LoggerFactory.create(SelectionItemProviderAdapter.class);

    public SelectionItemProviderAdapter(ProxyAccessible accessible, ISelectionItemProvider provider) {
        super(accessible, provider);
    }

    @Override
    public void AddToSelection() {
        LOG.debug(this, () -> "AddToSelection()");
        provider.AddToSelection();
    }

    @Override
    public void RemoveFromSelection() {
        LOG.debug(this, () -> "RemoveFromSelection()");
        provider.RemoveFromSelection();
    }

    @Override
    public void Select() {
        LOG.debug(this, () -> "Select()");
        provider.Select();
    }

    @Override
    public boolean get_IsSelected() {
        return provider.get_IsSelected();
    }

    @Override
    public long get_SelectionContainer() {
        IUIAElement el = provider.get_SelectionContainer();
        if (el == null) return 0L;
        ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().findFXAccessible(el);
        if (acc == null) {
            acc = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, el);
        }
        return acc.getNativeAccessible();
    }

}
