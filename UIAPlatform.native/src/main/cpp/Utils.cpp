/*
 * Copyright (c) 2011, 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "common.h"

#include "com_sun_glass_events_KeyEvent.h"

 /*
  * Initialize the Java VM instance variable when the library is
  * first loaded
  */
static JavaVM* jvm;

JavaIDs javaIDs;

JavaVM* GetJVM()
{
    return jvm;
}

JNIEnv* GetEnv()
{
    void* env;
    jvm->GetEnv(&env, JNI_VERSION_1_2);
    return (JNIEnv*)env;
}

HRESULT GetEnv(JNIEnv** pResult) {
  JNIEnv* env;
  jint rc = jvm->GetEnv((void**) &env, JNI_VERSION_1_2);
  if (0 != rc) return E_FAIL;
  if (env == NULL) return E_FAIL;
  *pResult = env;
  return S_OK;
}

jboolean CheckAndClearException(JNIEnv* env)
{
    jthrowable t = env->ExceptionOccurred();
    if (!t) {
        return JNI_FALSE;
    }
    fprintf(stderr, "CheckAndClearException[\n");
    env->ExceptionDescribe();
    fprintf(stderr, "]\n");
    env->ExceptionClear();

    
    // output exception
    jclass tc = env->FindClass("java/lang/Throwable");
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        fprintf(stderr, "FAIL_A\n");
        return JNI_TRUE;
    }
    jmethodID printMID = env->GetMethodID(tc, "printStackTrace", "()V");
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        fprintf(stderr, "FAIL_B\n");
        return JNI_TRUE;
    }
    fprintf(stderr, "CheckAndClearException[\n");
    env->CallVoidMethod(t, printMID);
    fprintf(stderr, "]\n");
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        fprintf(stderr, "FAIL_C\n");
        return JNI_TRUE;
    }
    // ------


    jclass cls = env->FindClass("com/sun/glass/ui/Application");
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        fprintf(stderr, "FAIL_D\n");
        return JNI_TRUE;
    }
    env->CallStaticVoidMethod(cls, javaIDs.Application.reportExceptionMID, t);
    if (env->ExceptionOccurred()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        fprintf(stderr, "FAIL_E\n");
        return JNI_TRUE;
    }
    env->DeleteLocalRef(cls);

    return JNI_TRUE;
}

jint GetModifiers()
{
    jint modifiers = 0;
    if (HIBYTE(::GetKeyState(VK_CONTROL)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_CONTROL;
    }
    if (HIBYTE(::GetKeyState(VK_SHIFT)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_SHIFT;
    }
    if (HIBYTE(::GetKeyState(VK_MENU)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_ALT;
    }
    if (HIBYTE(::GetKeyState(VK_LWIN)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_WINDOWS;
    }
    if (HIBYTE(::GetKeyState(VK_RWIN)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_WINDOWS;
    }
    if (HIBYTE(::GetKeyState(VK_MBUTTON)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_BUTTON_MIDDLE;
    }
    if (HIBYTE(::GetKeyState(VK_RBUTTON)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_BUTTON_SECONDARY;
    }
    if (HIBYTE(::GetKeyState(VK_LBUTTON)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_BUTTON_PRIMARY;
    }
    /* 
    
    XXX not in openjfx8

    if (HIBYTE(::GetKeyState(VK_XBUTTON1)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_BUTTON_BACK;
    }
    if (HIBYTE(::GetKeyState(VK_XBUTTON2)) != 0) {
        modifiers |= com_sun_glass_events_KeyEvent_MODIFIER_BUTTON_FORWARD;
    }*/

    return modifiers;
}

extern "C" {

#ifdef STATIC_BUILD
    JNIEXPORT jint JNICALL JNI_OnLoad_glass(JavaVM* vm, void* reserved)
#else
    JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
#endif
    {
        memset(&javaIDs, 0, sizeof(javaIDs));
        jvm = vm;
        return JNI_VERSION_1_2;
    }

} // extern "C"