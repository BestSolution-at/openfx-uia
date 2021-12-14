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
package javafx.uia;

/**
 * Extends the ITransformProvider interface to enable Microsoft UI Automation providers to expose properties to support the viewport zooming functionality of a control.
 */
public interface ITransformProvider2 extends IInitable {
    
    /**
	 * context object for ITransformProvider2.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class TransformProvider2Context {
        public final IProperty<Boolean> CanZoom;
        public final IProperty<Double> ZoomLevel;
        public final IProperty<Double> ZoomMaximum;
        public final IProperty<Double> ZoomMinimum;

		public TransformProvider2Context(IInitContext init, ITransformProvider2 provider) {
            CanZoom = init.addProperty(StandardPropertyIds.UIA_Transform2CanZoomPropertyId, provider::get_CanZoom, StandardVariantConverters.BOOL_Boolean);
            ZoomLevel = init.addProperty(StandardPropertyIds.UIA_Transform2ZoomLevelPropertyId, provider::get_ZoomLevel, StandardVariantConverters.R8_Double);
            ZoomMinimum = init.addProperty(StandardPropertyIds.UIA_Transform2ZoomMinimumPropertyId, provider::get_ZoomMaximum, StandardVariantConverters.R8_Double);
            ZoomMaximum = init.addProperty(StandardPropertyIds.UIA_Transform2ZoomMaximumPropertyId, provider::get_ZoomMinimum, StandardVariantConverters.R8_Double);
        }
	}

    /**
     * Indicates whether the control supports zooming of its viewport.
     * @return true if supports zooming
     */
    boolean get_CanZoom();

    /**
     * Retrieves the current zoom level of the element.
     * @return the current zoom level
     */
    double get_ZoomLevel();

    /**
     * Retrieves the maximum zoom level of the element.
     * @return the max zoom level
     */
    double get_ZoomMaximum();

    /**
     * Retrieves the minimum zoom level of the element.
     * @return the min zoom level
     */
    double get_ZoomMinimum();

    /**
     * Zooms the viewport of the control.
     * @param zoom The amount to zoom the viewport, specified as a percentage. The provider should zoom the viewport to the nearest supported value.
     */
    void Zoom(double zoom);

    /**
     * Zooms the viewport of the control by the specified logical unit.
     * @param zoomUnit The logical unit by which to increase or decrease the zoom of the viewport.
     */
    void ZoomByUnit(ZoomUnit zoomUnit);

}
