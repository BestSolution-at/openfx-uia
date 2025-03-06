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
package at.bestsolution.uia;

import java.util.Optional;
import java.util.stream.Stream;

/** Contains values that specify the type of change in the Microsoft UI Automation tree structure. */
public enum StructureChangeType implements INativeEnum {
    /** A child element was added to the UI Automation element tree. */
    ChildAdded(0),
    /** A child element was removed from the UI Automation element tree. */
    ChildRemoved(1),
    /** Child elements were invalidated in the UI Automation element tree. This might mean that one or more child elements were added or removed, or a combination of both. This value can also indicate that one subtree in the UI was substituted for another. For example, the entire contents of a dialog box changed at once, or the view of a list changed because an Explorer-type application navigated to another location. The exact meaning depends on the UI Automation provider implementation. */
    ChildrenInvalidated(2),
    /** Child elements were added in bulk to the UI Automation element tree. */
    ChildrenBulkAdded(3),
    /** Child elements were removed in bulk from the UI Automation element tree. */
    ChildrenBulkRemoved(4),
    /** The order of child elements has changed in the UI Automation element tree. Child elements may or may not have been added or removed. */
    ChildrenReordered(5);

    private int value;
    private StructureChangeType(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }

    @Override
    public String getConstantName() {
        return "StructureChangeType_" + name();
    }

    public static Optional<StructureChangeType> fromNativeValue(int nativeValue) {
        return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
    }
}
