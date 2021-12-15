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
import java.util.Arrays;
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
import com.sun.glass.ui.uia.provider.UIAElementAdapter;
import com.sun.javafx.tk.quantum.QuantumToolkit;

import javafx.geometry.Point2D;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.uia.ExpandCollapseState;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.StandardPatternIds;
import javafx.uia.Variant;

@SuppressWarnings("restriction")
public class ProxyAccessible extends Accessible {

    private static int next = 0;
    private final int num;

    public int getNum() {
        return num;
    }

    private static void reportEnvironment() {
		Logger.debug(ProxyAccessible.class, () -> "Environment: ");
		Arrays.stream(new String[] {
			"java.vendor",
			"java.version", 
			"java.vm.version",
			"javafx.version",
			"javafx.runtime.version"
		}).forEach(prop -> Logger.debug(ProxyAccessible.class, () -> "\t" + prop + ": " + System.getProperty(prop)));
	}

    public static void requireLibrary() {

    }
    
    private static native void _initIDs();
    static {
        reportEnvironment();
        try {
            System.load(LibMan.uiaPlatformDll.toString());

        } catch (Exception e) {
            Logger.fatal(ProxyAccessible.class, () -> "Exception during initialization", e);
        }
        _initIDs();
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
        Logger.debug(this, () -> "ProxyAccessible" + num + " created.");

        this.peer = _createProxyAccessible();
        if (this.peer == 0L) {
            throw new RuntimeException("could not create platform accessible");
        }

        ProxyAccessibleRegistry.getInstance().registerNative(this, peer);

        glass = new WinAccessible(this);

        saveCreationStack();
    }

    // this is the *virtual* Accessible
    ProxyAccessible(ProxyAccessible context, IUIAElement uiaElement) {

        if (uiaElement == null) {
            Logger.debug(this, () -> "virtual ProxyAccessible cannot be created without an uiaElement");
            throw new NullPointerException("uiaElement is null!");
        }

        this.num = next++;
        if (context.glass != null) {
            glassRoot = context.glass;
        } else {
            glassRoot = context.glassRoot;
        }
        Logger.debug(this, () -> "ProxyAccessible" + num + " created (virtual) " + uiaElement);
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

        saveCreationStack();
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
        System.err.println("DISPOSE " + this);

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
            Logger.debug(this, () -> "Got uiaNode: " + uiaElement);
            if (uiaElement != null) {
                initializeJavaFXElement(uiaElement);
                Logger.debug(this, () -> "connected to " + uiaElement + ".");
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
                    Logger.error(ProxyAccessible.this, () -> "The expected provider type" + " is " + providerType.getSimpleName() + " but found "
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
                    Logger.error(ProxyAccessible.this, () -> "The expected provider type" + " is " + providerType.getSimpleName() + " but found "
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
        String glassStr = "" + glass;
        if (glass != null) {
            View view = glass.getView();
            glassStr += " view: " + view;
        }
        return "ProxyAccessible" + num + "[" + glassStr  + ", " + glassRoot + ", " + uiaElement + "]";
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
            Logger.error(this, () -> "Glass is null in " + this);
            new NullPointerException("glass is null").printStackTrace();
        }
    }

    /***********************************************/
    /*        IRawElementProviderSimple            */
    /***********************************************/
    private long IRawElementProviderSimple_GetPatternProvider(int patternId) {
        return callElementLongW(el -> el.GetPatternProvider(patternId), glass -> glass.GetPatternProvider(patternId), 0L);
    }
    private long IRawElementProviderSimple_get_HostRawElementProvider() {
        return callElementLongW(UIAElementAdapter::get_HostRawElementProvider, WinAccessible::get_HostRawElementProvider, 0L);
    }
    private WinVariant IRawElementProviderSimple_GetPropertyValue(int propertyId) {
        return callElementObjW(el -> el.GetPropertyValue(propertyId), glass -> glass.GetPropertyValue(propertyId), Variant.vt_empty().toWinVariant());
    }

    /***********************************************/
    /*       IRawElementProviderFragment           */
    /***********************************************/
    private float[] IRawElementProviderFragment_get_BoundingRectangle() {
        return callElementFloatArrayW(UIAElementAdapter::get_BoundingRectangle, WinAccessible::get_BoundingRectangle, new float[0]);
    }
    private long IRawElementProviderFragment_get_FragmentRoot() {
        return callElementLongW(UIAElementAdapter::get_FragmentRoot, WinAccessible::get_FragmentRoot, 0L);
    }
    private long[] IRawElementProviderFragment_GetEmbeddedFragmentRoots() {
        return callElementLongArrayW(UIAElementAdapter::GetEmbeddedFragmentRoots, WinAccessible::GetEmbeddedFragmentRoots, new long[0]);
    }
    private int[] IRawElementProviderFragment_GetRuntimeId() {
        return callElementIntArrayW(UIAElementAdapter::GetRuntimeId, WinAccessible::GetRuntimeId, new int[0]);
    }
    public long IRawElementProviderFragment_Navigate(int direction) {
        return callElementLongW(el -> el.Navigate(direction), glass -> glass.Navigate(direction), 0L);
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
            Logger.error(ProxyAccessible.class, () -> "Failed to get Accessible! " + node, e);
            throw new RuntimeException("Failed to get Accessible! " + node , e);
        }
    }

    /***********************************************/
    /*     IRawElementProviderFragmentRoot         */
    /***********************************************/
    private long IRawElementProviderFragmentRoot_ElementProviderFromPoint(double x, double y) {
        return Util.guard(() -> {
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
        System.err.println("GetFocus!! " + this);
        // Our only root is the scene itself, so GetFocus needs to be answered by the scene
        return Util.guard(() -> {
            checkGlass();
            return glass.GetFocus();
        });
    }
    /***********************************************/
    /*     IRawElementProviderAdviseEvents         */
    /***********************************************/
    private void IRawElementProviderAdviseEvents_AdviseEventAdded(int eventId, long propertyIDs) {
        Util.guard(() -> {
            checkGlass();
            glass.AdviseEventAdded(eventId, propertyIDs);
        });
    }
    private void IRawElementProviderAdviseEvents_AdviseEventRemoved(int eventId, long propertyIDs) {
        Util.guard(() -> {
            checkGlass();
            glass.AdviseEventRemoved(eventId, propertyIDs);
        });
    }

    /***********************************************/
    /*             IInvokeProvider                 */
    /***********************************************/
    private void IInvokeProvider_Invoke() {
        callProviderW(NativeIInvokeProvider.class, NativeIInvokeProvider::Invoke, WinAccessible::Invoke);
    }
    /***********************************************/
    /*           ISelectionProvider                */
    /***********************************************/
    private long[] ISelectionProvider_GetSelection() {
        return callProviderLongArrayW(NativeISelectionProvider.class, NativeISelectionProvider::GetSelection, WinAccessible::GetSelection, new long[0]);
    }
    private boolean ISelectionProvider_get_CanSelectMultiple() {
        return callProviderBooleanW(NativeISelectionProvider.class, NativeISelectionProvider::get_CanSelectMultiple, WinAccessible::get_CanSelectMultiple, false);
    }
    private boolean ISelectionProvider_get_IsSelectionRequired() {
        return callProviderBooleanW(NativeISelectionProvider.class, NativeISelectionProvider::get_IsSelectionRequired, WinAccessible::get_IsSelectionRequired, false);
    }
    /***********************************************/
    /*          ISelectionItemProvider             */
    /***********************************************/
    private void ISelectionItemProvider_Select() {
         callProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::Select, WinAccessible::Select);
    }
    private void ISelectionItemProvider_AddToSelection() {
        callProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::AddToSelection, WinAccessible::AddToSelection);
    }
    private void ISelectionItemProvider_RemoveFromSelection() {
        callProviderW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::RemoveFromSelection, WinAccessible::RemoveFromSelection);
    }
    private boolean ISelectionItemProvider_get_IsSelected() {
        return callProviderBooleanW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::get_IsSelected, WinAccessible::get_IsSelected, false);
    }
    private long ISelectionItemProvider_get_SelectionContainer() {
        return callProviderLongW(NativeISelectionItemProvider.class, NativeISelectionItemProvider::get_SelectionContainer, WinAccessible::get_SelectionContainer, 0L);
    }
    /***********************************************/
    /*           IRangeValueProvider               */
    /***********************************************/
    private void IRangeValueProvider_SetValue(double val) {
        callProviderW(NativeIRangeValueProvider.class, provider -> provider.SetValue(val), win -> win.SetValue(val));
    }
    private double IRangeValueProvider_get_Value() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Value, WinAccessible::get_Value, 0);
    }
    /*  Note that this method is called by the IValueProvider also. */
    private boolean IRangeValueProvider_get_IsReadOnly() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                if (uiaElementAdapter.getProviderRegistry().isProviderAvailable(StandardPatternIds.UIA_RangeValuePatternId.getNativeValue())) {
                    return callProviderBoolean(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_IsReadOnly, false, false);
                }
                if (uiaElementAdapter.getProviderRegistry().isProviderAvailable(StandardPatternIds.UIA_ValuePatternId.getNativeValue())) {
                    return callProviderBoolean(NativeIValueProvider.class, NativeIValueProvider::get_IsReadOnly, false, false);
                }
            }
            if (glass != null) {
                return glass.get_IsReadOnly();
            }
            return false;
        });
    }
    private double IRangeValueProvider_get_Maximum() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Maximum, WinAccessible::get_Maximum, 1);
    }
    private double IRangeValueProvider_get_Minimum() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_Minimum, WinAccessible::get_Minimum, 0);
    }
    private double IRangeValueProvider_get_LargeChange() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_LargeChange, WinAccessible::get_LargeChange, Double.NaN);
    }
    private double IRangeValueProvider_get_SmallChange() {
        return callProviderDoubleW(NativeIRangeValueProvider.class, NativeIRangeValueProvider::get_SmallChange, WinAccessible::get_SmallChange, Double.NaN);
    }
    /***********************************************/
    /*             IValueProvider                  */
    /***********************************************/
    private void IValueProvider_SetValueString(String val) {
        callProviderW(NativeIValueProvider.class, provider -> provider.SetValue(val), win -> win.SetValueString(val));
    }
    private String IValueProvider_get_ValueString() {
        return callProviderW(NativeIValueProvider.class, NativeIValueProvider::get_Value, WinAccessible::get_ValueString, (String)null);
    }
    /***********************************************/
    /*              ITextProvider                  */
    /***********************************************/
    private long[] ITextProvider_GetVisibleRanges() {
        return callProviderLongArrayW(NativeITextProvider.class, NativeITextProvider::GetVisibleRanges, WinAccessible::GetVisibleRanges, new long[0]);
    }
    private long[] ITextProvider_GetSelection() {
        return callProviderLongArrayW(NativeITextProvider.class, NativeITextProvider::GetSelection, WinAccessible::GetSelection, new long[0]);
    }
    private long ITextProvider_RangeFromChild(long childElement) {
        return callProviderLongW(NativeITextProvider.class, p -> p.RangeFromChild(childElement), g -> g.RangeFromChild(childElement), 0);
    }
    private long ITextProvider_RangeFromPoint(double x, double y) {
        return callProviderLongW(NativeITextProvider.class, p -> p.RangeFromPoint(x, y), g -> g.RangeFromPoint(x, y), 0);
    }
    private long ITextProvider_get_DocumentRange() {
        return callProviderLongW(NativeITextProvider.class, NativeITextProvider::get_DocumentRange, WinAccessible::get_DocumentRange, 0);
    }
    private int ITextProvider_get_SupportedTextSelection() {
        return callProviderIntW(NativeITextProvider.class, NativeITextProvider::get_SupportedTextSelection, WinAccessible::get_SupportedTextSelection, 0);
    }
     /***********************************************/
    /*             IGridProvider                   */
    /***********************************************/
    private int IGridProvider_get_ColumnCount() {
        return callProviderIntW(NativeIGridProvider.class, NativeIGridProvider::get_ColumnCount, WinAccessible::get_ColumnCount, 0);
    }
    private int IGridProvider_get_RowCount() {
        return callProviderIntW(NativeIGridProvider.class, NativeIGridProvider::get_RowCount, WinAccessible::get_RowCount, 0);
    }
    private long IGridProvider_GetItem(int row, int column) {
        return callProviderLongW(NativeIGridProvider.class, p -> p.GetItem(row, column), g -> g.GetItem(row, column), 0);
    }
    /***********************************************/
    /*             IGridItemProvider               */
    /***********************************************/
    private int IGridItemProvider_get_Column() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Column, WinAccessible::get_Column, 0);
    }
    private int IGridItemProvider_get_ColumnSpan() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ColumnSpan, WinAccessible::get_ColumnSpan, 0);
    }
    private long IGridItemProvider_get_ContainingGrid() {
        return callProviderLongW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ContainingGrid, WinAccessible::get_ContainingGrid, 0);
    }
    private int IGridItemProvider_get_Row() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Row, WinAccessible::get_Row, 0);
    }
    private int IGridItemProvider_get_RowSpan() {
        return callProviderIntW(NativeIGridItemProvider.class, NativeIGridItemProvider::get_RowSpan, WinAccessible::get_RowSpan, 0);
    }
    /***********************************************/
    /*               ITableProvider                */
    /***********************************************/
    private long[] ITableProvider_GetColumnHeaders() {
        return callProviderLongArrayW(NativeITableProvider.class, NativeITableProvider::GetColumnHeaders, WinAccessible::GetColumnHeaders, new long[0]);
    }
    private long[] ITableProvider_GetRowHeaders() {
        return callProviderLongArrayW(NativeITableProvider.class, NativeITableProvider::GetRowHeaders, WinAccessible::GetRowHeaders, new long[0]);
    }
    private int ITableProvider_get_RowOrColumnMajor() {
        return callProviderIntW(NativeITableProvider.class, NativeITableProvider::get_RowOrColumnMajor, WinAccessible::get_RowOrColumnMajor, 0);
    }
    /***********************************************/
    /*             ITableItemProvider              */
    /***********************************************/
    private long[] ITableItemProvider_GetColumnHeaderItems() {
        return callProviderLongArrayW(NativeITableItemProvider.class, NativeITableItemProvider::GetColumnHeaderItems, WinAccessible::GetColumnHeaderItems, new long[0]);
    }
    private long[] ITableItemProvider_GetRowHeaderItems() {
        return callProviderLongArrayW(NativeITableItemProvider.class, NativeITableItemProvider::GetRowHeaderItems, WinAccessible::GetRowHeaderItems, new long[0]);
    }
    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    private void IToggleProvider_Toggle() {
        callProviderW(NativeIToggleProvider.class, NativeIToggleProvider::Toggle, WinAccessible::Toggle);
    }
    private int IToggleProvider_get_ToggleState() {
        return callProviderIntW(NativeIToggleProvider.class, NativeIToggleProvider::get_ToggleState, WinAccessible::get_ToggleState, 0);
    }
    /***********************************************/
    /*             IExpandCollapseProvider         */
    /***********************************************/
    private void IExpandCollapseProvider_Collapse() {
        callProviderW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::Collapse, WinAccessible::Collapse);
    }
    private void IExpandCollapseProvider_Expand() {
        callProviderW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::Expand, WinAccessible::Expand);
    }
    private int IExpandCollapseProvider_get_ExpandCollapseState() {
        return callProviderIntW(NativeIExpandCollapseProvider.class, NativeIExpandCollapseProvider::get_ExpandCollapseState, WinAccessible::get_ExpandCollapseState, ExpandCollapseState.Collapsed.getNativeValue());
    }
    /***********************************************/
    /*             ITransformProvider              */
    /***********************************************/
    private boolean ITransformProvider_get_CanMove() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanMove, WinAccessible::get_CanMove, false);
    }
    private boolean ITransformProvider_get_CanResize() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanResize, WinAccessible::get_CanResize, false);
    }
    private boolean ITransformProvider_get_CanRotate() {
        return callProviderBooleanW(NativeITransformProvider.class, NativeITransformProvider::get_CanRotate, WinAccessible::get_CanRotate, false);
    }
    private void ITransformProvider_Move(double x, double y) {
        callProviderW(NativeITransformProvider.class, provider -> provider.Move(x, y), glass -> glass.Move(x, y));
    }
    private void ITransformProvider_Resize(double width, double height) {
        callProviderW(NativeITransformProvider.class, provider -> provider.Resize(width, height), glass -> glass.Resize(width, height));
    }
    private void ITransformProvider_Rotate(double degrees) {
        callProviderW(NativeITransformProvider.class, provider -> provider.Rotate(degrees), glass -> glass.Rotate(degrees));
    }

    // ITransformProvider2
    private boolean ITransformProvider2_get_CanZoom() {
        return callProviderBoolean(NativeITransformProvider2.class, NativeITransformProvider2::get_CanZoom, false, false);
    }
    private double ITransformProvider2_get_ZoomLevel() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomLevel, 1, 1);
    }
    private double ITransformProvider2_get_ZoomMinimum() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomMinimum, 1, 1);
    }
    private double ITransformProvider2_get_ZoomMaximum() {
        return callProviderDouble(NativeITransformProvider2.class, NativeITransformProvider2::get_ZoomMaximum, 1, 1);
    }
    private void ITransformProvider2_Zoom(double zoom) {
        callProvider(NativeITransformProvider2.class, provider -> provider.Zoom(zoom));
    }
    private void ITransformProvider2_ZoomByUnit(int zoomUnit) {
        callProvider(NativeITransformProvider2.class, provider -> provider.ZoomByUnit(zoomUnit));
    }

    /***********************************************/
    /*             IScrollProvider                 */
    /***********************************************/
    private void IScrollProvider_Scroll(int horizontalAmount, int verticalAmount) {
        callProviderW(NativeIScrollProvider.class, p -> p.Scroll(horizontalAmount, verticalAmount), g -> g.Scroll(horizontalAmount, verticalAmount));
    }
    private void IScrollProvider_SetScrollPercent(double horizontalPercent, double verticalPercent) {
        callProviderW(NativeIScrollProvider.class, p -> p.SetScrollPercent(horizontalPercent, verticalPercent), g -> g.SetScrollPercent(horizontalPercent, verticalPercent));
    }
    private boolean IScrollProvider_get_HorizontallyScrollable() {
        return callProviderBooleanW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontallyScrollable, WinAccessible::get_HorizontallyScrollable, false);
    }
    private double IScrollProvider_get_HorizontalScrollPercent() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalScrollPercent, WinAccessible::get_HorizontalScrollPercent, 0);
    }
    private double IScrollProvider_get_HorizontalViewSize() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalViewSize, WinAccessible::get_HorizontalViewSize, 0);
    }
    private boolean IScrollProvider_get_VerticallyScrollable() {
        return callProviderBooleanW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticallyScrollable, WinAccessible::get_VerticallyScrollable, false);
    }
    private double IScrollProvider_get_VerticalScrollPercent() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalScrollPercent, WinAccessible::get_VerticalScrollPercent, 0);
    }
    private double IScrollProvider_get_VerticalViewSize() {
        return callProviderDoubleW(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalViewSize, WinAccessible::get_VerticalViewSize, 0);
    }
    /***********************************************/
    /*             IScrollItemProvider             */
    /***********************************************/
    private void IScrollItemProvider_ScrollIntoView() {
        callProviderW(NativeIScrollItemProvider.class, NativeIScrollItemProvider::ScrollIntoView, WinAccessible::ScrollIntoView);
    }


    // IWindowProvider
    private void WindowProvider_Close() {
        callProvider(NativeIWindowProvider.class, NativeIWindowProvider::Close);
    }
    private boolean WindowProvider_get_CanMaximize() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_CanMaximize, false, false);
    }
    private boolean WindowProvider_get_CanMinimize() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_CanMinimize, false, false);
    }
    private boolean WindowProvider_get_IsModal() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_IsModal, false, false);
    }
    private boolean WindowProvider_get_IsTopmost() {
        return callProviderBoolean(NativeIWindowProvider.class, NativeIWindowProvider::get_IsTopmost, false, false);
    }
    private int WindowProvider_get_WindowInteractionState() {
        return callProviderInt(NativeIWindowProvider.class, NativeIWindowProvider::get_WindowInteractionState, 0, 0);
    }
    private int WindowProvider_get_WindowVisualState() {
        return callProviderInt(NativeIWindowProvider.class, NativeIWindowProvider::get_WindowVisualState, 0, 0);
    }
    private void WindowProvider_SetVisualState(int state) {
        callProvider(NativeIWindowProvider.class, provider -> provider.SetVisualState(state));
    }
    private boolean WindowProvider_WaitForInputIdle(int milliseconds) {
        return callProviderBoolean(NativeIWindowProvider.class, provider -> provider.WaitForInputIdle(milliseconds), false, false);
    }

    
    // IDockProvider
    private int     DockProvider_get_DockPosition() { 
        return callProviderInt(NativeIDockProvider.class, NativeIDockProvider::get_DockPosition, 0 , 0);
    }
    private void    DockProvider_SetDockPosition(int dockPosition) {
        callProvider(NativeIDockProvider.class, provider -> provider.SetDockPosition(dockPosition));
    }
    // IAnnotationProvider
    private int     IAnnotationProvider_get_AnnotationTypeId() { 
        return callProviderInt(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_AnnotationTypeId, 0, 0);
    }
    private String  IAnnotationProvider_get_AnnotationTypeName()  {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_AnnotationTypeName, (String) null, null);
    }
    private String  IAnnotationProvider_get_Author() {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_Author, (String) null, null);
    }
    private String  IAnnotationProvider_get_DateTime() {
        return callProvider(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_DateTime, (String) null, null);
    }
    private long    IAnnotationProvider_get_Target() {
        return callProviderLong(NativeIAnnotationProvider.class, NativeIAnnotationProvider::get_Target, 0L, 0L);
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
        return 0L;
    }
    // IMultipleViewProvider
    private int         IMultipleViewProvider_get_CurrentView() {return 0;}
    private int[]       IMultipleViewProvider_GetSupportedViews() { return new int[0]; }
    private String      IMultipleViewProvider_GetViewName(int viewId) { return ""; }
    private void        IMultipleViewProvider_SetCurrentView(int viewId) {}

    // ITextChildProvider
    private long ITextChildProvider_get_TextContainer() {
        return callProviderLong(NativeITextChildProvider.class, NativeITextChildProvider::get_TextContainer, 0L, 0L);
    }
    private long ITextChildProvider_get_TextRange() {
        return callProviderLong(NativeITextChildProvider.class, NativeITextChildProvider::get_TextRange, 0L, 0L);
    }

    // ITextProvider2
    public static final class NCaretRangeResult {
        public boolean isActive;
        public long range;
    }
    private NCaretRangeResult   ITextProvider2_GetCaretRange() { 
        return callProvider(NativeITextProvider2.class, NativeITextProvider2::GetCaretRange, null, null);
    }
    private long                ITextProvider2_RangeFromAnnotation(long annotationElement) {
        return callProviderLong(NativeITextProvider2.class, prov -> prov.RangeFromAnnotation(annotationElement), 0L, 0L);
    }
    // ITextEditProvider
    private long ITextEditProvider_GetActiveComposition() {
        return callProviderLong(NativeITextEditProvider.class, NativeITextEditProvider::GetActiveComposition, 0L, 0L);
    }
    private long ITextEditProvider_GetConversionTarget() {
        return callProviderLong(NativeITextEditProvider.class, NativeITextEditProvider::GetConversionTarget, 0L, 0L);
    }


    // Utility functions

    <R> R callElementObjW(Function<UIAElementAdapter, R> method, Function<WinAccessible, R> glassMethod, R errorValue) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return method.apply(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.apply(glass);
            }
        }, errorValue);
    }

    void callElementW(Consumer<UIAElementAdapter> method, Consumer<WinAccessible> glassMethod) {
        Util.guard(() -> {
            if (uiaElementAdapter != null) {
                method.accept(uiaElementAdapter);
            } else {
                checkGlass();
                glassMethod.accept(glass);
            }
        });
    }


    long callElementLongW(ToLongFunction<UIAElementAdapter> method, ToLongFunction<WinAccessible> glassMethod, long errorValue) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsLong(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsLong(glass);
            }
        }, errorValue);
    }

    long[] callElementLongArrayW(ToLongArrayFunction<UIAElementAdapter> method, ToLongArrayFunction<WinAccessible> glassMethod, long[] errorValue) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsLongArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsLongArray(glass);
            }
        }, errorValue);
    }

    float[] callElementFloatArrayW(ToFloatArrayFunction<UIAElementAdapter> method, ToFloatArrayFunction<WinAccessible> glassMethod, float[] errorValue) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsFloatArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsFloatArray(glass);
            }
        }, errorValue);
    }

    int[] callElementIntArrayW(ToIntArrayFunction<UIAElementAdapter> method, ToIntArrayFunction<WinAccessible> glassMethod, int[] errorValue) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return method.applyAsIntArray(uiaElementAdapter);
            } else {
                checkGlass();
                return glassMethod.applyAsIntArray(glass);
            }
        }, errorValue);
    }

    <P> void callProviderW(Class<P> providerType, Consumer<P> method, Consumer<WinAccessible> glassMethod) {
        Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                method.accept(provider);
            } else {
                checkGlass();
                glassMethod.accept(glass);
            }
        });
    }

    <P> void callProvider(Class<P> providerType, Consumer<P> method) {
        Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                method.accept(provider);
            }
        });
    }

    <P, R> R callProviderW(Class<P> providerType, Function<P, R> method, Function<WinAccessible, R> glassMethod, R errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.apply(provider);
            } else {
                checkGlass();
                return glassMethod.apply(glass);
            }
        });
    }
    
    <P, R> R callProvider(Class<P> providerType, Function<P, R> method, R noProviderValue, R errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.apply(provider);
            } else {
                return noProviderValue;
            }
        });
    }

    <P> long callProviderLongW(Class<P> providerType, ToLongFunction<P> method, ToLongFunction<WinAccessible> glassMethod, long errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLong(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsLong(glass);
            }
        }, errorValue);
    }
    <P> long callProviderLong(Class<P> providerType, ToLongFunction<P> method, long noProviderValue, long errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLong(provider);
            } else {
                return noProviderValue;
            } 
        }, errorValue);
    }
   
    <P> long[] callProviderLongArrayW(Class<P> providerType, ToLongArrayFunction<P> method, ToLongArrayFunction<WinAccessible> alternative, long[] errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsLongArray(provider);
            } else {
                checkGlass();
                return alternative.applyAsLongArray(glass);
            }
        }, errorValue);
    }

    <P> int callProviderIntW(Class<P> providerType, ToIntFunction<P> method, ToIntFunction<WinAccessible> glassMethod, int errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsInt(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsInt(glass);
            } 
        }, errorValue);
    }

    <P> int callProviderInt(Class<P> providerType, ToIntFunction<P> method, int noProviderValue, int errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsInt(provider);
            } else {
                return noProviderValue;
            } 
        }, errorValue);
    }



    <P> double callProviderDoubleW(Class<P> providerType, ToDoubleFunction<P> method, ToDoubleFunction<WinAccessible> glassMethod, double errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsDouble(provider);
            } else {
                checkGlass();
                return glassMethod.applyAsDouble(glass);
            } 
        }, errorValue);
    }

    <P> double callProviderDouble(Class<P> providerType, ToDoubleFunction<P> method, double noProviderValue, double errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsDouble(provider);
            } else {
                return noProviderValue;
            } 
        }, errorValue);
    }

    
    <P> boolean callProviderBooleanW(Class<P> providerType, ToBooleanFunction<P> method, ToBooleanFunction<WinAccessible> alternative, boolean errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsBoolean(provider);
            } else {
                checkGlass();
                return alternative.applyAsBoolean(glass);
            } 
        }, errorValue);
    }

    <P> boolean callProviderBoolean(Class<P> providerType, ToBooleanFunction<P> method, boolean noProviderValue, boolean errorValue) {
        return Util.guard(() -> {
            P provider = getNativeProvider(providerType);
            if (provider != null) {
                return method.applyAsBoolean(provider);
            } else {
                return noProviderValue;
            } 
        }, errorValue);
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
