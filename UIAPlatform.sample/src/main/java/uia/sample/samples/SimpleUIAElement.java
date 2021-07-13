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

import javafx.geometry.Bounds;
import javafx.scene.AccessibleAttribute;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.uia.IUIAElement;
import javafx.uia.UIA;
import uia.sample.Sample;

public class SimpleUIAElement implements Sample {

    class MyLabel extends Label {
        IUIAElement uia = new IUIAElement() {

            @Override
            public Bounds getBounds() {
                return content.localToScreen(content.getBoundsInLocal());
            }
    
            @Override
            public void SetFocus() {
            }
     
        };

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

    public SimpleUIAElement() {
        desc = new Label("the basic usage of IUIAElement. A Label which a11y calls are handled by openfx-uia instead of vanilla glass a11y. Since no providers are registered there is less functionality than with glass a11y");
        desc.setWrapText(true);

        content = new MyLabel();
        content.setText("Hello UIA");
    }

    @Override
    public String getName() {
        return "Simple UIA Element";
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
