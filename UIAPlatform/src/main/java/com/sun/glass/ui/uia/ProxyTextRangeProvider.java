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
package com.sun.glass.ui.uia;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import com.sun.glass.ui.uia.glass.WinVariant;
import com.sun.glass.ui.uia.provider.Convert;
import com.sun.glass.ui.uia.winapi.Windows;

import javafx.geometry.Bounds;
import javafx.uia.ITextAttribute;
import javafx.uia.ITextAttributeId;
import javafx.uia.ITextAttributeSupport;
import javafx.uia.ITextRangeProvider;
import javafx.uia.ITextRangeProvider2;
import javafx.uia.IUIAElement;
import javafx.uia.IVariantConverter;
import javafx.uia.TextAttributeValue;
import javafx.uia.TextPatternRangeEndpoint;
import javafx.uia.TextUnit;
import javafx.uia.Variant;

public class ProxyTextRangeProvider {
    
    private native static void _initIDs();
    static {
        _initIDs();
    }

    /* Creates a GlassTextRangeProvider linked to the caller (GlobalRef) */
    private native long _createTextRangeProvider(long accessible);

    /* Releases the GlassTextRangeProvider and deletes the GlobalRef */
    private native void _destroyTextRangeProvider(long textRangeProvider);


    private ProxyAccessible accessible;
    private long peer;

    private ITextRangeProvider impl;


    private static int idCount = 1;
    private int id;

   
    private Map<ITextAttributeId, Supplier<TextAttributeValue<Object>>> attribs = new HashMap<>();
    private Map<ITextAttributeId, IVariantConverter<Object>> converters = new HashMap<>();
    private Map<ITextAttributeId, javafx.uia.FindAttribute<Object>> findAttributes = new HashMap<>();

    @SuppressWarnings("unchecked")
    private ITextAttributeSupport support = new ITextAttributeSupport(){

        @Override
        public <T> ITextAttribute<T> addAttribute(ITextAttributeId id, Supplier<TextAttributeValue<T>> getter, IVariantConverter<T> converter, javafx.uia.FindAttribute<T> findAttribute) {
            
            attribs.put(id, (Supplier<TextAttributeValue<Object>>) (Supplier<?>)getter);
            converters.put(id, (IVariantConverter<Object>) converter);
            if (findAttribute != null) {
                findAttributes.put(id, (javafx.uia.FindAttribute<Object>) findAttribute);
            }
            return null;
        }
        
    };

    public ProxyTextRangeProvider(ProxyAccessible accessible, ITextRangeProvider impl) {

        if (impl == null) {
            Logger.debug(this, () -> "ProxyTextRangeProvider cannot be created without an ITextRangeProvider");
            throw new NullPointerException("impl is null!");
        }

        this.accessible = accessible;
        this.impl = impl;
        peer = _createTextRangeProvider(accessible.getNativeAccessible());
        id = idCount++;

        Logger.debug(this, () -> "ProxyTextRangeProvider created. acc=" + accessible);

        impl.initialize(support);
    }

    public long getNativeProvider() {
        return peer;
    }

    void dispose() {
        _destroyTextRangeProvider(peer);
        peer = 0L;
    }


    /***********************************************/
    /*            ITextRangeProvider               */
    /***********************************************/
    private long Clone() {
        return Util.guard(() -> {
            ITextRangeProvider clone = impl.Clone();
            
            ProxyTextRangeProvider cloneProxy = new ProxyTextRangeProvider(accessible, clone);
            return cloneProxy.getNativeProvider();

            /* Note: Currently Clone() natively does not call AddRef() on the returned object.
            * This mean JFX does not keep a reference to this object, consequently it does not
            * need to free it.
            */
        }, 0L);
    }

    private boolean Compare(ProxyTextRangeProvider range) {
        return Util.guard(() -> {
            return impl.Compare(range.impl);
        });
    }

    private int CompareEndpoints(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
        return Util.guard(() -> {
            return impl.CompareEndpoints(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.impl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
        });
    }

    private void ExpandToEnclosingUnit(int unit) {
        Util.guard(() -> {
            impl.ExpandToEnclosingUnit(TextUnit.fromNativeValue(unit).get());
        });
    }

    private Variant convert(long variant) {
        short vt = Windows.VariantGetVt(variant);
        switch (vt) {
            case Windows.VT_ARRAY | Windows.VT_R4:
                return Variant.vt_r4_array(Windows.VariantGetFltSafeArray(variant));
            case Windows.VT_ARRAY | Windows.VT_R8:
                return Variant.vt_r8_array(Windows.VariantGetDblSafeArray(variant));
            case Windows.VT_I2:
                return Variant.vt_i2(Windows.VariantGetIVal(variant));
            case Windows.VT_I4:
                return Variant.vt_i4(Windows.VariantGetLVal(variant));
            case Windows.VT_R4:
                return Variant.vt_r4(Windows.VariantGetFltVal(variant));
            case Windows.VT_R8:
                return Variant.vt_r8(Windows.VariantGetDblVal(variant));
            case Windows.VT_BOOL:
                return Variant.vt_bool(Windows.VariantGetBoolVal(variant));
            case Windows.VT_BSTR:
                return Variant.vt_bstr(Windows.VariantGetBstrVal(variant));
            case Windows.VT_UNKNOWN:
                return Variant.vt_unknown(Windows.VariantGetPunkVal(variant));
        }
        Logger.warning(this, () -> "Variant unknown; using VT_EMPTY");
        Thread.dumpStack();
        return Variant.vt_empty();
    }

    private long FindAttribute(int attributeId, long variantValue, boolean backward) {
        return Util.guard(() -> {
            try {
                ITextAttributeId id = ITextAttributeId.fromNativeValue(attributeId);
                javafx.uia.FindAttribute<Object> findAttribute = findAttributes.get(id);
                if (findAttribute != null) {
                    Variant variant = convert(variantValue);
                    IVariantConverter<Object> converter = converters.get(id);
                    Object value = converter.toObject(variant);
                    ITextRangeProvider range = findAttribute.findAttribute(backward, value);
                    if (range != null) {
                        ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, range);
                        return proxy.getNativeProvider();
                    } else {
                        return 0L;
                    }
                } else {
                    return 0L;
                }


            } catch (Exception e) {
                e.printStackTrace();
                return 0L;
            }
        }, 0L);
    }

    private long FindAttribute(int attributeId, WinVariant val, boolean backward) {
        return 0L;
    }

    private long FindText(String text, boolean backward, boolean ignoreCase) {
        return Util.guard(() -> {
            ITextRangeProvider range = impl.FindText(text, backward, ignoreCase);
            if (range != null) {
                ProxyTextRangeProvider proxy = new ProxyTextRangeProvider(accessible, range);
                return proxy.getNativeProvider();
            } else {
                return 0L;
            }
        }, 0L);
    }

    private WinVariant convert(Variant variant) {
        return variant.toWinVariant();
    }

    private WinVariant GetAttributeValue(int attributeId) {
        return Util.guard(() -> {
            ITextAttributeId id = ITextAttributeId.fromNativeValue(attributeId);

            Supplier<TextAttributeValue<Object>> getter = attribs.get(id);

            if (getter != null) {

                TextAttributeValue<Object> value = getter.get();

                if (value.isNotSupported()) {
                    return Variant.vt_unknown(Windows.UiaGetReservedNotSupportedValue()).toWinVariant();
                }
                if (value.isMixed()) {
                    return Variant.vt_unknown(Windows.UiaGetReservedMixedAttributeValue()).toWinVariant();
                }

                return converters.get(id).toVariant(value.getValue()).toWinVariant();

            } else {
                return Variant.vt_empty().toWinVariant();
            }

        }, Variant.vt_empty().toWinVariant());
    }

    private double[] GetBoundingRectangles() {
        return Util.guard(() -> {
            Bounds[] bounds = impl.GetBoundingRectangles();
            return Convert.convertBoundsArrayDouble(bounds);
        });
    }

    private long GetEnclosingElement() {
        return Util.guard(() -> {
            return (long) Optional.ofNullable(impl.GetEnclosingElement())
            .map(element -> {
                // since GetEnclosingElement navigates upwards we must not create a virtual accessible for a fx element
                ProxyAccessible acc = ProxyAccessibleRegistry.getInstance().findFXAccessible(element);
                if (acc == null) {
                    acc = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, element);
                }
                return acc;
            })
            .map(ProxyAccessible::getNativeAccessible)
            .orElse(0L);
        });
    }

    private String GetText(int maxLength) {
        return Util.guard(() -> {
            return impl.GetText(maxLength);
        });
    }

    private int Move(int unit, final int requestedCount) {
        return Util.guard(() -> {
            return impl.Move(TextUnit.fromNativeValue(unit).get(), requestedCount);
        });
    }

    private int MoveEndpointByUnit(int endpoint, int unit, final int requestedCount) {
        return Util.guard(() -> {
            return impl.MoveEndpointByUnit(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), TextUnit.fromNativeValue(unit).get(), requestedCount);
        });
    }

    private void MoveEndpointByRange(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
        Util.guard(() -> {
            impl.MoveEndpointByRange(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.impl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
        });
    }

    private void Select() {
        Util.guard(() -> {
            impl.Select();
        });
    }

    private void AddToSelection() {
        Util.guard(() -> {
            impl.AddToSelection();
        });
    }

    private void RemoveFromSelection() {
        Util.guard(() -> {
            impl.RemoveFromSelection();
        });
    }

    private void ScrollIntoView(boolean alignToTop) {
        Util.guard(() -> {
            impl.ScrollIntoView(alignToTop);
        });
    }

    private long[] GetChildren() {
        return Util.guard(() -> {
            IUIAElement[] childElements = impl.GetChildren();
            return Arrays.stream(childElements)
            .map(element -> ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, element))
            .mapToLong(ProxyAccessible::getNativeAccessible)
            .toArray();
        });
    }

    /***********************************************/
    /*            ITextRangeProvider2              */
    /***********************************************/
    private void ShowContextMenu() {
        Util.guard(() -> {
            // TODO disable ITextRangeProvider2 interface in IUnknown casting if not available
            if (impl instanceof ITextRangeProvider2) {
                ((ITextRangeProvider2) impl).ShowContextMenu();
            }
        });
    }

}
