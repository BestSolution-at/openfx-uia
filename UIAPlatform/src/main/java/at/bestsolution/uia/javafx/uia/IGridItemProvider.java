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

/**
 * Provides access to individual child controls of containers that implement IGridProvider.
 * <p>
 * Implemented on a UI Automation provider that must support the GridItem control pattern.<br>
 * Controls that implement IGridItemProvider can typically be traversed (that is, a UI Automation client can move to adjacent controls) by using the keyboard.
 * </p>
 */
public interface IGridItemProvider extends IInitable {

	/**
	 * context object for IGridItemProvider.
	 * <p>
	 * The context object encapsulates the functionality to fire property changes and events.
	 * </p>
	 */
	class GridItemProviderContext {
		public final IProperty<Integer> Column;
		public final IProperty<Integer> ColumnSpan;
		public final IProperty<Integer> Row;
		public final IProperty<Integer> RowSpan;
		public final IProperty<IUIAElement> ContainingGrid;

		public GridItemProviderContext(IInitContext init, IGridItemProvider gridItemProvider) {
			Column = init.addProperty(StandardPropertyIds.UIA_GridItemColumnPropertyId, gridItemProvider::get_Column, StandardVariantConverters.I4_Integer);
			ColumnSpan = init.addProperty(StandardPropertyIds.UIA_GridItemColumnSpanPropertyId, gridItemProvider::get_ColumnSpan, StandardVariantConverters.I4_Integer);
			Row = init.addProperty(StandardPropertyIds.UIA_GridItemRowPropertyId, gridItemProvider::get_Row, StandardVariantConverters.I4_Integer);
			RowSpan = init.addProperty(StandardPropertyIds.UIA_GridItemRowSpanPropertyId, gridItemProvider::get_RowSpan, StandardVariantConverters.I4_Integer);
			ContainingGrid = init.addProperty(StandardPropertyIds.UIA_GridItemContainingGridPropertyId, gridItemProvider::get_ContainingGrid, StandardVariantConverters.UNKNOWN_IUIAElement);
		}
	}

	/**
	 * Specifies the ordinal number of the column that contains this cell or item.
	 * @return the column
	 */
	int get_Column();

	/**
	 * Specifies the number of columns spanned by this cell or item.
	 * @return the column span
	 */
	int get_ColumnSpan();

	/**
	 * Specifies the ordinal number of the row that contains this cell or item.
	 * @return the row
	 */
	int get_Row();

	/**
	 * Specifies the number of rows spanned by this cell or item.
	 * @return the row span
	 */
	int get_RowSpan();

	/**
	 * Specifies the UI Automation provider that implements IGridProvider and represents the container of this cell or item.
	 * @return the conaining grid element
	 */
	IUIAElement get_ContainingGrid();

}
