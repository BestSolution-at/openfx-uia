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

import java.util.function.Supplier;

import javafx.scene.paint.Color;

public interface ITextAttributeSupport {



    <T> ITextAttribute<T> addAttribute(ITextAttributeId id, Supplier<TextAttributeValue<T>> getter, IVariantConverter<T> converter, FindAttribute<T> findAttribute);

    default ITextAttribute<Double> addAfterParagraphSpacingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_AfterParagraphSpacingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<Double> addBeforeParagraphSpacingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_BeforeParagraphSpacingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<BulletStyle> addBulletStyleAttribute(Supplier<TextAttributeValue<BulletStyle>> getter, FindAttribute<BulletStyle> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_BulletStyleAttributeId, getter, StandardVariantConverters.I4_INativeEnum(BulletStyle::fromNativeValue), findAttribute);
    }

    default ITextAttribute<OutlineStyles> addOutlineStylesAttribute(Supplier<TextAttributeValue<OutlineStyles>> getter, FindAttribute<OutlineStyles> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_OutlineStylesAttributeId, getter, StandardVariantConverters.I4_INativeEnum(OutlineStyles::fromNativeValue), findAttribute);
    }

    default ITextAttribute<Color> addOverlineColorAttribute(Supplier<TextAttributeValue<Color>> getter, FindAttribute<Color> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_OverlineColorAttributeId, getter, StandardVariantConverters.I4_Color, findAttribute);
    }
    default ITextAttribute<TextDecorationLineStyle> addOverlineStyleAttribute(Supplier<TextAttributeValue<TextDecorationLineStyle>> getter, FindAttribute<TextDecorationLineStyle> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_OverlineStyleAttributeId, getter, StandardVariantConverters.I4_INativeEnum(TextDecorationLineStyle::fromNativeValue), findAttribute);
    }

    default ITextAttribute<Color> addStrikethroughColorAttribute(Supplier<TextAttributeValue<Color>> getter, FindAttribute<Color> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_StrikethroughColorAttributeId, getter, StandardVariantConverters.I4_Color, findAttribute);
    }
    default ITextAttribute<TextDecorationLineStyle> addStrikethroughStyleAttribute(Supplier<TextAttributeValue<TextDecorationLineStyle>> getter, FindAttribute<TextDecorationLineStyle> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_StrikethroughStyleAttributeId, getter, StandardVariantConverters.I4_INativeEnum(TextDecorationLineStyle::fromNativeValue), findAttribute);
    }

    default ITextAttribute<Color> addUnderlineColorAttribute(Supplier<TextAttributeValue<Color>> getter, FindAttribute<Color> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_UnderlineColorAttributeId, getter, StandardVariantConverters.I4_Color, findAttribute);
    }
    default ITextAttribute<TextDecorationLineStyle> addUnderlineStyleAttribute(Supplier<TextAttributeValue<TextDecorationLineStyle>> getter, FindAttribute<TextDecorationLineStyle> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_UnderlineStyleAttributeId, getter, StandardVariantConverters.I4_INativeEnum(TextDecorationLineStyle::fromNativeValue), findAttribute);
    }

    default ITextAttribute<ActiveEnd> addSelectionActiveEndAttribute(Supplier<TextAttributeValue<ActiveEnd>> getter, FindAttribute<ActiveEnd> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_SelectionActiveEndAttributeId, getter, StandardVariantConverters.I4_INativeEnum(ActiveEnd::fromNativeValue), findAttribute);
    }

    default ITextAttribute<Integer> addStyleIdAttribute(Supplier<TextAttributeValue<Integer>> getter, FindAttribute<Integer> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_StyleIdAttributeId, getter, StandardVariantConverters.I4_Integer, findAttribute);
    }

    default ITextAttribute<Double[]> addTabsAttribute(Supplier<TextAttributeValue<Double[]>> getter, FindAttribute<Double[]> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_TabsAttributeId, getter, StandardVariantConverters.R8Array_DoubleArray, findAttribute);
    }

    default ITextAttribute<String> addStyleNameAttribute(Supplier<TextAttributeValue<String>> getter, FindAttribute<String> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_StyleNameAttributeId, getter, StandardVariantConverters.BSTR, findAttribute);
    }

    default ITextAttribute<CaretBidiMode> addCaretBidiModeAttribute(Supplier<TextAttributeValue<CaretBidiMode>> getter, FindAttribute<CaretBidiMode> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_CaretBidiModeAttributeId, getter, StandardVariantConverters.I4_INativeEnum(CaretBidiMode::fromNativeValue), findAttribute);
    }

    default ITextAttribute<CaretPosition> addCaretPositionAttribute(Supplier<TextAttributeValue<CaretPosition>> getter, FindAttribute<CaretPosition> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_CaretPositionAttributeId, getter, StandardVariantConverters.I4_INativeEnum(CaretPosition::fromNativeValue), findAttribute);
    }

    default ITextAttribute<String> addCultureAttribute(Supplier<TextAttributeValue<String>> getter, FindAttribute<String> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_CultureAttributeId, getter, StandardVariantConverters.BSTR, findAttribute);
    }

    default ITextAttribute<Double> addHorizontalTextAlignmentAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_HorizontalTextAlignmentAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<Double> addIndentationFirstLineAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IndentationFirstLineAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<Double> addIndentationLeadingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IndentationLeadingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<Double> addIndentationTrailingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IndentationTrailingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<Boolean> addIsActiveAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsActiveAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<Boolean> addIsItalicAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsItalicAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<Boolean> addIsHiddenAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsHiddenAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<Boolean> addIsSubscriptAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsSubscriptAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<Boolean> addIsSuperscriptAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsSuperscriptAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<Boolean> addIsReadOnlyAttribute(Supplier<TextAttributeValue<Boolean>> getter, FindAttribute<Boolean> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_IsReadOnlyAttributeId, getter, StandardVariantConverters.BOOL_Boolean, findAttribute);
    }

    default ITextAttribute<String> addLineSpacingAttribute(Supplier<TextAttributeValue<String>> getter, FindAttribute<String> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_LineSpacingAttributeId, getter, StandardVariantConverters.BSTR, findAttribute);
    }

    default ITextAttribute<Double> addMarginBottomAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_MarginBottomAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }
    default ITextAttribute<Double> addMarginLeadingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_MarginLeadingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }
    default ITextAttribute<Double> addMarginTopAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_MarginTopAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }
    default ITextAttribute<Double> addMarginTrailingAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_MarginTrailingAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<AnimationStyle> addAnimationStyleAttribute(Supplier<TextAttributeValue<AnimationStyle>> getter, FindAttribute<AnimationStyle> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_AnimationStyleAttributeId, getter, StandardVariantConverters.I4_INativeEnum(AnimationStyle::fromNativeValue), findAttribute);
    }

    default ITextAttribute<Color> addBackgroundColorAttribute(Supplier<TextAttributeValue<Color>> getter, FindAttribute<Color> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_BackgroundColorAttributeId, getter, StandardVariantConverters.I4_Color, findAttribute);
    }

    default ITextAttribute<Color> addForegroundColorAttribute(Supplier<TextAttributeValue<Color>> getter, FindAttribute<Color> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_ForegroundColorAttributeId, getter, StandardVariantConverters.I4_Color, findAttribute);
    }

    default ITextAttribute<Double> addFontSizeAttribute(Supplier<TextAttributeValue<Double>> getter, FindAttribute<Double> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_FontSizeAttributeId, getter, StandardVariantConverters.R8_Double, findAttribute);
    }

    default ITextAttribute<String> addFontNameAttribute(Supplier<TextAttributeValue<String>> getter, FindAttribute<String> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_FontNameAttributeId, getter, StandardVariantConverters.BSTR, findAttribute);
    }

    default ITextAttribute<Integer> addFontWeightAttribute(Supplier<TextAttributeValue<Integer>> getter, FindAttribute<Integer> findAttribute) {
        return addAttribute(StandardTextAttributeIds.UIA_FontWeightAttributeId, getter, StandardVariantConverters.I4_Integer, findAttribute);
    }

}
