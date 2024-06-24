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

import java.lang.reflect.Method;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.View;
import com.sun.glass.ui.uia.glass.WinAccessible;
import com.sun.glass.ui.uia.glass.WinVariant;
import com.sun.glass.ui.uia.provider.NativeIAnnotationProvider;
import com.sun.glass.ui.uia.provider.NativeIItemContainerProvider;
import com.sun.glass.ui.uia.provider.NativeIDockProvider;
import com.sun.glass.ui.uia.provider.NativeIExpandCollapseProvider;
import com.sun.glass.ui.uia.provider.NativeIGridItemProvider;
import com.sun.glass.ui.uia.provider.NativeIGridProvider;
import com.sun.glass.ui.uia.provider.NativeIInvokeProvider;
import com.sun.glass.ui.uia.provider.NativeIRangeValueProvider;
import com.sun.glass.ui.uia.provider.NativeIScrollItemProvider;
import com.sun.glass.ui.uia.provider.NativeIScrollProvider;
import com.sun.glass.ui.uia.provider.NativeISelectionItemProvider;
import com.sun.glass.ui.uia.provider.NativeISelectionProvider;
import com.sun.glass.ui.uia.provider.NativeISelectionProvider2;
import com.sun.glass.ui.uia.provider.NativeITableItemProvider;
import com.sun.glass.ui.uia.provider.NativeITableProvider;
import com.sun.glass.ui.uia.provider.NativeITextChildProvider;
import com.sun.glass.ui.uia.provider.NativeITextProvider;
import com.sun.glass.ui.uia.provider.NativeITextProvider2;
import com.sun.glass.ui.uia.provider.NativeITextEditProvider;
import com.sun.glass.ui.uia.provider.NativeIToggleProvider;
import com.sun.glass.ui.uia.provider.NativeITransformProvider;
import com.sun.glass.ui.uia.provider.NativeITransformProvider2;
import com.sun.glass.ui.uia.provider.NativeIValueProvider;
import com.sun.glass.ui.uia.provider.NativeIWindowProvider;
import com.sun.glass.ui.uia.provider.NativeISynchronizedInputProvider;
import com.sun.glass.ui.uia.provider.NativeIVirtualizedItemProvider;
import com.sun.glass.ui.uia.provider.NativeIMultipleViewProvider;
import com.sun.glass.ui.uia.provider.NativeIStylesProvider;
import com.sun.glass.ui.uia.provider.UIAElementAdapter;
import com.sun.javafx.tk.quantum.QuantumToolkit;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.StandardPatternIds;

import com.sun.glass.ui.uia.ProxyAccessible;


import static com.sun.glass.ui.uia.Util.*;

@SuppressWarnings("restriction")
public class ProxyAccessible extends Accessible {

  private static final Logger LOG = Logger.create(ProxyAccessible.class);

    private static int next = 0;
    private final int num;

    public int getNum() {
        return num;
    }

    public ProxyAccessible getFocusDelegate() {
        if (uiaElement != null && uiaElement instanceof IUIAVirtualRootElement) {
            IUIAVirtualRootElement rootElement = (IUIAVirtualRootElement) uiaElement;
            IUIAElement focusDelegate = rootElement.getFocus();
            return ProxyAccessibleRegistry.getInstance().getVirtualAccessible(this, focusDelegate);
        }
        return null;
    }

    public static void requireLibrary() {

    }
    
    private static native void _initIDs();
    
    static {
      NativeLibrary.require();
        _initIDs();

        InstanceTracker.require();
    }

    private long peer;
    private IUIAElement uiaElement;

    private UIAElementAdapter uiaElementAdapter;

    WinAccessible glass;
    WinAccessible glassRoot;

    public StackTraceElement[] src;

    private void saveCreationStack() {
        src = Thread.currentThread().getStackTrace();
    }
    
    /*package*/ ProxyAccessible() {
      this.num = next++;
      
      this.peer = _createProxyAccessible();
      if (this.peer == 0L) {
        throw new RuntimeException("could not create platform accessible");
      }
      
      ProxyAccessibleRegistry.getInstance().registerNative(this, peer);
      
      glass = new WinAccessible(this);
      
      //LOG.debug(this, () -> "created. (glass)");

        saveCreationStack();
    }

    // this is the *virtual* Accessible
    ProxyAccessible(ProxyAccessible context, IUIAElement uiaElement) {

        if (uiaElement == null) {
            LOG.debug(this, () -> "virtual ProxyAccessible cannot be created without an uiaElement");
            throw new NullPointerException("uiaElement is null!");
        }

        this.num = next++;
        if (context.glass != null) {
            glassRoot = context.glass;
        } else {
            glassRoot = context.glassRoot;
        }
        
        this.peer = _createProxyAccessible();
        if (this.peer == 0L) {
            throw new RuntimeException("could not create platform accessible");
        }


        ProxyAccessibleRegistry.getInstance().registerNative(this, peer);

        this.uiaElement = uiaElement;
        // no glass instance available
        if (uiaElement != null) {
            initializeVirtualElement(uiaElement);
        }

        //LOG.debug(this, () -> "created. (virtual)");

        saveCreationStack();
    }

    public float[] getPlatformBounds(float x, float y, float w, float h) {
        Scene scene = (Scene) getGlassAccessibleRoot().getAttribute(AccessibleAttribute.SCENE);
        return PlatformBoundsUtil.convertToPlatformBounds(scene, x, y, w, h);
    }

    public Bounds getPlatformBounds(Bounds bounds) {
        float[] b = this.getPlatformBounds((float) bounds.getMinX(), (float) bounds.getMinY(), (float) bounds.getWidth(), (float) bounds.getHeight());
        return new BoundingBox(b[0], b[1], b[2], b[3]);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(Class<T> type) {
        if (type == IUIAElement.UIAElementContext.class) {
            return (T) uiaElementAdapter.getContext();
        }
        return uiaElementAdapter.getProviderRegistry().getContext(type);
    }

    private <T> T getNativeProvider(Class<T> nativeType) {
        return uiaElementAdapter == null ? null : uiaElementAdapter.getProviderRegistry().getNativeInstance(nativeType);
    }


    public IUIAElement getUIAElement() {
        return uiaElement;
    }

    @Override
    public void dispose() {
        //LOG.debug(this, () -> "DISPOSE " + this);

        super.dispose();

        if (peer != 0L) {
            _destroyProxyAccessible(peer);
            peer = 0L;
        }

        if (glass != null) glass.dispose();

        ProxyAccessibleRegistry.getInstance().unregister(this);

        if (uiaElement instanceof IUIAVirtualRootElement) {
            IUIAVirtualRootElement root = (IUIAVirtualRootElement) uiaElement;
            for (IUIAElement child : root.getChildren()) {
                ProxyAccessible virtual = ProxyAccessibleRegistry.getInstance().findVirtualAccessible(child);
                if (virtual != null) {
                    virtual.dispose();
                }
            }
        } else if (uiaElement instanceof IUIAVirtualElement) {
            IUIAVirtualElement root = (IUIAVirtualElement) uiaElement;
            for (IUIAElement child : root.getChildren()) {
                ProxyAccessible virtual = ProxyAccessibleRegistry.getInstance().findVirtualAccessible(child);
                if (virtual != null) {
                    virtual.dispose();
                }
            }
        }

    }

    @Override
    public Object getAttribute(AccessibleAttribute attribute, Object... parameters) {
        return glass.getAttribute(attribute, parameters);
    }

    @Override
    public void executeAction(AccessibleAction action, Object... parameters) {
        glass.executeAction(action, parameters);
    }

    @Override
    public View getView() {
        return glass.getView();
    }

    @Override
    public void setView(View view) {
        glass.setView(view);
    }

    @Override
    public EventHandler getEventHandler() {
        return glass.getEventHandler();
    }

    @Override
    public void setEventHandler(EventHandler eventHandler) {
        glass.setEventHandler(eventHandler);

        connect();

        if (this.uiaElement != null) {
        //   LOG.debug(this, () -> "connected to " + this.uiaElement);
        } else {
        //   LOG.debug(this, () -> "connected to " + eventHandler);
        }
    }

    private void listen(IUIAElement node) {
        if (node == null) return;
        // TODO
        //node.controlTypeProperty().addListener((obs, ol, ne) -> {
        //    UiaRaiseAutomationPropertyChangedEvent(peer, StandardPropertyIds.UIA_ControlTypePropertyId.getNativeValue(), Convert.convertEnum(ol), Convert.convertEnum(ne));
        //});
    }

    private void initialize(IUIAElement element) {
        uiaElementAdapter = new UIAElementAdapter(this, element);
    }

    private void initializeJavaFXElement(IUIAElement element) {
        initialize(element);

        ProxyAccessibleRegistry.getInstance().registerFXAccessible(element, this);
    }

    private void initializeVirtualElement(IUIAElement element) {
        initialize(element);
    }

    private void connect() {
        if (uiaElement == null) {
            uiaElement = getProvider(IUIAElement.class);
            //LOG.debug(this, () -> "Got uiaNode: " + uiaElement);
            if (uiaElement != null) {
                initializeJavaFXElement(uiaElement);
                //LOG.debug(this, () -> "connected to " + uiaElement + ".");
            }
        }
    }

    private final AccessControlContext getAccessControlContext() {
		AccessControlContext acc = null;
		try {
			acc = getEventHandler().getAccessControlContext();
		} catch (Exception e) {
			/* The node was already removed from the scene */
		}
		return acc;
	}

    private class GetProvider implements PrivilegedAction<Object> {
		Class<?> providerType;

		@Override
		public Object run() {
			Object result = getEventHandler().getAttribute(AccessibleAttribute.TEXT, "getProvider", providerType);
			if (result != null) {
				
				if (result instanceof String) {
					return null;
				}
				
				try {
					providerType.cast(result);
				} catch (Exception e) {
                    LOG.error(ProxyAccessible.this, () -> "The expected provider type" + " is " + providerType.getSimpleName() + " but found "
                    + result.getClass().getSimpleName(), e);
					return null; // Fail no exception
				}
			}
			return result;
		}
	}

    private class GetProviderFromNode implements PrivilegedAction<Object> {
        Node node;
		Class<?> providerType;

		@Override
		public Object run() {
            Object result = node.queryAccessibleAttribute(AccessibleAttribute.TEXT, "getProvider", providerType);
			if (result != null) {
				
				if (result instanceof String) {
					return null;
				}
				
				try {
					providerType.cast(result);
				} catch (Exception e) {
                    LOG.error(ProxyAccessible.this, () -> "The expected provider type" + " is " + providerType.getSimpleName() + " but found "
                            + result.getClass().getSimpleName(), e);
					return null; // Fail no exception
				}
			}
			return result;
		}
	}

	GetProvider getProvider = new GetProvider();
    GetProviderFromNode getProviderFromNode = new GetProviderFromNode();

	@SuppressWarnings("unchecked")
	public <T> T getProvider(Class<T> providerType) {
		AccessControlContext acc = getAccessControlContext();
		if (acc == null)
			return null;
		return (T) QuantumToolkit.runWithoutRenderLock(() -> {
			getProvider.providerType = providerType;
			return (T) AccessController.doPrivileged(getProvider, acc);
		});
	}

    @SuppressWarnings("unchecked")
	public <T> T getProviderFromNode(Node node, Class<T> providerType) {
		AccessControlContext acc = getAccessControlContext();
		if (acc == null)
			return null;
		return (T) QuantumToolkit.runWithoutRenderLock(() -> {
            getProviderFromNode.node = node;
			getProviderFromNode.providerType = providerType;
			return (T) AccessController.doPrivileged(getProviderFromNode, acc);
		});
	}

    @Override
    public String toString() {
        // String glassStr = "" + glass;
        // if (glass != null) {
        //     View view = glass.getView();
        //     glassStr += " view: " + view;
        // }
        // return "ProxyAccessible " + num + " [" + getNativeAccessible()+ "]" + " [" + glassStr  + ", " + glassRoot + ", " + uiaElement + "]";

        return "ProxyAccessible " + num + " [" + getNativeAccessible()+ "] ("+uiaElement+")";
    }

    public WinAccessible getGlassAccessible() {
        return glass;
    }

    public WinAccessible getGlassAccessibleRoot() {
        if (glass != null) {
            return glass;
        }
        return glassRoot;
    }

    /* Creates a GlassAccessible linked to the caller (GlobalRef) */
    public native long _createProxyAccessible();

    /* Releases the GlassAccessible and deletes the GlobalRef */
    public native void _destroyProxyAccessible(long accessible);

    public static native long UiaRaiseAutomationEvent(long pProvider, int id);
    public static native long UiaRaiseAutomationPropertyChangedEvent(long pProvider, int id, WinVariant oldV, WinVariant newV);
    public static native boolean UiaClientsAreListening();

    @Override
    public long getNativeAccessible() {
        return peer;
    }

    @Override
    public void sendNotification(AccessibleAttribute notification) {
        glass.sendNotification(notification);
    }

    private void checkGlass() {
        if (glass == null) {
            LOG.error(this, () -> "Glass is null in " + this);
            new NullPointerException("glass is null").printStackTrace();
        }
    }

    /***********************************************/
    /*        IRawElementProviderSimple            */
    /***********************************************/
    private long IRawElementProviderSimple_GetPatternProvider(int patternId) {
        return callElementLongW(el -> el.GetPatternProvider(patternId), glass -> glass.GetPatternProvider(patternId));
    }
    private long IRawElementProviderSimple_get_HostRawElementProvider() {
        return callElementLongW(UIAElementAdapter::get_HostRawElementProvider, WinAccessible::get_HostRawElementProvider);
    }
    private WinVariant IRawElementProviderSimple_GetPropertyValue(int propertyId) {
        return callElementObjW(el -> el.GetPropertyValue(propertyId), glass -> glass.GetPropertyValue(propertyId));
    }

    /***********************************************/
    /*       IRawElementProviderFragment           */
    /***********************************************/
    private float[] IRawElementProviderFragment_get_BoundingRectangle() {
        return callElementFloatArrayW(UIAElementAdapter::get_BoundingRectangle, WinAccessible::get_BoundingRectangle);
    }
    private long IRawElementProviderFragment_get_FragmentRoot() {
        return callElementLongW(UIAElementAdapter::get_FragmentRoot, WinAccessible::get_FragmentRoot);
    }
    private long[] IRawElementProviderFragment_GetEmbeddedFragmentRoots() {
        return callElementLongArrayW(UIAElementAdapter::GetEmbeddedFragmentRoots, WinAccessible::GetEmbeddedFragmentRoots);
    }
    private int[] IRawElementProviderFragment_GetRuntimeId() {
        return callElementIntArrayW(UIAElementAdapter::GetRuntimeId, WinAccessible::GetRuntimeId);
    }
    public long IRawElementProviderFragment_Navigate(int direction) {
        return callElementLongW(el -> el.Navigate(direction), glass -> glass.Navigate(direction));
    }
    private void IRawElementProviderFragment_SetFocus() {
        callElementW(UIAElementAdapter::SetFocus, WinAccessible::SetFocus);
    }

    private static ProxyAccessible getNodeAccessible(Node node) {
        // TODO maybe there is a way without using reflection
        try {
            Method getter = Node.class.getDeclaredMethod("getAccessible");
            getter.setAccessible(true);
            ProxyAccessible accessible = (ProxyAccessible) getter.invoke(node);
            return accessible;
        } catch (Exception e) {
            LOG.error(ProxyAccessible.class, () -> "Failed to get Accessible! " + node, e);
            throw new RuntimeException("Failed to get Accessible! " + node , e);
        }
    }

    /***********************************************/
    /*     IRawElementProviderFragmentRoot         */
    /***********************************************/
    private long IRawElementProviderFragmentRoot_ElementProviderFromPoint(double x, double y) {
        return guardLong(() -> {
            // glass should never be null, since this query is only executed at Scene Level
            Node node = glass.ElementProviderFromPointAsNode(x, y);
            ProxyAccessible accessible = getNodeAccessible(node);
            IUIAElement element = accessible.getUIAElement();
            if (element instanceof IUIAVirtualRootElement) {
                // we deal with a virtual root - so we need to pick into the virtual structure...
                IUIAVirtualRootElement virtualRoot = (IUIAVirtualRootElement) element;
                IUIAElement pickedChild = virtualRoot.getChildFromPoint(new Point2D(x, y));
                if (pickedChild != null && pickedChild != virtualRoot) {
                    // now we have the IUIAElement, but it may not have been initialized yet
                    // since all children of a virtual root have the virtual root itself as glassRoot this should work
                    ProxyAccessible pickedAccessible = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, pickedChild);
                    return pickedAccessible.getNativeAccessible();
                } else {
                    // if we pick null, we return the root itself
                    return getNativeAccessible(node);
                }
            } else {
                // we return the javafx node
                return getNativeAccessible(node);
            }
        });
    }
    private long IRawElementProviderFragmentRoot_GetFocus() {
        // Our only root is the scene itself, so GetFocus needs to be answered by the scene
        return guardLong(() -> {
            checkGlass();
            System.err.println("FragmentRoot_GetFocus()");
            return glass.GetFocus();
        });
    }
    /***********************************************/
    /*     IRawElementProviderAdviseEvents         */
    /***********************************************/
    private void IRawElementProviderAdviseEvents_AdviseEventAdded(int eventId, long propertyIDs) {
        guardVoid(() -> {
            checkGlass();
            glass.AdviseEventAdded(eventId, propertyIDs);
        });
    }
    private void IRawElementProviderAdviseEvents_AdviseEventRemoved(int eventId, long propertyIDs) {
        guardVoid(() -> {
            checkGlass();
            glass.AdviseEventRemoved(eventId, propertyIDs);
        });
    }

    /***********************************************/
    /*             IInvokeProvider                 */
    /***********************************************/
    private void IInvokeProvider_Invoke() {
        callVoidProviderW(NativeIInvokeProvider.class, NativeIInvokeProvider::Invoke, WinAccessible::Invoke);
    }
    /***********************************************/
    /*           ISelectionProvider                */
    /***********************************************/
    private long[] ISelectionProvider_GetSelection() {
        return callProviderLongArrayW(NativeISelectionProvider.class, NativeISelectionProvider::GetSelection, WinAccessible::GetSelection);
    }
    private boolean ISelectionProvider_get_CanSelectMultiple() {
        return callProviderBooleanW(NativeISelectionProvider.class, NativeISelectionProvider::get_CanSelectMultiple, WinAccessible::get_CanSelectMultiple);
    }
    private boolean ISelectionProvider_get_IsSelectionRequired() {
        return callProviderBooleanW(NativeISelectionProvider.class, NativeISelectionProvider::get_IsSelectionRequired, WinAccessible::get_IsSelectionRequired);
    }
    /***********************************************/
    /*          ISelectionItemProvider             */
    /***********************************************/
    private void ISelectionItemProvider_Select() {
        callVoidProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::Select, WinAccessible::Select);
    }
    private void ISelectionItemProvider_AddToSelection() {
        callVoidProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::AddToSelection, WinAccessible::AddToSelection);
    }
    private void ISelectionItemProvider_RemoveFromSelection() {
        callVoidProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::RemoveFromSelection, WinAccessible::RemoveFromSelection);
    }
    private boolean ISelectionItemProvider_get_IsSelected() {
        return callProviderBooleanW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::get_IsSelected, WinAccessible::get_IsSelected);
    }
    private long ISelectionItemProvider_get_SelectionContainer() {
        return callProviderLongW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::get_SelectionContainer, WinAccessible::get_SelectionContainer);
    }
    /***********************************************/
    /*           IRangeValueProvider               */
    /***********************************************/
    private void IRangeValueProvider_SetValue(double val) {
        callVoidProviderW(NativeIRangeValueProvider.class, provider -> provider.SetValue(val), win -> win.SetValue(val));
    }
    private double IRangeValueProvider_get_Value() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Value, WinAccessible::get_Value);
    }
    /*  Note that this method is called by the IValueProvider also. */
    private boolean IRangeValueProvider_get_IsReadOnly() {
        return guardBoolean(() -> {
            if (uiaElementAdapter != null) {
                if (uiaElementAdapter.getProviderRegistry().isProviderAvailable(StandardPatternIds.UIA_RangeValuePatternId.getNativeValue())) {
                    return callProviderBoolean(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_IsReadOnly);
                }
                if (uiaElementAdapter.getProviderRegistry().isProviderAvailable(StandardPatternIds.UIA_ValuePatternId.getNativeValue())) {
                    return callProviderBoolean(NativeIValueProvider.class, NativeIValueProvider::get_IsReadOnly);
                }
            }
            if (glass != null) {
                return glass.get_IsReadOnly();
            }
            return false;
        });
    }
    private double IRangeValueProvider_get_Maximum() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Maximum, WinAccessible::get_Maximum);
    }
    private double IRangeValueProvider_get_Minimum() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Minimum, WinAccessible::get_Minimum);
    }
    private double IRangeValueProvider_get_LargeChange() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_LargeChange, WinAccessible::get_LargeChange);
    }
    private double IRangeValueProvider_get_SmallChange() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_SmallChange, WinAccessible::get_SmallChange);
    }
    /***********************************************/
    /*             IValueProvider                  */
    /***********************************************/
    private void IValueProvider_SetValueString(String val) {
        callVoidProviderW(NativeIValueProvider.class, provider -> provider.SetValue(val), win -> win.SetValueString(val));
    }
    private String IValueProvider_get_ValueString() {
        return callProviderW(NativeIValueProvider.class, NativeIValueProvider::get_Value, WinAccessible::get_ValueString);
    }
    /***********************************************/
    /*              ITextProvider                  */
    /***********************************************/
    private long[] ITextProvider_GetVisibleRanges() {
        return InstanceTracker.withReason("ITextProvider_GetVisibleRanges", () ->
            callProviderLongArrayW(NativeITextProvider.class, NativeITextProvider::GetVisibleRanges, WinAccessible::GetVisibleRanges)
        );
    }
    private long[] ITextProvider_GetSelection() {
        return InstanceTracker.withReason("ITextProvider_GetSelection", () ->
            callProviderLongArrayW(NativeITextProvider.class, NativeITextProvider::GetSelection, WinAccessible::GetSelection)
        );
    }
    private long ITextProvider_RangeFromChild(long childElement) {
        return InstanceTracker.withReason("ITextProvider_RangeFromChild", () -> 
            callProviderLongW(NativeITextProvider.class, p -> p.RangeFromChild(childElement), g -> g.RangeFromChild(childElement))
        );
    }
    private long ITextProvider_RangeFromPoint(double x, double y) {
        return InstanceTracker.withReason("ITextProvider_RangeFromPoint", () ->
            callProviderLongW(NativeITextProvider.class, p -> p.RangeFromPoint(x, y), g -> g.RangeFromPoint(x, y))
        );
    }
    private long ITextProvider_get_DocumentRange() {
        return InstanceTracker.withReason("ITextProvider_get_DocumentRange", () ->
            callProviderLongW(NativeITextProvider.class, NativeITextProvider::get_DocumentRange, WinAccessible::get_DocumentRange)
        );
    }
    private int ITextProvider_get_SupportedTextSelection() {
        return callProviderIntW(NativeITextProvider.class, NativeITextProvider::get_SupportedTextSelection, WinAccessible::get_SupportedTextSelection);
    }
     /***********************************************/
    /*             IGridProvider                   */
    /***********************************************/
    private int IGridProvider_get_ColumnCount() {
        return callProviderIntW(NativeIGridProvider.class, NativeIGridProvider::get_ColumnCount, WinAccessible::get_ColumnCount);
    }
    private int IGridProvider_get_RowCount() {
        return callProviderIntW(NativeIGridProvider.class, NativeIGridProvider::get_RowCount, WinAccessible::get_RowCount);
    }
    private long IGridProvider_GetItem(int row, int column) {
        return callProviderLongW(NativeIGridProvider.class, p -> p.GetItem(row, column), g -> g.GetItem(row, column));
    }
    /***********************************************/
    /*             IGridItemProvider               */
    /***********************************************/
    private int IGridItemProvider_get_Column() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Column, WinAccessible::get_Column);
    }
    private int IGridItemProvider_get_ColumnSpan() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ColumnSpan, WinAccessible::get_ColumnSpan);
    }
    private long IGridItemProvider_get_ContainingGrid() {
        return callProviderLongW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ContainingGrid, WinAccessible::get_ContainingGrid);
    }
    private int IGridItemProvider_get_Row() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Row, WinAccessible::get_Row);
    }
    private int IGridItemProvider_get_RowSpan() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_RowSpan, WinAccessible::get_RowSpan);
    }
    /***********************************************/
    /*               ITableProvider                */
    /***********************************************/
    private long[] ITableProvider_GetColumnHeaders() {
        return callProviderLongArrayW(NativeITableProvider.class, NativeITableProvider::GetColumnHeaders, WinAccessible::GetColumnHeaders);
    }
    private long[] ITableProvider_GetRowHeaders() {
        return callProviderLongArrayW(NativeITableProvider.class, NativeITableProvider::GetRowHeaders, WinAccessible::GetRowHeaders);
    }
    private int ITableProvider_get_RowOrColumnMajor() {
        return callProviderIntW(NativeITableProvider.class, NativeITableProvider::get_RowOrColumnMajor, WinAccessible::get_RowOrColumnMajor);
    }
    /***********************************************/
    /*             ITableItemProvider              */
    /***********************************************/
    private long[] ITableItemProvider_GetColumnHeaderItems() {
        return callProviderLongArrayW(NativeITableItemProvider.class, NativeITableItemProvider::GetColumnHeaderItems, WinAccessible::GetColumnHeaderItems);
    }
    private long[] ITableItemProvider_GetRowHeaderItems() {
        return callProviderLongArrayW(NativeITableItemProvider.class, NativeITableItemProvider::GetRowHeaderItems, WinAccessible::GetRowHeaderItems);
    }
    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    private void IToggleProvider_Toggle() {
        callVoidProviderW(NativeIToggleProvider.class, NativeIToggleProvider::Toggle, WinAccessible::Toggle);
    }
    private int IToggleProvider_get_ToggleState() {
        return callProviderIntW(NativeIToggleProvider.class, NativeIToggleProvider::get_ToggleState, WinAccessible::get_ToggleState);
    }
    /***********************************************/
    /*             IExpandCollapseProvider         */
    /***********************************************/
    private void IExpandCollapseProvider_Collapse() {
        callVoidProviderW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::Collapse, WinAccessible::Collapse);
    }
    private void IExpandCollapseProvider_Expand() {
        callVoidProviderW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::Expand, WinAccessible::Expand);
    }
    private int IExpandCollapseProvider_get_ExpandCollapseState() {
        return callProviderIntW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::get_ExpandCollapseState, WinAccessible::get_ExpandCollapseState);
    }
    /***********************************************/
    /*             ITransformProvider              */
    /***********************************************/
    private boolean ITransformProvider_get_CanMove() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanMove, WinAccessible::get_CanMove);
    }
    private boolean ITransformProvider_get_CanResize() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanResize, WinAccessible::get_CanResize);
    }
    private boolean ITransformProvider_get_CanRotate() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanRotate, WinAccessible::get_CanRotate);
    }
    private void ITransformProvider_Move(double x, double y) {
        callVoidProviderW(NativeITransformProvider.class, provider -> provider.Move(x, y), glass -> glass.Move(x, y));
    }
    private void ITransformProvider_Resize(double width, double height) {
        callVoidProviderW(NativeITransformProvider.class, provider -> provider.Resize(width, height), glass -> glass.Resize(width, height));
    }
    private void ITransformProvider_Rotate(double degrees) {
        callVoidProviderW(NativeITransformProvider.class, provider -> provider.Rotate(degrees), glass -> glass.Rotate(degrees));
    }

    // ITransformProvider2
    private boolean ITransformProvider2_get_CanZoom() {
        return callProviderBoolean(NativeITransformProvider2.class, NativeITransformProvider2::get_CanZoom);
    }
    private double ITransformProvider2_get_ZoomLevel() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomLevel);
    }
    private double ITransformProvider2_get_ZoomMinimum() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomMinimum);
    }
    private double ITransformProvider2_get_ZoomMaximum() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomMaximum);
    }
    private void ITransformProvider2_Zoom(double zoom) {
        callVoidProvider(NativeITransformProvider2.class, provider -> provider.Zoom(zoom));
    }
    private void ITransformProvider2_ZoomByUnit(int zoomUnit) {
        callVoidProvider(NativeITransformProvider2.class, provider -> provider.ZoomByUnit(zoomUnit));
    }

    /***********************************************/
    /*             IScrollProvider                 */
    /***********************************************/
    private void IScrollProvider_Scroll(int horizontalAmount, int verticalAmount) {
        callVoidProviderW(NativeIScrollProvider.class, p -> p.Scroll(horizontalAmount, verticalAmount), g -> g.Scroll(horizontalAmount, verticalAmount));
    }
    private void IScrollProvider_SetScrollPercent(double horizontalPercent, double verticalPercent) {
        callVoidProviderW(NativeIScrollProvider.class, p -> p.SetScrollPercent(horizontalPercent, verticalPercent), g -> g.SetScrollPercent(horizontalPercent, verticalPercent));
    }
    private boolean IScrollProvider_get_HorizontallyScrollable() {
        return callProviderBooleanW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontallyScrollable, WinAccessible::get_HorizontallyScrollable);
    }
    private double IScrollProvider_get_HorizontalScrollPercent() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalScrollPercent, WinAccessible::get_HorizontalScrollPercent);
    }
    private double IScrollProvider_get_HorizontalViewSize() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalViewSize, WinAccessible::get_HorizontalViewSize);
    }
    private boolean IScrollProvider_get_VerticallyScrollable() {
        return callProviderBooleanW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticallyScrollable, WinAccessible::get_VerticallyScrollable);
    }
    private double IScrollProvider_get_VerticalScrollPercent() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalScrollPercent, WinAccessible::get_VerticalScrollPercent);
    }
    private double IScrollProvider_get_VerticalViewSize() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalViewSize, WinAccessible::get_VerticalViewSize);
    }
    /***********************************************/
    /*             IScrollItemProvider             */
    /***********************************************/
    private void IScrollItemProvider_ScrollIntoView() {
        callVoidProviderW(NativeIScrollItemProvider.class, NativeIScrollItemProvider::ScrollIntoView, WinAccessible::ScrollIntoView);
    }


    // IWindowProvider
    private void WindowProvider_Close() {
        callVoidProvider(NativeIWindowProvider.class, NativeIWindowProvider::Close);
    }
    private boolean WindowProvider_get_CanMaximize() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_CanMaximize);
    }
    private boolean WindowProvider_get_CanMinimize() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_CanMinimize);
    }
    private boolean WindowProvider_get_IsModal() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_IsModal);
    }
    private boolean WindowProvider_get_IsTopmost() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_IsTopmost);
    }
    private int WindowProvider_get_WindowInteractionState() {
        return callProviderInt(NativeIWindowProvider.class, NativeIWindowProvider::get_WindowInteractionState);
    }
    private int WindowProvider_get_WindowVisualState() {
        return callProviderInt(NativeIWindowProvider.class, NativeIWindowProvider::get_WindowVisualState);
    }
    private void WindowProvider_SetVisualState(int state) {
        callVoidProvider(NativeIWindowProvider.class, provider -> provider.SetVisualState(state));
    }
    private boolean WindowProvider_WaitForInputIdle(int milliseconds) {
        return callProviderBoolean(NativeIWindowProvider.class, provider -> provider.WaitForInputIdle(milliseconds));
    }

    
    // IDockProvider
    private int     DockProvider_get_DockPosition() { 
        return callProviderInt(NativeIDockProvider.class, NativeIDockProvider::get_DockPosition);
    }
    private void    DockProvider_SetDockPosition(int dockPosition) {
        callVoidProvider(NativeIDockProvider.class, provider -> provider.SetDockPosition(dockPosition));
    }
    // IAnnotationProvider
    private int     IAnnotationProvider_get_AnnotationTypeId() { 
        return callProviderInt(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_AnnotationTypeId);
    }
    private String  IAnnotationProvider_get_AnnotationTypeName()  {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_AnnotationTypeName);
    }
    private String  IAnnotationProvider_get_Author() {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_Author);
    }
    private String  IAnnotationProvider_get_DateTime() {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_DateTime);
    }
    private long    IAnnotationProvider_get_Target() {
        return callProviderLong(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_Target);
    }
    // IDragProvider
    private String      IDragProvider_get_DropEffect() { return ""; }
    private String[]    IDragProvider_get_DropEffects() { return new String[0]; }
    private boolean     IDragProvider_get_IsGrabbed() { return false; }
    private long[]      IDragProvider_GetGrabbedItems() { return null; }
    // IDropTargetProvider
    private String      IDropTargetProvider_get_DropTargetEffect() { return ""; }
    private String[]    IDropTargetProvider_get_DropTargetEffects() { return new String[0]; }
    // IItemContainerProvider
    private long        IItemContainerProvider_FindItemByProperty(long pStartAfter, int propertyId, long pVariant) {
        return callProviderLong(NativeIItemContainerProvider.class, provider -> provider.FindItemByProperty(pStartAfter, propertyId, pVariant));
    }
    // IMultipleViewProvider
    private int         IMultipleViewProvider_get_CurrentView() {
        return callProviderInt(NativeIMultipleViewProvider.class, NativeIMultipleViewProvider::get_CurrentView);
    }
    private int[]       IMultipleViewProvider_GetSupportedViews() {
        return callProvider(NativeIMultipleViewProvider.class, NativeIMultipleViewProvider::GetSupportedViews);
    }
    private String      IMultipleViewProvider_GetViewName(int viewId) {
        return callProvider(NativeIMultipleViewProvider.class, provider -> provider.GetViewName(viewId));
    }
    private void        IMultipleViewProvider_SetCurrentView(int viewId) {
        callVoidProvider(NativeIMultipleViewProvider.class, provider -> provider.SetCurrentView(viewId));
    }

    // IStylesProvider
    private String IStylesProvider_get_ExtendedProperties() {
        return callProvider(NativeIStylesProvider.class, NativeIStylesProvider::get_ExtendedProperties);
    }
    private int IStylesProvider_get_FillColor() {
        return callProviderInt(NativeIStylesProvider.class, NativeIStylesProvider::get_FillColor);
    }
    private int IStylesProvider_get_FillPatternColor() {
        return callProviderInt(NativeIStylesProvider.class, NativeIStylesProvider::get_FillPatternColor);
    }
    private String IStylesProvider_get_FillPatternStyle() {
        return callProvider(NativeIStylesProvider.class, NativeIStylesProvider::get_FillPatternStyle);
    }   
    private String IStylesProvider_get_Shape() {
        return callProvider(NativeIStylesProvider.class, NativeIStylesProvider::get_Shape);
    }
    private int IStylesProvider_get_StyleId() {
        return callProviderInt(NativeIStylesProvider.class, NativeIStylesProvider::get_StyleId);
    }
    private String IStylesProvider_get_StyleName() {
        return callProvider(NativeIStylesProvider.class, NativeIStylesProvider::get_StyleName);
    }

    // ITextChildProvider
    private long ITextChildProvider_get_TextContainer() {
        return callProviderLong(NativeITextChildProvider.class, NativeITextChildProvider::get_TextContainer);
    }
    private long ITextChildProvider_get_TextRange() {
        return InstanceTracker.withReason("ITextChildProvider_get_TextRange", () -> 
            callProviderLong(NativeITextChildProvider.class, NativeITextChildProvider::get_TextRange)
        );
    }

    // ITextProvider2
    public static final class NCaretRangeResult {
        public boolean isActive;
        public long range;
    }
    private NCaretRangeResult   ITextProvider2_GetCaretRange() { 
        return InstanceTracker.withReason("ITextProvider2_GetCaretRange", () -> 
            callProvider(NativeITextProvider2.class, NativeITextProvider2::GetCaretRange)
        );
    }
    private long                ITextProvider2_RangeFromAnnotation(long annotationElement) {
        return InstanceTracker.withReason("ITextProvider2_RangeFromAnnotation", () ->
            callProviderLong(NativeITextProvider2.class, prov -> prov.RangeFromAnnotation(annotationElement))
        );
    }
    // ITextEditProvider
    private long ITextEditProvider_GetActiveComposition() {
        return InstanceTracker.withReason("ITextEditProvider_GetActiveComposition", () ->
            callProviderLong(NativeITextEditProvider.class, NativeITextEditProvider::GetActiveComposition)
        );
    }
    private long ITextEditProvider_GetConversionTarget() {
        return InstanceTracker.withReason("ITextEditProvider_GetConversionTarget", () ->
            callProviderLong(NativeITextEditProvider.class, NativeITextEditProvider::GetConversionTarget)
        );
    }

    // IVirtualizedItemProvider
    private void IVirtualizedItemProvider_Realize() {
        callVoidProvider(NativeIVirtualizedItemProvider.class, NativeIVirtualizedItemProvider::Realize);
    }

    // ISynchronizedInputProvider
    private void ISynchronizedItemProvider_Cancel() {
        callVoidProvider(NativeISynchronizedInputProvider.class, NativeISynchronizedInputProvider::Cancel);
    }
    private void ISynchronizedItemProvider_StartListening(int inputType) {
        callVoidProvider(NativeISynchronizedInputProvider.class, provider -> provider.StartListening(inputType));
    }

    // ISelectionProvider2
    private int ISelectionProvider2_get_ItemCount() {
        return callProviderInt(NativeISelectionProvider2.class, NativeISelectionProvider2::get_ItemCount);
    }
    private long ISelectionProvider2_get_CurrentSelectedItem() {
        return callProviderLong(NativeISelectionProvider2.class, NativeISelectionProvider2::get_CurrentSelectedItem);
    }
    private long ISelectionProvider2_get_FirstSelectedItem() {
        return callProviderLong(NativeISelectionProvider2.class, NativeISelectionProvider2::get_FirstSelectedItem);
    }
    private long ISelectionProvider2_get_LastSelectedItem() {
        return callProviderLong(NativeISelectionProvider2.class, NativeISelectionProvider2::get_LastSelectedItem);
    }


    // Utility functions

    <R> R callElementObjW(Function<UIAElementAdapter, R> method, Function<WinAccessible, R> glassMethod) {
        return guardObject(() -> {
            if (uiaElementAdapter != null) {
                return method.apply(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.apply(glass);
            }
        });
    }

    void callElementW(Consumer<UIAElementAdapter> method, Consumer<WinAccessible> glassMethod) {
        guardVoid(() -> {
            if (uiaElementAdapter != null) {
                method.accept(uiaElementAdapter);
            } else {
                checkGlass();
                glassMethod.accept(glass);
            }
        });
    }


    long callElementLongW(ToLongFunction<UIAElementAdapter> method, ToLongFunction<WinAccessible> glassMethod) {
        return guardLong(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsLong(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsLong(glass);
            }
        });
    }

    long[] callElementLongArrayW(ToLongArrayFunction<UIAElementAdapter> method, ToLongArrayFunction<WinAccessible> glassMethod) {
        return guardLongArray(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsLongArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsLongArray(glass);
            }
        });
    }

    float[] callElementFloatArrayW(ToFloatArrayFunction<UIAElementAdapter> method, ToFloatArrayFunction<WinAccessible> glassMethod) {
        return guardFloatArray(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsFloatArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsFloatArray(glass);
            }
        });
    }

    int[] callElementIntArrayW(ToIntArrayFunction<UIAElementAdapter> method, ToIntArrayFunction<WinAccessible> glassMethod) {
        return guardIntArray(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsIntArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsIntArray(glass);
            }
        });
    }

    <P> void callVoidProviderW(Class<P> providerType, Consumer<P> method, Consumer<WinAccessible> glassMethod) {
        guardVoid(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                method.accept(provider);
            } else {
                checkGlass();
                glassMethod.accept(glass);
            }
        });
    }

    <P> void callVoidProvider(Class<P> providerType, Consumer<P> method) {
        guardVoid(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                method.accept(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            }
        });
    }

    <P, R> R callProviderW(Class<P> providerType, Function<P, R> method, Function<WinAccessible, R> glassMethod) {
        return guardObject(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.apply(provider);
            } else {
                checkGlass();
                return glassMethod.apply(glass);
            }
        });
    }
    
    <P, R> R callProvider(Class<P> providerType, Function<P, R> method) {
        return guardObject(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.apply(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            }
        });
    }

    <P> long callProviderLongW(Class<P> providerType, ToLongFunction<P> method, ToLongFunction<WinAccessible> glassMethod) {
        return guardLong(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLong(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsLong(glass);
            }
        });
    }
    <P> long callProviderLong(Class<P> providerType, ToLongFunction<P> method) {
        return guardLong(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLong(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            } 
        });
    }
   
    <P> long[] callProviderLongArrayW(Class<P> providerType, ToLongArrayFunction<P> method, ToLongArrayFunction<WinAccessible> alternative) {
        return guardLongArray(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLongArray(provider);
            } else {
                checkGlass();
                return alternative.applyAsLongArray(glass);
            }
        });
    }

    <P> int callProviderIntW(Class<P> providerType, ToIntFunction<P> method, ToIntFunction<WinAccessible> glassMethod) {
        return guardInt(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsInt(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsInt(glass);
            } 
        });
    }

    <P> int callProviderInt(Class<P> providerType, ToIntFunction<P> method) {
        return guardInt(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsInt(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            } 
        });
    }


    <P> double callProviderDoubleW(Class<P> providerType, ToDoubleFunction<P> method, ToDoubleFunction<WinAccessible> glassMethod) {
        return guardDouble(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsDouble(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsDouble(glass);
            } 
        });
    }

    <P> double callProviderDouble(Class<P> providerType, ToDoubleFunction<P> method) {
        return guardDouble(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsDouble(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            } 
        });
    }

    
    <P> boolean callProviderBooleanW(Class<P> providerType, ToBooleanFunction<P> method, ToBooleanFunction<WinAccessible> alternative) {
        return guardBoolean(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsBoolean(provider);
            } else {
                checkGlass();
                return alternative.applyAsBoolean(glass);
            } 
        });
    }

    <P> boolean callProviderBoolean(Class<P> providerType, ToBooleanFunction<P> method) {
        return guardBoolean(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsBoolean(provider);
            } else {
                throw new HResultException(HResultException.E_NOTIMPL);
            } 
        });
    }

    @FunctionalInterface
    static interface ToBooleanFunction<T> {
        boolean applyAsBoolean(T value);
    }
    @FunctionalInterface
    static interface ToLongArrayFunction<T> {
        long[] applyAsLongArray(T value);
    }
    @FunctionalInterface
    static interface ToFloatArrayFunction<T> {
        float[] applyAsFloatArray(T value);
    }
    @FunctionalInterface
    static interface ToIntArrayFunction<T> {
        int[] applyAsIntArray(T value);
    }

}
