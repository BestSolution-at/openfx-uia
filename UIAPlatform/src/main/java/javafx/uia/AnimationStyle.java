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

/** Contains values for the AnimationStyle text attribute. */
public enum AnimationStyle implements INativeEnum {
    /** None. */
    None(0),
    /** The bounding rectangle displays a border of alternating icons of different colors. */
    LasVegasLights(1),
    /** The font and background alternate between assigned colors and contrasting colors. */
    BlinkingBackground(2),
    /** The background displays flashing, multicolored icons. */
    SparkleText(3),
    /** The bounding rectangle displays moving black dashes. */
    MarchingBlackAnts(4),
    /** The bounding rectangle displays moving red dashes. */
    MarchingRedAnts(5),
    /** The font alternates between solid and blurred. */
    Shimmer(6),
    /** Other. */
    Other(-1);
    
    private final int value;
    private AnimationStyle(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "AnimationStyle_" + name();
    }
    
    public static Optional<AnimationStyle> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
