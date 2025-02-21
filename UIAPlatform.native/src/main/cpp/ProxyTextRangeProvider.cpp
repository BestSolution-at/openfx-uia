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
#include "ProxyTextRangeProvider.h"
#include "ProxyAccessible.h"


#include "InstanceTracker.h"

#include "JniUtil.h"
#include <JniImpl.h>

using namespace jni;

/* TextRangeProvider Method IDs */
static jmethodID mid_Clone;
static jmethodID mid_Compare;
static jmethodID mid_CompareEndpoints;
static jmethodID mid_ExpandToEnclosingUnit;
static jmethodID mid_FindAttribute;
static jmethodID mid_FindAttribute2;
static jmethodID mid_FindText;
static jmethodID mid_GetAttributeValue;
static jmethodID mid_GetBoundingRectangles;
static jmethodID mid_GetEnclosingElement;
static jmethodID mid_GetText;
static jmethodID mid_Move;
static jmethodID mid_MoveEndpointByUnit;
static jmethodID mid_MoveEndpointByRange;
static jmethodID mid_Select;
static jmethodID mid_AddToSelection;
static jmethodID mid_RemoveFromSelection;
static jmethodID mid_ScrollIntoView;
static jmethodID mid_GetChildren;

static jmethodID mid_onNativeDelete;

static jmethodID mid_ShowContextMenu;

ProxyTextRangeProvider::ProxyTextRangeProvider(JNIEnv* env, jobject jTextRangeProvider, ProxyAccessible* glassAccessible)
: m_refCount(1)
{
  m_jTextRangeProvider = env->NewGlobalRef(jTextRangeProvider);
  m_glassAccessible = glassAccessible;
  m_glassAccessible->AddRef();

  InstanceTracker::Get().create(this);
  InstanceTracker::Get().setType(this, "ProxyTextRangeProvider");
  InstanceTracker::Get().setJava(this, jTextRangeProvider);
}

ProxyTextRangeProvider::~ProxyTextRangeProvider()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  if (SUCCEEDED(hr)) {
    env->DeleteGlobalRef(m_jTextRangeProvider);
    m_glassAccessible->Release();
    InstanceTracker::Get().destroy(this);
  } else {
    print_hr(hr);
  }
}

jobject ProxyTextRangeProvider::get_jobject() {
  return m_jTextRangeProvider;
}

/***********************************************/
/*                  IUnknown                   */
/***********************************************/
IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::AddRef() {
  InstanceTracker::Get().addRef(this);
  return InterlockedIncrement(&m_refCount);
}

IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::Release() {
  InstanceTracker::Get().release(this);
  long val = InterlockedDecrement(&m_refCount);
  if (val == 0) {
    JNIEnv* env = GetEnv();
    env->CallVoidMethod(m_jTextRangeProvider, mid_onNativeDelete);
    delete this;
  }
  return val;
}

IFACEMETHODIMP ProxyTextRangeProvider::QueryInterface(REFIID riid, void** ppInterface) {
  if (riid == __uuidof(IUnknown)) {
      *ppInterface = static_cast<ITextRangeProvider*>(this);
  } else if (riid == __uuidof(ITextRangeProvider)) {
      *ppInterface = static_cast<ITextRangeProvider*>(this);
  } else if (riid == __uuidof(ITextRangeProvider2)) {
      *ppInterface = static_cast<ITextRangeProvider2*>(this);
  } else {
      *ppInterface = NULL;
      return E_NOINTERFACE;
  }

  this->AddRef();
  return S_OK;
}

/***********************************************/
/*             ITextRangeProvider              */
/***********************************************/
IFACEMETHODIMP ProxyTextRangeProvider::Clone(ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_Clone);
}

IFACEMETHODIMP ProxyTextRangeProvider::Compare(ITextRangeProvider *targetRange, BOOL *pResult) {
  IMPL_CALL_BOOL(pResult, mid_Compare, _object(targetRange));
}

IFACEMETHODIMP ProxyTextRangeProvider::CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange, TextPatternRangeEndpoint targetEndpoint, int *pResult) {
  IMPL_CALL_INT(pResult, mid_CompareEndpoints, _int(endpoint), _object(targetRange), _int(targetEndpoint));
}

IFACEMETHODIMP ProxyTextRangeProvider::ExpandToEnclosingUnit(TextUnit unit) {
  IMPL_CALL_VOID(mid_ExpandToEnclosingUnit, _int(unit));
}

IFACEMETHODIMP ProxyTextRangeProvider::FindAttribute(TEXTATTRIBUTEID attributeId, VARIANT val, BOOL backward, ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_FindAttribute2, _int(attributeId), _ptr(&val), _boolean(backward));
}

IFACEMETHODIMP ProxyTextRangeProvider::FindText(BSTR text, BOOL backward, BOOL ignoreCase, ITextRangeProvider **ppResult) {
  IMPL_CALL_TEXTRANGE(ppResult, mid_FindText, _string(text), _boolean(backward), _boolean(ignoreCase));
}

IFACEMETHODIMP ProxyTextRangeProvider::GetAttributeValue(TEXTATTRIBUTEID attributeId, VARIANT *pResult) {
  IMPL_CALL_VARIANT(pResult, mid_GetAttributeValue, _int(attributeId));
}

IFACEMETHODIMP ProxyTextRangeProvider::GetBoundingRectangles(SAFEARRAY **ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_R8, mid_GetBoundingRectangles);
}

IFACEMETHODIMP ProxyTextRangeProvider::GetEnclosingElement(IRawElementProviderSimple **ppResult) {
  IMPL_CALL_ACCESSIBLE(ppResult, mid_GetEnclosingElement);
}

IFACEMETHODIMP ProxyTextRangeProvider::GetText(int maxLength, BSTR *pResult) {
  IMPL_CALL_BSTR(pResult, mid_GetText, _int(maxLength));
}

IFACEMETHODIMP ProxyTextRangeProvider::Move(TextUnit unit, int count, int *pResult) {
  IMPL_CALL_INT(pResult, mid_Move, _int(unit), _int(count));
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count, int *pResult) {
  IMPL_CALL_INT(pResult, mid_MoveEndpointByUnit, _int(endpoint), _int(unit), _int(count));
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange, TextPatternRangeEndpoint targetEndpoint) {
  IMPL_CALL_VOID(mid_MoveEndpointByRange, _int(endpoint), _object(targetRange), _int(targetEndpoint));
}

IFACEMETHODIMP ProxyTextRangeProvider::Select() {
  IMPL_CALL_VOID(mid_Select);
}

IFACEMETHODIMP ProxyTextRangeProvider::AddToSelection() {
  IMPL_CALL_VOID(mid_AddToSelection);
}

IFACEMETHODIMP ProxyTextRangeProvider::RemoveFromSelection() {
  IMPL_CALL_VOID(mid_RemoveFromSelection);
}

IFACEMETHODIMP ProxyTextRangeProvider::ScrollIntoView(BOOL alignToTop) {
  IMPL_CALL_VOID(mid_ScrollIntoView, _boolean(alignToTop));
}

IFACEMETHODIMP ProxyTextRangeProvider::GetChildren(SAFEARRAY **ppResult) {
  IMPL_CALL_SAFEARRAY(ppResult, VT_UNKNOWN, mid_GetChildren);
}

/***********************************************/
/*             ITextRangeProvider              */
/***********************************************/
IFACEMETHODIMP ProxyTextRangeProvider::ShowContextMenu() {
  IMPL_CALL_VOID(mid_ShowContextMenu);
}

/***********************************************/
/*                  JNI                        */
/***********************************************/

extern "C" JNIEXPORT void JNICALL Java_at_bestsolution_uia_ProxyTextRangeProvider__1initIDs
  (JNIEnv *env, jclass jClass)
{
    mid_Clone = env->GetMethodID(jClass, "Clone", "()J");
    if (env->ExceptionCheck()) return;
    mid_Compare = env->GetMethodID(jClass, "Compare", "(Lat/bestsolution/uia/ProxyTextRangeProvider;)Z");
    if (env->ExceptionCheck()) return;
    mid_CompareEndpoints = env->GetMethodID(jClass, "CompareEndpoints", "(ILat/bestsolution/uia/ProxyTextRangeProvider;I)I");
    if (env->ExceptionCheck()) return;
    mid_ExpandToEnclosingUnit = env->GetMethodID(jClass, "ExpandToEnclosingUnit", "(I)V");
    if (env->ExceptionCheck()) return;
    mid_FindAttribute = env->GetMethodID(jClass, "FindAttribute", "(ILat/bestsolution/uia/glass/WinVariant;Z)J");
    if (env->ExceptionCheck()) return;
    mid_FindAttribute2 = env->GetMethodID(jClass, "FindAttribute", "(IJZ)J");
    if (env->ExceptionCheck()) return;
    mid_FindText = env->GetMethodID(jClass, "FindText", "(Ljava/lang/String;ZZ)J");
    if (env->ExceptionCheck()) return;
    mid_GetAttributeValue = env->GetMethodID(jClass, "GetAttributeValue", "(I)Lat/bestsolution/uia/glass/WinVariant;");
    if (env->ExceptionCheck()) return;
    mid_GetBoundingRectangles = env->GetMethodID(jClass, "GetBoundingRectangles", "()[D");
    if (env->ExceptionCheck()) return;
    mid_GetEnclosingElement = env->GetMethodID(jClass, "GetEnclosingElement", "()J");
    if (env->ExceptionCheck()) return;
    mid_GetText = env->GetMethodID(jClass, "GetText", "(I)Ljava/lang/String;");
    if (env->ExceptionCheck()) return;
    mid_Move = env->GetMethodID(jClass, "Move", "(II)I");
    if (env->ExceptionCheck()) return;
    mid_MoveEndpointByUnit = env->GetMethodID(jClass, "MoveEndpointByUnit", "(III)I");
    if (env->ExceptionCheck()) return;
    mid_MoveEndpointByRange = env->GetMethodID(jClass, "MoveEndpointByRange", "(ILat/bestsolution/uia/ProxyTextRangeProvider;I)V");
    if (env->ExceptionCheck()) return;
    mid_Select = env->GetMethodID(jClass, "Select", "()V");
    if (env->ExceptionCheck()) return;
    mid_AddToSelection = env->GetMethodID(jClass, "AddToSelection", "()V");
    if (env->ExceptionCheck()) return;
    mid_RemoveFromSelection = env->GetMethodID(jClass, "RemoveFromSelection", "()V");
    if (env->ExceptionCheck()) return;
    mid_ScrollIntoView = env->GetMethodID(jClass, "ScrollIntoView", "(Z)V");
    if (env->ExceptionCheck()) return;
    mid_GetChildren = env->GetMethodID(jClass, "GetChildren", "()[J");
    if (env->ExceptionCheck()) return;

    mid_ShowContextMenu = env->GetMethodID(jClass, "ShowContextMenu", "()V");
    if (env->ExceptionCheck()) return;

    mid_onNativeDelete = env->GetMethodID(jClass, "onNativeDelete", "()V");
    if (env->ExceptionCheck()) return;
}


extern "C" JNIEXPORT jlong JNICALL Java_at_bestsolution_uia_ProxyTextRangeProvider__1createTextRangeProvider
  (JNIEnv *env, jobject jTextRangeProvider, jlong proxyAccessible)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(proxyAccessible);
    if (acc == NULL) return NULL;
    ProxyTextRangeProvider* provider = new (std::nothrow) ProxyTextRangeProvider(env, jTextRangeProvider, acc);
    return reinterpret_cast<jlong>(provider);
}


extern "C" JNIEXPORT void JNICALL Java_at_bestsolution_uia_ProxyTextRangeProvider__1destroyTextRangeProvider
  (JNIEnv *env, jobject object, jlong provider)
{
    ProxyTextRangeProvider* p = reinterpret_cast<ProxyTextRangeProvider*>(provider);
    p->Release();
}
