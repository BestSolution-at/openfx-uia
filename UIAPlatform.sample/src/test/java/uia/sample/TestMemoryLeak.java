package uia.sample;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

import at.bestsolution.uia.IEvent;
import at.bestsolution.uia.IInitContext;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.StandardEventIds;
import at.bestsolution.uia.UIA;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
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
    
    /**
     * This loads the uia client.
     * In the MemoryLeakSample this is enough to initialize the Context without narrator
     * But here in the test it somehow doesn't work
     */
    interface UiaCore extends StdCallLibrary {
        UiaCore INSTANCE = Native.load("UIAutomationCore", UiaCore.class);
        boolean UiaClientsAreListening();
    }

	
    @Start
    void start(Stage stage) {
        boolean listening = UiaCore.INSTANCE.UiaClientsAreListening();
        System.out.println("UIA clients listening: " + listening);
        Button button = new Button("init focus");
        root.getChildren().add(button);
       
        stage.setScene(new Scene(root, 500, 500));
        stage.show();
    }

    @Test
    void testIUIAElementMemoryLeak(FxRobot robot) {
        MyLabel control = new MyLabel();
        control.setText("Test");
        robot.interact(() -> root.getChildren().add(control));
        robot.sleep(300);
        robot.interact(() -> control.requestFocus());
        robot.sleep(300);
        
        //Only passes when uia-client is running in the background
        Assertions.assertTrue(control.uia.initialized.get());
        
        robot.interact(() ->root.getChildren().remove(control));
        
        robot.interact(() -> JMemoryBuddy.memoryTest( checker -> checker.assertCollectable( control ) ));
    }
	

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
            	System.out.println(" uia call ");
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }
    
    private class MyUIAElement implements IUIAElement {
        final Node node;
        private BooleanProperty initialized = new SimpleBooleanProperty();
        
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
        	IEvent automationFocusChanged = init.addEvent( StandardEventIds.UIA_AutomationFocusChangedEventId );
            automationFocusChanged.fire();
            initialized.set(true);
        }
    };
}
