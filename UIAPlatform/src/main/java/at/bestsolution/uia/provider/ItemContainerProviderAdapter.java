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
import at.bestsolution.uia.javafx.uia.IItemContainerProvider;
import at.bestsolution.uia.javafx.uia.IPropertyId;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.Variant;


public class ItemContainerProviderAdapter extends BaseAdapter<IItemContainerProvider> implements NativeIItemContainerProvider {

    public ItemContainerProviderAdapter(ProxyAccessible accessible, IItemContainerProvider provider) {
        super(accessible, provider);
    }

    @Override
    public long FindItemByProperty(long pStartAfter, int propertyId, long pVariant) {
      // map pointer to IUIAElement
      ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().getByNative(pStartAfter);
      IUIAElement startAfter = acc.getUIAElement();
      // map propertyId
      IPropertyId propId = IPropertyId.fromNativeValue(propertyId);

      // map variant
      Variant variant = Variant.fromNativePointer(pVariant);

      IUIAElement result = provider.FindItemByProperty(startAfter, propId, variant);

      ProxyAccessible res = ProxyAccessibleRegistry.getInstance().findAccessible(result);
      return res.getNativeAccessible();
    }


}
