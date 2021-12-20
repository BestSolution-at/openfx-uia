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

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.sun.glass.ui.uia.ProxyAccessible;

import javafx.uia.IInitContext;
import javafx.uia.IInitable;
import javafx.uia.IUIAElement;

public class ProviderRegistry {
    static class ProviderInstance<JavaType, NativeType> {
        final ProviderDescriptor<JavaType, NativeType> descriptor;

        final IUIAElement owner;
        final JavaType javaInstance;
        final Object context;
        final NativeType nativeInstance;

        public ProviderInstance(IInitContext init, ProxyAccessible accessible, ProviderDescriptor<JavaType, NativeType> descriptor, IUIAElement owner, JavaType javaInstance) {
            Objects.requireNonNull(init);
            Objects.requireNonNull(accessible);
            this.descriptor = Objects.requireNonNull(descriptor);
            this.owner =  Objects.requireNonNull(owner);
            this.javaInstance =  Objects.requireNonNull(javaInstance);

            // we only need to call initialize on different instances than the already initialized element
            if (javaInstance != owner) {
                IInitable initable = (IInitable) javaInstance;
                initable.initialize(init);
            }

            this.nativeInstance = descriptor.createAdapter(accessible, javaInstance);
            this.context = initializeContext(init, descriptor.javaType, javaInstance);
        }

        private static Object initializeContext(IInitContext init, Class<?> javaType, Object javaProvider) {
            try {
                String providerName = javaType.getSimpleName().substring(1);
                // initialize the providers context
                // this should turn ISomeProvider to SomeProviderContext which is the naming scheme for the inner context classes
                String contextName = providerName + "Context"; 
                Class<?> contextClass = Class.forName(javaType.getName() + "$" + contextName, true, javaType.getClassLoader());
                Constructor<?> constructor = contextClass.getConstructor(IInitContext.class, javaType);
                
                Object context = constructor.newInstance(init, javaProvider);
                return context;
            } catch (Exception e) {
                RuntimeException e1 =  new RuntimeException("Failed to initialize " + javaType + ": This indicates a bug!", e);
                e1.printStackTrace();
                throw e1;
            }
        }
    }

    private final Map<Class<?>, ProviderInstance<?, ?>> byNativeType = new HashMap<>();
    private final Map<Class<?>, ProviderInstance<?, ?>> byJavaType = new HashMap<>();
    private final Map<Class<?>, ProviderInstance<?, ?>> byContextType = new HashMap<>();
    private final Set<Integer> ids = new HashSet<>();
    
    public void register(ProviderInstance<?, ?> instance) {
        byNativeType.put(instance.descriptor.nativeType, instance);
        byJavaType.put(instance.descriptor.javaType, instance);
        byContextType.put(instance.context.getClass(), instance);
        ids.add(instance.descriptor.pattern.getNativeValue());
    }

    public boolean isProviderAvailable(int patternId) {
        return ids.contains(patternId);
    }

    @SuppressWarnings("unchecked")
    public <NativeType> NativeType getNativeInstance(Class<NativeType> nativeType) {
        ProviderInstance<?, NativeType> instance = (ProviderInstance<?, NativeType>) byNativeType.get(nativeType);
        return instance != null ? instance.nativeInstance : null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(Class<T> contextType) {
        ProviderInstance<?, ?> instance = (ProviderInstance<?, ?>) byContextType.get(contextType);
        return (T) instance.context;
    }
}
