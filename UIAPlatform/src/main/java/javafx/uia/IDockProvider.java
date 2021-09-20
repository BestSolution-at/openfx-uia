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
 * Provides access to an element in a docking container.
 * <p>
 * IDockProvider does not expose any properties of the docking container or any properties of controls that might be docked adjacent to the current control in the docking container.
 * Controls are docked relative to each other based on their current z-order; the higher their z-order placement, the farther they are placed from the specified edge of the docking container.
 * </p>
 */
public interface IDockProvider extends IInitable {

	/**
	 * context object for IDockProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class DockProviderContext {
		public final IProperty<DockPosition> DockPosition;

		public DockProviderContext(IInitContext init, IDockProvider provider) {
			DockPosition = init.addProperty(StandardPropertyIds.UIA_ToggleToggleStatePropertyId, provider::get_DockPosition, StandardVariantConverters.I4_INativeEnum(javafx.uia.DockPosition::fromNativeValue));
		}
	}
	
	/**
	 * Sets the docking position of this element.
	 * <p>
	 * A docking container is a control that allows the arrangement of child elements, both horizontally and vertically, relative to the boundaries of the docking container and other elements within the container.
	 * </p>
	 * @return The new docking position.
	 */
	void SetDockPosition(DockPosition dockPosition);

	/**
	 * Indicates the current docking position of this element.
	 * <p>
	 * A docking container is a control that allows the arrangement of child elements, both horizontally and vertically, relative to the boundaries of the docking container and other elements in the container.
	 * </p>	 
	 */
	DockPosition get_DockPosition();

}
