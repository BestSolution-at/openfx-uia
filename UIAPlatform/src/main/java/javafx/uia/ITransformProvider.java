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
 * Provides access to controls that can be moved, resized, and/or rotated within a two-dimensional space.
 * <p>
 * Implemented on a Microsoft UI Automation provider that must support the Transform control pattern.
 * Support for this control pattern is not limited to objects on the desktop. This control pattern must also be implemented by the children of a container object as long as the children can be moved, resized, or rotated freely within the boundaries of the container.
 * </p>
 */
public interface ITransformProvider extends IInitable {
    
    /**
	 * context object for ITransformProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class TransformProviderContext {

        public final IProperty<Boolean> CanRotate;
        public final IProperty<Boolean> CanResize;
        public final IProperty<Boolean> CanMove;

		public TransformProviderContext(IInitContext init, ITransformProvider provider) {
            CanRotate = init.addProperty(StandardPropertyIds.UIA_TransformCanRotatePropertyId, provider::get_CanRotate, StandardVariantConverters.BOOL_Boolean);
            CanResize = init.addProperty(StandardPropertyIds.UIA_TransformCanResizePropertyId, provider::get_CanResize, StandardVariantConverters.BOOL_Boolean);
            CanMove = init.addProperty(StandardPropertyIds.UIA_TransformCanMovePropertyId, provider::get_CanMove, StandardVariantConverters.BOOL_Boolean);
        }
	}

    /**
     * Indicates whether the control can be resized.
     * @return true if control can be rotated
     */
    boolean get_CanRotate();
    /**
     * Indicates whether the control can be rotated.
     * @return true if control can be resized
     */
    boolean get_CanResize();
    /**
     * Indicates whether the control can be moved.
     * @return true if control can be moved
     */
    boolean get_CanMove();

    /**
     * Moves the control.
     * <p>
     * An object cannot be moved, resized or rotated such that its resulting screen location would be completely outside the coordinates of its container and inaccessible to keyboard or mouse. For example, when a top-level window is
     * moved completely off-screen or a child object is moved outside the boundaries of the container's viewport. In these cases the object is placed as close to the requested screen coordinates as possible with the top or left coordinates overridden to be within the container boundaries.
     * </p>
     * @param x The absolute screen coordinates of the left side of the control.
     * @param y The absolute screen coordinates of the top of the control.
     */
    void Move(double x, double y);

    /**
     * Resizes the control.
     * <p>
     * When called on a control supporting split panes, this method might have the side effect of resizing other contiguous panes.
     * An object cannot be moved, resized, or rotated such that its resulting screen location would be completely outside the coordinates of its container and inaccessible to keyboard or mouse. For example, a top-level window moved completely off-screen or a child object moved outside the boundaries of the container's viewport. In these cases the object is placed as close to the requested screen coordinates as possible with the top or left coordinates overridden to be within the container boundaries.
     * </p>
     * @param width The new width of the window in pixels.
     * @param height The new height of the window in pixels.
     */
    void Resize(double width, double height);

    /**
     * Rotates the control.
     * <p>
     * An object cannot be moved, resized, or rotated such that its resulting screen location would be completely outside the coordinates of its container and inaccessible to keyboard or mouse. For example, a top-level window moved completely off-screen or a child object moved outside the boundaries of the container's viewport. In these cases the object is placed as close to the requested screen coordinates as possible with the top or left coordinates overridden to be within the container boundaries.
     * </p>
     * @param degrees The number of degrees to rotate the control. A positive number rotates clockwise; a negative number rotates counterclockwise.
     */
    void Rotate(double degrees);

}
