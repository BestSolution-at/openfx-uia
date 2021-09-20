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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.uia.ITransformProvider;
import javafx.uia.IUIAElement;
import javafx.uia.UIA;
import uia.sample.Sample;

public class SimpleITransformProvider implements Sample {

    private BooleanProperty canMove = new SimpleBooleanProperty(true);
    private BooleanProperty canResize = new SimpleBooleanProperty(true);
    private BooleanProperty canRotate = new SimpleBooleanProperty(true);
    
    class UIATransform implements IUIAElement, ITransformProvider {

        final Pane node;

        public UIATransform(Pane node) {
            this.node = node;
            canMove.addListener((obs, oldValue, newValue) -> {
                withContext(TransformProviderContext.class, ctx -> {
                    ctx.CanMove.fireChanged(oldValue, newValue);
                });
            });
            canResize.addListener((obs, oldValue, newValue) -> {
                withContext(TransformProviderContext.class, ctx -> {
                    ctx.CanResize.fireChanged(oldValue, newValue);
                });
            });
            canRotate.addListener((obs, oldValue, newValue) -> {
                withContext(TransformProviderContext.class, ctx -> {
                    ctx.CanRotate.fireChanged(oldValue, newValue);
                });
            });
        }

        @Override
        public boolean get_CanRotate() {
            return canRotate.get();
        }

        @Override
        public boolean get_CanResize() {
            return canResize.get();
        }

        @Override
        public boolean get_CanMove() {
            return canMove.get();
        }

        @Override
        public void Move(double x, double y) {
            if (!canMove.get()) {
                return;
            }
            if (node.getParent() != null) {
                Point2D move = node.getParent().screenToLocal(x, y);
                node.relocate(move.getX(), move.getY());
            }
            
        }

        @Override
        public void Resize(double width, double height) {
            if (!canResize.get()) {
                return;
            }
            node.resize(width, height);
        }

        @Override
        public void Rotate(double degrees) {
            if (!canRotate.get()) {
                return;
            }
            node.setRotate(degrees);
        }

        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            node.requestFocus();
        }

    }

    Label desc;

    Pane host;

    VBox ctrl;

    public SimpleITransformProvider() {
        desc = new Label("Basic usage of ITransformProvider.");
        desc.setWrapText(true);
        
        host = new Pane();

        BorderPane pane0 = new BorderPane() {
            UIATransform uiaEl = new UIATransform(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        pane0.setStyle("-fx-border-width: 1px; -fx-border-color: red;");

       
        
        StringBinding bLbl = Bindings.createStringBinding(() -> {
            return "canMove: " + canMove.get() + "\ncanResize: " + canResize.get() + "\ncanRotate: " + canRotate.get();
        }, canMove, canResize, canRotate);
        
        Label lblState = new Label();
        lblState.textProperty().bind(bLbl);
        
        pane0.setCenter(lblState);
        pane0.setManaged(false);

        // init
        pane0.resizeRelocate(20, 20, 100, 100);

        host.getChildren().add(pane0);


        ctrl = new VBox();

        CheckBox move = new CheckBox("CanMove");
        move.selectedProperty().bindBidirectional(canMove);
        ctrl.getChildren().add(move);

        CheckBox resize = new CheckBox("CanResize");
        resize.selectedProperty().bindBidirectional(canResize);
        ctrl.getChildren().add(resize);

        CheckBox rotate = new CheckBox("CanRotate");
        rotate.selectedProperty().bindBidirectional(canRotate);
        ctrl.getChildren().add(rotate);

        
    }

  

    @Override
    public String getName() {
        return "ITransformProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return host;
    }

    @Override
    public Node getControls() {
        return ctrl;
    }
    


}
