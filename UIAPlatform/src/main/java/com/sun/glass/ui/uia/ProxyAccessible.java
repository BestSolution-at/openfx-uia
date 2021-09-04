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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
import com.sun.glass.ui.uia.provider.NativeITextProvider;
import com.sun.glass.ui.uia.provider.NativeITextChildProvider;
import com.sun.glass.ui.uia.provider.NativeIToggleProvider;
import com.sun.glass.ui.uia.provider.NativeIInvokeProvider;
import com.sun.glass.ui.uia.provider.NativeIScrollProvider;
import com.sun.glass.ui.uia.provider.NativeIScrollItemProvider;
import com.sun.glass.ui.uia.provider.NativeITableProvider;
import com.sun.glass.ui.uia.provider.NativeITableItemProvider;
import com.sun.glass.ui.uia.provider.NativeIGridProvider;
import com.sun.glass.ui.uia.provider.NativeIGridItemProvider;
import com.sun.glass.ui.uia.provider.UIAElementAdapter;
import com.sun.javafx.tk.quantum.QuantumToolkit;

import javafx.geometry.Point2D;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualRootElement;

@SuppressWarnings("restriction")
public class ProxyAccessible extends Accessible {

    private static int next = 0;
    private final int num;

    public static void requireLibrary() {

    }
    
    private static native void _initIDs();
    static {
        // TODO better library loading - maybe look at how glass does it?
        try {
            URL library = ProxyAccessible.class.getResource("/UIAPlatform.dll");
            Path libDir = Files.createTempDirectory("openfx-uia");
            Path lib = libDir.resolve("UIAPlatform.dll");
            Files.copy(library.openStream(), lib);
            Logger.debug(ProxyAccessible.class, () -> "Using " + lib.toString());
            System.load(lib.toString());

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
    
    /*package*/ ProxyAccessible() {
        this.num = next++;
        Logger.debug(this, () -> "ProxyAccessible" + num + " created.");

        this.peer = _createProxyAccessible();
        if (this.peer == 0L) {
            throw new RuntimeException("could not create platform accessible");
        }

        ProxyAccessibleRegistry.getInstance().registerNative(this, peer);

        glass = new WinAccessible(this);
    }

    // this is the *virtual* Accessible
    ProxyAccessible(ProxyAccessible context, IUIAElement uiaElement) {

        if (uiaElement == null) {
            System.err.println("virtual ProxyAccessible cannot be created without an uiaElement");
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
        super.dispose();

        if (peer != 0L) {
            _destroyProxyAccessible(peer);
            peer = 0L;
        }

        // TODO virtuals have no glass NPE!
        glass.dispose();
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
        return "ProxyAccessible" + num + "[" + glass + ", " + glassRoot + ", " + uiaElement + "]";
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
            System.err.println("Glass is null in " + this);
            new NullPointerException("glass is null").printStackTrace();
        }
    }

    /***********************************************/
    /*        IRawElementProviderSimple            */
    /***********************************************/
    private long IRawElementProviderSimple_GetPatternProvider(int patternId) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.GetPatternProvider(patternId);
            } else {
                checkGlass();
                return glass.GetPatternProvider(patternId);
            }
        });
    }
    private long IRawElementProviderSimple_get_HostRawElementProvider() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.get_HostRawElementProvider();
            } else {
                checkGlass();
                return glass.get_HostRawElementProvider();
            }
        });
    }
    private WinVariant IRawElementProviderSimple_GetPropertyValue(int propertyId) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.GetPropertyValue(propertyId);
            } else {
                checkGlass();
                return glass.GetPropertyValue(propertyId);
            }
        });
    }

    /***********************************************/
    /*       IRawElementProviderFragment           */
    /***********************************************/
    private float[] IRawElementProviderFragment_get_BoundingRectangle() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.get_BoundingRectangle();
            } else {
                return glass.get_BoundingRectangle();
            }
        });
    }
    private long IRawElementProviderFragment_get_FragmentRoot() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.get_FragmentRoot();
            } else {
                return glass.get_FragmentRoot();
            }
        });
    }
    private long[] IRawElementProviderFragment_GetEmbeddedFragmentRoots() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.GetEmbeddedFragmentRoots();
            } else {
                return glass.GetEmbeddedFragmentRoots();
            }
        });
    }
    private int[] IRawElementProviderFragment_GetRuntimeId() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.GetRuntimeId();
            } else {
                return glass.GetRuntimeId();
            }
        });
    }
    private long IRawElementProviderFragment_Navigate(int direction) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.Navigate(direction);
            } else {
                return glass.Navigate(direction);
            }
        });
    }
    private void IRawElementProviderFragment_SetFocus() {
        Util.guard(() -> {
            if (uiaElementAdapter != null) {
                uiaElementAdapter.SetFocus();
            } else {
                glass.SetFocus();
            }
        });
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
        callProvider(NativeIInvokeProvider.class, NativeIInvokeProvider::Invoke, WinAccessible::Invoke);
    }
    /***********************************************/
    /*           ISelectionProvider                */
    /***********************************************/
    private long[] ISelectionProvider_GetSelection() {
        return Util.guard(() -> {
            checkGlass();
        return glass.GetSelection();
    });
    }
    private boolean ISelectionProvider_get_CanSelectMultiple() {
        return Util.guard(() -> {
            checkGlass();
        return glass.get_CanSelectMultiple();
    });
    }
    private boolean ISelectionProvider_get_IsSelectionRequired() {
        return Util.guard(() -> {
            checkGlass();
        return glass.get_IsSelectionRequired();
    });
    }
    /***********************************************/
    /*          ISelectionItemProvider             */
    /***********************************************/
    private void ISelectionItemProvider_Select() {
        Util.guard(() -> {
            checkGlass();
            glass.Select();
        });
    }
    private void ISelectionItemProvider_AddToSelection() {
        Util.guard(() -> {
            checkGlass();
            glass.AddToSelection();
        });
    }
    private void ISelectionItemProvider_RemoveFromSelection() {
        Util.guard(() -> {
            checkGlass();
            glass.RemoveFromSelection();
        });
    }
    private boolean ISelectionItemProvider_get_IsSelected() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_IsSelected();
        });
    }
    private long ISelectionItemProvider_get_SelectionContainer() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_SelectionContainer();
        });
    }
    /***********************************************/
    /*           IRangeValueProvider               */
    /***********************************************/
    private void IRangeValueProvider_SetValue(double val) {
        Util.guard(() -> {
            checkGlass();
            glass.SetValue(val);
        });
    }
    private double IRangeValueProvider_get_Value() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_Value();
        });
    }
    /*  Note that this method is called by the IValueProvider also. */
    private boolean IRangeValueProvider_get_IsReadOnly() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_IsReadOnly();
        });
    }
    private double IRangeValueProvider_get_Maximum() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_Maximum();
        });
    }
    private double IRangeValueProvider_get_Minimum() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_Minimum();
        });
    }
    private double IRangeValueProvider_get_LargeChange() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_LargeChange();
        });
    }
    private double IRangeValueProvider_get_SmallChange() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_SmallChange();
        });
    }
    /***********************************************/
    /*             IValueProvider                  */
    /***********************************************/
    private void IValueProvider_SetValueString(String val) {
        Util.guard(() -> {
            checkGlass();
            glass.SetValueString(val);
        });
    }
    private String IValueProvider_get_ValueString() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_ValueString();
        });
    }
    /***********************************************/
    /*              ITextProvider                  */
    /***********************************************/
    private long[] ITextProvider_GetVisibleRanges() {
        return callProviderLongArray(NativeITextProvider.class, NativeITextProvider::GetVisibleRanges, WinAccessible::GetVisibleRanges, new long[0]);
    }
    private long[] ITextProvider_GetSelection() {
        return callProviderLongArray(NativeITextProvider.class, NativeITextProvider::GetSelection, WinAccessible::GetSelection, new long[0]);
    }
    private long ITextProvider_RangeFromChild(long childElement) {
        return callProviderLong(NativeITextProvider.class, p -> p.RangeFromChild(childElement), g -> g.RangeFromChild(childElement), 0);
    }
    private long ITextProvider_RangeFromPoint(double x, double y) {
        return callProviderLong(NativeITextProvider.class, p -> p.RangeFromPoint(x, y), g -> g.RangeFromPoint(x, y), 0);
    }
    private long ITextProvider_get_DocumentRange() {
        return callProviderLong(NativeITextProvider.class, NativeITextProvider::get_DocumentRange, WinAccessible::get_DocumentRange, 0);
    }
    private int ITextProvider_get_SupportedTextSelection() {
        return callProviderInt(NativeITextProvider.class, NativeITextProvider::get_SupportedTextSelection, WinAccessible::get_SupportedTextSelection, 0);
    }
     /***********************************************/
    /*             IGridProvider                   */
    /***********************************************/
    private int IGridProvider_get_ColumnCount() {
        return callProviderInt(NativeIGridProvider.class, NativeIGridProvider::get_ColumnCount, WinAccessible::get_ColumnCount, 0);
    }
    private int IGridProvider_get_RowCount() {
        return callProviderInt(NativeIGridProvider.class, NativeIGridProvider::get_RowCount, WinAccessible::get_RowCount, 0);
    }
    private long IGridProvider_GetItem(int row, int column) {
        return callProviderLong(NativeIGridProvider.class, p -> p.GetItem(row, column), g -> g.GetItem(row, column), 0);
    }
    /***********************************************/
    /*             IGridItemProvider               */
    /***********************************************/
    private int IGridItemProvider_get_Column() {
        return callProviderInt(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Column, WinAccessible::get_Column, 0);
    }
    private int IGridItemProvider_get_ColumnSpan() {
        return callProviderInt(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ColumnSpan, WinAccessible::get_ColumnSpan, 0);
    }
    private long IGridItemProvider_get_ContainingGrid() {
        return callProviderLong(NativeIGridItemProvider.class, NativeIGridItemProvider::get_ContainingGrid, WinAccessible::get_ContainingGrid, 0);
    }
    private int IGridItemProvider_get_Row() {
        return callProviderInt(NativeIGridItemProvider.class, NativeIGridItemProvider::get_Row, WinAccessible::get_Row, 0);
    }
    private int IGridItemProvider_get_RowSpan() {
        return callProviderInt(NativeIGridItemProvider.class, NativeIGridItemProvider::get_RowSpan, WinAccessible::get_RowSpan, 0);
    }
    /***********************************************/
    /*               ITableProvider                */
    /***********************************************/
    private long[] ITableProvider_GetColumnHeaders() {
        return callProviderLongArray(NativeITableProvider.class, NativeITableProvider::GetColumnHeaders, WinAccessible::GetColumnHeaders, new long[0]);
    }
    private long[] ITableProvider_GetRowHeaders() {
        return callProviderLongArray(NativeITableProvider.class, NativeITableProvider::GetRowHeaders, WinAccessible::GetRowHeaders, new long[0]);
    }
    private int ITableProvider_get_RowOrColumnMajor() {
        return callProviderInt(NativeITableProvider.class, NativeITableProvider::get_RowOrColumnMajor, WinAccessible::get_RowOrColumnMajor, 0);
    }
    /***********************************************/
    /*             ITableItemProvider              */
    /***********************************************/
    private long[] ITableItemProvider_GetColumnHeaderItems() {
        return callProviderLongArray(NativeITableItemProvider.class, NativeITableItemProvider::GetColumnHeaderItems, WinAccessible::GetColumnHeaderItems, new long[0]);
    }
    private long[] ITableItemProvider_GetRowHeaderItems() {
        return callProviderLongArray(NativeITableItemProvider.class, NativeITableItemProvider::GetRowHeaderItems, WinAccessible::GetRowHeaderItems, new long[0]);
    }
    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    private void IToggleProvider_Toggle() {
        callProvider(NativeIToggleProvider.class, NativeIToggleProvider::Toggle, WinAccessible::Toggle);
    }
    private int IToggleProvider_get_ToggleState() {
        return callProviderInt(NativeIToggleProvider.class, NativeIToggleProvider::get_ToggleState, WinAccessible::get_ToggleState, 0);
    }
    /***********************************************/
    /*             IExpandCollapseProvider         */
    /***********************************************/
    private void IExpandCollapseProvider_Collapse() {
        Util.guard(() -> {
            checkGlass();
            glass.Collapse();
        });
    }
    private void IExpandCollapseProvider_Expand() {
        Util.guard(() -> {
            checkGlass();
            glass.Expand();
        });
    }
    private int IExpandCollapseProvider_get_ExpandCollapseState() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_ExpandCollapseState();
        });
    }
    /***********************************************/
    /*             ITransformProvider              */
    /***********************************************/
    private boolean ITransformProvider_get_CanMove() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_CanMove();
        });
    }
    private boolean ITransformProvider_get_CanResize() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_CanResize();
        });
    }
    private boolean ITransformProvider_get_CanRotate() {
        return Util.guard(() -> {
            checkGlass();
            return glass.get_CanRotate();
        });
    }
    private void ITransformProvider_Move(double x, double y) {
        Util.guard(() -> {
            checkGlass();
            glass.Move(x, y);
        });
    }
    private void ITransformProvider_Resize(double width, double height) {
        Util.guard(() -> {
            checkGlass();
            glass.Resize(width, height);
        });
    }
    private void ITransformProvider_Rotate(double degrees) {
        Util.guard(() -> {
            checkGlass();
            glass.Rotate(degrees);
        });
    }
    /***********************************************/
    /*             IScrollProvider                 */
    /***********************************************/
    private void IScrollProvider_Scroll(int horizontalAmount, int verticalAmount) {
        callProvider(NativeIScrollProvider.class, p -> p.Scroll(horizontalAmount, verticalAmount), g -> g.Scroll(horizontalAmount, verticalAmount));
    }
    private void IScrollProvider_SetScrollPercent(double horizontalPercent, double verticalPercent) {
        callProvider(NativeIScrollProvider.class, p -> p.SetScrollPercent(horizontalPercent, verticalPercent), g -> g.SetScrollPercent(horizontalPercent, verticalPercent));
    }
    private boolean IScrollProvider_get_HorizontallyScrollable() {
        return callProviderBoolean(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontallyScrollable, WinAccessible::get_HorizontallyScrollable, false);
    }
    private double IScrollProvider_get_HorizontalScrollPercent() {
        return callProviderDouble(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalScrollPercent, WinAccessible::get_HorizontalScrollPercent, 0);
    }
    private double IScrollProvider_get_HorizontalViewSize() {
        return callProviderDouble(NativeIScrollProvider.class, NativeIScrollProvider::get_HorizontalViewSize, WinAccessible::get_HorizontalViewSize, 0);
    }
    private boolean IScrollProvider_get_VerticallyScrollable() {
        return callProviderBoolean(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticallyScrollable, WinAccessible::get_VerticallyScrollable, false);
    }
    private double IScrollProvider_get_VerticalScrollPercent() {
        return callProviderDouble(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalScrollPercent, WinAccessible::get_VerticalScrollPercent, 0);
    }
    private double IScrollProvider_get_VerticalViewSize() {
        return callProviderDouble(NativeIScrollProvider.class, NativeIScrollProvider::get_VerticalViewSize, WinAccessible::get_VerticalViewSize, 0);
    }
    /***********************************************/
    /*             IScrollItemProvider             */
    /***********************************************/
    private void IScrollItemProvider_ScrollIntoView() {
        callProvider(NativeIScrollItemProvider.class, NativeIScrollItemProvider::ScrollIntoView, WinAccessible::ScrollIntoView);
    }


    // other
    private void WindowProvider_Close() {}
    private boolean WindowProvider_get_CanMaximize() {return false;}
    private boolean WindowProvider_get_CanMinimize() {return false;}
    private boolean WindowProvider_get_IsModal() {return false;}
    private boolean WindowProvider_get_IsTopmost() {return false;}
    private int WindowProvider_get_WindowInteractionState() {return 0;}
    private int WindowProvider_get_WindowVisualState() {return 0;}
    private void WindowProvider_SetVisualState(int s) {}
    private boolean WindowProvider_WaitForInputIdle(int s) {return false;}

    
    // IDockProvider
    private int     DockProvider_get_DockPosition() { return 0; }
    private void    DockProvider_SetDockPosition(int dockPosition) {}
    // IAnnotationProvider
    private int     IAnnotationProvider_get_AnnotationTypeId() { return 0; }
    private String  IAnnotationProvider_get_AnnotationTypeName()  { return ""; }
    private String  IAnnotationProvider_get_Author() { return ""; }
    private String  IAnnotationProvider_get_DateTime() { return ""; }
    private long    IAnnotationProvider_get_Target() { return 0L; }
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
    // XXX GetCaretRange has 2 output arguments!!!
    private long        ITextProvider2_GetCaretRange() { return 0L; }
    private boolean     ITextProvider2_GetCaretRangeIsActive() { return false; }
    private long        ITextProvider2_RangeFromAnnotation(long annotationElement) { return 0L; }



    // Utility functions
    <P> void callProvider(Class<P> providerType, Consumer<P> method, Consumer<WinAccessible> glassMethod) {
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

    <P, R> R callProvider(Class<P> providerType, Function<P, R> method, Function<WinAccessible, R> glassMethod, R errorValue) {
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

    <P> long callProviderLong(Class<P> providerType, ToLongFunction<P> method, ToLongFunction<WinAccessible> glassMethod, long errorValue) {
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
    @FunctionalInterface
    static interface ToLongArrayFunction<T> {
        long[] applyAsLongArray(T value);
    }
    <P> long[] callProviderLongArray(Class<P> providerType, ToLongArrayFunction<P> method, ToLongArrayFunction<WinAccessible> alternative, long[] errorValue) {
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

    <P> int callProviderInt(Class<P> providerType, ToIntFunction<P> method, ToIntFunction<WinAccessible> glassMethod, int errorValue) {
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

    <P> double callProviderDouble(Class<P> providerType, ToDoubleFunction<P> method, ToDoubleFunction<WinAccessible> glassMethod, double errorValue) {
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

    @FunctionalInterface
    static interface ToBooleanFunction<T> {
        boolean applyAsBoolean(T value);
    }
    <P> boolean callProviderBoolean(Class<P> providerType, ToBooleanFunction<P> method, ToBooleanFunction<WinAccessible> alternative, boolean errorValue) {
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


}
