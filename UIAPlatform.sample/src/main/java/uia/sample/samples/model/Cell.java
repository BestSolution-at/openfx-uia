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
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import uia.sample.samples.model.Text.TextFragment;
import uia.sample.samples.model.TextSupport.Glyph;

public class Cell extends BaseModel {

    public Grid grid;

    public int row;
    public int rowSpan;
    public int col;
    public int colSpan;

    private String content;

    private TextSupport textSupport;

    public boolean inRow(int row) {
        return this.row <= row && this.row + this.rowSpan > row;
    }
    public boolean inCol(int col) {
        return this.col <= col && this.col + this.colSpan > col;
    }

    public void setContent(String content) {
        this.content = content;// + "\u0007";
        textSupport = new TextSupport();
        /*
        textHelper = new Text() {
            @Override
            double computeParentLayoutX() {
                return Cell.this.computeParentLayoutX();
            }
            @Override
            double computeParentLayoutY() {
                return Cell.this.computeParentLayoutY();
            }
            @Override
            public int getBegin() {
                return Cell.this.getBegin();
            }
            @Override
            public int getEnd() {
                return Cell.this.getEnd();
            }
        };
        textHelper.addText(content/* + "\u0007"*//*, Font.getDefault(), Color.BLACK);
        */

    }

    @Override
    public int computeIndices(int curBegin) {
        begin = curBegin;
        int length = content.length();
        //int length = textHelper.computeIndices(curBegin);
        end = begin + length;
        return length;
    }

    private List<Glyph> localGlyphs = new ArrayList<>();

    @Override
    public int computeGlyphs(int beginIndex) {
        begin = beginIndex;
        int length = content.length();
        //int length = textHelper.computeIndices(curBegin);

        int idx = beginIndex;
        if (textSupport != null) {
            for (Glyph tg : textSupport.getGlyphList()) {
                Glyph g = new Glyph();
                g.x = tg.x;
                g.y = tg.y;
                g.w = tg.w;
                g.h = tg.h;
                g.render = tg.render;
                g.index = idx;
                localGlyphs.add(g);

                idx ++;
            }
        }

        end = begin + length;
        return length;
    }

    @Override
    public Stream<Glyph> streamGlyphs() {
        return localGlyphs.stream();
    }

    @Override
    public List<Bounds> getTextBounds(int begin, int end) {
        return textSupport.getTextBounds(begin, end);
        //return textHelper.getTextBounds(begin, end);
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void layout() {
        layoutX = grid.colWidth * col;
        layoutY = grid.rowHeight * row;
        layoutW = grid.colWidth * colSpan;
        layoutH = grid.rowHeight * rowSpan;

        double textX = computeParentLayoutX() + layoutX + 5;
        double textY = computeParentLayoutY() + layoutY + 15;


        if (textSupport != null) {
            textSupport.layout(getBegin(), textX, textY, layoutW - 10, Collections.singletonList(new TextFragment(content, Font.getDefault(), Color.BLACK)));
        }
        /*
        if (textHelper != null) {
            textHelper.layoutX = layoutX + 5;
            textHelper.layoutY = layoutY + 15;
            textHelper.layoutW = layoutW - 10;
            textHelper.layout();
        }
        */
    }

    public Bounds computeCellBounds() {
        double x = grid.layoutX + grid.colWidth * col;
        double y = grid.layoutY + grid.rowHeight * row;
        double w = grid.colWidth * colSpan;
        double h = grid.rowHeight * rowSpan;
        return new BoundingBox(x, y, w, h);
    }

    //@Override
    public Bounds getRenderBounds() {
        double w = grid.colWidth * colSpan;
        double h = grid.rowHeight * rowSpan;
        return new BoundingBox(layoutX, layoutY, w, h);
    }

    //@Override
    public void layout(double x, double y) {
        layoutX = x;
        layoutY = y;
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setStroke(Color.BLACK);
        
        Bounds lb = getLayoutBounds();
        double x = lb.getMinX();
        double y = lb.getMinY();
        double w = lb.getWidth();
        double h = lb.getHeight();

        gc.strokeRect(x, y, w, h);

        gc.setFont(Font.font(10d));
        gc.setFill(Color.BLACK);
        gc.fillText("" + col + "/" + row, x + 5, y + 12);
        
        if (textSupport != null) {
            textSupport.render(gc);
        }
/*
        if (textHelper != null) {
           
            textHelper.render(gc);
        }
*/

        // if (content != null) {
        //     gc.setFont(Font.font(14));
        //     gc.fillText(content, x + 5, y + h / 2 + 8);
        // }
    }
    

    @Override
    public int pickText(double x, double y) {
        return textSupport.pickText(x, y);
    }
}
