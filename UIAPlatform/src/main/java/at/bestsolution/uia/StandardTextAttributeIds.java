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

/**
 * This topic describes the named constants used to identify text attributes of a Microsoft UI Automation text range. These constants are used with the following methods:
 */
public enum StandardTextAttributeIds implements ITextAttributeId, INativeEnum {
    /** Identifies the AfterParagraphSpacing text attribute, which specifies the size of spacing after the paragraph.
     * Variant type: VT_R8
     * Default value: 0 */
    UIA_AfterParagraphSpacingAttributeId(40042),
    /** Identifies the AnimationStyle text attribute, which specifies the type of animation applied to the text. This attribute is specified as a value from the AnimationStyle enumerated type.
     * Variant type: VT_I4
     * Default value: AnimationStyle_None  */
    UIA_AnimationStyleAttributeId(40000),
    /** Identifies the AnnotationObjects text attribute, which maintains an array of IUIAutomationElement2 interfaces, one for each element in the current text range that implements the Annotation control pattern. Each element might also implement other control patterns as needed to describe the annotation. For example, an annotation that is a comment would also support the Text control pattern. Supported starting with Windows 8.
     *  Variant type: VT_UNKNOWN
     *  Default value: empty array
    */
    UIA_AnnotationObjectsAttributeId(40032),
    /** Identifies the AnnotationTypes text attribute, which maintains a list of annotation type identifiers for a range of text. For a list of possible values, see Annotation Type Identifiers. Supported starting with Windows 8.
     *  Variant type: VT_ARRAY | VT_I4
     *  Default value: empty array
     */
    UIA_AnnotationTypesAttributeId(40031),
    /** Identifies the BackgroundColor text attribute, which specifies the background color of the text. This attribute is specified as a COLORREF; a 32-bit value used to specify an RGB or RGBA color.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_BackgroundColorAttributeId(40001),
    /** Identifies the BeforeParagraphSpacing text attribute, which specifies the size of spacing before the paragraph.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_BeforeParagraphSpacingAttributeId(40041),
    /** Identifies the BulletStyle text attribute, which specifies the style of bullets used in the text range. This attribute is specified as a value from the BulletStyle enumerated type.
     *  Variant type: VT_I4
     *  Default value: BulletStyle_None
     */
    UIA_BulletStyleAttributeId(40002),
    /** Identifies the CaretBidiMode text attribute, which indicates the direction of text flow in the text range. This attribute is specified as a value from the CaretBidiMode enumerated type. Supported starting with Windows 8.
     *  Variant type: VT_I4
     *  Default value: CaretBidiMode_LTR
     */
    UIA_CaretBidiModeAttributeId(40039),
    /** Identifies the CaretPosition text attribute, which indicates whether the caret is at the beginning or the end of a line of text in the text range. This attribute is specified as a value from the CaretPosition enumerated type. Supported starting with Windows 8.
     *  Variant type: VT_I4
     *  Default value: CaretPosition_Unknown
     */
    UIA_CaretPositionAttributeId(40038),
    /** Identifies the Culture text attribute, which specifies the locale of the text by locale identifier (LCID).
     *  Variant type: VT_I4
     *  Default value: locale of the application UI
     */
    UIA_CultureAttributeId(40004),
    /** Identifies the FontName text attribute, which specifies the name of the font. Examples: "Arial Black"; "Arial Narrow". The font name string is not localized.
     *  Variant type: VT_BSTR
     *  Default value: empty string
     */
    UIA_FontNameAttributeId(40005),
    /** Identifies the FontSize text attribute, which specifies the point size of the font.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_FontSizeAttributeId(40006),
    /** Identifies the FontWeight text attribute, which specifies the relative stroke, thickness, or boldness of the font. The FontWeight attribute is modeled after the lfWeight member of the GDI LOGFONT structure, and related standards, and can be one of the following values:
     * <ul>
     * <li>0 = DontCare</li>
     * <li>100 = Thin</li>
     * <li>200 = ExtraLight or UltraLight</li>
     * <li>300 = Light</li>
     * <li>400 = Normal or Regular</li>
     * <li>500 = Medium</li>
     * <li>600 = SemiBold</li>
     * <li>700 = Bold</li>
     * <li>800 = ExtraBold or UltraBold</li>
     * <li>900 = Heavy or Black</li>
     * </ul>
     * Variant type: VT_I4
     *  Default value: 0
     */
    UIA_FontWeightAttributeId(40007),
    /** Identifies the ForegroundColor text attribute, which specifies the foreground color of the text. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_ForegroundColorAttributeId(40008),
    /** Identifies the HorizontalTextAlignment text attribute, which specifies how the text is aligned horizontally. This attribute is specified as a value from the HorizontalTextAlignmentEnum enumerated type.
     *  Variant type: VT_I4
     *  Default value: HorizontalTextAlignment_Left
     */
    UIA_HorizontalTextAlignmentAttributeId(40009),
    /** Identifies the IndentationFirstLine text attribute, which specifies how far, in points, to indent the first line of a paragraph.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_IndentationFirstLineAttributeId(40010),
    /** Identifies the IndentationLeading text attribute, which specifies the leading indentation, in points.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_IndentationLeadingAttributeId(40011),
    /** Identifies the IndentationTrailing text attribute, which specifies the trailing indentation, in points.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_IndentationTrailingAttributeId(40012),
    /** Identifies the IsActive text attribute, which indicates whether the control that contains the text range has the keyboard focus (TRUE) or not (FALSE). Supported starting with Windows 8.
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsActiveAttributeId(40036),
    /** Identifies the IsHidden text attribute, which indicates whether the text is hidden (TRUE) or visible (FALSE).
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsHiddenAttributeId(40013),
    /** Identifies the IsItalic text attribute, which indicates whether the text is italic (TRUE) or not (FALSE).
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsItalicAttributeId(40014),
    /** Identifies the IsReadOnly text attribute, which indicates whether the text is read-only (TRUE) or can be modified (FALSE).
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsReadOnlyAttributeId(40015),
    /** Identifies the IsSubscript text attribute, which indicates whether the text is subscript (TRUE) or not (FALSE).
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsSubscriptAttributeId(40016),
    /** Identifies the IsSuperscript text attribute, which indicates whether the text is subscript (TRUE) or not (FALSE).
     *  Variant type: VT_BOOL
     *  Default value: FALSE
     */
    UIA_IsSuperscriptAttributeId(40017),
    /** Identifies the LineSpacing text attribute, which specifies the spacing between lines of text.
     *  Variant type: VT_BSTR
     *  Default value: "LineSpacingAttributeDefault"
     */
    UIA_LineSpacingAttributeId(40040),
    /** Identifies the Link text attribute, which contains the IUIAutomationTextRange interface of the text range that is the target of an internal link in a document. Supported starting with Windows 8.
     *  Variant type: VT_UNKNOWN
     *  Default value: NULL
     */
    UIA_LinkAttributeId(40035),
    /** Identifies the MarginBottom text attribute, which specifies the size, in points, of the bottom margin applied to the page associated with the text range.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_MarginBottomAttributeId(40018),
    /** Identifies the MarginLeading text attribute, which specifies the size, in points, of the leading margin applied to the page associated with the text range.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_MarginLeadingAttributeId(40019),
    /** Identifies the MarginTop text attribute, which specifies the size, in points, of the top margin applied to the page associated with the text range.
     *  Variant type: VT_R8
     *  Ddefault value: 0
     */
    UIA_MarginTopAttributeId(40020),
    /** Identifies the MarginTrailing text attribute, which specifies the size, in points, of the trailing margin applied to the page associated with the text range.
     *  Variant type: VT_R8
     *  Default value: 0
     */
    UIA_MarginTrailingAttributeId(40021),
    /** Identifies the OutlineStyles text attribute, which specifies the outline style of the text. This attribute is specified as a value from the OutlineStyles enumerated type.
     *  Variant type: VT_I4
     *  Default value: OutlineStyles_None
     */
    UIA_OutlineStylesAttributeId(40022),
    /** Identifies the OverlineColor text attribute, which specifies the color of the overline text decoration. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_OverlineColorAttributeId(40023),
    /** Identifies the OverlineStyle text attribute, which specifies the style of the overline text decoration. This attribute is specified as a value from the TextDecorationLineStyleEnum enumerated type.
     *  Variant type: VT_I4
     *  Default value: TextDecorationLineStyle_None
     */
    UIA_OverlineStyleAttributeId(40024),
    /** Identifies the SelectionActiveEnd text attribute, which indicates the location of the caret relative to a text range that represents the currently selected text. This attribute is specified as a value from the ActiveEnd enumeration. Supported starting with Windows 8.
     *  Variant type: VT_I4
     *  Default value: ActiveEnd_None
     */
    UIA_SelectionActiveEndAttributeId(40037),
    /** Identifies the StrikethroughColor text attribute, which specifies the color of the strikethrough text decoration. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_StrikethroughColorAttributeId(40025),
    /** Identifies the StrikethroughStyle text attribute, which specifies the style of the strikethrough text decoration. This attribute is specified as a value from the TextDecorationLineStyleEnum enumerated type.
     *  Variant type: VT_I4
     *  Default value: TextDecorationLineStyle_None
     */
    UIA_StrikethroughStyleAttributeId(40026),
    /** Identifies the StyleId text attribute, which indicates the text styles in use for a text range. For a list of possible values, see Style Identifiers. Supported starting with Windows 8.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_StyleIdAttributeId(40034),
    /** Identifies the StyleName text attribute, which identifies the localized name of the text style in use for a text range. Supported starting with Windows 8.
     *  Variant type: VT_BSTR
     *  Default value: empty string
     */
    UIA_StyleNameAttributeId(40033),
    /** Identifies the Tabs text attribute, which is an array specifying the tab stops for the text range. Each array element specifies a distance, in points, from the leading margin.
     *  Variant type: VT_ARRAY | VT_R8
     *  Default value: empty array
     */
    UIA_TabsAttributeId(40027),
    /** Identifies the TextFlowDirections text attribute, which specifies the direction of text flow. This attribute is specified as a combination of values from the FlowDirections enumerated type.
     *  Variant type: VT_I4
     *  Default value: FlowDirections_Default
     */
    UIA_TextFlowDirectionsAttributeId(40028),
    /** Identifies the UnderlineColor text attribute, which specifies the color of the underline text decoration. This attribute is specified as a COLORREF, a 32-bit value used to specify an RGB or RGBA color.
     *  Variant type: VT_I4
     *  Default value: 0
     */
    UIA_UnderlineColorAttributeId(40029),
    /** Identifies the UnderlineStyle text attribute, which specifies the style of the underline text decoration. This attribute is specified as a value from the TextDecorationLineStyleEnum enumerated type.
     *  Variant type: VT_I4
     *  Default value: TextDecorationLineStyle_None
     */
    UIA_UnderlineStyleAttributeId(40030),
    /** not documented */
    UIA_SayAsInterpretAsAttributeId(40043),
    /** Identifies the CapStyle text attribute, which specifies the capitalization style for the text. This attribute is specified as a value from the CapStyle enumerated type.
     *  Variant type: VT_I4
     *  Default value: CapStyle_None
     */
    UIA_CapStyleAttributeId(40003)
    ;

    private int nativeValue;

    private StandardTextAttributeIds(int nativeValue) {
        this.nativeValue = nativeValue;
    }

    @Override
    public int getNativeValue() {
        return nativeValue;
    }

    @Override
    public String getConstantName() {
        return name();
    }

    public static Optional<StandardTextAttributeIds> fromNativeValue(int nativeValue) {
		return Stream.of(values()).filter(value -> value.getNativeValue() == nativeValue).findFirst();
	}
}
