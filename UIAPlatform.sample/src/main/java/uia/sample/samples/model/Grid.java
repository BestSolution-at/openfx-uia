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

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class Grid extends BaseModel {

    public Canvas canvas;

    public double colWidth;
    public double rowHeight;

    public int colCount;
    public int rowCount;

    //public double layoutX = 0;
    //public double layoutY = 0;

    protected List<Cell> cells = new ArrayList<>();

    public void addCell(Cell cell) {
        cells.add(cell);
        addChild(cell);
    }

    public Bounds computeGridBounds() {
        return new BoundingBox(layoutX, layoutY, colCount * colWidth, rowCount * rowHeight);
    }

    @Override
    public void layout() {
        layoutW = colCount * colWidth;
        layoutH = rowCount * rowHeight;
        

        getModelChildren().forEach(IModel::layout);
    }

    //@Override
    public Bounds getRenderBounds() {
        return computeGridBounds();
    }

    //@Override
    public void layout(double x, double y) {
        layoutX = x;
        layoutY = y;
        cells.forEach(cell -> cell.layout(x, y));
    }

    @Override
    public void render(GraphicsContext gc) {
        cells.forEach(cell -> cell.render(gc));
    }
    
}
