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

import java.util.Collections;
import java.util.List;

import at.bestsolution.uia.javafx.uia.IGridItemProvider;
import at.bestsolution.uia.javafx.uia.IInitContext;
import at.bestsolution.uia.javafx.uia.ITextChildProvider;
import at.bestsolution.uia.javafx.uia.ITextRangeProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualElement;
import javafx.geometry.Bounds;

public class UIACell extends Cell implements /*IUIAElement,*/ IUIAVirtualElement, IGridItemProvider, ITextChildProvider {

    @Override
    public void initialize(IInitContext init) {
        init.addNameProperty(this::getName);
    }

    String getName() {
        return getContent();
    }

    @Override
    public int get_Column() {
        return col;
    }

    @Override
    public int get_ColumnSpan() {
        return colSpan;
    }

    @Override
    public int get_Row() {
        return row;
    }

    @Override
    public int get_RowSpan() {
        return rowSpan;
    }

    @Override
    public IUIAElement get_ContainingGrid() {
        return (UIAGrid) grid;
    }

    @Override
    public IUIAElement getParent() {
        return (UIAGrid) grid;
    }

    @Override
    public List<IUIAElement> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Bounds getBounds() {
        return UIACanvas.getCanvas(this).localToScreen(getLayoutBounds());
    }

    @Override
    public void SetFocus() {
    }


    @Override
    public IUIAElement get_TextContainer() {
        return getDocument();
    }

    @Override
    public ITextRangeProvider get_TextRange() {
        return new UIATextRange(getDocument(), getBegin(), getEnd());
    }

    private UIADocument getDocument() {
        return parents().filter(p -> p instanceof UIADocument)
        .map(p -> (UIADocument) p)
        .findFirst()
        .orElse(null);
    }

}
