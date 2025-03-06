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
package at.bestsolution.uia.internal;

import static at.bestsolution.uia.internal.Util.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import at.bestsolution.uia.ITextAttribute;
import at.bestsolution.uia.ITextAttributeId;
import at.bestsolution.uia.ITextAttributeSupport;
import at.bestsolution.uia.ITextRangeProvider;
import at.bestsolution.uia.ITextRangeProvider2;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.IVariantConverter;
import at.bestsolution.uia.TextAttributeValue;
import at.bestsolution.uia.TextPatternRangeEndpoint;
import at.bestsolution.uia.TextUnit;
import at.bestsolution.uia.Variant;
import at.bestsolution.uia.internal.glass.IWinTextRangeProvider;
import at.bestsolution.uia.internal.glass.WinVariant;
import at.bestsolution.uia.internal.provider.Convert;
import at.bestsolution.uia.internal.winapi.Windows;
import javafx.geometry.Bounds;

public class ProxyTextRangeProvider {

  private static final Logger LOG = LoggerFactory.create(ProxyTextRangeProvider.class);

    public static class TextRangeProviderException extends HResultException {
      String msg;
      public TextRangeProviderException(String msg) {
        super(E_INVALIDARG);
        this.msg = msg;
      }
    }

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

    private ITextRangeProvider uiaImpl;
    public IWinTextRangeProvider glassImpl;


    private static int idCount = 1;
    private int id;


    private Map<ITextAttributeId, Supplier<TextAttributeValue<Object>>> attribs = new HashMap<>();
    private Map<ITextAttributeId, IVariantConverter<Object>> converters = new HashMap<>();
    private Map<ITextAttributeId, at.bestsolution.uia.FindAttribute<Object>> findAttributes = new HashMap<>();

    @SuppressWarnings("unchecked")
    private ITextAttributeSupport support = new ITextAttributeSupport(){

        @Override
        public <T> ITextAttribute<T> addAttribute(ITextAttributeId id, Supplier<TextAttributeValue<T>> getter, IVariantConverter<T> converter, at.bestsolution.uia.FindAttribute<T> findAttribute) {

            attribs.put(id, (Supplier<TextAttributeValue<Object>>) (Supplier<?>)getter);
            converters.put(id, (IVariantConverter<Object>) converter);
            if (findAttribute != null) {
                findAttributes.put(id, (at.bestsolution.uia.FindAttribute<Object>) findAttribute);
            }
            return null;
        }

    };

    private ProxyTextRangeProvider(ProxyAccessible accessible, IWinTextRangeProvider glassImpl) {
      this.accessible = accessible;
      this.glassImpl = glassImpl;
      setOnNativeDelete(this.glassImpl::onNativeDelete);
      peer = _createTextRangeProvider(accessible.getNativeAccessible());
      id = idCount++;

      // LOG.debug(this, () -> "created. (glass) acc=" + accessible);
    }

    private ProxyTextRangeProvider(ProxyAccessible accessible, ITextRangeProvider uiaImpl) {
        if (uiaImpl == null) {
            LOG.debug(this, () -> "ProxyTextRangeProvider cannot be created without an ITextRangeProvider");
            throw new NullPointerException("uiaImpl is null!");
        }

        this.accessible = accessible;
        this.uiaImpl = uiaImpl;
        peer = _createTextRangeProvider(accessible.getNativeAccessible());
        id = idCount++;

        // LOG.debug(this, () -> "created. (uia) acc=" + accessible);

        uiaImpl.initialize(support);
    }

    @Override
    public String toString() {
      return "ProxyTextRangeProvider " + id + " [" + getNativeProvider() + "]";
    }

    public static ProxyTextRangeProvider wrap(ProxyAccessible accessible, ITextRangeProvider provider) {
      if (provider == null) return null;
      return new ProxyTextRangeProvider(accessible, provider);
    }

    public static ProxyTextRangeProvider wrap(ProxyAccessible accessible, IWinTextRangeProvider provider) {
      if (provider == null) return null;
      return new ProxyTextRangeProvider(accessible, provider);
    }

    public static long wrapNative(ProxyAccessible accessible, ITextRangeProvider provider) {
      if (provider == null) return 0L;
      return wrap(accessible, provider).getNativeProvider();
    }

    public static long wrapNative(ProxyAccessible accessible, IWinTextRangeProvider provider) {
      if (provider == null) return 0L;
      return wrap(accessible, provider).getNativeProvider();
    }

    public boolean isUIA() {
      return uiaImpl != null;
    }

    public boolean isGlass() {
      return glassImpl != null;
    }

    public long getNativeProvider() {
        return peer;
    }

    public void dispose() {
        _destroyTextRangeProvider(peer);
        peer = 0L;
    }

    private void onNativeDelete() {
    //  LOG.debug(this, () -> "TextRange was deleted by refcount.");
      if (this.nativeDeleteCallback != null) {
        this.nativeDeleteCallback.run();;
        this.nativeDeleteCallback = null;
      }
    }

    private Runnable nativeDeleteCallback;

    public void setOnNativeDelete(Runnable cb) {
      this.nativeDeleteCallback = cb;
    }


    /***********************************************/
    /*            ITextRangeProvider               */
    /***********************************************/
    private long Clone() {
        return guardLong(() -> {
          return InstanceTracker.withReason("ITextRangeProvider_Clone", () -> {

            if (this.isUIA()) {
              return wrapNative(accessible, uiaImpl.Clone());
            } else if (this.isGlass()) {
              return wrapNative(accessible, glassImpl.Clone());
            } else {
              throw new TextRangeProviderException("provider missing");
            }


            /* Note: Currently Clone() natively does not call AddRef() on the returned object.
            * This mean JFX does not keep a reference to this object, consequently it does not
            * need to free it.
            */

          });

        });
    }

    private boolean Compare(ProxyTextRangeProvider range) {
        return guardBoolean(() -> {
          if (range == null) {
            return false;
          }
          if (this.isUIA() && range.isUIA()) {
            return this.uiaImpl.Compare(range.uiaImpl);
          } else if (this.isGlass() && range.isGlass()) {
            return this.glassImpl.Compare(range.glassImpl);
          } else {
            throw new TextRangeProviderException("provider mismatch");
          }
        });
    }

    private int CompareEndpoints(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
        return guardInt(() -> {
          if (this.isUIA() && targetRange.isUIA()) {
            return uiaImpl.CompareEndpoints(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.uiaImpl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
          } else if (this.isGlass() && targetRange.isGlass()) {
            return glassImpl.CompareEndpoints(endpoint, targetRange.glassImpl, targetEndpoint);
          } else {
            throw new TextRangeProviderException("provider mismatch");
          }
        });
    }

    private void ExpandToEnclosingUnit(int unit) {
        guardVoid(() -> {
          if (this.isUIA()) {
            uiaImpl.ExpandToEnclosingUnit(TextUnit.fromNativeValue(unit).get());
          } else if (this.isGlass()) {
            glassImpl.ExpandToEnclosingUnit(unit);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
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
        LOG.warning(this, () -> "Variant unknown; using VT_EMPTY", new Exception());
        return Variant.vt_empty();
    }

    private long FindAttribute(int attributeId, long variantValue, boolean backward) {
        return guardLong(() -> {
          return InstanceTracker.withReason("ITextRangeProvider_FindAttribute", () -> {

            if (this.isUIA()) {
                ITextAttributeId id = ITextAttributeId.fromNativeValue(attributeId);
                at.bestsolution.uia.FindAttribute<Object> findAttribute = findAttributes.get(id);
                if (findAttribute != null) {
                    Variant variant = convert(variantValue);
                    IVariantConverter<Object> converter = converters.get(id);
                    Object value = converter.toObject(variant);
                    ITextRangeProvider range = findAttribute.findAttribute(backward, value);
                    return wrapNative(accessible, range);
                } else {
                    return 0L;
                }
            } else if (this.isGlass()) {
              // TODO convert variant
              // but since glass FindAttribute always returns 0 we do it here
              return 0L;
              // return glassImpl.FindAttribute(attributeId, val, backward);
            } else {
              throw new TextRangeProviderException("provider missing");
            }
          });
        });
    }

    private long FindAttribute(int attributeId, WinVariant val, boolean backward) {
        return 0L;
    }

    private long FindText(String text, boolean backward, boolean ignoreCase) {
        return guardLong(() -> {
          return InstanceTracker.withReason("ITextRangeProvider_FindText", () -> {
            if (this.isUIA()) {
              ITextRangeProvider range = uiaImpl.FindText(text, backward, ignoreCase);
              return wrapNative(accessible, range);
            } else if (this.isGlass()) {
              IWinTextRangeProvider glass = glassImpl.FindText(text, backward, ignoreCase);
              return wrapNative(accessible, glass);
            } else {
              throw new TextRangeProviderException("provider missing");
            }
          });
        });
    }

    private WinVariant convert(Variant variant) {
        return variant.toWinVariant();
    }

    private WinVariant GetAttributeValue(int attributeId) {
        return guardObject(() -> {
          if (this.isUIA()) {
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
          } else if (this.isGlass()) {
            return this.glassImpl.GetAttributeValue(attributeId);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private double[] GetBoundingRectangles() {
        return guardDoubleArray(() -> {
          if (this.isUIA()) {
            Bounds[] bounds = uiaImpl.GetBoundingRectangles();
            Bounds[] platformBounds = Arrays.stream(bounds).map(b -> accessible.getPlatformBounds((b))).toArray(size -> new Bounds[size]);
            return Convert.convertBoundsArrayDouble(platformBounds);
          } else if (this.isGlass()) {
            return glassImpl.GetBoundingRectangles();
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private long GetEnclosingElement() {
        return guardLong(() -> {
          if (this.isUIA()) {
            return (long) Optional.ofNullable(uiaImpl.GetEnclosingElement())
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
          } else if (this.isGlass()) {
            return glassImpl.GetEnclosingElement();
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private String GetText(int maxLength) {
        return guardObject(() -> {
          if (this.isUIA()) {
            return uiaImpl.GetText(maxLength);
          } else if (this.isGlass()) {
            return glassImpl.GetText(maxLength);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private int Move(int unit, final int requestedCount) {
        return guardInt(() -> {
          if (this.isUIA()) {
            return uiaImpl.Move(TextUnit.fromNativeValue(unit).get(), requestedCount);
          } else if (this.isGlass()) {
            return glassImpl.Move(unit, requestedCount);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private int MoveEndpointByUnit(int endpoint, int unit, final int requestedCount) {
        return guardInt(() -> {
          if (this.isUIA()) {
            return uiaImpl.MoveEndpointByUnit(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), TextUnit.fromNativeValue(unit).get(), requestedCount);
          } else if (this.isGlass()) {
            return glassImpl.MoveEndpointByUnit(endpoint, unit, requestedCount);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private void MoveEndpointByRange(int endpoint, ProxyTextRangeProvider targetRange, int targetEndpoint) {
      //LOG.debug(() -> "MoveEndpointByRange: " + endpoint + ", " + targetRange + ", " + targetEndpoint);
        guardVoid(() -> {
          if (this.isUIA() && targetRange.isUIA()) {
            uiaImpl.MoveEndpointByRange(TextPatternRangeEndpoint.fromNativeValue(endpoint).get(), targetRange.uiaImpl, TextPatternRangeEndpoint.fromNativeValue(targetEndpoint).get());
          } else if (this.isGlass() && targetRange.isGlass()) {
            glassImpl.MoveEndpointByRange(endpoint, targetRange.glassImpl, targetEndpoint);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private void Select() {
        guardVoid(() -> {
          if (this.isUIA()) {
            uiaImpl.Select();
          } else if (this.isGlass()) {
            glassImpl.Select();
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private void AddToSelection() {
        guardVoid(() -> {
          if (this.isUIA()) {
            uiaImpl.AddToSelection();
          } else if (this.isGlass()) {
            glassImpl.AddToSelection();
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private void RemoveFromSelection() {
        guardVoid(() -> {
          if (this.isUIA()) {
            uiaImpl.RemoveFromSelection();
          } else if (this.isGlass()) {
            glassImpl.RemoveFromSelection();
          } else {
            throw new TextRangeProviderException("provider missing");
          }

        });
    }

    private void ScrollIntoView(boolean alignToTop) {
        guardVoid(() -> {
          if (this.isUIA()) {
            uiaImpl.ScrollIntoView(alignToTop);
          } else if (this.isGlass()) {
            glassImpl.ScrollIntoView(alignToTop);
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    private long[] GetChildren() {
        return guardLongArray(() -> {
          if (this.isUIA()) {
            IUIAElement[] childElements = uiaImpl.GetChildren();
            return Arrays.stream(childElements)
            .map(element -> {
                if (element instanceof IUIAVirtualRootElement) {
                    return ProxyAccessibleRegistry.getInstance().findFXAccessible(element);
                }
                else {
                    return ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, element);
                }
            })
            .mapToLong(ProxyAccessible::getNativeAccessible)
            .toArray();
          } else if (this.isGlass()) {
            return glassImpl.GetChildren();
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

    /***********************************************/
    /*            ITextRangeProvider2              */
    /***********************************************/
    private void ShowContextMenu() {
        guardVoid(() -> {
          if (this.isUIA()) {
            // TODO disable ITextRangeProvider2 interface in IUnknown casting if not available
            if (uiaImpl instanceof ITextRangeProvider2) {
                ((ITextRangeProvider2) uiaImpl).ShowContextMenu();
            }
          } else if (this.isGlass()) {
            // NOOP
          } else {
            throw new TextRangeProviderException("provider missing");
          }
        });
    }

}
