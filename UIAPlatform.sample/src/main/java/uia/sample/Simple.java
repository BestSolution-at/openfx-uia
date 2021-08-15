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
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import uia.sample.samples.CanvasWithVirtualChildren;
import uia.sample.samples.SimpleTextProvider;
import uia.sample.samples.SimpleTextProviderWithAttributes;
import uia.sample.samples.SimpleTextProviderWithChildren;
import uia.sample.samples.SimpleTextProviderWithChildren2;
import uia.sample.samples.SimpleUIAElement;
import uia.sample.samples.SimpleUIAElementWithProperties;
import uia.sample.samples.ToggleProviderSample;

public class Simple extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        BorderPane root = new BorderPane();

        
        root.setPrefSize(800, 600);

        List<Sample> samples = Arrays.asList(
            new SimpleUIAElement(), 
            new SimpleUIAElementWithProperties(),
            new ToggleProviderSample(), 
            new CanvasWithVirtualChildren(),
            new SimpleTextProvider(),
            new SimpleTextProviderWithAttributes(),
            new SimpleTextProviderWithChildren(),
            new SimpleTextProviderWithChildren2()
            );

        VBox s = new VBox();
        s.setFillWidth(true);
        s.setPadding(new Insets(20));

        s.getChildren().addAll(samples.stream().map(sample -> new SampleListItem(sample)).collect(Collectors.toList()));

        ScrollPane sp = new ScrollPane();
        sp.setFitToWidth(true);
        sp.setContent(s);

        root.setCenter(sp);

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