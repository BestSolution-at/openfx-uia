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
#ifndef _PROXY_TEXTRANGEPROVIDER_
#define _PROXY_TEXTRANGEPROVIDER_


#include <UIAutomation.h>
#include "ProxyAccessible.h"

class ProxyTextRangeProvider : /*public ITextRangeProvider,*/ public ITextRangeProvider2 {

public:
    ProxyTextRangeProvider(JNIEnv* env, jobject jTextRangeProvider, ProxyAccessible* glassProvider);

    // IUnknown methods
    IFACEMETHODIMP_(ULONG) AddRef();
    IFACEMETHODIMP_(ULONG) Release();
    IFACEMETHODIMP QueryInterface(REFIID riid, void**);

    // ITextRangeProvider
    IFACEMETHODIMP Clone(ITextRangeProvider **pRetVal);
    IFACEMETHODIMP Compare(ITextRangeProvider *range, BOOL *pRetVal);
    IFACEMETHODIMP CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                    TextPatternRangeEndpoint targetEndpoint, int *pRetVal);
    IFACEMETHODIMP ExpandToEnclosingUnit(TextUnit unit);
    IFACEMETHODIMP FindAttribute(TEXTATTRIBUTEID attributeId, VARIANT val, BOOL backward, ITextRangeProvider **pRetVal);
    IFACEMETHODIMP FindText(BSTR text, BOOL backward, BOOL ignoreCase, ITextRangeProvider **pRetVal);
    IFACEMETHODIMP GetAttributeValue(TEXTATTRIBUTEID attributeId, VARIANT *pRetVal);
    IFACEMETHODIMP GetBoundingRectangles(SAFEARRAY * *pRetVal);
    IFACEMETHODIMP GetEnclosingElement(IRawElementProviderSimple **pRetVal);
    IFACEMETHODIMP GetText(int maxLength, BSTR *pRetVal);
    IFACEMETHODIMP Move(TextUnit unit, int count, int *pRetVal);
    IFACEMETHODIMP MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count, int *pRetVal);
    IFACEMETHODIMP MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider *targetRange,
                                       TextPatternRangeEndpoint targetEndpoint);
    IFACEMETHODIMP Select();
    IFACEMETHODIMP AddToSelection();
    IFACEMETHODIMP RemoveFromSelection();
    IFACEMETHODIMP ScrollIntoView(BOOL alignToTop);
    IFACEMETHODIMP GetChildren(SAFEARRAY * *pRetVal);

    // ITextRangeProvider2
    IFACEMETHODIMP ShowContextMenu();

private:
    virtual ~ProxyTextRangeProvider();

    ULONG m_refCount;
    jobject m_jTextRangeProvider;  // The GlobalRef Java side object
    ProxyAccessible* m_glassAccessible;

    IFACEMETHODIMP getTargetRange(ITextRangeProvider* targetRange, ProxyTextRangeProvider** pResult);
};



#endif // _PROXY_TEXTRANGEPROVIDER
