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

/** Contains values that specify the orientation of a control. */
public enum OrientationType implements INativeEnum {
	/** The control has no orientation. */
	None(0), 
	/** The control has horizontal orientation. */
	Horizontal(1), 
	/** The control has vertical orientation. */
	Vertical(2);
	
	private int nativeValue;
	
	@Override
	public int getNativeValue() {
		return nativeValue;
	}

	@Override
	public String getConstantName() {
		return "OrientationType_" + name();
	}
	
	private OrientationType(int nativeValue) {
		this.nativeValue = nativeValue;
	}
	
	public static Optional<OrientationType> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
