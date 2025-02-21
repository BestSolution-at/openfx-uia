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
package uia.sample.samples.model;

import at.bestsolution.uia.javafx.uia.ControlType;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class UIATableHeaderCell extends UIATableCell {

    public boolean isColumnHeader;
    public boolean isRowHeader;

    @Override
    public void render(GraphicsContext gc) {

        gc.setFill(Color.LIGHTGREY);

        Bounds lb = getLayoutBounds();
        gc.fillRect(lb.getMinX(), lb.getMinY(), lb.getWidth(), lb.getHeight());

        super.render(gc);
    }

    @Override
    public ControlType getControlType() {
        return ControlType.UIA_HeaderItemControlTypeId;
    }

    @Override
    public IUIAElement[] GetColumnHeaderItems() {
        return new IUIAElement[0];
    }

    @Override
    public IUIAElement[] GetRowHeaderItems() {
        return new IUIAElement[0];
    }
}
