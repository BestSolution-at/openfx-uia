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
 * Contains values that specify the current state of the window for purposes of user interaction.
 */
public enum WindowInteractionState implements INativeEnum {
	/**
	 * The window is running. This does not guarantee that the window is ready for user interaction or is responding.
	 */
	Running(0),
	/**
	 * The window is closing.
	 */
	Closing(1),
	/**
	 * The window is ready for user interaction.
	 */
	ReadyForUserInteraction(2),
	/**
	 * The window is blocked by a modal window.
	 */
	BlockedByModalWindow(3),
	/**
	 * The window is not responding.
	 */
	NotResponding(4);

	private int nativeValue;

	@Override
	public int getNativeValue() {
		return nativeValue;
	}

	@Override
	public String getConstantName() {
		return name();
	}

	private WindowInteractionState(int nativeValue) {
		this.nativeValue = nativeValue;
	}

	public static Optional<WindowInteractionState> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
