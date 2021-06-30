package uia.sample;

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
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class SampleListItem extends BorderPane {
    
    private Sample sample;

    private Label title = new Label();
    private BorderPane contentArea;
    private VBox detailArea;


    SampleListItem(Sample sample) {
        this.sample = sample;
        getStyleClass().addAll("sample-list-item");

        title = new Label();
        title.getStyleClass().add("title");

        contentArea = new BorderPane();
        contentArea.getStyleClass().addAll("content-area");

        detailArea = new VBox();
        detailArea.setFillWidth(true);
        detailArea.getStyleClass().addAll("detail-area");

        setTop(title);
        setCenter(contentArea);
        setRight(detailArea);


        title.setText(this.sample.getName());
        contentArea.setCenter(this.sample.getSample());

        if (this.sample.getDescription() != null) {
            detailArea.getChildren().add(this.sample.getDescription());
        }
        if (this.sample.getControls() != null) {
            BorderPane ctrl = new BorderPane(this.sample.getControls());
            ctrl.getStyleClass().add("control-area");
            detailArea.getChildren().add(ctrl);
        }
    }


}
