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

import java.util.List;

import javafx.geometry.Bounds;
import javafx.scene.input.KeyCombination;

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
public interface IUIAElement {
    
    interface IUIAElementEvents {
        /** 
         * raises an <code>UIA_AutomationPropertyChangedEventId</code> for <code>UIA_ControlTypePropertyId</code>
         * @param oldValue the old value
         * @param newValue the new value
        */
        void notifyControlTypeChanged(ControlType oldValue, ControlType newValue);
        /** 
         * raises an <code>UIA_AutomationPropertyChangedEventId</code> for <code>UIA_BoundingRectanglePropertyId</code>
         * @param oldValue the old value
         * @param newValue the new value
         * */
        void notifyBoundsChanged(Bounds oldValue, Bounds newValue);

        /** raises an <code>UIA_StructureChangedEventId</code> */
        void notifyStructureChanged();
    }

    /**
     * 
     * @return 
     *   null if it is attached to a JavaFX node
     *   the parent if it is a virtual element
     */
    IUIAElement getParent();

    /**
     * 
     * @return
     *   the virtual children of this element.
     *   <p>Note: if an element has children its JavaFX children are ignored.</p>
     */
    List<IUIAElement> getChildren();

    /**
     * if the element is purely virtual
     * @return virtual
     */
    default boolean isVirtual() {
        return getParent() != null;
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
    ControlType getControlType();

    /** 
     * The bounds of the element, in screen space 
     * <p>this method is from the IRawElementProviderFragment</p>
     * @return
     *   the elements bounds in screen space
     */
    Bounds getBounds();

    interface Common {
         /** UIA_NamePropertyId
          * @return the name
          */
        String getName();
        /** UIA_IsContentElementPropertyId 
         * @return true if content element
        */
        boolean isContentElement();
        /** UIA_IsControlElementPropertyId 
         * @return true if control element
        */
        boolean isControlElement();
        /** UIA_IsEnabledPropertyId 
         * @return true if enabled
        */
        boolean isEnabled();
        /** UIA_AccessKeyPropertyId 
         * @return the access key
        */
        String getAccessKey();
        /** UIA_AcceleratorKeyPropertyId 
         * @return the accelerator key combination
        */
        KeyCombination getAcceleratorKey();
        /** UIA_HelpTextPropertyId 
         * @return the help text
        */
        String getHelpText();
        /** UIA_LocalizedControlTypePropertyId 
         * @return the localized control type label
        */
        String getLocalizedControlType();
        /** UIA_HasKeyboardFocusPropertyId 
         * @return true if has keyboard focus
        */
        boolean hasKeyboardFocus();
        /** UIA_IsKeyboardFocusablePropertyId 
         * @return true if is keyboard focusable
        */
        boolean isKeyboardFocusable();
        /** UIA_IsPasswordPropertyId 
         * @return true if password field
        */
        boolean isPassword();


    }

    /**
     * Sets the focus to this element.
     * <p>this method is from the IRawElementProviderFragment</p>
     */
    void SetFocus();

    void initialize(IUIAElementEvents events);

    default boolean isProviderAvailable(Class<?> providerType) {
        return providerType.isAssignableFrom(getClass());
    }
    @SuppressWarnings("unchecked")
    default <T> T getProvider(Class<T> providerType) {
        return (T) this;
    }
}
