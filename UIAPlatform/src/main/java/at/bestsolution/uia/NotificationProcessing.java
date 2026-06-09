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
package at.bestsolution.uia;

import java.util.Optional;
import java.util.stream.Stream;

/** Defines values that indicate how a notification should be processed. */
public enum NotificationProcessing implements INativeEnum {
    /** These notifications should be presented to the user as soon as possible and all of the notifications from this source should be delivered to the user.
        <p>Warning  Use this in a limited capacity as this style of message could cause a flooding of information to the user due to the nature of the request to deliver all notifications.</p> */
    ImportantAll(0),
    /** These notifications
should be presented to the user as soon as possible. The most recent notification from this source should be delivered to the user because it supersedes all of the other notifications. */
    ImportantMostRecent(1),
    /** These notifications
should be presented to the user when possible.
All of the notifications from this source should be delivered to the user. */
    All(2),
    /** These notifications
should be presented to the user when possible. The most recent notification from this source should be delivered to the user because it supersedes all of the other notifications. */
    MostRecent(3),
    /** These notifications
should be presented to the user when possible.
Don’t interrupt the current notification for this one.
If new notifications come in from the same source while the current notification is being presented, keep the most recent and ignore the rest until the current processing is completed. Then, use the most recent message as the current message. */
    CurrentThenMostRecent(4);

    private final int value;
    private NotificationProcessing(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "NotificationProcessing_" + name();
    }

    public static Optional<NotificationProcessing> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}

}
