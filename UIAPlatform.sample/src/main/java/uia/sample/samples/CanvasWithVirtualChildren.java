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

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import at.bestsolution.uia.javafx.uia.ControlType;
import at.bestsolution.uia.javafx.uia.IToggleProvider;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualElement;
import at.bestsolution.uia.javafx.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.javafx.uia.ToggleState;
import at.bestsolution.uia.javafx.uia.UIA;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import uia.sample.Sample;

public class CanvasWithVirtualChildren implements Sample {

    private MyCanvas sample;

    private Node description;

    private Stream<IUIAElement> flatten(IUIAElement element) {
        if (element instanceof IUIAVirtualRootElement) {
            IUIAVirtualRootElement root = (IUIAVirtualRootElement) element;
            return Stream.concat(
                Stream.of(element),
                root.getChildren().stream().flatMap(this::flatten)
            );
        } else if (element instanceof IUIAVirtualElement) {
            IUIAVirtualElement node = (IUIAVirtualElement) element;
            return Stream.concat(
                Stream.of(element),
                node.getChildren().stream().flatMap(this::flatten)
            );
        } else {
            return Stream.of(element);
        }
    }

    private class MyCanvas extends Canvas {

        class UIACanvas implements IUIAVirtualRootElement {
            private List<IUIAElement> children = new ArrayList<>();

            @Override
            public List<IUIAElement> getChildren() {
                return children;
            }

            @Override
            public IUIAElement getChildFromPoint(Point2D point) {
                // we simply select the last candidate - it should be the top most one in our case
                List<IUIAElement> candidates = flatten(this).filter(child -> child.getBounds().contains(point)).collect(Collectors.toList());
                if (candidates.isEmpty()) {
                    return null;
                } else {
                    return candidates.get(candidates.size() - 1);
                }
            }

            @Override
            public ControlType getControlType() {
                return ControlType.UIA_CustomControlTypeId;
            }
            @Override
            public Bounds getBounds() {
                return MyCanvas.this.localToScreen(MyCanvas.this.getBoundsInLocal());
            }
            @Override
            public void SetFocus() {
            }
        }

        UIACanvas uia = new UIACanvas();

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }
    }

    private class MyRectangle implements IUIAVirtualElement {

        IUIAElement parent;
        List<IUIAElement> children = new ArrayList<>();

        Paint color;
        double x;
        double y;
        double w;
        double h;

        public MyRectangle(Paint color, double x, double y, double w, double h) {
            this.color = color;
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        void render(GraphicsContext ctx) {
            ctx.setFill(color);
            ctx.fillRect(x, y, w, h);
        }

        @Override
        public IUIAElement getParent() {
            return parent;
        }
        @Override
        public List<IUIAElement> getChildren() {
            return children;
        }
        @Override
        public ControlType getControlType() {
            return ControlType.UIA_PaneControlTypeId;
        }
        @Override
        public Bounds getBounds() {
            return sample.localToScreen(new BoundingBox(x, y, w, h));
        }
        @Override
        public void SetFocus() {
        }
    }

    private class ToggleRect extends MyRectangle implements IToggleProvider {

        public BooleanProperty on = new SimpleBooleanProperty();

        ToggleRect(double x, double y, double w, double h) {
            super(Color.RED, x, y, w, h);
            on.addListener((obs, ol, ne) -> withContext(IToggleProvider.ToggleProviderContext.class, context -> context.ToggleState.fireChanged(ol ? ToggleState.On : ToggleState.Off, ne ? ToggleState.On : ToggleState.Off)));
            on.addListener((obs, ol, ne) -> {
                color = ne ? Color.GREEN : Color.RED;
            });
        }

        @Override
        public void Toggle() {
            on.set(!on.get());
        }

        @Override
        public ToggleState get_ToggleState() {
            return on.get() ? ToggleState.On : ToggleState.Off;
        }

    }

    public CanvasWithVirtualChildren() {

        Label desc = new Label("A canvas with with basic virtual UIA Elements as children. The first rectangle also implements an IToggleProvider, which can be toggled via various UIA clients");
        desc.setWrapText(true);
        description = desc;

        sample = new MyCanvas();
        sample.setWidth(300);
        sample.setHeight(50);

        MyRectangle parent1 = new MyRectangle(new Color(1.0, 0, 0, 0.4), 70, 10, 125, 30);
        ToggleRect red = new ToggleRect(10, 10, 50, 30);
        MyRectangle green = new MyRectangle(Color.GREEN, 75, 15, 50, 20);
        MyRectangle blue = new MyRectangle(Color.BLUE, 140, 15, 50, 20);

        sample.uia.children.add(red); red.parent = sample.uia;
        sample.uia.children.add(parent1); parent1.parent = sample.uia;

        parent1.children.add(green); green.parent = parent1;
        parent1.children.add(blue); blue.parent = parent1;

        GraphicsContext ctx = sample.getGraphicsContext2D();
        Consumer<IUIAElement> render = new Consumer<IUIAElement>() {
            @Override
            public void accept(IUIAElement el) {
                if (el instanceof MyRectangle) {
                    ((MyRectangle) el).render(ctx);
                }
                if (el instanceof IUIAVirtualRootElement) {
                    ((IUIAVirtualRootElement) el).getChildren().forEach(this);
                } else if (el instanceof IUIAVirtualElement) {
                    ((IUIAVirtualElement) el).getChildren().forEach(this);
                }
            }
        };

        render.accept(sample.uia);

        red.on.addListener((obs, ol, ne) -> {
            red.render(ctx);
        });
    }

    @Override
    public String getName() {
        return "Canvas with virtual Children";
    }

    @Override
    public Node getDescription() {
        return description;
    }

    @Override
    public Node getSample() {
        return sample;
    }

    @Override
    public Node getControls() {
        return null;
    }

}
