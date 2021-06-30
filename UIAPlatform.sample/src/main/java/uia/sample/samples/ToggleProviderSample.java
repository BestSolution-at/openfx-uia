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

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.uia.ControlType;
import javafx.uia.IToggleProvider;
import javafx.uia.IUIAElement;
import javafx.uia.ToggleState;
import javafx.uia.UIA;
import uia.sample.Sample;

public class ToggleProviderSample implements Sample {

    class MyLabel extends Label {

        BooleanProperty toggleState = new SimpleBooleanProperty();

        class MyElement implements IUIAElement, IToggleProvider {

            private IToggleProviderEvents events;

            MyElement() {
                MyLabel.this.toggleState.addListener((obs, ol, ne) -> {
                    if (events != null) {
                        events.notifyToggleStateChanged(ol ? ToggleState.On : ToggleState.Off, ne ? ToggleState.On : ToggleState.Off);
                    }
                });
            }

            @Override
            public IUIAElement getParent() {
                return null;
            }

            @Override
            public List<IUIAElement> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public ControlType getControlType() {
                return ControlType.UIA_TextControlTypeId;
            }

            @Override
            public Bounds getBounds() {
                return MyLabel.this.localToScreen(MyLabel.this.getBoundsInLocal());
            }

            @Override
            public void SetFocus() {
            }

            @Override
            public void initialize(IUIAElementEvents events) {
            }



            @Override
            public void Toggle() {
                toggleState.set(!toggleState.get());
            }

            @Override
            public ToggleState get_ToggleState() {
                return toggleState.get() ? ToggleState.On : ToggleState.Off;
            }

            @Override
            public void initialize(IToggleProviderEvents events) {
                this.events = events;
            }
            
        };

        MyElement uia = new MyElement();

        MyLabel(String text) {
            super(text);
            textProperty().bind(Bindings.createStringBinding(() -> {
                return text + " (toggleState = " + toggleState.get() + ")";
            }, toggleState));
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }

    }

    Label desc;
    MyLabel content;

    Node control;

    public ToggleProviderSample() {
        desc = new Label("Basic usage of a simple Provider. The IToggleProvider allows an element to share information about its toggle state.");
        desc.setWrapText(true);
        content = new MyLabel("Hello IToggleProvider");

        CheckBox cb = new CheckBox("Toggle State");
        cb.selectedProperty().bindBidirectional(content.toggleState);

        Button t = new Button("Toggle");
        t.setOnAction(evt -> content.toggleState.set(!content.toggleState.get()));
        control = new HBox(cb, t);
    }

    @Override
    public String getName() {
        return "Simple IToggleProvider";
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
