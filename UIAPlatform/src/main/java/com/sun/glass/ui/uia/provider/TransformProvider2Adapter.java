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

import javafx.uia.ITransformProvider2;
import javafx.uia.ZoomUnit;

public class TransformProvider2Adapter extends BaseAdapter<ITransformProvider2> implements NativeITransformProvider2 {
    
    public TransformProvider2Adapter(ProxyAccessible accessible, ITransformProvider2 provider) {
        super(accessible, provider);
    }

    @Override
    public boolean get_CanZoom() {
        return provider.get_CanZoom();
    }

    @Override
    public double get_ZoomLevel() {
        return provider.get_ZoomLevel();
    }

    @Override
    public double get_ZoomMinimum() {
        return provider.get_ZoomMinimum();
    }

    @Override
    public double get_ZoomMaximum() {
        return provider.get_ZoomMaximum();
    }

    @Override
    public void Zoom(double zoom) {
        provider.Zoom(zoom);
    }

    @Override
    public void ZoomByUnit(int zoomUnit) {
        ZoomUnit.fromNativeValue(zoomUnit).ifPresent(provider::ZoomByUnit);
    }

}
