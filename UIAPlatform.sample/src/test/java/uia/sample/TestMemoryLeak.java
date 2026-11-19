package uia.sample;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IStructureChangedEvent;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.StructureChangeType;
import at.bestsolution.uia.UIA;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import one.jpro.jmemorybuddy.JMemoryBuddy;

@ExtendWith(ApplicationExtension.class)
class TestMemoryLeak {
    private VBox root = new VBox();
    Button button;
    
    /**
     * This loads the uia-client.
     */
    interface UiaCore extends StdCallLibrary {
        UiaCore INSTANCE = Native.load("UIAutomationCore", UiaCore.class);
        boolean UiaClientsAreListening();
    }

	
    @Start
    void start(Stage stage) {
        boolean listening = UiaCore.INSTANCE.UiaClientsAreListening();
        System.out.println("UIA clients listening: " + listening);
       
        button = new Button("init focus");
        root.getChildren().add(button);
       
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    @Test
    void testIUIAElementMemoryLeak(FxRobot robot) {
        JMemoryBuddy.memoryTest(checker -> {
            final UIANode uiaNode = new UIANode();
            uiaNode.setText("Test");
            robot.interact(() ->  root.getChildren().add(uiaNode));
           
            robot.sleep(300);
            robot.interact(() -> uiaNode.requestFocus());
            robot.sleep(300);
            
            /**
             * When this fails, UiaCore is probably not enough. Try with running narrator in background!
             */
            Assertions.assertTrue(uiaNode.uia.initialized.get());
            robot.interact(() ->  root.getChildren().remove(uiaNode));
            
            robot.sleep(300);
            checker.assertCollectable(uiaNode.uia);
        });
    }
    
    @Test
    void testIUIAVirtualElementMemoryLeak(FxRobot robot) {
        JMemoryBuddy.memoryTest(checker -> {
            final UIARootNode uiaRootNode = new UIARootNode();
            uiaRootNode.setText("Test");
            robot.interact(() ->  root.getChildren().add(uiaRootNode));
            
            MyUIAVirtualElement virtualUIAElement = new MyUIAVirtualElement(uiaRootNode, uiaRootNode.uia);
            uiaRootNode.uia.virtualUiaChildren.add(virtualUIAElement);
           
            robot.sleep(300);
            robot.interact(() -> uiaRootNode.requestFocus());
            robot.sleep(300);
            
            Assertions.assertTrue(uiaRootNode.uia.initialized.get());
            Assertions.assertTrue(virtualUIAElement.initialized.get());
            uiaRootNode.uia.virtualUiaChildren.clear();
            
            /**
             * ChildrenInvalidated event needed to make sure IUIAElement is collectible.
             */
            robot.interact(() ->  uiaRootNode.uia.StructureChanged.fire( StructureChangeType.ChildrenInvalidated, null ) );
            
            robot.sleep(300);
            
            /**
             * Check memory leak if only virtual child is removed
             */
            checker.assertCollectable(virtualUIAElement);
            
            virtualUIAElement = new MyUIAVirtualElement(uiaRootNode, uiaRootNode.uia);
            uiaRootNode.uia.virtualUiaChildren.add(virtualUIAElement);
           
            robot.sleep(300);
            // notifyAccessibleAttributeChanged instead of requestFocus because node is already focused
            robot.interact(() -> uiaRootNode.notifyAccessibleAttributeChanged( AccessibleAttribute.FOCUS_NODE ) );
            robot.sleep(300);
            
            Assertions.assertTrue(uiaRootNode.uia.initialized.get());
            Assertions.assertTrue(virtualUIAElement.initialized.get());
            
            robot.interact(() ->  root.getChildren().remove(uiaRootNode));
            robot.sleep(300);
            
            /**
             * Check memory leak if root node gets removed from scene-graph.
             * Here no ChildrenInvalidated event is needed.
             */
            checker.assertCollectable(virtualUIAElement);
        });
    }
	

    class UIANode extends Label {
        final MyUIAElement uia = new MyUIAElement(this);
       
        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
    
    class UIARootNode extends Label {
        final MyUIARootElement uia = new MyUIARootElement(this);
       
        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
    
    private class MyUIARootElement extends MyUIAElement implements IUIAVirtualRootElement {
    	ObservableList<IUIAElement> virtualUiaChildren = FXCollections.observableArrayList();
        IStructureChangedEvent StructureChanged;
        
        MyUIARootElement (Node node) {
        	super(node);
        }
        
        @Override
        public IUIAElement getFocus() {
            return virtualUiaChildren.isEmpty() ? null : virtualUiaChildren.get(0);
        }
        
        @Override
        public void initialize(IInitContext init) {
        	super.initialize(init);
            StructureChanged = init.addStructureChangedEvent();
        }

        @Override
        public List<IUIAElement> getChildren() {
            return virtualUiaChildren;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }
    
    };
    
    private class MyUIAVirtualElement extends MyUIAElement implements IUIAVirtualElement {
        final IUIAElement parent;
        
        MyUIAVirtualElement( Node referenceNode, IUIAElement parent ) {
            super( referenceNode );
            this.parent = parent;
        }

        List<IUIAElement> childs = new ArrayList<>();
        
        @Override
        public IUIAElement getParent() {
            return parent;
            }

        @Override
        public List<IUIAElement> getChildren() {
            return childs;
        }
    
    };
    
    private class MyUIAElement implements IUIAElement {
        final Node node;
        final BooleanProperty initialized = new SimpleBooleanProperty();
        final List<IUIAElement> childs = new ArrayList<>();
        
        MyUIAElement (Node node) {
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
            init.addHasKeyboardFocusProperty(() -> true );
            initialized.set(true);
        }
    };
}
