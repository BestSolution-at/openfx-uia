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
package at.bestsolution.uia.javafx.uia;

/**
 * Provides access to a text-based control (or an object embedded in text) that is a child or descendant of another text-based control.
 */
public interface ITextChildProvider {

    class TextChildProviderContext {

		public TextChildProviderContext(IInitContext init, ITextChildProvider provider) {
            // empty for now
		}
	}

    /**
     * Retrieves this element's nearest ancestor provider that supports the Text control pattern.
     * @return the nearest element with ITextProvider
     */
    IUIAElement get_TextContainer();

    /**
     * Retrieves a text range that encloses this child element.
     * @return the text range
     */
    ITextRangeProvider get_TextRange();

}
