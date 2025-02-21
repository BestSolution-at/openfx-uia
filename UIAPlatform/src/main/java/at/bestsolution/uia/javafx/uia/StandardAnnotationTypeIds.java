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

import java.util.Optional;
import java.util.stream.Stream;

/**
 * constants that identify annnotation type ids
 */
public enum StandardAnnotationTypeIds implements IAnnotationTypeId {
    /** An advanced proofing issue. */
    AdvancedProofingIssue(60020),
    /** The author of the document. */
    Author(60019),
    /** A circular reference error that occurred. */
    CircularReferenceError(60022),
    /** A comment. Comments can take different forms depending on the application. */
    Comment(60003),
    /** A conflicting change that was made to the document. */
    ConflictingChange(60018),
    /** A data validation error that occurred. */
    DataValidationError(60021),
    /** A deletion change that was made to the document. */
    DeletionChange(60012),
    /** An editing locked change that was made to the document. */
    EditingLockedChange(60016),
    /** The endnote for a document. */
    Endnote(60009),
    /** An external change that was made to the document. */
    ExternalChange(60017),
    /** The footer for a page in a document. */
    Footer(60007),
    /** The footnote for a page in a document. */
    Footnote(60010),
    /** A format change that was made. */
    FormatChange(60014),
    /** An error in a formula. Formula errors typically include red text and exclamation marks. */
    FormulaError(60004),
    /** A grammatical error, often denoted by a green squiggly line.  */
    GrammarError(60002),
    /** The header for a page in a document. */
    Header(60006),
    /** Highlighted content, typically denoted by a contrasting background color. */
    Highlighted(60008),
    /** An insertion change that was made to the document. */
    InsertionChange(60011),
    /** A text range containing mathematics. */
    Mathematics(60023),
    /** A move change that was made to the document. */
    MoveChange(60013),
    /** A spelling error, often denoted by a red squiggly line. */
    SpellingError(60001),
    /** A change that was made to the document. */
    TrackChanges(60005),
    /** The annotation type is unknown. */
    Unknown(60000),
    /** An unsynced change that was made to the document. */
    UnsyncedChange(60015)
    ;

    private final int value;
    private StandardAnnotationTypeIds(int value) {
        this.value = value;
    }

    @Override
    public int getNativeValue() {
        return value;
    }
    @Override
    public String getConstantName() {
        return "AnnotationType_" + name();
    }

    public static Optional<IAnnotationTypeId> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue)
            .map(val -> (IAnnotationTypeId) val)
            .findFirst();
	}
}
