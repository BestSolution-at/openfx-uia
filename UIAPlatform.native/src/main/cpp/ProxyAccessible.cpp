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

#include "common.h"
#include "com_sun_glass_ui_uia_ProxyAccessible.h"
#include "ProxyAccessible.h"
#include "GlassTextRangeProvider.h"
#include "GlassCounter.h"

 /* WinAccessible Method IDs */
static jmethodID mid_IRawElementProviderSimple_GetPatternProvider;
static jmethodID mid_IRawElementProviderSimple_get_HostRawElementProvider;
static jmethodID mid_IRawElementProviderSimple_GetPropertyValue;

static jmethodID mid_IRawElementProviderFragment_get_BoundingRectangle;
static jmethodID mid_IRawElementProviderFragment_get_FragmentRoot;
static jmethodID mid_IRawElementProviderFragment_GetEmbeddedFragmentRoots;
static jmethodID mid_IRawElementProviderFragment_GetRuntimeId;
static jmethodID mid_IRawElementProviderFragment_Navigate;
static jmethodID mid_IRawElementProviderFragment_SetFocus;

static jmethodID mid_IRawElementProviderFragmentRoot_ElementProviderFromPoint;
static jmethodID mid_IRawElementProviderFragmentRoot_GetFocus;

static jmethodID mid_IRawElementProviderAdviseEvents_AdviseEventAdded;
static jmethodID mid_IRawElementProviderAdviseEvents_AdviseEventRemoved;

static jmethodID mid_IInvokeProvider_Invoke;

static jmethodID mid_ISelectionProvider_GetSelection;
static jmethodID mid_ISelectionProvider_get_CanSelectMultiple;
static jmethodID mid_ISelectionProvider_get_IsSelectionRequired;

static jmethodID mid_ISelectionItemProvider_Select;
static jmethodID mid_ISelectionItemProvider_AddToSelection;
static jmethodID mid_ISelectionItemProvider_RemoveFromSelection;
static jmethodID mid_ISelectionItemProvider_get_IsSelected;
static jmethodID mid_ISelectionItemProvider_get_SelectionContainer;

static jmethodID mid_IRangeValueProvider_SetValue;
static jmethodID mid_IRangeValueProvider_get_Value;
static jmethodID mid_IRangeValueProvider_get_IsReadOnly;
static jmethodID mid_IRangeValueProvider_get_Maximum;
static jmethodID mid_IRangeValueProvider_get_Minimum;
static jmethodID mid_IRangeValueProvider_get_LargeChange;
static jmethodID mid_IRangeValueProvider_get_SmallChange;

static jmethodID mid_IValueProvider_SetValueString;
static jmethodID mid_IValueProvider_get_ValueString;

static jmethodID mid_ITextProvider_GetSelection;
static jmethodID mid_ITextProvider_GetVisibleRanges;
static jmethodID mid_ITextProvider_RangeFromChild;
static jmethodID mid_ITextProvider_RangeFromPoint;
static jmethodID mid_ITextProvider_get_DocumentRange;
static jmethodID mid_ITextProvider_get_SupportedTextSelection;

static jmethodID mid_IGridProvider_get_ColumnCount;
static jmethodID mid_IGridProvider_get_RowCount;
static jmethodID mid_IGridProvider_GetItem;

static jmethodID mid_IGridItemProvider_get_Row;
static jmethodID mid_IGridItemProvider_get_RowSpan;
static jmethodID mid_IGridItemProvider_get_ContainingGrid;
static jmethodID mid_IGridItemProvider_get_Column;
static jmethodID mid_IGridItemProvider_get_ColumnSpan;

static jmethodID mid_ITableProvider_GetColumnHeaders;
static jmethodID mid_ITableProvider_GetRowHeaders;
static jmethodID mid_ITableProvider_get_RowOrColumnMajor;

static jmethodID mid_ITableItemProvider_GetColumnHeaderItems;
static jmethodID mid_ITableItemProvider_GetRowHeaderItems;

static jmethodID mid_IToggleProvider_Toggle;
static jmethodID mid_IToggleProvider_get_ToggleState;

static jmethodID mid_IExpandCollapseProvider_Collapse;
static jmethodID mid_IExpandCollapseProvider_Expand;
static jmethodID mid_IExpandCollapseProvider_get_ExpandCollapseState;

static jmethodID mid_ITransformProvider_get_CanMove;
static jmethodID mid_ITransformProvider_get_CanResize;
static jmethodID mid_ITransformProvider_get_CanRotate;
static jmethodID mid_ITransformProvider_Move;
static jmethodID mid_ITransformProvider_Resize;
static jmethodID mid_ITransformProvider_Rotate;

static jmethodID mid_IScrollProvider_Scroll;
static jmethodID mid_IScrollProvider_SetScrollPercent;
static jmethodID mid_IScrollProvider_get_HorizontallyScrollable;
static jmethodID mid_IScrollProvider_get_HorizontalScrollPercent;
static jmethodID mid_IScrollProvider_get_HorizontalViewSize;
static jmethodID mid_IScrollProvider_get_VerticallyScrollable;
static jmethodID mid_IScrollProvider_get_VerticalScrollPercent;
static jmethodID mid_IScrollProvider_get_VerticalViewSize;

static jmethodID mid_IScrollItemProvider_ScrollIntoView;


/* IWindowProvider */
static jmethodID mid_WindowProvider_Close;
static jmethodID mid_WindowProvider_get_CanMaximize;
static jmethodID mid_WindowProvider_get_CanMinimize;
static jmethodID mid_WindowProvider_get_IsModal;
static jmethodID mid_WindowProvider_get_IsTopmost;
static jmethodID mid_WindowProvider_get_WindowInteractionState;
static jmethodID mid_WindowProvider_get_WindowVisualState;
static jmethodID mid_WindowProvider_SetVisualState;
static jmethodID mid_WindowProvider_WaitForInputIdle;

// IDockProvider
static jmethodID mid_DockProvider_get_DockPosition;
static jmethodID mid_DockProvider_SetDockPosition;
// IAnnotationProvider
static jmethodID mid_IAnnotationProvider_get_AnnotationTypeId;
static jmethodID mid_IAnnotationProvider_get_AnnotationTypeName;
static jmethodID mid_IAnnotationProvider_get_Author;
static jmethodID mid_IAnnotationProvider_get_DateTime;
static jmethodID mid_IAnnotationProvider_get_Target;
// IDragProvider
static jmethodID mid_IDragProvider_get_DropEffect;
static jmethodID mid_IDragProvider_get_DropEffects;
static jmethodID mid_IDragProvider_get_IsGrabbed;
static jmethodID mid_IDragProvider_GetGrabbedItems;
// IDropTargetProvider
static jmethodID mid_IDropTargetProvider_get_DropTargetEffect;
static jmethodID mid_IDropTargetProvider_get_DropTargetEffects;

/* Variant Field IDs */
static jfieldID fid_vt;
static jfieldID fid_iVal;
static jfieldID fid_lVal;
static jfieldID fid_punkVal;
static jfieldID fid_fltVal;
static jfieldID fid_dblVal;
static jfieldID fid_boolVal;
static jfieldID fid_bstrVal;
static jfieldID fid_pDblVal;
static jfieldID fid_pFltVal;

static jfieldID fid_pPunkVal;



/* static */ HRESULT ProxyAccessible::copyString(JNIEnv* env, jstring jString, BSTR* pbstrVal)
{
    if (pbstrVal != NULL && jString != NULL) {
        UINT length = env->GetStringLength(jString);
        const jchar* ptr = env->GetStringCritical(jString, NULL);
        if (ptr != NULL) {
            *pbstrVal = SysAllocStringLen(reinterpret_cast<const OLECHAR*>(ptr), length);
            env->ReleaseStringCritical(jString, ptr);
            return S_OK;
        }
    }
    return E_FAIL;
}

/* static */ HRESULT ProxyAccessible::copyList(JNIEnv* env, jarray list, SAFEARRAY** pparrayVal, VARTYPE vt)
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
                    //TODO make sure AddRef on elements is not required ?
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
            *pparrayVal = psa;
            return S_OK;
        }
    }
}

/* static */ HRESULT ProxyAccessible::copyVariant(JNIEnv* env, jobject jVariant, VARIANT* pRetVal)
{
    if (pRetVal == NULL) return E_FAIL;
    if (jVariant == NULL) {
        pRetVal->vt = VT_EMPTY;
        return E_FAIL;
    }
    HRESULT hr = S_OK;
    pRetVal->vt = (VARTYPE)env->GetShortField(jVariant, fid_vt);
    switch (pRetVal->vt) {
    case VT_I2:
        pRetVal->iVal = env->GetShortField(jVariant, fid_iVal);
        break;
    case VT_I4:
        pRetVal->lVal = env->GetIntField(jVariant, fid_lVal);
        break;
    case VT_UNKNOWN:
        pRetVal->punkVal = (IUnknown*)env->GetLongField(jVariant, fid_punkVal);
        if (pRetVal->punkVal != NULL) {
            pRetVal->punkVal->AddRef();
        }
        else {
            hr = E_FAIL;
        }
        break;
    case VT_R4:
        pRetVal->fltVal = env->GetFloatField(jVariant, fid_fltVal);
        break;
    case VT_R8:
        pRetVal->dblVal = env->GetDoubleField(jVariant, fid_dblVal);
        break;
    case VT_BOOL: {
        jboolean boolVal = env->GetBooleanField(jVariant, fid_boolVal);
        pRetVal->boolVal = boolVal ? VARIANT_TRUE : VARIANT_FALSE;
        break;
    }
    case VT_BSTR: {
        jstring str = (jstring)env->GetObjectField(jVariant, fid_bstrVal);
        hr = ProxyAccessible::copyString(env, str, &(pRetVal->bstrVal));
        break;
    }
    case VT_R8 | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, fid_pDblVal);
        hr = ProxyAccessible::copyList(env, list, &(pRetVal->parray), VT_R8);
        break;
    }
    case VT_R4 | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, fid_pFltVal);
        hr = ProxyAccessible::copyList(env, list, &(pRetVal->parray), VT_R4);
        break;
    }
    case VT_UNKNOWN | VT_ARRAY: {
        jarray list = (jarray)env->GetObjectField(jVariant, fid_pPunkVal);
        hr = ProxyAccessible::copyList(env, list, &(pRetVal->parray), VT_UNKNOWN);
        break;
    }
    }
    if (FAILED(hr)) pRetVal->vt = VT_EMPTY;
    return hr;
}

HRESULT ProxyAccessible::callLongMethod(jmethodID mid, ProxyAccessible** pRetVal, ...)
{
    va_list vl;
    va_start(vl, pRetVal);
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethodV(m_jAccessible, mid, vl);
    va_end(vl);
    if (CheckAndClearException(env)) return E_FAIL;

    /* AddRef the result */
    ProxyAccessible* ga = reinterpret_cast<ProxyAccessible*>(ptr);
    if (ga) ga->AddRef();
    *pRetVal = ga;
    return S_OK;
}

HRESULT ProxyAccessible::callArrayMethod(jmethodID mid, VARTYPE vt, SAFEARRAY** pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jarray list = (jarray)env->CallObjectMethod(m_jAccessible, mid);
    if (CheckAndClearException(env)) return E_FAIL;

    return ProxyAccessible::copyList(env, list, pRetVal, vt);
}

ProxyAccessible::ProxyAccessible(JNIEnv* env, jobject jAccessible)
    : m_refCount(1)
{
    m_jAccessible = env->NewGlobalRef(jAccessible);
    GlassCounter::IncrementAccessibility();
}

ProxyAccessible::~ProxyAccessible()
{
    JNIEnv* env = GetEnv();
    if (env) env->DeleteGlobalRef(m_jAccessible);
    GlassCounter::DecrementAccessibility();
}



/***********************************************/
/*                  IUnknown                   */
/***********************************************/
IFACEMETHODIMP_(ULONG) ProxyAccessible::AddRef()
{
    return InterlockedIncrement(&m_refCount);
}

IFACEMETHODIMP_(ULONG) ProxyAccessible::Release()
{
    long val = InterlockedDecrement(&m_refCount);
    if (val == 0) {
        delete this;
    }
    return val;
}

IFACEMETHODIMP ProxyAccessible::QueryInterface(REFIID riid, void** ppInterface)
{
    if (riid == __uuidof(IUnknown)) {
        *ppInterface = static_cast<IRawElementProviderSimple*>(this);
    }
    else if (riid == __uuidof(IRawElementProviderSimple)) {
        *ppInterface = static_cast<IRawElementProviderSimple*>(this);
    }
    else if (riid == __uuidof(IRawElementProviderFragment)) {
        *ppInterface = static_cast<IRawElementProviderFragment*>(this);
    }
    else if (riid == __uuidof(IRawElementProviderFragmentRoot)) {
        *ppInterface = static_cast<IRawElementProviderFragmentRoot*>(this);
    }
    else if (riid == __uuidof(IRawElementProviderAdviseEvents)) {
        *ppInterface = static_cast<IRawElementProviderAdviseEvents*>(this);
    }
    else if (riid == __uuidof(IInvokeProvider)) {
        *ppInterface = static_cast<IInvokeProvider*>(this);
    }
    else if (riid == __uuidof(ISelectionProvider)) {
        *ppInterface = static_cast<ISelectionProvider*>(this);
    }
    else if (riid == __uuidof(ISelectionItemProvider)) {
        *ppInterface = static_cast<ISelectionItemProvider*>(this);
    }
    else if (riid == __uuidof(IRangeValueProvider)) {
        *ppInterface = static_cast<IRangeValueProvider*>(this);
    }
    else if (riid == __uuidof(IValueProvider)) {
        *ppInterface = static_cast<IValueProvider*>(this);
    }
    else if (riid == __uuidof(ITextProvider)) {
        *ppInterface = static_cast<ITextProvider*>(this);
    }
    else if (riid == __uuidof(IGridProvider)) {
        *ppInterface = static_cast<IGridProvider*>(this);
    }
    else if (riid == __uuidof(IGridItemProvider)) {
        *ppInterface = static_cast<IGridItemProvider*>(this);
    }
    else if (riid == __uuidof(ITableProvider)) {
        *ppInterface = static_cast<ITableProvider*>(this);
    }
    else if (riid == __uuidof(ITableItemProvider)) {
        *ppInterface = static_cast<ITableItemProvider*>(this);
    }
    else if (riid == __uuidof(IToggleProvider)) {
        *ppInterface = static_cast<IToggleProvider*>(this);
    }
    else if (riid == __uuidof(IExpandCollapseProvider)) {
        *ppInterface = static_cast<IExpandCollapseProvider*>(this);
    }
    else if (riid == __uuidof(ITransformProvider)) {
        *ppInterface = static_cast<ITransformProvider*>(this);
    }
    else if (riid == __uuidof(IScrollProvider)) {
        *ppInterface = static_cast<IScrollProvider*>(this);
    }
    else if (riid == __uuidof(IScrollItemProvider)) {
        *ppInterface = static_cast<IScrollItemProvider*>(this);
    }
    else if (riid == __uuidof(IWindowProvider)) {
        *ppInterface = static_cast<IWindowProvider*>(this);
    }
    else if (riid == __uuidof(IDockProvider)) {
        *ppInterface = static_cast<IDockProvider*>(this);
    }
    else if (riid == __uuidof(IAnnotationProvider)) {
        *ppInterface = static_cast<IAnnotationProvider*>(this);
    }
    else if (riid == __uuidof(IDragProvider)) {
        *ppInterface = static_cast<IDragProvider*>(this);
    }
    else if (riid == __uuidof(IDropTargetProvider)) {
        *ppInterface = static_cast<IDropTargetProvider*>(this);
    }
    else {
        *ppInterface = NULL;
        return E_NOINTERFACE;
    }

    this->AddRef();
    return S_OK;
}

/***********************************************/
/*        IRawElementProviderSimple            */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_HostRawElementProvider(IRawElementProviderSimple** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong hwnd = env->CallLongMethod(m_jAccessible, mid_IRawElementProviderSimple_get_HostRawElementProvider);
    if (CheckAndClearException(env)) return E_FAIL;

    // We ignore the return value of UiaHostProviderFromHwnd because it returns E_INVALIDARG
    // when invoked with NULL hwnd. We use NULL hwnds to represent "lightweight" accessibles.
    // If we don't ignore it and return it from ProxyAccessible::get_HostRawElementProvider,
    // then a11y is broken on Windows 7.
    UiaHostProviderFromHwnd(reinterpret_cast<HWND>(hwnd), pRetVal);

    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_ProviderOptions(ProviderOptions* pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    /*
     * Very important to use ProviderOptions_UseComThreading, otherwise the call
     * to the providers are sent in a different thread (GetEnv() returns NULL).
     */
    *pRetVal = ProviderOptions_ServerSideProvider | ProviderOptions_UseComThreading;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::GetPatternProvider(PATTERNID patternId, IUnknown** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IRawElementProviderSimple_GetPatternProvider, &ptr, patternId);
    *pRetVal = reinterpret_cast<IUnknown*>(ptr);
    return hr;
}

IFACEMETHODIMP ProxyAccessible::GetPropertyValue(PROPERTYID propertyId, VARIANT* pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jobject jVariant = env->CallObjectMethod(m_jAccessible, mid_IRawElementProviderSimple_GetPropertyValue, propertyId);
    if (CheckAndClearException(env)) return E_FAIL;

    return copyVariant(env, jVariant, pRetVal);
}

/***********************************************/
/*       IRawElementProviderFragment           */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_BoundingRectangle(UiaRect* pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jfloatArray bounds = (jfloatArray)env->CallObjectMethod(m_jAccessible, mid_IRawElementProviderFragment_get_BoundingRectangle);
    if (CheckAndClearException(env)) return E_FAIL;

    if (bounds) {
        jfloat* boundsPtr = (jfloat*)env->GetPrimitiveArrayCritical(bounds, 0);
        pRetVal->left = boundsPtr[0];
        pRetVal->top = boundsPtr[1];
        pRetVal->width = boundsPtr[2];
        pRetVal->height = boundsPtr[3];
        env->ReleasePrimitiveArrayCritical(bounds, boundsPtr, 0);
    }
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_FragmentRoot(IRawElementProviderFragmentRoot** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IRawElementProviderFragment_get_FragmentRoot, &ptr);
    *pRetVal = static_cast<IRawElementProviderFragmentRoot*>(ptr);
    return hr;
}

IFACEMETHODIMP ProxyAccessible::GetEmbeddedFragmentRoots(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_IRawElementProviderFragment_GetEmbeddedFragmentRoots, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::GetRuntimeId(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_IRawElementProviderFragment_GetRuntimeId, VT_I4, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::Navigate(NavigateDirection direction, IRawElementProviderFragment** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IRawElementProviderFragment_Navigate, &ptr, direction);
    *pRetVal = static_cast<IRawElementProviderFragment*>(ptr);
    return hr;
}

IFACEMETHODIMP ProxyAccessible::SetFocus()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IRawElementProviderFragment_SetFocus);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*     IRawElementProviderFragmentRoot         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::ElementProviderFromPoint(double x, double y, IRawElementProviderFragment** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IRawElementProviderFragmentRoot_ElementProviderFromPoint, &ptr, x, y);
    *pRetVal = static_cast<IRawElementProviderFragment*>(ptr);
    return hr;
}

IFACEMETHODIMP ProxyAccessible::GetFocus(IRawElementProviderFragment** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IRawElementProviderFragmentRoot_GetFocus, &ptr);
    *pRetVal = static_cast<IRawElementProviderFragment*>(ptr);
    return hr;
}

/***********************************************/
/*     IRawElementProviderAdviseEvents         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::AdviseEventAdded(EVENTID eventId, SAFEARRAY* propertyIDs)
{
    JNIEnv* env = GetEnv();
    /* For some reason, probably a bug, Windows call AdviseEventRemoved() on a different thread when
     * Narrator is shutting down. The fix is to ignored any method on IRawElementProviderAdviseEvents
     * if env is NULL.
     */
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventAdded, eventId, (jlong)propertyIDs);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}


IFACEMETHODIMP ProxyAccessible::AdviseEventRemoved(EVENTID eventId, SAFEARRAY* propertyIDs)
{
    JNIEnv* env = GetEnv();
    /* For some reason, probably a bug, Windows call AdviseEventRemoved() on a different thread when
     * Narrator is shutting down. The fix is to ignored any method on IRawElementProviderAdviseEvents
     * if env is NULL.
     */
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventRemoved, eventId, (jlong)propertyIDs);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*             IInvokeProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Invoke()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    //env->CallVoidMethod(m_jAccessible, mid_Invoke);
    env->CallVoidMethod(m_jAccessible, mid_IInvokeProvider_Invoke);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*           ISelectionProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetSelection(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;

    // TODO GetSelection is both for ITextRangeProvider and ISelectionProvider
    // how should we switch between the two :?

    return callArrayMethod(mid_ITextProvider_GetSelection, VT_UNKNOWN, pRetVal);


    //return callArrayMethod(mid_ISelectionProvider_GetSelection, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::get_CanSelectMultiple(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ISelectionProvider_get_CanSelectMultiple);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_IsSelectionRequired(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ISelectionProvider_get_IsSelectionRequired);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         ISelectionItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Select()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ISelectionItemProvider_Select);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::AddToSelection()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ISelectionItemProvider_AddToSelection);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::RemoveFromSelection()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ISelectionItemProvider_RemoveFromSelection);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_IsSelected(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ISelectionItemProvider_get_IsSelected);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_SelectionContainer(IRawElementProviderSimple** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_ISelectionItemProvider_get_SelectionContainer, &ptr);
    *pRetVal = static_cast<IRawElementProviderSimple*>(ptr);
    return hr;
}

/***********************************************/
/*           IRangeValueProvider               */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::SetValue(double val)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IRangeValueProvider_SetValue, val);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_Value(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IRangeValueProvider_get_Value);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_IsReadOnly(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_IRangeValueProvider_get_IsReadOnly);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_Maximum(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IRangeValueProvider_get_Maximum);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_Minimum(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IRangeValueProvider_get_Minimum);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_LargeChange(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IRangeValueProvider_get_LargeChange);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_SmallChange(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IRangeValueProvider_get_SmallChange);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*           IValueProvider                    */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::SetValue(LPCWSTR val)
{
    if (!val) return S_OK;
    size_t size = wcslen(val);
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = env->NewString((const jchar*)val, (jsize)size);
    if (!CheckAndClearException(env)) {
        env->CallVoidMethod(m_jAccessible, mid_IValueProvider_SetValueString, str);
        if (CheckAndClearException(env)) {
            return E_FAIL;
        }
    }
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_Value(BSTR* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IValueProvider_get_ValueString);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}

/***********************************************/
/*              ITextProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetVisibleRanges(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_ITextProvider_GetVisibleRanges, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::RangeFromChild(IRawElementProviderSimple* childElement, ITextRangeProvider** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_RangeFromChild, (jlong)childElement);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    /* This code is intentionally commented.
     * JavaFX returns a new ITextRangeProvider instance each time.
     * The caller holds the only reference to this object.
     */
     //    if (gtrp) gtrp->AddRef();

    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::RangeFromPoint(UiaPoint point, ITextRangeProvider** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_RangeFromPoint, point.x, point.y);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    /* This code is intentionally commented.
     * JavaFX returns a new ITextRangeProvider instance each time.
     * The caller holds the only reference to this object.
     */
     //    if (gtrp) gtrp->AddRef();

    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_DocumentRange(ITextRangeProvider** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_get_DocumentRange);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    if (gtrp) gtrp->AddRef();
    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_SupportedTextSelection(SupportedTextSelection* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (SupportedTextSelection)env->CallIntMethod(m_jAccessible, mid_ITextProvider_get_SupportedTextSelection);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*              IGridProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_ColumnCount(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridProvider_get_ColumnCount);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_RowCount(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridProvider_get_RowCount);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::GetItem(int row, int column, IRawElementProviderSimple** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IGridProvider_GetItem, &ptr, row, column);
    *pRetVal = static_cast<IRawElementProviderSimple*>(ptr);
    return hr;
}

/***********************************************/
/*              IGridItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_Column(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridItemProvider_get_Column);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_ColumnSpan(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridItemProvider_get_ColumnSpan);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_ContainingGrid(IRawElementProviderSimple** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IGridItemProvider_get_ContainingGrid, &ptr);
    *pRetVal = static_cast<IRawElementProviderSimple*>(ptr);
    return hr;
}

IFACEMETHODIMP ProxyAccessible::get_Row(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridItemProvider_get_Row);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_RowSpan(int* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IGridItemProvider_get_RowSpan);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*              ITableProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetColumnHeaders(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_ITableProvider_GetColumnHeaders, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaders(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_ITableProvider_GetRowHeaders, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::get_RowOrColumnMajor(RowOrColumnMajor* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (RowOrColumnMajor)env->CallIntMethod(m_jAccessible, mid_ITableProvider_get_RowOrColumnMajor);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}


/***********************************************/
/*              ITableItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetColumnHeaderItems(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_ITableItemProvider_GetColumnHeaderItems, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaderItems(SAFEARRAY** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_ITableItemProvider_GetRowHeaderItems, VT_UNKNOWN, pRetVal);
}


/***********************************************/
/*              IToggleProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Toggle()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IToggleProvider_Toggle);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_ToggleState(ToggleState* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (ToggleState)env->CallIntMethod(m_jAccessible, mid_IToggleProvider_get_ToggleState);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         IExpandCollapseProvider             */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Collapse()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IExpandCollapseProvider_Collapse);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::Expand()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IExpandCollapseProvider_Expand);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_ExpandCollapseState(ExpandCollapseState* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (ExpandCollapseState)env->CallIntMethod(m_jAccessible, mid_IExpandCollapseProvider_get_ExpandCollapseState);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         ITransformProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_CanMove(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ITransformProvider_get_CanMove);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_CanResize(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ITransformProvider_get_CanResize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_CanRotate(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ITransformProvider_get_CanRotate);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::Move(double x, double y)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ITransformProvider_Move, x, y);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::Resize(double width, double height)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ITransformProvider_Resize, width, height);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::Rotate(double degrees)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ITransformProvider_Rotate, degrees);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         IScrollProvider                     */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IScrollProvider_Scroll, horizontalAmount, verticalAmount);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::SetScrollPercent(double horizontalPercent, double verticalPercent)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IScrollProvider_SetScrollPercent, horizontalPercent, verticalPercent);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_HorizontallyScrollable(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_IScrollProvider_get_HorizontallyScrollable);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_HorizontalScrollPercent(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IScrollProvider_get_HorizontalScrollPercent);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_HorizontalViewSize(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IScrollProvider_get_HorizontalViewSize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_VerticallyScrollable(BOOL* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_IScrollProvider_get_VerticallyScrollable);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_VerticalScrollPercent(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IScrollProvider_get_VerticalScrollPercent);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_VerticalViewSize(double* pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_IScrollProvider_get_VerticalViewSize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         IScrollItemProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::ScrollIntoView()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IScrollItemProvider_ScrollIntoView);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

/***********************************************/
/*         IWindowProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Close() {
    fprintf(stderr, "ProxyAccessible::Close()\n");
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_WindowProvider_Close);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_CanMaximize(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_WindowProvider_get_CanMaximize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_CanMinimize(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_WindowProvider_get_CanMinimize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_IsModal(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_WindowProvider_get_IsModal);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_IsTopmost(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_WindowProvider_get_IsTopmost);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_WindowInteractionState(WindowInteractionState* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (WindowInteractionState)env->CallIntMethod(m_jAccessible, mid_WindowProvider_get_WindowInteractionState);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_WindowVisualState(WindowVisualState* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (WindowVisualState)env->CallIntMethod(m_jAccessible, mid_WindowProvider_get_WindowVisualState);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::SetVisualState(WindowVisualState state) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_WindowProvider_SetVisualState, state);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::WaitForInputIdle(int milliseconds, BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_WindowProvider_WaitForInputIdle, (jint) milliseconds);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}


// IDockProvider
IFACEMETHODIMP ProxyAccessible::get_DockPosition(DockPosition *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (DockPosition) env->CallIntMethod(m_jAccessible, mid_DockProvider_get_DockPosition);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::SetDockPosition(DockPosition dockPosition) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_DockProvider_SetDockPosition, (jint) dockPosition);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
// IAnnotationProvider
IFACEMETHODIMP ProxyAccessible::get_AnnotationTypeId(int *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jAccessible, mid_IAnnotationProvider_get_AnnotationTypeId);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_AnnotationTypeName(BSTR *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IAnnotationProvider_get_AnnotationTypeName);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}
IFACEMETHODIMP ProxyAccessible::get_Author(BSTR *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IAnnotationProvider_get_Author);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}
IFACEMETHODIMP ProxyAccessible::get_DateTime(BSTR *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IAnnotationProvider_get_DateTime);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}
IFACEMETHODIMP ProxyAccessible::get_Target(IRawElementProviderSimple** pRetVal)
{
    if (pRetVal == NULL) return E_INVALIDARG;
    ProxyAccessible* ptr = NULL;
    HRESULT hr = callLongMethod(mid_IAnnotationProvider_get_Target, &ptr);
    *pRetVal = static_cast<IRawElementProviderSimple*>(ptr);
    return hr;
}
// IDragProvider
IFACEMETHODIMP ProxyAccessible::get_DropEffect(BSTR *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IDragProvider_get_DropEffect);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}
IFACEMETHODIMP ProxyAccessible::get_DropEffects(SAFEARRAY** pRetVal) {
    if (pRetVal == NULL) return E_INVALIDARG;
    // TODO need to support BSTR in array copy!!
     return callArrayMethod(mid_IDragProvider_GetGrabbedItems, VT_BSTR, pRetVal);
}
IFACEMETHODIMP ProxyAccessible::get_IsGrabbed(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_IDragProvider_get_IsGrabbed);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::GetGrabbedItems(SAFEARRAY **pRetVal) {
    if (pRetVal == NULL) return E_INVALIDARG;
    return callArrayMethod(mid_IDragProvider_GetGrabbedItems, VT_UNKNOWN, pRetVal);
}
// IDropTargetProvider
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffect(BSTR *pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring str = (jstring)env->CallObjectMethod(m_jAccessible, mid_IDropTargetProvider_get_DropTargetEffect);
    if (CheckAndClearException(env)) return E_FAIL;
    HRESULT res = ProxyAccessible::copyString(env, str, pRetVal);
    return res;
}
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffects(SAFEARRAY** pRetVal) {
    if (pRetVal == NULL) return E_INVALIDARG;
    // TODO need to support BSTR in array copy!!
     return callArrayMethod(mid_IDropTargetProvider_get_DropTargetEffects, VT_BSTR, pRetVal);
}

/***********************************************/
/*                  JNI                        */
/***********************************************/

/*
 * Class:     com_sun_glass_ui_uia_ProxyAccessible
 * Method:    _initIDs
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible__1initIDs
(JNIEnv* env, jclass jClass)
{
    /* IRawElementProviderSimple */
    mid_IRawElementProviderSimple_GetPatternProvider = env->GetMethodID(jClass, "IRawElementProviderSimple_GetPatternProvider", "(I)J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderSimple_get_HostRawElementProvider = env->GetMethodID(jClass, "IRawElementProviderSimple_get_HostRawElementProvider", "()J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderSimple_GetPropertyValue = env->GetMethodID(jClass, "IRawElementProviderSimple_GetPropertyValue", "(I)Lcom/sun/glass/ui/uia/glass/WinVariant;");
    if (env->ExceptionCheck()) return;

    /* IRawElementProviderFragment */
    mid_IRawElementProviderFragment_get_BoundingRectangle = env->GetMethodID(jClass, "IRawElementProviderFragment_get_BoundingRectangle", "()[F");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragment_get_FragmentRoot = env->GetMethodID(jClass, "IRawElementProviderFragment_get_FragmentRoot", "()J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragment_GetEmbeddedFragmentRoots = env->GetMethodID(jClass, "IRawElementProviderFragment_GetEmbeddedFragmentRoots", "()[J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragment_GetRuntimeId = env->GetMethodID(jClass, "IRawElementProviderFragment_GetRuntimeId", "()[I");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragment_Navigate = env->GetMethodID(jClass, "IRawElementProviderFragment_Navigate", "(I)J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragment_SetFocus = env->GetMethodID(jClass, "IRawElementProviderFragment_SetFocus", "()V");
    if (env->ExceptionCheck()) return;

    /* IRawElementProviderFragmentRoot */
    mid_IRawElementProviderFragmentRoot_ElementProviderFromPoint = env->GetMethodID(jClass, "IRawElementProviderFragmentRoot_ElementProviderFromPoint", "(DD)J");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderFragmentRoot_GetFocus = env->GetMethodID(jClass, "IRawElementProviderFragmentRoot_GetFocus", "()J");
    if (env->ExceptionCheck()) return;

    /* IRawElementProviderAdviseEvents */
    mid_IRawElementProviderAdviseEvents_AdviseEventAdded = env->GetMethodID(jClass, "IRawElementProviderAdviseEvents_AdviseEventAdded", "(IJ)V");
    if (env->ExceptionCheck()) return;
    mid_IRawElementProviderAdviseEvents_AdviseEventRemoved = env->GetMethodID(jClass, "IRawElementProviderAdviseEvents_AdviseEventRemoved", "(IJ)V");
    if (env->ExceptionCheck()) return;

    /* IInvokeProvider */
    mid_IInvokeProvider_Invoke = env->GetMethodID(jClass, "IInvokeProvider_Invoke", "()V");
    if (env->ExceptionCheck()) return;


    /* ISelectionProvider */
    mid_ISelectionProvider_GetSelection = env->GetMethodID(jClass, "ISelectionProvider_GetSelection", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ISelectionProvider_get_CanSelectMultiple = env->GetMethodID(jClass, "ISelectionProvider_get_CanSelectMultiple", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ISelectionProvider_get_IsSelectionRequired = env->GetMethodID(jClass, "ISelectionProvider_get_IsSelectionRequired", "()Z");
    if (env->ExceptionCheck()) return;

    /* ISelectionItemProvider */
    mid_ISelectionItemProvider_Select = env->GetMethodID(jClass, "ISelectionItemProvider_Select", "()V");
    if (env->ExceptionCheck()) return;
    mid_ISelectionItemProvider_AddToSelection = env->GetMethodID(jClass, "ISelectionItemProvider_AddToSelection", "()V");
    if (env->ExceptionCheck()) return;
    mid_ISelectionItemProvider_RemoveFromSelection = env->GetMethodID(jClass, "ISelectionItemProvider_RemoveFromSelection", "()V");
    if (env->ExceptionCheck()) return;
    mid_ISelectionItemProvider_get_IsSelected = env->GetMethodID(jClass, "ISelectionItemProvider_get_IsSelected", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ISelectionItemProvider_get_SelectionContainer = env->GetMethodID(jClass, "ISelectionItemProvider_get_SelectionContainer", "()J");
    if (env->ExceptionCheck()) return;

    /* IRangeValueProvider */
    mid_IRangeValueProvider_SetValue = env->GetMethodID(jClass, "IRangeValueProvider_SetValue", "(D)V");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_Value = env->GetMethodID(jClass, "IRangeValueProvider_get_Value", "()D");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_IsReadOnly = env->GetMethodID(jClass, "IRangeValueProvider_get_IsReadOnly", "()Z");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_Maximum = env->GetMethodID(jClass, "IRangeValueProvider_get_Maximum", "()D");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_Minimum = env->GetMethodID(jClass, "IRangeValueProvider_get_Minimum", "()D");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_LargeChange = env->GetMethodID(jClass, "IRangeValueProvider_get_LargeChange", "()D");
    if (env->ExceptionCheck()) return;
    mid_IRangeValueProvider_get_SmallChange = env->GetMethodID(jClass, "IRangeValueProvider_get_SmallChange", "()D");
    if (env->ExceptionCheck()) return;

    /* IValueProvider */
    mid_IValueProvider_SetValueString = env->GetMethodID(jClass, "IValueProvider_SetValueString", "(Ljava/lang/String;)V");
    if (env->ExceptionCheck()) return;
    mid_IValueProvider_get_ValueString = env->GetMethodID(jClass, "IValueProvider_get_ValueString", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;

    /* ITextProvider */
    mid_ITextProvider_GetSelection = env->GetMethodID(jClass, "ITextProvider_GetSelection", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider_GetVisibleRanges = env->GetMethodID(jClass, "ITextProvider_GetVisibleRanges", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider_RangeFromChild = env->GetMethodID(jClass, "ITextProvider_RangeFromChild", "(J)J");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider_RangeFromPoint = env->GetMethodID(jClass, "ITextProvider_RangeFromPoint", "(DD)J");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider_get_DocumentRange = env->GetMethodID(jClass, "ITextProvider_get_DocumentRange", "()J");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider_get_SupportedTextSelection = env->GetMethodID(jClass, "ITextProvider_get_SupportedTextSelection", "()I");
    if (env->ExceptionCheck()) return;

    /* IGridProvider */
    mid_IGridProvider_get_ColumnCount = env->GetMethodID(jClass, "IGridProvider_get_ColumnCount", "()I");
    if (env->ExceptionCheck()) return;
    mid_IGridProvider_get_RowCount = env->GetMethodID(jClass, "IGridProvider_get_RowCount", "()I");
    if (env->ExceptionCheck()) return;
    mid_IGridProvider_GetItem = env->GetMethodID(jClass, "IGridProvider_GetItem", "(II)J");
    if (env->ExceptionCheck()) return;

    /* IGridItemProvider */
    mid_IGridItemProvider_get_Column = env->GetMethodID(jClass, "IGridItemProvider_get_Column", "()I");
    if (env->ExceptionCheck()) return;
    mid_IGridItemProvider_get_ColumnSpan = env->GetMethodID(jClass, "IGridItemProvider_get_ColumnSpan", "()I");
    if (env->ExceptionCheck()) return;
    mid_IGridItemProvider_get_ContainingGrid = env->GetMethodID(jClass, "IGridItemProvider_get_ContainingGrid", "()J");
    if (env->ExceptionCheck()) return;
    mid_IGridItemProvider_get_Row = env->GetMethodID(jClass, "IGridItemProvider_get_Row", "()I");
    if (env->ExceptionCheck()) return;
    mid_IGridItemProvider_get_RowSpan = env->GetMethodID(jClass, "IGridItemProvider_get_RowSpan", "()I");
    if (env->ExceptionCheck()) return;

    /* ITableProvider */
    mid_ITableProvider_GetColumnHeaders = env->GetMethodID(jClass, "ITableProvider_GetColumnHeaders", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ITableProvider_GetRowHeaders = env->GetMethodID(jClass, "ITableProvider_GetRowHeaders", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ITableProvider_get_RowOrColumnMajor = env->GetMethodID(jClass, "ITableProvider_get_RowOrColumnMajor", "()I");
    if (env->ExceptionCheck()) return;

    /* ITableItemProvider */
    mid_ITableItemProvider_GetColumnHeaderItems = env->GetMethodID(jClass, "ITableItemProvider_GetColumnHeaderItems", "()[J");
    if (env->ExceptionCheck()) return;
    mid_ITableItemProvider_GetRowHeaderItems = env->GetMethodID(jClass, "ITableItemProvider_GetRowHeaderItems", "()[J");
    if (env->ExceptionCheck()) return;

    /* IToggleProvider */
    mid_IToggleProvider_Toggle = env->GetMethodID(jClass, "IToggleProvider_Toggle", "()V");
    if (env->ExceptionCheck()) return;
    mid_IToggleProvider_get_ToggleState = env->GetMethodID(jClass, "IToggleProvider_get_ToggleState", "()I");
    if (env->ExceptionCheck()) return;

    /* IExpandCollapseProvider */
    mid_IExpandCollapseProvider_Collapse = env->GetMethodID(jClass, "IExpandCollapseProvider_Collapse", "()V");
    if (env->ExceptionCheck()) return;
    mid_IExpandCollapseProvider_Expand = env->GetMethodID(jClass, "IExpandCollapseProvider_Expand", "()V");
    if (env->ExceptionCheck()) return;
    mid_IExpandCollapseProvider_get_ExpandCollapseState = env->GetMethodID(jClass, "IExpandCollapseProvider_get_ExpandCollapseState", "()I");
    if (env->ExceptionCheck()) return;

    /* ITransformProvider */
    mid_ITransformProvider_get_CanMove = env->GetMethodID(jClass, "ITransformProvider_get_CanMove", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider_get_CanResize = env->GetMethodID(jClass, "ITransformProvider_get_CanResize", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider_get_CanRotate = env->GetMethodID(jClass, "ITransformProvider_get_CanRotate", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider_Move = env->GetMethodID(jClass, "ITransformProvider_Move", "(DD)V");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider_Resize = env->GetMethodID(jClass, "ITransformProvider_Resize", "(DD)V");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider_Rotate = env->GetMethodID(jClass, "ITransformProvider_Rotate", "(D)V");
    if (env->ExceptionCheck()) return;

    /* IScrollProvider */
    mid_IScrollProvider_Scroll = env->GetMethodID(jClass, "IScrollProvider_Scroll", "(II)V");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_SetScrollPercent = env->GetMethodID(jClass, "IScrollProvider_SetScrollPercent", "(DD)V");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_HorizontallyScrollable = env->GetMethodID(jClass, "IScrollProvider_get_HorizontallyScrollable", "()Z");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_HorizontalScrollPercent = env->GetMethodID(jClass, "IScrollProvider_get_HorizontalScrollPercent", "()D");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_HorizontalViewSize = env->GetMethodID(jClass, "IScrollProvider_get_HorizontalViewSize", "()D");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_VerticallyScrollable = env->GetMethodID(jClass, "IScrollProvider_get_VerticallyScrollable", "()Z");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_VerticalScrollPercent = env->GetMethodID(jClass, "IScrollProvider_get_VerticalScrollPercent", "()D");
    if (env->ExceptionCheck()) return;
    mid_IScrollProvider_get_VerticalViewSize = env->GetMethodID(jClass, "IScrollProvider_get_VerticalViewSize", "()D");
    if (env->ExceptionCheck()) return;

    /* IScrollItemProvider */
    mid_IScrollItemProvider_ScrollIntoView = env->GetMethodID(jClass, "IScrollItemProvider_ScrollIntoView", "()V");
    if (env->ExceptionCheck()) return;

    /* IWindowProvider */
    mid_WindowProvider_Close = env->GetMethodID(jClass, "WindowProvider_Close", "()V");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_CanMaximize = env->GetMethodID(jClass, "WindowProvider_get_CanMaximize", "()Z");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_CanMinimize = env->GetMethodID(jClass, "WindowProvider_get_CanMinimize", "()Z");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_IsModal = env->GetMethodID(jClass, "WindowProvider_get_IsModal", "()Z");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_IsTopmost = env->GetMethodID(jClass, "WindowProvider_get_IsTopmost", "()Z");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_WindowInteractionState = env->GetMethodID(jClass, "WindowProvider_get_WindowInteractionState", "()I");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_get_WindowVisualState = env->GetMethodID(jClass, "WindowProvider_get_WindowVisualState", "()I");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_SetVisualState = env->GetMethodID(jClass, "WindowProvider_SetVisualState", "(I)V");
    if (env->ExceptionCheck()) return;
    mid_WindowProvider_WaitForInputIdle = env->GetMethodID(jClass, "WindowProvider_WaitForInputIdle", "(I)Z");
    if (env->ExceptionCheck()) return;

    // IDockProvider
    mid_DockProvider_get_DockPosition = env->GetMethodID(jClass, "DockProvider_get_DockPosition", "()I");
    if (env->ExceptionCheck()) return;
    mid_DockProvider_SetDockPosition = env->GetMethodID(jClass, "DockProvider_SetDockPosition", "(I)V");
    if (env->ExceptionCheck()) return;
    // IAnnotationProvider
    mid_IAnnotationProvider_get_AnnotationTypeId = env->GetMethodID(jClass, "IAnnotationProvider_get_AnnotationTypeId", "()I");
    if (env->ExceptionCheck()) return;
    mid_IAnnotationProvider_get_AnnotationTypeName = env->GetMethodID(jClass, "IAnnotationProvider_get_AnnotationTypeName", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IAnnotationProvider_get_Author = env->GetMethodID(jClass, "IAnnotationProvider_get_Author", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IAnnotationProvider_get_DateTime = env->GetMethodID(jClass, "IAnnotationProvider_get_DateTime", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IAnnotationProvider_get_Target = env->GetMethodID(jClass, "IAnnotationProvider_get_Target", "()J");
    if (env->ExceptionCheck()) return;
    // IDragProvider
    mid_IDragProvider_get_DropEffect = env->GetMethodID(jClass, "IDragProvider_get_DropEffect", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IDragProvider_get_DropEffects = env->GetMethodID(jClass, "IDragProvider_get_DropEffects", "()[Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IDragProvider_get_IsGrabbed = env->GetMethodID(jClass, "IDragProvider_get_IsGrabbed", "()Z");
    if (env->ExceptionCheck()) return;
    mid_IDragProvider_GetGrabbedItems = env->GetMethodID(jClass, "IDragProvider_GetGrabbedItems", "()[J");
    if (env->ExceptionCheck()) return;
    // IDropTargetProvider
    mid_IDropTargetProvider_get_DropTargetEffect = env->GetMethodID(jClass, "IDropTargetProvider_get_DropTargetEffect", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IDropTargetProvider_get_DropTargetEffects = env->GetMethodID(jClass, "IDropTargetProvider_get_DropTargetEffects", "()[Ljava/lang/String;");
    if (env->ExceptionCheck()) return;



    /* Variant */
    jclass jVariantClass = env->FindClass("com/sun/glass/ui/uia/glass/WinVariant");
    if (env->ExceptionCheck()) return;
    fid_vt = env->GetFieldID(jVariantClass, "vt", "S");
    if (env->ExceptionCheck()) return;
    fid_iVal = env->GetFieldID(jVariantClass, "iVal", "S");
    if (env->ExceptionCheck()) return;
    fid_lVal = env->GetFieldID(jVariantClass, "lVal", "I");
    if (env->ExceptionCheck()) return;
    fid_punkVal = env->GetFieldID(jVariantClass, "punkVal", "J");
    if (env->ExceptionCheck()) return;
    fid_fltVal = env->GetFieldID(jVariantClass, "fltVal", "F");
    if (env->ExceptionCheck()) return;
    fid_dblVal = env->GetFieldID(jVariantClass, "dblVal", "D");
    if (env->ExceptionCheck()) return;
    fid_boolVal = env->GetFieldID(jVariantClass, "boolVal", "Z");
    if (env->ExceptionCheck()) return;
    fid_bstrVal = env->GetFieldID(jVariantClass, "bstrVal", "Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    fid_pDblVal = env->GetFieldID(jVariantClass, "pDblVal", "[D");
    if (env->ExceptionCheck()) return;
    fid_pFltVal = env->GetFieldID(jVariantClass, "pFltVal", "[F");
    if (env->ExceptionCheck()) return;
    fid_pPunkVal = env->GetFieldID(jVariantClass, "pPunkVal", "[J");
    if (env->ExceptionCheck()) return;
}

extern "C" JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible_UiaRaiseNotificationEvent
(JNIEnv * env, jclass jClass, jlong jAccessible, jint notificationKind, jint notificationProcessing, jstring displayString, jstring activityId)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(jAccessible);
    IRawElementProviderSimple* pProvider = static_cast<IRawElementProviderSimple*>(acc);

    BSTR bstrDisplayString;
    ProxyAccessible::copyString(env, displayString, &bstrDisplayString);

    BSTR bstrActivityId;
    ProxyAccessible::copyString(env, activityId, &bstrActivityId);

    jlong result = (jlong)UiaRaiseNotificationEvent(pProvider, (NotificationKind)notificationKind, (NotificationProcessing)notificationProcessing, bstrDisplayString, bstrActivityId);

    ::SysFreeString(bstrDisplayString);
    ::SysFreeString(bstrActivityId);

    return result;
}



/*
 * Class:     com_sun_glass_ui_win_WinAccessible
 * Method:    _createProxyAccessible
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible__1createProxyAccessible
(JNIEnv* env, jobject jAccessible)
{
    ProxyAccessible* acc = new (std::nothrow) ProxyAccessible(env, jAccessible);
    return reinterpret_cast<jlong>(acc);
}

/*
 * Class:     com_sun_glass_ui_win_WinAccessible
 * Method:    _destroyProxyAccessible
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible__1destroyProxyAccessible
(JNIEnv* env, jobject jAccessible, jlong winAccessible)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(winAccessible);
    acc->Release();
}

/*
 * Class:     com_sun_glass_ui_win_WinAccessible
 * Method:    UiaRaiseAutomationEvent
 * Signature: (JI)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible_UiaRaiseAutomationEvent
(JNIEnv* env, jclass jClass, jlong jAccessible, jint id)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(jAccessible);
    IRawElementProviderSimple* pProvider = static_cast<IRawElementProviderSimple*>(acc);
    return (jlong)UiaRaiseAutomationEvent(pProvider, (EVENTID)id);
}

/*
 * Class:     com_sun_glass_ui_win_WinAccessible
 * Method:    UiaRaiseAutomationPropertyChangedEvent
 * Signature: (JILcom/sun/glass/ui/win/WinVariant;Lcom/sun/glass/ui/win/WinVariant;)J
 */
JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible_UiaRaiseAutomationPropertyChangedEvent
(JNIEnv* env, jclass jClass, jlong jAccessible, jint id, jobject oldV, jobject newV)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(jAccessible);
    IRawElementProviderSimple* pProvider = static_cast<IRawElementProviderSimple*>(acc);
    VARIANT ov = { 0 }, nv = { 0 };
    HRESULT hr = E_FAIL;

    hr = ProxyAccessible::copyVariant(env, oldV, &ov);
    if (FAILED(hr)) return (jlong)hr;
    hr = ProxyAccessible::copyVariant(env, newV, &nv);
    if (FAILED(hr)) return (jlong)hr; 

    return (jlong)UiaRaiseAutomationPropertyChangedEvent(pProvider, (PROPERTYID)id, ov, nv);
}

/*
 * Class:     com_sun_glass_ui_win_WinAccessible
 * Method:    UiaClientsAreListening
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible_UiaClientsAreListening
(JNIEnv* env, jclass jClass)
{
    return UiaClientsAreListening() ? JNI_TRUE : JNI_FALSE;
}



