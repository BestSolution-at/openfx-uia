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

/**
 * Provides access to controls that can be set to a value within a range.
 */
public interface IRangeValueProvider extends IInitable {

	/**
	 * context object for IRangeValueProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class RangeValueProviderContext {
		public final IProperty<Boolean> IsReadonly;
		public final IProperty<Double> Value;
		public final IProperty<Double> LargeChange;
		public final IProperty<Double> SmallChange;
		public final IProperty<Double> Maximum;
		public final IProperty<Double> Minimum;

		public RangeValueProviderContext(IInitContext init, IRangeValueProvider provider) {
			IsReadonly = init.addProperty(StandardPropertyIds.UIA_RangeValueIsReadOnlyPropertyId, provider::get_IsReadOnly, StandardVariantConverters.BOOL_Boolean);
			Value = init.addProperty(StandardPropertyIds.UIA_RangeValueValuePropertyId, provider::get_Value, StandardVariantConverters.R8_Double);
			LargeChange = init.addProperty(StandardPropertyIds.UIA_RangeValueLargeChangePropertyId, provider::get_LargeChange, StandardVariantConverters.R8_Double);
			SmallChange = init.addProperty(StandardPropertyIds.UIA_RangeValueSmallChangePropertyId, provider::get_SmallChange, StandardVariantConverters.R8_Double);
			Maximum = init.addProperty(StandardPropertyIds.UIA_RangeValueMaximumPropertyId, provider::get_Maximum, StandardVariantConverters.R8_Double);
			Minimum = init.addProperty(StandardPropertyIds.UIA_RangeValueMinimumPropertyId, provider::get_Minimum, StandardVariantConverters.R8_Double);
		}
	}
	
	/**
	 * Indicates whether the value of a control is read-only.
	 * @return true if readonly
	 */
	boolean get_IsReadOnly();

	/**
	 * Specifies the value that is added to or subtracted from the IRangeValueProvider::Value property when a large change is made, such as when the PAGE DOWN key is pressed.
	 * <p>
	 * The LargeChange property can support Not a Number (NaN) value. When returning a NaN value, the provider should return a quiet (non-signaling) NaN to avoid raising an exception if floating-point exceptions are turned on. The following example shows how to create a quiet NaN:
	 * </p>
	 * <code>
	 * ULONGLONG ulNaN = 0xFFFFFFFFFFFFFFFF;
     * *pRetVal = *reinterpret_cast<double*>(&ulNaN);
	 * </code>
	 * @return the large change
	 */
	double get_LargeChange();

	/**
	 * Specifies the maximum range value supported by the control.
	 * <p>
	 * This value should be greater than Minimum.
	 * </p>
	 * @return the maximum
	 */
	double get_Maximum();

	/**
	 * Specifies the minimum range value supported by the control.
	 * <p>
	 * This value should be less than Maximum.
	 * </p>
	 * @return the minimum
	 */
	double get_Minimum();

	/**
	 * Specifies the value that is added to or subtracted from the IRangeValueProvider::Value property when a small change is made, such as when an arrow key is pressed.
	 * <p>
	 * The SmallChange property can support Not a Number (NaN) value. When returning a NaN value, the provider should return a quiet (non-signaling) NaN to avoid raising an exception if floating-point exceptions are turned on. The following example shows how to create a quiet NaN:
	 * </p>
	 * <code>
	 * ULONGLONG ulNaN = 0xFFFFFFFFFFFFFFFF;
	 * *pRetVal = *reinterpret_cast<double*>(&ulNaN);
	 * </code>
	 * @return
	 */
	double get_SmallChange();

	/**
	 * The value of the control.
	 * @return the value
	 */
	double get_Value();

	/**
	 * Sets the value of control.
	 * <p>
	 * The actual value set depends on the control implementation. The control may round the requested value up or down.
	 * </p>
	 * @param value the value
	 */
	void SetValue(double value);

}
