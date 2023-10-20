package uia.sample;

import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class IllegalNotify extends Application {
    private static final String SAVE_ON_EXIT_QUESTION_MESSAGE = "Sollen die Änderungen vor Schließen des Dokuments gespeichert werden?";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Sample To Check JVM Crash");
        final BorderPane rootPane = new BorderPane();
        final Scene scene = new Scene(rootPane, 300, 150);

        VBox centerCont = new VBox(10);
        rootPane.setCenter(centerCont);
        fillChildren(centerCont.getChildren());

        BorderPane.setMargin(rootPane.getCenter(), new Insets(5, 5, 5, 5));

        stage.setScene(scene);
        stage.show();
    }

    private static void fillChildren(final List<Node> contentChildren) {
        contentChildren.add(new Button("Initial Focus"));//nothing - just for initial focus

        contentChildren.add(new Label("Perform tests with UIA-agent and Narrator.\n" +
                "Close service dialog on the X button\n" +
                "Click the next button several times to get jvm-crash:"));

        final Button showDlgBtnSimple = new Button("Show service dlg simple");
        showDlgBtnSimple.setOnAction((ActionEvent e) -> {
            Window owner = ((Node) e.getSource()).getScene().getWindow();
            onShowDialogSimple(owner);
        });

        contentChildren.add(showDlgBtnSimple);
    }

    private static void onShowDialogSimple(final Window wOwner) {
        final Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.initStyle(StageStyle.TRANSPARENT);
        dialog.initOwner(wOwner);
        dialog.initModality(Modality.WINDOW_MODAL);

        dialog.setTitle("Message 1");

        final TextArea tArea = new TextArea(SAVE_ON_EXIT_QUESTION_MESSAGE);
        tArea.setWrapText(true);
        tArea.setEditable(false);
        tArea.setPrefWidth(260);

        final DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getChildren().stream().filter(ButtonBar.class::isInstance).forEach((Node node) -> ((ButtonBar) node).setButtonOrder("L_EUFBXI_YNOCAH_R"));
        dialogPane.setExpanded(true);

        dialogPane.setHeader(createTitleBar(dialog));
        dialogPane.setContent(new VBox(tArea));

        dialog.setWidth(280);
        dialog.setHeight(100);
        dialog.showAndWait();
    }

    private static Node createTitleBar(final Alert dialog) {
        final BorderPane dialogTitlePane = new BorderPane();

        final Button closeBtn = new Button("X");
        closeBtn.setFocusTraversable(false);//TODO here is the problem!!! can be defined in css
        closeBtn.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> closeBtn.requestFocus());//TODO here is the problem!!! setting of the focus on non-focusable button!!!

        closeBtn.setOnAction((ActionEvent event) -> dialog.setResult(ButtonType.CLOSE));
        dialogTitlePane.setRight(closeBtn);

        BorderPane.setAlignment(closeBtn, Pos.CENTER);
        return dialogTitlePane;
    }
}