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

import java.util.function.Supplier;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCombination;

public interface IPropertySupport {
    <T> IProperty<T> addProperty(IPropertyId id, Supplier<T> getter, IVariantConverter<T> converter);

    default IProperty<ControlType> addControlTypeProperty(Supplier<ControlType> getter) {
        return addProperty(StandardPropertyIds.UIA_ControlTypePropertyId, getter, StandardVariantConverters.I4_INativeEnum(ControlType::fromNativeValue));
    }

    default IProperty<String> addNameProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_NamePropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<Boolean> addIsContentElementProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsContentElementPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<Boolean> addIsControlElementProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsControlElementPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<Boolean> addIsEnabledElementProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsEnabledPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<KeyCombination> addAccessKeyProperty(Supplier<KeyCombination> getter) {
        return addProperty(StandardPropertyIds.UIA_AccessKeyPropertyId, getter, StandardVariantConverters.BSTR_KeyCombination);
    }

    default IProperty<KeyCombination> addAcceleratorKeyProperty(Supplier<KeyCombination> getter) {
        return addProperty(StandardPropertyIds.UIA_AcceleratorKeyPropertyId, getter, StandardVariantConverters.BSTR_KeyCombination);
    }
   
    default IProperty<String> addHelpTextProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_HelpTextPropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<String> addLocalizedControlTypeProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_LocalizedControlTypePropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<Boolean> addHasKeyboardFocusProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_HasKeyboardFocusPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<Boolean> addIsKeyboardFocusableProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsKeyboardFocusablePropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<Boolean> addIsPasswordProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsPasswordPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<Bounds> addBoundingRectangleProperty(Supplier<Bounds> getter) {
        return addProperty(StandardPropertyIds.UIA_BoundingRectanglePropertyId, getter, StandardVariantConverters.R8Array_Bounds);
    }

    default IProperty<String> addAutomationIdProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_AutomationIdPropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<String> addClassNameProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_ClassNamePropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<Point2D> addClickablePointProperty(Supplier<Point2D> getter) {
        return addProperty(StandardPropertyIds.UIA_ClickablePointPropertyId, getter, StandardVariantConverters.R8Array_Point2D);
    }

    // Culture I4 -> java mapping to locale ?

    default IProperty<IUIAElement> addLabeledByProperty(Supplier<IUIAElement> getter) {
        return addProperty(StandardPropertyIds.UIA_LabeledByPropertyId, getter, StandardVariantConverters.UNKNOWN_IUIAElement);
    }

    default IProperty<String> addItemTypeProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_ItemTypePropertyId, getter, StandardVariantConverters.BSTR);
    }

    default IProperty<Boolean> addIsOffscreenProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsOffscreenPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<OrientationType> addOrientationTypeProperty(Supplier<OrientationType> getter) {
        return addProperty(StandardPropertyIds.UIA_OrientationPropertyId, getter, StandardVariantConverters.I4_INativeEnum(OrientationType::fromNativeValue));
    }

    default IProperty<Boolean> addIsRequiredForFormProperty(Supplier<Boolean> getter) {
        return addProperty(StandardPropertyIds.UIA_IsRequiredForFormPropertyId, getter, StandardVariantConverters.BOOL);
    }

    default IProperty<String> addItemStatusProperty(Supplier<String> getter) {
        return addProperty(StandardPropertyIds.UIA_ItemStatusPropertyId, getter, StandardVariantConverters.BSTR);
    }

    interface ValuePatternProperties {
        <T> IProperty<T> addProperty(IPropertyId id, Supplier<T> getter, IVariantConverter<T> converter);

        default IProperty<String> addValueProperty(Supplier<String> getter) {
            return addProperty(StandardPropertyIds.UIA_ValueValuePropertyId, getter, StandardVariantConverters.BSTR);
        }

        default IProperty<Boolean> addIsReadOnlyProperty(Supplier<Boolean> getter) {
            return addProperty(StandardPropertyIds.UIA_ValueIsReadOnlyPropertyId, getter, StandardVariantConverters.BOOL);
        }
    }

}
