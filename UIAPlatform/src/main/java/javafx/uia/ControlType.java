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

// TODO javadoc
public enum ControlType implements INativeEnum {
	UIA_ButtonControlTypeId(50000),
	UIA_CalendarControlTypeId(50001),
	UIA_CheckBoxControlTypeId(50002),
	UIA_ComboBoxControlTypeId(50003),
	UIA_EditControlTypeId(50004),
	UIA_HyperlinkControlTypeId(50005),
	UIA_ImageControlTypeId(50006),
	UIA_ListItemControlTypeId(50007),
	UIA_ListControlTypeId(50008),
	UIA_MenuControlTypeId(50009),
	UIA_MenuBarControlTypeId(50010),
	UIA_MenuItemControlTypeId(50011),
	UIA_ProgressBarControlTypeId(50012),
	UIA_RadioButtonControlTypeId(50013),
	UIA_ScrollBarControlTypeId(50014),
	UIA_SliderControlTypeId(50015),
	UIA_SpinnerControlTypeId(50016),
	UIA_StatusBarControlTypeId(50017),
	UIA_TabControlTypeId(50018),
	UIA_TabItemControlTypeId(50019),
	UIA_TextControlTypeId(50020),
	UIA_ToolBarControlTypeId(50021),
	UIA_ToolTipControlTypeId(50022),
	UIA_TreeControlTypeId(50023),
	UIA_TreeItemControlTypeId(50024),
	UIA_CustomControlTypeId(50025),
	UIA_GroupControlTypeId(50026),
	UIA_ThumbControlTypeId(50027),
	UIA_DataGridControlTypeId(50028),
	UIA_DataItemControlTypeId(50029),
	UIA_DocumentControlTypeId(50030),
	UIA_SplitButtonControlTypeId(50031),
	UIA_WindowControlTypeId(50032),
	UIA_PaneControlTypeId(50033),
	UIA_HeaderControlTypeId(50034),
	UIA_HeaderItemControlTypeId(50035),
	UIA_TableControlTypeId(50036),
	UIA_TitleBarControlTypeId(50037),
	UIA_SeparatorControlTypeId(50038),
	UIA_SemanticZoomControlTypeId(50039),
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
