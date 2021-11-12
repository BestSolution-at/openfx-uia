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

import javafx.uia.ControlType;
import javafx.uia.IInitContext;
import javafx.uia.ITableProvider;
import javafx.uia.IUIAElement;
import javafx.uia.RowOrColumnMajor;

public class UIATable extends UIAGrid implements ITableProvider {

    @Override
    public RowOrColumnMajor get_RowOrColumnMajor() {
        return RowOrColumnMajor.RowMajor;
    }

    @Override
    public IUIAElement[] GetColumnHeaders() {
        return cells.stream()
            .filter(cell -> cell instanceof UIATableHeaderCell)
            .map(cell -> (UIATableHeaderCell) cell)
            .filter(cell -> cell.isColumnHeader)
            .toArray(size -> new IUIAElement[size]);
    }

    @Override
    public IUIAElement[] GetRowHeaders() {
        return cells.stream()
            .filter(cell -> cell instanceof UIATableHeaderCell)
            .map(cell -> (UIATableHeaderCell) cell)
            .filter(cell -> cell.isRowHeader)
            .toArray(size -> new IUIAElement[size]);
    }


    @Override
    public ControlType getControlType() {
        return ControlType.UIA_TableControlTypeId;
    }

    @Override
    public void initialize(IInitContext init) {
        super.initialize(init);

        init.addIsControlElementProperty(() -> true);
        init.addIsContentElementProperty(() -> true);
    }
}