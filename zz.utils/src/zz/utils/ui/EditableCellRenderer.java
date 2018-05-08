/*
 * Created on Mar 3, 2006
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 * A table cell renderer that simply changes the background of non-editable cells.
 * @author gpothier
 */
public class EditableCellRenderer extends DefaultTableCellRenderer
{
	private TableModel itsTableModel;
	private Color itsEditableBackground;
	private Color itsNoneditableBackground;
	
	public EditableCellRenderer(TableModel aTableModel)
	{
		this (aTableModel, Color.LIGHT_GRAY);
	}
	
	public EditableCellRenderer(TableModel aTableModel, Color aNoneditableBackground)
	{
		this(aTableModel, aNoneditableBackground, Color.WHITE);
	}
	
	public EditableCellRenderer(TableModel aTableModel, Color aNoneditableBackground, Color aEditableBackground)
	{
		itsTableModel = aTableModel;
		itsEditableBackground = aEditableBackground;
		itsNoneditableBackground = aNoneditableBackground;
	}

	@Override
	public Component getTableCellRendererComponent(JTable aTable, Object aValue, boolean aIsSelected, boolean aHasFocus, int aRow, int aColumn)
	{
		super.getTableCellRendererComponent(aTable, aValue, aIsSelected, aHasFocus, aRow, aColumn);
		
		Color theBackground = itsTableModel.isCellEditable(aRow, aColumn) ?
				itsEditableBackground 
				: itsNoneditableBackground;
		
		if (theBackground != null) setBackground(theBackground);
		
		return this;
	}
	
	
	
}
