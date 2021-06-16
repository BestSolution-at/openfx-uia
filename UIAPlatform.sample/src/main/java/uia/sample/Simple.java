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



import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.uia.ControlType;
import javafx.uia.IRawElementProviderSimple;
import javafx.uia.ISimpleProvider;
import javafx.uia.ITextAttributeId;
import javafx.uia.ITextProvider;
import javafx.uia.ITextRangeProvider;
import javafx.uia.IToggleProvider;
import javafx.uia.IUIAElement;
import javafx.uia.SupportedTextSelection;
import javafx.uia.TextPatternRangeEndpoint;
import javafx.uia.TextUnit;
import javafx.uia.ToggleState;
import javafx.uia.UIA;
import javafx.uia.UIANode;
import javafx.uia.Variant;

public class Simple extends Application {

    static class MyToggleNode implements IUIAElement, IToggleProvider {
        private Supplier<Bounds> myBounds;
        BooleanProperty on = new SimpleBooleanProperty(false);
        
        IUIAElementEvents uiaEvents;
        IToggleProviderEvents toggleEvents;


        public MyToggleNode(Supplier<Bounds> myBounds) {
            this.myBounds = myBounds;
            on.addListener((obs, ol, ne) -> toggleEvents.notifyToggleStateChanged(ol ? ToggleState.On : ToggleState.Off, ne ? ToggleState.On : ToggleState.Off));
        }

        @Override
        public void initialize(IUIAElementEvents events) {
            this.uiaEvents = events;
        }
        @Override
        public void initialize(IToggleProviderEvents events) {
            this.toggleEvents = events;
        }

        @Override
        public IUIAElement getParent() {
            return null;
        }

        @Override
        public List<IUIAElement> getChildren() {
            return Collections.emptyList();
        }

        @Override
        public void SetFocus() {
            System.err.println("SetFocus on " + this);
        }

        @Override
        public Bounds getBounds() {
            return myBounds.get();
        }

        @Override
        public ControlType getControlType() {
            return ControlType.UIA_PaneControlTypeId;
        }

        public BooleanProperty onProperty() {
            return on;
        }

        @Override
        public void Toggle() {
            on.set(!on.get());
           
        }

        @Override
        public ToggleState get_ToggleState() {
            return on.get() ? ToggleState.On : ToggleState.Off;
        }

        @Override
        public String toString() {
            return "MyToggleNode";
        }
    }

    MyToggleNode myToggleNode;
    @Override
    public void start(Stage primaryStage) throws Exception {

        
        BorderPane root = new BorderPane() {
            {
                myToggleNode = new MyToggleNode(() -> localToScreen(getBoundsInLocal()));
            }
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... arguments) {
                if (UIA.isUIAQuery(attribute, arguments)) {
                    return myToggleNode;
                }
                return super.queryAccessibleAttribute(attribute, arguments);
            }
        };
        
        CheckBox cb = new CheckBox("on");
        cb.selectedProperty().bindBidirectional(myToggleNode.onProperty());

       

        
        
        

        root.setPadding(new Insets(20));
        root.setPrefSize(800, 600);

        VBox widgets = new VBox();
        widgets.setSpacing(20);
        widgets.getChildren().add(new TextField());
        widgets.getChildren().add(new Button("Button"));
        root.setTop(widgets);
        root.setCenter(createCanvas());
        root.setBottom(cb);

        Label fun = new Label();
        fun.getStyleClass().add("target");
        fun.setText("Fun");
        fun.setStyle("-fx-border-color: red; -fx-border-width: 1px");

        root.setLeft(fun);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Simple.class.getResource("/test.css").toExternalForm());

        primaryStage.setTitle("Simple");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    Canvas createCanvas() {

        IUIAElement uiaCanvas;

        class MyRect implements IUIAElement, ISimpleProvider {
            int x;
            int y;
            int width;
            int height;
            MyRect(int x, int y, int width, int height) {
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
            }

            @Override
            public void initialize(IUIAElementEvents events) {
            }

            void draw(GraphicsContext gc) {
                gc.setFill(Color.LIME);
                gc.fillRect(x, y, width, height);
            }


            @Override
            public ControlType getControlType() {
                return ControlType.UIA_PaneControlTypeId;
            }

            @Override
            public void SetFocus() {
            }

            @Override
            public Bounds getBounds() {
                return new BoundingBox(x, y, width, height);
            }

            @Override
            public boolean isContentElement() {
                return false;
            }

            @Override
            public boolean isControlElement() {
                return false;
            }

            @Override
            public List<IUIAElement> getChildren() {
                return Collections.emptyList();
            }

            @Override
            public IUIAElement getParent() {
                return null;
            }
        }

        MyRect rect0 = new MyRect(10, 10, 100, 100);


        class SimpleElement implements IUIAElement {

            private IUIAElement parent;
            private List<IUIAElement> children = new ArrayList<>();
            private Bounds bounds;

            private IUIAElementEvents uiaEvents;

            @Override
            public void initialize(IUIAElementEvents events) {
                this.uiaEvents = events;
            }

            public void setBounds(Bounds bounds) {
                Bounds oldBounds = this.bounds;
                this.bounds = bounds;
                if (uiaEvents != null) uiaEvents.notifyBoundsChanged(oldBounds, bounds);
            }
            @Override
            public IUIAElement getParent() {
                return parent;
            }

            @Override
            public List<IUIAElement> getChildren() {
                return children;
            }

            public void addChild(SimpleElement child) {
                child.parent = this;
                children.add(child);
                if (uiaEvents != null) uiaEvents.notifyStructureChanged();
            }

            @Override
            public ControlType getControlType() {
                return ControlType.UIA_PaneControlTypeId;
            }

            @Override
            public Bounds getBounds() {
                return bounds;
            }

            @Override
            public void SetFocus() {
            }

        }
        SimpleElement vRoot = new SimpleElement() {
            @Override
            public String toString() {
                return "vRoot";
            }
        };

        SimpleElement vChild1 = new SimpleElement() {
            @Override
            public String toString() {
                return "vChild1";
            }
        };
        vChild1.setBounds(new BoundingBox(10, 10, 100, 100));
        

        SimpleElement vChild2 = new SimpleElement() {
            @Override
            public String toString() {
                return "vChild2";
            }
        };
        vChild2.setBounds(new BoundingBox(10, 10, 100, 100));

        class SimpleTextRange implements ITextRangeProvider {
            String text = "Lorem Ipsum";
            int start = 0;
            int end = 10;

            public SimpleTextRange(int start, int end) {
                this.start = start;
                this.end = end;
            }

            @Override
            public void AddToSelection() {
            }

            @Override
            public ITextRangeProvider Clone() {
                return new SimpleTextRange(start, end);
            }

            @Override
            public boolean Compare(ITextRangeProvider other) {
                SimpleTextRange s = (SimpleTextRange) other;
                return start == s.start && end == s.end;
            }

            @Override
            public int CompareEndpoints(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange,
                    TextPatternRangeEndpoint targetEndpoint) {
                    SimpleTextRange s = (SimpleTextRange) targetRange;

                return 0;
            }

            @Override
            public void ExpandToEnclosingUnit(TextUnit unit) {
            }

            @Override
            public ITextRangeProvider FindAttribute(ITextAttributeId attributeId, Variant val, boolean backward) {
                return null;
            }

            @Override
            public ITextRangeProvider FindText(String text, boolean backward, boolean ignoreCase) {
                String rangeText = text.substring(start, end);
                if (ignoreCase) {
                    rangeText = rangeText.toLowerCase();
                    text = text.toLowerCase();
                }
                int index = -1;
                if (backward) {
                    index = rangeText.lastIndexOf(text);
                } else {
                    index = rangeText.indexOf(text);
                }
                if (index == -1) return null;
                return new SimpleTextRange(start + index, start + index + text.length());
            }

            @Override
            public Variant GetAttributeValue(ITextAttributeId attributeId) {
                return null;
            }

            @Override
            public Bounds[] GetBoundingRectangles() {
                return new Bounds[0];
            }

            @Override
            public IRawElementProviderSimple[] GetChildren() {
                return null;
            }

            @Override
            public IRawElementProviderSimple GetEnclosingElement() {
                return null;
            }

            @Override
            public String GetText(int maxLength) {
                int endOffset = maxLength != -1 ? Math.min(end, start + maxLength) : end;
                return text.substring(start, endOffset);
            }

            @Override
            public int Move(TextUnit unit, int count) {
                return 0;
            }

            @Override
            public void MoveEndpointByRange(TextPatternRangeEndpoint endpoint, ITextRangeProvider targetRange,
                    TextPatternRangeEndpoint targetEndpoint) {
            }

            @Override
            public int MoveEndpointByUnit(TextPatternRangeEndpoint endpoint, TextUnit unit, int count) {
                return 0;
            }

            @Override
            public void RemoveFromSelection() {
            }

            @Override
            public void ScrollIntoView(boolean alignToTop) {
            }

            @Override
            public void Select() {
            }
            
        }
        class Third extends UIANode implements ITextProvider {

            @Override
            public ITextRangeProvider get_DocumentRange() {
                return new SimpleTextRange(0, 10);
            }

            @Override
            public SupportedTextSelection get_SupportedTextSelection() {
                return SupportedTextSelection.None;
            }

            @Override
            public ITextRangeProvider[] GetSelection() {
                return new ITextRangeProvider[0];
            }

            @Override
            public ITextRangeProvider[] GetVisibleRanges() {
                return new ITextRangeProvider[0];
            }

            @Override
            public ITextRangeProvider RangeFromChild(IRawElementProviderSimple childElement) {
                
                return null;
            }
            
        }

        UIANode vChild3 = new Third() {
            @Override
            public String toString() {
                return "vChild3";
            }
        };
        vRoot.addChild(vChild1);
        vRoot.addChild(vChild2);
        //vRoot.getChildren().addAll(vChild1, vChild2, vChild3);

        //vRoot.controlTypeProperty().set(ControlType.UIA_PaneControlTypeId);

        Canvas canvas = new Canvas(500, 500) {
            @Override
            public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... arguments) {
                if (UIA.isUIAQuery(attribute, arguments)) {
                    return vRoot;
                }
                return super.queryAccessibleAttribute(attribute, arguments);
            }
       
            @Override
            public void relocate(double x, double y) {
                super.relocate(x, y);
                vChild1.setBounds(localToScreen(new BoundingBox(10, 10, 100, 100)));
                vChild2.setBounds(localToScreen(new BoundingBox(110, 10, 100, 100)));
                //vChild3.boundsProperty().set(localToScreen(new BoundingBox(220, 10, 100, 100)));
            }
        };

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        rect0.draw(gc);

       
        
        return canvas;
    }


    public static void main(String[] args) {
        launch(args);
    }

}