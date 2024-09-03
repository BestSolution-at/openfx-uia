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

#include <iostream>

#include "JniUtil.h"
#include <JniImpl.h>
using namespace jni;

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
    : m_refCount(1), m_log("ProxyAccessible") {
    m_jAccessible = env->NewGlobalRef(jAccessible);
    GlassCounter::IncrementAccessibility();

    m_log.trace() << "ProxyAccessible created." << std::endl;
}

ProxyAccessible::~ProxyAccessible() {
    JNIEnv* env = GetEnv();
    if (env) env->DeleteGlobalRef(m_jAccessible);
    GlassCounter::DecrementAccessibility();

    m_log.trace() << "ProxyAccessible destroyed." << std::endl;
}

jobject ProxyAccessible::get_jobject() {
  return m_jAccessible;
}

/***********************************************/
/*                  IUnknown                   */
/***********************************************/
IFACEMETHODIMP_(ULONG) ProxyAccessible::AddRef() {
    return InterlockedIncrement(&m_refCount);
}

IFACEMETHODIMP_(ULONG) ProxyAccessible::Release() {
    long val = InterlockedDecrement(&m_refCount);
    if (val == 0) {
        delete this;
    }
    return val;
}

IFACEMETHODIMP ProxyAccessible::toString(BSTR* pResult) {
  IMPL_BEGIN
    assert_arg(pResult);
    auto env = get_env();
    LocalFrame frame(env, 10);
    auto result = jni::call_toString(env, get_jobject());
    jni::fill_bstr(env, result, pResult);
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::QueryInterface(REFIID riid, void** ppInterface) {
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
IFACEMETHODIMP ProxyAccessible::get_HostRawElementProvider(IRawElementProviderSimple** ppResult) {
  IMPL_BEGIN
    assert_arg(ppResult);
    auto env = get_env();
    auto pointer = call_long(env, get_jobject(), mid_IRawElementProviderSimple_get_HostRawElementProvider);
    auto hWnd = reinterpret_cast<HWND>(pointer);
    if (IsWindow(hWnd)) {
      auto hr = UiaHostProviderFromHwnd(hWnd, ppResult);
      if (!SUCCEEDED(hr)) throw hr;
    } else {
      *ppResult = nullptr;
    }
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::get_ProviderOptions(ProviderOptions* pResult) {
  IMPL_BEGIN
    assert_arg(pResult);
    /*
      * Very important to use ProviderOptions_UseComThreading, otherwise the call
      * to the providers are sent in a different thread (GetEnv() returns NULL).
      */
    *pResult = ProviderOptions_ServerSideProvider | ProviderOptions_UseComThreading;
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::GetPatternProvider(PATTERNID patternId, IUnknown** ppResult) {
  IMPL_BEGIN
    assert_arg(ppResult);
    auto env = get_env();
    auto result = call_accessible(env, get_jobject(), mid_IRawElementProviderSimple_GetPatternProvider, _int(patternId));
    if (result != nullptr) result->AddRef();
    *ppResult = static_cast<IRawElementProviderSimple*>(result);
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::GetPropertyValue(PROPERTYID propertyId, VARIANT* pResult) {
  // For now we output here the propertyId on hr fail (#25)
  //IMPL_CALL_VARIANT(pResult, mid_IRawElementProviderSimple_GetPropertyValue, _int(propertyId));
  IMPL_BEGIN
  try {
    assert_arg(pResult);
    auto env = get_env();
    LocalFrame frame(env, 10);
    auto result = call_object(env, get_jobject(), mid_IRawElementProviderSimple_GetPropertyValue, _int(propertyId));
    fill_variant(env, result, pResult);
  } catch (HRESULT hr) {

    try {
      VARIANT v;
      VariantInit(&v);

      auto env = get_env();
      LocalFrame frame(env, 10);
      auto r = call_object(env, get_jobject(), mid_IRawElementProviderSimple_GetPropertyValue, _int(UIA_ControlTypePropertyId));
      fill_variant(env, r, &v);

      auto controlTypeId = v.lVal;
      VariantClear(&v);
      std::cerr << "[HR FAIL] The following HR FAIL happened during GetPropertyValue(propertyId = " << propertyId << ") call (controlTypeId=" << controlTypeId << "):" << std::endl;
    } catch(...) {
    }

    throw hr;
  }
  IMPL_END
}

/***********************************************/
/*       IRawElementProviderFragment           */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_BoundingRectangle(UiaRect* pResult) {
  IMPL_BEGIN
    assert_arg(pResult);
    auto env = get_env();
    LocalFrame frame(env, 10);
    auto result = call_object(env, get_jobject(), mid_IRawElementProviderFragment_get_BoundingRectangle);
    fill_bounds(env, (jfloatArray) result, pResult);
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::get_FragmentRoot(IRawElementProviderFragmentRoot** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IRawElementProviderFragment_get_FragmentRoot);
}

IFACEMETHODIMP ProxyAccessible::GetEmbeddedFragmentRoots(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_IRawElementProviderFragment_GetEmbeddedFragmentRoots);
}

IFACEMETHODIMP ProxyAccessible::GetRuntimeId(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_I4, mid_IRawElementProviderFragment_GetRuntimeId);
}

IFACEMETHODIMP ProxyAccessible::Navigate(NavigateDirection direction, IRawElementProviderFragment** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IRawElementProviderFragment_Navigate, _int(direction));
}

IFACEMETHODIMP ProxyAccessible::SetFocus() {
  IMPL_CALL_VOID(mid_IRawElementProviderFragment_SetFocus);
}

/***********************************************/
/*     IRawElementProviderFragmentRoot         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::ElementProviderFromPoint(double x, double y, IRawElementProviderFragment** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IRawElementProviderFragmentRoot_ElementProviderFromPoint, _double(x), _double(y));
}

IFACEMETHODIMP ProxyAccessible::GetFocus(IRawElementProviderFragment** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IRawElementProviderFragmentRoot_GetFocus);
}

/***********************************************/
/*     IRawElementProviderAdviseEvents         */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::AdviseEventAdded(EVENTID eventId, SAFEARRAY* propertyIDs) {
  IMPL_CALL_VOID(mid_IRawElementProviderAdviseEvents_AdviseEventAdded, eventId, _ptr(propertyIDs));
}


IFACEMETHODIMP ProxyAccessible::AdviseEventRemoved(EVENTID eventId, SAFEARRAY* propertyIDs) {
  IMPL_CALL_VOID(mid_IRawElementProviderAdviseEvents_AdviseEventRemoved, eventId, _ptr(propertyIDs));
}

/***********************************************/
/*             IInvokeProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Invoke() {
  IMPL_CALL_VOID(mid_IInvokeProvider_Invoke);
}

/***********************************************/
/*           ISelectionProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetSelection(SAFEARRAY** ppResult) {
  // GetSelection is both for ITextRangeProvider and ISelectionProvider
  // for now we test by calling GetPatternProvider...
  IUnknown* textPatternProvider;
  GetPatternProvider(UIA_TextPatternId, &textPatternProvider);
  if (textPatternProvider != NULL) {
    textPatternProvider->Release();
    // got a text pattern
    // it seems the ITextRangeProvider get one ref too much by adding them to the safearray -.-
    IMPL_CALL_SAFEARRAY_NO_ADDREF(ppResult, VT_UNKNOWN, mid_ITextProvider_GetSelection);
  } else {
    // got a selection pattern
    // the selection items need the addref
    IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_ISelectionProvider_GetSelection);
  }
}

IFACEMETHODIMP ProxyAccessible::get_CanSelectMultiple(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ISelectionProvider_get_CanSelectMultiple);
}

IFACEMETHODIMP ProxyAccessible::get_IsSelectionRequired(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ISelectionProvider_get_IsSelectionRequired);
}

/***********************************************/
/*         ISelectionItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Select() {
  IMPL_CALL_VOID(mid_ISelectionItemProvider_Select);
}

IFACEMETHODIMP ProxyAccessible::AddToSelection() {
  IMPL_CALL_VOID(mid_ISelectionItemProvider_AddToSelection);
}

IFACEMETHODIMP ProxyAccessible::RemoveFromSelection() {
  IMPL_CALL_VOID(mid_ISelectionItemProvider_RemoveFromSelection);
}

IFACEMETHODIMP ProxyAccessible::get_IsSelected(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ISelectionItemProvider_get_IsSelected);
}

IFACEMETHODIMP ProxyAccessible::get_SelectionContainer(IRawElementProviderSimple** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_ISelectionItemProvider_get_SelectionContainer);
}

/***********************************************/
/*           IRangeValueProvider               */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::SetValue(double val) {
  IMPL_CALL_VOID(mid_IRangeValueProvider_SetValue, _double(val));
}

IFACEMETHODIMP ProxyAccessible::get_Value(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IRangeValueProvider_get_Value);
}

IFACEMETHODIMP ProxyAccessible::get_IsReadOnly(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_IRangeValueProvider_get_IsReadOnly);
}

IFACEMETHODIMP ProxyAccessible::get_Maximum(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IRangeValueProvider_get_Maximum);
}

IFACEMETHODIMP ProxyAccessible::get_Minimum(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IRangeValueProvider_get_Minimum);
}

IFACEMETHODIMP ProxyAccessible::get_LargeChange(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IRangeValueProvider_get_LargeChange);
}

IFACEMETHODIMP ProxyAccessible::get_SmallChange(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IRangeValueProvider_get_SmallChange);
}

/***********************************************/
/*           IValueProvider                    */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::SetValue(LPCWSTR val) {
  IMPL_CALL_VOID(mid_IValueProvider_SetValueString, _string(val));
}

IFACEMETHODIMP ProxyAccessible::get_Value(BSTR* pResult) {
  IMPL_CALL_BSTR(pResult, mid_IValueProvider_get_ValueString);
}

/***********************************************/
/*              ITextProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetVisibleRanges(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY_NO_ADDREF(ppResult, VT_UNKNOWN, mid_ITextProvider_GetVisibleRanges);
}

IFACEMETHODIMP ProxyAccessible::RangeFromChild(IRawElementProviderSimple* childElement, ITextRangeProvider** ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextProvider_RangeFromChild, (jlong)childElement);
}

IFACEMETHODIMP ProxyAccessible::RangeFromPoint(UiaPoint point, ITextRangeProvider** ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextProvider_RangeFromPoint, _double(point.x), _double(point.y));
}

IFACEMETHODIMP ProxyAccessible::get_DocumentRange(ITextRangeProvider** ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextProvider_get_DocumentRange);
}

IFACEMETHODIMP ProxyAccessible::get_SupportedTextSelection(SupportedTextSelection* pResult) {
  IMPL_CALL_ENUM(pResult, SupportedTextSelection, mid_ITextProvider_get_SupportedTextSelection);
}

/***********************************************/
/*              IGridProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_ColumnCount(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridProvider_get_ColumnCount);
}

IFACEMETHODIMP ProxyAccessible::get_RowCount(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridProvider_get_RowCount);
}

IFACEMETHODIMP ProxyAccessible::GetItem(int row, int column, IRawElementProviderSimple** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IGridProvider_GetItem, row, column);
}

/***********************************************/
/*              IGridItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_Column(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridItemProvider_get_Column);
}

IFACEMETHODIMP ProxyAccessible::get_ColumnSpan(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridItemProvider_get_ColumnSpan);
}

IFACEMETHODIMP ProxyAccessible::get_ContainingGrid(IRawElementProviderSimple** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IGridItemProvider_get_ContainingGrid);
}

IFACEMETHODIMP ProxyAccessible::get_Row(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridItemProvider_get_Row);
}

IFACEMETHODIMP ProxyAccessible::get_RowSpan(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IGridItemProvider_get_RowSpan);
}

/***********************************************/
/*              ITableProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetColumnHeaders(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_ITableProvider_GetColumnHeaders);
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaders(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_ITableProvider_GetRowHeaders);
}

IFACEMETHODIMP ProxyAccessible::get_RowOrColumnMajor(RowOrColumnMajor* pResult) {
  IMPL_CALL_ENUM(pResult, RowOrColumnMajor, mid_ITableProvider_get_RowOrColumnMajor);
}


/***********************************************/
/*              ITableItemProvider              */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::GetColumnHeaderItems(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_ITableItemProvider_GetColumnHeaderItems);
}

IFACEMETHODIMP ProxyAccessible::GetRowHeaderItems(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_ITableItemProvider_GetRowHeaderItems);
}


/***********************************************/
/*              IToggleProvider                */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Toggle() {
  IMPL_CALL_VOID(mid_IToggleProvider_Toggle);
}

IFACEMETHODIMP ProxyAccessible::get_ToggleState(ToggleState* pResult) {
  IMPL_CALL_ENUM(pResult, ToggleState, mid_IToggleProvider_get_ToggleState);
}

/***********************************************/
/*         IExpandCollapseProvider             */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Collapse() {
  IMPL_CALL_VOID(mid_IExpandCollapseProvider_Collapse);
}

IFACEMETHODIMP ProxyAccessible::Expand() {
  IMPL_CALL_VOID(mid_IExpandCollapseProvider_Expand);
}

IFACEMETHODIMP ProxyAccessible::get_ExpandCollapseState(ExpandCollapseState* pResult) {
  IMPL_CALL_ENUM(pResult, ExpandCollapseState, mid_IExpandCollapseProvider_get_ExpandCollapseState);
}

/***********************************************/
/*         ITransformProvider                  */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::get_CanMove(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ITransformProvider_get_CanMove);
}

IFACEMETHODIMP ProxyAccessible::get_CanResize(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ITransformProvider_get_CanResize);
}

IFACEMETHODIMP ProxyAccessible::get_CanRotate(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ITransformProvider_get_CanRotate);
}

IFACEMETHODIMP ProxyAccessible::Move(double x, double y) {
  IMPL_CALL_VOID(mid_ITransformProvider_Move, _double(x), _double(y));
}

IFACEMETHODIMP ProxyAccessible::Resize(double width, double height) {
  IMPL_CALL_VOID(mid_ITransformProvider_Resize, _double(width), _double(height));
}

IFACEMETHODIMP ProxyAccessible::Rotate(double degrees) {
  IMPL_CALL_VOID(mid_ITransformProvider_Rotate, degrees);
}

/***********************************************/
/*         IScrollProvider                     */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount) {
  IMPL_CALL_VOID(mid_IScrollProvider_Scroll, horizontalAmount, verticalAmount);
}

IFACEMETHODIMP ProxyAccessible::SetScrollPercent(double horizontalPercent, double verticalPercent) {
  IMPL_CALL_VOID(mid_IScrollProvider_SetScrollPercent, horizontalPercent, verticalPercent);
}

IFACEMETHODIMP ProxyAccessible::get_HorizontallyScrollable(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_IScrollProvider_get_HorizontallyScrollable);
}

IFACEMETHODIMP ProxyAccessible::get_HorizontalScrollPercent(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IScrollProvider_get_HorizontalScrollPercent);
}

IFACEMETHODIMP ProxyAccessible::get_HorizontalViewSize(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IScrollProvider_get_HorizontalViewSize);
}

IFACEMETHODIMP ProxyAccessible::get_VerticallyScrollable(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_IScrollProvider_get_VerticallyScrollable);
}

IFACEMETHODIMP ProxyAccessible::get_VerticalScrollPercent(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IScrollProvider_get_VerticalScrollPercent);
}

IFACEMETHODIMP ProxyAccessible::get_VerticalViewSize(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_IScrollProvider_get_VerticalViewSize);
}

/***********************************************/
/*         IScrollItemProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::ScrollIntoView() {
  IMPL_CALL_VOID(mid_IScrollItemProvider_ScrollIntoView);
}

/***********************************************/
/*         IWindowProvider                 */
/***********************************************/
IFACEMETHODIMP ProxyAccessible::Close() {
  IMPL_CALL_VOID(mid_WindowProvider_Close);
}
IFACEMETHODIMP ProxyAccessible::get_CanMaximize(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_WindowProvider_get_CanMaximize);
}
IFACEMETHODIMP ProxyAccessible::get_CanMinimize(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_WindowProvider_get_CanMinimize);
}
IFACEMETHODIMP ProxyAccessible::get_IsModal(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_WindowProvider_get_IsModal);
}
IFACEMETHODIMP ProxyAccessible::get_IsTopmost(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_WindowProvider_get_IsTopmost);
}
IFACEMETHODIMP ProxyAccessible::get_WindowInteractionState(WindowInteractionState* pResult) {
  IMPL_CALL_ENUM(pResult, WindowInteractionState, mid_WindowProvider_get_WindowInteractionState);
}
IFACEMETHODIMP ProxyAccessible::get_WindowVisualState(WindowVisualState* pResult) {
  IMPL_CALL_ENUM(pResult, WindowVisualState, mid_WindowProvider_get_WindowVisualState);
}
IFACEMETHODIMP ProxyAccessible::SetVisualState(WindowVisualState state) {
  IMPL_CALL_VOID(mid_WindowProvider_SetVisualState, state);
}
IFACEMETHODIMP ProxyAccessible::WaitForInputIdle(int milliseconds, BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_WindowProvider_WaitForInputIdle, (jint) milliseconds);
}

// IDockProvider
IFACEMETHODIMP ProxyAccessible::get_DockPosition(DockPosition* pResult) {
  IMPL_CALL_ENUM(pResult, DockPosition, mid_DockProvider_get_DockPosition);
}
IFACEMETHODIMP ProxyAccessible::SetDockPosition(DockPosition dockPosition) {
  IMPL_CALL_VOID(mid_DockProvider_SetDockPosition, (jint) dockPosition);
}
// IAnnotationProvider
IFACEMETHODIMP ProxyAccessible::get_AnnotationTypeId(int *pResult) {
  IMPL_CALL_INT(pResult, mid_IAnnotationProvider_get_AnnotationTypeId);
}
IFACEMETHODIMP ProxyAccessible::get_AnnotationTypeName(BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IAnnotationProvider_get_AnnotationTypeName);
}
IFACEMETHODIMP ProxyAccessible::get_Author(BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IAnnotationProvider_get_Author);
}
IFACEMETHODIMP ProxyAccessible::get_DateTime(BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IAnnotationProvider_get_DateTime);
}
IFACEMETHODIMP ProxyAccessible::get_Target(IRawElementProviderSimple** ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IAnnotationProvider_get_Target);
}
// IDragProvider
IFACEMETHODIMP ProxyAccessible::get_DropEffect(BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IDragProvider_get_DropEffect);
}
IFACEMETHODIMP ProxyAccessible::get_DropEffects(SAFEARRAY** ppResult) {
  // TODO need to support BSTR in array copy!!
  IMPL_CALL_SAFEARRAY(ppResult, VT_BSTR, mid_IDragProvider_get_DropEffects);
}
IFACEMETHODIMP ProxyAccessible::get_IsGrabbed(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_IDragProvider_get_IsGrabbed);
}
IFACEMETHODIMP ProxyAccessible::GetGrabbedItems(SAFEARRAY **ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_IDragProvider_GetGrabbedItems);
}
// IDropTargetProvider
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffect(BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IDropTargetProvider_get_DropTargetEffect);
}
IFACEMETHODIMP ProxyAccessible::get_DropTargetEffects(SAFEARRAY** ppResult) {
  // TODO need to support BSTR in array copy!!
  IMPL_CALL_SAFEARRAY(ppResult, VT_BSTR, mid_IDropTargetProvider_get_DropTargetEffects);
}
 // IItemContainerProvider
IFACEMETHODIMP ProxyAccessible::FindItemByProperty(IRawElementProviderSimple *pStartAfter, PROPERTYID propertyID, VARIANT value, IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_IItemContainerProvider_FindItemByProperty, _ptr(pStartAfter), _int(propertyID), _ptr(&value));
}
// IMultipleViewProvider
IFACEMETHODIMP ProxyAccessible::get_CurrentView(int *pResult) {
  IMPL_CALL_INT(pResult, mid_IMultipleViewProvider_get_CurrentView);
}
IFACEMETHODIMP ProxyAccessible::GetSupportedViews(SAFEARRAY** ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_I4, mid_IMultipleViewProvider_GetSupportedViews);
}
IFACEMETHODIMP ProxyAccessible::GetViewName(int viewId, BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_IMultipleViewProvider_GetViewName, _int(viewId));
}
IFACEMETHODIMP ProxyAccessible::SetCurrentView(int viewId) {
  IMPL_CALL_VOID(mid_IMultipleViewProvider_SetCurrentView, _int(viewId));
}
// ITextChildProvider
IFACEMETHODIMP ProxyAccessible::get_TextContainer(IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_ITextChildProvider_get_TextContainer);
}
IFACEMETHODIMP ProxyAccessible::get_TextRange(ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextChildProvider_get_TextRange);
}

// ITextProvider2
IFACEMETHODIMP ProxyAccessible::GetCaretRange(BOOL *pIsActive, ITextRangeProvider **ppResult) {
  IMPL_BEGIN
    assert_arg(pIsActive);
    assert_arg(ppResult);
    auto env = get_env();
    LocalFrame frame(env, 10);
    auto result = call_object(env, get_jobject(), mid_ITextProvider2_GetCaretRange);
    auto isActive = env->GetBooleanField(result, fid_isActive);
    check_and_throw(env);
    auto pointer = env->GetLongField(result, fid_range);
    check_and_throw(env);
    *pIsActive = isActive == JNI_TRUE ? TRUE : FALSE;
    *ppResult = reinterpret_cast<ProxyTextRangeProvider*>(pointer);
  IMPL_END
}

IFACEMETHODIMP ProxyAccessible::RangeFromAnnotation(IRawElementProviderSimple *annotationElement, ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextProvider2_RangeFromAnnotation, _ptr(annotationElement));
}

// ITextEditProvider
IFACEMETHODIMP ProxyAccessible::GetActiveComposition(ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextEditProvider_GetActiveComposition);
}
IFACEMETHODIMP ProxyAccessible::GetConversionTarget(ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_ITextEditProvider_GetConversionTarget);
}

// ITransformProvider2
IFACEMETHODIMP ProxyAccessible::get_CanZoom(BOOL* pResult) {
  IMPL_CALL_BOOL(pResult, mid_ITransformProvider2_get_CanZoom);
}
IFACEMETHODIMP ProxyAccessible::get_ZoomLevel(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_ITransformProvider2_get_ZoomLevel);
}
IFACEMETHODIMP ProxyAccessible::get_ZoomMinimum(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_ITransformProvider2_get_ZoomMinimum);
}
IFACEMETHODIMP ProxyAccessible::get_ZoomMaximum(double* pResult) {
  IMPL_CALL_DOUBLE(pResult, mid_ITransformProvider2_get_ZoomMaximum);
}
IFACEMETHODIMP ProxyAccessible::Zoom(double zoom) {
  IMPL_CALL_VOID(mid_ITransformProvider2_Zoom, _double(zoom));
}
IFACEMETHODIMP ProxyAccessible::ZoomByUnit(ZoomUnit zoomUnit) {
  IMPL_CALL_VOID(mid_ITransformProvider2_ZoomByUnit, _int(zoomUnit));
}

// IVirtualizedItemProvider
IFACEMETHODIMP ProxyAccessible::Realize() {
  IMPL_CALL_VOID(mid_IVirtualizedItemProvider_Realize);
}

// IStylesProvider
IFACEMETHODIMP ProxyAccessible::get_ExtendedProperties(BSTR* pResult) {
  IMPL_CALL_BSTR(pResult, mid_IStylesProvider_get_ExtendedProperties);
}
IFACEMETHODIMP ProxyAccessible::get_FillColor(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IStylesProvider_get_FillColor);
}
IFACEMETHODIMP ProxyAccessible::get_FillPatternColor(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IStylesProvider_get_FillPatternColor);
}
IFACEMETHODIMP ProxyAccessible::get_FillPatternStyle(BSTR* pResult) {
  IMPL_CALL_BSTR(pResult, mid_IStylesProvider_get_FillPatternStyle);
}
IFACEMETHODIMP ProxyAccessible::get_Shape(BSTR* pResult) {
  IMPL_CALL_BSTR(pResult, mid_IStylesProvider_get_Shape);
}
IFACEMETHODIMP ProxyAccessible::get_StyleId(int* pResult) {
  IMPL_CALL_INT(pResult, mid_IStylesProvider_get_StyleId);
}
IFACEMETHODIMP ProxyAccessible::get_StyleName(BSTR* pResult) {
  IMPL_CALL_BSTR(pResult, mid_IStylesProvider_get_StyleName);
}

// ISynchronizedInputProvider
IFACEMETHODIMP ProxyAccessible::Cancel() {
  IMPL_CALL_VOID(mid_ISynchronizedInputProvider_Cancel);
}
IFACEMETHODIMP ProxyAccessible::StartListening(SynchronizedInputType inputType) {
  IMPL_CALL_VOID( mid_ISynchronizedInputProvider_StartListening, _int(inputType));
}

// ISelectionProvider2
IFACEMETHODIMP ProxyAccessible::get_CurrentSelectedItem(IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_ISelectionProvider2_get_CurrentSelectedItem);
}
IFACEMETHODIMP ProxyAccessible::get_FirstSelectedItem(IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_ISelectionProvider2_get_FirstSelectedItem);
}
IFACEMETHODIMP ProxyAccessible::get_LastSelectedItem(IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_ISelectionProvider2_get_LastSelectedItem);
}
IFACEMETHODIMP ProxyAccessible::get_ItemCount(int *pResult) {
  IMPL_CALL_INT(pResult, mid_ISelectionProvider2_get_ItemCount);
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
    // JniUtil::initIDs(env);
    jni::ids::initIDs(env);

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
    jni::fill_bstr(env, displayString, &bstrDisplayString);

    BSTR bstrActivityId;
    jni::fill_bstr(env, activityId, &bstrActivityId);

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

    try {
      jni::fill_variant(env, oldV, &ov);
      jni::fill_variant(env, newV, &nv);
    } catch (HRESULT hr) {
      return (jlong)hr;
    }

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
