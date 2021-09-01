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
 * Provides access to controls that act as containers for a collection of child elements. 
 * The children of this element must implement ITableItemProvider and be organized in a two-dimensional 
 * logical coordinate system that can be traversed by using the keyboard.
 * <p>
 * This control pattern is analogous to IGridProvider with the distinction that any control that implements 
 * ITableProvider must also expose a column and/or row header relationship for each child element.<br/>
 * Controls that implement ITableProvider are also required to implement IGridProvider so as to expose the 
 * inherent grid functionality of a table control.<br/>
 * Implemented on a UI Automation provider that must support the Table control pattern and Grid control pattern.
 * </p>
 */
public interface ITableProvider extends IInitable {

	/**
	 * context object for ITableProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class TableProviderContext {
		public final IProperty<RowOrColumnMajor> RowOrColumnMajor;
		public final IProperty<IUIAElement[]> ColumnHeaders;
		public final IProperty<IUIAElement[]> RowHeaders;

		public TableProviderContext(IInitContext init, ITableProvider tableProvider) {
			RowOrColumnMajor = init.addProperty(StandardPropertyIds.UIA_TableRowOrColumnMajorPropertyId, tableProvider::get_RowOrColumnMajor, StandardVariantConverters.I4_INativeEnum(javafx.uia.RowOrColumnMajor::fromNativeValue));
			ColumnHeaders = init.addProperty(StandardPropertyIds.UIA_TableColumnHeadersPropertyId, tableProvider::GetColumnHeaders, StandardVariantConverters.UNKNOWNArray_IUIAElementArray);
			RowHeaders = init.addProperty(StandardPropertyIds.UIA_TableRowHeadersPropertyId, tableProvider::GetRowHeaders, StandardVariantConverters.UNKNOWNArray_IUIAElementArray);
		}
	}
	
	/**
	 * Specifies the primary direction of traversal for the table.
	 * @return the RowOrColumnMajor
	 */
	RowOrColumnMajor get_RowOrColumnMajor();

	/**
	 * Gets a collection of Microsoft UI Automation providers that represents all the column headers in a table.
	 * @return the column header items
	 */
	IUIAElement[] GetColumnHeaders();

	/**
	 * Gets a collection of Microsoft UI Automation providers that represents all the row headers in a table.
	 * @return the row header items
	 */
	IUIAElement[] GetRowHeaders();

	

}
