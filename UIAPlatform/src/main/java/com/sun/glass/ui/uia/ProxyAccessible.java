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
package com.sun.glass.ui.uia;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.View;
import com.sun.glass.ui.uia.glass.WinAccessible;
import com.sun.glass.ui.uia.glass.WinVariant;

import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;

@SuppressWarnings("restriction")
public class ProxyAccessible extends Accessible {

    private static native void _initIDs();
    static {
        try {
            URL library = ProxyAccessible.class.getResource("/UIAPlatform.dll");
            Path libDir = Files.createTempDirectory("openfx-uia");
            Path lib = libDir.resolve("UIAPlatform.dll");
            Files.copy(library.openStream(), lib);
            System.err.println("Using " + lib.toString());
            System.load(lib.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO library loading, for now we just use the sys prop
        //System.load(System.getProperty("uia.dll"));
        _initIDs();
    }

    private long peer;

    WinAccessible glass;

    /*package*/ ProxyAccessible() {
        System.err.println("ProxyAccessible created");

        this.peer = _createProxyAccessible();
        if (this.peer == 0L) {
            throw new RuntimeException("could not create platform accessible");
        }

        glass = new WinAccessible(this);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (peer != 0L) {
            _destroyProxyAccessible(peer);
            peer = 0L;
        }

        glass.dispose();
    }

    @Override
    public Object getAttribute(AccessibleAttribute attribute, Object... parameters) {
        return glass.getAttribute(attribute, parameters);
    }

    @Override
    public void executeAction(AccessibleAction action, Object... parameters) {
        glass.executeAction(action, parameters);
    }

    @Override
    public View getView() {
        return glass.getView();
    }

    @Override
    public void setView(View view) {
        glass.setView(view);
    }

    @Override
    public EventHandler getEventHandler() {
        return glass.getEventHandler();
    }

    @Override
    public void setEventHandler(EventHandler eventHandler) {
        glass.setEventHandler(eventHandler);
    }



    @Override
    public String toString() {
        return "ProxyAccessible[" + glass + "]";
    }

    public WinAccessible getGlassAccessible() {
        return glass;
    }

    /* Creates a GlassAccessible linked to the caller (GlobalRef) */
    public native long _createProxyAccessible();

    /* Releases the GlassAccessible and deletes the GlobalRef */
    public native void _destroyProxyAccessible(long accessible);

    public native static long UiaRaiseAutomationEvent(long pProvider, int id);
    public native static long UiaRaiseAutomationPropertyChangedEvent(long pProvider, int id, WinVariant oldV, WinVariant newV);
    public native static boolean UiaClientsAreListening();

    @Override
    public long getNativeAccessible() {
        return peer;
    }

    @Override
    public void sendNotification(AccessibleAttribute notification) {
        glass.sendNotification(notification);
    }

   
    
    /***********************************************/
    /*        IRawElementProviderSimple            */
    /***********************************************/
    private long IRawElementProviderSimple_GetPatternProvider(int patternId) {
        return glass.GetPatternProvider(patternId);
    }
    private long IRawElementProviderSimple_get_HostRawElementProvider() {
        return glass.get_HostRawElementProvider();
    }
    private WinVariant IRawElementProviderSimple_GetPropertyValue(int propertyId) {
        return glass.GetPropertyValue(propertyId);
    }

    /***********************************************/
    /*       IRawElementProviderFragment           */
    /***********************************************/
    private float[] IRawElementProviderFragment_get_BoundingRectangle() {
        return glass.get_BoundingRectangle();
    }
    private long IRawElementProviderFragment_get_FragmentRoot() {
        return glass.get_FragmentRoot();
    }
    private long[] IRawElementProviderFragment_GetEmbeddedFragmentRoots() {
        return glass.GetEmbeddedFragmentRoots();
    }
    private int[] IRawElementProviderFragment_GetRuntimeId() {
        return glass.GetRuntimeId();
    }
    private long IRawElementProviderFragment_Navigate(int direction) {
        return glass.Navigate(direction);
    }
    private void IRawElementProviderFragment_SetFocus() {
        glass.SetFocus();
    }

    /***********************************************/
    /*     IRawElementProviderFragmentRoot         */
    /***********************************************/
    private long IRawElementProviderFragmentRoot_ElementProviderFromPoint(double x, double y) {
        return glass.ElementProviderFromPoint(x, y);
    }
    private long IRawElementProviderFragmentRoot_GetFocus() {
        return glass.GetFocus();
    }
    /***********************************************/
    /*     IRawElementProviderAdviseEvents         */
    /***********************************************/
    private void IRawElementProviderAdviseEvents_AdviseEventAdded(int eventId, long propertyIDs) {
        glass.AdviseEventAdded(eventId, propertyIDs);
    }
    private void IRawElementProviderAdviseEvents_AdviseEventRemoved(int eventId, long propertyIDs) {
        glass.AdviseEventRemoved(eventId, propertyIDs);
    }

    /***********************************************/
    /*             IInvokeProvider                 */
    /***********************************************/
    private void IInvokeProvider_Invoke() {
        glass.Invoke();
    }
    /***********************************************/
    /*           ISelectionProvider                */
    /***********************************************/
    private long[] ISelectionProvider_GetSelection() {
        return glass.GetSelection();
    }
    private boolean ISelectionProvider_get_CanSelectMultiple() {
        return glass.get_CanSelectMultiple();
    }
    private boolean ISelectionProvider_get_IsSelectionRequired() {
        return glass.get_IsSelectionRequired();
    }
    /***********************************************/
    /*          ISelectionItemProvider             */
    /***********************************************/
    private void ISelectionItemProvider_Select() {
        glass.Select();
    }
    private void ISelectionItemProvider_AddToSelection() {
        glass.AddToSelection();
    }
    private void ISelectionItemProvider_RemoveFromSelection() {
        glass.RemoveFromSelection();
    }
    private boolean ISelectionItemProvider_get_IsSelected() {
        return glass.get_IsSelected();
    }
    private long ISelectionItemProvider_get_SelectionContainer() {
        return glass.get_SelectionContainer();
    }
    /***********************************************/
    /*           IRangeValueProvider               */
    /***********************************************/
    private void IRangeValueProvider_SetValue(double val) {
        glass.SetValue(val);
    }
    private double IRangeValueProvider_get_Value() {
        return glass.get_Value();
    }
    /*  Note that this method is called by the IValueProvider also. */
    private boolean IRangeValueProvider_get_IsReadOnly() {
        return glass.get_IsReadOnly();
    }
    private double IRangeValueProvider_get_Maximum() {
        return glass.get_Maximum();
    }
    private double IRangeValueProvider_get_Minimum() {
        return glass.get_Minimum();
    }
    private double IRangeValueProvider_get_LargeChange() {
        return glass.get_LargeChange();
    }
    private double IRangeValueProvider_get_SmallChange() {
        return glass.get_SmallChange();
    }
    /***********************************************/
    /*             IValueProvider                  */
    /***********************************************/
    private void IValueProvider_SetValueString(String val) {
        glass.SetValueString(val);
    }
    private String IValueProvider_get_ValueString() {
        return glass.get_ValueString();
    }
    /***********************************************/
    /*              ITextProvider                  */
    /***********************************************/
    private long[] ITextProvider_GetVisibleRanges() {
        return glass.GetVisibleRanges();
    }
    private long ITextProvider_RangeFromChild(long childElement) {
        return glass.RangeFromChild(childElement);
    }
    private long ITextProvider_RangeFromPoint(double x, double y) {
        return glass.RangeFromPoint(x, y);
    }
    private long ITextProvider_get_DocumentRange() {
        return glass.get_DocumentRange();
    }
    private int ITextProvider_get_SupportedTextSelection() {
        return glass.get_SupportedTextSelection();
    }
     /***********************************************/
    /*             IGridProvider                   */
    /***********************************************/
    private int IGridProvider_get_ColumnCount() {
        return glass.get_ColumnCount();
    }
    private int IGridProvider_get_RowCount() {
        return glass.get_RowCount();
    }
    private long IGridProvider_GetItem(int row, int column) {
        return glass.GetItem(row, column);
    }
    /***********************************************/
    /*             IGridItemProvider               */
    /***********************************************/
    private int IGridItemProvider_get_Column() {
        return glass.get_Column();
    }
    private int IGridItemProvider_get_ColumnSpan() {
        return glass.get_ColumnSpan();
    }
    private long IGridItemProvider_get_ContainingGrid() {
        return glass.get_ContainingGrid();
    }
    private int IGridItemProvider_get_Row() {
        return glass.get_Row();
    }
    private int IGridItemProvider_get_RowSpan() {
        return glass.get_RowSpan();
    }
    /***********************************************/
    /*               ITableProvider                */
    /***********************************************/
    private long[] ITableProvider_GetColumnHeaders() {
        return glass.GetColumnHeaders();
    }
    private long[] ITableProvider_GetRowHeaders() {
        return glass.GetRowHeaders();
    }
    private int ITableProvider_get_RowOrColumnMajor() {
        return glass.get_RowOrColumnMajor();
    }
    /***********************************************/
    /*             ITableItemProvider              */
    /***********************************************/
    private long[] ITableItemProvider_GetColumnHeaderItems() {
        return glass.GetColumnHeaderItems();
    }
    private long[] ITableItemProvider_GetRowHeaderItems() {
        return glass.GetRowHeaderItems();
    }
    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    private void IToggleProvider_Toggle() {
        glass.Toggle();
    }
    private int IToggleProvider_get_ToggleState() {
        return glass.get_ToggleState();
    }
    /***********************************************/
    /*             IExpandCollapseProvider         */
    /***********************************************/
    private void IExpandCollapseProvider_Collapse() {
        glass.Collapse();
    }
    private void IExpandCollapseProvider_Expand() {
        glass.Expand();
    }
    private int IExpandCollapseProvider_get_ExpandCollapseState() {
        return glass.get_ExpandCollapseState();
    }
    /***********************************************/
    /*             ITransformProvider              */
    /***********************************************/
    private boolean ITransformProvider_get_CanMove() {
        return glass.get_CanMove();
    }
    private boolean ITransformProvider_get_CanResize() {
        return glass.get_CanResize();
    }
    private boolean ITransformProvider_get_CanRotate() {
        return glass.get_CanRotate();
    }
    private void ITransformProvider_Move(double x, double y) {
        glass.Move(x, y);
    }
    private void ITransformProvider_Resize(double width, double height) {
        glass.Resize(width, height);
    }
    private void ITransformProvider_Rotate(double degrees) {
        glass.Rotate(degrees);
    }
    /***********************************************/
    /*             IScrollProvider                 */
    /***********************************************/
    private void IScrollProvider_Scroll(int horizontalAmount, int verticalAmount) {
        glass.Scroll(horizontalAmount, verticalAmount);
    }
    private void IScrollProvider_SetScrollPercent(double horizontalPercent, double verticalPercent) {
        glass.SetScrollPercent(horizontalPercent, verticalPercent);
    }
    private boolean IScrollProvider_get_HorizontallyScrollable() {
        return glass.get_HorizontallyScrollable();
    }
    private double IScrollProvider_get_HorizontalScrollPercent() {
        return glass.get_HorizontalScrollPercent();
    }
    private double IScrollProvider_get_HorizontalViewSize() {
        return glass.get_HorizontalViewSize();
    }
    private boolean IScrollProvider_get_VerticallyScrollable() {
        return glass.get_VerticallyScrollable();
    }
    private double IScrollProvider_get_VerticalScrollPercent() {
        return glass.get_VerticalScrollPercent();
    }
    private double IScrollProvider_get_VerticalViewSize() {
        return glass.get_VerticalViewSize();
    }
    /***********************************************/
    /*             IScrollItemProvider             */
    /***********************************************/
    private void IScrollItemProvider_ScrollIntoView() {
        glass.ScrollIntoView();
    }


    // other
    private void WindowProvider_Close() {}
    private boolean WindowProvider_get_CanMaximize() {return false;}
    private boolean WindowProvider_get_CanMinimize() {return false;}
    private boolean WindowProvider_get_IsModal() {return false;}
    private boolean WindowProvider_get_IsTopmost() {return false;}
    private int WindowProvider_get_WindowInteractionState() {return 0;}
    private int WindowProvider_get_WindowVisualState() {return 0;}
    private void WindowProvider_SetVisualState(int s) {}
    private boolean WindowProvider_WaitForInputIdle(int s) {return false;}
}
