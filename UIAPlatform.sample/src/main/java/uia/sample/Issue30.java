package uia.sample;

import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;


public class Issue30 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("JVM Crash Combobox Sample");
        BorderPane rootPane = new BorderPane();
        final Scene scene = new Scene(rootPane, 250, 250);

        VBox centerCont = new VBox(10);
        rootPane.setCenter(centerCont);
        fillChildren(centerCont.getChildren());

        BorderPane.setMargin(rootPane.getCenter(), new Insets(5, 5, 5, 5));

        stage.setScene(scene);
        stage.show();
    }

    protected void fillChildren(final List<Node> contentChildren) {
        contentChildren.add(new Button("Initial Focus"));//nothing simple for initial focus

        final ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableList(Arrays.asList("a item 1", "b item 2 very long item for tests\nsecond line of the second item", "c item 3", "d item 4")));
        comboBox.setEditable(true);
        comboBox.setPrefWidth(100);
        comboBox.setMinWidth(100);
        comboBox.setMaxWidth(100);

        comboBox.setOnKeyReleased(event -> {
            String editorText = comboBox.getEditor().getText();
            String itemText = comboBox.getItems().stream().filter((String item) -> item.contains(editorText)).findFirst().orElse(null);

            boolean showPopup = !editorText.isEmpty() && itemText != null;
            if (showPopup) {
                if (!comboBox.isShowing()) {
                    comboBox.show();
                    resizePopupContent(comboBox);// rebuilding of the combobox-popup inside will cause jvm crash under uia-lib
                }
            } else {
                if (comboBox.isShowing()) {
                    comboBox.hide();
                }
            }
        });

        contentChildren.add(comboBox);
        contentChildren.add(new Button("Next Focus"));//nothing simple for initial focus
    }

    private static void resizePopupContent(final ComboBox<?> comboBox) {
        final ObservableList<?> sourceList = comboBox.getItems();
        final ListView<?> listView = ((ComboBoxListViewSkin<?>) comboBox.getSkin()).getListView();

        final double computedWidth = listView.prefWidth(Region.USE_COMPUTED_SIZE);
        final double width = Math.max(computedWidth, calculatePrefWidth(listView, sourceList));
        listView.resize(width, listView.prefHeight(Region.USE_COMPUTED_SIZE));
        listView.requestLayout();
    }

    /**
     * Calculates pref width of ComboBox without restrains of pane.
     *
     * @param data     that we put to ComboBox
     * @param listView to calc pref width for
     * @return pref width of ComboBox
     */
    private static double calculatePrefWidth(final ListView<?> listView, final ObservableList<?> data) {
        double prefWidth = 1.0D;
        // check if cells width is smaller than ComboBox width
        final VirtualFlow<?> vf = (VirtualFlow<?>) listView.lookup(".virtual-flow");
        if (vf != null) {
            vf.recreateCells(); // JVM-CRASH under UIA!!! // recreate cells, so we can get correct width of all cells
            vf.requestCellLayout();
            listView.layout();

            double maxCellWidth = Region.USE_COMPUTED_SIZE;
            double cellOffsets = 0.0D;
            final int count = data.size();
            for (int i = 0; i < count; i++) {
                final IndexedCell<?> cell = vf.getCell(i);
                double cellPrefWidth = cell.prefWidth(Region.USE_COMPUTED_SIZE);

                // when cell contains graphic, calculate its width, so  we don't need to use reflection to call com.sun.javafx.scene.control.skin.VirtualFlow.getCellLength(int)
                final Node graphic = cell.getGraphic();
                if (graphic instanceof Label) {
                    // from first visual cell get padding and graphic text gap for correct final width, to avoid showing of the scroll bar
                    if (cellOffsets <= 0.0D && graphic.getLayoutBounds().getWidth() > 0.0D) {
                        cellOffsets = listView.getPadding().getLeft() + cell.getPadding().getLeft() + cell.getPadding().getRight();
                    }
                    final double graphicPrefWidth = graphic.prefWidth(Region.USE_COMPUTED_SIZE);
                    // set cell pref width
                    cellPrefWidth = Math.max(cellPrefWidth, graphicPrefWidth) + cellOffsets;
                }
                maxCellWidth = Math.max(maxCellWidth, cellPrefWidth);
            }

            // assign max width of cells to prefWidth
            if (maxCellWidth > Region.USE_COMPUTED_SIZE) {
                final double scrollBarWidth = 20D;
                prefWidth = maxCellWidth + scrollBarWidth; // add scroll bar width to max cell width, to avoid showing of the scroll bar
            }
        }

        return prefWidth;
    }
}