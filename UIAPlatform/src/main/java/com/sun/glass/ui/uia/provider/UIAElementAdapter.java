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
package com.sun.glass.ui.uia.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.geometry.Bounds;
import javafx.uia.ControlType;
import javafx.uia.IInvokeProvider;
import javafx.uia.IToggleProvider;
import javafx.uia.IUIAElement;
import javafx.uia.IWindowProvider;
import javafx.uia.StandardEventIds;
import javafx.uia.StandardPatternIds;
import javafx.uia.StandardPropertyIds;

public class UIAElementAdapter extends BaseAdapter<IUIAElement> implements NativeIRawElementProviderSimple, NativeIRawElementProviderFragment {

    private final IUIAElement.IUIAElementEvents events = new IUIAElement.IUIAElementEvents(){
        @Override
        public void notifyControlTypeChanged(ControlType oldValue, ControlType newValue) {
            UiaRaiseAutomationPropertyChangedEvent(StandardPropertyIds.UIA_ControlTypePropertyId, oldValue, newValue);
        }
        @Override
        public void notifyBoundsChanged(Bounds oldBounds, Bounds newBounds) {
            // TODO need to pass bounds over variant
        }
        @Override
        public void notifyStructureChanged() {
            UiaRaiseAutomationEvent(StandardEventIds.UIA_StructureChangedEventId);            
        }
    };

    private Map<Class<?>, Object> nativeProviders = new HashMap<>();

    public UIAElementAdapter(ProxyAccessible accessible, IUIAElement provider) {
        super(accessible, provider);
        provider.initialize(events);

        if (provider.isProviderAvailable(IWindowProvider.class)) {
            IWindowProvider windowProvider = provider.getProvider(IWindowProvider.class);
            nativeProviders.put(NativeIWindowProvider.class, new WindowProviderAdapter(accessible, windowProvider));
        }
        if (provider.isProviderAvailable(IToggleProvider.class)) {
            IToggleProvider toggleProvider = provider.getProvider(IToggleProvider.class);
            nativeProviders.put(NativeIToggleProvider.class, new ToggleProviderAdapter(accessible, toggleProvider));
        }
        if (provider.isProviderAvailable(IInvokeProvider.class)) {
            IInvokeProvider javaProvider = provider.getProvider(IInvokeProvider.class);
            nativeProviders.put(NativeIInvokeProvider.class, new InvokeProviderAdapter(accessible, javaProvider));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getNativeProvider(Class<T> type) {
        return (T) nativeProviders.get(type);
    }

    private void log(IUIAElement element, String msg) {
        System.err.println("V: " + element + " - " + msg);
    }


    @Override
    public float[] get_BoundingRectangle() {
        // TODO should we return javafx bounds?
        return Convert.convertBounds(provider.getBounds());
    }

    @Override
    public long get_FragmentRoot() {
        return accessible.getGlassAccessibleRoot().get_FragmentRoot();
    }

    @Override
    public long[] GetEmbeddedFragmentRoots() {
        return accessible.getGlassAccessibleRoot().GetEmbeddedFragmentRoots();

    }

    @Override
    public int[] GetRuntimeId() {
        IUIAElement element = accessible.getUIAElement();
        if (element != null && element.isVirtual()) {
            //log(node, "GetRuntimeId()");
            final int UiaAppendRuntimeId                  = 3;
            return new int[] {UiaAppendRuntimeId, element.getId()};
        } else {
            //System.err.println("GetRuntimeId() -> " + accessible.getGlassAccessibleRoot());
            return accessible.getGlassAccessibleRoot().GetRuntimeId();
        }
    }

    private <T> Optional<T> first(List<T> list) {
		if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(0));
    }
    private <T> Optional<T> last(List<T> list) {
    	if (list.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(list.get(list.size() - 1));
    }
    @Override
    public long Navigate(int direction) {
        IUIAElement element = accessible.getUIAElement();
        if (element != null && (element.isVirtual() || !element.getChildren().isEmpty())) {
            log(element, "Navigate("+direction+")");
        }
        // TODO custom UIANode child navigation - how to get the UIANode
       
        if (element != null) {
            boolean isVirtual = element.isVirtual();
            boolean isVirtualRoot = !element.getChildren().isEmpty() && accessible.getGlassAccessible() != null;

        
            if (isVirtual || isVirtualRoot) {
                final int NavigateDirection_Parent            = 0;
                final int NavigateDirection_NextSibling       = 1;
                final int NavigateDirection_PreviousSibling   = 2;
                final int NavigateDirection_FirstChild        = 3;
                final int NavigateDirection_LastChild         = 4;
                try {
                    // Note: need mapping from UIANode to ProxyAccessible
                    switch (direction) {
                        case NavigateDirection_FirstChild: {
                            List<IUIAElement> children = element.getChildren();
                            Optional<IUIAElement> firstChild = first(children);
                            log(element, "First Child: " + firstChild);
                            return firstChild.map(accessible::getVirtualAccessible).map(ProxyAccessible::getNativeAccessible).orElse(0L);
                        } 
                        case NavigateDirection_LastChild: {
                            List<IUIAElement> children = element.getChildren();
                            Optional<IUIAElement> lastChild = last(children);
                            log(element, "Last Child: " + lastChild);
                            return lastChild.map(accessible::getVirtualAccessible).map(ProxyAccessible::getNativeAccessible).orElse(0L);
                        }
                    
                        case NavigateDirection_Parent: 
                            if (isVirtualRoot) {
                                // a virtual root needs to delegate to the glass version
                                return accessible.getGlassAccessible().Navigate(direction);
                            } else {
                                return accessible.getVirtualAccessible(element.getParent()).getNativeAccessible();
                            } 

                        case NavigateDirection_NextSibling:
                            if (isVirtualRoot) {
                                // a virtual root needs to delegate to the glass version
                                return accessible.getGlassAccessible().Navigate(direction);
                            } else {
                                List<IUIAElement> siblings = element.getParent().getChildren();
                                int index = siblings.indexOf(element);
                                int next = index + 1;
                                if (next < siblings.size()) {
                                    log(element, "Next Sibling: " + siblings.get(next));
                                    return accessible.getVirtualAccessible(siblings.get(next)).getNativeAccessible();
                                } else {
                                    return 0L;
                                }
                            }
                            case NavigateDirection_PreviousSibling:
                            if (isVirtualRoot) {
                                // a virtual root needs to delegate to the glass version
                                return accessible.getGlassAccessible().Navigate(direction);
                            } else {
                                List<IUIAElement> siblings = element.getParent().getChildren();
                                int index = siblings.indexOf(element);
                                int prev = index - 1;
                                if (prev >= 0) {
                                    log(element, "Prev Sibling: " + siblings.get(prev));
                                    return accessible.getVirtualAccessible(siblings.get(prev)).getNativeAccessible();
                                } else {
                                    return 0L;
                                }
                            }
                        
                        default:
                        return 0L;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0L;
                }
            }
        }

        if (accessible.getGlassAccessible() != null) {
            // no node means glass should handle it
            //System.err.println("Delegating navigate to glass: " + accessible.getGlassAccessible());
            return accessible.getGlassAccessible().Navigate(direction);
        }

        log(element, "FALLTHROGH PANIK!!"); 
        System.err.println(accessible);
        System.err.println(element);
        Thread.dumpStack();
        return 0L;
    }

    @Override
    public void SetFocus() {
        provider.SetFocus();
    }

    @Override
    public long GetPatternProvider(int patternId) {

        // TODO we should return here for the default patterns that are always provided without asking glass
        log(provider, "GetPatternProvider("+StandardPatternIds.fromNativeValue(patternId)+")");

        if (StandardPatternIds.UIA_TogglePatternId.getNativeValue() == patternId) {
            return getNativeProvider(NativeIToggleProvider.class) != null ? accessible.getNativeAccessible() : 0L;
        }

        if (StandardPatternIds.UIA_WindowPatternId.getNativeValue() == patternId) {
            return getNativeProvider(NativeIWindowProvider.class) != null ? accessible.getNativeAccessible() : 0L;
        }

        if (StandardPatternIds.UIA_InvokePatternId.getNativeValue() == patternId) {
            return getNativeProvider(NativeIInvokeProvider.class) != null ? accessible.getNativeAccessible() : 0L;
        }

        // fall back to glass
        return accessible.getGlassAccessibleRoot().GetPatternProvider(patternId);


//       IUIAElement element = accessible.getUIAElement();
//       if (element != null && element.isVirtual()) {
//           log(element, "GetPatternProvider("+patternId+")");
//       }
//       //System.err.println("GetPatternProvider("+patternId+") -> " + accessible.getGlassAccessibleRoot());
//       return accessible.getGlassAccessibleRoot().GetPatternProvider(patternId);

    }

    @Override
    public long get_HostRawElementProvider() {
        IUIAElement element = accessible.getUIAElement();
        if (element != null && element.isVirtual()) {
            // virtual node
            //log(node, "get_HostRawElementProvider() -> 0L");
            return 0L;
        } else {
            //System.err.println("get_HostRawElementProvider() -> " + accessible.getGlassAccessibleRoot());
            return accessible.getGlassAccessibleRoot().get_HostRawElementProvider();
        }
    }

    @Override
    public WinVariant GetPropertyValue(int propertyId) {
        // TODO
        IUIAElement element = accessible.getUIAElement();
        if (element != null) {
            String pid = StandardPropertyIds.fromNativeValue(propertyId).map(Object::toString).orElse("" + propertyId);
            log(element, "GetPropertyValue("+pid+") for " + element);


            // this is for now for debugging
            if (StandardPropertyIds.UIA_HelpTextPropertyId.getNativeValue() == propertyId) {
                return Convert.variantString("UIANode: " + element);
            }

            if (element.isVirtual()) {
                if (StandardPropertyIds.UIA_AutomationIdPropertyId.getNativeValue() == propertyId) {
                    return Convert.variantString(element.getAutomationId());
                }
            }

            if (StandardPropertyIds.UIA_ControlTypePropertyId.getNativeValue() == propertyId) {
                if (element.getControlType() != null) {
                    return Convert.variantEnum(element.getControlType());
                }
            }
            /*
            if (PropertyId.UIA_BoundingRectanglePropertyId.getNativeValue() == propertyId) {
                if (node.boundsProperty() != null) {
                    return Convert.variantBounds(node.boundsProperty().get());
                }
            }
            */
        }
        //System.err.println("GetPropertyValue("+propertyId+") -> " + accessible.getGlassAccessibleRoot());
        WinVariant result = accessible.getGlassAccessibleRoot().GetPropertyValue(propertyId);
        String pid = StandardPropertyIds.fromNativeValue(propertyId).map(Object::toString).orElse("" + propertyId);
        //System.err.println(pid + " => " + result);
        return result;
    }
    
}
