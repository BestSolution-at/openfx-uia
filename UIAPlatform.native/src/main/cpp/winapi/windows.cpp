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

#include <jni.h>
#include <windows.h>
#include <UIAutomation.h>
#include <iostream>

extern "C" {

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantInit
(JNIEnv* env, jclass jClass) 
{
    VARIANT* variant = new VARIANT();
    VariantInit(variant);
    return (jlong) variant;
}

JNIEXPORT jint JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantClear
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    jint hr = VariantClear(variant);

    delete variant;
    return hr;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetVt
(JNIEnv* env, jclass jClass, jlong _variant, jshort _vt) {
    VARIANT* variant = (VARIANT*) _variant;
    VARTYPE vt = (VARTYPE) _vt;
    variant->vt = vt;
}
JNIEXPORT jshort JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetVt
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jshort) variant->vt;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetIVal
(JNIEnv* env, jclass jClass, jlong _variant, jshort _iVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->iVal  = (SHORT) _iVal;
}
JNIEXPORT jshort JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetIVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jshort) variant->iVal;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetLVal
(JNIEnv* env, jclass jClass, jlong _variant, jint _lVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->iVal  = (LONG) _lVal;
}
JNIEXPORT jint JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetLVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jint) variant->lVal;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetFltVal
(JNIEnv* env, jclass jClass, jlong _variant, jfloat _fltVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->fltVal  = (FLOAT) _fltVal;
}
JNIEXPORT jfloat JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetFltVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jfloat) variant->fltVal;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetDblVal
(JNIEnv* env, jclass jClass, jlong _variant, jdouble _dblVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->dblVal  = (DOUBLE) _dblVal;
}
JNIEXPORT jdouble JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetDblVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jdouble) variant->dblVal;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetBoolVal
(JNIEnv* env, jclass jClass, jlong _variant, jboolean _boolVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->boolVal  = _boolVal ? VARIANT_TRUE : VARIANT_FALSE;
}
JNIEXPORT jboolean JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetBoolVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return variant->boolVal == VARIANT_TRUE ? JNI_TRUE : JNI_FALSE;
}

BSTR toBSTR(JNIEnv* env, jstring value) {
    UINT length = env->GetStringLength(value);
    const jchar* ptr = env->GetStringCritical(value, NULL);
    BSTR result = SysAllocStringLen(reinterpret_cast<const OLECHAR*>(ptr), length);
    env->ReleaseStringCritical(value, ptr);
    return result;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetBstrVal
(JNIEnv* env, jclass jClass, jlong _variant, jstring _bstrVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->bstrVal = toBSTR(env, _bstrVal);
}
JNIEXPORT jstring JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetBstrVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;

    int wslen = ::SysStringLen(variant->bstrVal);
    int len = ::WideCharToMultiByte(CP_ACP, 0, (wchar_t*) variant->bstrVal, wslen, NULL, 0, NULL, NULL);
    std::string dblstr(len, '\0');
    len = ::WideCharToMultiByte(CP_ACP, 0 /* no flags */,
                            (wchar_t*) variant->bstrVal, wslen /* not necessary NULL-terminated */,
                            &dblstr[0], len,
                            NULL, NULL /* no default char */);

    return env->NewStringUTF(dblstr.c_str());
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetPunkVal
(JNIEnv* env, jclass jClass, jlong _variant, jlong _punkVal) {
    VARIANT* variant = (VARIANT*) _variant;
    variant->punkVal = (IUnknown*) _punkVal;
    variant->punkVal->AddRef();
}
JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetPunkVal
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    return (jlong) variant->punkVal;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetFltSafeArray
(JNIEnv* env, jclass jClass, jlong _variant, jarray _fltArray) {
    VARIANT* variant = (VARIANT*) _variant;
    
    jsize size = env->GetArrayLength(_fltArray);
    variant->parray = SafeArrayCreateVector(VT_R4, 0, size);
    void* listPtr = env->GetPrimitiveArrayCritical(_fltArray, 0);
    jfloat* floatPtr = (jfloat*)listPtr;
    for (LONG i = 0; i < size; i++) {
        SafeArrayPutElement(variant->parray, &i, (void*)&(floatPtr[i]));
    }
    env->ReleasePrimitiveArrayCritical(_fltArray, listPtr, 0);
}
JNIEXPORT jfloatArray JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetFltSafeArray
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;

    FLOAT* values;
    HRESULT hr = SafeArrayAccessData(variant->parray, (void**)&values); // direct access to SA memory

    long lowerBound, upperBound;  // get array bounds
    SafeArrayGetLBound(variant->parray, 1 , &lowerBound);
    SafeArrayGetUBound(variant->parray, 1, &upperBound);
    
    long cnt_elements = upperBound - lowerBound + 1; 

    jfloat* tmp = new jfloat[cnt_elements];
    
    for (int i = 0; i < cnt_elements; ++i)  // iterate through returned values
    {                              
        FLOAT val = values[i];
        tmp[i] = (jfloat) val;
    }       
    SafeArrayUnaccessData(variant->parray);

    jfloatArray result = env->NewFloatArray(cnt_elements);
    env->SetFloatArrayRegion(result, 0, cnt_elements, tmp);
    return result;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetDblSafeArray
(JNIEnv* env, jclass jClass, jlong _variant, jarray _dblArray) {
    VARIANT* variant = (VARIANT*) _variant;
    
    jsize size = env->GetArrayLength(_dblArray);
    variant->parray = SafeArrayCreateVector(VT_R8, 0, size);
    void* listPtr = env->GetPrimitiveArrayCritical(_dblArray, 0);
    jdouble* doublePtr = (jdouble*)listPtr;
    for (LONG i = 0; i < size; i++) {
        SafeArrayPutElement(variant->parray, &i, (void*)&(doublePtr[i]));
    }
    env->ReleasePrimitiveArrayCritical(_dblArray, listPtr, 0);
}
JNIEXPORT jdoubleArray JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetDblSafeArray
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;

    DOUBLE* values;
    HRESULT hr = SafeArrayAccessData(variant->parray, (void**)&values); // direct access to SA memory

    long lowerBound, upperBound;  // get array bounds
    SafeArrayGetLBound(variant->parray, 1 , &lowerBound);
    SafeArrayGetUBound(variant->parray, 1, &upperBound);
    
    long cnt_elements = upperBound - lowerBound + 1; 

    jdouble* tmp = new jdouble[cnt_elements];
    
    for (int i = 0; i < cnt_elements; ++i)  // iterate through returned values
    {                              
        DOUBLE val = values[i];
        tmp[i] = (jdouble) val;
    }       
    SafeArrayUnaccessData(variant->parray);

    jdoubleArray result = env->NewDoubleArray(cnt_elements);
    env->SetDoubleArrayRegion(result, 0, cnt_elements, tmp);
    return result;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantSetPunkSafeArray
(JNIEnv* env, jclass jClass, jlong _variant, jarray _punkArray) {
    VARIANT* variant = (VARIANT*) _variant;
    
    jsize size = env->GetArrayLength(_punkArray);
    variant->parray = SafeArrayCreateVector(VT_UNKNOWN, 0, size);
    void* listPtr = env->GetPrimitiveArrayCritical(_punkArray, 0);
    jlong* longPtr = (jlong*)listPtr;
    for (LONG i = 0; i < size; i++) {
        IUnknown* punk = (IUnknown*) longPtr[i];
        punk->AddRef();
        SafeArrayPutElement(variant->parray, &i, (void*)&(longPtr[i]));
    }
    env->ReleasePrimitiveArrayCritical(_punkArray, listPtr, 0);
}
JNIEXPORT jlongArray JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantGetPunkSafeArray
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;

    LONG* values;
    HRESULT hr = SafeArrayAccessData(variant->parray, (void**)&values); // direct access to SA memory

    long lowerBound, upperBound;  // get array bounds
    SafeArrayGetLBound(variant->parray, 1 , &lowerBound);
    SafeArrayGetUBound(variant->parray, 1, &upperBound);
    
    long cnt_elements = upperBound - lowerBound + 1; 

    jlong* tmp = new jlong[cnt_elements];
    
    for (int i = 0; i < cnt_elements; ++i)  // iterate through returned values
    {                              
        LONG val = values[i];
        tmp[i] = (jlong) val;
    }       
    SafeArrayUnaccessData(variant->parray);

    jlongArray result = env->NewLongArray(cnt_elements);
    env->SetLongArrayRegion(result, 0, cnt_elements, tmp);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaGetReservedMixedAttributeValue
(JNIEnv* env, jclass jClass)
{
    IUnknown* mixedAttributeValue;
    UiaGetReservedMixedAttributeValue(&mixedAttributeValue);
    return (jlong) mixedAttributeValue;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaGetReservedNotSupportedValue
(JNIEnv* env, jclass jClass)
{
    IUnknown* notSupportedValue;
    UiaGetReservedNotSupportedValue(&notSupportedValue);
    return (jlong) notSupportedValue;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_VariantDebugOutput
(JNIEnv* env, jclass jClass, jlong _variant) {
    VARIANT* variant = (VARIANT*) _variant;
    switch (variant->vt) {
        case VT_R4 | VT_ARRAY:
        {
            std::cerr << "Variant: " << "VT_R4 | VT_ARRAY" << std::endl;    
            FLOAT* values;
            HRESULT hr = SafeArrayAccessData(variant->parray, (void**)&values); // direct access to SA memory

            long lowerBound, upperBound;  // get array bounds
            SafeArrayGetLBound(variant->parray, 1 , &lowerBound);
            SafeArrayGetUBound(variant->parray, 1, &upperBound);
            
            long cnt_elements = upperBound - lowerBound + 1; 
            for (int i = 0; i < cnt_elements; ++i)  // iterate through returned values
            {                              
                FLOAT val = values[i];   
                std::cerr << "       : Element " << i << ": " << val << std::endl;
            }       
            SafeArrayUnaccessData(variant->parray);
        }
        
        break;
        case VT_R8 | VT_ARRAY:
        {
            std::cerr << "Variant: " << "VT_R8 | VT_ARRAY" << std::endl;
            DOUBLE* values;
            HRESULT hr = SafeArrayAccessData(variant->parray, (void**)&values); // direct access to SA memory

            long lowerBound, upperBound;  // get array bounds
            SafeArrayGetLBound(variant->parray, 1 , &lowerBound);
            SafeArrayGetUBound(variant->parray, 1, &upperBound);
            
            long cnt_elements = upperBound - lowerBound + 1; 
            for (int i = 0; i < cnt_elements; ++i)  // iterate through returned values
            {                              
                DOUBLE val = values[i];   
                std::cerr << "       : Element " << i << ": " << val << std::endl;
            }       
            SafeArrayUnaccessData(variant->parray);
        }
        break;
        case VT_UNKNOWN | VT_ARRAY:
        std::cerr << "Variant: " << "VT_UNKNOWN | VT_ARRAY" << std::endl;
        break;
        case VT_EMPTY:
        std::cerr << "Variant: " << "VT_EMPTY" << std::endl;
        break;
        case VT_NULL:
        std::cerr << "Variant: " << "VT_NULL" << std::endl;
        break;
        case VT_I2:
        std::cerr << "Variant: " << "VT_I2" << " " << variant->iVal << std::endl;
        break;
        case VT_I4:
        std::cerr << "Variant: " << "VT_I4" << " " << variant->lVal << std::endl;
        break;
        case VT_R4:
        std::cerr << "Variant: " << "VT_R4" << " " << variant->fltVal << std::endl;
        break;
        case VT_R8:
        std::cerr << "Variant: " << "VT_R8" << " " << variant->dblVal << std::endl;
        break;
        case VT_BOOL:
        std::cerr << "Variant: " << "VT_BOOL" << " " << variant->boolVal << std::endl;
        break;
        case VT_BSTR:
        {
            int wslen = ::SysStringLen(variant->bstrVal);
            int len = ::WideCharToMultiByte(CP_ACP, 0, (wchar_t*) variant->bstrVal, wslen, NULL, 0, NULL, NULL);
            std::string dblstr(len, '\0');
            len = ::WideCharToMultiByte(CP_ACP, 0 /* no flags */,
                                    (wchar_t*) variant->bstrVal, wslen /* not necessary NULL-terminated */,
                                    &dblstr[0], len,
                                    NULL, NULL /* no default char */);

            std::cerr << "Variant: " << "VT_BSTR" << " " << dblstr << std::endl;
        }
        break;
        case VT_UNKNOWN:
        std::cerr << "Variant: " << "VT_UNKNOWN" << " " << variant->punkVal << std::endl;
        break;
    }
    std::cerr     << "       : " << variant << std::endl;
}


JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseActiveTextPositionChangedEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jlong _pTextRange) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    ITextRangeProvider* pTextRange = (ITextRangeProvider*) _pTextRange;
    HRESULT result = UiaRaiseActiveTextPositionChangedEvent(pProvider, pTextRange);
    return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseAsyncContentLoadedEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _asyncContentLoadedState, jdouble _percentComplete) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    AsyncContentLoadedState asyncContentLoadedState = (AsyncContentLoadedState) _asyncContentLoadedState;
    double percentComplete = (double) _percentComplete;
    std::cerr << "FOOO " << asyncContentLoadedState << ", " << percentComplete << std::endl;
    HRESULT result = UiaRaiseAsyncContentLoadedEvent(pProvider, asyncContentLoadedState, percentComplete);
    return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseAutomationEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _id) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    EVENTID id = (EVENTID) _id;
    HRESULT result = UiaRaiseAutomationEvent(pProvider, id);
    return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseAutomationPropertyChangedEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _id, jlong _variantOld, jlong _variantNew) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    PROPERTYID id = (PROPERTYID) _id;
    VARIANT* variantOld = (VARIANT*) _variantOld;
    VARIANT* variantNew = (VARIANT*) _variantNew;
    HRESULT result = UiaRaiseAutomationPropertyChangedEvent(pProvider, id, *variantOld, *variantNew);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseChangesEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _eventIdCount, jlong _pUiaChanges) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    int eventIdCount = (int) _eventIdCount;
    UiaChangeInfo* pUiaChanges = (UiaChangeInfo*) _pUiaChanges;
    HRESULT result = UiaRaiseChangesEvent(pProvider, eventIdCount, pUiaChanges);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseNotificationEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _notificationKind, jint _notificationProcessing, jstring _displayString, jstring _activityId) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    NotificationKind notificationKind = (NotificationKind) _notificationKind;
    NotificationProcessing notificationProcessing = (NotificationProcessing) _notificationProcessing;
    BSTR displayString = toBSTR(env, _displayString);
    BSTR activityId = toBSTR(env, _activityId);
    HRESULT result = UiaRaiseNotificationEvent(pProvider, notificationKind, notificationProcessing, displayString, activityId);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseStructureChangedEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _structureChangeType, jarray _pRuntimeId) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    StructureChangeType structureChangeType = (StructureChangeType) _structureChangeType;

    int* pRuntimeId = NULL;
    int cRuntimeIdLen = 0;

    if (_pRuntimeId != NULL) {
        jsize len = env->GetArrayLength(_pRuntimeId);
        pRuntimeId = new int[len];
        void* listPtr = env->GetPrimitiveArrayCritical(_pRuntimeId, 0);
        jint* intPtr = (jint*)listPtr;
        for (LONG i = 0; i < len; i++) {
            pRuntimeId[i] = intPtr[i];
        }
        env->ReleasePrimitiveArrayCritical(_pRuntimeId, listPtr, 0);
        cRuntimeIdLen = (int) len;
    }

    
    HRESULT result = UiaRaiseStructureChangedEvent(pProvider, structureChangeType, pRuntimeId, cRuntimeIdLen);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_UiaRaiseTextEditTextChangedEvent
(JNIEnv* env, jclass jClass, jlong _pProvider, jint _textEditChangeType, jarray _pRuntimeId, jlong _pChangedData) {
    IRawElementProviderSimple* pProvider = (IRawElementProviderSimple*) _pProvider;
    TextEditChangeType textEditChangeType = (TextEditChangeType) _textEditChangeType;
    SAFEARRAY* pChangedData = (SAFEARRAY*) _pChangedData;
    HRESULT result = UiaRaiseTextEditTextChangedEvent(pProvider, textEditChangeType, pChangedData);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SysAllocString
(JNIEnv* env, jclass jClass, jstring value) {
    UINT length = env->GetStringLength(value);
    const jchar* ptr = env->GetStringCritical(value, NULL);
    BSTR result = SysAllocStringLen(reinterpret_cast<const OLECHAR*>(ptr), length);
    env->ReleaseStringCritical(value, ptr);

    std::wcerr << "allocated: " << result << std:: endl;
    return (jlong) result;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SysFreeString
(JNIEnv* env, jclass jClass, jlong pBstr) {
    BSTR bstr = (BSTR) pBstr;
    SysFreeString(bstr);
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SafeArrayCreateVector
(JNIEnv* env, jclass jClass, jshort _vt, jint _lLbound, jint _cElements) {
    VARTYPE vt = (VARTYPE) _vt;
    LONG lLbound = (LONG) _lLbound;
    ULONG cElements = (ULONG) _cElements;
    SAFEARRAY* result = SafeArrayCreateVector(vt, lLbound, cElements);
    return (jlong) result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SafeArrayDestroy
(JNIEnv* env, jclass jClass, jlong _psa) {
    SAFEARRAY* psa = (SAFEARRAY*) _psa;
    HRESULT result = SafeArrayDestroy(psa);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SafeArrayPutElement
(JNIEnv* env, jclass jClass, jlong _psa, jint _index, jlong pValue) {
    SAFEARRAY* psa = (SAFEARRAY*) _psa;
    LONG index = (LONG) _index;
    void* value = (void*) pValue;
    HRESULT result = SafeArrayPutElement(psa, &index, value);
    return result;
}

JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_SafeArrayGetElement
(JNIEnv* env, jclass jClass, jlong _psa, jint _index, jlong pValue) {
    SAFEARRAY* psa = (SAFEARRAY*) _psa;
    LONG index = (LONG) _index;
    BSTR value;
    HRESULT result = SafeArrayGetElement(psa, &index, &value);
    std::wcerr << value << std::endl;
    return (jlong) value;
}

JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_winapi_Windows_DebugOutputBSTR
(JNIEnv* env, jclass jClass, jlong _pBstr) {
    BSTR bstr = (BSTR) _pBstr;
    std::wcerr << bstr << std::endl;
}

}