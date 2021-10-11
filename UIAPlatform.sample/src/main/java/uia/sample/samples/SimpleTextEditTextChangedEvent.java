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
import javafx.uia.IInitContext;
import javafx.uia.ITextEditTextChangedEvent;
import javafx.uia.IUIAElement;
import javafx.uia.TextEditChangeType;
import javafx.uia.UIA;
import uia.sample.Sample;

public class SimpleTextEditTextChangedEvent implements Sample {

    ITextEditTextChangedEvent event;

    class UIANotify implements IUIAElement {
        Node node;
        public UIANotify(Node node) {
            this.node = node;
        }

        @Override
        public void initialize(IInitContext init) {
            event = init.addTextEditTextChangedEvent();
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

    public SimpleTextEditTextChangedEvent() {
        desc = new Label("Showcase for raising TextEditTextChanged.");
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

        ChoiceBox<TextEditChangeType> changeType = new ChoiceBox<>();
        changeType.setItems(FXCollections.observableArrayList(TextEditChangeType.values()));
        changeType.setValue(TextEditChangeType.None);

        TextField changedData = new TextField("Hello,World");

        Button fire = new Button("fire");
        fire.setOnAction(evt -> {
            if (event != null) {
                event.fire(changeType.getValue(), changedData.getText().split("[,]"));
            }
        });

        ev.getChildren().addAll(
            new VBox(new Label("TextEditChangeType:"), changeType),
            new VBox(new Label("changedData (comma separated): "), changedData),
            fire);

        content.getChildren().add(ev);
    }

    @Override
    public String getName() {
        return "TextEditTextChangedEvent";
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
