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

/**
 * Control Type Identifiers
 * see https://docs.microsoft.com/en-us/windows/win32/winauto/uiauto-controltype-ids
 */
public enum ControlType implements INativeEnum {
	/** Identifies the Button control type. */
	UIA_ButtonControlTypeId(50000),
	/** Identifies the Calendar control type. */
	UIA_CalendarControlTypeId(50001),
	/** Identifies the CheckBox control type. */
	UIA_CheckBoxControlTypeId(50002),
	/** Identifies the ComboBox control type.  */
	UIA_ComboBoxControlTypeId(50003),
	/** Identifies the Edit control type. */
	UIA_EditControlTypeId(50004),
	/** Identifies the Hyperlink control type.  */
	UIA_HyperlinkControlTypeId(50005),
	/** Identifies the Image control type.  */
	UIA_ImageControlTypeId(50006),
	/** Identifies the ListItem control type. */
	UIA_ListItemControlTypeId(50007),
	/** Identifies the List control type. */
	UIA_ListControlTypeId(50008),
	/** Identifies the Menu control type.  */
	UIA_MenuControlTypeId(50009),
	/** Identifies the MenuBar control type.  */
	UIA_MenuBarControlTypeId(50010),
	/** Identifies the MenuItem control type.  */
	UIA_MenuItemControlTypeId(50011),
	/** Identifies the ProgressBar control type.  */
	UIA_ProgressBarControlTypeId(50012),
	/** Identifies the RadioButton control type.  */
	UIA_RadioButtonControlTypeId(50013),
	/** Identifies the ScrollBar control type.  */
	UIA_ScrollBarControlTypeId(50014),
	/** Identifies the Slider control type.  */
	UIA_SliderControlTypeId(50015),
	/** Identifies the Spinner control type.  */
	UIA_SpinnerControlTypeId(50016),
	/** Identifies the StatusBar control type.  */
	UIA_StatusBarControlTypeId(50017),
	/** Identifies the Tab control type.  */
	UIA_TabControlTypeId(50018),
	/** Identifies the TabItem control type.  */
	UIA_TabItemControlTypeId(50019),
	/** Identifies the Text control type.  */
	UIA_TextControlTypeId(50020),
	/** Identifies the ToolBar control type.  */
	UIA_ToolBarControlTypeId(50021),
	/** Identifies the ToolTip control type.  */
	UIA_ToolTipControlTypeId(50022),
	/** Identifies the Tree control type.  */
	UIA_TreeControlTypeId(50023),
	/** Identifies the TreeItem control type.  */
	UIA_TreeItemControlTypeId(50024),
	/** Identifies the Custom control type. For more information, see Custom Properties, Events, and Control Patterns.  */
	UIA_CustomControlTypeId(50025),
	/** Identifies the Group control type.  */
	UIA_GroupControlTypeId(50026),
	/** Identifies the Thumb control type.  */
	UIA_ThumbControlTypeId(50027),
	/** Identifies the DataGrid control type.  */
	UIA_DataGridControlTypeId(50028),
	/** Identifies the DataItem control type.  */
	UIA_DataItemControlTypeId(50029),
	/** Identifies the Document control type.  */
	UIA_DocumentControlTypeId(50030),
	/** Identifies the SplitButton control type.  */
	UIA_SplitButtonControlTypeId(50031),
	/** Identifies the Window control type.  */
	UIA_WindowControlTypeId(50032),
	/** Identifies the Pane control type.  */
	UIA_PaneControlTypeId(50033),
	/** Identifies the Header control type.  */
	UIA_HeaderControlTypeId(50034),
	/** Identifies the HeaderItem control type.  */
	UIA_HeaderItemControlTypeId(50035),
	/** Identifies the Table control type.  */
	UIA_TableControlTypeId(50036),
	/** Identifies the TitleBar control type.  */
	UIA_TitleBarControlTypeId(50037),
	/** Identifies the Separator control type.  */
	UIA_SeparatorControlTypeId(50038),
	/** Identifies the SemanticZoom control type. Supported starting with Windows 8. */
	UIA_SemanticZoomControlTypeId(50039),
	/** Identifies the AppBar control type. Supported starting with Windows 8.1. */
	UIA_AppBarControlTypeId(50040);

	private int nativeValue;
	
	@Override
	public int getNativeValue() {
		return nativeValue;
	}

	@Override
	public String getConstantName() {
		return name();
	}
	
	private ControlType(int nativeValue) {
		this.nativeValue = nativeValue;
	}
	
	public static Optional<ControlType> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
