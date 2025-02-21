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

import java.util.List;
import java.util.stream.Collectors;

import at.bestsolution.uia.javafx.uia.IGridProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualElement;
import javafx.geometry.Bounds;

public class UIAGrid extends Grid implements /*IUIAElement,*/ IUIAVirtualElement, IGridProvider {

    @Override
    public IUIAElement getParent() {
        return parents().filter(p -> p instanceof IUIAElement).map(p -> (IUIAElement) p).findFirst().orElse(null);
    }

    @Override
    public List<IUIAElement> getChildren() {
        return cells.stream().map(cell -> (UIACell) cell).collect(Collectors.toList());
    }

    @Override
    public Bounds getBounds() {
        return UIACanvas.getCanvas(this).localToScreen(getLayoutBounds());
    }

    @Override
    public void SetFocus() {
    }

    @Override
    public int get_ColumnCount() {
        return colCount;
    }

    @Override
    public int get_RowCount() {
        return rowCount;
    }

    @Override
    public IUIAElement GetItem(int row, int column) {
        return cells.stream()
            .filter(cell -> cell.inRow(row) && cell.inCol(column))
            .map(cell -> (UIACell) cell)
            .findFirst()
            .orElse(null);
    }

}
