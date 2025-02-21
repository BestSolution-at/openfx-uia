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
package at.bestsolution.uia.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

import at.bestsolution.uia.ProxyAccessible;
import at.bestsolution.uia.javafx.uia.IAnnotationProvider;
import at.bestsolution.uia.javafx.uia.IDockProvider;
import at.bestsolution.uia.javafx.uia.IExpandCollapseProvider;
import at.bestsolution.uia.javafx.uia.IGridItemProvider;
import at.bestsolution.uia.javafx.uia.IGridProvider;
import at.bestsolution.uia.javafx.uia.IInitContext;
import at.bestsolution.uia.javafx.uia.IInvokeProvider;
import at.bestsolution.uia.javafx.uia.IMultipleViewProvider;
import at.bestsolution.uia.javafx.uia.IPatternId;
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
import at.bestsolution.uia.javafx.uia.IValueProvider;
import at.bestsolution.uia.javafx.uia.IVirtualizedItemProvider;
import at.bestsolution.uia.javafx.uia.IWindowProvider;
import at.bestsolution.uia.javafx.uia.StandardPatternIds;

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
            register(StandardPatternIds.UIA_TextPattern2Id, ITextProvider2.class, NativeITextProvider2.class, TextProviderAdapter2::new);
            register(StandardPatternIds.UIA_TextEditPatternId, ITextEditProvider.class, NativeITextEditProvider.class, TextEditProviderAdapter::new);

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
            register(StandardPatternIds.UIA_TransformPattern2Id, ITransformProvider2.class, NativeITransformProvider2.class, TransformProvider2Adapter::new);

            register(StandardPatternIds.UIA_DockPatternId, IDockProvider.class, NativeIDockProvider.class, DockProviderAdapter::new);

            register(StandardPatternIds.UIA_AnnotationPatternId, IAnnotationProvider.class, NativeIAnnotationProvider.class, AnnotationProviderAdapter::new);

            register(StandardPatternIds.UIA_ValuePatternId, IValueProvider.class, NativeIValueProvider.class, ValueProviderAdapter::new);
            register(StandardPatternIds.UIA_RangeValuePatternId, IRangeValueProvider.class, NativeIRangeValueProvider.class, RangeValueProviderAdapter::new);

            register(StandardPatternIds.UIA_MultipleViewPatternId, IMultipleViewProvider.class, NativeIMultipleViewProvider.class, MultipleViewProviderAdapter::new);

            register(StandardPatternIds.UIA_SynchronizedInputPatternId, ISynchronizedInputProvider.class, NativeISynchronizedInputProvider.class, SynchronizedInputProviderAdapter::new);
            register(StandardPatternIds.UIA_VirtualizedItemPatternId, IVirtualizedItemProvider.class, NativeIVirtualizedItemProvider.class, VirtualizedItemProviderAdapter::new);

            register(StandardPatternIds.UIA_StylesPatternId, IStylesProvider.class, NativeIStylesProvider.class, StylesProviderAdapter::new);
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
