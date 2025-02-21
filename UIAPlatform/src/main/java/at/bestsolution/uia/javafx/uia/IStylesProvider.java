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

import javafx.scene.paint.Color;

/**
 * Provides access to the visual styles associated with the content of a document.
 */
public interface IStylesProvider extends IInitable {

	/**
	 * context object for IStylesProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class StylesProviderContext {
		public final IProperty<String> ExtendedProperties;
		public final IProperty<Color> FillColor;
		public final IProperty<Color> FillPatternColor;
		public final IProperty<String> FillPatternStyle;
		public final IProperty<String> Shape;
		public final IProperty<Integer> StyleId;
		public final IProperty<String> StyleName;

		public StylesProviderContext(IInitContext init, IStylesProvider provider) {
			ExtendedProperties = init.addProperty(StandardPropertyIds.UIA_StylesExtendedPropertiesPropertyId, provider::get_ExtendedProperties, StandardVariantConverters.BSTR_String);
			FillColor = init.addProperty(StandardPropertyIds.UIA_StylesFillColorPropertyId, provider::get_FillColor, StandardVariantConverters.I4_Color);
			FillPatternColor = init.addProperty(StandardPropertyIds.UIA_StylesFillPatternColorPropertyId, provider::get_FillPatternColor, StandardVariantConverters.I4_Color);
			FillPatternStyle = init.addProperty(StandardPropertyIds.UIA_StylesFillPatternStylePropertyId, provider::get_FillPatternStyle, StandardVariantConverters.BSTR_String);
			Shape = init.addProperty(StandardPropertyIds.UIA_StylesShapePropertyId, provider::get_Shape, StandardVariantConverters.BSTR_String);
			StyleId = init.addProperty(StandardPropertyIds.UIA_StylesStyleIdPropertyId, provider::get_StyleId, StandardVariantConverters.I4_Integer);
			StyleName = init.addProperty(StandardPropertyIds.UIA_StylesStyleNamePropertyId, provider::get_StyleName, StandardVariantConverters.BSTR_String);
		}
	}

	/**
	 * Contains additional properties that are not included in this control pattern, but that provide information about the document content that might be useful to the user.
	 * <p>
	 * The extended properties must be localized because they are intended to be consumed by the user.
	 * </p>
	 * @return the extended properties
	 */
	String get_ExtendedProperties();

	/**
	 * Specifies the fill color of an element in a document.
	 * @return the fill color
	 */
	Color get_FillColor();

	/**
	 * Specifies the color of the pattern used to fill an element in a document.
	 * @return the fill pattern color
	 */
	Color get_FillPatternColor();

	/**
	 * Specifies the fill pattern style of an element in a document.
	 * @return the fill pattern style
	 */
	String get_FillPatternStyle();

	/**
	 * Specifies the shape of an element in a document.
	 * @return the shape
	 */
	String get_Shape();

	/**
	 * Identifies the visual style of an element in a document.
	 * <p>
	 * A provider should use this property to expose style identifiers that are useful to client applications. For example, a provider might expose the StyleId_Title identifier for an element that represents the title of a presentation. A screen reader could then retrieve the StyleId property, discover that the element is a presentation title, and read the title to the user.
	 * </p>
	 * <p>
	 * List Styles
	 * IDs for list styles are supported starting with Windows 8.1.
	 * These styles should be applied at a paragraph level; all text that is part of a list item should have one of these styles applied to it.
	 * When bullet styles are mixed within a list, the BulletedList style should be applied to the whole range, and the BulletStyle attribute value (property identified by UIA_BulletStyleAttributeId) should be mixed according to breakdown of different bullet types within the range.
	 * When nested lists contain bullets also (perhaps of a different type than the main list), the BulletedList style would again be applied to the whole range, and the BulletStyle attribute value is whatever the nested bullet style is (for the range covering the nested list).
	 * </p>
	 * @return the style id
	 */
	int get_StyleId();

	/**
	 * Specifies the name of the visual style of an element in a document.
	 * <p>
	 * The style name typically indicates the role of the element in the document, such as "Heading 1."
	 * </p>
	 * @return the style name
	 */
	String get_StyleName();
}
