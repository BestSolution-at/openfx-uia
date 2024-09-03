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
package javafx.uia;

import java.util.List;

import javafx.geometry.Point2D;

/**
 * marks an IUIAElement to be a virtual root.
 * <p>Its descendents must be IUIAVirtualElements</p>
 */
public interface IUIAVirtualRootElement extends IUIAElement {
    
    /**
     * 
     * @return the element that has the input focus or null
     */
    default IUIAElement getFocus() {
        return null;
    }


    /**
     * 
     * @return
     *   the virtual children of this element.
     *   <p>Note: if an element has children its JavaFX children are ignored.</p>
     */
    List<IUIAElement> getChildren();

    /**
     * @param point the coordinates in screen space
     * @return the virtual child under the point
     */
    IUIAElement getChildFromPoint(Point2D point);

}
