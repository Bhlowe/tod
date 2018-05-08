/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 10, 2002
 * Time: 11:03:37 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

public class Borders
{
	/**
	 * Returns the TextField style border for the current L&F
	 */
	public static Border getTextFieldBorder ()
	{
		return UIManager.getBorder("TextField.border");
	}

	protected static CompoundBorder itsFlatAsymetricBorder = BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.gray),
			BorderFactory.createEmptyBorder(3, 3, 0, 0));

	public static Border getFlatAsymetricBorder ()
	{
		return itsFlatAsymetricBorder;
	}
}
