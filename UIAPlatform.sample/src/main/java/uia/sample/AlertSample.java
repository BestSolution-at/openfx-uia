package uia.sample;
 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
public class AlertSample extends Application {
 
    // launch the application
    public void start(Stage s)
    {
        // set title for the stage
        s.setTitle("creating alerts");
 
        // create a button
        Button b = new Button("Confirmation alert");
        Button b1 = new Button("error alert");
        Button b2 = new Button("Information alert");
        Button b3 = new Button("Warning alert");
 
        // create a tile pane
        TilePane r = new TilePane();
 
        // create a alert
        Alert a = new Alert(AlertType.NONE);
 
        // action event
        EventHandler<ActionEvent> event = new
                         EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // set alert type
                a.setAlertType(AlertType.CONFIRMATION);
 
                // show the dialog
                a.show();
            }
        };
 
        // action event
        EventHandler<ActionEvent> event1 = new
                         EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // set alert type
                a.setAlertType(AlertType.ERROR);
 
                // show the dialog
                a.show();
            }
        };
 
        // action event
        EventHandler<ActionEvent> event2 = new
                         EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // set alert type
                a.setAlertType(AlertType.INFORMATION);
 
                // show the dialog
                a.show();
            }
        };
 
        // action event
        EventHandler<ActionEvent> event3 = new
                          EventHandler<ActionEvent>() {
            public void handle(ActionEvent e)
            {
                // set alert type
                a.setAlertType(AlertType.WARNING);
 
                // show the dialog
                a.show();
            }
        };
 
        // when button is pressed
        b.setOnAction(event);
        b1.setOnAction(event1);
        b2.setOnAction(event2);
        b3.setOnAction(event3);
 
        // add button
        r.getChildren().add(b);
        r.getChildren().add(b1);
        r.getChildren().add(b2);
        r.getChildren().add(b3);
 
        // create a scene
        Scene sc = new Scene(r, 200, 200);
 
        // set the scene
        s.setScene(sc);
 
        s.show();
    }
 
    public static void main(String args[])
    {
        // launch the application
        launch(args);
    }
}