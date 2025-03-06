package at.bestsolution.uia.internal.glass;


import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.View;

import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;

public interface IWinAccessible {

    // IRawElementProviderSimple
    public long GetPatternProvider(int patternId);
    public long get_HostRawElementProvider();
    public WinVariant GetPropertyValue(int propertyId);
    public float[] get_BoundingRectangle();
    public long get_FragmentRoot();
    public long[] GetEmbeddedFragmentRoots();
    public int[] GetRuntimeId();
    public long Navigate(int direction);
    public void SetFocus();
    // IRawElementProviderFragmentRoot
    public long ElementProviderFromPoint(double x, double y);
    public Node ElementProviderFromPointAsNode(double x, double y);
    public long GetFocus();
    // IRawElementProviderAdviseEvents
    public void AdviseEventAdded(int eventId, long propertyIDs);
    public void AdviseEventRemoved(int eventId, long propertyIDs);
    // IInvokeProvider
    public void Invoke();
    // ISelectionProvider
    public long[] GetSelection();
    public boolean get_CanSelectMultiple();
    public boolean get_IsSelectionRequired();
    // IRangeValueProvider
    public void SetValue(double val);
    public double get_Value();
    public boolean get_IsReadOnly();
    public double get_Maximum();
    public double get_Minimum();
    public double get_LargeChange();
    public double get_SmallChange();
    // IValueProvider
    public void SetValueString(String val);
    public String get_ValueString();
    // ISelectionItemProvider
    public void Select();
    public void AddToSelection();
    public void RemoveFromSelection();
    public boolean get_IsSelected();
    public long get_SelectionContainer();
    // ITextProvider
    public long[] GetVisibleRanges();
    public long RangeFromChild(long childElement);
    public long RangeFromPoint(double x, double y);
    public long get_DocumentRange();
    public int get_SupportedTextSelection();
    // IGridProvider
    public int get_ColumnCount();
    public int get_RowCount();
    public long GetItem(int row, int column);
    // IGridItemProvider
    public int get_Column();
    public int get_ColumnSpan();
    public long get_ContainingGrid();
    public int get_Row();
    public int get_RowSpan();
    // ITableProvider
    public long[] GetColumnHeaders();
    public long[] GetRowHeaders();
    public int get_RowOrColumnMajor();
    // ITableItemProvider
    public long[] GetColumnHeaderItems();
    public long[] GetRowHeaderItems();
    // IToggleProvider
    public void Toggle();
    public int get_ToggleState();
    // IExpandCollapseProvider
    public void Collapse();
    public void Expand();
    public int get_ExpandCollapseState();
    // ITransformProvider
    public boolean get_CanMove();
    public boolean get_CanResize();
    public boolean get_CanRotate();
    public void Move(double x, double y);
    public void Resize(double width, double height);
    public void Rotate(double degrees);
    // IScrollProvider
    public void Scroll(int horizontalAmount, int verticalAmount);
    public void SetScrollPercent(double horizontalPercent, double verticalPercent);
    public boolean get_HorizontallyScrollable();
    public double get_HorizontalScrollPercent();
    public double get_HorizontalViewSize();
    public boolean get_VerticallyScrollable();
    public double get_VerticalScrollPercent();
    public double get_VerticalViewSize();
    // IScrollItemProvider
    public void ScrollIntoView();


    public void dispose();
    public Object getAttribute(AccessibleAttribute attribute, Object... parameters);
    public void executeAction(AccessibleAction action, Object... parameters);
    public View getView();
    public void setView(View view);
    public Accessible.EventHandler getEventHandler();
    public void setEventHandler(Accessible.EventHandler eventHandler);

    public void sendNotification(AccessibleAttribute notification);

    public long getPeer();
}
