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
#include "JniUtil.h"

#include <iostream>


namespace jni { namespace ids {
  /* Variant Field IDs */
  struct VariantIDs {
    jfieldID vt;
    jfieldID iVal;
    jfieldID lVal;
    jfieldID punkVal;
    jfieldID fltVal;
    jfieldID dblVal;
    jfieldID boolVal;
    jfieldID bstrVal;
    jfieldID pDblVal;
    jfieldID pFltVal;

    jfieldID pPunkVal;
    jfieldID pLVal;
  };
  VariantIDs WinVariant;

  struct ObjectIDs {
    jmethodID toString;
  };
  ObjectIDs Object;

  struct HResultExceptionIDs {
    jclass clazz;
    jmethodID getHResult;
    jmethodID isHResultException;
  };
  HResultExceptionIDs HResultException;


  HRESULT initIDs(JNIEnv* env) {
    /* Variant */
    jclass jVariantClass = env->FindClass("at/bestsolution/uia/glass/WinVariant");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.vt = env->GetFieldID(jVariantClass, "vt", "S");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.iVal = env->GetFieldID(jVariantClass, "iVal", "S");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.lVal = env->GetFieldID(jVariantClass, "lVal", "I");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.punkVal = env->GetFieldID(jVariantClass, "punkVal", "J");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.fltVal = env->GetFieldID(jVariantClass, "fltVal", "F");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.dblVal = env->GetFieldID(jVariantClass, "dblVal", "D");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.boolVal = env->GetFieldID(jVariantClass, "boolVal", "Z");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.bstrVal = env->GetFieldID(jVariantClass, "bstrVal", "Ljava/lang/String;");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.pDblVal = env->GetFieldID(jVariantClass, "pDblVal", "[D");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.pFltVal = env->GetFieldID(jVariantClass, "pFltVal", "[F");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.pPunkVal = env->GetFieldID(jVariantClass, "pPunkVal", "[J");
    if (env->ExceptionCheck()) return E_FAIL;
    WinVariant.pLVal = env->GetFieldID(jVariantClass, "pLVal", "[I");
    if (env->ExceptionCheck()) return E_FAIL;

    /* Object */
    jclass jObjectClass = env->FindClass("java/lang/Object");
    if (env->ExceptionCheck()) return E_FAIL;
    Object.toString = env->GetMethodID(jObjectClass, "toString", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return E_FAIL;

    /* HResultException */
    jclass jHResultExceptionClass = env->FindClass("at/bestsolution/uia/HResultException");
    if (env->ExceptionCheck()) return E_FAIL;
    HResultException.clazz = (jclass) env->NewGlobalRef(jHResultExceptionClass);
    if (env->ExceptionCheck()) return E_FAIL;
    HResultException.getHResult = env->GetMethodID(HResultException.clazz, "getHResult", "()I");
    if (env->ExceptionCheck()) return E_FAIL;
    HResultException.isHResultException = env->GetStaticMethodID(HResultException.clazz, "isHResultException", "(Ljava/lang/Throwable;)Z");
    if (env->ExceptionCheck()) return E_FAIL;

    return S_OK;
  }

} }

jni::LocalFrame::LocalFrame(JNIEnv* env, jint size) {
  this->env = env;
  auto res = this->env->PushLocalFrame(size);
  if (res != JNI_OK) {
    jni::check_and_throw(this->env);
    throw E_FAIL;
  }
}
jni::LocalFrame::~LocalFrame() {
  this->env->PopLocalFrame(NULL);
  this->env = nullptr;
}

jstring jni::_string0(JNIEnv* env, BSTR value) {
  auto size = SysStringLen(value);
  auto result = env->NewString((const jchar *)value, size);
  jni::check_and_throw(env);
  return result;
}

jstring jni::_string0(JNIEnv* env, LPCWSTR value) {
  auto size = wcslen(value);
  auto result = env->NewString((const jchar*)value, (jsize)size);
  jni::check_and_throw(env);
  return result;
}

jboolean jni::_boolean(BOOL value) {
  return value == TRUE ? JNI_TRUE : JNI_FALSE;
}
jlong jni::_long(long value) {
  return (jlong) value;
}
jint jni::_int(int value) {
  return (jint) value;
}
jlong jni::_ptr(void* value) {
  return (jlong) value;
}
jfloat jni::_float(float value) {
  return (jfloat) value;
}
jdouble jni::_double(double value) {
  return (jdouble) value;
}

jobject jni::_object(ITextRangeProvider* textRange) {
  return static_cast<ProxyTextRangeProvider*>(textRange)->get_jobject();
}

jstring jni::call_string(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = (jstring) env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jobject jni::call_object(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jarray jni::call_array(JNIEnv* env, jobject obj, jmethodID mid, ...) {
    va_list args;
  va_start(args, mid);
  auto result = (jarray) env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

void jni::call_void(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  env->CallVoidMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
}

jboolean jni::call_bool(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallBooleanMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jint jni::call_int(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallIntMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jlong jni::call_long(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallLongMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jfloat jni::call_float(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallFloatMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

jdouble jni::call_double(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallDoubleMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return result;
}

ProxyAccessible* jni::call_accessible(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallLongMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return reinterpret_cast<ProxyAccessible*>(result);
}

ProxyTextRangeProvider* jni::call_textrange(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  auto result = env->CallLongMethodV(obj, mid, args);
  va_end(args);
  jni::check_and_throw(env);
  return reinterpret_cast<ProxyTextRangeProvider*>(result);
}

void jni::assert_arg(void* arg) {
  if (arg == nullptr) {
    throw E_INVALIDARG;
  }
}

bool jni::isHResultException(JNIEnv* env, jobject t) {
  jboolean result = env->CallStaticBooleanMethod(ids::HResultException.clazz, ids::HResultException.isHResultException, t);

  return result == JNI_TRUE;
}

HRESULT jni::getHResult(JNIEnv* env, jobject t) {
  jint hr = env->CallIntMethod(t, ids::HResultException.getHResult);
  return (HRESULT) hr;
}

void jni::check_and_throw(JNIEnv* env) {
  jthrowable t = env->ExceptionOccurred();
  if (t == nullptr) {
    return;
  }

  if (isHResultException(env, t)) {
    HRESULT hr = getHResult(env, t);
    env->ExceptionClear();
    throw hr;
  }

  // print it
  std::cerr << "check_and_throw(): fallthrough[" << std::endl;
  env->ExceptionDescribe();
   std::cerr << "] returning HRESULT E_JAVAEXCEPTION" << std::endl;
  env->ExceptionClear();

  throw E_JAVAEXCEPTION;
}

void jni::fill_bstr(JNIEnv* env, jstring value, BSTR* pResult) {
  if (value == nullptr) throw E_FAIL;
  auto length = env->GetStringLength(value);
  const jchar* ptr = env->GetStringCritical(value, NULL);
  if (ptr == nullptr) {
    throw E_FAIL;
  } else {
    *pResult = SysAllocStringLen(reinterpret_cast<const OLECHAR*>(ptr), length);
    env->ReleaseStringCritical(value, ptr);
  }
  jni::check_and_throw(env);
}

void jni::fill_safearray(JNIEnv* env, jarray data, VARTYPE vt, SAFEARRAY** ppResult) {
  if (data) {
    auto size = env->GetArrayLength(data);
    auto psa = SafeArrayCreateVector(vt, 0, size);
    if (psa) {
      auto listPtr = env->GetPrimitiveArrayCritical(data, 0);
      auto intPtr = (jint*)listPtr;
      auto longPtr = (jlong*)listPtr;
      auto doublePtr = (jdouble*)listPtr;
      auto floatPtr = (jfloat*)listPtr;
      for (LONG i = 0; i < size; i++) {
        if (vt == VT_UNKNOWN) {
          SafeArrayPutElement(psa, &i, (void*)longPtr[i]);
        }
        else if (vt == VT_I4) {
          SafeArrayPutElement(psa, &i, (void*)&(intPtr[i]));
        }
        else if (vt == VT_R8) {
          SafeArrayPutElement(psa, &i, (void*)&(doublePtr[i]));
        }
        else if (vt == VT_R4) {
          SafeArrayPutElement(psa, &i, (void*)&(floatPtr[i]));
        }
      }
      env->ReleasePrimitiveArrayCritical(data, listPtr, 0);
      *ppResult = psa;
    }
  }
  // data == null -> emtpy array
}

void jni::release_all_iunknown(SAFEARRAY* array) {
  IUnknown** pData = nullptr;
  HRESULT hr = SafeArrayAccessData(array, reinterpret_cast<void**>(&pData));
  if (SUCCEEDED(hr)) {
    long lowerBound, upperBound;
    SafeArrayGetLBound(array, 1, &lowerBound);
    SafeArrayGetUBound(array, 1, &upperBound);
    long count = upperBound - lowerBound + 1;
    for (int i = 0; i < count; i++) {
      IUnknown* cur = pData[i];
      cur->Release();
    }
    hr = SafeArrayUnaccessData(array);
    if (!SUCCEEDED(hr)) {
      throw hr;
    }
  } else {
    throw hr;
  }
}

void jni::fill_safearray_no_addref(JNIEnv* env, jarray data, VARTYPE vt, SAFEARRAY** ppResult) {
  jni::fill_safearray(env, data, vt, ppResult);
  jni::release_all_iunknown(*ppResult);
}

void jni::fill_variant(JNIEnv* env, jobject variant, VARIANT* pResult) {
    if (pResult == NULL) throw E_FAIL;
    if (variant == NULL) {
        pResult->vt = VT_EMPTY;
        // return E_FAIL;
        // an empty variant is a valid result... i think
        return;
    }
    try {
      pResult->vt = (VARTYPE)env->GetShortField(variant, ids::WinVariant.vt);
      switch (pResult->vt) {
      case VT_I2:
          pResult->iVal = env->GetShortField(variant, ids::WinVariant.iVal);
          break;
      case VT_I4:
          pResult->lVal = env->GetIntField(variant, ids::WinVariant.lVal);
          break;
      case VT_UNKNOWN:
          pResult->punkVal = (IUnknown*)env->GetLongField(variant, ids::WinVariant.punkVal);
          if (pResult->punkVal != NULL) {
              pResult->punkVal->AddRef();
          } else {
              throw E_FAIL;
          }
          break;
      case VT_R4:
          pResult->fltVal = env->GetFloatField(variant, ids::WinVariant.fltVal);
          break;
      case VT_R8:
          pResult->dblVal = env->GetDoubleField(variant, ids::WinVariant.dblVal);
          break;
      case VT_BOOL: {
          auto boolVal = env->GetBooleanField(variant, ids::WinVariant.boolVal);
          pResult->boolVal = boolVal ? VARIANT_TRUE : VARIANT_FALSE;
          break;
      }
      case VT_BSTR: {
          auto str = (jstring)env->GetObjectField(variant, ids::WinVariant.bstrVal);
          jni::fill_bstr(env, str, &(pResult->bstrVal));
          break;
      }
      case VT_R8 | VT_ARRAY: {
          auto list = (jarray)env->GetObjectField(variant, ids::WinVariant.pDblVal);
          jni::fill_safearray(env, list, VT_R8, &(pResult->parray));
          break;
      }
      case VT_R4 | VT_ARRAY: {
          auto list = (jarray)env->GetObjectField(variant, ids::WinVariant.pFltVal);
          jni::fill_safearray(env, list, VT_R4, &(pResult->parray));
          break;
      }
      case VT_I4 | VT_ARRAY: {
          auto list = (jarray)env->GetObjectField(variant, ids::WinVariant.pLVal);
          jni::fill_safearray(env, list, VT_I4, &(pResult->parray));
          break;
      }
      case VT_UNKNOWN | VT_ARRAY: {
          auto list = (jarray)env->GetObjectField(variant, ids::WinVariant.pPunkVal);
          jni::fill_safearray(env, list, VT_UNKNOWN, &(pResult->parray));
          break;
      }
      }
    } catch (HRESULT hr) {
      pResult->vt = VT_EMPTY;
      throw hr;
    }
}

void jni::fill_bounds(JNIEnv* env, jfloatArray bounds, UiaRect* pResult) {
  if (bounds == NULL) return; // to return on java null
  if (pResult == NULL) throw E_INVALIDARG;

  auto pBounds = (jfloat*) env->GetPrimitiveArrayCritical(bounds, 0);
  pResult->left = pBounds[0];
  pResult->top = pBounds[1];
  pResult->width = pBounds[2];
  pResult->height = pBounds[3];
  env->ReleasePrimitiveArrayCritical(bounds, pBounds, 0);
}


JNIEnv* jni::get_env() {
  JNIEnv* env;
  jint rc = GetJVM()->GetEnv((void**) &env, JNI_VERSION_1_2);
  if (0 != rc) throw E_FAIL;
  if (env == NULL) throw E_FAIL;
  return env;
}

jstring jni::call_toString(JNIEnv* env, jobject obj) {
  jstring result = jni::call_string(env, obj, ids::Object.toString);
  check_and_throw(env);
  return result;
}
