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
 * Exposes the properties of an annotation in a document.
 */
public interface IAnnotationProvider extends IInitable {

    /**
	 * context object for IAnnotationProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class AnnotationProviderContext {
        public final IProperty<IAnnotationTypeId> AnnotationTypeId;
        public final IProperty<String> AnnotationTypeName;
        public final IProperty<String> Author;
        public final IProperty<String> DateTime;
        public final IProperty<IUIAElement> Target;

		public AnnotationProviderContext(IInitContext init, IAnnotationProvider provider) {
            AnnotationTypeId = init.addProperty(StandardPropertyIds.UIA_AnnotationAnnotationTypeIdPropertyId, provider::get_AnnotationTypeId, StandardVariantConverters.I4_INativeConstant(IAnnotationTypeId::fromNativeValue));
            AnnotationTypeName = init.addProperty(StandardPropertyIds.UIA_AnnotationAnnotationTypeNamePropertyId, provider::get_AnnotationTypeName, StandardVariantConverters.BSTR_String);
            Author = init.addProperty(StandardPropertyIds.UIA_AnnotationAuthorPropertyId, provider::get_Author, StandardVariantConverters.BSTR_String);
            DateTime = init.addProperty(StandardPropertyIds.UIA_AnnotationDateTimePropertyId, provider::get_DateTime, StandardVariantConverters.BSTR_String);
            Target = init.addProperty(StandardPropertyIds.UIA_AnnotationTargetPropertyId, provider::get_Target, StandardVariantConverters.UNKNOWN_IUIAElement);
        }
	}

    /**
     * The annotation type identifier of this annotation.
     * @return the annotation type identifier
     */
    IAnnotationTypeId get_AnnotationTypeId();

    /**
     * The name of this annotation type.
     * @return the name of this annotation type
     */
    String get_AnnotationTypeName();

    /**
     * The name of the annotation author.
     * @return the name of the author
     */
    String get_Author();

    /**
     * The date and time when this annotation was created.
     * @return the date and time of creation
     */
    String get_DateTime();

    /**
     * The UI Automation element that is being annotated.
     * @return the target
     */
    IUIAElement get_Target();
}
