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
 * Provides access to controls that act as containers for a collection of child elements organized in a two-dimensional
 * logical coordinate system that can be traversed (that is, a Microsoft UI Automation client can move to adjacent controls)
 * by using the keyboard. The children of this element must implement IGridItemProvider.
 * <p>
 * The IGridProvider interface exposes methods and properties to support UI Automation client access to controls that act as
 * containers for a collection of child elements. The children of this element must implement IGridItemProvider and be organized
 * in a two-dimensional logical coordinate system that can be traversed (that is, a UI Automation client can move to adjacent controls)
 * by using the keyboard.<br>
 * Implemented on a UI Automation provider that must support the Grid control pattern.<br>
 * IGridProvider does not enable active manipulation of a grid; ITransformProvider must be implemented for this.
 * </p>
 */
public interface IGridProvider extends IInitable {

	/**
	 * context object for IGridProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class GridProviderContext {
		public final IProperty<Integer> ColumnCount;
		public final IProperty<Integer> RowCount;

		public GridProviderContext(IInitContext init, IGridProvider gridProvider) {
			ColumnCount = init.addProperty(StandardPropertyIds.UIA_GridColumnCountPropertyId, gridProvider::get_ColumnCount, StandardVariantConverters.I4_Integer);
			RowCount = init.addProperty(StandardPropertyIds.UIA_GridRowCountPropertyId, gridProvider::get_RowCount, StandardVariantConverters.I4_Integer);
		}
	}

	/**
	 * Specifies the total number of columns in the grid.
	 * <p>
	 * Hidden rows and columns, depending on the provider implementation, may be loaded in the logical tree and will therefore be reflected in the IGridProvider::RowCount and IGridProvider::ColumnCount properties. If the hidden rows and columns have not yet been loaded they will not be counted.
	 * </p>
	 * @return the column count
	 */
	int get_ColumnCount();


	/**
	 * Specifies the total number of rows in the grid.
	 * <p>
	 * Hidden rows and columns, depending on the provider implementation, may be loaded in the logical tree and will therefore be reflected in the IGridProvider::RowCount and IGridProvider::ColumnCount properties. If the hidden rows and columns have not yet been loaded they will not be counted.
	 * </p>
	 * @return the row count
	 */
	int get_RowCount();

	/**
	 * Retrieves the Microsoft UI Automation provider for the specified cell.
	 * <p>
	 * Grid coordinates are zero-based with the upper left (or upper right cell depending on locale) having coordinates (0,0).<br>
	 * If a cell is empty a UI Automation provider must still be returned in order to support the ContainingGrid property for that cell. This is possible when the layout of child elements in the grid is similar to a ragged array. <br>
	 * Hidden rows and columns, depending on the provider implementation, may be loaded in the UI Automation tree and will therefore be reflected in the IGridProvider::RowCount and IGridProvider::ColumnCount properties. If the hidden rows and columns have not yet been loaded they should not be counted.<br>
	 * </p>
	 * @param row The ordinal number of the row of interest.
	 * @param column The ordinal number of the column of interest.
	 * @return Receives a pointer to a UI Automation provider for the specified cell or a null reference (Nothing in Microsoft Visual Basic .NET) if the cell is empty.
	 */
	IUIAElement GetItem(int row, int column);



}
