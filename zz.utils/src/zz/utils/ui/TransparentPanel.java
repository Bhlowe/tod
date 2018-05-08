/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 16, 2001
 * Time: 4:14:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.LayoutManager;

import javax.swing.JPanel;

public class TransparentPanel extends JPanel
{
	public TransparentPanel (LayoutManager layout, boolean isDoubleBuffered)
	{
		super (layout, isDoubleBuffered);
		setOpaque (false);
	}

	public TransparentPanel (LayoutManager layout)
	{
		super (layout);
		setOpaque (false);
	}

	public TransparentPanel (boolean isDoubleBuffered)
	{
		super (isDoubleBuffered);
		setOpaque (false);
	}

	public TransparentPanel ()
	{
		setOpaque (false);
	}

	public boolean isOptimizedDrawingEnabled ()
	{
		return false;
	}
}
