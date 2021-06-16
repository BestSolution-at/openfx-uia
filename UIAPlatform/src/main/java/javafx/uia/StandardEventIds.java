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
public enum StandardEventIds implements IEventId {
    UIA_ToolTipOpenedEventId(20000),
    UIA_ToolTipClosedEventId(20001),
    UIA_StructureChangedEventId(20002),
    UIA_MenuOpenedEventId(20003),
    UIA_AutomationPropertyChangedEventId(20004),
    UIA_AutomationFocusChangedEventId(20005),
    UIA_AsyncContentLoadedEventId(20006),
    UIA_MenuClosedEventId(20007),
    UIA_LayoutInvalidatedEventId(20008),
    UIA_Invoke_InvokedEventId(20009),
    UIA_SelectionItem_ElementAddedToSelectionEventId(20010),
    UIA_SelectionItem_ElementRemovedFromSelectionEventId(20011),
    UIA_SelectionItem_ElementSelectedEventId(20012),
    UIA_Selection_InvalidatedEventId(20013),
    UIA_Text_TextSelectionChangedEventId(20014),
    UIA_Text_TextChangedEventId(20015),
    UIA_Window_WindowOpenedEventId(20016),
    UIA_Window_WindowClosedEventId(20017),
    UIA_MenuModeStartEventId(20018),
    UIA_MenuModeEndEventId(20019),
    UIA_InputReachedTargetEventId(20020),
    UIA_InputReachedOtherElementEventId(20021),
    UIA_InputDiscardedEventId(20022),
    UIA_SystemAlertEventId(20023),
    UIA_LiveRegionChangedEventId(20024),
    UIA_HostedFragmentRootsInvalidatedEventId(20025),
    UIA_Drag_DragStartEventId(20026),
    UIA_Drag_DragCancelEventId(20027),
    UIA_Drag_DragCompleteEventId(20028),
    UIA_DropTarget_DragEnterEventId(20029),
    UIA_DropTarget_DragLeaveEventId(20030),
    UIA_DropTarget_DroppedEventId(20031),
    UIA_TextEdit_TextChangedEventId(20032),
    UIA_TextEdit_ConversionTargetChangedEventId(20033),
    UIA_ChangesEventId(20034),
    UIA_NotificationEventId(20035),
    UIA_ActiveTextPositionChangedEventId(20036);

    private final int value;
    private StandardEventIds(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    public static Optional<StandardEventIds> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}