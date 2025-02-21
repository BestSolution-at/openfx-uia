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

import at.bestsolution.uia.javafx.uia.IInitContext;
import at.bestsolution.uia.javafx.uia.IProperty;
import at.bestsolution.uia.javafx.uia.IUIAElement;
import at.bestsolution.uia.javafx.uia.UIA;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Label;
import uia.sample.Sample;

public class SimpleUIAElementWithProperties implements Sample {

    class MyLabelElementWithProperties implements IUIAElement {

        public IProperty<String> name;
        public IProperty<String> helpText;
        public IProperty<String> className;
        public IProperty<Point2D> clickablePoint;

        public IProperty<Boolean> isContentElement;
        public IProperty<Boolean> isControlElement;

        public IProperty<String> localizedControlType;

        String getName() {
            return content.getText();
        }

        String getHelpText() {
            return "This control was customized by using the UIA API";
        }

        String getClassName() {
            return "HighlySpecializedCustomControl";
        }

        boolean isControlElement() {
            return true;
        }

        boolean isContentElement() {
            return true;
        }

        String getLocalizedControlType() {
            return "MeinLabel";
        }

        @Override
        public void initialize(IInitContext init) {
            name = init.addNameProperty(this::getName);
            localizedControlType = init.addLocalizedControlTypeProperty(this::getLocalizedControlType);
            helpText = init.addHelpTextProperty(this::getHelpText);
            className = init.addClassNameProperty(this::getClassName);
            clickablePoint = init.addClickablePointProperty(this::getClickablePoint);
            isContentElement = init.addIsContentElementProperty(this::isContentElement);
            isControlElement = init.addIsControlElementProperty(this::isControlElement);
        }

        public Point2D getClickablePoint() {
            Bounds b = getBounds();
            return new Point2D(b.getMinX() + b.getWidth() / 2, b.getMinY() + b.getHeight() / 2);
        }

        @Override
        public Bounds getBounds() {
            return content.localToScreen(content.getBoundsInLocal());
        }

        @Override
        public void SetFocus() {
        }

    }

    class MyLabel extends Label {

        MyLabelElementWithProperties uia = new MyLabelElementWithProperties();

        MyLabel(String text) {
            super(text);

            // property change example
            boundsInLocalProperty().addListener((obs, oldBounds, newBounds) -> {
                // Note: this simple sample does not reflect the bounds changes when the window is moved, or the parent structure changes within the scene
                uia.withContext(IUIAElement.UIAElementContext.class, context -> context.BoundingRectangle.fireChanged(localToScreen(oldBounds), localToScreen(newBounds)));
            });

            textProperty().addListener((obs, oldText, newText) -> {
                uia.name.fireChanged(oldText, newText);
            });
        }

        @Override
        public Object queryAccessibleAttribute(AccessibleAttribute attribute, Object... parameters) {
            if (UIA.isUIAQuery(attribute, parameters)) {
                return uia;
            }
            return super.queryAccessibleAttribute(attribute, parameters);
        }

    }

    Label desc;
    MyLabel content;

    public SimpleUIAElementWithProperties() {
        desc = new Label("a basic IUIAElement with additional properties. The properties are registered by implementing the IUIAElement#initilize(IInitContext) method.");
        desc.setWrapText(true);
        content = new MyLabel("Hello UIA with properties");
    }

    @Override
    public String getName() {
        return "Simple UIA Element w/ properties";
    }

    @Override
    public Node getDescription() {
        return desc;
    }

    @Override
    public Node getSample() {
        return content;
    }

    @Override
    public Node getControls() {
        return null;
    }



}
