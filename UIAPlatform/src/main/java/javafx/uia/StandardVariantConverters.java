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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;

import com.sun.glass.ui.uia.ProxyAccessibleRegistry;

/**
 * provides standard variant converters
 */
public class StandardVariantConverters {

    private static <T> IVariantConverter<T> createConverter(Function<T, Variant> toVariant, Function<Variant, T> toObject) {
        return new IVariantConverter<T>() {

            @Override
            public Variant toVariant(T value) {
                return toVariant.apply(value);
            }

            @Override
            public T toObject(Variant variant) {
                return toObject.apply(variant);
            }
        };
    }
    
    public static final IVariantConverter<Boolean> BOOL_Boolean = createConverter(value -> Variant.vt_bool(value), variant -> variant.getBoolVal());
    public static final IVariantConverter<Boolean> BOOL = BOOL_Boolean;

    public static final IVariantConverter<String> BSTR_String = createConverter(value -> Variant.vt_bstr(value), variant -> variant.getBstrVal());
    public static final IVariantConverter<String> BSTR = BSTR_String;

    public static final IVariantConverter<KeyCombination> BSTR_KeyCombination = createConverter(value -> Variant.vt_bstr(value.toString()), variant -> null); // TODO create UIA compatible Key String from KeyCombination

    public static final IVariantConverter<Bounds> R8Array_Bounds = createConverter(
        value -> Variant.vt_r8_array(new double[] {value.getMinX(), value.getMinY(), value.getWidth(), value.getHeight()}),
        variant -> new BoundingBox( variant.getDblSafeArray()[0], variant.getDblSafeArray()[1], variant.getDblSafeArray()[2], variant.getDblSafeArray()[3])
    );

    public static final IVariantConverter<Point2D> R8Array_Point2D = createConverter(
        value -> Variant.vt_r8_array(new double[] { value.getX(), value.getY() }),
        variant -> new Point2D(variant.getDblSafeArray()[0], variant.getDblSafeArray()[1])
    );

    public static final IVariantConverter<IUIAElement> UNKNOWN_IUIAElement = createConverter(
        value -> Variant.vt_unknown(ProxyAccessibleRegistry.getInstance().findAccessible(value).getNativeAccessible()),
        variant -> null // variant.getPunkVal() // TODO lookup via native accessible pointer for IUIAElement
    );

    public static final IVariantConverter<IUIAElement[]> UNKNOWNArray_IUIAElementArray = createConverter(
        value -> Variant.vt_unknown_array(Arrays.stream(value).mapToLong(element -> ProxyAccessibleRegistry.getInstance().findAccessible(element).getNativeAccessible()).toArray()),
        variant -> null // TODO punk val backmapping = ?
    );

    public static final IVariantConverter<Double> R8_Double = createConverter(value -> Variant.vt_r8(value), variant -> variant.getDblVal());

	public static final IVariantConverter<Color> I4_Color = createConverter(value -> { 
        int red = (int) Math.floor(value.getRed() * 255);
        int green = (int) Math.floor(value.getGreen() * 255);
        int blue = (int) Math.floor(value.getBlue() * 255);

        final int rgbRed   =  0x000000FF;
        final int rgbGreen =  0x0000FF00;
        final int rgbBlue  =  0x00FF0000;

        int color = (red   << 0) & rgbRed;
        color |= (green << 8) & rgbGreen;
        color |= (blue  << 16) & rgbBlue;

        // TODO how to handle alpha

        return Variant.vt_i4(color);
     }, variant -> {

        int color = variant.getLVal();

        final int rgbRed   =  0x000000FF;
        final int rgbGreen =  0x0000FF00;
        final int rgbBlue  =  0x00FF0000;

        int red = color & rgbRed;
        int green = (color & rgbGreen) >> 8;
        int blue = (color & rgbBlue) >> 16;

        return Color.rgb(red, green, blue);
     });

	public static final IVariantConverter<Integer> I4_Integer = createConverter(value -> Variant.vt_i4(value), variant -> variant.getLVal());
    
    private static double[] unbox(Double[] ary) {
        double[] result = new double[ary.length];
        for (int i = 0; i < ary.length; i++) {
            result[i] = ary[i];
        }
        return result;
    }
    private static Double[] box(double[] ary) {
        Double[] result = new Double[ary.length];
        for (int i = 0; i < ary.length; i++) {
            result[i] = ary[i];
        }
        return result;
    }
    
    public static final IVariantConverter<Double[]> R8Array_DoubleArray = createConverter(value -> Variant.vt_r8_array(unbox(value)), variant -> box(variant.getDblSafeArray()));

    public static final <T extends INativeEnum> IVariantConverter<T> I4_INativeEnum(Function<Integer, Optional<T>> enumResolver) {
        return createConverter(value -> Variant.vt_i4(value.getNativeValue()), variant -> enumResolver.apply(variant.getLVal()).orElse(null));
    }

    public static void main(String[] args) {
        Color value = Color.rgb(100, 200, 255);
        int red = (int) Math.floor(value.getRed() * 255);
        int green = (int) Math.floor(value.getGreen() * 255);
        int blue = (int) Math.floor(value.getBlue() * 255);

        final int rgbRed   =  0x000000FF;
        final int rgbGreen =  0x0000FF00;
        final int rgbBlue  =  0x00FF0000;

        int color = (red   << 0) & rgbRed;
        color |= (green << 8) & rgbGreen;
        color |= (blue  << 16) & rgbBlue;

        System.err.println(Integer.toHexString(color));
    }
}
