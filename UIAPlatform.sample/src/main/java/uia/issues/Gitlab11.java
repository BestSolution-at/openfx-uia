package uia.issues;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author dvhaus gmbh in cooperation with intechcore gmbh
 */
public class Gitlab11 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        root.setPrefSize(200, 200);

        root.setTop(new Button("Top button"));
        root.setCenter(new TextField("Center text"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Test UIA");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
