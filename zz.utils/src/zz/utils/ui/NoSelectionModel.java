/*
 * Created on 14-sep-2004
 */
package zz.utils.ui;

import javax.swing.DefaultListSelectionModel;


/**
 * This selection model doesn't allow any selection.
 * @author gpothier
 */
public class NoSelectionModel extends DefaultListSelectionModel
{
	private static final NoSelectionModel INSTANCE = new NoSelectionModel();

	public static NoSelectionModel getInstance()
	{
		return INSTANCE;
	}

	private NoSelectionModel()
	{
	}
	
	public boolean isSelectedIndex(int aIndex)
	{
		return false;
	}
	
	public boolean isSelectionEmpty()
	{
		return true;
	}
}