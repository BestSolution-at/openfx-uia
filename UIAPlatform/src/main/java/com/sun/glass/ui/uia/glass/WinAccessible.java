/*
 * Copyright (c) 2013, 2017, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package com.sun.glass.ui.uia.glass;

import com.sun.glass.ui.uia.ProxyTextRangeProvider;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAction;
import javafx.scene.AccessibleAttribute;
import javafx.scene.AccessibleRole;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import com.sun.glass.ui.Accessible;
import com.sun.glass.ui.View;
import com.sun.glass.ui.uia.Logger;
import com.sun.glass.ui.uia.ProxyAccessible;
import com.sun.glass.ui.uia.ProxyAccessibleRegistry;

import static javafx.scene.AccessibleAttribute.*;
import com.sun.javafx.scene.NodeHelper;
import com.sun.javafx.scene.SceneHelper;

/*
 * This class is the Java peer for GlassAccessible.
 * GlassAccessible implements all UIA interfaces required including:
 * IRawElementProviderSimple
 * IRawElementProviderFragment
 * IRawElementProviderFragmentRoot
 * IInvokeProvider
 *
 *
 * Ideas (performance, low priority):
 * 1. Only raise events when needed:
 *   - Either implement or IRawElementProviderAdviseEvents use UiaClientsAreListening
 *
 * 2. When returning the same string to UIA we could save the BSTR instead
 *    of creating a new one every time.
 *
 */

 // Modifications
 // * no native counterpart -> everything is handled through UIAAccessibleProxy
 // * the proxy is passed to the constructor to allow reverse calls
 // * the formerly native methods are implemented to delegate to the proxy
 // * private methods called from native are public scoped, so the proxy can call them
 // * the getAccessible methods translate the proxy accessible to the WinAccessible
 // this should make it easy to merge openjfx changes

 @SuppressWarnings({"restriction", "javadoc"})
public final class WinAccessible extends Accessible {

  private static Logger LOG = Logger.create(WinAccessible.class);

//    private native static void _initIDs();
//    static {
//        _initIDs();
//    }

    private static int idCount = 1;

    /* PROPERTYID  */
    private static final int UIA_BoundingRectanglePropertyId     = 30001;
    private static final int UIA_ProcessIdPropertyId             = 30002;
    private static final int UIA_ControlTypePropertyId           = 30003;
    private static final int UIA_LocalizedControlTypePropertyId  = 30004;
    private static final int UIA_NamePropertyId                  = 30005;
    private static final int UIA_AcceleratorKeyPropertyId        = 30006;
    private static final int UIA_AccessKeyPropertyId             = 30007;
    private static final int UIA_HasKeyboardFocusPropertyId      = 30008;
    private static final int UIA_IsKeyboardFocusablePropertyId   = 30009;
    private static final int UIA_IsEnabledPropertyId             = 30010;
    private static final int UIA_AutomationIdPropertyId          = 30011;
    private static final int UIA_ClassNamePropertyId             = 30012;
    private static final int UIA_HelpTextPropertyId              = 30013;
    private static final int UIA_ClickablePointPropertyId        = 30014;
    private static final int UIA_CulturePropertyId               = 30015;
    private static final int UIA_IsControlElementPropertyId      = 30016;
    private static final int UIA_IsContentElementPropertyId      = 30017;
    private static final int UIA_LabeledByPropertyId             = 30018;
    private static final int UIA_IsPasswordPropertyId            = 30019;
    private static final int UIA_NativeWindowHandlePropertyId    = 30020;
    private static final int UIA_ItemTypePropertyId              = 30021;
    private static final int UIA_IsOffscreenPropertyId           = 30022;
    private static final int UIA_OrientationPropertyId           = 30023;
    private static final int UIA_FrameworkIdPropertyId           = 30024;
    private static final int UIA_ValueValuePropertyId            = 30045;
    private static final int UIA_RangeValueValuePropertyId       = 30047;
    private static final int UIA_ExpandCollapseExpandCollapseStatePropertyId = 30070;
    private static final int UIA_ToggleToggleStatePropertyId     = 30086;
    private static final int UIA_AriaRolePropertyId              = 30101;
    private static final int UIA_ProviderDescriptionPropertyId   = 30107;
    private static final int UIA_IsDialogPropertyId              = 30174;

    /* Control Pattern Identifiers */
    private static final int UIA_InvokePatternId                 = 10000;
    private static final int UIA_SelectionPatternId              = 10001;
    private static final int UIA_ValuePatternId                  = 10002;
    private static final int UIA_RangeValuePatternId             = 10003;
    private static final int UIA_ScrollPatternId                 = 10004;
    private static final int UIA_ExpandCollapsePatternId         = 10005;
    private static final int UIA_GridPatternId                   = 10006;
    private static final int UIA_GridItemPatternId               = 10007;
    private static final int UIA_SelectionItemPatternId          = 10010;
    private static final int UIA_TablePatternId                  = 10012;
    private static final int UIA_TableItemPatternId              = 10013;
    private static final int UIA_TextPatternId                   = 10014;
    private static final int UIA_TogglePatternId                 = 10015;
    private static final int UIA_TransformPatternId              = 10016;
    private static final int UIA_ScrollItemPatternId             = 10017;
    private static final int UIA_ItemContainerPatternId          = 10019;

    /* UIA_ControlTypeIds */
    private static final int UIA_ButtonControlTypeId             = 50000;
    private static final int UIA_CheckBoxControlTypeId           = 50002;
    private static final int UIA_ComboBoxControlTypeId           = 50003;
    private static final int UIA_EditControlTypeId               = 50004;
    private static final int UIA_HyperlinkControlTypeId          = 50005;
    private static final int UIA_ImageControlTypeId              = 50006;
    private static final int UIA_ListItemControlTypeId           = 50007;
    private static final int UIA_ListControlTypeId               = 50008;
    private static final int UIA_MenuControlTypeId               = 50009;
    private static final int UIA_MenuBarControlTypeId            = 50010;
    private static final int UIA_MenuItemControlTypeId           = 50011;
    private static final int UIA_ProgressBarControlTypeId        = 50012;
    private static final int UIA_RadioButtonControlTypeId        = 50013;
    private static final int UIA_ScrollBarControlTypeId          = 50014;
    private static final int UIA_SliderControlTypeId             = 50015;
    private static final int UIA_SpinnerControlTypeId            = 50016;
    private static final int UIA_TabControlTypeId                = 50018;
    private static final int UIA_TabItemControlTypeId            = 50019;
    private static final int UIA_TextControlTypeId               = 50020;
    private static final int UIA_ToolBarControlTypeId            = 50021;
    private static final int UIA_TreeControlTypeId               = 50023;
    private static final int UIA_TreeItemControlTypeId           = 50024;
    private static final int UIA_GroupControlTypeId              = 50026;
    private static final int UIA_ThumbControlTypeId              = 50027;
    private static final int UIA_DataGridControlTypeId           = 50028;
    private static final int UIA_DataItemControlTypeId           = 50029;
    private static final int UIA_SplitButtonControlTypeId        = 50031;
    private static final int UIA_WindowControlTypeId             = 50032;
    private static final int UIA_PaneControlTypeId               = 50033;
    private static final int UIA_TableControlTypeId              = 50036;

    /* NavigateDirection */
    private static final int NavigateDirection_Parent            = 0;
    private static final int NavigateDirection_NextSibling       = 1;
    private static final int NavigateDirection_PreviousSibling   = 2;
    private static final int NavigateDirection_FirstChild        = 3;
    private static final int NavigateDirection_LastChild         = 4;

    /* RowOrColumnMajor */
    private static final int RowOrColumnMajor_RowMajor          = 0;
    private static final int RowOrColumnMajor_ColumnMajor       = 1;
    private static final int RowOrColumnMajor_Indeterminate     = 2;

    /* Event ID constants */
    private static final int UIA_MenuOpenedEventId               = 20003;
    private static final int UIA_AutomationPropertyChangedEventId= 20004;
    private static final int UIA_AutomationFocusChangedEventId   = 20005;
    private static final int UIA_MenuClosedEventId               = 20007;
    private static final int UIA_SelectionItem_ElementRemovedFromSelectionEventId = 20011;
    private static final int UIA_SelectionItem_ElementSelectedEventId = 20012;
    private static final int UIA_Text_TextSelectionChangedEventId = 20014;
    private static final int UIA_Text_TextChangedEventId         = 20015;
    private static final int UIA_MenuModeStartEventId            = 20018;
    private static final int UIA_MenuModeEndEventId              = 20019;

    /* SupportedTextSelection */
    private static final int SupportedTextSelection_None         = 0;
    private static final int SupportedTextSelection_Single       = 1;
    private static final int SupportedTextSelection_Multiple     = 2;

    /* ExpandCollapseState */
    private static final int ExpandCollapseState_Collapsed          = 0;
    private static final int ExpandCollapseState_Expanded           = 1;
    private static final int ExpandCollapseState_PartiallyExpanded  = 2;
    private static final int ExpandCollapseState_LeafNode           = 3;

    /* ScrollAmount */
    private static final int ScrollAmount_LargeDecrement        = 0;
    private static final int ScrollAmount_SmallDecrement        = 1;
    private static final int ScrollAmount_NoAmount              = 2;
    private static final int ScrollAmount_LargeIncrement        = 3;
    private static final int ScrollAmount_SmallIncrement        = 4;

    /* Scroll */
    private static final int UIA_ScrollPatternNoScroll          = -1;

    /* ToggleState */
    private static final int ToggleState_Off                    = 0;
    private static final int ToggleState_On                     = 1;
    private static final int ToggleState_Indeterminate          = 2;

    /* Other constants */
    private static final int UiaAppendRuntimeId                  = 3;

    private long peer;
    private int id;

    /* Text Support */
    private boolean documentRangeRequested = false;
    private int curSelectionStart = -1;
    private int curSelectionEnd = -1;

    /* The lastIndex is used by parents to keep track of the index of the last child
     * returned in Navigate. It is very common for Narrator to traverse the children
     * list by calling next sibling sequentially, without lastIndex the caller would
     * have to traverse the list to find the location of the current child before it
     * can return the next sibling.
     */
    private int lastIndex = 0;

    /* Creates a GlassAccessible linked to the caller (GlobalRef) */
    //private native long _createGlassAccessible();

    /* Releases the GlassAccessible and deletes the GlobalRef */
    // private native void _destroyGlassAccessible(long accessible);

    private static long UiaRaiseAutomationEvent(long pProvider, int id) {
        return ProxyAccessible.UiaRaiseAutomationEvent(pProvider, id);
    }
    private static long UiaRaiseAutomationPropertyChangedEvent(long pProvider, int id, WinVariant oldV, WinVariant newV) {
        return ProxyAccessible.UiaRaiseAutomationPropertyChangedEvent(pProvider, id, oldV, newV);
    }
    private static boolean UiaClientsAreListening() {
        return ProxyAccessible.UiaClientsAreListening();
    }

    private final ProxyAccessible proxy;

    public WinAccessible(ProxyAccessible proxy) {
        this.proxy = proxy;

        this.peer = proxy.getNativeAccessible();

        //this.peer = _createGlassAccessible();
        //if (this.peer == 0L) {
        //    throw new RuntimeException("could not create platform accessible");
        //}
        this.id = idCount++;
    }

    public long getPeer() {
        return peer;
    }

    @Override
    public void dispose() {
        super.dispose();
        // if (glassSelectionRange != null) {
        //   glassSelectionRange = null;
        // }
        // if (selectionRange != null) {
        //     selectionRange.dispose();
        //     selectionRange = null;
        // }
        // if (documentRange != null) {
        //     documentRange.dispose();
        //     documentRange = null;
        // }
        peer = 0L;
    }

    @Override
    public void sendNotification(AccessibleAttribute notification) {
        if (isDisposed()) return;

        switch (notification) {
            case FOCUS_NODE:
                if (getView() != null) {
                    // This is a Scene
                    long focus = GetFocus();
                    if (focus != 0) {
                        ProxyAccessible p = ProxyAccessibleRegistry.getInstance().getByNative(focus);
                        LOG.debug(this, () -> "focus target: " + p);
                        ProxyAccessible focusDelegate = p.getFocusDelegate();
                        if (focusDelegate != null) {
                            LOG.debug(this, () -> "focus delegate: " + focusDelegate);
                            UiaRaiseAutomationEvent(focusDelegate.getNativeAccessible(), UIA_AutomationFocusChangedEventId);
                        } else {
                            UiaRaiseAutomationEvent(focus, UIA_AutomationFocusChangedEventId);
                        }
                    }
                } else {
                    // This is a Scene.transientFocusContainer
                    Node node = (Node)getAttribute(FOCUS_NODE);
                    if (node != null) {
                      LOG.debug(this, () -> "->FocusChanged transientFocusContainer");
                        UiaRaiseAutomationEvent(getNativeAccessible(node), UIA_AutomationFocusChangedEventId);



                    } else {
                        // Delegate back to the Scene if the transient focus owner is null
                        LOG.debug(this, () -> "->FocusChanged delegate");
                        Scene scene = (Scene)getAttribute(SCENE);
                        Accessible acc = getAccessible(scene);
                        if (acc != null) {
                            acc.sendNotification(FOCUS_NODE);
                        }
                    }
                }
                break;
            case FOCUS_ITEM: {
                Node node = (Node)getAttribute(FOCUS_ITEM);
                long id = getNativeAccessible(node);
                if (id != 0) {
                    UiaRaiseAutomationEvent(id, UIA_AutomationFocusChangedEventId);
                }
                break;
            }
            case INDETERMINATE: {
                if (getAttribute(ROLE) == AccessibleRole.CHECK_BOX) {
                    notifyToggleState();
                }
                break;
            }
            case SELECTED: {
                Object role = getAttribute(ROLE);
                if (role == AccessibleRole.CHECK_BOX || role == AccessibleRole.TOGGLE_BUTTON) {
                    notifyToggleState();
                    break;
                }
                Boolean selected = (Boolean)getAttribute(SELECTED);
                if (selected != null) {
                    if (selected) {
                        UiaRaiseAutomationEvent(peer, UIA_SelectionItem_ElementSelectedEventId);
                    } else {
                        UiaRaiseAutomationEvent(peer, UIA_SelectionItem_ElementRemovedFromSelectionEventId);
                    }
                }
                break;
            }
            case FOCUSED: {
                /* HANDLED IN FOCUS_NODE */
                break;
            }
            case VALUE: {
                Double value = (Double)getAttribute(VALUE);
                if (value != null) {
                    WinVariant vo = new WinVariant();
                    vo.vt = WinVariant.VT_R8;
                    vo.dblVal = 0;
                    WinVariant vn = new WinVariant();
                    vn.vt = WinVariant.VT_R8;
                    vn.dblVal = value;
                    UiaRaiseAutomationPropertyChangedEvent(peer, UIA_RangeValueValuePropertyId, vo, vn);
                }
                break;
            }
            case SELECTION_START:
            case SELECTION_END:
                Integer start = (Integer)getAttribute(SELECTION_START);
                boolean newStart = start != null && start != curSelectionStart; //glassSelectionRange.getStart();
                Integer end = (Integer)getAttribute(SELECTION_END);
                boolean newEnd = end != null && end != curSelectionEnd; //glassSelectionRange.getEnd();
                /* Sending unnecessary selection changed events causes Narrator
                  * not to focus an empty text control when clicking.
                  */
                if (newStart || newEnd) {
                    UiaRaiseAutomationEvent(peer, UIA_Text_TextSelectionChangedEventId);
                }
                break;
            case TEXT:
                String value = (String)getAttribute(TEXT);
                if (value != null) {
                    WinVariant vo = new WinVariant();
                    vo.vt = WinVariant.VT_BSTR;
                    vo.bstrVal = "";
                    WinVariant vn = new WinVariant();
                    vn.vt = WinVariant.VT_BSTR;
                    vn.bstrVal = value;
                    if (getAttribute(ROLE) == AccessibleRole.SPINNER) {
                        UiaRaiseAutomationPropertyChangedEvent(peer, UIA_NamePropertyId, vo, vn);
                    } else {
                        /* Combo and Text both implement IValueProvider */
                        UiaRaiseAutomationPropertyChangedEvent(peer, UIA_ValueValuePropertyId, vo, vn);
                    }
                }

                if (curSelectionStart != -1 || documentRangeRequested) {
                    UiaRaiseAutomationEvent(peer, UIA_Text_TextChangedEventId);
                }
                break;
            case EXPANDED: {
                Boolean expanded = (Boolean)getAttribute(EXPANDED);
                if (expanded != null) {
                    WinVariant vo = new WinVariant();
                    vo.vt = WinVariant.VT_I4;
                    vo.lVal = expanded ? ExpandCollapseState_Collapsed : ExpandCollapseState_Expanded;
                    WinVariant vn = new WinVariant();
                    vn.vt = WinVariant.VT_I4;
                    vn.lVal = expanded ? ExpandCollapseState_Expanded : ExpandCollapseState_Collapsed;
                    if (getAttribute(ROLE) == AccessibleRole.TREE_TABLE_ROW) {
                        Accessible treeTableView = getContainer();
                        Integer rowIndex = (Integer)getAttribute(INDEX);
                        if (treeTableView != null && rowIndex != null) {
                            Node node = (Node)treeTableView.getAttribute(CELL_AT_ROW_COLUMN, rowIndex, 0);
                            if (node != null) {
                                long ptr = ((WinAccessible)getAccessible(node)).getNativeAccessible();
                                UiaRaiseAutomationPropertyChangedEvent(ptr, UIA_ExpandCollapseExpandCollapseStatePropertyId, vo, vn);
                            }
                        }
                    } else {
                        UiaRaiseAutomationPropertyChangedEvent(peer, UIA_ExpandCollapseExpandCollapseStatePropertyId, vo, vn);
                    }
                }
                break;
            }
            case PARENT:
                break;
            default:
                UiaRaiseAutomationEvent(peer, UIA_AutomationPropertyChangedEventId);
        }
    }

    private void notifyToggleState() {
        int state = get_ToggleState();
        WinVariant vo = new WinVariant();
        vo.vt = WinVariant.VT_I4;
        vo.lVal = state;
        WinVariant vn = new WinVariant();
        vn.vt = WinVariant.VT_I4;
        vn.lVal = state;
        UiaRaiseAutomationPropertyChangedEvent(peer, UIA_ToggleToggleStatePropertyId, vo, vn);
    }

    @Override
    protected long getNativeAccessible() {
        return peer;
    }

    @Override
    protected Accessible getAccessible(Node node) {
        if (node == null) return null;
        Accessible acc =  NodeHelper.getAccessible(node);
        // TODO is this right, we always get the glass accessible in here
        ProxyAccessible proxy = (ProxyAccessible) acc;
        return proxy.getGlassAccessible();
    }

    @Override
    protected Accessible getAccessible(Scene scene) {
        if (scene == null) return null;
        Accessible acc = SceneHelper.getAccessible(scene);
        ProxyAccessible proxy = (ProxyAccessible) acc;
        return proxy.getGlassAccessible();
    }

    private Accessible getContainer() {
        if (isDisposed()) return null;
        AccessibleRole role = (AccessibleRole) getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case TABLE_ROW:
                case TABLE_CELL: return getContainerAccessible(AccessibleRole.TABLE_VIEW);
                case LIST_ITEM: return getContainerAccessible(AccessibleRole.LIST_VIEW);
                case TAB_ITEM: return getContainerAccessible(AccessibleRole.TAB_PANE);
                case PAGE_ITEM: return getContainerAccessible(AccessibleRole.PAGINATION);
                case TREE_ITEM: return getContainerAccessible(AccessibleRole.TREE_VIEW);
                case TREE_TABLE_ROW:
                case TREE_TABLE_CELL: return getContainerAccessible(AccessibleRole.TREE_TABLE_VIEW);
                default:
            }
        }
        return null;
    }

    private int getControlType() {
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == null) return UIA_GroupControlTypeId;
        switch (role) {
            case CONTEXT_MENU: return UIA_MenuControlTypeId;
            case RADIO_MENU_ITEM:
            case CHECK_MENU_ITEM:
            case MENU:
            case MENU_ITEM: return UIA_MenuItemControlTypeId;
            case BUTTON:
            case MENU_BUTTON:
            case TOGGLE_BUTTON:
            case INCREMENT_BUTTON:
            case DECREMENT_BUTTON: return UIA_ButtonControlTypeId;
            case SPLIT_MENU_BUTTON: return UIA_SplitButtonControlTypeId;
            case PAGINATION:
            case TAB_PANE: return UIA_TabControlTypeId;
            case PAGE_ITEM:
            case TAB_ITEM: return UIA_TabItemControlTypeId;
            case SLIDER: return UIA_SliderControlTypeId;
            case PARENT: return getView() != null ? UIA_WindowControlTypeId : UIA_PaneControlTypeId;
            case TEXT: return UIA_TextControlTypeId;
            case TEXT_FIELD:
            case PASSWORD_FIELD:
            case TEXT_AREA: return UIA_EditControlTypeId;
            case TREE_TABLE_VIEW:
            case TABLE_VIEW: return UIA_TableControlTypeId;
            case LIST_VIEW: return UIA_ListControlTypeId;
            case LIST_ITEM: return UIA_ListItemControlTypeId;
            case TREE_TABLE_CELL:
            case TABLE_CELL: return UIA_DataItemControlTypeId;
            case IMAGE_VIEW: return UIA_ImageControlTypeId;
            case RADIO_BUTTON: return UIA_RadioButtonControlTypeId;
            case CHECK_BOX: return UIA_CheckBoxControlTypeId;
            case COMBO_BOX: return UIA_ComboBoxControlTypeId;
            case HYPERLINK: return UIA_HyperlinkControlTypeId;
            case TREE_VIEW: return UIA_TreeControlTypeId;
            case TREE_ITEM: return UIA_TreeItemControlTypeId;
            case PROGRESS_INDICATOR: return UIA_ProgressBarControlTypeId;
            case TOOL_BAR: return UIA_ToolBarControlTypeId;
            case TITLED_PANE: return UIA_GroupControlTypeId;
            case SCROLL_PANE: return UIA_PaneControlTypeId;
            case SCROLL_BAR: return UIA_ScrollBarControlTypeId;
            case THUMB: return UIA_ThumbControlTypeId;
            case MENU_BAR: return UIA_MenuBarControlTypeId;
            case DATE_PICKER: return UIA_PaneControlTypeId;
            case SPINNER: return UIA_SpinnerControlTypeId;
            default: return 0;
        }
    }

    /* Filter out hidden child nodes */
    private List<Node> getUnignoredChildren(WinAccessible acc) {
        if (acc == null) return FXCollections.emptyObservableList();

        @SuppressWarnings("unchecked")
        ObservableList<Node> children = (ObservableList<Node>)acc.getAttribute(CHILDREN);
        if (children == null) return FXCollections.emptyObservableList();
        return children.stream()
                .filter(Node::isVisible)
                .collect(Collectors.toList());
    }

    /* Helper used by TreeTableCell to find the TableRow */
    private Accessible getRow() {
        Integer columnIndex = (Integer)getAttribute(COLUMN_INDEX);
        if (columnIndex == null) return null;
        if (columnIndex != 0) return null;
        Integer rowIndex = (Integer)getAttribute(ROW_INDEX);
        if (rowIndex == null) return null;
        Accessible treeTableView = getContainer();
        if (treeTableView == null) return null;
        Node treeTableRow = (Node)treeTableView.getAttribute(ROW_AT_INDEX, rowIndex);
        return getAccessible(treeTableRow);
    }

    private void changeSelection(boolean add, boolean clear) {
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == null) return;
        Accessible container = getContainer();
        if (container == null) return;
        Node item = null;
        switch (role) {
            case LIST_ITEM: {
                Integer index = (Integer)getAttribute(INDEX);
                if (index != null) {
                    item = (Node)container.getAttribute(ITEM_AT_INDEX, index);
                }
                break;
            }
            case TREE_ITEM: {
                Integer index = (Integer)getAttribute(INDEX);
                if (index != null) {
                    item = (Node)container.getAttribute(ROW_AT_INDEX, index);
                }
                break;
            }
            case TABLE_CELL:
            case TREE_TABLE_CELL: {
                Integer rowIndex = (Integer)getAttribute(ROW_INDEX);
                Integer columnIndex = (Integer)getAttribute(COLUMN_INDEX);
                if (rowIndex != null && columnIndex != null) {
                    item = (Node)container.getAttribute(CELL_AT_ROW_COLUMN, rowIndex, columnIndex);
                }
                break;
            }
            default:
        }
        if (item != null) {
            ObservableList<Node> newItems = FXCollections.observableArrayList();
            if (!clear) {
                @SuppressWarnings("unchecked")
                ObservableList<Node> items = (ObservableList<Node>)container.getAttribute(SELECTED_ITEMS);
                if (items != null) {
                    newItems.addAll(items);
                }
            }
            if (add) {
                newItems.add(item);
            } else {
                newItems.remove(item);
            }
            container.executeAction(AccessibleAction.SET_SELECTED_ITEMS, newItems);
        }
    }

    /* **********************************************/
    /*        IRawElementProviderSimple            */
    /* **********************************************/
    public long GetPatternProvider(int patternId) {
        if (isDisposed()) return 0;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        boolean impl = false;
        switch (role) {
            case MENU:
            case SPLIT_MENU_BUTTON:
                impl = patternId == UIA_InvokePatternId ||
                       patternId == UIA_ExpandCollapsePatternId;
                break;
            case RADIO_MENU_ITEM:
            case CHECK_MENU_ITEM:
                impl = patternId == UIA_InvokePatternId ||
                       patternId == UIA_TogglePatternId;
                break;
            case HYPERLINK:
            case BUTTON:
            case INCREMENT_BUTTON:
            case DECREMENT_BUTTON:
            case MENU_BUTTON:
            case MENU_ITEM:
                impl = patternId == UIA_InvokePatternId;
                break;
            case PAGE_ITEM:
            case TAB_ITEM:
                impl = patternId == UIA_SelectionItemPatternId;
                break;
            case PAGINATION:
            case TAB_PANE:
                impl = patternId == UIA_SelectionPatternId;
                break;
            case SCROLL_PANE:
                impl = patternId == UIA_ScrollPatternId;
                break;
            case TREE_TABLE_VIEW:
            case TABLE_VIEW:
                impl = patternId == UIA_SelectionPatternId ||
                       patternId == UIA_GridPatternId ||
                       patternId == UIA_TablePatternId ||
                       patternId == UIA_ScrollPatternId;
                break;
            case TREE_TABLE_CELL:
                impl = patternId == UIA_SelectionItemPatternId ||
                       patternId == UIA_GridItemPatternId ||
                       patternId == UIA_TableItemPatternId ||
                       patternId == UIA_ExpandCollapsePatternId ||
                       patternId == UIA_ScrollItemPatternId;
                break;
            case TABLE_CELL:
                impl = patternId == UIA_SelectionItemPatternId ||
                       patternId == UIA_GridItemPatternId ||
                       patternId == UIA_TableItemPatternId ||
                       patternId == UIA_ScrollItemPatternId;
                break;
            case TREE_VIEW:
                impl = patternId == UIA_SelectionPatternId ||
                       patternId == UIA_ScrollPatternId;
                break;
            case TREE_ITEM:
                impl = patternId == UIA_SelectionItemPatternId ||
                       patternId == UIA_ExpandCollapsePatternId ||
                       patternId == UIA_ScrollItemPatternId;
                break;
            case LIST_VIEW:
                impl = patternId == UIA_SelectionPatternId ||
                       patternId == UIA_ScrollPatternId;
                break;
            case LIST_ITEM:
                impl = patternId == UIA_SelectionItemPatternId ||
                       patternId == UIA_ScrollItemPatternId;
                break;
            /*
             * MSDN doc is confusing if text elements should implement
             * UIA_ValuePatternId. The article 'Text and TextRange Control
             * Patterns' says to implement for backward compatibility (MSAA).
             * The article 'Text Control Type' says to never implement it,
             * and says to use 'Edit control type' instead (which is only
             * available on Windows 8).
             *
             * For now UIA_ValuePatternId is implemented to enable us to set the
             * value on TextField / TextArea controls.
             */
            case TEXT_FIELD:
            case TEXT_AREA:
                impl = patternId == UIA_TextPatternId ||
                       patternId == UIA_ValuePatternId;
                break;
            case TEXT:
                /* UIA_TextPatternId seems overkill for text. Use UIA_NamePropertyId instead */
                break;
            case RADIO_BUTTON:
                impl = patternId == UIA_SelectionItemPatternId;
                break;
            case CHECK_BOX:
            case TOGGLE_BUTTON:
                impl = patternId == UIA_TogglePatternId;
                break;
            case TITLED_PANE:
            case TOOL_BAR:
                impl = patternId == UIA_ExpandCollapsePatternId;
                break;
            case COMBO_BOX:
                impl = patternId == UIA_ExpandCollapsePatternId ||
                       patternId == UIA_ValuePatternId;
                break;
            case SCROLL_BAR:
            case SLIDER:
            case PROGRESS_INDICATOR:
                impl = patternId == UIA_RangeValuePatternId;
                break;
            default:
        }
        return impl ? getNativeAccessible() : 0L;
    }

    public long get_HostRawElementProvider() {
        if (isDisposed()) return 0;
        /*
         * Returning NULL makes this accessible 'lightweight',
         * GetRuntimeID() will be called for it.
         */
        View view = getView();
        return view != null ? view.getNativeView() : 0;
    }

    public WinVariant GetPropertyValue(int propertyId) {
        if (isDisposed()) return null;
        WinVariant variant = null;
        switch (propertyId) {
            case UIA_ControlTypePropertyId: {
                int controlType = getControlType();
                if (controlType != 0) {
                    variant = new WinVariant();
                    variant.vt = WinVariant.VT_I4;
                    variant.lVal = controlType;
                }
                break;
            }
            case UIA_AccessKeyPropertyId: {
                String mnemonic = (String)getAttribute(MNEMONIC);
                if (mnemonic != null) {
                    variant = new WinVariant();
                    variant.vt = WinVariant.VT_BSTR;
                    variant.bstrVal = "Alt+" + mnemonic.toLowerCase();
                }
                break;
            }
            case UIA_AcceleratorKeyPropertyId: {
                KeyCombination kc = (KeyCombination)getAttribute(ACCELERATOR);
                if (kc != null) {
                    variant = new WinVariant();
                    variant.vt = WinVariant.VT_BSTR;
                    /* Note: KeyCombination should have a getDisplayText() which encapsulates
                     * KeystrokeUtils.toString()
                     */
                    variant.bstrVal = kc.toString().replaceAll("Shortcut", "Ctrl");
                }
                break;
            }
            case UIA_NamePropertyId: {
                String name;

                AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
                if (role == null) role = AccessibleRole.NODE;
                switch (role) {
                    case TEXT_FIELD:
                    case TEXT_AREA:
                    case COMBO_BOX:
                        /*
                         *  IValueProvider controls use UIA_NamePropertyId to
                         *  return the LABELED_BY and get_ValueString() to
                         *  return the TITLE.
                         */
                        name = null;
                        break;
                    case DECREMENT_BUTTON:
                    case INCREMENT_BUTTON: {
                        name = (String)getAttribute(TEXT);
                        if (name == null || name.length() == 0) {
                            if (role == AccessibleRole.INCREMENT_BUTTON) {
                                name = "increment";
                            } else {
                                name = "decrement";
                            }
                        }
                        break;
                    }
                    default:
                        name = (String)getAttribute(TEXT);
                }

                if (name == null || name.length() == 0) {
                    Node label = (Node)getAttribute(LABELED_BY);
                    if (label != null) {
                        name = (String)getAccessible(label).getAttribute(TEXT);
                    }
                }
                if (name == null || name.length() == 0) {
                    /* Code intentionally commented - use for debugging only */
//                    if (getView() != null) {
//                        name = "JavaFXWindow" + id;
//                    } else {
//                        name = toString() + "-" + id;
//                    }
                    break;
                }
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BSTR;
                variant.bstrVal = (String)name;
                break;
            }
            case UIA_HelpTextPropertyId: {
                String help = (String)getAttribute(HELP);
                if (help != null) {
                    variant = new WinVariant();
                    variant.vt = WinVariant.VT_BSTR;
                    variant.bstrVal = help;
                }
                break;
            }
            case UIA_LocalizedControlTypePropertyId: {
                String description = (String)getAttribute(ROLE_DESCRIPTION);
                if (description == null) {
                    AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
                    if (role == null) role = AccessibleRole.NODE;
                    switch (role) {
                        case TITLED_PANE: description = "title pane"; break;
                        case PAGE_ITEM: description = "page"; break;
                        case DIALOG: description = "dialog"; break;
                        default:
                    }
                }
                if (description != null) {
                    variant = new WinVariant();
                    variant.vt = WinVariant.VT_BSTR;
                    variant.bstrVal = description;
                }
                break;
            }
            case UIA_HasKeyboardFocusPropertyId: {
                Boolean focus = (Boolean)getAttribute(FOCUSED);
                /*
                 * Note that accessibility focus and scene focus are not the same.
                 * Windows won't work correctly unless the accessible returned in GetFocus()
                 * answer TRUE in UIA_HasKeyboardFocusPropertyId.
                 * Note that UIA_HasKeyboardFocusPropertyId reports true for the main parent
                 * of a 'focus item', but that doesn't seem to cause problems.
                 */
                if (Boolean.FALSE.equals(focus)) {
                    Scene scene = (Scene)getAttribute(SCENE);
                    if (scene != null) {
                        Accessible acc = getAccessible(scene);
                        if (acc != null) {
                            Node node = (Node)acc.getAttribute(FOCUS_NODE);
                            if (node != null) {
                                Node item = (Node)getAccessible(node).getAttribute(FOCUS_ITEM);
                                if (getNativeAccessible(item) == peer) {
                                    focus = true;
                                }
                            }
                        }
                    }
                }
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = focus != null ? focus : false;
                break;
            }
            case UIA_IsDialogPropertyId: {
                AccessibleRole role = (AccessibleRole) getAttribute(ROLE);
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = (role == AccessibleRole.DIALOG);
            } break;
            case UIA_IsContentElementPropertyId:
            case UIA_IsControlElementPropertyId: {
                //TODO how to handle ControlElement versus ContentElement
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = getView() != null || !isIgnored();
                break;
            }
            case UIA_IsEnabledPropertyId: {
                Boolean disabled = (Boolean)getAttribute(DISABLED);
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = disabled != null ? !disabled : true;
                break;
            }
            case UIA_IsKeyboardFocusablePropertyId: {
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = true; //TODO return focusTraversable
                break;
            }
            case UIA_IsPasswordPropertyId: {
                AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BOOL;
                variant.boolVal = role == AccessibleRole.PASSWORD_FIELD;
                break;
            }
            case UIA_AutomationIdPropertyId: {
                /* Not required but useful for debugging */
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BSTR;
                variant.bstrVal = "JavaFX"+id;
                break;
            }
            case UIA_ProviderDescriptionPropertyId: {
                /* Not required but useful for debugging */
                variant = new WinVariant();
                variant.vt = WinVariant.VT_BSTR;
                variant.bstrVal = "JavaFXProvider";
                break;
            }
            default:
        }
        return variant;
    }
    
    float[] getPlatformBounds(float x, float y, float w, float h) {
        return proxy.getPlatformBounds(x, y, w, h);
    }
    /* **********************************************/
    /*       IRawElementProviderFragment           */
    /* **********************************************/
    public float[] get_BoundingRectangle() {
        if (isDisposed()) return null;
        /* No needs to answer for the root */
        if (getView() != null) return null;

        Bounds bounds = (Bounds)getAttribute(BOUNDS);
        if (bounds != null) {
            return getPlatformBounds(   (float) bounds.getMinX(),
                                        (float) bounds.getMinY(),
                                        (float) bounds.getWidth(),
                                        (float) bounds.getHeight());                    
        }
        return null;
    }

    public long get_FragmentRoot() {
        if (isDisposed()) return 0L;
        Scene scene = (Scene)getAttribute(SCENE);
        if (scene == null) return 0L;
        WinAccessible acc = (WinAccessible)getAccessible(scene);
        if (acc == null || acc.isDisposed()) return 0L;
        return acc.getNativeAccessible();
    }

    public long[] GetEmbeddedFragmentRoots() {
        if (isDisposed()) return null;
        return null;
    }

    public int[] GetRuntimeId() {
        if (isDisposed()) return null;

        /* MSDN: Implementations should return NULL for a top-level element that is hosted in a window. */
        if (getView() != null) return null;
        return new int[] {UiaAppendRuntimeId, id};
    }

    private long NavigateListView(WinAccessible listItemAccessible, int direction) {
        Accessible listAccessible = listItemAccessible.getContainer();
        if (listAccessible == null) return 0;
        Integer count = (Integer)listAccessible.getAttribute(ITEM_COUNT);
        if (count == null || count == 0) return 0;
        Integer index = (Integer)listItemAccessible.getAttribute(INDEX);
        if (index == null) return 0;

        /* A view implementation can use stock items to measuring, these items can
         * have index equal to -1 for identification. See ViewFlow#accumCell as an example.
         * These items should be ignored to avoid incorrect item count computation by
         * the screen reader.
         */
        if (!(0 <= index && index < count)) return 0;
        switch (direction) {
            case NavigateDirection_NextSibling: index++; break;
            case NavigateDirection_PreviousSibling: index--; break;
            case NavigateDirection_FirstChild: index = 0; break;
            case NavigateDirection_LastChild: index = count - 1; break;
        }
        if (!(0 <= index && index < count)) return 0;
        Node node = (Node)listAccessible.getAttribute(ITEM_AT_INDEX, index);
        return getNativeAccessible(node);
    }

    public long Navigate(int direction) {
        if (isDisposed()) return 0;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        /* special case for the tree item hierarchy, as expected by Windows */
        boolean treeCell = role == AccessibleRole.TREE_ITEM;
        Node node = null;
        switch (direction) {
            case NavigateDirection_Parent: {
                /* Return null for the top level node */
                if (getView() != null) return 0L;

                if (treeCell) {
                    node = (Node)getAttribute(TREE_ITEM_PARENT);
                    if (node == null) {
                        /* root tree item case*/
                        WinAccessible acc = (WinAccessible)getContainer();
                        return acc != null ? acc.getNativeAccessible() : 0L;
                    }
                } else {
                    node = (Node)getAttribute(PARENT);
                    if (node == null) {
                        /* This is the root node of the scene or the scene itself */
                        Scene scene = (Scene)getAttribute(SCENE);
                        WinAccessible acc = (WinAccessible)getAccessible(scene);
                        /* Return 0 if we are already at the scene or if scene is null */
                        if (acc == null || acc == this || acc.isDisposed()) return 0L;
                        return acc.getNativeAccessible();
                    }
                }
                break;
            }
            case NavigateDirection_NextSibling:
            case NavigateDirection_PreviousSibling: {
                if (role == AccessibleRole.LIST_ITEM) {
                    return NavigateListView(this, direction);
                }

                Node parent = (Node)getAttribute(treeCell ? TREE_ITEM_PARENT : PARENT);
                /*
                 * When the parent is NULL is indicates either the root node for the scene
                 * or the root tree item in a tree view. Either way, there is no siblings.
                 */
                if (parent != null) {
                    WinAccessible parentAccessible = (WinAccessible)getAccessible(parent);
                    Function<Integer, Node> getChild;
                    int count = 0;
                    if (treeCell) {
                        Integer result = (Integer)parentAccessible.getAttribute(TREE_ITEM_COUNT);
                        if (result == null) return 0;
                        count = result;
                        getChild = index -> {
                            return (Node)parentAccessible.getAttribute(AccessibleAttribute.TREE_ITEM_AT_INDEX, index);
                        };
                    } else {
                        List<Node> children = getUnignoredChildren(parentAccessible);
                        if (children == null) return 0;
                        count = children.size();
                        getChild = index -> {
                            return children.get(index);
                        };
                    }

                    int lastIndex = parentAccessible.lastIndex;
                    int currentIndex = -1;
                    if (0 <= lastIndex && lastIndex < count && getNativeAccessible(getChild.apply(lastIndex)) == peer) {
                        currentIndex = lastIndex;
                    } else {
                        for (int i = 0; i < count; i++) {
                            if (getNativeAccessible(getChild.apply(i)) == peer) {
                                currentIndex = i;
                                break;
                            }
                        }
                    }
                    if (currentIndex != -1) {
                        if (direction == NavigateDirection_NextSibling) {
                            currentIndex++;
                        } else {
                            currentIndex--;
                        }
                        if (0 <= currentIndex && currentIndex < count) {
                            node = getChild.apply(currentIndex);
                            parentAccessible.lastIndex = currentIndex;
                        }
                    }
                }
                break;
            }
            case NavigateDirection_FirstChild:
            case NavigateDirection_LastChild: {
                lastIndex = -1;
                if (role == AccessibleRole.LIST_VIEW) {
                    /* Windows 7. Initially the ComboBox contains the ListView,
                     * but the ListCells will only be created if one an item is
                     * selected. This causes Narrator to read combo box with
                     * zero items. The fix is to ask for first item, which will
                     * cause NavigateListView to be used.
                     * */
                    getAttribute(ITEM_AT_INDEX, 0);
                }
                if (role == AccessibleRole.TREE_VIEW) {
                    /* The TreeView only returns the root node as child */
                    lastIndex = 0;
                    node = (Node)getAttribute(ROW_AT_INDEX, 0);
                } else if (treeCell) {
                    Integer count = (Integer)getAttribute(TREE_ITEM_COUNT);
                    if (count != null && count > 0) {
                        lastIndex = direction == NavigateDirection_FirstChild ? 0 : count - 1;
                        node = (Node)getAttribute(TREE_ITEM_AT_INDEX, lastIndex);
                    }
                } else {
                    List<Node> children = getUnignoredChildren(this);
                    if (children != null && children.size() > 0) {
                        lastIndex = direction == NavigateDirection_FirstChild ? 0 : children.size() - 1;
                        node = children.get(lastIndex);
                    }
                    if (node != null) {
                        role = (AccessibleRole)getAccessible(node).getAttribute(ROLE);
                        if (role == AccessibleRole.LIST_ITEM) {
                            WinAccessible itemAcc = (WinAccessible)getAccessible(node);
                            return NavigateListView(itemAcc, direction);
                        }
                    }
                }
                break;
            }
        }
        return getNativeAccessible(node);
    }

    public void SetFocus() {
        if (isDisposed()) return;
        executeAction(AccessibleAction.REQUEST_FOCUS);
    }

    /* **********************************************/
    /*     IRawElementProviderFragmentRoot         */
    /* **********************************************/
    public long ElementProviderFromPoint(double x, double y) {
        if (isDisposed()) return 0;
        Node node = (Node)getAttribute(NODE_AT_POINT, new Point2D(x, y));
        LOG.trace(this, () -> "ElementProviderFromPont(" + x + ", " + y + ") -> " + node);
        return getNativeAccessible(node);
    }
    public Node ElementProviderFromPointAsNode(double x, double y) {
        if (isDisposed()) return null;
        Node node = (Node)getAttribute(NODE_AT_POINT, new Point2D(x, y));
        LOG.trace(this, () -> "ElementProviderFromPointAsNode(" + x + ", " + y + ") -> " + node);
        return node;
    }
    

    public long GetFocus() {
        if (isDisposed()) return 0;
        Node node = (Node)getAttribute(FOCUS_NODE);
        if (node == null) return 0L;
        Node item = (Node)getAccessible(node).getAttribute(FOCUS_ITEM);
        if (item != null) return getNativeAccessible(item);
        return getNativeAccessible(node);
    }

    /* **********************************************/
    /*     IRawElementProviderAdviseEvents         */
    /* **********************************************/
    public void AdviseEventAdded(int eventId, long propertyIDs) {
        /* Implementing IRawElementProviderAdviseEvents ensures
         * that the window is announced by Narrator when it first
         * opens. No further action is required.
         */
    }

    public void AdviseEventRemoved(int eventId, long propertyIDs) {
        /* See AdviseEventAdded() */
    }

    /* **********************************************/
    /*             IInvokeProvider                 */
    /* **********************************************/
    public void Invoke() {
        if (isDisposed()) return;
        executeAction(AccessibleAction.FIRE);
    }

    /* **********************************************/
    /*           ISelectionProvider                */
    /* **********************************************/
    public long[] GetSelection() {
        if (isDisposed()) return null;

        /*
         * GetSelection() is sent by ISelectionProvider and ITextProvider.
         * Check the role before processing message.
         */
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == null) return null;
        switch (role) {
            case TREE_TABLE_VIEW:
            case TABLE_VIEW:
            case TREE_VIEW:
            case LIST_VIEW: {
                @SuppressWarnings("unchecked")
                ObservableList<Node> selection = (ObservableList<Node>)getAttribute(SELECTED_ITEMS);
                if (selection != null) {
                    return selection.stream().mapToLong(n -> getNativeAccessible(n)).toArray();
                }
                break;
            }
            case TAB_PANE:
            case PAGINATION: {
                Node node = (Node)getAttribute(FOCUS_ITEM);
                if (node != null) {
                    return new long[] {getNativeAccessible(node)};
                }
                break;
            }
            case TEXT_FIELD:
            case TEXT_AREA: {
                ProxyTextRangeProvider selectionRange = ProxyTextRangeProvider.wrap(proxy, new WinTextRangeProvider(this));
                selectionRange.setOnNativeDelete(() -> {
                  LOG.debug(this, () -> "selectionRange auto clear " + selectionRange);
                });
                Integer result = (Integer)getAttribute(SELECTION_START);
                int start = result != null ? result : 0;
                int end = -1;
                int length = -1;
                if (start >= 0) {
                    result = (Integer)getAttribute(SELECTION_END);
                    end = result != null ? result : 0;
                    if (end >= start) {
                        String string = (String)getAttribute(TEXT);
                        length = string != null ? string.length() : 0;
                    }
                }
                if (length != -1 && end <= length) {
                    selectionRange.glassImpl.setRange(start, end);
                    curSelectionStart = start;
                    curSelectionEnd = end;
                } else {
                    selectionRange.glassImpl.setRange(0, 0);
                    curSelectionStart = 0;
                    curSelectionEnd = 0;
                }

                return new long[] {selectionRange.getNativeProvider()};
            }
            default:
        }
        return null;
    }

    public boolean get_CanSelectMultiple() {
        if (isDisposed()) return false;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case LIST_VIEW:
                case TABLE_VIEW:
                case TREE_VIEW:
                case TREE_TABLE_VIEW:
                    return Boolean.TRUE.equals(getAttribute(MULTIPLE_SELECTION));
                default:
            }
        }
        return false;
    }

    public boolean get_IsSelectionRequired() {
        if (isDisposed()) return false;
        //TODO: this may not be true...
        return true;
    }

    /* **********************************************/
    /*           IRangeValueProvider               */
    /* **********************************************/
    public void SetValue(double val) {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case SLIDER:
                case SCROLL_BAR:
                    executeAction(AccessibleAction.SET_VALUE, val);
                    break;
                default:
            }
        }
    }

    public double get_Value() {
        if (isDisposed()) return 0;
        if (Boolean.TRUE.equals(getAttribute(INDETERMINATE))) return 0;
        Double value = (Double)getAttribute(VALUE);
        return value != null ? value : 0;
    }

    /*
     * Note that this method is called by the IValueProvider also.
     */
    public boolean get_IsReadOnly() {
        if (isDisposed()) return false;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case SLIDER: return false;
                case SCROLL_BAR: return true;
                case TEXT_FIELD:
                case TEXT_AREA:
                case COMBO_BOX: return Boolean.FALSE.equals(getAttribute(EDITABLE));
                default:
            }
        }
        return true;
    }

    public double get_Maximum() {
        if (isDisposed()) return 0;
        Double value = (Double)getAttribute(MAX_VALUE);
        return value != null ? value : 0;
    }

    public double get_Minimum() {
        if (isDisposed()) return 0;
        Double value = (Double)getAttribute(MIN_VALUE);
        return value != null ? value : 0;
    }

    public double get_LargeChange() {
        if (isDisposed()) return 0;
        return 10;//TODO
    }

    public double get_SmallChange() {
        if (isDisposed()) return 0;
        return 3;//TODO
    }

    /* **********************************************/
    /*             IValueProvider                  */
    /* **********************************************/
    public void SetValueString(String val) {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case TEXT_FIELD:
                case TEXT_AREA:
                    executeAction(AccessibleAction.SET_TEXT, val);
                    break;
                default:
            }
        }
    }

    public String get_ValueString() {
        if (isDisposed()) return null;
        return (String)getAttribute(TEXT);
    }

    /***********************************************/
    /*          ISelectionItemProvider             */
    /***********************************************/
    public void Select() {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case PAGE_ITEM:
                case TAB_ITEM:
                    executeAction(AccessibleAction.REQUEST_FOCUS);
                    break;
                case RADIO_BUTTON:
                case BUTTON:
                case TOGGLE_BUTTON:
                case INCREMENT_BUTTON:
                case DECREMENT_BUTTON:
                    executeAction(AccessibleAction.FIRE);
                    break;
                case LIST_ITEM:
                case TREE_ITEM:
                case TABLE_CELL:
                case TREE_TABLE_CELL:
                    changeSelection(true, true);
                    break;
                default:
            }
        }
    }

    public void AddToSelection() {
        if (isDisposed()) return;
        changeSelection(true, false);
    }

    public void RemoveFromSelection() {
        if (isDisposed()) return;
        changeSelection(false, false);
    }

    public boolean get_IsSelected() {
        if (isDisposed()) return false;
        return Boolean.TRUE.equals(getAttribute(SELECTED));
    }

    public long get_SelectionContainer() {
        if (isDisposed()) return 0;
        WinAccessible acc = (WinAccessible)getContainer();
        return acc != null ? acc.getNativeAccessible() : 0L;
    }

    /* **********************************************/
    /*              ITextProvider                  */
    /* **********************************************/
    public long[] GetVisibleRanges() {
        if (isDisposed()) return null;
        return new long[] {get_DocumentRange()};
    }

    public long RangeFromChild(long childElement) {
        if (isDisposed()) return 0;
        return 0;
    }

    public long RangeFromPoint(double x, double y) {
        if (isDisposed()) return 0;
        Integer offset = (Integer)getAttribute(OFFSET_AT_POINT, new Point2D(x, y));
        if (offset != null) {
            WinTextRangeProvider range = new WinTextRangeProvider(this);
            range.setRange(offset, offset);
            return ProxyTextRangeProvider.wrapNative(proxy, range);
        }
        return 0;
    }

    public long get_DocumentRange() {
        if (isDisposed()) return 0;

        String text = (String)getAttribute(TEXT);
        if (text == null) return 0;

        ProxyTextRangeProvider documentRange = ProxyTextRangeProvider.wrap(proxy, new WinTextRangeProvider(this));
        documentRangeRequested = true;
        documentRange.setOnNativeDelete(() -> {
          LOG.debug(this, () -> "documentRange auto clear");
        });
        documentRange.glassImpl.setRange(0, text.length());
        return documentRange.getNativeProvider();
    }

    public int get_SupportedTextSelection() {
        if (isDisposed()) return 0;
        /* Before this can be done extra API for multiple selection will be required. */
//        if (Boolean.TRUE.equals(getAttribute(MULTIPLE_SELECTION))) {
//            return SupportedTextSelection_Multiple;
//        }
        return SupportedTextSelection_Single;
    }

    /* **********************************************/
    /*             IGridProvider                   */
    /* **********************************************/
    public int get_ColumnCount() {
        if (isDisposed()) return 0;
        Integer count = (Integer)getAttribute(COLUMN_COUNT);

        /*
         * JFX does not require ListView to report column count == 1
         * But Windows wants a column count of (at least) 1.
         */
        return count != null ? count : 1;
    }

    public int get_RowCount() {
        if (isDisposed()) return 0;
        Integer count = (Integer)getAttribute(ROW_COUNT);
        return count != null ? count : 0;
    }

    public long GetItem(int row, int column) {
        if (isDisposed()) return 0;
        Node node = (Node)getAttribute(CELL_AT_ROW_COLUMN, row, column);
        return getNativeAccessible(node);
    }

    /* **********************************************/
    /*             IGridItemProvider               */
    /* **********************************************/
    public int get_Column() {
        if (isDisposed()) return 0;
        Integer result = (Integer)getAttribute(COLUMN_INDEX);
        return result != null ? result : 0;
    }

    public int get_ColumnSpan() {
        if (isDisposed()) return 0;
        return 1;
    }

    public long get_ContainingGrid() {
        if (isDisposed()) return 0;
        WinAccessible acc = (WinAccessible)getContainer();
        return acc != null ? acc.getNativeAccessible() : 0L;
    }

    public int get_Row() {
        if (isDisposed()) return 0;
        Integer result = null;
        AccessibleRole role = (AccessibleRole) getAttribute(ROLE);
        if (role != null) {
            switch (role) {
                case TABLE_ROW:
                case TREE_TABLE_ROW:
                case LIST_ITEM: result = (Integer)getAttribute(INDEX); break;
                case TREE_TABLE_CELL:
                case TABLE_CELL: result = (Integer)getAttribute(ROW_INDEX); break;
                default:
            }
        }
        return result != null ? result : 0;
    }

    public int get_RowSpan() {
        if (isDisposed()) return 0;
        return 1;
    }

    /* **********************************************/
    /*               ITableProvider                */
    /* **********************************************/
    public long[] GetColumnHeaders() {
        if (isDisposed()) return null;
       /* No support in JFX to return all columns with a single call */
       return null;
    }

    public long[] GetRowHeaders() {
        if (isDisposed()) return null;
       /* No row header support on JFX */
       return null;
    }

    public int get_RowOrColumnMajor() {
        if (isDisposed()) return 0;
        return RowOrColumnMajor_RowMajor;
    }

    /* **********************************************/
    /*             ITableItemProvider              */
    /* **********************************************/
    public long[] GetColumnHeaderItems() {
        if (isDisposed()) return null;
       Integer columnIndex = (Integer)getAttribute(COLUMN_INDEX);
       if (columnIndex == null) return null;
       Accessible acc = getContainer();
       if (acc == null) return null;
       Node column = (Node)acc.getAttribute(COLUMN_AT_INDEX, columnIndex);
       if (column == null) return null;
       return new long[] {getNativeAccessible(column)};
    }

    public long[] GetRowHeaderItems() {
        if (isDisposed()) return null;
        /* No row header support on JFX */
       return null;
    }

    /***********************************************/
    /*             IToggleProvider                 */
    /***********************************************/
    public void Toggle() {
        if (isDisposed()) return;
        executeAction(AccessibleAction.FIRE);
    }

    public int get_ToggleState() {
        if (isDisposed()) return 0;
        if (Boolean.TRUE.equals(getAttribute(INDETERMINATE))) {
            return ToggleState_Indeterminate;
        }
        boolean selected = Boolean.TRUE.equals(getAttribute(SELECTED));
        return selected ? ToggleState_On : ToggleState_Off;
    }

    /***********************************************/
    /*             IExpandCollapseProvider         */
    /***********************************************/
    public void Collapse() {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == AccessibleRole.TOOL_BAR) {
            Node button = (Node)getAttribute(OVERFLOW_BUTTON);
            if (button != null) {
                getAccessible(button).executeAction(AccessibleAction.FIRE);
            }
            return;
        }
        if (role == AccessibleRole.TREE_TABLE_CELL) {
            Accessible row = getRow();
            if (row != null) row.executeAction(AccessibleAction.COLLAPSE);
            return;
        }
        executeAction(AccessibleAction.COLLAPSE);
    }

    public void Expand() {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == AccessibleRole.TOOL_BAR) {
            Node button = (Node)getAttribute(OVERFLOW_BUTTON);
            if (button != null) {
                getAccessible(button).executeAction(AccessibleAction.FIRE);
            }
            return;
        }
        if (role == AccessibleRole.TREE_TABLE_CELL) {
            Accessible row = getRow();
            if (row != null) row.executeAction(AccessibleAction.EXPAND);
            return;
        }
        executeAction(AccessibleAction.EXPAND);
    }

    public int get_ExpandCollapseState() {
        if (isDisposed()) return 0;

        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == AccessibleRole.TOOL_BAR) {
            Node button = (Node)getAttribute(OVERFLOW_BUTTON);
            if (button != null) {
                boolean visible = Boolean.TRUE.equals(getAccessible(button).getAttribute(VISIBLE));
                return visible ? ExpandCollapseState_Collapsed : ExpandCollapseState_Expanded;
            }
        }

        if (role == AccessibleRole.TREE_TABLE_CELL) {
            Accessible row = getRow();
            if (row == null) return ExpandCollapseState_LeafNode;
            Object o = row.getAttribute(LEAF);
            if (Boolean.TRUE.equals(o)) return ExpandCollapseState_LeafNode;
            o = row.getAttribute(EXPANDED);
            boolean isExpanded = Boolean.TRUE.equals(o);
            return isExpanded ? ExpandCollapseState_Expanded : ExpandCollapseState_Collapsed;
        }

        /*
         * We ask if the accessible is a leaf for the TreeItem case where
         * we want to return that it is a leaf node. In other cases
         * (e.g. ComboBox) this will return false which means the ComboBox is
         * not a leaf and the final return statement falls through to returning
         * either expanded or collapsed, as expected.
         */
        Object o = getAttribute(LEAF);
        if (Boolean.TRUE.equals(o)) return ExpandCollapseState_LeafNode;

        o = getAttribute(EXPANDED);
        boolean isExpanded = Boolean.TRUE.equals(o);
        return isExpanded ? ExpandCollapseState_Expanded : ExpandCollapseState_Collapsed;
    }

    /* **********************************************/
    /*             ITransformProvider              */
    /* **********************************************/
    public boolean get_CanMove() {
        return false;
    }

    public boolean get_CanResize() {
        return false;
    }

    public boolean get_CanRotate() {
        return false;
    }

    public void Move(double x, double y) {
    }

    public void Resize(double width, double height) {
    }

    public void Rotate(double degrees) {
    }

    /* **********************************************/
    /*             IScrollProvider                 */
    /* **********************************************/
    public void Scroll(int horizontalAmount, int verticalAmount) {
        if (isDisposed()) return;

        /* dealing with vertical scroll first */
        if (get_VerticallyScrollable()) {
            Node vsb = (Node)getAttribute(VERTICAL_SCROLLBAR);
            Accessible vsba = getAccessible(vsb);
            if (vsba == null) return;
            switch (verticalAmount) {
                case ScrollAmount_LargeIncrement:
                    vsba.executeAction(AccessibleAction.BLOCK_INCREMENT);
                    break;
                case ScrollAmount_SmallIncrement:
                    vsba.executeAction(AccessibleAction.INCREMENT);
                    break;
                case ScrollAmount_LargeDecrement:
                    vsba.executeAction(AccessibleAction.BLOCK_DECREMENT);
                    break;
                case ScrollAmount_SmallDecrement:
                    vsba.executeAction(AccessibleAction.DECREMENT);
                    break;
                default:
            }
        }

        /* now dealing with horizontal scroll */
        if (get_HorizontallyScrollable()) {
            Node hsb = (Node)getAttribute(HORIZONTAL_SCROLLBAR);
            Accessible hsba = getAccessible(hsb);
            if (hsba == null) return;
            switch (horizontalAmount) {
                case ScrollAmount_LargeIncrement:
                    hsba.executeAction(AccessibleAction.BLOCK_INCREMENT);
                    break;
                case ScrollAmount_SmallIncrement:
                    hsba.executeAction(AccessibleAction.INCREMENT);
                    break;
                case ScrollAmount_LargeDecrement:
                    hsba.executeAction(AccessibleAction.BLOCK_DECREMENT);
                    break;
                case ScrollAmount_SmallDecrement:
                    hsba.executeAction(AccessibleAction.DECREMENT);
                    break;
                default:
            }
        }
    }

    public void SetScrollPercent(double horizontalPercent, double verticalPercent) {
        if (isDisposed()) return;

        /* dealing with vertical scroll first */
        if (verticalPercent != UIA_ScrollPatternNoScroll && get_VerticallyScrollable()) {
            Node vsb = (Node)getAttribute(VERTICAL_SCROLLBAR);
            Accessible acc = getAccessible(vsb);
            if (acc == null) return;
            Double min = (Double)acc.getAttribute(MIN_VALUE);
            Double max = (Double)acc.getAttribute(MAX_VALUE);
            if (min != null && max != null) {
                acc.executeAction(AccessibleAction.SET_VALUE, (max-min)*(verticalPercent/100)+min);
            }
        }

        /* now dealing with horizontal scroll */
        if (horizontalPercent != UIA_ScrollPatternNoScroll && get_HorizontallyScrollable()) {
            Node hsb = (Node)getAttribute(HORIZONTAL_SCROLLBAR);
            Accessible acc = getAccessible(hsb);
            if (acc == null) return;
            Double min = (Double)acc.getAttribute(MIN_VALUE);
            Double max = (Double)acc.getAttribute(MAX_VALUE);
            if (min != null && max != null) {
                acc.executeAction(AccessibleAction.SET_VALUE, (max-min)*(horizontalPercent/100)+min);
            }
        }
    }

    public boolean get_HorizontallyScrollable() {
        if (isDisposed()) return false;

        Node hsb = (Node)getAttribute(HORIZONTAL_SCROLLBAR);
        if (hsb == null) return false;

        Boolean visible = (Boolean)getAccessible(hsb).getAttribute(VISIBLE);
        return Boolean.TRUE.equals(visible);
    }

    public double get_HorizontalScrollPercent() {
        if (isDisposed()) return 0;

        if (!get_HorizontallyScrollable()) {
            return UIA_ScrollPatternNoScroll;
        }

        Node hsb = (Node) getAttribute(HORIZONTAL_SCROLLBAR);
        if (hsb != null) {
            /* Windows expects a percentage between 0 and 100 */
            Accessible hsba = getAccessible(hsb);
            Double value = (Double)hsba.getAttribute(VALUE);
            if (value == null) return 0;
            Double max = (Double)hsba.getAttribute(MAX_VALUE);
            if (max == null) return 0;
            Double min = (Double)hsba.getAttribute(MIN_VALUE);
            if (min == null) return 0;
            return (100 * (value - min)) / (max - min);
        }

        return 0;
    }

    public double get_HorizontalViewSize() {
        if (isDisposed()) return 0;
        if (!get_HorizontallyScrollable()) return 100; /* MSDN spec */
        Node content = (Node) getAttribute(CONTENTS);
        if (content == null) return 100;
        Bounds contentBounds = (Bounds)getAccessible(content).getAttribute(BOUNDS);
        if (contentBounds == null) return 0;
        Bounds scrollPaneBounds = (Bounds)getAttribute(BOUNDS);
        if (scrollPaneBounds == null) return 0;
        return scrollPaneBounds.getWidth() / contentBounds.getWidth() * 100;
    }

    public boolean get_VerticallyScrollable() {
        if (isDisposed()) return false;

        Node vsb = (Node) getAttribute(VERTICAL_SCROLLBAR);
        if (vsb == null) return false;

        Boolean visible = (Boolean)getAccessible(vsb).getAttribute(VISIBLE);
        return Boolean.TRUE.equals(visible);
    }

    public double get_VerticalScrollPercent() {
        if (isDisposed()) return 0;

        if (!get_VerticallyScrollable()) {
            return UIA_ScrollPatternNoScroll;
        }

        Node vsb = (Node) getAttribute(AccessibleAttribute.VERTICAL_SCROLLBAR);
        if (vsb != null) {
            /* Windows expects a percentage between 0 and 100 */
            Accessible vsba = getAccessible(vsb);
            Double value = (Double)vsba.getAttribute(VALUE);
            if (value == null) return 0;
            Double max = (Double)vsba.getAttribute(MAX_VALUE);
            if (max == null) return 0;
            Double min = (Double)vsba.getAttribute(MIN_VALUE);
            if (min == null) return 0;
            return (100 * (value - min)) / (max - min);
        }

        return 0;
    }

    public double get_VerticalViewSize() {
        if (isDisposed()) return 0;
        if (!get_VerticallyScrollable()) return 100;

        double contentHeight = 0;

        Bounds scrollPaneBounds = (Bounds) getAttribute(BOUNDS);
        if (scrollPaneBounds == null) return 0;
        double scrollPaneHeight = scrollPaneBounds.getHeight();

        AccessibleRole role = (AccessibleRole) getAttribute(ROLE);
        if (role == null) return 0;
        if (role == AccessibleRole.SCROLL_PANE) {
            Node content = (Node) getAttribute(CONTENTS);
            if (content != null) {
                Bounds contentBounds = (Bounds)getAccessible(content).getAttribute(BOUNDS);
                contentHeight = contentBounds == null ? 0 : contentBounds.getHeight();
            }
        } else {
            Integer itemCount = 0;
            switch (role) {
                case LIST_VIEW:
                    itemCount = (Integer) getAttribute(ITEM_COUNT);
                    break;
                case TABLE_VIEW:
                case TREE_VIEW:
                case TREE_TABLE_VIEW:
                    itemCount = (Integer) getAttribute(ROW_COUNT);
                    break;
                default:
            }

            /*
             * Do a quick calculation to approximate the height of the
             * content area by assuming a fixed height multiplied by the number
             * of items. The default height we use is 24px, which is the default
             * height as specified in com.sun.javafx.scene.control.skin.CellSkinBase.
             */
            contentHeight = itemCount == null ? 0 : itemCount * 24;
        }

        return contentHeight == 0 ? 0 : scrollPaneHeight / contentHeight * 100;
    }

    /* **********************************************/
    /*             IScrollItemProvider             */
    /* **********************************************/
    public void ScrollIntoView() {
        if (isDisposed()) return;
        AccessibleRole role = (AccessibleRole)getAttribute(ROLE);
        if (role == null) return;
        Accessible container = getContainer();
        if (container == null) return;
        Node item = null;
        switch (role) {
            case LIST_ITEM: {
                Integer index = (Integer)getAttribute(INDEX);
                if (index != null) {
                    item = (Node)container.getAttribute(ITEM_AT_INDEX, index);
                }
                break;
            }
            case TREE_ITEM: {
                Integer index = (Integer)getAttribute(INDEX);
                if (index != null) {
                    item = (Node)container.getAttribute(ROW_AT_INDEX, index);
                }
                break;
            }
            case TABLE_CELL:
            case TREE_TABLE_CELL: {
                Integer rowIndex = (Integer)getAttribute(ROW_INDEX);
                Integer columnIndex = (Integer)getAttribute(COLUMN_INDEX);
                if (rowIndex != null && columnIndex != null) {
                    item = (Node)container.getAttribute(CELL_AT_ROW_COLUMN, rowIndex, columnIndex);
                }
                break;
            }
            default:
        }
        if (item != null) {
            container.executeAction(AccessibleAction.SHOW_ITEM, item);
        }
    }
}
