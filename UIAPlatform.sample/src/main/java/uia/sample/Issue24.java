package uia.sample;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class Issue24 extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        primaryStage.setTitle("ComboBox UIA sample");
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(
                "First elem", "Second elem", "Third elem", "Fourth elem"));
        comboBox.setEditable(true);
        comboBox.setValue("First elem");
        Label labelForLbl = new Label("Label for ComboBox");
        labelForLbl.setLabelFor(comboBox);
        HBox hBox = new HBox(10, labelForLbl, comboBox);
        Scene scene = new Scene(hBox, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
