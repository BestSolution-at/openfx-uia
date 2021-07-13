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
    ControlType getControlType();

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

    default boolean isProviderAvailable(Class<?> providerType) {
        return providerType.isAssignableFrom(getClass());
    }
    @SuppressWarnings("unchecked")
    default <T> T getProvider(Class<T> providerType) {
        return (T) this;
    }

}
