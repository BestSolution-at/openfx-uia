package uia.issues;

import java.util.Arrays;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class InitialFocus extends Application {



    static void createAndShowStage(Stage stage) {

        stage.setTitle("Initial Focus Stage");

        Button initial = new Button("Inital Focus Target");

        BorderPane root = new BorderPane();
        root.setTop(initial);

        VBox center = new VBox();
        center.setPrefSize(VBox.USE_COMPUTED_SIZE, VBox.USE_COMPUTED_SIZE);
        center.setMinSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);
        center.setMaxSize(VBox.USE_PREF_SIZE, VBox.USE_PREF_SIZE);

        // Stage
        Button openStage = new Button("Open Stage");
        openStage.setOnAction(e -> createAndShowStage(new Stage()));
        center.getChildren().add(openStage);

        // Combo
        ComboBox<String> combo = new ComboBox<>();
        combo.setEditable(false);
        combo.setItems(FXCollections.observableArrayList(Arrays.asList("A", "B", "C")));
        center.getChildren().add(combo);

        // Menu
        MenuItem menuItem = new MenuItem("open Stage");
        Menu muVert = new Menu("First level", null, menuItem);
        MenuButton menuButton = new MenuButton("Open stage menu", null, muVert);
        center.getChildren().add(menuButton);

        // FOO
        Button openMenu = new Button("Open Menu");
        ContextMenu menu = new ContextMenu();
        Menu a = new Menu("A");
        a.getItems().addAll(new MenuItem("A1"), new MenuItem("A2"));
        menu.getItems().addAll(a, new MenuItem("B"), new MenuItem("C"));
        openMenu.setContextMenu(menu);
        openMenu.setOnAction(e -> menu.show(openMenu, Side.BOTTOM, 0, 0));
        
        center.getChildren().add(openMenu);





        root.setCenter(center);

        Scene scene = new Scene(root, 300, 275);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        createAndShowStage(primaryStage);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
