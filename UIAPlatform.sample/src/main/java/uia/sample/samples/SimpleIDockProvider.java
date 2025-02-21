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

import java.util.function.Function;

import at.bestsolution.uia.javafx.uia.DockPosition;
import at.bestsolution.uia.javafx.uia.IDockProvider;
import at.bestsolution.uia.javafx.uia.IProperty;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.UIA;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class SimpleIDockProvider implements Sample {

    private ObjectProperty<DockPosition> dockPosition = new SimpleObjectProperty<>(DockPosition.None);

    class UIADock implements IUIAElement, IDockProvider {

        final Pane node;

        public UIADock(Pane node) {
            this.node = node;
            simpleListen(DockProviderContext.class, ctx -> ctx.DockPosition, dockPosition);

        }

        <Ctx, T> void simpleListen(Class<Ctx> contextType, Function<Ctx, IProperty<T>> prop, ObservableValue<T> obsValue) {
            obsValue.addListener((obs, oldValue, newValue) -> {
                withContext(contextType, ctx -> {
                    prop.apply(ctx).fireChanged(oldValue, newValue);
                });
            });
        }

        @Override
        public void SetDockPosition(DockPosition pos) {
            dockPosition.set(pos);
        }

        @Override
        public DockPosition get_DockPosition() {
            return dockPosition.get();
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
    BorderPane pane0;

    VBox ctrl;


    public SimpleIDockProvider() {
        desc = new Label("Basic usage of IDockProvider.");
        desc.setWrapText(true);

        host = new Pane() {
            @Override
            protected void layoutChildren() {
                doLayout();
            }
        };

        dockPosition.addListener(inv -> host.requestLayout());

        pane0 = new BorderPane() {
            UIADock uiaEl = new UIADock(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        pane0.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
        pane0.setManaged(false);


        StringBinding bLbl = Bindings.createStringBinding(() -> {
            return "dockPosition: " + dockPosition.get();
        }, dockPosition);

        Label lblState = new Label();
        lblState.textProperty().bind(bLbl);

        pane0.setCenter(lblState);


        // init
        pane0.resizeRelocate(20, 20, 100, 100);

        host.getChildren().add(pane0);


        ctrl = new VBox();

        {
            ctrl.getChildren().add(new Label("DockPosition"));
            ChoiceBox<DockPosition> cb = new ChoiceBox<>();
            cb.setItems(FXCollections.observableArrayList(DockPosition.values()));
            cb.valueProperty().bindBidirectional(dockPosition);
            ctrl.getChildren().add(cb);
        }
    }

    private void doLayout() {

        double hw = host.getWidth();
        double hh = host.getHeight();

        double w = pane0.prefWidth(-1);
        double h = pane0.prefHeight(-1);

        switch(dockPosition.get()) {
            case None:
                pane0.resizeRelocate(50, 50, w, h);
                break;
            case Top:
                pane0.resizeRelocate(0, 0, hw, h);
                break;
            case Bottom:
                pane0.resizeRelocate(0, hh - h, hw, h);
                break;
            case Left:
                pane0.resizeRelocate(0, 0, w, hh);
                break;
            case Right:
                pane0.resizeRelocate(hw - w, 0, w, hh);
                break;
            case Fill:
                pane0.resizeRelocate(0, 0, hw, hh);
                break;
        }
    }

    @Override
    public String getName() {
        return "IDockProvider";
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
