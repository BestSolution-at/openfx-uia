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


#ifndef _UIA_PROXY_ACCESSIBLE_
#define _UIA_PROXY_ACCESSIBLE_

#include <UIAutomation.h>
#include <jni.h>

class ProxyAccessible : public IRawElementProviderSimple,
    public IRawElementProviderFragment,
    public IRawElementProviderFragmentRoot,
    public IRawElementProviderAdviseEvents,
    public IInvokeProvider,
    public ISelectionProvider,
    public ISelectionProvider2,
    public ISelectionItemProvider,
    public IRangeValueProvider,
    public IValueProvider,
    public ITextProvider,
    public ITextProvider2,
    public ITextEditProvider,
    public ITextChildProvider,
    public IGridProvider,
    public IGridItemProvider,
    public ITableProvider,
    public ITableItemProvider,
    public IToggleProvider,
    public IExpandCollapseProvider,
    public ITransformProvider,
    public ITransformProvider2,
    public IScrollProvider,
    public IScrollItemProvider,
    public IWindowProvider,
    public IDockProvider,
    public IAnnotationProvider,
    public IDragProvider,
    public IDropTargetProvider,
    public IItemContainerProvider,
    public IMultipleViewProvider,
    public ISynchronizedInputProvider,
    public IVirtualizedItemProvider,
    public IStylesProvider
{

public:
    ProxyAccessible(JNIEnv* env, jobject jAccessible);

    // IUnknown methods
    IFACEMETHODIMP_(ULONG) AddRef();
    IFACEMETHODIMP_(ULONG) Release();
    IFACEMETHODIMP QueryInterface(REFIID riid, void**);

    // IRawElementProviderSimple properties and methods
    IFACEMETHODIMP get_HostRawElementProvider(IRawElementProviderSimple** pRetVal);
    IFACEMETHODIMP get_ProviderOptions(ProviderOptions* pRetVal);
    IFACEMETHODIMP GetPatternProvider(PATTERNID patternId, IUnknown** pRetVal);
    IFACEMETHODIMP GetPropertyValue(PROPERTYID propertyId, VARIANT* pRetVal);

    // IRawElementProviderFragment properties and methods
    IFACEMETHODIMP get_BoundingRectangle(UiaRect* pRetVal);
    IFACEMETHODIMP get_FragmentRoot(IRawElementProviderFragmentRoot** pRetVal);
    IFACEMETHODIMP GetEmbeddedFragmentRoots(SAFEARRAY** pRetVal);
    IFACEMETHODIMP GetRuntimeId(SAFEARRAY** pRetVal);
    IFACEMETHODIMP Navigate(NavigateDirection direction, IRawElementProviderFragment** pRetVal);
    IFACEMETHODIMP SetFocus();

    // IRawElementProviderFragmentRoot methods
    IFACEMETHODIMP ElementProviderFromPoint(double x, double y, IRawElementProviderFragment** pRetVal);
    IFACEMETHODIMP GetFocus(IRawElementProviderFragment** pRetVal);

    // IRawElementProviderAdviseEvents
    IFACEMETHODIMP AdviseEventAdded(EVENTID eventId, SAFEARRAY* propertyIDs);
    IFACEMETHODIMP AdviseEventRemoved(EVENTID eventId, SAFEARRAY* propertyIDs);

    // IInvokeProvider
    IFACEMETHODIMP Invoke();

    // ISelectionProvider
    IFACEMETHODIMP GetSelection(SAFEARRAY** pRetVal);
    IFACEMETHODIMP get_CanSelectMultiple(BOOL* pRetVal);
    IFACEMETHODIMP get_IsSelectionRequired(BOOL* pRetVal);

    // ISelectionItemProvider
    IFACEMETHODIMP Select();
    IFACEMETHODIMP AddToSelection();
    IFACEMETHODIMP RemoveFromSelection();
    IFACEMETHODIMP get_IsSelected(BOOL* pRetVal);
    IFACEMETHODIMP get_SelectionContainer(IRawElementProviderSimple** pRetVal);

    // IRangeValueProvider
    IFACEMETHODIMP SetValue(double val);
    IFACEMETHODIMP get_Value(double* pRetVal);
    IFACEMETHODIMP get_IsReadOnly(BOOL* pRetVal);
    IFACEMETHODIMP get_Maximum(double* pRetVal);
    IFACEMETHODIMP get_Minimum(double* pRetVal);
    IFACEMETHODIMP get_LargeChange(double* pRetVal);
    IFACEMETHODIMP get_SmallChange(double* pRetVal);

    // IValueProvider
    IFACEMETHODIMP SetValue(LPCWSTR val);
    IFACEMETHODIMP get_Value(BSTR* pRetVal);
    //    IFACEMETHODIMP get_IsReadOnly(BOOL *pRetVal); /* Defined in IRangeValueProvider */

        // ITextProvider
    //    IFACEMETHODIMP GetSelection(SAFEARRAY **pRetVal); /* Defined in ISelectionProvider */
    IFACEMETHODIMP GetVisibleRanges(SAFEARRAY** pRetVal);
    IFACEMETHODIMP RangeFromChild(IRawElementProviderSimple* childElement, ITextRangeProvider** pRetVal);
    IFACEMETHODIMP RangeFromPoint(UiaPoint point, ITextRangeProvider** pRetVal);
    IFACEMETHODIMP get_DocumentRange(ITextRangeProvider** pRetVal);
    IFACEMETHODIMP get_SupportedTextSelection(SupportedTextSelection* pRetVal);

    // IGridProvider
    IFACEMETHODIMP get_ColumnCount(int* pRetVal);
    IFACEMETHODIMP get_RowCount(int* pRetVal);
    IFACEMETHODIMP GetItem(int row, int column, IRawElementProviderSimple** pRetVal);

    // IGridItemProvider
    IFACEMETHODIMP get_Column(int* pRetVal);
    IFACEMETHODIMP get_ColumnSpan(int* pRetVal);
    IFACEMETHODIMP get_ContainingGrid(IRawElementProviderSimple** pRetVal);
    IFACEMETHODIMP get_Row(int* pRetVal);
    IFACEMETHODIMP get_RowSpan(int* pRetVal);

    // ITableProvider
    IFACEMETHODIMP GetColumnHeaders(SAFEARRAY** pRetVal);
    IFACEMETHODIMP GetRowHeaders(SAFEARRAY** pRetVal);
    IFACEMETHODIMP get_RowOrColumnMajor(RowOrColumnMajor* pRetVal);

    // ITableItemProvider
    IFACEMETHODIMP GetColumnHeaderItems(SAFEARRAY** pRetVal);
    IFACEMETHODIMP GetRowHeaderItems(SAFEARRAY** pRetVal);

    // IToggleProvider
    IFACEMETHODIMP Toggle();
    IFACEMETHODIMP get_ToggleState(ToggleState* pRetVal);

    // IExpandCollapseProvider
    IFACEMETHODIMP Collapse();
    IFACEMETHODIMP Expand();
    IFACEMETHODIMP get_ExpandCollapseState(ExpandCollapseState* pRetVal);

    // ITransformProvider
    IFACEMETHODIMP get_CanMove(BOOL* pRetVal);
    IFACEMETHODIMP get_CanResize(BOOL* pRetVal);
    IFACEMETHODIMP get_CanRotate(BOOL* pRetVal);
    IFACEMETHODIMP Move(double x, double y);
    IFACEMETHODIMP Resize(double width, double height);
    IFACEMETHODIMP Rotate(double degrees);

    // IScrollProvider
    IFACEMETHODIMP Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount);
    IFACEMETHODIMP SetScrollPercent(double horizontalPercent, double verticalPercent);
    IFACEMETHODIMP get_HorizontallyScrollable(BOOL* pRetVal);
    IFACEMETHODIMP get_HorizontalScrollPercent(double* pRetVal);
    IFACEMETHODIMP get_HorizontalViewSize(double* pRetVal);
    IFACEMETHODIMP get_VerticallyScrollable(BOOL* pRetVal);
    IFACEMETHODIMP get_VerticalScrollPercent(double* pRetVal);
    IFACEMETHODIMP get_VerticalViewSize(double* pRetVal);

    // IScrollItemProvider
    IFACEMETHODIMP ScrollIntoView();

    // IWindowProvider
    IFACEMETHODIMP Close();
    IFACEMETHODIMP get_CanMaximize(BOOL* pRetVal);
    IFACEMETHODIMP get_CanMinimize(BOOL* pRetVal);
    IFACEMETHODIMP get_IsModal(BOOL* pRetVal);
    IFACEMETHODIMP get_IsTopmost(BOOL* pRetVal);
    IFACEMETHODIMP get_WindowInteractionState(WindowInteractionState* pRetVal);
    IFACEMETHODIMP get_WindowVisualState(WindowVisualState* pRetVal);
    IFACEMETHODIMP SetVisualState(WindowVisualState state);
    IFACEMETHODIMP WaitForInputIdle(int milliseconds, BOOL* pRetVal);

    // IDockProvider
    IFACEMETHODIMP get_DockPosition(DockPosition *pRetVal);
    IFACEMETHODIMP SetDockPosition(DockPosition dockPosition);
    // IAnnotationProvider
    IFACEMETHODIMP get_AnnotationTypeId(int *pRetVal);
    IFACEMETHODIMP get_AnnotationTypeName(BSTR *pRetVal);
    IFACEMETHODIMP get_Author(BSTR *pRetVal);
    IFACEMETHODIMP get_DateTime(BSTR *pRetVal);
    IFACEMETHODIMP get_Target(IRawElementProviderSimple **pRetVal);
    // IDragProvider
    IFACEMETHODIMP get_DropEffect(BSTR *pRetVal);
    IFACEMETHODIMP get_DropEffects(SAFEARRAY **pRetVal);
    IFACEMETHODIMP get_IsGrabbed(BOOL* pRetVal);
    IFACEMETHODIMP GetGrabbedItems(SAFEARRAY **pRetVal);
    // IDropTargetProvider
    IFACEMETHODIMP get_DropTargetEffect(BSTR *pRetVal);
    IFACEMETHODIMP get_DropTargetEffects(SAFEARRAY **pRetVal);
    // IItemContainerProvider
    IFACEMETHODIMP FindItemByProperty(IRawElementProviderSimple *pStartAfter, PROPERTYID propertyID, VARIANT value, IRawElementProviderSimple **pFound);
    // IMultipleViewProvider
    IFACEMETHODIMP get_CurrentView(int *pRetVal);
    IFACEMETHODIMP GetSupportedViews(SAFEARRAY** pRetVal);
    IFACEMETHODIMP GetViewName(int viewId, BSTR *pRetVal);
    IFACEMETHODIMP SetCurrentView(int viewId);
    // ITextChildProvider
    IFACEMETHODIMP get_TextContainer(IRawElementProviderSimple **pRetVal);
    IFACEMETHODIMP get_TextRange(ITextRangeProvider **pRetVal);
    // ITextProvider2
    IFACEMETHODIMP GetCaretRange(BOOL *isActive, ITextRangeProvider **pRetVal);
    IFACEMETHODIMP RangeFromAnnotation(IRawElementProviderSimple *annotationElement, ITextRangeProvider **pRetVal);
    // ITextEditProvider
    IFACEMETHODIMP GetActiveComposition(ITextRangeProvider **pRetVal);
    IFACEMETHODIMP GetConversionTarget(ITextRangeProvider **pRetVal);

    // IStylesProvider
    IFACEMETHODIMP get_ExtendedProperties(BSTR* pRetVal);
    IFACEMETHODIMP get_FillColor(int* pRetVal);
    IFACEMETHODIMP get_FillPatternColor(int* pRetVal);
    IFACEMETHODIMP get_FillPatternStyle(BSTR* pRetVal);
    IFACEMETHODIMP get_Shape(BSTR* pRetVal);
    IFACEMETHODIMP get_StyleId(int* pRetVal);
    IFACEMETHODIMP get_StyleName(BSTR* pRetVal);

    // IVirtualizedItemProvider
    IFACEMETHODIMP Realize();

    // ISynchronizedInputProvider
    IFACEMETHODIMP Cancel();
    IFACEMETHODIMP StartListening(SynchronizedInputType inputType);

    // ISelectionProvider2
    IFACEMETHODIMP get_CurrentSelectedItem(IRawElementProviderSimple **pRetVal);
    IFACEMETHODIMP get_FirstSelectedItem(IRawElementProviderSimple **pRetVal);
    IFACEMETHODIMP get_LastSelectedItem(IRawElementProviderSimple **pRetVal);
    IFACEMETHODIMP get_ItemCount(int *pRetVal);

    // ITransformProvider2
    IFACEMETHODIMP get_CanZoom(BOOL* pRetVal);
    IFACEMETHODIMP get_ZoomLevel(double* pRetVal);
    IFACEMETHODIMP get_ZoomMinimum(double* pRetVal);
    IFACEMETHODIMP get_ZoomMaximum(double* pRetVal);
    IFACEMETHODIMP Zoom(double zoom);
    IFACEMETHODIMP ZoomByUnit(ZoomUnit zoomUnit);

    static HRESULT copyVariant(JNIEnv* env, jobject jVariant, VARIANT* pRetVal);
    static HRESULT copyString(JNIEnv* env, jstring jString, BSTR* pbstrVal);
    static HRESULT copyList(JNIEnv* env, jarray list, SAFEARRAY** pparrayVal, VARTYPE vt);

private:
    virtual ~ProxyAccessible();

    /* Call the method specified by 'mid', AddRef the returning ptr (expects result to be IUnkonwn) */
    virtual HRESULT callLongMethod(jmethodID mid, IUnknown** pRetVal, ...);

    /* Call the method specified by 'mid' and converts the returning jarray to a SAFEARRAY */
    virtual HRESULT callArrayMethod(jmethodID mid, VARTYPE vt, SAFEARRAY** pRetVal);

    ULONG m_refCount;
    jobject m_jAccessible;  // The GlobalRef Java side object

};

#endif //_UIA_PROXY_ACCESSIBLE_