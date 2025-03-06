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

public interface ITextProvider2 extends ITextProvider {

    class TextProvider2Context {

		public TextProvider2Context(IInitContext init, ITextProvider2 provider) {
            // empty for now
		}
	}

    static class CaretRangeResult {
        public final boolean isActive;
        public final ITextRangeProvider range;
        public CaretRangeResult(ITextRangeProvider range, boolean isActive) {
            this.range = range;
            this.isActive = isActive;
        }
    }

    CaretRangeResult GetCaretRange();
    ITextRangeProvider RangeFromAnnotation(IUIAElement annotationElement);

}
