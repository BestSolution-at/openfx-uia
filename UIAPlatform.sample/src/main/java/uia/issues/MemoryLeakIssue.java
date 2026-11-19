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
package uia.issues;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IStructureChangedEvent;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.StructureChangeType;
import at.bestsolution.uia.UIA;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import one.jpro.jmemorybuddy.JMemoryBuddyLive;
import uia.sample.Sample;

// Sample to reproduce issue-40
// https://gitlab.bestsolution.at/justiz/openfx-uia/-/work_items/40
public class MemoryLeakIssue extends Application {
    private final BorderPane root = new BorderPane();
    private final VBox simpleContent = new VBox();
    private final VBox rootVirtualContent = new VBox();
    
    private final ObservableList<IUIAElement> virtualUiaChildren = FXCollections.observableArrayList();
    private final Label leaksLabel = new Label();
    
    private final RootUiaNode rootUIANode = new RootUiaNode();
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        HBox tools = new HBox();
        tools.setAlignment(Pos.CENTER_LEFT);
        tools.setSpacing(20);
        
        Button add = new Button("Add Item");
        add.setOnAction( evt -> onAdd());
        
        Button addVirtualChild = new Button("Add Virtual Child");
        addVirtualChild.setOnAction( evt -> onAddVirtualChild());
        
        Button remove = new Button("Clear");
        remove.setOnAction( evt -> onRemove());
        
        tools.getChildren().addAll(addVirtualChild, add, remove, leaksLabel);
        
        root.setTop(tools);
        
        Label desc = new Label( computeDesc() );
        root.setBottom(desc);
        
        HBox center = new HBox();
        center.setSpacing(50);
        center.setPadding(new Insets( 20 ));
        center.getChildren().addAll(rootVirtualContent, simpleContent);
        root.setCenter(center);
        
        simpleContent.setSpacing(10);
        
        rootVirtualContent.getChildren().add(rootUIANode);
        
        onAddVirtualChild();
        onAdd();
        
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    private String computeDesc() {
        StringBuilder result = new StringBuilder();
        result.append("'Add Virtual Item' to add a new IUIAVirtualElement to a IUIAVirtualRootElement\n");
        result.append("\n");
        result.append("'Add Item' to add a new simple node with IUIAElement\n");
        result.append("\n");
        result.append("'Clear' to cleanup all simple nodes and removes all IUIAVirtualElement from root element\n");
        result.append("\n");
        result.append("Make sure to focus the items by mouse-press or keyboard to initialze IUIAElement\n");
        result.append("\n");
        result.append("\n");
        result.append("UIA-Client running: " +  UiaCore.INSTANCE.UiaClientsAreListening());
        return result.toString();
    }
    
    private void onAdd() {
        UiaNode node = new UiaNode();
        simpleContent.getChildren().add(node);
    }
    
    private void onAddVirtualChild() {
    	virtualUiaChildren.add(new MyUIAVirtualElement(rootUIANode));
    }
    
    private void onRemove() {
        simpleContent.getChildren().clear();
        virtualUiaChildren.clear();
        
        if(rootUIANode.uia.structureChanged != null) {
            rootUIANode.uia.structureChanged.fire( StructureChangeType.ChildrenInvalidated, null );
        }
        
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
    
    class UiaNode extends Label {
        final MyUIAElement uia = new MyUIAElement(this);
        
        UiaNode() {
            this.setFocusTraversable(true);
            this.setOnMousePressed( evt -> requestFocus());
            uia.initialized.subscribe( val  -> {
                this.setText("UiaNode\nMyUIAElement@" +  System.identityHashCode(uia) + " init: " + val );
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
    
    class RootUiaNode extends Label {
        final MyUIARootElement uia = new MyUIARootElement(this);
        
        RootUiaNode() {
            this.setFocusTraversable(true);
            this.setOnMousePressed( evt -> requestFocus());
            refreshText();
            virtualUiaChildren.addListener( (ListChangeListener<? super IUIAElement> ) change -> refreshText() );
        }
        
        void refreshText() {
            StringBuilder result = new StringBuilder();
            result.append("RootUiaNode\n");
            virtualUiaChildren.forEach( child -> result.append("MyUIAVirtualElement@" + System.identityHashCode(child) + " init: " + ((MyUIAElement) child).initialized.get() +"\n"));
            this.setText(result.toString());
        }
        
        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
    
    private class MyUIARootElement implements IUIAVirtualRootElement {
        IStructureChangedEvent structureChanged;
        Node node;
        
        MyUIARootElement (Node node) {
            this.node = node;
        }
        
        @Override
        public IUIAElement getFocus() {
            return virtualUiaChildren.isEmpty() ? null : virtualUiaChildren.get(0);
        }

        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            //
        }
        
        @Override
        public void initialize(IInitContext init) {
            structureChanged = init.addStructureChangedEvent();
            init.addHasKeyboardFocusProperty(() -> true );
        }

        @Override
        public List<IUIAElement> getChildren() {
            return virtualUiaChildren;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }
    }
    
    private class MyUIAVirtualElement extends MyUIAElement implements IUIAVirtualElement {
        MyUIAVirtualElement(Node node) {
            super(node);
        }

        private final List<IUIAElement> children = new ArrayList<>();
        
        @Override
        public IUIAElement getParent() {
            return rootUIANode.uia;
            }

        @Override
        public List<IUIAElement> getChildren() {
            return children;
        }
    }
    
    private class MyUIAElement implements IUIAElement {
        final Node node;
        final BooleanProperty initialized = new SimpleBooleanProperty();
        List<IUIAElement> childs = new ArrayList<>();
        
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
            //empty
        }
        
        @Override
        public void initialize(IInitContext init) {
        	init.addHasKeyboardFocusProperty(() -> true );
            initialized.set(true);
            rootUIANode.refreshText();
        }
    }
    
    interface UiaCore extends StdCallLibrary {
        UiaCore INSTANCE = Native.load("UIAutomationCore", UiaCore.class);
        boolean UiaClientsAreListening();
    }
}
