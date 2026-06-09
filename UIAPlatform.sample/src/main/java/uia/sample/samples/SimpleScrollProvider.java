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
import java.util.Collections;
import java.util.List;

import at.bestsolution.uia.IScrollItemProvider;
import at.bestsolution.uia.IScrollProvider;
import at.bestsolution.uia.IUIAElement;
import at.bestsolution.uia.IUIAVirtualElement;
import at.bestsolution.uia.IUIAVirtualRootElement;
import at.bestsolution.uia.ScrollAmount;
import at.bestsolution.uia.UIA;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import uia.sample.Sample;

public class SimpleScrollProvider implements Sample {

    double scrollX = 30;
    double scrollY = 0;

    double viewportWidth = 100;
    double viewportHeight = 100;

    double contentWidth = 500;
    double contentHeight = 500;

    class ScrollItemElement implements /*IUIAElement,*/ IUIAVirtualElement, IScrollItemProvider {

        IUIAElement parent;

        Color color;
        Bounds area;
        Canvas container;

        @Override
        public void ScrollIntoView() {
            reveal(area);
        }

        @Override
        public IUIAElement getParent() {
            return parent;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public Bounds getBounds() {
            return container.localToScreen(area);
        }

        @Override
        public void SetFocus() {

        }

        public void render() {
            GraphicsContext gc = container.getGraphicsContext2D();
            gc.setFill(color);
            gc.fillRect(area.getMinX(), area.getMinY(), area.getWidth(), area.getHeight());
        }

    }

    class ScrollAreaElement implements /*IUIAElement,*/ IUIAVirtualRootElement, IScrollProvider {

        List<IUIAElement> children = new ArrayList<>();

        Canvas canvas;

        Pane pane;

        @Override
        public Bounds getBounds() {
            return pane.localToScreen(new BoundingBox(0, 0, viewportWidth, viewportHeight));
        }

        @Override
        public void SetFocus() {
        }

        @Override
        public boolean get_HorizontallyScrollable() {
            return viewportWidth < contentWidth;
        }

        @Override
        public double get_HorizontalScrollPercent() {
            return scrollX / (contentWidth - viewportWidth);
        }

        @Override
        public double get_HorizontalViewSize() {
            return viewportWidth;
        }

        @Override
        public boolean get_VerticallyScrollable() {
            return viewportHeight < contentHeight;
        }

        @Override
        public double get_VerticalScrollPercent() {
            return scrollY / (contentHeight - viewportHeight);
        }

        @Override
        public double get_VerticalViewSize() {
            return viewportHeight;
        }

        private double convert(ScrollAmount amount) {
            switch (amount) {
                case NoAmount:
                default:
                return 0;
                case SmallIncrement: return 10;
                case LargeIncrement: return 30;
                case SmallDecrement: return -10;
                case LargeDecrement: return -30;
            }
        }

        @Override
        public void Scroll(ScrollAmount horizontalAmount, ScrollAmount verticalAmount) {
            System.err.println("Scroll( " + horizontalAmount + ", " + verticalAmount + ")");

            scrollX += convert(horizontalAmount);
            scrollY += convert(verticalAmount);

            updateFX();
        }

        @Override
        public void SetScrollPercent(double horizontalPercent, double verticalPercent) {
            System.err.println("SetScrollPercent(" + horizontalPercent + ", " + verticalPercent + ")");

            scrollX = toXOffset(horizontalPercent);
            scrollY = toYOffset(verticalPercent);

            updateFX();
        }

        @Override
        public List<IUIAElement> getChildren() {
            return children;
        }

        @Override
        public IUIAElement getChildFromPoint(Point2D point) {
            // TODO Auto-generated method stub
            return null;
        }


        public void render() {

            GraphicsContext gc = canvas.getGraphicsContext2D();

            // gc.setFill(Color.YELLOWGREEN);
            gc.clearRect(0, 0, 500, 500);

            for (IUIAElement el : children) {
                if (el instanceof ScrollItemElement) {
                    ((ScrollItemElement)el).render();
                }
            }
        }

    }

    private double toYOffset(double yPercent) {
        return yPercent * (contentHeight - viewportHeight);
    }
    private double toXOffset(double xPercent) {
        return xPercent * (contentWidth - viewportWidth);
    }
    private double toYPercent(double yOffset) {
        return yOffset / (contentHeight - viewportHeight);
    }
    private double toXPercent(double xOffset) {
        return xOffset / (contentWidth - viewportWidth);
    }

    private void reveal(Bounds area) {
        System.err.println("reveal " + area);


        // y
        double yBegin = scrollY;
        double yEnd = scrollY + viewportHeight;

        if (area.getMinY() < yBegin) {
            scrollY = area.getMinY();
        } else if (area.getMaxY() > yEnd) {
            scrollY = area.getMaxY() - viewportHeight;
        }


        // x
        double xBegin = scrollX;
        double xEnd = scrollX + viewportWidth;

        if (area.getMinX() < xBegin) {
            scrollX = area.getMinX();
        } else if (area.getMaxX() > xEnd) {
            scrollX = area.getMaxX() - viewportWidth;
        }

        updateFX();
    }



    Node sample;

    Label desc;

    Node control;

    Pane viewPort;
    Node content;
    private ScrollBar hScroll;
    private ScrollBar vScroll;

    public SimpleScrollProvider() {
        desc = new Label("Basic usage of IScrollProvider and IScrollItemProvider");
        desc.setWrapText(true);

        ScrollAreaElement scrollEl = new ScrollAreaElement();
        Canvas canvas = new Canvas(500, 500);

        {
            scrollEl.canvas = canvas;


            // ScrollItemElement boxOne = new ScrollItemElement();
            // boxOne.color = Color.GREEN;
            // boxOne.area = new BoundingBox(15, 15, 100, 30);
            // boxOne.parent = scrollEl;
            // boxOne.container = canvas;
            // scrollEl.children.add(boxOne);


            // ScrollItemElement boxTwo = new ScrollItemElement();
            // boxTwo.color = Color.RED;
            // boxTwo.area = new BoundingBox(15, 300, 100, 30);
            // boxTwo.parent = scrollEl;
            // boxTwo.container = canvas;
            // scrollEl.children.add(boxTwo);


            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 10; j++) {

                    double x = 5 + i * 100;
                    double y = 5 + j * 50;
                    double w = 90;
                    double h = 40;

                    int a = 100 + (int) Math.round(i / 5d * 155);
                    int b = 100 + (int) Math.round(j / 10d * 155);

                    ScrollItemElement box = new ScrollItemElement();
                    box.color = Color.rgb(a, b, 0);
                    box.area = new BoundingBox(x, y, w, h);
                    box.parent = scrollEl;
                    box.container = canvas;
                    scrollEl.children.add(box);

                }
            }



            scrollEl.render();
        }

        content = canvas;

        viewPort = new Pane() {
            {
                scrollEl.pane = this;
            }
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
                if (UIA.isUIAQuery(attribute, parameters)) {
                    return scrollEl;
                }
                return super.queryAccessibleAttribute(attribute, parameters);
            }
        };
        viewPort.getChildren().add(canvas);
        viewPort.setStyle("-fx-border-width: 1px; -fx-border-color: red; -fx-border-style: solid;");

        Rectangle mask = new Rectangle();
        mask.widthProperty().bind(viewPort.widthProperty());
        mask.heightProperty().bind(viewPort.heightProperty());
        viewPort.setClip(mask);



        hScroll = new ScrollBar();
        hScroll.setOrientation(Orientation.HORIZONTAL);
        vScroll = new ScrollBar();
        vScroll.setOrientation(Orientation.VERTICAL);

        hScroll.setMin(0);
        hScroll.setMax(1);
        vScroll.setMin(0);
        vScroll.setMax(1);

        updateFX();

        vScroll.valueProperty().addListener((obs, ol, ne) -> {
            scrollY = toYOffset(ne.doubleValue());
            updateFX();
        });

        hScroll.valueProperty().addListener((obs, ol, ne) -> {
            scrollX = toXOffset(ne.doubleValue());
            updateFX();
        });


        GridPane pane = new GridPane();

        pane.add(viewPort, 0, 0);
        pane.add(hScroll, 0, 1);
        pane.add(vScroll, 1, 0);

        sample = pane;

    }

    private void updateFX() {
        viewPort.setMinSize(viewportWidth, viewportHeight);
        viewPort.setMaxSize(viewportWidth, viewportHeight);

        content.setTranslateX(-scrollX);
        content.setTranslateY(-scrollY);

        hScroll.setValue(toXPercent(scrollX));
        vScroll.setValue(toYPercent(scrollY));
    }

    @Override
    public String getName() {
        return "IScrollProvider & IScrollItemProvider";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return sample;
    }

    @Override
    public Node getControls() {
        return control;
    }



}
