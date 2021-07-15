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
package javafx.uia;

import java.util.Optional;
import java.util.stream.Stream;

/** Describes the named constants that identify Microsoft UI Automation control patterns. */
public enum StandardPatternIds implements IPatternId {
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementinginvoke">Invoke</a> control pattern. */
    UIA_InvokePatternId(10000),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingselection">Selection</a> control pattern. */
    UIA_SelectionPatternId(10001),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingvalue">Value</a> control pattern. */
    UIA_ValuePatternId(10002),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingrangevalue">RangeValue</a> control pattern. */
    UIA_RangeValuePatternId(10003),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingscroll">Scroll</a> control pattern. */
    UIA_ScrollPatternId(10004),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingexpandcollapse">ExpandCollapse</a> control pattern. */
    UIA_ExpandCollapsePatternId(10005),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementinggrid">Grid</a> control pattern. */
    UIA_GridPatternId(10006),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementinggriditem">GridItem</a> control pattern. */
    UIA_GridItemPatternId(10007),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingmultipleview">MultipleView</a> control pattern. */
    UIA_MultipleViewPatternId(10008),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingwindow">Window</a> control pattern. */
    UIA_WindowPatternId(10009),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingselectionitem">SelectionItem</a> control pattern. */
    UIA_SelectionItemPatternId(10010),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingdock">Dock</a> control pattern.  */
    UIA_DockPatternId(10011),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtable">Table</a> control pattern. */
    UIA_TablePatternId(10012),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtableitem">TableItem</a> control pattern. */
    UIA_TableItemPatternId(10013),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtextandtextrange">Text</a> control pattern. */
    UIA_TextPatternId(10014),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtoggle">Toggle</a> control pattern. */
    UIA_TogglePatternId(10015),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtransform">Transform</a> control pattern. */
    UIA_TransformPatternId(10016),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingscrollitem">ScrollItem</a> control pattern. */
    UIA_ScrollItemPatternId(10017),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementinglegacyiaccessible">LegacyIAccessible</a> control pattern. */
    UIA_LegacyIAccessiblePatternId(10018),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingitemcontainer">ItemContainer</a> control pattern. */
    UIA_ItemContainerPatternId(10019),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingvirtualizeditem">VirtualizedItem</a> control pattern. */
    UIA_VirtualizedItemPatternId(10020),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingsynchronizedinput">SynchronizedInput</a> control pattern. */
    UIA_SynchronizedInputPatternId(10021),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingobjectmodel">ObjectModel</a> control pattern. Supported starting with Windows 8. */
    UIA_ObjectModelPatternId(10022),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingannotation">Annotation</a> control pattern. Supported starting with Windows 8. */
    UIA_AnnotationPatternId(10023),
    /** Identifies the second version of the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtextandtextrange">Text</a> control pattern. Supported starting with Windows 8. */
    UIA_TextPattern2Id(10024),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/desktop/WinAuto/uiauto-implementingstyles">Styles</a> control pattern. Supported starting with Windows 8. */
    UIA_StylesPatternId(10025),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingspreadsheet">Spreadsheet</a> control pattern. Supported starting with Windows 8. */
    UIA_SpreadsheetPatternId(10026),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingspreadsheetitem">SpreadsheetItem</a> control pattern. Supported starting with Windows 8. */
    UIA_SpreadsheetItemPatternId(10027),
    /** Identifies the second version of the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingtransform">Transform</a> control pattern. Supported starting with Windows 8. */
    UIA_TransformPattern2Id(10028),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/textchild-control-pattern">TextChild</a> control pattern. Supported starting with Windows 8. */
    UIA_TextChildPatternId(10029),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/desktop/WinAuto/uiauto-implementingdrag">Drag</a> control pattern. Supported starting with Windows 8. */
    UIA_DragPatternId(10030),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/desktop/WinAuto/uiauto-implementingdroptarget">DropTarget</a> control pattern. Supported starting with Windows 8. */
    UIA_DropTargetPatternId(10031),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/desktop/api/uiautomationcore/nn-uiautomationcore-itexteditprovider"><strong>TextEdit</strong></a> control pattern. Supported starting with Windows 8.1. */
    UIA_TextEditPatternId(10032),
    /** Identifies the <a href="https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-implementingcustomnavigation">CustomNavigation</a> control pattern. Supported starting with Windows 10. */
    UIA_CustomNavigationPatternId(10033),
    UIA_SelectionPattern2Id(10034);


    private final int value;
    private StandardPatternIds(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    public static Optional<StandardPatternIds> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
    
    
}
