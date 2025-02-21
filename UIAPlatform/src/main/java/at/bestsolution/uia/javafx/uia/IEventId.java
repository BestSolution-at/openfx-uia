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
package at.bestsolution.uia.javafx.uia;

import java.util.stream.Stream;

/**
 * Represents an UIA Event Id
 */
public interface IEventId {

    /**
     * native value
     * @return
     *   the native value of the event id
     */
    int getNativeValue();

    /**
     * a lookup in standard event ids
     * @param nativeValue the native value
     * @return the standard event id or a simple wrapped native value
     */
    public static IEventId fromNativeValue(int nativeValue) {
        return Stream.of(StandardEventIds.values())
        .filter(value -> value.getNativeValue() == nativeValue)
        .map(v -> (IEventId)v).findFirst()
        .orElse(new IEventId() {
            @Override
            public int getNativeValue() {
                return nativeValue;
            }
        });
	}

}
