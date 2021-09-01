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

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cell implements IDrawable {

    public Grid grid;

    public int row;
    public int rowSpan;
    public int col;
    public int colSpan;

    public boolean inRow(int row) {
        return this.row <= row && this.row + this.rowSpan > row;
    }
    public boolean inCol(int col) {
        return this.col <= col && this.col + this.colSpan > col;
    }

    public Bounds computeCellBounds() {
        double x = grid.colWidth * col;
        double y = grid.rowHeight * row;
        double w = grid.colWidth * colSpan;
        double h = grid.rowHeight * rowSpan;
        return new BoundingBox(x, y, w, h);
    }

    @Override
    public void render(GraphicsContext gc) {
        
        gc.setStroke(Color.BLACK);
        
        double x = grid.colWidth * col;
        double y = grid.rowHeight * row;
        double w = grid.colWidth * colSpan;
        double h = grid.rowHeight * rowSpan;

        gc.strokeRect(x, y, w, h);

        gc.setFill(Color.BLACK);
        gc.fillText("" + col + "/" + row, x + 5, y + h / 2);
        
    }
    
}
