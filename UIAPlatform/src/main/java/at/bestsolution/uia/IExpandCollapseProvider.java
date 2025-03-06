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

/**
 * Provides access to a control that visually expands to display content, and collapses to hide content.
 */
public interface IExpandCollapseProvider extends IInitable {

    /**
	 * context object for IExpandCollapseProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ExpandCollapseProviderContext {

        public final IProperty<ExpandCollapseState> ExpandCollapseState;

		public ExpandCollapseProviderContext(IInitContext init, IExpandCollapseProvider provider) {
            ExpandCollapseState = init.addProperty(StandardPropertyIds.UIA_ExpandCollapseExpandCollapseStatePropertyId, provider::get_ExpandCollapseState, StandardVariantConverters.I4_INativeEnum(at.bestsolution.uia.ExpandCollapseState::fromNativeValue));
        }
	}

    /**
     * Hides all child nodes, controls, or content of this element.
     */
    void Expand();

    /**
     * Displays all child nodes, controls, or content of the control.
     */
    void Collapse();

    /**
     * Indicates the state, expanded or collapsed, of the control.
     * @return the expand/collapse state
     */
    ExpandCollapseState get_ExpandCollapseState();
}
