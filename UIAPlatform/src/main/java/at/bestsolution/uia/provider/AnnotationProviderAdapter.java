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
import at.bestsolution.uia.javafx.uia.IAnnotationProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;

public class AnnotationProviderAdapter extends BaseAdapter<IAnnotationProvider> implements NativeIAnnotationProvider {

    public AnnotationProviderAdapter(ProxyAccessible accessible, IAnnotationProvider provider) {
        super(accessible, provider);
    }

    @Override
    public int get_AnnotationTypeId() {
        return provider.get_AnnotationTypeId().getNativeValue();
    }

    @Override
    public String get_AnnotationTypeName() {
        return provider.get_AnnotationTypeName();
    }

    @Override
    public String get_Author() {
        return provider.get_Author();
    }

    @Override
    public String get_DateTime() {
        return provider.get_DateTime();
    }

    @Override
    public long get_Target() {
        IUIAElement el = provider.get_Target();
        if (el != null) {
            ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().findFXAccessible(el);
            if (acc == null) {
                acc = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, el);
            }
            if (acc != null) {
                return acc.getNativeAccessible();
            }
        }
        return 0;
    }

}
