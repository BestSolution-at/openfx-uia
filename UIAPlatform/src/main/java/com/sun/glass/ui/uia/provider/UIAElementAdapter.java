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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import com.sun.glass.ui.uia.Logger;
import com.sun.glass.ui.uia.LoggerFactory;
import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;
import com.sun.glass.ui.uia.glass.WinAccessible;
import com.sun.glass.ui.uia.glass.WinVariant;
import com.sun.glass.ui.uia.provider.ProviderRegistry.ProviderInstance;

import javafx.uia.IActiveTextPositionChangedEvent;
import javafx.uia.IAsyncContentLoadedEvent;
import javafx.uia.IEvent;
import javafx.uia.IEventId;
import javafx.uia.IInitContext;
import javafx.uia.INotificationEvent;
import javafx.uia.IProperty;
import javafx.uia.IPropertyId;
import javafx.uia.IStructureChangedEvent;
import javafx.uia.ITextEditTextChangedEvent;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.IVariantConverter;
import javafx.uia.NotificationKind;
import javafx.uia.NotificationProcessing;
import javafx.uia.StandardPropertyIds;
import javafx.uia.StandardVariantConverters;
import javafx.uia.Variant;

public class UIAElementAdapter extends BaseAdapter<IUIAElement> implements NativeIRawElementProviderSimple, NativeIRawElementProviderFragment {

  private final static Logger LOG = LoggerFactory.create(UIAElementAdapter.class);

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
            LOG.debug(UIAElementAdapter.this, () -> {
                String oldString;
                String newString;
                if (oldValue instanceof IUIAElement[]) {
                    oldString = Arrays.toString((IUIAElement[])oldValue);
                } else {
                    oldString = Objects.toString(oldValue);
                }
                if (newValue instanceof IUIAElement[]) {
                    newString = Arrays.toString((IUIAElement[])newValue);
                } else {
                    newString = Objects.toString(newValue);
                }
                return id + ".fireChanged("+oldString+", "+newString+")";
            });

            // ensure all reported IVirtualUIAElements are initialized
            ensureExists(oldValue);
            ensureExists(newValue);
            UiaRaiseAutomationPropertyChangedEvent(id, converter.toVariant(oldValue), converter.toVariant(newValue));
        }
    }

    private class Event implements IEvent {
        IEventId eventId;
        public Event(IEventId eventId) {
            this.eventId = eventId;
        }
        @Override
        public void fire() {
            LOG.debug(UIAElementAdapter.this, () -> eventId + ".fire()");
            UiaRaiseAutomationEvent(eventId);
        }
    }

    private class NotificationEvent implements INotificationEvent {
        @Override
        public void fire(NotificationKind notificationKind, NotificationProcessing notificaitonProcessing,
                String displayString, String activityId) {
            LOG.debug(UIAElementAdapter.this, () -> "UiaRaiseNotificationEvent("+notificationKind+", "+notificaitonProcessing+", "+displayString+", "+activityId+")");
            UiaRaiseNotificationEvent(notificationKind, notificaitonProcessing, displayString, activityId);
        }
    }

    private void ensureExists(Object o) {
        // TODO do we need to do the same for IUIAElements?
        if (o instanceof IUIAVirtualElement) {
            IUIAVirtualElement element = (IUIAVirtualElement) o;
            boolean created = ProxyAccessibleRegistry.getInstance().ensureExists(accessible, element);
            LOG.debug(this, () -> " * ensureExists("+element+") " + (created ? " -> was created!" : " -> already existed")) ;
        } else if (o instanceof IUIAElement[]) {
            IUIAElement[] elementArray = (IUIAElement[]) o;
            Arrays.stream(elementArray).forEach(el -> ensureExists(el));
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
            return new Event(id);
        }

        @Override
        public INotificationEvent addNotificationEvent() {
            return new NotificationEvent();
        }

        @Override
        public ITextEditTextChangedEvent addTextEditTextChangedEvent() {
            return UIAElementAdapter.this::UiaRaiseTextEditTextChangedEvent;
        }

        @Override
        public IActiveTextPositionChangedEvent addActiveTextPositionChangedEvent() {
            return UIAElementAdapter.this::UiaRaiseActiveTextPositionChangedEvent;
        }

        @Override
        public IAsyncContentLoadedEvent addAsyncContentLoadedEvent() {
            return UIAElementAdapter.this::UiaRaiseAsyncContentLoadedEvent;
        }

        @Override
        public IStructureChangedEvent addStructureChangedEvent() {
            return UIAElementAdapter.this::UiaRaiseStructureChangedEvent;
        }

    };

    private Map<Integer, Prop<?>> installedProps = new HashMap<>();

    private ProviderRegistry providerRegistry = new ProviderRegistry();

    public ProviderRegistry getProviderRegistry() {
        return providerRegistry;
    }

    private final IUIAElement.UIAElementContext ctx;

    public UIAElementAdapter(ProxyAccessible accessible, IUIAElement element) {
        super(accessible, element);

        // element init
        element.initialize(init);

        ctx = new IUIAElement.UIAElementContext(init, element);


        // pattern provider init
        List<ProviderInstance<?, ?>> adapters = new ArrayList<>();
        for (ProviderDescriptor<?, ?> descriptor : ProviderDescriptor.Registry.getAvailable()) {
            if (element.isProviderAvailable(descriptor.javaType)) {
                ProviderInstance<?, ?> providerInstance = descriptor.createInstance(init, accessible, element);
                adapters.add(providerInstance);
                providerRegistry.register(providerInstance);
            }
        }

        LOG.debug(this, () -> "Element init " + element);
        for (ProviderInstance<?, ?> p : adapters) {
            LOG.debug(this, () -> " * " + p.descriptor.javaType);
        }
    }

    public IUIAElement.UIAElementContext getContext() {
        return ctx;
    }


    // private void log(IUIAElement element, String msg) {
    //     ProxyAccessible proxy = ProxyAccessibleRegistry.getInstance().findAccessible(element);
    //     String glass = " (" + (proxy != null ? proxy.getGlassAccessible() : null) + ")";
    //     LOG.debug(this, () -> "V: " + element + glass + " - " + msg);
    // }


    @Override
    public float[] get_BoundingRectangle() {
        return Convert.convertBounds(accessible.getPlatformBounds(provider.getBounds()));
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
        NavRes result = DoNavigate(direction);
        // IUIAElement element = accessible.getUIAElement();
        // if (element != null && (isVirtual(element) || isVirtualRoot(element))) {
        //     log(element, "Navigate("+NavigateDirection.fromNativeValue(direction)+") -> " + result);
        // }
        return result.toLong();
    }

    interface NavRes {
        long toLong();
    }
    class UIANavRes implements NavRes {
        Optional<IUIAElement> data;
        public UIANavRes(Optional<IUIAElement> data) {
            this.data = data;
        }
        @Override
        public String toString() {
            return data.toString();
        }
        @Override
        public long toLong() {
            return data.flatMap(UIAElementAdapter.this::getNative).orElse(0L);
        }
    }
    class NativeNavRes implements NavRes {
        long result;
        public NativeNavRes(long result) {
            this.result = result;
        }
        @Override
        public String toString() {
            return "Glass("+result+")";
        }
        @Override
        public long toLong() {
            return result;
        }
    }
    class WinAccessibleNavRes implements NavRes {
        private WinAccessible accessible;
        public WinAccessibleNavRes(WinAccessible accessible) {
            this.accessible = accessible;
        }
        @Override
        public String toString() {
            return "" + accessible;
        }
        @Override
        public long toLong() {
            return accessible.getPeer();
        }
    }

    public NavRes DoNavigate(int direction) {
        IUIAElement element = accessible.getUIAElement();

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
                            //log(element, "First Child: " + firstChild);
                            return new UIANavRes(firstChild);
                        }
                        case NavigateDirection_LastChild: {
                            Optional<IUIAElement> lastChild = findLastChild(element);
                            //log(element, "Last Child: " + lastChild);
                            return new UIANavRes(lastChild);
                        }

                        case NavigateDirection_Parent:
                            if (isVirtualRoot(element)) {
                                // a virtual root needs to delegate to the glass version
                                // TODO CHECK THIS MESS!!
                                //log(element, "PARENT: " + accessible + " / glass: " + (accessible!=null?""+accessible.getGlassAccessible():"-"));
                                WinAccessible glass = accessible.getGlassAccessible();
                                if (glass == null) {
                                    glass = accessible.getGlassAccessibleRoot();
                                    return new WinAccessibleNavRes(glass);
                                } else {
                                    return new NativeNavRes(glass.Navigate(direction));
                                }
                            } else {
                                Optional<IUIAElement> parent = findParent(element);
                                if (!parent.isPresent()) {
                                    // Runtime panic
                                    LOG.error(element, () -> "parent of " + element + " not found!");
                                }
                                return new UIANavRes(parent);
                            }

                        case NavigateDirection_NextSibling:
                            if (isVirtualRoot(element)) {
                                // System.err.println("NextSibling on VirtualRoot: " + accessible);
                                return new NativeNavRes(accessible.getGlassAccessible().Navigate(direction));
                            }
                            Optional<IUIAElement> nextSibling = findNextSibling(element);
                            // log(element, "Next Sibling: " + nextSibling);
                            return new UIANavRes(nextSibling);
                        case NavigateDirection_PreviousSibling:
                            if (isVirtualRoot(element)) {
                                return new NativeNavRes(accessible.getGlassAccessible().Navigate(direction));
                            }
                            Optional<IUIAElement> prevSibling = findPreviousSibling(element);
                            // log(element, "Prev Sibling: " + prevSibling);
                            return new UIANavRes(prevSibling);

                        default:
                            LOG.warning(element, () -> "DEFAULT FALLTHROUGH!");
                        return new NativeNavRes(0L);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return new NativeNavRes(0L);
                }
            }
        }

        // default handling for non virtual elements
        if (accessible.getGlassAccessible() != null) {
            // no node means glass should handle it
            return new NativeNavRes(accessible.getGlassAccessible().Navigate(direction));
        }

        LOG.error(element, () ->  "FALLTHROGH PANIK!! - this should never happen!");
        Thread.dumpStack();
        return new NativeNavRes(0L);
    }


    @Override
    public void SetFocus() {
        LOG.trace(this, () -> "SetFocus()");
        provider.SetFocus();
    }

    @Override
    public long GetPatternProvider(int patternId) {
        if (providerRegistry.isProviderAvailable(patternId)) {
            // log(provider, "GetPatternProvider("+StandardPatternIds.fromNativeValue(patternId)+")");
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
            return 0L;
        } else {
            return accessible.getGlassAccessibleRoot().get_HostRawElementProvider();
        }
    }

    @Override
    public WinVariant GetPropertyValue(int propertyId) {
        IUIAElement element = accessible.getUIAElement();
        if (element != null) {
            //String pid = StandardPropertyIds.fromNativeValue(propertyId).map(Object::toString).orElse("" + propertyId);
            //log(element, "GetPropertyValue("+pid+") for " + element);

            Prop<?> p = installedProps.get(propertyId);
            if (p != null) {
                return p.getAsVariant().toWinVariant();
            }

            if (StandardPropertyIds.UIA_ProviderDescriptionPropertyId.getNativeValue() == propertyId) {
                return StandardVariantConverters.BSTR.toVariant("OpenFX-UIA Provider").toWinVariant();
            }

        }
        return Variant.vt_empty().toWinVariant();
    }



}
