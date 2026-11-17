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

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.UIA;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import one.jpro.jmemorybuddy.JMemoryBuddyLive;
import uia.sample.Sample;

public class MemoryLeakSample implements Sample {
    Label desc;
    
    private VBox root = new VBox();
    private ArrayList<MyLabel> uiaElements = new ArrayList<>();	 
    private Label leaksLabel = new Label();

    class MyLabel extends Label {
        final MyUIAElement uia = new MyUIAElement(this);
        
        MyLabel() {
            this.setFocusTraversable(true);
            this.setOnMousePressed( evt -> requestFocus());
            uia.initialized.subscribe( val  -> {
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
        desc = new Label( computeDesc() );
        
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(20);
        root.getChildren().addAll(hbox, leaksLabel );
        root.setSpacing(20);
        
        Button add = new Button("Add Item");
        add.setOnAction( evt -> onAdd());
        
        Button remove = new Button("Clear");
        remove.setOnAction( evt -> onRemove());
        
        hbox.getChildren().addAll(add, remove);
    }
    
    private String computeDesc() {
        StringBuilder result = new StringBuilder();
        result.append("1. Add-Items\n");
        result.append("2. Clear\n");
        result.append("3. No leaks\n");
        result.append("\n");
        result.append("4. Add-Items\n");
        result.append("5. Focus the item\n");
        result.append("6. Clear\n");
        result.append("7. Object still in memory\n");
        result.append("\n");
        result.append("\n");
        result.append("UIA-Client running: \n");
        result.append( UiaCore.INSTANCE.UiaClientsAreListening());
        return result.toString();
    }
    
    private void onAdd() {
        MyLabel item = new MyLabel();
        root.getChildren().add(item);
        uiaElements.add(item);
    }
    
    private void onRemove() {
        root.getChildren().removeAll(uiaElements);
        uiaElements.clear();
        
        checkLeak();
    }
    
    private void checkLeak() {
        leaksLabel.setText("Checking...");
        Thread t = new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    System.gc();
                    Thread.sleep(300);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            var report = JMemoryBuddyLive.getReport();
            String text = report.uncollectedEntries.isEmpty()
                    ? "No leaks"
                    : "Uncollected:\n" + report.uncollectedEntries.stream()
                            .map(entry -> entry.name)
                            .collect(Collectors.joining("\n"));
            Platform.runLater(() -> leaksLabel.setText(text));
        }, "leak-check");
        t.setDaemon(true);
        t.start();
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
    

    private class MyUIAElement implements IUIAElement {
        final Node node;
        private BooleanProperty initialized = new SimpleBooleanProperty();
        
        MyUIAElement (Node node) {
            this.node = node;
            JMemoryBuddyLive.markCollectable("MyUIAElement@" + System.identityHashCode(this), this);
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
        	init.addHasKeyboardFocusProperty(() -> true );
            initialized.set(true);
        }
        
    };
    
    interface UiaCore extends StdCallLibrary {
        UiaCore INSTANCE = Native.load("UIAutomationCore", UiaCore.class);
        boolean UiaClientsAreListening();
    }
}
