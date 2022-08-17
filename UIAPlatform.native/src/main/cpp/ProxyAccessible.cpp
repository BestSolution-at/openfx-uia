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
#include "ProxyTextRangeProvider.h"

#include "JniUtil.h"

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

/* ITransformProvider2 */
static jmethodID mid_ITransformProvider2_get_CanZoom;
static jmethodID mid_ITransformProvider2_get_ZoomLevel;
static jmethodID mid_ITransformProvider2_get_ZoomMinimum;
static jmethodID mid_ITransformProvider2_get_ZoomMaximum;
static jmethodID mid_ITransformProvider2_Zoom;
static jmethodID mid_ITransformProvider2_ZoomByUnit;

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
// IItemContainerProvider
static jmethodID mid_IItemContainerProvider_FindItemByProperty;
// IMultipleViewProvider
static jmethodID mid_IMultipleViewProvider_get_CurrentView;
static jmethodID mid_IMultipleViewProvider_GetSupportedViews;
static jmethodID mid_IMultipleViewProvider_GetViewName;
static jmethodID mid_IMultipleViewProvider_SetCurrentView;

// ITextChildProvider
static jmethodID mid_ITextChildProvider_get_TextContainer;
static jmethodID mid_ITextChildProvider_get_TextRange;

// ITextProvider2
static jmethodID mid_ITextProvider2_GetCaretRange;
static jmethodID mid_ITextProvider2_RangeFromAnnotation;

// ITextEditProvider
static jmethodID mid_ITextEditProvider_GetActiveComposition;
static jmethodID mid_ITextEditProvider_GetConversionTarget;

// IVirtualizedItemProvider
static jmethodID mid_IVirtualizedItemProvider_Realize;

// IStylesProvider
static jmethodID mid_IStylesProvider_get_ExtendedProperties;
static jmethodID mid_IStylesProvider_get_FillColor;
static jmethodID mid_IStylesProvider_get_FillPatternColor;
static jmethodID mid_IStylesProvider_get_FillPatternStyle;
static jmethodID mid_IStylesProvider_get_Shape;
static jmethodID mid_IStylesProvider_get_StyleId;
static jmethodID mid_IStylesProvider_get_StyleName;

// ISynchronizedInputProvider
static jmethodID mid_ISynchronizedInputProvider_Cancel;
static jmethodID mid_ISynchronizedInputProvider_StartListening;

// ISelectionProvider2
static jmethodID mid_ISelectionProvider2_get_ItemCount;
static jmethodID mid_ISelectionProvider2_get_CurrentSelectedItem;
static jmethodID mid_ISelectionProvider2_get_FirstSelectedItem;
static jmethodID mid_ISelectionProvider2_get_LastSelectedItem;

/* NCaretRangeResult */
static jfieldID fid_isActive;
static jfieldID fid_range;


static HRESULT ReleaseAllIUnknown(SAFEARRAY* array) {
  HRESULT hr;
  IUnknown** pData = nullptr;
  hr = SafeArrayAccessData(array, reinterpret_cast<void**>(&pData));
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
  }
  return hr;
}

static HRESULT PrintAllIUnknowRefCount(SAFEARRAY* array) {
  HRESULT hr;
  IUnknown** pData = nullptr;
  hr = SafeArrayAccessData(array, reinterpret_cast<void**>(&pData));
  if (SUCCEEDED(hr)) {
    long lowerBound, upperBound;
    SafeArrayGetLBound(array, 1, &lowerBound);
    SafeArrayGetUBound(array, 1, &upperBound);
    long count = upperBound - lowerBound + 1;
    for (int i = 0; i < count; i++) {
      IUnknown* cur = pData[i];
      cur->AddRef();
      ULONG rc = cur->Release();
      fprintf(stderr, " array[%d] refcount = %d refs\n", i, rc);
    }
    hr = SafeArrayUnaccessData(array);
  }
  return hr;
}

ProxyAccessible::ProxyAccessible(JNIEnv* env, jobject jAccessible)
    : m_refCount(1)
{
    m_jAccessible = env->NewGlobalRef(jAccessible);
    GlassCounter::IncrementAccessibility();

    this->LOG = Logger::create(env, "native.ProxyAccessible");

  HRESULT hr;

    // BSTR toStr;
    hr = JniUtil::toString(env, m_jAccessible, &toStr);
    if (SUCCEEDED(hr)) {
      // BSTR clsName;
      hr = JniUtil::getClassName(env, m_jAccessible, &clsName);
      if (SUCCEEDED(hr)) {
        LOG->debugf("created native ProxyAccessible %S (%S) [%d]", toStr, clsName, m_jAccessible);
        // SysFreeString(clsName);
      }
      // SysFreeString(toStr);
    }
  
    
}

ProxyAccessible::~ProxyAccessible()
{

    LOG->debugf("DESTROY %S (%S) [%d]", toStr, clsName, m_jAccessible);
    JNIEnv* env = GetEnv();
    if (env) env->DeleteGlobalRef(m_jAccessible);
    GlassCounter::DecrementAccessibility();

    delete this->LOG;
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
        LOG->debugf("REF COUNT ZERO %S (%S) [%d]", toStr, clsName, m_jAccessible);
        delete this;
    }
    return val;
}

IFACEMETHODIMP ProxyAccessible::toString(BSTR* result) {
  
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  hr = JniUtil::toString(env, m_jAccessible, result);
  return_on_fail(hr);

  return hr;
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
    else if (riid == __uuidof(ISelectionProvider2)) {
        *ppInterface = static_cast<ISelectionProvider2*>(this);
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
    else if (riid == __uuidof(ITextProvider2)) {
        *ppInterface = static_cast<ITextProvider2*>(this);
    }
    else if (riid == __uuidof(ITextEditProvider)) {
        *ppInterface = static_cast<ITextEditProvider*>(this);
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
    else if (riid == __uuidof(ITransformProvider2)) {
        *ppInterface = static_cast<ITransformProvider2*>(this);
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
    else if (riid == __uuidof(IItemContainerProvider)) {
        *ppInterface = static_cast<IItemContainerProvider*>(this);
    }
    else if (riid == __uuidof(IMultipleViewProvider)) {
        *ppInterface = static_cast<IMultipleViewProvider*>(this);
    }
    else if (riid == __uuidof(ITextChildProvider)) {
        *ppInterface = static_cast<ITextChildProvider*>(this);
    }
    else if (riid == __uuidof(ISynchronizedInputProvider)) {
        *ppInterface = static_cast<ISynchronizedInputProvider*>(this);
    }
    else if (riid == __uuidof(IVirtualizedItemProvider)) {
        *ppInterface = static_cast<IVirtualizedItemProvider*>(this);
    }
    else if (riid == __uuidof(IStylesProvider)) {
        *ppInterface = static_cast<IStylesProvider*>(this);
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

IFACEMETHODIMP ProxyAccessible::GetPatternProvider(PATTERNID patternId, IUnknown** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IRawElementProviderSimple_GetPatternProvider, &pointer, patternId);
  return_on_fail(hr);

  IUnknown* result = reinterpret_cast<IUnknown*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetPropertyValue(PROPERTYID propertyId, VARIANT* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jobject jVariant;
  hr = JniUtil::callObjectMethod(env, m_jAccessible, mid_IRawElementProviderSimple_GetPropertyValue, &jVariant, propertyId);
  return_on_fail(hr);

  hr = JniUtil::copyVariant(env, jVariant, pResult);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*       IRawElementProviderFragment           */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_BoundingRectangle(UiaRect* pResult) {


  auto tid = GetCurrentThreadId();

  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  {
  BSTR ltoStr;
  HRESULT hr = this->toString(&ltoStr);
  if (SUCCEEDED(hr)) {
    BSTR lclsName;
    hr = JniUtil::getClassName(env, m_jAccessible, &lclsName);
    if (SUCCEEDED(hr)) {
      LOG->debugf("[%d] get_BoundingRectangle on %S (%S) [%d]", tid, ltoStr, lclsName, m_jAccessible);
      SysFreeString(lclsName);
    }
    SysFreeString(ltoStr);
  }
  LOG->debugf("[%d] get_BoundingRectangle on %S (%S) (STORED)", tid, toStr, clsName);
}

  jobject result;
  LOG->debugf("calling object method! on %d\n", m_jAccessible);
  hr = JniUtil::callObjectMethod(env, m_jAccessible, mid_IRawElementProviderFragment_get_BoundingRectangle, &result);
  return_on_fail(hr);

  jfloatArray bounds = (jfloatArray) result;
  hr = JniUtil::copyBounds(env, bounds, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_FragmentRoot(IRawElementProviderFragmentRoot** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;
  
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IRawElementProviderFragment_get_FragmentRoot, &pointer);
  return_on_fail(hr);

  IRawElementProviderFragmentRoot* result = reinterpret_cast<IRawElementProviderFragmentRoot*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetEmbeddedFragmentRoots(SAFEARRAY** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IRawElementProviderFragment_GetEmbeddedFragmentRoots, &array);
  return_on_fail(hr);

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetRuntimeId(SAFEARRAY** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IRawElementProviderFragment_GetRuntimeId, &array);
  return_on_fail(hr);

  hr = JniUtil::toSafeArray(env, array, VT_I4, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::Navigate(NavigateDirection direction, IRawElementProviderFragment** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;
  
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IRawElementProviderFragment_Navigate, &pointer, direction);
  return_on_fail(hr);

  IRawElementProviderFragment* result = reinterpret_cast<IRawElementProviderFragment*>(pointer);
  // AddRef ok (example @ https://docs.microsoft.com/en-us/windows/win32/api/uiautomationcore/nf-uiautomationcore-irawelementproviderfragment-navigate)
  if (result != NULL) result->AddRef();

  //fprintf(stderr, "Navigate(%d) => %d\n", direction, result);

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyAccessible::SetFocus()
{
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IRawElementProviderFragment_SetFocus);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*     IRawElementProviderFragmentRoot         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::ElementProviderFromPoint(double x, double y, IRawElementProviderFragment** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;
  
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IRawElementProviderFragmentRoot_ElementProviderFromPoint, &pointer, x, y);
  return_on_fail(hr);

  IRawElementProviderFragment* result = reinterpret_cast<IRawElementProviderFragment*>(pointer);
  // Addref ok (example @ https://docs.microsoft.com/en-us/windows/win32/api/uiautomationcore/nf-uiautomationcore-irawelementproviderfragmentroot-elementproviderfrompoint)
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetFocus(IRawElementProviderFragment** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;
  
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IRawElementProviderFragmentRoot_GetFocus, &pointer);
  return_on_fail(hr);

  IRawElementProviderFragment* result = reinterpret_cast<IRawElementProviderFragment*>(pointer);
  if (result != NULL) result->AddRef();

  //fprintf(stderr, "GetFocus => %d\n", result);

  *pResult = result;
  return hr;
}

/***********************************************/
/*     IRawElementProviderAdviseEvents         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::AdviseEventAdded(EVENTID eventId, SAFEARRAY* propertyIDs)
{
    // JNIEnv* env = GetEnv();
    /* For some reason, probably a bug, Windows call AdviseEventRemoved() on a different thread when
     * Narrator is shutting down. The fix is to ignored any method on IRawElementProviderAdviseEvents
     * if env is NULL.
     */
    // if (env == NULL) return E_FAIL;
    // env->CallVoidMethod(m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventAdded, eventId, (jlong)propertyIDs);
    // if (CheckAndClearException(env)) return E_FAIL;
    // return S_OK;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventAdded, eventId, (jlong) propertyIDs);
  return_on_fail(hr);

  return hr;
}


IFACEMETHODIMP ProxyAccessible::AdviseEventRemoved(EVENTID eventId, SAFEARRAY* propertyIDs)
{
    // JNIEnv* env = GetEnv();
    /* For some reason, probably a bug, Windows call AdviseEventRemoved() on a different thread when
     * Narrator is shutting down. The fix is to ignored any method on IRawElementProviderAdviseEvents
     * if env is NULL.
     */
    // if (env == NULL) return E_FAIL;
    // env->CallVoidMethod(m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventRemoved, eventId, (jlong)propertyIDs);
    // if (CheckAndClearException(env)) return E_FAIL;
    // return S_OK;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IRawElementProviderAdviseEvents_AdviseEventRemoved, eventId, (jlong) propertyIDs);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*             IInvokeProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Invoke()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr)

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IInvokeProvider_Invoke);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*           ISelectionProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetSelection(SAFEARRAY** pRetVal) {
  // TODO GetSelection is both for ITextRangeProvider and ISelectionProvider
  // how should we switch between the two :?

  IUnknown* textPatternProvider;
  GetPatternProvider(UIA_TextPatternId, &textPatternProvider);
  if (textPatternProvider != NULL) {
    textPatternProvider->Release();

    // got a text pattern

    if (pRetVal == NULL) return E_INVALIDARG;

    HRESULT hr;

    JNIEnv* env;
    hr = GetEnv(&env);
    return_on_fail(hr);

    jarray array;
    hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITextProvider_GetSelection, &array);
    return_on_fail(hr);

    hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pRetVal);
    return_on_fail(hr);

    // we want to undo the AddRef the safe array push caused
    hr = ReleaseAllIUnknown(*pRetVal);
    return_on_fail(hr);

    return hr;


  } else {

    // got a selection pattern

    if (pRetVal == NULL) return E_INVALIDARG;

    HRESULT hr;

    JNIEnv* env;
    hr = GetEnv(&env);
    return_on_fail(hr);

    jarray array;
    hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ISelectionProvider_GetSelection, &array);
    return_on_fail(hr);

    hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pRetVal);
    return_on_fail(hr);

    // here we need the implicit AddRef() from toSafeArray

    return hr;

  }

  

  // int count;
  // IUnknown** result;
  // HRESULT hr = callIUnknownArrayMethod(m_jAccessible, mid_ITextProvider_GetSelection, &count, &result);
  // if (SUCCEEDED(hr)) {
  //   SAFEARRAY* psa = SafeArrayCreateVector(VT_UNKNOWN, 0, count);
  //   for (LONG i = 0; i < count; i++) {
  //     SafeArrayPutElement(psa, &i, result[i]);
  //     result[i]->Release(); // PutElement adds a ref we don't want
  //   }
  //   delete result;
  //   *pRetVal = psa;
  //   return S_OK;
  // }
  // return hr;



//     HRESULT hr = callArrayMethod(mid_ITextProvider_GetSelection, VT_UNKNOWN, pRetVal);
    // if (SUCCEEDED(hr)) {
    //   ReleaseAllIUnknown(*pRetVal);
    //   PrintAllIUnknowRefCount(*pRetVal);
    // }

    //return hr;

    //return callArrayMethod(mid_ISelectionProvider_GetSelection, VT_UNKNOWN, pRetVal);
}

IFACEMETHODIMP ProxyAccessible::get_CanSelectMultiple(BOOL* pResult)
{
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)
  jboolean result;
  hr = JniUtil::callBooleanMethod(env, m_jAccessible, mid_ISelectionProvider_get_CanSelectMultiple, &result);
  return_on_fail(hr)
  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_IsSelectionRequired(BOOL* pResult)
{
  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)
  jboolean result;
  hr = JniUtil::callBooleanMethod(env, m_jAccessible, mid_ISelectionProvider_get_IsSelectionRequired, &result);
  return_on_fail(hr)
  *pResult = result;
  return hr;
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

IFACEMETHODIMP ProxyAccessible::get_SelectionContainer(IRawElementProviderSimple** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ISelectionItemProvider_get_SelectionContainer, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
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
IFACEMETHODIMP ProxyAccessible::SetValue(LPCWSTR val) {
  if (!val) return S_OK;
  
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring value;
  hr = JniUtil::newStringFromLPCWSTR(env, val, &value);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IValueProvider_SetValueString, value);
  return_on_fail(hr);

  env->DeleteLocalRef(value);

  return hr;

    // size_t size = wcslen(val);
    // JNIEnv* env = GetEnv();
    // if (env == NULL) return E_FAIL;
    // jstring str = env->NewString((const jchar*)val, (jsize)size);
    // if (!CheckAndClearException(env)) {
    //     env->CallVoidMethod(m_jAccessible, mid_IValueProvider_SetValueString, str);
    //     if (CheckAndClearException(env)) {
    //         return E_FAIL;
    //     }
    // }
    // return S_OK;
}

IFACEMETHODIMP ProxyAccessible::get_Value(BSTR* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IValueProvider_get_ValueString, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*              ITextProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetVisibleRanges(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITextProvider_GetVisibleRanges, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  // we want to undo the AddRef the safe array push caused
  hr = ReleaseAllIUnknown(*pResult);
  return_on_fail(hr)

  return hr;
}

IFACEMETHODIMP ProxyAccessible::RangeFromChild(IRawElementProviderSimple* childElement, ITextRangeProvider** pRetVal)
{
   LOG->debugf("+ RangeFromChild");
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_RangeFromChild, (jlong)childElement);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    //if (gtrp) gtrp->AddRef();

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
   LOG->debugf("+ RangeFromPoint");
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_RangeFromPoint, point.x, point.y);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    //if (gtrp) gtrp->AddRef();
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
  LOG->debugf("+ get_DocumentRange");
    if (pRetVal == NULL) return E_INVALIDARG;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jAccessible, mid_ITextProvider_get_DocumentRange);
    if (CheckAndClearException(env)) return E_FAIL;

    GlassTextRangeProvider* gtrp = reinterpret_cast<GlassTextRangeProvider*>(ptr);
    //if (gtrp) gtrp->AddRef();
    //if (gtrp) gtrp->AddRef();
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

IFACEMETHODIMP ProxyAccessible::GetItem(int row, int column, IRawElementProviderSimple** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IGridProvider_GetItem, &pointer, row, column);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
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

IFACEMETHODIMP ProxyAccessible::get_ContainingGrid(IRawElementProviderSimple** pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IGridItemProvider_get_ContainingGrid, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
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
IFACEMETHODIMP ProxyAccessible::GetColumnHeaders(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITableProvider_GetColumnHeaders, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaders(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITableProvider_GetRowHeaders, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_RowOrColumnMajor(RowOrColumnMajor* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jint result;
  hr = JniUtil::callIntMethod(env, m_jAccessible, mid_ITableProvider_get_RowOrColumnMajor, &result);
  return_on_fail(hr);

  *pResult = (RowOrColumnMajor) result;

  return hr;
}


/***********************************************/
/*              ITableItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetColumnHeaderItems(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITableItemProvider_GetColumnHeaderItems, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  return hr;
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaderItems(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_ITableItemProvider_GetRowHeaderItems, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  return hr;
}


/***********************************************/
/*              IToggleProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Toggle()
{
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IToggleProvider_Toggle);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_ToggleState(ToggleState* pResult)
{
  if (pResult == NULL) return E_INVALIDARG;
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jAccessible, mid_IToggleProvider_get_ToggleState, &result);
  return_on_fail(hr);

  *pResult = (ToggleState) result;

  return hr;
}

/***********************************************/
/*         IExpandCollapseProvider             */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Collapse() {
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IExpandCollapseProvider_Collapse);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::Expand() {
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IExpandCollapseProvider_Expand);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_ExpandCollapseState(ExpandCollapseState* pResult) {
  if (pResult == NULL) return E_INVALIDARG;
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jAccessible, mid_IExpandCollapseProvider_get_ExpandCollapseState, &result);
  return_on_fail(hr);

  *pResult = (ExpandCollapseState) result;

  return hr;
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
    LOG->debugf("ProxyAccessible::Close()");
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
IFACEMETHODIMP ProxyAccessible::get_AnnotationTypeName(BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IAnnotationProvider_get_AnnotationTypeName, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyAccessible::get_Author(BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IAnnotationProvider_get_Author, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_DateTime(BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IAnnotationProvider_get_DateTime, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_Target(IRawElementProviderSimple** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IAnnotationProvider_get_Target, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}
// IDragProvider
IFACEMETHODIMP ProxyAccessible::get_DropEffect(BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IDragProvider_get_DropEffect, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_DropEffects(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IDragProvider_get_DropEffects, &array);
  return_on_fail(hr)

  // TODO need to support BSTR in array copy!!
  hr = JniUtil::toSafeArray(env, array, VT_BSTR, pResult);
  return_on_fail(hr)

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_IsGrabbed(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_IDragProvider_get_IsGrabbed);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::GetGrabbedItems(SAFEARRAY **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IDragProvider_GetGrabbedItems, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_UNKNOWN, pResult);
  return_on_fail(hr)

  return hr;
}
// IDropTargetProvider
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffect(BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IDropTargetProvider_get_DropTargetEffect, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffects(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr)

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IDropTargetProvider_get_DropTargetEffects, &array);
  return_on_fail(hr)

  // TODO need to support BSTR in array copy!!
  hr = JniUtil::toSafeArray(env, array, VT_BSTR, pResult);
  return_on_fail(hr)

  return hr;
}
 // IItemContainerProvider
IFACEMETHODIMP ProxyAccessible::FindItemByProperty(IRawElementProviderSimple *pStartAfter, PROPERTYID propertyID, VARIANT value, IRawElementProviderSimple **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_IItemContainerProvider_FindItemByProperty, &pointer, (jlong) pStartAfter, (jint) propertyID, (jlong) (&value));
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}
// IMultipleViewProvider
IFACEMETHODIMP ProxyAccessible::get_CurrentView(int *pResult) {
  if (pResult == NULL) return E_INVALIDARG;
 
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jAccessible, mid_IMultipleViewProvider_get_CurrentView, &result);
  return_on_fail(hr);

  *pResult = (int) result;

  return hr;
}
IFACEMETHODIMP ProxyAccessible::GetSupportedViews(SAFEARRAY** pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jarray array;
  hr = JniUtil::callArrayMethod(env, m_jAccessible, mid_IMultipleViewProvider_GetSupportedViews, &array);
  return_on_fail(hr)

  hr = JniUtil::toSafeArray(env, array, VT_I4, pResult);
  return_on_fail(hr)

  return hr;
}
IFACEMETHODIMP ProxyAccessible::GetViewName(int viewId, BSTR *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IMultipleViewProvider_GetViewName, &result, (jint)viewId);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::SetCurrentView(int viewId) {
  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jAccessible, mid_IMultipleViewProvider_SetCurrentView, (jint) viewId);
  return_on_fail(hr);

  return hr;
}
// ITextChildProvider
IFACEMETHODIMP ProxyAccessible::get_TextContainer(IRawElementProviderSimple **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ITextChildProvider_get_TextContainer, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();

  *pResult = result;
  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_TextRange(ITextRangeProvider **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ITextChildProvider_get_TextRange, &pointer);
  return_on_fail(hr);

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  // we do not call AddRef() on ITextRangeProviders!
  *pResult = result;
  return hr;

    // IUnknown* ptr = NULL;
    // JNIEnv* env = GetEnv();
    // // NO AddRef!
    // HRESULT hr = env->CallLongMethod(m_jAccessible, mid_ITextChildProvider_get_TextRange, &ptr);
    //  if (CheckAndClearException(env)) {
    //     return E_FAIL;
    // }
    // //HRESULT hr = callLongMethod(mid_ITextChildProvider_get_TextRange, &ptr);
    // *pRetVal = static_cast<ITextRangeProvider*>(ptr);
    // return hr;
}

// ITextProvider2
IFACEMETHODIMP ProxyAccessible::GetCaretRange(BOOL *pIsActive, ITextRangeProvider **pResult) {
  if (pIsActive == NULL) return E_INVALIDARG;
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jobject jResult;
  hr = JniUtil::callObjectMethod(env, m_jAccessible, mid_ITextProvider2_GetCaretRange, &jResult);
  return_on_fail(hr);

  jboolean isActive;
  hr = JniUtil::getBooleanField(env, jResult, fid_isActive, &isActive);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::getLongField(env, jResult, fid_range, &pointer);
  return_on_fail(hr);

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  // no AddRef() on text ranges

  *pIsActive = JniUtil::toBOOL(isActive);
  *pResult = result;

  return hr;
}

IFACEMETHODIMP ProxyAccessible::RangeFromAnnotation(IRawElementProviderSimple *annotationElement, ITextRangeProvider **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ITextProvider2_RangeFromAnnotation, &pointer, (jlong) annotationElement);
  return_on_fail(hr);

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  // no add ref on text providers
  *pResult = result;

  return hr;
}

// ITextEditProvider
IFACEMETHODIMP ProxyAccessible::GetActiveComposition(ITextRangeProvider **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ITextEditProvider_GetActiveComposition, &pointer);
  return_on_fail(hr);

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  // no add ref on text providers
  *pResult = result;

  return hr;
}
IFACEMETHODIMP ProxyAccessible::GetConversionTarget(ITextRangeProvider **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ITextEditProvider_GetConversionTarget, &pointer);
  return_on_fail(hr);

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  // no add ref on text providers
  *pResult = result;

  return hr;
}

// ITransformProvider2
IFACEMETHODIMP ProxyAccessible::get_CanZoom(BOOL* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jAccessible, mid_ITransformProvider2_get_CanZoom);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_ZoomLevel(double* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_ITransformProvider2_get_ZoomLevel);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_ZoomMinimum(double* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_ITransformProvider2_get_ZoomMinimum);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_ZoomMaximum(double* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallDoubleMethod(m_jAccessible, mid_ITransformProvider2_get_ZoomMaximum);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::Zoom(double zoom) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ITransformProvider2_Zoom, zoom);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::ZoomByUnit(ZoomUnit zoomUnit) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ITransformProvider2_ZoomByUnit, (jint) zoomUnit);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

// IVirtualizedItemProvider
IFACEMETHODIMP ProxyAccessible::Realize() {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_IVirtualizedItemProvider_Realize);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

// IStylesProvider
IFACEMETHODIMP ProxyAccessible::get_ExtendedProperties(BSTR* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IStylesProvider_get_ExtendedProperties, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_FillColor(int* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (int) env->CallIntMethod(m_jAccessible, mid_IStylesProvider_get_FillColor);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_FillPatternColor(int* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (int) env->CallIntMethod(m_jAccessible, mid_IStylesProvider_get_FillPatternColor);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_FillPatternStyle(BSTR* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IStylesProvider_get_FillPatternStyle, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_Shape(BSTR* pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IStylesProvider_get_Shape, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_StyleId(int* pRetVal) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = (int) env->CallIntMethod(m_jAccessible, mid_IStylesProvider_get_StyleId);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::get_StyleName(BSTR* pResult) {
if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;

  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jAccessible, mid_IStylesProvider_get_StyleName, &result);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}

// ISynchronizedInputProvider
IFACEMETHODIMP ProxyAccessible::Cancel() {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ISynchronizedInputProvider_Cancel);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}
IFACEMETHODIMP ProxyAccessible::StartListening(SynchronizedInputType inputType) {
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jAccessible, mid_ISynchronizedInputProvider_StartListening, (jint) inputType);
    // TODO this method should return E_INVALIDOPERATION if it is already listening
    // since the java api has no HRESULT it is not implemented
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

// ISelectionProvider2
IFACEMETHODIMP ProxyAccessible::get_CurrentSelectedItem(IRawElementProviderSimple **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ISelectionProvider2_get_CurrentSelectedItem, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();
  *pResult = result;
  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_FirstSelectedItem(IRawElementProviderSimple **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ISelectionProvider2_get_FirstSelectedItem, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();
  *pResult = result;
  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_LastSelectedItem(IRawElementProviderSimple **pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jAccessible, mid_ISelectionProvider2_get_LastSelectedItem, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result != NULL) result->AddRef();
  *pResult = result;
  return hr;
}
IFACEMETHODIMP ProxyAccessible::get_ItemCount(int *pResult) {
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jAccessible, mid_ISelectionProvider2_get_ItemCount, &result);
  return_on_fail(hr);

  *pResult = (int) result;

  return hr;
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

    /* ITransformProvider2 */
    mid_ITransformProvider2_get_CanZoom = env->GetMethodID(jClass, "ITransformProvider2_get_CanZoom", "()Z");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider2_get_ZoomLevel = env->GetMethodID(jClass, "ITransformProvider2_get_ZoomLevel", "()D");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider2_get_ZoomMinimum = env->GetMethodID(jClass, "ITransformProvider2_get_ZoomMinimum", "()D");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider2_get_ZoomMaximum = env->GetMethodID(jClass, "ITransformProvider2_get_ZoomMaximum", "()D");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider2_Zoom = env->GetMethodID(jClass, "ITransformProvider2_Zoom", "(D)V");
    if (env->ExceptionCheck()) return;
    mid_ITransformProvider2_ZoomByUnit = env->GetMethodID(jClass, "ITransformProvider2_ZoomByUnit", "(I)V");
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
    // IItemContainerProvider
    mid_IItemContainerProvider_FindItemByProperty = env->GetMethodID(jClass, "IItemContainerProvider_FindItemByProperty", "(JIJ)J");
    if (env->ExceptionCheck()) return;
    // IMultipleViewProvider
    mid_IMultipleViewProvider_get_CurrentView = env->GetMethodID(jClass, "IMultipleViewProvider_get_CurrentView", "()I");
    if (env->ExceptionCheck()) return;
    mid_IMultipleViewProvider_GetSupportedViews = env->GetMethodID(jClass, "IMultipleViewProvider_GetSupportedViews", "()[I");
    if (env->ExceptionCheck()) return;
    mid_IMultipleViewProvider_GetViewName = env->GetMethodID(jClass, "IMultipleViewProvider_GetViewName", "(I)Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IMultipleViewProvider_SetCurrentView = env->GetMethodID(jClass, "IMultipleViewProvider_SetCurrentView", "(I)V");
    if (env->ExceptionCheck()) return;
    // ITextChildProvider
    mid_ITextChildProvider_get_TextContainer = env->GetMethodID(jClass, "ITextChildProvider_get_TextContainer", "()J");
    if (env->ExceptionCheck()) return;
    mid_ITextChildProvider_get_TextRange = env->GetMethodID(jClass, "ITextChildProvider_get_TextRange", "()J");
    if (env->ExceptionCheck()) return;
    // ITextProvider2
    mid_ITextProvider2_GetCaretRange = env->GetMethodID(jClass, "ITextProvider2_GetCaretRange", "()Lcom/sun/glass/ui/uia/ProxyAccessible$NCaretRangeResult;");
    if (env->ExceptionCheck()) return;
    mid_ITextProvider2_RangeFromAnnotation = env->GetMethodID(jClass, "ITextProvider2_RangeFromAnnotation", "(J)J");
    if (env->ExceptionCheck()) return;
    // ITextEditProvider
    mid_ITextEditProvider_GetActiveComposition = env->GetMethodID(jClass, "ITextEditProvider_GetActiveComposition", "()J");
    if (env->ExceptionCheck()) return;
    mid_ITextEditProvider_GetConversionTarget = env->GetMethodID(jClass, "ITextEditProvider_GetConversionTarget", "()J");
    if (env->ExceptionCheck()) return;
    // IVirtualizedItemProvider
    mid_IVirtualizedItemProvider_Realize = env->GetMethodID(jClass, "IVirtualizedItemProvider_Realize", "()V");
    if (env->ExceptionCheck()) return;
    // ISynchronizdInputProvider
    mid_ISynchronizedInputProvider_Cancel = env->GetMethodID(jClass, "ISynchronizedItemProvider_Cancel", "()V");
    if (env->ExceptionCheck()) return;
    mid_ISynchronizedInputProvider_StartListening = env->GetMethodID(jClass, "ISynchronizedItemProvider_StartListening", "(I)V");
    // ISelectionProvider2
    mid_ISelectionProvider2_get_ItemCount = env->GetMethodID(jClass, "ISelectionProvider2_get_ItemCount", "()I");
    if (env->ExceptionCheck()) return;
    mid_ISelectionProvider2_get_CurrentSelectedItem = env->GetMethodID(jClass, "ISelectionProvider2_get_CurrentSelectedItem", "()J");
    if (env->ExceptionCheck()) return;
    mid_ISelectionProvider2_get_FirstSelectedItem = env->GetMethodID(jClass, "ISelectionProvider2_get_FirstSelectedItem", "()J");
    if (env->ExceptionCheck()) return;
    mid_ISelectionProvider2_get_LastSelectedItem = env->GetMethodID(jClass, "ISelectionProvider2_get_LastSelectedItem", "()J");
    if (env->ExceptionCheck()) return;

    // IStylesProvider
    mid_IStylesProvider_get_ExtendedProperties = env->GetMethodID(jClass, "IStylesProvider_get_ExtendedProperties", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_FillColor = env->GetMethodID(jClass, "IStylesProvider_get_FillColor", "()I");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_FillPatternColor = env->GetMethodID(jClass, "IStylesProvider_get_FillPatternColor", "()I");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_FillPatternStyle = env->GetMethodID(jClass, "IStylesProvider_get_FillPatternStyle", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_Shape = env->GetMethodID(jClass, "IStylesProvider_get_Shape", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_StyleId = env->GetMethodID(jClass, "IStylesProvider_get_StyleId", "()I");
    if (env->ExceptionCheck()) return;
    mid_IStylesProvider_get_StyleName = env->GetMethodID(jClass, "IStylesProvider_get_StyleName", "()Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    
    /* Variant */
    JniUtil::initIDs(env);

    /* NCaretRangeResult */
    jclass jNCaretRangeResultClass = env->FindClass("com/sun/glass/ui/uia/ProxyAccessible$NCaretRangeResult");
    if (env->ExceptionCheck()) return;
    fid_isActive = env->GetFieldID(jNCaretRangeResultClass, "isActive", "Z");
    if (env->ExceptionCheck()) return;
    fid_range = env->GetFieldID(jNCaretRangeResultClass, "range", "J");
    if (env->ExceptionCheck()) return;
}

extern "C" JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyAccessible_UiaRaiseNotificationEvent
(JNIEnv * env, jclass jClass, jlong jAccessible, jint notificationKind, jint notificationProcessing, jstring displayString, jstring activityId)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(jAccessible);
    IRawElementProviderSimple* pProvider = static_cast<IRawElementProviderSimple*>(acc);

    BSTR bstrDisplayString;
    JniUtil::copyString(env, displayString, &bstrDisplayString);

    BSTR bstrActivityId;
    JniUtil::copyString(env, activityId, &bstrActivityId);

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

    hr = JniUtil::copyVariant(env, oldV, &ov);
    if (FAILED(hr)) return (jlong)hr;
    hr = JniUtil::copyVariant(env, newV, &nv);
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



