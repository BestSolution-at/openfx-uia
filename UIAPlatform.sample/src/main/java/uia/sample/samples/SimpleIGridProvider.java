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
package uia.sample.samples;

import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.uia.UIA;
import uia.sample.Sample;
import uia.sample.samples.model.UIACanvas;
import uia.sample.samples.model.UIACell;
import uia.sample.samples.model.UIAGrid;

public class SimpleIGridProvider implements Sample {


    Node sample;

    Label desc;


    Canvas canvas;

    public SimpleIGridProvider() {
        desc = new Label("Basic usage of IGridProvider & IGridItemProvider");
        desc.setWrapText(true);
        
        UIACanvas root = new UIACanvas();

        canvas = new Canvas(500, 500) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return root;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };

        
        UIAGrid grid = new UIAGrid();
        grid.colCount = 3;
        grid.rowCount = 5;
        grid.colWidth = 100;
        grid.rowHeight = 50;
        grid.canvas = canvas;

        for (int c = 0; c < grid.colCount; c++) {
            for (int r = 0; r < grid.rowCount; r++) {
                if (c == 1 && r == 3) continue;
                if (c == 2 && r == 4) continue;
                UIACell cell = new UIACell();
                cell.grid = grid;
                cell.row = r;
                cell.col = c;
                cell.rowSpan = (c == 1 && r == 2) ? 2 : 1;
                cell.colSpan = (c == 1 && r == 4) ? 2 : 1;
                grid.addCell(cell);
            }
        }
        
        root.addChild(grid);

        root.layout();

        // grid.layout(0, 0);
        grid.render(canvas.getGraphicsContext2D());

        sample = canvas;
    }

  

    @Override
    public String getName() {
        return "IGridProvider & IGridItemProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return sample;
    }

    @Override
    public Node getControls() {
        return null;
    }
    


}
