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
 * Provides access to controls that have an intrinsic value that does not span a range, and that can be represented as a string.
 */
public interface IValueProvider extends IInitable {

	/**
	 * context object for IValueProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class ValueProviderContext {
		public final IProperty<Boolean> IsReadonly;
		public final IProperty<String> Value;

		public ValueProviderContext(IInitContext init, IValueProvider provider) {
			IsReadonly = init.addProperty(StandardPropertyIds.UIA_ValueIsReadOnlyPropertyId, provider::get_IsReadOnly, StandardVariantConverters.BOOL_Boolean);
			Value = init.addProperty(StandardPropertyIds.UIA_ValueValuePropertyId, provider::get_Value, StandardVariantConverters.BSTR_String);
		}
	}

	/**
	 * Indicates whether the value of a control is read-only.
	 * <p>
	 * A control should have its IsEnabled property (UIA_IsEnabledPropertyId) set to TRUE and its IValueProvider::IsReadOnly property set to FALSE before allowing a call to IValueProvider::SetValue.
	 * </p>
	 * @return true if readonly
	 */
	boolean get_IsReadOnly();

	/**
	 * The value of the control.
	 * <p>
	 * Single-line edit controls support programmatic access to their contents by implementing IValueProvider (in addition to ITextProvider). However, multi-line edit controls do not implement IValueProvider.
	 * To retrieve the textual contents of multi-line edit controls, the controls must implement ITextProvider. However, ITextProvider does not support setting the value of a control.
	 * IValueProvider does not support the retrieval of formatting information or substring values. Implement ITextProvider in these scenarios.
	 * </p>
	 * @return the value
	 */
	String get_Value();

	/**
	 * Sets the value of control.
	 * <p>
	 * Single-line edit controls support programmatic access to their contents by implementing IValueProvider. However, multi-line edit controls do not implement IValueProvider; instead they provide access to their content by implementing ITextProvider.
	 * Controls such as ListItem and TreeItem must implement IValueProvider if the value of any of the items is editable, regardless of the current edit mode of the control. The parent control must also implement IValueProvider if the child items are editable.
	 * </p>
	 * @param value the value
	 */
	void SetValue(String value);

}
