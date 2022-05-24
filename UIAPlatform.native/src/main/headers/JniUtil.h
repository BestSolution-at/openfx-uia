
#ifndef _JNI_UTIL_
#define _JNI_UTIL_

#include "common.h"

#include <UIAutomationCore.h>

#define print_hr(hr) LPVOID buf; \
    ::FormatMessageA(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM, NULL, hr, MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT), (LPSTR) &buf, 0, NULL); \
    fprintf(stderr, "[HR FAIL] %s:%d, hr=%#010x (%s)\n", __FUNCTION__, __LINE__, hr, (char*) buf); \
    ::LocalFree(buf);

#define return_on_fail(hr) if (!SUCCEEDED(hr)) { \
  print_hr(hr); \
  return hr; \
  }

namespace JniUtil {

  HRESULT callVoidMethod(JNIEnv* env, jobject obj, jmethodID mid, ...);
  HRESULT callBooleanMethod(JNIEnv* env, jobject obj, jmethodID mid, jboolean* pResult, ...);
  HRESULT callIntMethod(JNIEnv* env, jobject obj, jmethodID mid, jint* pResult, ...);
  HRESULT callLongMethod(JNIEnv* env, jobject obj, jmethodID mid, jlong* pResult, ...);
  HRESULT callArrayMethod(JNIEnv* env, jobject obj, jmethodID mid, jarray* pResult, ...);
  HRESULT callStringMethod(JNIEnv* env, jobject obj, jmethodID mid, jstring* pResult, ...);
  HRESULT callObjectMethod(JNIEnv* env, jobject obj, jmethodID mid, jobject* pResult, ...);
  HRESULT toSafeArray(JNIEnv* env, jarray array, VARTYPE vt, SAFEARRAY** ppResult);

  HRESULT newStringFromBSTR(JNIEnv* env, BSTR value, jstring* result);
  HRESULT newStringFromLPCWSTR(JNIEnv* env, LPCWSTR value, jstring* result);
  jboolean toJBoolean(BOOL value);
  BOOL toBOOL(jboolean value);

  HRESULT copyString(JNIEnv* env, jstring jString, BSTR* pResult);
  HRESULT copyList(JNIEnv* env, jarray list, SAFEARRAY** pResult, VARTYPE vt);
  HRESULT copyVariant(JNIEnv* env, jobject jVariant, VARIANT* pResult);

  HRESULT copyBounds(JNIEnv* env, jfloatArray bounds, UiaRect* pResult);

  HRESULT getClassName(JNIEnv* env, jobject obj, BSTR* pResult);
  HRESULT toString(JNIEnv* env, jobject obj, BSTR* pResult);

  HRESULT getBooleanField(JNIEnv* env, jobject obj, jfieldID fid, jboolean* pResult);
  HRESULT getLongField(JNIEnv* env, jobject obj, jfieldID fid, jlong* pResult);

  HRESULT initIDs(JNIEnv* env);

}
#endif _JNI_UTIL_