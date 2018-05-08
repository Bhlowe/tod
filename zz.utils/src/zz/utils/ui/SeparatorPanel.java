/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 7, 2001
 * Time: 12:02:12 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.LayoutManager;

public class SeparatorPanel extends TransparentPanel
{
	protected boolean itsHorizontal = true;

	public SeparatorPanel (LayoutManager layout, boolean isDoubleBuffered)
	{
		super (layout, isDoubleBuffered);
	}

	public SeparatorPanel (LayoutManager layout)
	{
		super (layout);
	}

	public SeparatorPanel (boolean isDoubleBuffered)
	{
		super (isDoubleBuffered);
	}

	public SeparatorPanel ()
	{
	}

	protected void paintComponent (Graphics g)
	{
		super.paintComponent (g);

		if (itsHorizontal)
		{
			int w = getWidth();
			int h = getHeight()/2;

			g.setColor (Color.gray);
			g.drawLine (0, h, w, h);
			g.setColor (Color.white);
			g.drawLine (0, h+1, w, h+1);
		}
	}
}
