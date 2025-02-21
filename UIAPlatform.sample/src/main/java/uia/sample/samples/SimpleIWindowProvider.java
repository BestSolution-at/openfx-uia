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

import at.bestsolution.uia.javafx.uia.IProperty;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IWindowProvider;
import at.bestsolution.uia.javafx.uia.UIA;
import at.bestsolution.uia.javafx.uia.WindowInteractionState;
import at.bestsolution.uia.javafx.uia.WindowVisualState;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class SimpleIWindowProvider implements Sample {

    BorderPane window;
    VBox ctrl;

    private BooleanProperty canMaximize = new SimpleBooleanProperty(true);
    private BooleanProperty canMinimize = new SimpleBooleanProperty(true);
    private BooleanProperty isModal = new SimpleBooleanProperty(false);
    private BooleanProperty isTopmost = new SimpleBooleanProperty(true);

    private ObjectProperty<WindowInteractionState> windowInteractionState = new SimpleObjectProperty<>(WindowInteractionState.Running);
    private ObjectProperty<WindowVisualState> windowVisualState = new SimpleObjectProperty<>(WindowVisualState.Normal);



    class UIAWindow implements IUIAElement, IWindowProvider {

        final Node node;


        <Ctx, T> void simpleListen(Class<Ctx> contextType, Function<Ctx, IProperty<T>> prop, ObservableValue<T> obsValue) {
            obsValue.addListener((obs, oldValue, newValue) -> {
                withContext(contextType, ctx -> {
                    prop.apply(ctx).fireChanged(oldValue, newValue);
                });
            });
        }

        public UIAWindow(Node node) {
            this.node = node;
            simpleListen(WindowProviderContext.class, ctx -> ctx.CanMaximize, canMaximize);
            simpleListen(WindowProviderContext.class, ctx -> ctx.CanMinimize, canMinimize);
            simpleListen(WindowProviderContext.class, ctx -> ctx.IsModal, isModal);
            simpleListen(WindowProviderContext.class, ctx -> ctx.IsTopmost, isTopmost);
            simpleListen(WindowProviderContext.class, ctx -> ctx.WindowInteractionState, windowInteractionState);
            simpleListen(WindowProviderContext.class, ctx -> ctx.WindowVisualState, windowVisualState);
        }



        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            node.requestFocus();
        }

        @Override
        public boolean get_CanMaximize() {
            return canMaximize.get();
        }

        @Override
        public boolean get_CanMinimize() {
            return canMinimize.get();
        }

        @Override
        public boolean get_IsModal() {
            return isModal.get();
        }

        @Override
        public boolean get_IsTopmost() {
            return isTopmost.get();
        }

        @Override
        public WindowInteractionState get_WindowInteractionState() {
            return windowInteractionState.get();
        }

        @Override
        public WindowVisualState get_WindowVisualState() {
            return windowVisualState.get();
        }

        @Override
        public void Close() {
            System.err.println("Window Close from a11y");
        }

        @Override
        public void SetVisualState(WindowVisualState state) {
           windowVisualState.set(state);
        }

        @Override
        public boolean WaitForInputIdle(int milliseconds) {
            System.err.println("WaitForInputIdle " + milliseconds);
            return false;
        }

    }

    Label desc;

    public SimpleIWindowProvider() {
        desc = new Label("Basic usage of IWindowProvider.");
        desc.setWrapText(true);


        window = new BorderPane() {
            UIAWindow uiaEl = new UIAWindow(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };


        StringBinding lbl = Bindings.createStringBinding(() -> {
            return "Window\ncanMaximize: " + canMaximize.get() +
            "\ncanMinimize: " + canMinimize.get() +
            "\nisModal: " + isModal.get() +
            "\nisTopmost: " + isTopmost.get() +
            "\nwindowInteractionState: " + windowInteractionState.get() +
            "\nwindowVisualState: " + windowVisualState.get();
        }, canMaximize, canMaximize, isModal, isTopmost, windowInteractionState, windowVisualState);

        Label lblState = new Label();
        lblState.textProperty().bind(lbl);

        window.setCenter(lblState);



        window.setTop(new Label("Window"));


        ctrl = new VBox();

        {
            CheckBox cb = new CheckBox("CanMaximize");
            cb.selectedProperty().bindBidirectional(canMaximize);
            ctrl.getChildren().add(cb);
        }
        {
            CheckBox cb = new CheckBox("CanMinimize");
            cb.selectedProperty().bindBidirectional(canMinimize);
            ctrl.getChildren().add(cb);
        }
        {
            CheckBox cb = new CheckBox("IsModal");
            cb.selectedProperty().bindBidirectional(isModal);
            ctrl.getChildren().add(cb);
        }
        {
            CheckBox cb = new CheckBox("IsTopmost");
            cb.selectedProperty().bindBidirectional(isTopmost);
            ctrl.getChildren().add(cb);
        }
        {
            ctrl.getChildren().add(new Label("WindowInteractionState"));
            ChoiceBox<WindowInteractionState> cb = new ChoiceBox<>();
            cb.setItems(FXCollections.observableArrayList(WindowInteractionState.values()));
            cb.valueProperty().bindBidirectional(windowInteractionState);
            ctrl.getChildren().add(cb);
        }
        {
            ctrl.getChildren().add(new Label("WindowVisualState"));
            ChoiceBox<WindowVisualState> cb = new ChoiceBox<>();
            cb.setItems(FXCollections.observableArrayList(WindowVisualState.values()));
            cb.valueProperty().bindBidirectional(windowVisualState);
            ctrl.getChildren().add(cb);
        }


    }



    @Override
    public String getName() {
        return "IWindowProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return window;
    }

    @Override
    public Node getControls() {
        return ctrl;
    }



}
