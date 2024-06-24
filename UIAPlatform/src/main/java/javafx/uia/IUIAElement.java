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

import java.util.function.Consumer;

import javafx.geometry.Bounds;

/**
 * central interface of the openfx-uia api.
 * <p>
 * In the UIA API this interface represents several interfaces:
 * </p>
 * <ul>
 * <li>IRawElementProviderSimple</li>
 * <li>IRawElementProviderFragment</li>
 * <li>IRawElementProviderRoot??</li>
 * </ul>
 * <p>
 * This adoption of the UIA api is required to wire it with javafx.
 * </p>
 */
public interface IUIAElement extends IInitable {

	/**
	 * context object for IUIAElement.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class UIAElementContext {
        //public IProperty<Integer> Id;
        public final IProperty<String> AutomationId;
        public final IProperty<ControlType> ControlType;
        public final IProperty<Bounds> BoundingRectangle;

		public UIAElementContext(IInitContext init, IUIAElement element) {
            //Id = init.addProperty(StandardPropertyIds.UIA_RuntimeIdPropertyId)
            AutomationId = init.addAutomationIdProperty(element::getAutomationId);
            ControlType = init.addControlTypeProperty(element::getControlType);
            BoundingRectangle = init.addBoundingRectangleProperty(element::getBounds);
		}
	}

    /**
     * the id
     * @return the id
     */
    default int getId() {
        return UIA.Defaults.getId(this);
    }

    /**
     * The automation id
     * @return the automation id
     */
    default String getAutomationId() {
        return UIA.Defaults.getAutomationId(this);
    }

    /**
     * the controltype of the element
     * @return the control type
     */
    default ControlType getControlType() {
        // we default to the custom control type
        return ControlType.UIA_CustomControlTypeId;
    }

    /** 
     * The bounds of the element, in screen space 
     * <p>this method is from the IRawElementProviderFragment</p>
     * @return
     *   the elements bounds in screen space
     */
    Bounds getBounds();

    /**
     * Sets the focus to this element.
     * <p>this method is from the IRawElementProviderFragment</p>
     */
    void SetFocus();

    default <C> void withContext(Class<C> contextType, Consumer<C> run) {
        UIA.Defaults.runWithContext(contextType, this, run);
    }

    default boolean isProviderAvailable(Class<?> providerType) {
        return providerType.isAssignableFrom(getClass());
    }
    @SuppressWarnings("unchecked")
    default <T> T getProvider(Class<T> providerType) {
        return (T) this;
    }

    default IUIAElement GetFocusDelegate() {
        return this;
    }

}
