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
#ifndef _JNI_UTIL_
#define _JNI_UTIL_

#include "common.h"
#include "ProxyAccessible.h"
#include "ProxyTextRangeProvider.h"

#include <UIAutomationCore.h>

#define print_hr(hr) LPVOID buf; \
    ::FormatMessageA(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL, hr, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPSTR) &buf, 0, NULL); \
    fprintf(stderr, "[HR FAIL] %s:%d, hr=%#010x (%s)\n", __FUNCTION__, __LINE__, hr, (char*) buf); \
    ::LocalFree(buf);

#define print_on_fail(hr) if (!SUCCEEDED(hr)) { \
  print_hr(hr); \
  }

#define throw_on_fail(hr) if (!SUCCEEDED(hr)) { \
  print_hr(hr); \
  throw hr; \
  }

namespace jni {

  jstring call_toString(JNIEnv* env, jobject obj);

  namespace ids {
    HRESULT initIDs(JNIEnv* env);
  }

  class LocalFrame {
  public:
    LocalFrame(JNIEnv* env, jint size);
    ~LocalFrame();
  private:
    JNIEnv* env;
  };

jboolean _boolean(BOOL value);
jlong _long(long value);
jint _int(int value);
jlong _ptr(void* value);
jfloat _float(float value);
jdouble _double(double value);

jobject _object(ITextRangeProvider* textRange);

jstring _string0(JNIEnv* env, BSTR value);
jstring _string0(JNIEnv* env, LPCWSTR value);

bool isHResultException(JNIEnv* env, jobject t);
HRESULT getHResult(JNIEnv* env, jobject t);


jstring call_string(JNIEnv* env, jobject obj, jmethodID mid, ...);
void call_void(JNIEnv* env, jobject obj, jmethodID mid, ...);
jboolean call_bool(JNIEnv* env, jobject obj, jmethodID mid, ...);
jint call_int(JNIEnv* env, jobject obj, jmethodID mid, ...);
jlong call_long(JNIEnv* env, jobject obj, jmethodID mid, ...);
jfloat call_float(JNIEnv* env, jobject obj, jmethodID mid, ...);
jdouble call_double(JNIEnv* env, jobject obj, jmethodID mid, ...);
jobject call_object(JNIEnv* env, jobject obj, jmethodID mid, ...);
jarray call_array(JNIEnv* env, jobject obj, jmethodID mid, ...);
ProxyAccessible* call_accessible(JNIEnv* env, jobject obj, jmethodID mid, ...);
ProxyTextRangeProvider* call_textrange(JNIEnv* env, jobject obj, jmethodID mid, ...);

void assert_arg(void* arg);
void check_and_throw(JNIEnv* env);

void fill_bstr(JNIEnv* env, jstring value, BSTR* pResult);
void fill_safearray(JNIEnv* env, jarray data, VARTYPE vt, SAFEARRAY** ppResult);
void fill_safearray_no_addref(JNIEnv* env, jarray data, VARTYPE vt, SAFEARRAY** ppResult);
void fill_variant(JNIEnv* env, jobject variant, VARIANT* pResult);
void fill_bounds(JNIEnv* env, jfloatArray bounds, UiaRect* pResult);

void release_all_iunknown(SAFEARRAY* array);
JNIEnv* get_env();

}

#endif _JNI_UTIL_