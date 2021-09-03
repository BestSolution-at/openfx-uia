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
                return glass.GetPatternProvider(patternId);
            }
        });
    }
    private long IRawElementProviderSimple_get_HostRawElementProvider() {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.get_HostRawElementProvider();
            } else {
                return glass.get_HostRawElementProvider();
            }
        });
    }
    private WinVariant IRawElementProviderSimple_GetPropertyValue(int propertyId) {
        return Util.guard(() -> {
            if (uiaElementAdapter != null) {
                return uiaElementAdapter.GetPropertyValue(propertyId);
            } else {
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
        Util.guard(() -> {
            NativeIInvokeProvider provider = getNativeProvider(NativeIInvokeProvider.class);
            if (provider != null) {
                provider.Invoke();
            } else {
                checkGlass();
                glass.Invoke();
            }
        });
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
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.GetVisibleRanges();
            } else {
                checkGlass();
                return glass.GetVisibleRanges();
            }
        });
    }
    private long[] ITextProvider_GetSelection() {
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.GetSelection();
            } else {
                checkGlass();
                return glass.GetSelection();
            }
        });
    }
    private long ITextProvider_RangeFromChild(long childElement) {
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.RangeFromChild(childElement);
            } else {
                checkGlass();
                return glass.RangeFromChild(childElement);
            }
        });
    }
    private long ITextProvider_RangeFromPoint(double x, double y) {
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.RangeFromPoint(x, y);
            } else {
                checkGlass();
                return glass.RangeFromPoint(x, y);
            }
        });
    }
    private long ITextProvider_get_DocumentRange() {
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.get_DocumentRange();
            } else {
                checkGlass();
                return glass.get_DocumentRange();
            }
        });
    }
    private int ITextProvider_get_SupportedTextSelection() {
        return Util.guard(() -> {
            NativeITextProvider textProvider = getNativeProvider(NativeITextProvider.class);
            if (textProvider != null) {
                return textProvider.get_SupportedTextSelection();
            } else {
                checkGlass();
                return glass.get_SupportedTextSelection();
            }
        });
    }
     /***********************************************/
    /*             IGridProvider                   */
    /***********************************************/
    private int IGridProvider_get_ColumnCount() {
        return Util.guard(() -> {
            final NativeIGridProvider provider = getNativeProvider(NativeIGridProvider.class);
            if (provider != null) {
                return provider.get_ColumnCount();
            } else {
                checkGlass();
                return glass.get_ColumnCount();
            }
        });
    }
    private int IGridProvider_get_RowCount() {
        return Util.guard(() -> {
            final NativeIGridProvider provider = getNativeProvider(NativeIGridProvider.class);
            if (provider != null) {
                return provider.get_RowCount();
            } else {
                checkGlass();
                return glass.get_RowCount();
            }
        });
    }
    private long IGridProvider_GetItem(int row, int column) {
        return Util.guard(() -> {
            final NativeIGridProvider provider = getNativeProvider(NativeIGridProvider.class);
            if (provider != null) {
                return provider.GetItem(row, column);
            } else {
                checkGlass();
                return glass.GetItem(row, column);
            }
        });
    }
    /***********************************************/
    /*             IGridItemProvider               */
    /***********************************************/
    private int IGridItemProvider_get_Column() {
        return Util.guard(() -> {
            final NativeIGridItemProvider provider = getNativeProvider(NativeIGridItemProvider.class);
            if (provider != null) {
                return provider.get_Column();
            } else {
                checkGlass();
                return glass.get_Column();
            }
        });
    }
    private int IGridItemProvider_get_ColumnSpan() {
        return Util.guard(() -> {
            final NativeIGridItemProvider provider = getNativeProvider(NativeIGridItemProvider.class);
            if (provider != null) {
                return provider.get_ColumnSpan();
            } else {
                checkGlass();
                return glass.get_ColumnSpan();
            }
        });
    }
    private long IGridItemProvider_get_ContainingGrid() {
        return Util.guard(() -> {
            final NativeIGridItemProvider provider = getNativeProvider(NativeIGridItemProvider.class);
            if (provider != null) {
                return provider.get_ContainingGrid();
            } else {
                checkGlass();
                return glass.get_ContainingGrid();
            }
        });
    }
    private int IGridItemProvider_get_Row() {
        return Util.guard(() -> {
            final NativeIGridItemProvider provider = getNativeProvider(NativeIGridItemProvider.class);
            if (provider != null) {
                return provider.get_Row();
            } else {
                checkGlass();
                return glass.get_Row();
            }
        });
    }
    private int IGridItemProvider_get_RowSpan() {
        return Util.guard(() -> {
            final NativeIGridItemProvider provider = getNativeProvider(NativeIGridItemProvider.class);
            if (provider != null) {
                return provider.get_RowSpan();
            } else {
                checkGlass();
                return glass.get_RowSpan();
            }
        });
    }
    /***********************************************/
    /*               ITableProvider                */
    /***********************************************/
    private long[] ITableProvider_GetColumnHeaders() {
        return Util.guard(() -> {
            final NativeITableProvider provider = getNativeProvider(NativeITableProvider.class);
            if (provider != null) {
                return provider.GetColumnHeaders();
            } else {
                checkGlass();
                return glass.GetColumnHeaders();
            }
        });
    }
    private long[] ITableProvider_GetRowHeaders() {
        return Util.guard(() -> {
            final NativeITableProvider provider = getNativeProvider(NativeITableProvider.class);
            if (provider != null) {
                return provider.GetRowHeaders();
            } else {
                checkGlass();
                return glass.GetRowHeaders();
            }
        });
    }
    private int ITableProvider_get_RowOrColumnMajor() {
        return Util.guard(() -> {
            final NativeITableProvider provider = getNativeProvider(NativeITableProvider.class);
            if (provider != null) {
                return provider.get_RowOrColumnMajor();
            } else {
                checkGlass();
                return glass.get_RowOrColumnMajor();
            }
        });
    }
    /***********************************************/
    /*             ITableItemProvider              */
    /***********************************************/
    private long[] ITableItemProvider_GetColumnHeaderItems() {
        return Util.guard(() -> {
            final NativeITableItemProvider provider = getNativeProvider(NativeITableItemProvider.class);
            if (provider != null) {
                return provider.GetColumnHeaderItems();
            } else {
                checkGlass();
                return glass.GetColumnHeaderItems();
            }
        });
    }
    private long[] ITableItemProvider_GetRowHeaderItems() {
        return Util.guard(() -> {
            final NativeITableItemProvider provider = getNativeProvider(NativeITableItemProvider.class);
            if (provider != null) {
                return provider.GetRowHeaderItems();
            } else {
                checkGlass();
                return glass.GetRowHeaderItems();
            }
        });
    }
    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    private void IToggleProvider_Toggle() {
        Util.guard(() -> {
            NativeIToggleProvider provider = getNativeProvider(NativeIToggleProvider.class);
            if (provider != null) {
                provider.Toggle();
            } else {
                checkGlass();
                glass.Toggle();
            }
        });
    }
    private int IToggleProvider_get_ToggleState() {
        return Util.guard(() -> {
            NativeIToggleProvider provider = getNativeProvider(NativeIToggleProvider.class);
            if (provider != null) {
                return provider.get_ToggleState();
            } else {
                checkGlass();
                return glass.get_ToggleState();
            }
        });
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
        Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                provider.Scroll(horizontalAmount, verticalAmount);
            } else {
                checkGlass();
                glass.Scroll(horizontalAmount, verticalAmount);
            }
        });
    }
    private void IScrollProvider_SetScrollPercent(double horizontalPercent, double verticalPercent) {
        Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                provider.SetScrollPercent(horizontalPercent, verticalPercent);
            } else {
                checkGlass();
                glass.SetScrollPercent(horizontalPercent, verticalPercent);
            }
        });
    }
    private boolean IScrollProvider_get_HorizontallyScrollable() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_HorizontallyScrollable();
            } else {
                checkGlass();
                return glass.get_HorizontallyScrollable();
            }
        });
    }
    private double IScrollProvider_get_HorizontalScrollPercent() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_HorizontalScrollPercent();
            } else {
                checkGlass();
                return glass.get_HorizontalScrollPercent();
            }
        });
    }
    private double IScrollProvider_get_HorizontalViewSize() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_HorizontalViewSize();
            } else {
                checkGlass();
                return glass.get_HorizontalViewSize();
            }
        });
    }
    private boolean IScrollProvider_get_VerticallyScrollable() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_VerticallyScrollable();
            } else {
                checkGlass();
                return glass.get_VerticallyScrollable();
            }
        });
    }
    private double IScrollProvider_get_VerticalScrollPercent() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_VerticalScrollPercent();
            } else {
                checkGlass();
                return glass.get_VerticalScrollPercent();
            }
        });
    }
    private double IScrollProvider_get_VerticalViewSize() {
        return Util.guard(() -> {
            final NativeIScrollProvider provider = getNativeProvider(NativeIScrollProvider.class);
            if (provider != null) {
                return provider.get_VerticalViewSize();
            } else {
                checkGlass();
                return glass.get_VerticalViewSize();
            }
        });
    }
    /***********************************************/
    /*             IScrollItemProvider             */
    /***********************************************/
    private void IScrollItemProvider_ScrollIntoView() {
        Util.guard(() -> {
            final NativeIScrollItemProvider provider = getNativeProvider(NativeIScrollItemProvider.class);
            if (provider != null) {
                provider.ScrollIntoView();
            } else {
                checkGlass();
                glass.ScrollIntoView();
            }
        });
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
        return Util.guard(() -> {
            final NativeITextChildProvider provider = getNativeProvider(NativeITextChildProvider.class);
            if (provider != null) {
                return provider.get_TextContainer();
            } else {
                return 0L;
            }
        });
    }
    private long ITextChildProvider_get_TextRange() {
        return Util.guard(() -> {
            final NativeITextChildProvider provider = getNativeProvider(NativeITextChildProvider.class);
            if (provider != null) {
                return provider.get_TextRange();
            } else {
                return 0L;
            }
        });
    }
    // ITextProvider2
    // XXX GetCaretRange has 2 output arguments!!!
    private long        ITextProvider2_GetCaretRange() { return 0L; }
    private boolean     ITextProvider2_GetCaretRangeIsActive() { return false; }
    private long        ITextProvider2_RangeFromAnnotation(long annotationElement) { return 0L; }
}
