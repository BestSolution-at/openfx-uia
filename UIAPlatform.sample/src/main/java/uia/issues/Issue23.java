package uia.issues;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Issue23 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Spinner accessibility sample");
        Spinner<Integer> spinner = new Spinner<>(1, 100, 5, 1);
        Label labelForLbl = new Label("Label for spinner");
        labelForLbl.setLabelFor(spinner);
        HBox hBox = new HBox(10, labelForLbl, spinner);
        Scene scene = new Scene(hBox, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.toFront();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
