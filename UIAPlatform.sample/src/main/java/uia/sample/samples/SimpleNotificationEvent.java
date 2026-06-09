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

import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.INotificationEvent;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.NotificationKind;
import at.bestsolution.uia.NotificationProcessing;
import at.bestsolution.uia.UIA;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class SimpleNotificationEvent implements Sample {

    INotificationEvent notificationEvent;

    class UIANotify implements IUIAElement {
        Node node;
        public UIANotify(Node node) {
            this.node = node;
        }

        @Override
        public void initialize(IInitContext init) {
            notificationEvent = init.addNotificationEvent();
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

    public SimpleNotificationEvent() {
        desc = new Label("Showcase for raising NotificationEvent.");
        desc.setWrapText(true);

        content = new VBox() {
            UIANotify uia = new UIANotify(this);
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

        ChoiceBox<NotificationKind> kind = new ChoiceBox<>();
        kind.setItems(FXCollections.observableArrayList(NotificationKind.values()));
        kind.setValue(NotificationKind.ActionAborted);

        ChoiceBox<NotificationProcessing> processing = new ChoiceBox<>();
        processing.setItems(FXCollections.observableArrayList(NotificationProcessing.values()));
        processing.setValue(NotificationProcessing.All);

        TextField displayString = new TextField("Hello");
        TextField activityId = new TextField("myActivity");

        Button fire = new Button("fire");
        fire.setOnAction(evt -> {
            if (notificationEvent != null) {
                notificationEvent.fire(kind.getValue(), processing.getValue(), displayString.getText(), activityId.getText());
            }
        });

        ev.getChildren().addAll(
            new VBox(new Label("notificationKind:"), kind),
            new VBox(new Label("notificationProcessing: "), processing),
            new VBox(new Label("displayStirng"), displayString),
            new VBox(new Label("activityId"), activityId),
            fire);

        content.getChildren().add(ev);
    }

    @Override
    public String getName() {
        return "NotificationEvent";
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
