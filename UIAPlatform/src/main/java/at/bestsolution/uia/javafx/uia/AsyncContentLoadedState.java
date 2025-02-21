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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * Contains values that describe the progress of asynchronous loading of content.
 */
public enum AsyncContentLoadedState implements INativeEnum {
    /** Loading of the content into the UI Automation element is beginning. */
    Beginning(0),
    /** Loading of the content into the UI Automation element is in progress. */
    Progress(1),
    /** Loading of the content into the UI Automation element is complete. */
    Completed(2);

    private final int value;
    private AsyncContentLoadedState(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "AsyncContentLoadedState_" + name();
    }

    public static Optional<AsyncContentLoadedState> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
