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
package javafx.uia.agent;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;

import at.bestsolution.uia.agent.internal.AgentLogger;
import at.bestsolution.uia.agent.internal.AgentLoggerFactory;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;

public class UIAPlatformAgent {

    private static final AgentLogger LOG = AgentLoggerFactory.create(UIAPlatformAgent.class);

    static {
        at.bestsolution.uia.agent.Lib.reportVersionInfo();
    }
    private static Optional<Class<?>> findClass(String clazz) {
        try {
            return Optional.of(Class.forName(clazz));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }
    private static Optional<Module> findModuleFromClass(String clazz) {
        return findClass(clazz).map(Class::getModule);
    }

    private static void fixModules(Instrumentation instrumentation) {
        LOG.debug(() -> "patching module exports / opens");

        // Class<?> factory = findClass("at.bestsolution.uia.core.AccessibleFactory").orElseThrow();

        Module coreModule = findModuleFromClass("at.bestsolution.uia.core.AccessibleFactory").orElseThrow();
        LOG.info(() -> "loaded " + coreModule);
        Module platformModule = findModuleFromClass("at.bestsolution.uia.UIA").orElseThrow();
        LOG.info(() -> "loaded " + platformModule);

        Module graphicsModule =  ModuleLayer.boot().findModule("javafx.graphics").orElseThrow();
        LOG.info(() -> "loaded " + graphicsModule);

        Set<Module> extraReads = new HashSet<>();
        extraReads.add(coreModule);

        Map<String, Set<Module>> extraExports = new HashMap<>();
        extraExports.put("com.sun.glass.ui", new HashSet<>(Arrays.asList(platformModule)));
        extraExports.put("com.sun.javafx.tk", new HashSet<>(Arrays.asList(platformModule)));
        extraExports.put("com.sun.javafx.tk.quantum", new HashSet<>(Arrays.asList(platformModule)));
        extraExports.put("com.sun.javafx.scene", new HashSet<>(Arrays.asList(platformModule)));
        extraExports.put("com.sun.javafx.stage", new HashSet<>(Arrays.asList(platformModule)));
        extraExports.put("com.sun.javafx.util", new HashSet<>(Arrays.asList(platformModule)));

        Map<String, Set<Module>> extraOpens = new HashMap<>();
        // extraOpens.put("javafx.scene", new HashSet<>(Arrays.asList(platformModule)));

        Set<Class<?>> extraUses = new HashSet<>();
        // extraUses.add(factory);

        instrumentation.redefineModule(graphicsModule,
            extraReads,
            extraExports,
            extraOpens,
            extraUses,
            Collections.emptyMap());

    }

    public static void premain(String agentArgument, Instrumentation instrumentation) {
        LOG.trace(() -> "agent init");
        try {
            File file = LibraryManager.coreJar.toFile();
            LOG.debug(() -> "adding UIAPlatform.core.jar to Classloader ("+file+")");
            instrumentation.appendToSystemClassLoaderSearch(new JarFile(file, true));

           fixModules(instrumentation);
            // ModuleFinder.ofSystem().findAll().forEach(m -> System.out.println("module: " + m));
            // instrumentation.redefineModule(null, null, null, null, null, null);

            Class<?> cls = ClassLoader.getSystemClassLoader().loadClass("at.bestsolution.uia.core.AccessibleFactory");
            LOG.debug(() -> "cls: " + cls);
            // cls.getMethod("foo", new Class<?>[0]);
            //Class<?> c = java.lang.System.out.println(java.lang.ClassLoader.getSystemClassLoader().loadClass("at.bestsolution.uia.AccessibleFactory")).getMethod("createAccessible").invoke(null)

        } catch (Exception e) {
            LOG.fatal(() -> "Could not add UIAPlatform.core.jar to classloader^", e);
            throw new RuntimeException("Could not add UIAPlatform.core.jar to ClassLoader", e);
        }

        instrumentation.addTransformer(createTransformer());



        // add to ext class loader
        // try {
        //     URLClassLoader extClassLoader = findExtClassLoader();
        //     addURL(extClassLoader, LibraryManager.coreJar.toUri().toURL());
        // } catch (Exception e) {
        //     LOG.fatal(() -> "Could not add UIAPlatform.core.jar to Ext ClassLoader", e);
        //     throw new RuntimeException("Could not add UIAPlatform.core.jar to Ext ClassLoader", e);
        // }

    }

    // static boolean addClassPath(File f) {
    //     ClassLoader cl = ClassLoader.getSystemClassLoader();

    //     try {
    //         // If Java 9 or higher use Instrumentation
    //         if (!(cl instanceof URLClassLoader)) {
    //             inst.appendToSystemClassLoaderSearch(new JarFile(f));
    //             return;
    //         }

    //         // If Java 8 or below fallback to old method
    //         Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
    //         m.setAccessible(true);
    //         m.invoke(cl, (Object)f.toURI().toURL());
    //     } catch (Throwable e) { e.printStackTrace(); }
    // }

    // static void addURL(URLClassLoader classLoader, URL url) throws Exception {
    //     try {
    //         Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
    //         addUrl.setAccessible(true);
    //         addUrl.invoke(classLoader, url);
    //     } catch (Exception e) {
    //         LOG.error(() -> "Could not add URL to URLClassLoader", e);
    //         throw new Exception("Could not add URL to URLClassLoader" , e);
    //     }
    // }

    static URLClassLoader findExtClassLoader() throws Exception {
        ClassLoader cur = UIAPlatformAgent.class.getClassLoader();
        while (cur != null) {
            String c = cur.getClass().getName();
            LOG.fatal(() -> "CLASSLOADER: " + c);
            if (cur.getClass().getName().contains("Ext")) {
                if (cur instanceof URLClassLoader) {
                    return (URLClassLoader) cur;
                }
            }
            cur = cur.getParent();
        }
        LOG.error(() -> "Ext Classloader not found!");
        throw new Exception("Ext ClassLoader not found!");
    }

    private static final ScopedClassPoolFactoryImpl scopedClassPoolFactory = new ScopedClassPoolFactoryImpl();

    static ClassFileTransformer createTransformer() {

        return new ClassFileTransformer() {

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                    ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                        if ("com/sun/glass/ui/win/WinApplication".equals(className)) {
                            try {
                                // java.lang.reflect.Method m;
                                // m.invoke
                                LOG.debug(() -> "instrumenting JavaFX WinApplication#createAccessible");
                                ClassPool classPool = scopedClassPoolFactory.create(loader, ClassPool.getDefault(), ScopedClassPoolRepositoryImpl.getInstance());
                                // add to ClassPool for compiling
                                classPool.appendClassPath(LibraryManager.coreJar.toString());
                                classPool.appendSystemPath();

                                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
                                CtMethod ctMethod = ctClass.getDeclaredMethod("createAccessible");

                                // TODO: produces java.lang.NoClassDefFoundError: at/bestsolution/uia/core/AccessibleFactory

                                // the below reflective approach works
                                // String src = "return at.bestsolution.uia.core.AccessibleFactory.createAccessible();";
                                String src = """
                                        {
                                            java.lang.ClassLoader loader = java.lang.ClassLoader.getSystemClassLoader();
                                            java.lang.Class cls = loader.loadClass("at.bestsolution.uia.core.AccessibleFactory");
                                            // java.lang.Class cls = Class.forName("at.bestsolution.uia.core.AccessibleFactory");
                                            // java.lang.Class[] args = {};
                                            java.lang.reflect.Method m = cls.getMethod("createAccessible", new java.lang.Class[0]);
                                            //java.lang.Object[] invoke_args = {};
                                            return (com.sun.glass.ui.Accessible) m.invoke(null, new java.lang.Object[0]);
                                        }
                                        """;
                                ctMethod.setBody(src);

                                // ctMethod.setBody("{ return java.lang.ClassLoader.getSystemClassLoader().loadClass(\"at.bestsolution.uia.AccessibleFactory\").getMethod(\"createAccessible\", new java.lang.Class<java.lang.Object>[0]).invoke(null); }");
                                // ctMethod.setBody("{ java.lang.System.out.println(java.lang.ClassLoader.getSystemClassLoader().loadClass(\"at.bestsolution.uia.AccessibleFactory\")); return at.bestsolution.uia.AccessibleFactory.createAccessible(); }");
                                return ctClass.toBytecode();

                            } catch (Exception e) {
                                LOG.fatal(() -> "Instrumentation failed. openfx-uia not available.", e);
                            }
                        }

                return classfileBuffer;
            }

        };

    }

}
