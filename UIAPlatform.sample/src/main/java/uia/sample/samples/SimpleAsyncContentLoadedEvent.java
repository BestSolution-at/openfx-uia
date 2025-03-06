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

import at.bestsolution.uia.AsyncContentLoadedState;
import at.bestsolution.uia.IAsyncContentLoadedEvent;
import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.UIA;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class SimpleAsyncContentLoadedEvent implements Sample {

    IAsyncContentLoadedEvent event;

    class UIAAsyncContentLoaded implements IUIAElement {
        Node node;
        public UIAAsyncContentLoaded(Node node) {
            this.node = node;
        }

        @Override
        public void initialize(IInitContext init) {
            event = init.addAsyncContentLoadedEvent();
        }

        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
        }
    }





    Label desc;
    VBox content;

    Node control;

    double p = 0;

    public SimpleAsyncContentLoadedEvent() {
        desc = new Label("Showcase for raising AsyncContentLoadedEvent.");
        desc.setWrapText(true);

        content = new VBox() {
            UIAAsyncContentLoaded uia = new UIAAsyncContentLoaded(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uia;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };

        VBox ev = new VBox();
        ev.setSpacing(20);
        ev.setFillWidth(true);
        ev.setPadding(new Insets(20));

        ChoiceBox<AsyncContentLoadedState> state = new ChoiceBox<>();
        state.setItems(FXCollections.observableArrayList(AsyncContentLoadedState.values()));
        state.setValue(AsyncContentLoadedState.Beginning);

        TextField percent = new TextField("10");

        Button fire = new Button("fire");
        fire.setOnAction(evt -> {
            if (event != null) {
                event.fire(state.getValue(), Double.parseDouble(percent.getText()));
            }
        });

        ev.getChildren().addAll(
            new VBox(new Label("asyncContentLoadedState:"), state),
            new VBox(new Label("percentLoaded: "), percent),
            fire);


        BorderPane auto = new BorderPane();
        auto.setPadding(new Insets(20));

        ProgressIndicator prog = new ProgressIndicator();

        auto.setCenter(prog);

        Button start = new Button("start");
        start.setOnAction(evt -> {
            Thread runner = new Thread(() -> {

                // begin
                p = 0;
                Platform.runLater(() -> prog.setProgress(p));
                Platform.runLater(() -> event.fire(AsyncContentLoadedState.Beginning, p));

                while (p < 0.9) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    p += 0.1;

                    Platform.runLater(() -> prog.setProgress(p));
                    Platform.runLater(() -> event.fire(AsyncContentLoadedState.Progress, p));
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                // complete
                Platform.runLater(() -> prog.setProgress(1));
                Platform.runLater(() -> event.fire(AsyncContentLoadedState.Completed, 1));


                Platform.runLater(() -> start.setDisable(false));
            });

            start.setDisable(true);
            runner.start();
        });
        auto.setBottom(start);

        content.getChildren().add(new Label("fire event manually"));
        content.getChildren().add(ev);
        content.getChildren().add(new Label("fire event with progress bar"));
        content.getChildren().add(auto);
    }

    @Override
    public String getName() {
        return "AsyncContentLoadedEvent";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return content;
    }

    @Override
    public Node getControls() {
        return control;
    }



}
