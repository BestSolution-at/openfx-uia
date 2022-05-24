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

#include "JniUtil.h"

#include "InstanceTracker.h"

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

  InstanceTracker::create(this);
  InstanceTracker::setType(this, "ProxyTextRangeProvider");
  InstanceTracker::setJava(this, jTextRangeProvider);
}

ProxyTextRangeProvider::~ProxyTextRangeProvider()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  if (SUCCEEDED(hr)) {
    env->DeleteGlobalRef(m_jTextRangeProvider);
    m_glassAccessible->Release();
    InstanceTracker::destroy(this);
  } else {
    print_hr(hr);
  }
}

/***********************************************/
/*                  IUnknown                   */
/***********************************************/
IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::AddRef()
{
  InstanceTracker::addRef(this);
  return InterlockedIncrement(&m_refCount);
}

IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::Release()
{
  InstanceTracker::release(this);
  long val = InterlockedDecrement(&m_refCount);
  if (val == 0) {
    JNIEnv* env = GetEnv();
    env->CallVoidMethod(m_jTextRangeProvider, mid_onNativeDelete);
    delete this;
  }
  return val;
}

IFACEMETHODIMP ProxyTextRangeProvider::QueryInterface(REFIID riid, void** ppInterface)
{
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
IFACEMETHODIMP ProxyTextRangeProvider::Clone(ITextRangeProvider **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jTextRangeProvider, mid_Clone, &pointer);
  return_on_fail(hr);

  ProxyTextRangeProvider* result = reinterpret_cast<ProxyTextRangeProvider*>(pointer);
  
  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::Compare(ITextRangeProvider *targetRange, BOOL *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  ProxyTextRangeProvider* target;
  hr = getTargetRange(targetRange, &target);
  return_on_fail(hr);

  jboolean result;
  hr = JniUtil::callBooleanMethod(env, m_jTextRangeProvider, mid_Compare, &result, target->m_jTextRangeProvider);
  return_on_fail(hr);

  *pResult = JniUtil::toBOOL(result);
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                                        TextPatternRangeEndpoint targetEndpoint, int *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  ProxyTextRangeProvider* target;
  hr = getTargetRange(targetRange, &target);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jTextRangeProvider, mid_CompareEndpoints, &result, endpoint, target->m_jTextRangeProvider, targetEndpoint);
  return_on_fail(hr);

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::ExpandToEnclosingUnit(TextUnit unit)
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_ExpandToEnclosingUnit, unit);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::FindAttribute(TEXTATTRIBUTEID attributeId, VARIANT val, BOOL backward, ITextRangeProvider **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;

  hr = GetEnv(&env);
  return_on_fail(hr)

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jTextRangeProvider, mid_FindAttribute2, &pointer, (jint) attributeId, (jlong) &val, JniUtil::toJBoolean(backward));
  return_on_fail(hr)

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::FindText(BSTR text, BOOL backward, BOOL ignoreCase, ITextRangeProvider **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  HRESULT hr;
  JNIEnv* env;

  hr = GetEnv(&env);
  return_on_fail(hr)

  jstring jText;
  hr = JniUtil::newStringFromBSTR(env, text, &jText);
  return_on_fail(hr);
  
  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jTextRangeProvider, mid_FindText, &pointer, jText, JniUtil::toJBoolean(backward), JniUtil::toJBoolean(ignoreCase));
  return_on_fail(hr)

  ITextRangeProvider* result = reinterpret_cast<ITextRangeProvider*>(pointer);
  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetAttributeValue(TEXTATTRIBUTEID attributeId, VARIANT *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;
  hr = GetEnv(&env);
  return_on_fail(hr);

  jobject result;
  hr = JniUtil::callObjectMethod(env, m_jTextRangeProvider, mid_GetAttributeValue, &result, attributeId);
  return_on_fail(hr);

  hr = JniUtil::copyVariant(env, result, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetBoundingRectangles(SAFEARRAY **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env = GetEnv();
  if (env == NULL) return E_FAIL;
  jarray bounds = (jarray)env->CallObjectMethod(m_jTextRangeProvider, mid_GetBoundingRectangles);
  if (CheckAndClearException(env)) return E_FAIL;

  return JniUtil::copyList(env, bounds, pResult, VT_R8);
}

IFACEMETHODIMP ProxyTextRangeProvider::GetEnclosingElement(IRawElementProviderSimple **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jlong pointer;
  hr = JniUtil::callLongMethod(env, m_jTextRangeProvider, mid_GetEnclosingElement, &pointer);
  return_on_fail(hr);

  IRawElementProviderSimple* result = reinterpret_cast<IRawElementProviderSimple*>(pointer);
  if (result) result->AddRef();

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetText(int maxLength, BSTR *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jstring result;
  hr = JniUtil::callStringMethod(env, m_jTextRangeProvider, mid_GetText, &result, maxLength);
  return_on_fail(hr);

  hr = JniUtil::copyString(env, result, pResult);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::Move(TextUnit unit, int count, int *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jTextRangeProvider, mid_Move, &result, unit, count);
  return_on_fail(hr);

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count, int *pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jint result;
  hr = JniUtil::callIntMethod(env, m_jTextRangeProvider, mid_MoveEndpointByUnit, &result, endpoint, unit, count);
  return_on_fail(hr);

  *pResult = result;
  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                                           TextPatternRangeEndpoint targetEndpoint)
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  ProxyTextRangeProvider* target;
  hr = getTargetRange(targetRange, &target);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_MoveEndpointByRange, endpoint, target->m_jTextRangeProvider, targetEndpoint);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::getTargetRange(ITextRangeProvider* targetRange, ProxyTextRangeProvider** pResult) {
  ProxyTextRangeProvider* result = reinterpret_cast<ProxyTextRangeProvider*>(targetRange);
  if (result == NULL || result->m_jTextRangeProvider == NULL) {
    return E_FAIL;
  }
  *pResult = result;
  return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::Select()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_Select);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::AddToSelection()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_AddToSelection);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::RemoveFromSelection()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_RemoveFromSelection);
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::ScrollIntoView(BOOL alignToTop)
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_ScrollIntoView, JniUtil::toJBoolean(alignToTop));
  return_on_fail(hr);

  return hr;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetChildren(SAFEARRAY **pResult)
{
  if (pResult == NULL) return E_INVALIDARG;

  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  jarray children;
  hr = JniUtil::callArrayMethod(env, m_jTextRangeProvider, mid_GetChildren, &children);
  return_on_fail(hr);

  hr = JniUtil::copyList(env, children, pResult, VT_UNKNOWN);
  return_on_fail(hr);

  return hr;
}

/***********************************************/
/*             ITextRangeProvider              */
/***********************************************/
IFACEMETHODIMP ProxyTextRangeProvider::ShowContextMenu()
{
  JNIEnv* env;
  HRESULT hr;

  hr = GetEnv(&env);
  return_on_fail(hr);

  hr = JniUtil::callVoidMethod(env, m_jTextRangeProvider, mid_ShowContextMenu);
  return_on_fail(hr);

  return hr;
}


/***********************************************/
/*                  JNI                        */
/***********************************************/

extern "C" JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_ProxyTextRangeProvider__1initIDs
  (JNIEnv *env, jclass jClass)
{
    mid_Clone = env->GetMethodID(jClass, "Clone", "()J");
    if (env->ExceptionCheck()) return;
    mid_Compare = env->GetMethodID(jClass, "Compare", "(Lcom/sun/glass/ui/uia/ProxyTextRangeProvider;)Z");
    if (env->ExceptionCheck()) return;
    mid_CompareEndpoints = env->GetMethodID(jClass, "CompareEndpoints", "(ILcom/sun/glass/ui/uia/ProxyTextRangeProvider;I)I");
    if (env->ExceptionCheck()) return;
    mid_ExpandToEnclosingUnit = env->GetMethodID(jClass, "ExpandToEnclosingUnit", "(I)V");
    if (env->ExceptionCheck()) return;
    mid_FindAttribute = env->GetMethodID(jClass, "FindAttribute", "(ILcom/sun/glass/ui/uia/glass/WinVariant;Z)J");
    if (env->ExceptionCheck()) return;
    mid_FindAttribute2 = env->GetMethodID(jClass, "FindAttribute", "(IJZ)J");
    if (env->ExceptionCheck()) return;
    mid_FindText = env->GetMethodID(jClass, "FindText", "(Ljava/lang/String;ZZ)J");
    if (env->ExceptionCheck()) return;
    mid_GetAttributeValue = env->GetMethodID(jClass, "GetAttributeValue", "(I)Lcom/sun/glass/ui/uia/glass/WinVariant;");
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
    mid_MoveEndpointByRange = env->GetMethodID(jClass, "MoveEndpointByRange", "(ILcom/sun/glass/ui/uia/ProxyTextRangeProvider;I)V");
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


extern "C" JNIEXPORT jlong JNICALL Java_com_sun_glass_ui_uia_ProxyTextRangeProvider__1createTextRangeProvider
  (JNIEnv *env, jobject jTextRangeProvider, jlong proxyAccessible)
{
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(proxyAccessible);
    if (acc == NULL) return NULL;
    ProxyTextRangeProvider* provider = new (std::nothrow) ProxyTextRangeProvider(env, jTextRangeProvider, acc);
    return reinterpret_cast<jlong>(provider);
}


extern "C" JNIEXPORT void JNICALL Java_com_sun_glass_ui_uia_ProxyTextRangeProvider__1destroyTextRangeProvider
  (JNIEnv *env, jobject object, jlong provider)
{
    ProxyTextRangeProvider* p = reinterpret_cast<ProxyTextRangeProvider*>(provider);
    p->Release();
}