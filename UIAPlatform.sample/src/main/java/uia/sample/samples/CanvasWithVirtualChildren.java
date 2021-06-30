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
import java.util.Collections;
import java.util.List;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.uia.ControlType;
import javafx.uia.IUIAElement;
import javafx.uia.UIA;
import uia.sample.Sample;

public class CanvasWithVirtualChildren implements Sample {

    private MyCanvas sample;

    private Node description;

    private class MyCanvas extends Canvas {

        class UIACanvas implements IUIAElement {
            private List<IUIAElement> children = new ArrayList<>();

            @Override
            public IUIAElement getParent() {
                return null;
            }
            @Override
            public List<IUIAElement> getChildren() {
                return children;
            }
            @Override
            public ControlType getControlType() {
                return ControlType.UIA_CustomControlTypeId;
            }
            @Override
            public Bounds getBounds() {
                return MyCanvas.this.localToScreen(MyCanvas.this.getBoundsInLocal());
            }
            @Override
            public void SetFocus() {
            }
            @Override
            public void initialize(IUIAElementEvents events) {
            }
        }

        UIACanvas uia = new UIACanvas();

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    private class MyRectangle implements IUIAElement {

        Paint color;
        double x;
        double y;
        double w;
        double h;
        
        public MyRectangle(Paint color, double x, double y, double w, double h) {
            this.color = color;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void render(GraphicsContext ctx) {
            ctx.setFill(color);
            ctx.fillRect(x, y, w, h);
        }

        @Override
        public IUIAElement getParent() {
            return sample.uia;
        }
        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }
        @Override
        public ControlType getControlType() {
            return ControlType.UIA_PaneControlTypeId;
        }
        @Override
        public Bounds getBounds() {
            return sample.localToScreen(new BoundingBox(x, y, w, h));
        }
        @Override
        public void SetFocus() {
        }
        @Override
        public void initialize(IUIAElementEvents events) {
            
        }
    }

    public CanvasWithVirtualChildren() {

        Label desc = new Label("A canvas with with basic virtual UIA Elements as children");
        desc.setWrapText(true);
        description = desc;

        sample = new MyCanvas();
        sample.setWidth(300);
        sample.setHeight(50);

        List<MyRectangle> rects = new ArrayList<>();
        rects.add(new MyRectangle(Color.RED, 10, 10, 50, 20));
        rects.add(new MyRectangle(Color.GREEN, 70, 10, 50, 20));
        rects.add(new MyRectangle(Color.BLUE, 130, 10, 50, 20));


        sample.uia.children.addAll(rects);


        GraphicsContext ctx = sample.getGraphicsContext2D();
        rects.forEach(rect -> rect.render(ctx));
    }

    @Override
    public String getName() {
        return "Canvas with virtual Children";
    }

    @Override
    public Node getDescription() {
        return description;
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
