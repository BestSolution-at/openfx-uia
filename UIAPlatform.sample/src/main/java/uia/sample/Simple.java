package uia.sample;



import javafx.application.Application;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.uia.IWindowProvider;
import javafx.uia.UIA;

public class Simple extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane() {
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... arguments) {
                
                if (UIA.isProviderQuery(IWindowProvider.class, attribute, arguments)) {
                    
                }
                
                return super.queryAccessibleAttribute(attribute, arguments);
            }
        };
        root.setPrefSize(800, 600);

        VBox widgets = new VBox();
        widgets.setSpacing(20);
        widgets.getChildren().add(new TextField());
        widgets.getChildren().add(new Button("Button"));
        root.setCenter(widgets);

        Scene scene = new Scene(root);

        primaryStage.setTitle("Simple");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}