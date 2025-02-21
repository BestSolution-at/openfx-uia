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
package uia.sample.samples;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.bestsolution.uia.javafx.uia.ControlType;
import at.bestsolution.uia.javafx.uia.IAnnotationProvider;
import at.bestsolution.uia.javafx.uia.IDockProvider;
import at.bestsolution.uia.javafx.uia.IExpandCollapseProvider;
import at.bestsolution.uia.javafx.uia.IGridItemProvider;
import at.bestsolution.uia.javafx.uia.IGridProvider;
import at.bestsolution.uia.javafx.uia.IInvokeProvider;
import at.bestsolution.uia.javafx.uia.IMultipleViewProvider;
import at.bestsolution.uia.javafx.uia.IRangeValueProvider;
import at.bestsolution.uia.javafx.uia.IScrollItemProvider;
import at.bestsolution.uia.javafx.uia.IScrollProvider;
import at.bestsolution.uia.javafx.uia.ISelectionItemProvider;
import at.bestsolution.uia.javafx.uia.ISelectionProvider;
import at.bestsolution.uia.javafx.uia.ISelectionProvider2;
import at.bestsolution.uia.javafx.uia.IStylesProvider;
import at.bestsolution.uia.javafx.uia.ISynchronizedInputProvider;
import at.bestsolution.uia.javafx.uia.ITableItemProvider;
import at.bestsolution.uia.javafx.uia.ITableProvider;
import at.bestsolution.uia.javafx.uia.ITextChildProvider;
import at.bestsolution.uia.javafx.uia.ITextEditProvider;
import at.bestsolution.uia.javafx.uia.ITextProvider;
import at.bestsolution.uia.javafx.uia.ITextProvider2;
import at.bestsolution.uia.javafx.uia.IToggleProvider;
import at.bestsolution.uia.javafx.uia.ITransformProvider;
import at.bestsolution.uia.javafx.uia.ITransformProvider2;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.javafx.uia.IValueProvider;
import at.bestsolution.uia.javafx.uia.IVirtualizedItemProvider;
import at.bestsolution.uia.javafx.uia.IWindowProvider;
import at.bestsolution.uia.javafx.uia.UIA;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class AllDummyProviders implements Sample {

    static class Handler implements InvocationHandler {

        Node node;
        Class<?> provider;

        public Handler(Node node, Class<?> provider) {
            this.node = node;
            this.provider = provider;
        }

        private static int nextId = 1;
		private static Map<Integer, Integer> idMap = new HashMap<>();

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            if ("getBounds".equals(method.getName())) {
                return node.localToScreen(node.getLayoutBounds());
            }
            if ("getChildren".equals(method.getName())) {
                return Collections.emptyList();
            }
            if ("isProviderAvailable".equals(method.getName())) {
                return ((Class<?>)args[0]).isAssignableFrom(provider);
            }
            if ("getProvider".equals(method.getName())) {
                return proxy;
            }
            if ("getId".equals(method.getName())) {
                int hash = System.identityHashCode(proxy);
                return idMap.computeIfAbsent(hash, h -> nextId++);
            }
            if ("hashCode".equals(method.getName())) {
                return System.identityHashCode(proxy);
            }
            if ("toString".equals(method.getName())) {
                return "Dummy proxy for " + provider;
            }

            switch(method.getName()) {
                case "getControlType": return ControlType.UIA_CustomControlTypeId;
            }


            System.err.println(provider + "#" + method + " was called!");

            if (method.getReturnType().equals(int.class)) {
                return 0;
            }
            if (method.getReturnType().equals(double.class)) {
                return 0d;
            }
            if (method.getReturnType().equals(boolean.class)) {
                return false;
            }


            return null;
        }

    }

    IUIAElement create(Node node, Class<?> provider) {
        return (IUIAElement) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[] {IUIAVirtualRootElement.class, provider}, new Handler(node, provider));
    }

    Label description;
    VBox sample;


    public AllDummyProviders() {

        List<Class<?>> allProviders = new ArrayList<>();
        allProviders.add(IAnnotationProvider.class);
        allProviders.add(IDockProvider.class);
        //allProviders.add(IDragProvider.class);
        //allProviders.add(IDropTargetProvider.class);
        allProviders.add(IExpandCollapseProvider.class);
        allProviders.add(IGridItemProvider.class);
        allProviders.add(IGridProvider.class);
        allProviders.add(IInvokeProvider.class);
        //allProviders.add(IItemContainerProvider.class);
        allProviders.add(IMultipleViewProvider.class);
        allProviders.add(IRangeValueProvider.class);
        allProviders.add(IScrollItemProvider.class);
        allProviders.add(IScrollProvider.class);
        allProviders.add(ISelectionItemProvider.class);
        allProviders.add(ISelectionProvider.class);
        allProviders.add(ISelectionProvider2.class);
        allProviders.add(IStylesProvider.class);
        allProviders.add(ISynchronizedInputProvider.class);
        allProviders.add(ITableItemProvider.class);
        allProviders.add(ITableProvider.class);
        allProviders.add(ITextChildProvider.class);
        allProviders.add(ITextEditProvider.class);
        allProviders.add(ITextProvider.class);
        allProviders.add(ITextProvider2.class);
        allProviders.add(IToggleProvider.class);
        allProviders.add(ITransformProvider.class);
        allProviders.add(ITransformProvider2.class);
        allProviders.add(IValueProvider.class);
        allProviders.add(IVirtualizedItemProvider.class);
        allProviders.add(IWindowProvider.class);


        Label desc = new Label("dummy providers with noop implementations");
        desc.setWrapText(true);
        description = desc;

        sample = new VBox();

        for (Class<?> provider : allProviders) {
            Button label = new Button(provider.getSimpleName()) {
                IUIAElement el = create(this, provider);
                @Override
                public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                    if (UIA.isUIAQuery(attribute, parameters)) {
                        return el;
                    }
                    return super.queryAccessibleAttribute(attribute, parameters);
                }
            };
            sample.getChildren().add(label);
        }

    }

    @Override
    public String getName() {
        return "All Dummy Providers";
    }

    @Override
    public Node getDescription() {
        return description;
    }

    @Override
    public Node getSample() {
        return sample;
    }

    @Override
    public Node getControls() {
        return null;
    }

}
