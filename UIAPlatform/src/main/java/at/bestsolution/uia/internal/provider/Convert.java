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
package at.bestsolution.uia.internal.provider;

import at.bestsolution.uia.INativeEnum;
import javafx.geometry.Bounds;

public class Convert {

    public static int convertNativeEnum(INativeEnum value) {
        return value.getNativeValue();
    }

    public static float[] convertBounds(Bounds bounds) {
        if (bounds != null) {
            return new float[] {(float)bounds.getMinX(), (float)bounds.getMinY(),
                                (float)bounds.getWidth(), (float)bounds.getHeight()};
        }
        return null;
    }

    public static double[] convertBoundsArrayDouble(Bounds[] bounds) {
        if (bounds != null) {
            double[] result = new double[bounds.length * 4];
            int index = 0;
            for (int i = 0; i < bounds.length; i++) {
                Bounds b = bounds[i];
                result[index++] = b.getMinX();
                result[index++] = b.getMinY();
                result[index++] = b.getWidth();
                result[index++] = b.getHeight();
            }
            return result;
        }
        return null;
    }
}
