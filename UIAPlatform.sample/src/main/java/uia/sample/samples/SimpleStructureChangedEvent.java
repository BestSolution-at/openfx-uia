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

import java.util.Arrays;

import at.bestsolution.uia.javafx.uia.IInitContext;
import at.bestsolution.uia.javafx.uia.IStructureChangedEvent;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.StructureChangeType;
import at.bestsolution.uia.javafx.uia.UIA;
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

public class SimpleStructureChangedEvent implements Sample {

    IStructureChangedEvent event;

    class UIAStructureChanged implements IUIAElement {
        Node node;
        public UIAStructureChanged(Node node) {
            this.node = node;
        }

        @Override
        public void initialize(IInitContext init) {
            event = init.addStructureChangedEvent();
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

    public SimpleStructureChangedEvent() {
        desc = new Label("Showcase for raising StructureChangedEvent.");
        desc.setWrapText(true);

        content = new VBox() {
            UIAStructureChanged uia = new UIAStructureChanged(this);
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

        ChoiceBox<StructureChangeType> structureChangeType = new ChoiceBox<>();
        structureChangeType.setItems(FXCollections.observableArrayList(StructureChangeType.values()));
        structureChangeType.setValue(StructureChangeType.ChildAdded);

        TextField runtimeId = new TextField("");

        Button fire = new Button("fire");
        fire.setOnAction(evt -> {
            if (event != null) {
                String text = runtimeId.getText();
                int[] rId = null;
                // runtimeId may only be set on ChildRemoved (see ms docs)
                if (structureChangeType.getValue() == StructureChangeType.ChildRemoved) {
                    if (text != null && !text.isEmpty()) {
                        rId = Arrays.stream(text.split("[,]")).mapToInt(Integer::parseInt).toArray();
                    }
                }
                event.fire(structureChangeType.getValue(), rId);
            }
        });

        ev.getChildren().addAll(
            new VBox(new Label("structureChangeType:"), structureChangeType),
            new VBox(new Label("runtimeId (comma separated int)"), runtimeId),
            fire);

        content.getChildren().add(ev);
    }

    @Override
    public String getName() {
        return "StructureChangedEvent";
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
