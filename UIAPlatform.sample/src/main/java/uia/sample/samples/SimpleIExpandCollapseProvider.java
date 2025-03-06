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
package uia.sample.samples;

import at.bestsolution.uia.ExpandCollapseState;
import at.bestsolution.uia.IExpandCollapseProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.UIA;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import uia.sample.Sample;

public class SimpleIExpandCollapseProvider implements Sample {

    BorderPane expandCollapse;

    private ObjectProperty<ExpandCollapseState> state = new SimpleObjectProperty<>(ExpandCollapseState.Collapsed);

    class UIAExpandCollapse implements IUIAElement, IExpandCollapseProvider {

        final Node node;

        public UIAExpandCollapse(Node node) {
            this.node = node;
            state.addListener((obs, oldValue, newValue) -> {

                withContext(ExpandCollapseProviderContext.class, ctx -> {
                    ctx.ExpandCollapseState.fireChanged(oldValue, newValue);
                });

            });
        }

        @Override
        public void Expand() {
           state.set(ExpandCollapseState.Expanded);
        }

        @Override
        public void Collapse() {
            state.set(ExpandCollapseState.Collapsed);
        }

        @Override
        public ExpandCollapseState get_ExpandCollapseState() {
            return state.get();
        }

        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
            node.requestFocus();
        }

    }

    Label desc;

    public SimpleIExpandCollapseProvider() {
        desc = new Label("Basic usage of IExpandCollapseProvider.");
        desc.setWrapText(true);


        expandCollapse = new BorderPane() {
            UIAExpandCollapse uiaEl = new UIAExpandCollapse(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };


        Label lblState = new Label();
        lblState.textProperty().bind(state.asString());

        expandCollapse.setLeft(lblState);

        Button expand = new Button("expand");
        expand.setOnAction(evt -> state.set(ExpandCollapseState.Expanded));

        Button collapse = new Button("collapse");
        collapse.setOnAction(evt -> state.set(ExpandCollapseState.Collapsed));

        Button leaf = new Button("leaf");
        leaf.setOnAction(evt -> state.set(ExpandCollapseState.LeafNode));

        expandCollapse.setRight(new VBox(expand, collapse, leaf));
    }



    @Override
    public String getName() {
        return "IExpandCollapseProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return expandCollapse;
    }

    @Override
    public Node getControls() {
        return null;
    }



}
