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
import java.util.List;

import at.bestsolution.uia.ControlType;
import at.bestsolution.uia.IEvent;
import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IProperty;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.UIA;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Subscription;
import uia.sample.Sample;

public class MemoryLeakSample implements Sample {
    Label desc;
    
    private VBox root = new VBox();
    private ArrayList<MyLabel> uiaElements = new ArrayList<>();	 
    private IntegerProperty memoryLeaks = new SimpleIntegerProperty(0);

    class MyLabel extends Label {
        final UIASampleRootElement uia = new UIASampleRootElement(this);
        
        MyLabel() {
            this.setFocusTraversable(true);
            this.setOnMousePressed( evt -> requestFocus());
            uia.initialized.subscribe( val  -> {
                if( val ) {
                    memoryLeaks.set(memoryLeaks.get() + 1 );
                }
                this.setText("Item - " + (uia.initialized.get() ? "initialized" : "not initialized"));
                
            });
        }
        
        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }



    public MemoryLeakSample() {
        desc = new Label("1. Add-Items\n" +
            "2. Focus any item (keyboard or mouse-press)\n" +
            "3. IUIAElements gets initialized and memory-leak counter is increased\n" +
            "4. Remove Items\n"+
            "5. Open visual-vm and check how many objects of type UIASampleRootElement are still in the memory");
        desc.setWrapText(true);
        
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(20);
        root.getChildren().add(hbox);
        root.setSpacing(20);
        
        Button add = new Button("add");
        add.setOnAction( evt -> onAdd());
        
        Button remove = new Button("remove");
        remove.setOnAction( evt -> onRemove());
        
        
        StringBinding leaksLabelText = Bindings.createStringBinding(() -> "Memory-Leaks Counter: " + memoryLeaks.get(), memoryLeaks);
        Label leaksLabel = new Label();
        leaksLabel.textProperty().bind(leaksLabelText);
        
        hbox.getChildren().addAll(add, remove, leaksLabel);
    }
    
    private void onAdd() {
        MyLabel item = new MyLabel();
        root.getChildren().add(item);
        uiaElements.add(item);
    }
    
    private void onRemove() {
        root.getChildren().removeAll(uiaElements);
        uiaElements.clear();
    }

    @Override
    public String getName() {
        return "Test Memory-Leak";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return root;
    }

    @Override
    public Node getControls() {
        return null;
    }
    

    private class UIASampleRootElement implements IUIAVirtualRootElement {
        final Node node;
        final List<IUIAElement> childs = new ArrayList<>();
        private BooleanProperty initialized = new SimpleBooleanProperty();
        
        UIASampleRootElement (Node node) {
        	this.node = node;
        }

        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
        }
        
        @Override
        public void initialize(IInitContext init) {
            initialized.set(true);
        }
        
        @Override
        public ControlType getControlType() {
            return ControlType.UIA_ButtonControlTypeId;
        }
        
        @Override
        public IUIAElement GetFocusDelegate() {
            return this;
        }
        
        @Override
        public IUIAElement getFocus() {
            return this;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return childs;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }
    };
}
