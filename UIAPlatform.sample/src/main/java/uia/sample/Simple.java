/*
 * -----------------------------------------------------------------
 * Copyright (c) 2021 BestSolution.at EDV Systemhaus GmbH
 * All Rights Reserved.
 *
 * BestSolution.at MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE  OR NON - INFRINGEMENT.
 * BestSolution.at SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS
 * SOFTWARE OR ITS DERIVATIVES.
 *
 * This software is released under the terms of the
 *
 *                  "GNU General Public License, Version 2
 *                         with classpath exception"
 *
 * and may only be distributed and used under the terms of the
 * mentioned license. You should have received a copy of the license
 * along with this software product, if not you can download it from
 * http://www.gnu.org/licenses/gpl.html
 * ----------------------------------------------------------------
 */
package uia.sample;



import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uia.sample.samples.AllDummyProviders;
import uia.sample.samples.CanvasWithVirtualChildren;
import uia.sample.samples.DocumentModelSample;
import uia.sample.samples.PickTest;
import uia.sample.samples.SimpleAsyncContentLoadedEvent;
import uia.sample.samples.SimpleIDockProvider;
import uia.sample.samples.SimpleIExpandCollapseProvider;
import uia.sample.samples.SimpleIGridProvider;
import uia.sample.samples.SimpleIRangeValueProvider;
import uia.sample.samples.SimpleISelectionProvider;
import uia.sample.samples.SimpleITableProvider;
import uia.sample.samples.SimpleITransformProvider;
import uia.sample.samples.SimpleIValueProvider;
import uia.sample.samples.SimpleIWindowProvider;
import uia.sample.samples.SimpleNotificationEvent;
import uia.sample.samples.SimpleScrollProvider;
import uia.sample.samples.SimpleStructureChangedEvent;
import uia.sample.samples.SimpleTextEditTextChangedEvent;
import uia.sample.samples.SimpleTextProvider;
import uia.sample.samples.SimpleTextProviderWithAnnotationProvider;
import uia.sample.samples.SimpleTextProviderWithAttributes;
import uia.sample.samples.SimpleTextProviderWithChildren;
import uia.sample.samples.SimpleTextProviderWithChildren2;
import uia.sample.samples.SimpleUIAElement;
import uia.sample.samples.SimpleUIAElementWithProperties;
import uia.sample.samples.ToggleProviderSample;
import uia.sample.samples.VListSample;

public class Simple extends Application {

    static class MenuListCell extends ListCell<Sample> {
        @Override
        protected  void updateItem(Sample sample, boolean empty) {
            super.updateItem(sample, empty);
            if (empty) {
                setText("");
            } else {
                setText(sample.getName());
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //new AccessibleMonitor();

        BorderPane root = new BorderPane();


        root.setPrefSize(800, 600);

        List<Sample> samples = Arrays.asList(
            new PickTest(),
            new VListSample(),
            new AllDummyProviders(),
            new SimpleUIAElement(),
            new SimpleUIAElementWithProperties(),
            new ToggleProviderSample(),
            new CanvasWithVirtualChildren(),
            new SimpleTextProvider(),
            new SimpleTextProviderWithAttributes(),
            new SimpleTextProviderWithChildren(),
            new SimpleTextProviderWithChildren2(),
            new DocumentModelSample(),
            new SimpleScrollProvider(),
            new SimpleIGridProvider(),
            new SimpleITableProvider(),
            new SimpleIExpandCollapseProvider(),
            new SimpleITransformProvider(),
            new SimpleIWindowProvider(),
            new SimpleIDockProvider(),
            new SimpleTextProviderWithAnnotationProvider(),
            new SimpleIValueProvider(),
            new SimpleIRangeValueProvider(),
            new SimpleISelectionProvider(),
            new SimpleNotificationEvent(),
            new SimpleAsyncContentLoadedEvent(),
            new SimpleTextEditTextChangedEvent(),
            new SimpleStructureChangedEvent()
            );

        VBox s = new VBox();
        s.setFillWidth(true);
        s.setPadding(new Insets(20));

        //s.getChildren().addAll(samples.stream().map(sample -> new SampleListItem(sample)).collect(Collectors.toList()));

        BorderPane sampleArea = new BorderPane();
        sampleArea.getStyleClass().addAll("sample-area");

        BorderPane contentArea = new BorderPane();
        contentArea.getStyleClass().addAll("content-area");

        Label title = new Label();
        title.getStyleClass().add("title");

        sampleArea.setTop(title);
        sampleArea.setCenter(contentArea);

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setContent(s);

        BorderPane desc = new BorderPane();
        desc.setMaxWidth(200);

        ListView<Sample> nav = new ListView<>();
        nav.setItems(FXCollections.observableArrayList(samples));
        nav.setCellFactory(list -> new MenuListCell());
        nav.getSelectionModel().selectedItemProperty().addListener((obs, ol, ne) -> {
            if (ne != null) {
                Node sample = ne.getSample();

                title.setText(ne.getName());
                contentArea.setCenter(sample);
                desc.setTop(ne.getDescription());
                desc.setCenter(ne.getControls());

            } else {
                title.setText("");
                contentArea.setCenter(new Label("Please select a sample"));
                desc.setTop(null);
                desc.setCenter(null);
            }
        });
        root.setLeft(nav);
        root.setCenter(sampleArea);
        root.setRight(desc);
        //root.setCenter(sp);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Simple.class.getResource("/sample.css").toExternalForm());

        primaryStage.setTitle("Simple UIA samples");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
