
#include "JniUtil.h"


HRESULT JniUtil::newStringFromBSTR(JNIEnv* env, BSTR value, jstring *pResult) {
  if (pResult == NULL) return E_INVALIDARG;
  jsize length = SysStringLen(value);
  *pResult = env->NewString((const jchar *)value, length);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::newStringFromLPCWSTR(JNIEnv* env, LPCWSTR value, jstring *pResult) {
  if (pResult == NULL) return E_INVALIDARG;
  size_t size = wcslen(value);
  *pResult = env->NewString((const jchar*)value, (jsize)size);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

jboolean JniUtil::toJBoolean(BOOL value) {
  if (TRUE == value) {
    return JNI_TRUE;
  } else {
    return JNI_FALSE;
  }
}
BOOL JniUtil::toBOOL(jboolean value) {
  if (JNI_TRUE == value) {
    return TRUE;
  } else {
    return FALSE;
  }
}

HRESULT JniUtil::callVoidMethod(JNIEnv* env, jobject obj, jmethodID mid, ...) {
  va_list args;
  va_start(args, mid);
  env->CallVoidMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::callStringMethod(JNIEnv* env, jobject obj, jmethodID mid, jstring* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = (jstring) env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::callObjectMethod(JNIEnv* env, jobject obj, jmethodID mid, jobject* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::callIntMethod(JNIEnv* env, jobject obj, jmethodID mid, jint* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = env->CallIntMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::callLongMethod(JNIEnv* env, jobject obj, jmethodID mid, jlong* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = env->CallLongMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::callBooleanMethod(JNIEnv* env, jobject obj, jmethodID mid, jboolean* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = env->CallBooleanMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}


HRESULT JniUtil::callArrayMethod(JNIEnv* env, jobject obj, jmethodID mid, jarray* pResult, ...) {
  va_list args;
  va_start(args, pResult);
  *pResult = (jarray)env->CallObjectMethodV(obj, mid, args);
  va_end(args);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::toSafeArray(JNIEnv* env, jarray data, VARTYPE vt, SAFEARRAY** ppResult) {
  if (data) {
    jsize size = env->GetArrayLength(data);
    SAFEARRAY* psa = SafeArrayCreateVector(vt, 0, size);
    if (psa) {
      void* listPtr = env->GetPrimitiveArrayCritical(data, 0);
      jint* intPtr = (jint*)listPtr;
      jlong* longPtr = (jlong*)listPtr;
      jdouble* doublePtr = (jdouble*)listPtr;
      jfloat* floatPtr = (jfloat*)listPtr;
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
      return S_OK;
    }
  }
  // data == null -> emtpy array
  return S_OK;
}

HRESULT JniUtil::copyString(JNIEnv* env, jstring jString, BSTR* pResult) {
  if (jString == NULL) return E_INVALIDARG;
  if (pResult == NULL) return E_INVALIDARG;

  jsize length = env->GetStringLength(jString);
  const jchar* ptr = env->GetStringCritical(jString, NULL);
  if (ptr != NULL) {
      *pResult = SysAllocStringLen(reinterpret_cast<const OLECHAR*>(ptr), length);
      env->ReleaseStringCritical(jString, ptr);
      return S_OK;
  }
  return E_FAIL;
}

HRESULT JniUtil::copyBounds(JNIEnv* env, jfloatArray bounds, UiaRect* pResult) {
  if (bounds == NULL) return E_INVALIDARG; 
  if (pResult == NULL) return E_INVALIDARG;

  jfloat* pBounds = (jfloat*) env->GetPrimitiveArrayCritical(bounds, 0);
  pResult->left = pBounds[0];
  pResult->top = pBounds[1];
  pResult->width = pBounds[2];
  pResult->height = pBounds[3];
  env->ReleasePrimitiveArrayCritical(bounds, pBounds, 0);

  return S_OK;
}

HRESULT JniUtil::getClassName(JNIEnv* env, jobject obj, BSTR* pResult) {
  jclass cls = env->FindClass("java/lang/Class");
  if (CheckAndClearException(env)) return E_FAIL;
  jmethodID mid_getName = env->GetMethodID(cls, "getName", "()Ljava/lang/String;");
  if (CheckAndClearException(env)) return E_FAIL;
  jclass objCls = env->GetObjectClass(obj);
  if (CheckAndClearException(env)) return E_FAIL;
  jstring name = (jstring) env->CallObjectMethod(objCls, mid_getName);
  if (CheckAndClearException(env)) return E_FAIL;
  return copyString(env, name, pResult);
}

HRESULT JniUtil::toString(JNIEnv* env, jobject obj, BSTR* pResult) {
  jclass objCls = env->FindClass("java/lang/Object");
  if (CheckAndClearException(env)) return E_FAIL;
  jmethodID mid_toString = env->GetMethodID(objCls, "toString", "()Ljava/lang/String;");
  if (CheckAndClearException(env)) return E_FAIL;
  jstring val = (jstring) env->CallObjectMethod(obj, mid_toString);
  if (CheckAndClearException(env)) return E_FAIL;
  return copyString(env, val, pResult);
}

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


HRESULT JniUtil::initIDs(JNIEnv* env) {
  /* Variant */
  jclass jVariantClass = env->FindClass("com/sun/glass/ui/uia/glass/WinVariant");
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

  return S_OK;
}

HRESULT JniUtil::copyList(JNIEnv* env, jarray list, SAFEARRAY** pResult, VARTYPE vt)
{
  if (list) {
    jsize size = env->GetArrayLength(list);
    SAFEARRAY* psa = SafeArrayCreateVector(vt, 0, size);
    if (psa) {
      void* listPtr = env->GetPrimitiveArrayCritical(list, 0);
      jint* intPtr = (jint*)listPtr;
      jlong* longPtr = (jlong*)listPtr;
      jdouble* doublePtr = (jdouble*)listPtr;
      jfloat* floatPtr = (jfloat*)listPtr;
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
      env->ReleasePrimitiveArrayCritical(list, listPtr, 0);
      *pResult = psa;
      return S_OK;
    }
  }
}

HRESULT JniUtil::copyVariant(JNIEnv* env, jobject jVariant, VARIANT* pRetVal)
{
    if (pRetVal == NULL) return E_FAIL;
    if (jVariant == NULL) {
        pRetVal->vt = VT_EMPTY;
        // return E_FAIL;
        // an empty variant is a valid result... i think
        return S_OK;
    }
    HRESULT hr = S_OK;
    pRetVal->vt = (VARTYPE)env->GetShortField(jVariant, WinVariant.vt);
    switch (pRetVal->vt) {
    case VT_I2:
        pRetVal->iVal = env->GetShortField(jVariant, WinVariant.iVal);
        break;
    case VT_I4:
        pRetVal->lVal = env->GetIntField(jVariant, WinVariant.lVal);
        break;
    case VT_UNKNOWN:
        pRetVal->punkVal = (IUnknown*)env->GetLongField(jVariant, WinVariant.punkVal);
        if (pRetVal->punkVal != NULL) {
            pRetVal->punkVal->AddRef();
        }
        else {
            hr = E_FAIL;
        }
        break;
    case VT_R4:
        pRetVal->fltVal = env->GetFloatField(jVariant, WinVariant.fltVal);
        break;
    case VT_R8:
        pRetVal->dblVal = env->GetDoubleField(jVariant, WinVariant.dblVal);
        break;
    case VT_BOOL: {
        jboolean boolVal = env->GetBooleanField(jVariant, WinVariant.boolVal);
        pRetVal->boolVal = boolVal ? VARIANT_TRUE : VARIANT_FALSE;
        break;
    }
    case VT_BSTR: {
        jstring str = (jstring)env->GetObjectField(jVariant, WinVariant.bstrVal);
        hr = copyString(env, str, &(pRetVal->bstrVal));
        break;
    }
    case VT_R8 | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, WinVariant.pDblVal);
        hr = copyList(env, list, &(pRetVal->parray), VT_R8);
        break;
    }
    case VT_R4 | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, WinVariant.pFltVal);
        hr = copyList(env, list, &(pRetVal->parray), VT_R4);
        break;
    }
    case VT_I4 | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, WinVariant.pLVal);
        hr = copyList(env, list, &(pRetVal->parray), VT_I4);
        break;
    }
    case VT_UNKNOWN | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, WinVariant.pPunkVal);
        hr = copyList(env, list, &(pRetVal->parray), VT_UNKNOWN);
        break;
    }
    }
    if (FAILED(hr)) pRetVal->vt = VT_EMPTY;
    return hr;
}

HRESULT JniUtil::getBooleanField(JNIEnv* env, jobject obj, jfieldID fid, jboolean* pResult) {
  if (pResult == NULL) return E_INVALIDARG;
  *pResult = env->GetBooleanField(obj, fid);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}

HRESULT JniUtil::getLongField(JNIEnv* env, jobject obj, jfieldID fid, jlong* pResult) {
  if (pResult == NULL) return E_INVALIDARG;
  *pResult = env->GetLongField(obj, fid);
  if (CheckAndClearException(env)) return E_FAIL;
  return S_OK;
}
