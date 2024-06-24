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
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.scopedpool.ScopedClassPoolFactoryImpl;
import javassist.scopedpool.ScopedClassPoolRepositoryImpl;

public class UIAPlatformAgent {

    static {
        at.bestsolution.uia.agent.Lib.reportVersionInfo();
    }
    
    public static void premain(String agentArgument, Instrumentation instrumentation) {
        Logger.debug(UIAPlatformAgent.class, () -> "agent init");
        instrumentation.addTransformer(createTransformer());

        // add to ext class loader
        try {
            URLClassLoader extClassLoader = findExtClassLoader();
            addURL(extClassLoader, LibraryManager.coreJar.toUri().toURL());
        } catch (Exception e) {
            throw new RuntimeException("Could not add UIAPlatform.core.jar to Ext ClassLoader", e);
        }

    }

    static void addURL(URLClassLoader classLoader, URL url) throws Exception {
        try {
            Method addUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            addUrl.setAccessible(true);
            addUrl.invoke(classLoader, url);
        } catch (Exception e) {
            throw new Exception("Could not add URL to URLClassLoader" , e);
        }
    }

    static URLClassLoader findExtClassLoader() throws Exception {
        ClassLoader cur = UIAPlatformAgent.class.getClassLoader();
        while (cur != null) {
            if (cur.getClass().getName().contains("Ext")) {
                if (cur instanceof URLClassLoader) {
                    return (URLClassLoader) cur;
                }
            }
            cur = cur.getParent();
        }
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
                                Logger.debug(UIAPlatformAgent.class, () -> "instrumenting JavaFX WinApplication#createAccessible");
                                ClassPool classPool = scopedClassPoolFactory.create(loader, ClassPool.getDefault(), ScopedClassPoolRepositoryImpl.getInstance());  
                                // add to ClassPool for compiling
                                classPool.appendClassPath(LibraryManager.coreJar.toString());

                                CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));  
                                CtMethod ctMethod = ctClass.getDeclaredMethod("createAccessible"); 
                                ctMethod.setBody("{ return com.sun.glass.ui.uia.AccessibleFactory.createAccessible(); }");
                                return ctClass.toBytecode();

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        
                return classfileBuffer;
            }
            
        };

    }

}
