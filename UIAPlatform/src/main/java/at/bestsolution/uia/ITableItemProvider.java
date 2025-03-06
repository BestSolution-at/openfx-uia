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
 * Provides access to child controls of containers that implement ITableProvider.
 * <p>
 * This control pattern is analogous to IGridItemProvider with the distinction that any control
 * implementing ITableItemProvider must expose the relationship between the individual cell and its row and column information.<br>
 * Access to individual cell functionality is provided by the concurrent implementation of IGridItemProvider.<br>
 * Implemented on a UI Automation provider that must support the TableItem control pattern.
 * </p>
 */
public interface ITableItemProvider extends IInitable {

	/**
	 * context object for ITableItemProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class TableItemProviderContext {
		public final IProperty<IUIAElement[]> ColumnHeaderItems;
		public final IProperty<IUIAElement[]> RowHeaderItems;

		public TableItemProviderContext(IInitContext init, ITableItemProvider tableItemProvider) {
			ColumnHeaderItems = init.addProperty(StandardPropertyIds.UIA_TableColumnHeadersPropertyId, tableItemProvider::GetColumnHeaderItems, StandardVariantConverters.UNKNOWNArray_IUIAElementArray);
			RowHeaderItems = init.addProperty(StandardPropertyIds.UIA_TableRowHeadersPropertyId, tableItemProvider::GetRowHeaderItems, StandardVariantConverters.UNKNOWNArray_IUIAElementArray);
		}
	}



	/**
	 * Retrieves a collection of Microsoft UI Automation provider representing all the column headers associated with a table item or cell.
	 * @return the column header items
	 */
	IUIAElement[] GetColumnHeaderItems();

	/**
	 * Retrieves a collection of Microsoft UI Automation provider representing all the row headers associated with a table item or cell.
	 * @return the row header items
	 */
	IUIAElement[] GetRowHeaderItems();

}
