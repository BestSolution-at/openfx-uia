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

// TODO add javadoc from ms docs
public enum StandardPatternIds implements IPatternId {
    UIA_InvokePatternId(10000),
    UIA_SelectionPatternId(10001),
    UIA_ValuePatternId(10002),
    UIA_RangeValuePatternId(10003),
    UIA_ScrollPatternId(10004),
    UIA_ExpandCollapsePatternId(10005),
    UIA_GridPatternId(10006),
    UIA_GridItemPatternId(10007),
    UIA_MultipleViewPatternId(10008),
    UIA_WindowPatternId(10009),
    UIA_SelectionItemPatternId(10010),
    UIA_DockPatternId(10011),
    UIA_TablePatternId(10012),
    UIA_TableItemPatternId(10013),
    UIA_TextPatternId(10014),
    UIA_TogglePatternId(10015),
    UIA_TransformPatternId(10016),
    UIA_ScrollItemPatternId(10017),
    UIA_LegacyIAccessiblePatternId(10018),
    UIA_ItemContainerPatternId(10019),
    UIA_VirtualizedItemPatternId(10020),
    UIA_SynchronizedInputPatternId(10021),
    UIA_ObjectModelPatternId(10022),
    UIA_AnnotationPatternId(10023),
    UIA_TextPattern2Id(10024),
    UIA_StylesPatternId(10025),
    UIA_SpreadsheetPatternId(10026),
    UIA_SpreadsheetItemPatternId(10027),
    UIA_TransformPattern2Id(10028),
    UIA_TextChildPatternId(10029),
    UIA_DragPatternId(10030),
    UIA_DropTargetPatternId(10031),
    UIA_TextEditPatternId(10032),
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
