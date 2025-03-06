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

import at.bestsolution.uia.IRangeValueProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.UIA;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import uia.sample.Sample;

public class SimpleIRangeValueProvider implements Sample {

    private BooleanProperty isReadonly = new SimpleBooleanProperty(false);
    private ObjectProperty<Double> value = new SimpleObjectProperty<>(0d);
    private DoubleProperty maximum = new SimpleDoubleProperty(100);
    private DoubleProperty minimum = new SimpleDoubleProperty(0);
    private DoubleProperty smallIncrement = new SimpleDoubleProperty(1);
    private DoubleProperty largeIncrement = new SimpleDoubleProperty(10);

    class UIARangeValue implements IUIAVirtualRootElement, IRangeValueProvider {
        final Pane node;
        public UIARangeValue(Pane node) {
            this.node = node;
            isReadonly.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.IsReadonly.fireChanged(oldValue, newValue);
                });
            });
            value.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.Value.fireChanged(oldValue.doubleValue(), newValue.doubleValue());
                });
            });
            maximum.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.Maximum.fireChanged(oldValue.doubleValue(), newValue.doubleValue());
                });
            });
            minimum.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.Minimum.fireChanged(oldValue.doubleValue(), newValue.doubleValue());
                });
            });
            smallIncrement.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.SmallChange.fireChanged(oldValue.doubleValue(), newValue.doubleValue());
                });
            });
            largeIncrement.addListener((obs, oldValue, newValue) -> {
                withContext(RangeValueProviderContext.class, ctx -> {
                    ctx.LargeChange.fireChanged(oldValue.doubleValue(), newValue.doubleValue());
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
        public double get_Value() {
            return value.get();
        }
        @Override
        public void SetValue(double val) {
            if (isReadonly.get()) return;
            value.set(val);
        }
        @Override
        public double get_Minimum() {
            return minimum.get();
        }
        @Override
        public double get_Maximum() {
            return maximum.get();
        }
        @Override
        public double get_SmallChange() {
            return smallIncrement.get();
        }
        @Override
        public double get_LargeChange() {
            return largeIncrement.get();
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

    public SimpleIRangeValueProvider() {
        desc = new Label("Basic usage of IRangeValueProvider.");
        desc.setWrapText(true);

        host = new Pane();

        BorderPane pane0 = new BorderPane() {
            UIARangeValue uiaEl = new UIARangeValue(this);
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return uiaEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        pane0.setStyle("-fx-border-width: 1px; -fx-border-color: red;");

        StringBinding bLbl = Bindings.createStringBinding(() -> {
            return "Value: " + value.get();
        }, value);

        Text text = new Text();
        text.textProperty().bind(bLbl);
        pane0.setCenter(text);


        host.getChildren().add(pane0);


        ctrl = new VBox();

        CheckBox readonly = new CheckBox("isReadonly");
        readonly.selectedProperty().bindBidirectional(isReadonly);
        ctrl.getChildren().add(readonly);

        Spinner<Double> val = new Spinner<>(new SpinnerValueFactory.DoubleSpinnerValueFactory(minimum.get(), maximum.get()));
        val.getValueFactory().valueProperty().bindBidirectional(value);
        ctrl.getChildren().addAll(new Label("Value"), val);

        Label max = new Label();
        max.textProperty().bind(Bindings.createStringBinding(() -> "Maximum: " + maximum.get(), maximum));
        ctrl.getChildren().addAll(max);

        Label min = new Label();
        min.textProperty().bind(Bindings.createStringBinding(() -> "Minimum: " + minimum.get(), minimum));
        ctrl.getChildren().addAll(min);

        Label small = new Label();
        small.textProperty().bind(Bindings.createStringBinding(() -> "SmallChange: " + smallIncrement.get(), smallIncrement));
        ctrl.getChildren().addAll(small);

        Label large = new Label();
        large.textProperty().bind(Bindings.createStringBinding(() -> "LargeChange: " + largeIncrement.get(), largeIncrement));
        ctrl.getChildren().addAll(large);
    }



    @Override
    public String getName() {
        return "IRangeValueProvider";
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
