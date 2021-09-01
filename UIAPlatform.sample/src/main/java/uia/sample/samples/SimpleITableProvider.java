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

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.uia.IUIAElement;
import javafx.uia.IUIAVirtualRootElement;
import javafx.uia.UIA;
import uia.sample.Sample;
import uia.sample.samples.model.UIATable;
import uia.sample.samples.model.UIATableCell;
import uia.sample.samples.model.UIATableHeaderCell;

public class SimpleITableProvider implements Sample {

    class UIACanvas implements /*IUIAElement,*/ IUIAVirtualRootElement {

        List<IUIAElement> children = new ArrayList<>();

        @Override
        public List<IUIAElement> getChildren() {
            return children;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }

        @Override
        public Bounds getBounds() {
            return canvas.localToScreen(canvas.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
        }
        
    }
    

    Node sample;

    Label desc;


    Canvas canvas;

    public SimpleITableProvider() {
        desc = new Label("Basic usage of ITableProvider & ITableItemProvider. Those require IGridProvider and IGridItemProvider respectivly");
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

        
        UIATable grid = new UIATable();
        grid.colCount = 3;
        grid.rowCount = 5;
        grid.colWidth = 100;
        grid.rowHeight = 50;
        grid.canvas = canvas;

        for (int c = 0; c < grid.colCount; c++) {
            for (int r = 0; r < grid.rowCount; r++) {
                if (c == 1 && r == 3) continue;
                if (c == 2 && r == 4) continue;
                UIATableCell cell;

                if (r == 0 && c == 0) {
                    continue;
                }
                else if (r == 0) {
                    UIATableHeaderCell colHeader = new UIATableHeaderCell();
                    colHeader.isColumnHeader = true;
                    cell = colHeader;
                } else if (c == 0) {
                    UIATableHeaderCell rowHeader = new UIATableHeaderCell();
                    rowHeader.isRowHeader = true;
                    cell = rowHeader;
                } else {
                    cell  = new UIATableCell();
                }
               
                cell.grid = grid;
                cell.row = r;
                cell.col = c;
                cell.rowSpan = (c == 1 && r == 2) ? 2 : 1;
                cell.colSpan = (c == 1 && r == 4) ? 2 : 1;
                grid.cells.add(cell);
            }
        }
        
        root.children.add(grid);
        grid.parent = root;


        grid.render(canvas.getGraphicsContext2D());

        sample = canvas;
    }

  

    @Override
    public String getName() {
        return "ITableProvider & ITableItemProvider";
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
