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

import java.util.Collections;
import java.util.List;

import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.UIA;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import uia.sample.Sample;

public class PickTest implements Sample {

    class PickUIAElement implements IUIAVirtualRootElement {
        PickArea pane;



        public PickUIAElement(PickArea pane) {
            this.pane = pane;
        }

        @Override
        public Bounds getBounds() {
            return pane.localToScreen(pane.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            System.out.println("PICK AT " + point);


            //Point2D test = new Point2D(point.getX() / 1.25, point.getY() / 1.25);
            Point2D localPoint = pane.screenToLocal(point);
            pane.updateMarker(localPoint);
            return this;
        }



    }

    class PickArea extends Pane {
        IUIAElement uia;
        Rectangle marker;
        public PickArea() {
            this.marker = new Rectangle();
            marker.setWidth(10);
            marker.setHeight(10);
            marker.setFill(Color.RED);

            this.getChildren().add(marker);
            this.uia = new PickUIAElement(this);

            setStyle("-fx-border-color: red; -fx-border-width: 1px; -fx-border-style: solid;");
        }

        void updateMarker(Point2D pos) {
            this.marker.setX(pos.getX());
            this.marker.setY(pos.getY());
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    PickArea content;

    public PickTest() {
        content = new PickArea();
    }

    @Override
    public String getName() {
        return "Simple UIA Element";
    }

    @Override
    public Node getDescription() {
        return null;
    }

    @Override
    public Node getSample() {
        return content;
    }

    @Override
    public Node getControls() {
        return null;
    }



}
