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

import java.util.Collections;
import java.util.List;

import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.javafx.uia.IValueProvider;
import at.bestsolution.uia.javafx.uia.UIA;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uia.sample.Sample;

public class SimpleIValueProvider implements Sample {

    private BooleanProperty isReadonly = new SimpleBooleanProperty(false);
    private StringProperty value = new SimpleStringProperty("Hello");

    class UIAValue implements IUIAVirtualRootElement, IValueProvider {
        final Pane node;
        public UIAValue(Pane node) {
            this.node = node;
            isReadonly.addListener((obs, oldValue, newValue) -> {
                withContext(ValueProviderContext.class, ctx -> {
                    ctx.IsReadonly.fireChanged(oldValue, newValue);
                });
            });
            value.addListener((obs, oldValue, newValue) -> {
                withContext(ValueProviderContext.class, ctx -> {
                    ctx.Value.fireChanged(oldValue, newValue);
                });
            });
        }
        @Override
        public Bounds getBounds() {
            return node.localToScreen(node.getBoundsInLocal());
        }
        @Override
        public void SetFocus() {
            node.requestFocus();
        }
        @Override
        public boolean get_IsReadOnly() {
            return isReadonly.get();
        }
        @Override
        public String get_Value() {
            return value.get();
        }
        @Override
        public void SetValue(String val) {
            if (isReadonly.get()) return;
            value.set(val);
        }
        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }
        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            return null;
        }
    }

    Label desc;

    Pane host;

    VBox ctrl;

    public SimpleIValueProvider() {
        desc = new Label("Basic usage of IValueProvider.");
        desc.setWrapText(true);

        host = new Pane();

        BorderPane pane0 = new BorderPane() {
            UIAValue uiaEl = new UIAValue(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        pane0.setStyle("-fx-border-width: 1px; -fx-border-color: red;");
        Text text = new Text();
        text.textProperty().bind(value);
        pane0.setCenter(text);


        host.getChildren().add(pane0);


        ctrl = new VBox();

        CheckBox readonly = new CheckBox("isReadonly");
        readonly.selectedProperty().bindBidirectional(isReadonly);
        ctrl.getChildren().add(readonly);

        TextField val = new TextField();
        val.textProperty().bindBidirectional(value);
        ctrl.getChildren().addAll(new Label("Value"), val);

    }



    @Override
    public String getName() {
        return "IValueProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return host;
    }

    @Override
    public Node getControls() {
        return ctrl;
    }



}
