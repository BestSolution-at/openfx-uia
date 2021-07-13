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
import java.util.function.Supplier;

import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;
import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.uia.IEvent;
import javafx.uia.IEventId;
import javafx.uia.IInitContext;
import javafx.uia.IProperty;
import javafx.uia.IPropertyId;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.IVariantConverter;
import javafx.uia.StandardPatternIds;
import javafx.uia.StandardPropertyIds;
import javafx.uia.Variant;

public class UIAElementAdapter extends BaseAdapter<IUIAElement> implements NativeIRawElementProviderSimple, NativeIRawElementProviderFragment {

    private class Prop<T> implements IProperty<T> {
        private IPropertyId id;
        private Supplier<T> getter;
        private IVariantConverter<T> converter;

        private Variant getAsVariant() {
            return converter.toVariant(getter.get());
        }

        public Prop(IPropertyId id, Supplier<T> getter, IVariantConverter<T> converter) {
            this.id = id;
            this.getter = getter;
            this.converter = converter;
        }

        @Override
        public void fireChanged(T oldValue, T newValue) {
            UiaRaiseAutomationPropertyChangedEvent(id, converter.toVariant(oldValue), converter.toVariant(newValue));
        }
    }

    private final IInitContext init = new IInitContext(){
        @Override
        public <T> IProperty<T> addProperty(IPropertyId id, Supplier<T> getter, IVariantConverter<T> converter) {
            Prop<T> p = new Prop<T>(id, getter, converter);
            installedProps.put(id.getNativeValue(), p);
            return p;
        }

        @Override
        public IEvent addEvent(IEventId id) {
            return () -> UiaRaiseAutomationEvent(id);
        }
    };

    private Map<Integer, Prop<?>> installedProps = new HashMap<>();

    private ProviderRegistry providerRegistry = new ProviderRegistry();

    public ProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }

    public UIAElementAdapter(ProxyAccessible accessible, IUIAElement element) {
        super(accessible, element);

        // element init
        element.initialize(init);

        // pattern provider init
        for (ProviderDescriptor<?, ?> descriptor : ProviderDescriptor.Registry.getAvailable()) {
            if (element.isProviderAvailable(descriptor.javaType)) {
                providerRegistry.register(descriptor.createInstance(init, accessible, element));
            }
        }
    }


    private void log(IUIAElement element, String msg) {
        String glass = " (" + ProxyAccessibleRegistry.getInstance().findAccessible(element).getGlassAccessible() + ")";
        System.err.println("V: " + element + glass + " - " + msg);
    }


    @Override
    public float[] get_BoundingRectangle() {
        // TODO should we return javafx bounds?
        return Convert.convertBounds(provider.getBounds());
    }

    @Override
    public long get_FragmentRoot() {
        // JavaFX always uses the scene as fragment root#
        // should we do our own lookup here instead of going through the glass root?
        return accessible.getGlassAccessibleRoot().get_FragmentRoot();
    }

    @Override
    public long[] GetEmbeddedFragmentRoots() {
        // JavaFX always return null, it does not support embedded fragment roots
        return null;
        //return accessible.getGlassAccessibleRoot().GetEmbeddedFragmentRoots();
    }

    private boolean isVirtual(IUIAElement element) {
        return element instanceof IUIAVirtualElement;
    }
    private boolean isVirtualRoot(IUIAElement element) {
        return element instanceof IUIAVirtualRootElement;
    }

    @Override
    public int[] GetRuntimeId() {
        // To not collide with the javafx internal ids we prefix them with a namespace.
        final int OpenFXUiaNamespace = 0xFFFFFFFF;
        final int UiaAppendRuntimeId                  = 3;
        return new int[] {UiaAppendRuntimeId, OpenFXUiaNamespace, provider.getId()};
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

    private <T> Optional<T> next(List<T> list, T cur) {
        int index = list.indexOf(cur);
        int next = index + 1;
        if (next < list.size()) {
            return Optional.of(list.get(next));
        } else {
            return Optional.empty();
        }
    }
    private <T> Optional<T> prev(List<T> list, T cur) {
        int index = list.indexOf(cur);
        int prev = index - 1;
        if (prev >= 0) {
            return Optional.of(list.get(prev));
        } else {
            return Optional.empty();
        }
    }

    private Optional<IUIAElement> getParent(IUIAElement element) {
        if (isVirtual(element)) {
            return Optional.of(((IUIAVirtualElement) element).getParent());
        } else {
            return Optional.empty();
        }
    }
    private Optional<List<IUIAElement>> getChildren(IUIAElement element) {
        if (isVirtual(element)) {
            return  Optional.of(((IUIAVirtualElement) element).getChildren());
        } else if (isVirtualRoot(element)) {
            return  Optional.of(((IUIAVirtualRootElement) element).getChildren());
        } else {
            return Optional.empty();
        }
    }

    private Optional<IUIAElement> findParent(IUIAElement cur) {
        return getParent(cur);
    }
    private Optional<IUIAElement> findFirstChild(IUIAElement cur) {
        return getChildren(cur).flatMap(this::first);
    }
    private Optional<IUIAElement> findLastChild(IUIAElement cur) {
        return getChildren(cur).flatMap(this::last);
    }
    private Optional<IUIAElement> findNextSibling(IUIAElement cur) {
        return getParent(cur).flatMap(this::getChildren).flatMap(children -> next(children, cur));
    }
    private Optional<IUIAElement> findPreviousSibling(IUIAElement cur) {
        return getParent(cur).flatMap(this::getChildren).flatMap(children -> prev(children, cur));
    }

    
    private Optional<Long> getNative(IUIAElement element) {
        return Optional.ofNullable(element).map(el -> {
            ProxyAccessible result = ProxyAccessibleRegistry.getInstance().findFXAccessible(element);
            if (result == null) {
                result = ProxyAccessibleRegistry.getInstance().getVirtualAccessible(accessible, el);
            }
            return result;
        }).map(ProxyAccessible::getNativeAccessible);
    }

    @Override
    public long Navigate(int direction) {
        IUIAElement element = accessible.getUIAElement();
        if (element != null && (isVirtual(element) || isVirtualRoot(element))) {
            log(element, "Navigate("+direction+")");
        }
        // TODO custom UIANode child navigation - how to get the UIANode
        if (element != null) {
            if (isVirtual(element) || isVirtualRoot(element)) {
                final int NavigateDirection_Parent            = 0;
                final int NavigateDirection_NextSibling       = 1;
                final int NavigateDirection_PreviousSibling   = 2;
                final int NavigateDirection_FirstChild        = 3;
                final int NavigateDirection_LastChild         = 4;
                try {
                    // Note: need mapping from UIANode to ProxyAccessible
                    switch (direction) {
                        case NavigateDirection_FirstChild: {
                            Optional<IUIAElement> firstChild = findFirstChild(element);
                            log(element, "First Child: " + firstChild);
                            return firstChild.flatMap(this::getNative).orElse(0L);
                        } 
                        case NavigateDirection_LastChild: {
                            Optional<IUIAElement> lastChild = findLastChild(element);
                            log(element, "Last Child: " + lastChild);
                            return lastChild.flatMap(this::getNative).orElse(0L);
                        }
                    
                        case NavigateDirection_Parent: 
                            if (isVirtualRoot(element)) {
                                // a virtual root needs to delegate to the glass version
                                System.err.println("PARENT: " + accessible + " / glass: " + (accessible!=null?""+accessible.getGlassAccessible():"-"));
                                return accessible.getGlassAccessible().Navigate(direction);
                            } else {
                                Optional<IUIAElement> parent = findParent(element);
                                if (!parent.isPresent()) {
                                    // Runtime panic
                                    log(element, "ERROR parent of " + element + " not found!");
                                }
                                return parent.flatMap(this::getNative).orElse(0L);
                            } 

                        case NavigateDirection_NextSibling:
                            Optional<IUIAElement> nextSibling = findNextSibling(element);
                            log(element, "Next Sibling: " + nextSibling);
                            return nextSibling.flatMap(this::getNative).orElse(0L);
                        case NavigateDirection_PreviousSibling:
                            Optional<IUIAElement> prevSibling = findPreviousSibling(element);
                            log(element, "Prev Sibling: " + prevSibling);
                            return prevSibling.flatMap(this::getNative).orElse(0L);

                        default:
                            log(element, "DEFAULT FALLTHROUGH!");
                        return 0L;

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0L;
                }
            }
        }

        // default handling for non virtual elements
        if (accessible.getGlassAccessible() != null) {
            // no node means glass should handle it
            //System.err.println("Delegating navigate to glass: " + accessible.getGlassAccessible());
            return accessible.getGlassAccessible().Navigate(direction);
        }

        log(element, "FALLTHROGH PANIK!! - this should never happen!"); 
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
        
        if (providerRegistry.isProviderAvailable(patternId)) {
            log(provider, "GetPatternProvider("+StandardPatternIds.fromNativeValue(patternId)+")");
            return accessible.getNativeAccessible();
        } else {
            return 0L;
        }

    }

    @Override
    public long get_HostRawElementProvider() {
        IUIAElement element = accessible.getUIAElement();
        if (element != null && isVirtual(element)) {
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
            
            Prop<?> p = installedProps.get(propertyId);
            if (p != null) {
                return p.getAsVariant().toWinVariant();
            }
        


            // this is for now for debugging
            if (StandardPropertyIds.UIA_HelpTextPropertyId.getNativeValue() == propertyId) {
                return Convert.variantString("UIANode: " + element);
            }

            if (isVirtual(element)) {
                if (StandardPropertyIds.UIA_AutomationIdPropertyId.getNativeValue() == propertyId) {
                    return Convert.variantString(element.getAutomationId());
                }
            }

            if (StandardPropertyIds.UIA_ControlTypePropertyId.getNativeValue() == propertyId) {
                if (element.getControlType() != null) {
                    return Convert.variantEnum(element.getControlType());
                }
            }

            if (StandardPropertyIds.UIA_ProviderDescriptionPropertyId.getNativeValue() == propertyId) {
                return Convert.variantString("OpenFX-UIA Provider");
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


        //WinVariant result = accessible.getGlassAccessibleRoot().GetPropertyValue(propertyId);
        //String pid = StandardPropertyIds.fromNativeValue(propertyId).map(Object::toString).orElse("" + propertyId);
        //System.err.println(pid + " => " + result);
        //return result;

        // no defaults!
        return null;
    }

    
    
}
