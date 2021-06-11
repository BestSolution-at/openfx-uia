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

/** Defines values that indicate the type of a notification event, and a hint to the listener about the processing of the event. For example, if multiple notifications are received, they should all be read, or only the last one should be read, and so on. */
public enum NotificationKind implements INativeEnum {
    /** The current element and/or the container has had something added to it that should be presented to the user. */
    ItemAdded(0),
    /** The current element has had something removed from inside of it that should be presented to the user. */
    ItemRemoved(1),
    /** The current element has a notification that an action was completed. */
    ActionCompleted(2),
    /** The current element has a notification that an action was aborted. */
    ActionAborted(3),
    /** The current element has a notification not an add, remove, completed, or aborted action. */
    Other(4);

    private final int value;
    private NotificationKind(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "NotificationKind_" + name();
    }

    public static Optional<NotificationKind> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
    
}
