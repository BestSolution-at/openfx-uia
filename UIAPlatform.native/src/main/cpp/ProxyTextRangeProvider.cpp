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

/* TextRangeProvider Method IDs */
static jmethodID mid_Clone;
static jmethodID mid_Compare;
static jmethodID mid_CompareEndpoints;
static jmethodID mid_ExpandToEnclosingUnit;
static jmethodID mid_FindAttribute;
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

static jmethodID mid_ShowContextMenu;

ProxyTextRangeProvider::ProxyTextRangeProvider(JNIEnv* env, jobject jTextRangeProvider, ProxyAccessible* glassAccessible)
: m_refCount(1)
{
    m_jTextRangeProvider = env->NewGlobalRef(jTextRangeProvider);
    m_glassAccessible = glassAccessible;
    m_glassAccessible->AddRef();
}

ProxyTextRangeProvider::~ProxyTextRangeProvider()
{
    JNIEnv* env = GetEnv();
    if (env) env->DeleteGlobalRef(m_jTextRangeProvider);
    m_glassAccessible->Release();
}

/***********************************************/
/*                  IUnknown                   */
/***********************************************/
IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::AddRef()
{
    return InterlockedIncrement(&m_refCount);
}

IFACEMETHODIMP_(ULONG) ProxyTextRangeProvider::Release()
{
    long val = InterlockedDecrement(&m_refCount);
    if (val == 0) {
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
IFACEMETHODIMP ProxyTextRangeProvider::Clone(ITextRangeProvider **pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jTextRangeProvider, mid_Clone);
    if (CheckAndClearException(env)) return E_FAIL;
    ProxyTextRangeProvider* gtrp = reinterpret_cast<ProxyTextRangeProvider*>(ptr);

    /* This code is intentionally commented.
     * JavaFX returns a new ITextRangeProvider instance each time.
     * The caller holds the only reference to this object.
     */
//    if (gtrp) gtrp->AddRef();

    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::Compare(ITextRangeProvider *range, BOOL *pRetVal)
{
    ProxyTextRangeProvider* proxyRange = reinterpret_cast<ProxyTextRangeProvider*>(range);
    if (proxyRange == NULL ||  proxyRange->m_jTextRangeProvider == NULL) {
        *pRetVal = FALSE;
        return S_OK;
    }
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallBooleanMethod(m_jTextRangeProvider, mid_Compare, proxyRange->m_jTextRangeProvider);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                                        TextPatternRangeEndpoint targetEndpoint, int *pRetVal)
{
    ProxyTextRangeProvider* proxyRange = reinterpret_cast<ProxyTextRangeProvider*>(targetRange);
    if (proxyRange == NULL ||  proxyRange->m_jTextRangeProvider == NULL) {
        *pRetVal = FALSE;
        return S_OK;
    }
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jTextRangeProvider, mid_CompareEndpoints, endpoint, proxyRange->m_jTextRangeProvider, targetEndpoint);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::ExpandToEnclosingUnit(TextUnit unit)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_ExpandToEnclosingUnit, unit);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::FindAttribute(TEXTATTRIBUTEID attributeId, VARIANT val, BOOL backward, ITextRangeProvider **pRetVal)
{
    //TODO VAL TO JVAL
    jobject jVal = NULL;
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jTextRangeProvider, mid_FindAttribute, attributeId, jVal, backward);
    if (CheckAndClearException(env)) return E_FAIL;
    ProxyTextRangeProvider* gtrp = reinterpret_cast<ProxyTextRangeProvider*>(ptr);

    /* This code is intentionally commented.
     * JavaFX returns a new ITextRangeProvider instance each time.
     * The caller holds the only reference to this object.
     */
//    if (gtrp) gtrp->AddRef();

    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::FindText(BSTR text, BOOL backward, BOOL ignoreCase, ITextRangeProvider **pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jsize length = SysStringLen(text);
    jstring jText = env->NewString((const jchar *)text, length);
    jlong ptr = env->CallLongMethod(m_jTextRangeProvider, mid_FindText, jText, backward, ignoreCase);
    if (CheckAndClearException(env)) return E_FAIL;
    ProxyTextRangeProvider* gtrp = reinterpret_cast<ProxyTextRangeProvider*>(ptr);

    /* This code is intentionally commented.
     * JavaFX returns a new ITextRangeProvider instance each time.
     * The caller holds the only reference to this object.
     */
//    if (gtrp) gtrp->AddRef();

    *pRetVal = static_cast<ITextRangeProvider*>(gtrp);
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetAttributeValue(TEXTATTRIBUTEID attributeId, VARIANT *pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jobject jVariant = env->CallObjectMethod(m_jTextRangeProvider, mid_GetAttributeValue, attributeId);
    if (CheckAndClearException(env)) return E_FAIL;

    return ProxyAccessible::copyVariant(env, jVariant, pRetVal);
}

IFACEMETHODIMP ProxyTextRangeProvider::GetBoundingRectangles(SAFEARRAY **pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jarray bounds = (jarray)env->CallObjectMethod(m_jTextRangeProvider, mid_GetBoundingRectangles);
    if (CheckAndClearException(env)) return E_FAIL;

    return ProxyAccessible::copyList(env, bounds, pRetVal, VT_R8);
}

IFACEMETHODIMP ProxyTextRangeProvider::GetEnclosingElement(IRawElementProviderSimple **pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jlong ptr = env->CallLongMethod(m_jTextRangeProvider, mid_GetEnclosingElement);
    if (CheckAndClearException(env)) return E_FAIL;
    ProxyAccessible* acc = reinterpret_cast<ProxyAccessible*>(ptr);

    /* AddRef the result */
    if (acc) acc->AddRef();

    *pRetVal = static_cast<IRawElementProviderSimple*>(acc);
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetText(int maxLength, BSTR *pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jstring string = (jstring)env->CallObjectMethod(m_jTextRangeProvider, mid_GetText, maxLength);
    if (CheckAndClearException(env)) return E_FAIL;

    return ProxyAccessible::copyString(env, string, pRetVal);
}

IFACEMETHODIMP ProxyTextRangeProvider::Move(TextUnit unit, int count, int *pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jTextRangeProvider, mid_Move, unit, count);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count, int *pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    *pRetVal = env->CallIntMethod(m_jTextRangeProvider, mid_MoveEndpointByUnit, endpoint, unit, count);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                                           TextPatternRangeEndpoint targetEndpoint)
{

    ProxyTextRangeProvider* glassRange = reinterpret_cast<ProxyTextRangeProvider*>(targetRange);
    if (glassRange == NULL ||  glassRange->m_jTextRangeProvider == NULL) {
        return S_OK;
    }
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_MoveEndpointByRange, endpoint, glassRange->m_jTextRangeProvider, targetEndpoint);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::Select()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_Select);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::AddToSelection()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_AddToSelection);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::RemoveFromSelection()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_RemoveFromSelection);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::ScrollIntoView(BOOL alignToTop)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_ScrollIntoView, alignToTop);
    if (CheckAndClearException(env)) return E_FAIL;
    return S_OK;
}

IFACEMETHODIMP ProxyTextRangeProvider::GetChildren(SAFEARRAY **pRetVal)
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    jarray children = (jarray)env->CallObjectMethod(m_jTextRangeProvider, mid_GetChildren);
    if (CheckAndClearException(env)) return E_FAIL;

    return ProxyAccessible::copyList(env, children, pRetVal, VT_UNKNOWN);
}

/***********************************************/
/*             ITextRangeProvider              */
/***********************************************/
IFACEMETHODIMP ProxyTextRangeProvider::ShowContextMenu()
{
    JNIEnv* env = GetEnv();
    if (env == NULL) return E_FAIL;
    env->CallVoidMethod(m_jTextRangeProvider, mid_ShowContextMenu);
    if (CheckAndClearException(env)) return E_FAIL;

     return S_OK;
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