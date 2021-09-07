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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import com.sun.glass.ui.uia.ProxyAccessible;

import javafx.uia.IExpandCollapseProvider;
import javafx.uia.IGridItemProvider;
import javafx.uia.IGridProvider;
import javafx.uia.IInitContext;
import javafx.uia.IInvokeProvider;
import javafx.uia.IPatternId;
import javafx.uia.IScrollItemProvider;
import javafx.uia.IScrollProvider;
import javafx.uia.ISelectionItemProvider;
import javafx.uia.ISelectionProvider;
import javafx.uia.ISelectionProvider2;
import javafx.uia.ITableItemProvider;
import javafx.uia.ITableProvider;
import javafx.uia.ITextChildProvider;
import javafx.uia.ITextProvider;
import javafx.uia.IToggleProvider;
import javafx.uia.ITransformProvider;
import javafx.uia.IUIAElement;
import javafx.uia.IWindowProvider;
import javafx.uia.StandardPatternIds;

public class ProviderDescriptor<JavaType, NativeType> {
    
    static class Registry {

        final static List<ProviderDescriptor<?, ?>> descriptors = new ArrayList<>();
        final static Map<IPatternId, ProviderDescriptor<?, ?>> byId = new HashMap<>();

        static <JavaType, NativeType> void register(IPatternId pattern, Class<JavaType> javaType, Class<NativeType> nativeType, BiFunction<ProxyAccessible, JavaType, NativeType> factory) {
            ProviderDescriptor<JavaType, NativeType> desc = new ProviderDescriptor<>(pattern, javaType, nativeType, factory);
            descriptors.add(desc);
            byId.put(desc.pattern, desc);
        }
        static {
            register(StandardPatternIds.UIA_TogglePatternId, IToggleProvider.class, NativeIToggleProvider.class, ToggleProviderAdapter::new);
            register(StandardPatternIds.UIA_WindowPatternId, IWindowProvider.class, NativeIWindowProvider.class, WindowProviderAdapter::new);
            register(StandardPatternIds.UIA_InvokePatternId, IInvokeProvider.class, NativeIInvokeProvider.class, InvokeProviderAdapter::new);
            register(StandardPatternIds.UIA_TextPatternId, ITextProvider.class, NativeITextProvider.class, TextProviderAdapter::new);
            
            register(StandardPatternIds.UIA_TextChildPatternId, ITextChildProvider.class, NativeITextChildProvider.class, TextChildProviderAdapter::new);

            register(StandardPatternIds.UIA_ScrollPatternId, IScrollProvider.class, NativeIScrollProvider.class, ScrollProviderAdapter::new);
            register(StandardPatternIds.UIA_ScrollItemPatternId, IScrollItemProvider.class, NativeIScrollItemProvider.class, ScrollItemProviderAdapter::new);

            register(StandardPatternIds.UIA_GridPatternId, IGridProvider.class, NativeIGridProvider.class, GridProviderAdapter::new);
            register(StandardPatternIds.UIA_GridItemPatternId, IGridItemProvider.class, NativeIGridItemProvider.class, GridItemProviderAdapter::new);

            register(StandardPatternIds.UIA_TablePatternId, ITableProvider.class, NativeITableProvider.class, TableProviderAdapter::new);
            register(StandardPatternIds.UIA_TableItemPatternId, ITableItemProvider.class, NativeITableItemProvider.class, TableItemProviderAdapter::new);

            register(StandardPatternIds.UIA_SelectionPatternId, ISelectionProvider.class, NativeISelectionProvider.class, SelectionProviderAdapter::new);
            register(StandardPatternIds.UIA_SelectionPattern2Id, ISelectionProvider2.class, NativeISelectionProvider2.class, SelectionProvider2Adapter::new);
            register(StandardPatternIds.UIA_SelectionItemPatternId, ISelectionItemProvider.class, NativeISelectionItemProvider.class, SelectionItemProviderAdapter::new);

            register(StandardPatternIds.UIA_ExpandCollapsePatternId, IExpandCollapseProvider.class, NativeIExpandCollapseProvider.class, ExpandCollapseProviderAdapter::new);

            register(StandardPatternIds.UIA_TransformPatternId, ITransformProvider.class, NativeITransformProvider.class, TransformProviderAdapter::new);
        }

        static <JavaType, NativeType> boolean isAvailable(IPatternId id) {
            return byId.containsKey(id);
        }

        @SuppressWarnings("unchecked")
        static <JavaType, NativeType> ProviderDescriptor<JavaType, NativeType> getDescriptor(IPatternId id) {
            return (ProviderDescriptor<JavaType, NativeType>) byId.get(id);
        }

        static <JavaType, NativeType> NativeType createNativeProvider(IPatternId id, ProxyAccessible accessible, JavaType javaProvider) {
            ProviderDescriptor<JavaType, NativeType> desc = getDescriptor(id);
            return desc.factory.apply(accessible, javaProvider);
        }

        static List<ProviderDescriptor<?, ?>> getAvailable() {
            return Collections.unmodifiableList(descriptors);
        }

    }

    public final IPatternId pattern;
    public final Class<NativeType> nativeType;
    public final Class<JavaType> javaType;
    public final BiFunction<ProxyAccessible, JavaType, NativeType> factory;

    private ProviderDescriptor(IPatternId pattern, Class<JavaType> javaType, Class<NativeType> nativeType, BiFunction<ProxyAccessible, JavaType, NativeType> factory) {
        this.pattern = pattern;
        this.javaType = javaType;
        this.nativeType = nativeType;
        this.factory = factory;
    }

    public NativeType createAdapter(ProxyAccessible accessible, JavaType javaInstance) {
        return factory.apply(accessible, javaInstance);
    }

    public ProviderRegistry.ProviderInstance<JavaType, NativeType> createInstance(IInitContext init, ProxyAccessible accessible, IUIAElement element) {
        JavaType javaInstance = element.getProvider(javaType);
        return new ProviderRegistry.ProviderInstance<>(init, accessible, this, element, Objects.requireNonNull(javaInstance));
    }

}
