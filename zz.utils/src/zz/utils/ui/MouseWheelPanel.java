/*
 * Created on Oct 5, 2007
 */
package zz.utils.ui;

import java.awt.LayoutManager;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Same as {@link MousePanel} but implements {@link MouseWheelListener}
 * @author gpothier
 */
public class MouseWheelPanel extends MousePanel
implements MouseWheelListener
{
	{
		addMouseWheelListener(this);
	}
	

	public MouseWheelPanel()
	{
	}

	public MouseWheelPanel(boolean aIsDoubleBuffered)
	{
		super(aIsDoubleBuffered);
	}

	public MouseWheelPanel(LayoutManager aLayout, boolean aIsDoubleBuffered)
	{
		super(aLayout, aIsDoubleBuffered);
	}

	public MouseWheelPanel(LayoutManager aLayout)
	{
		super(aLayout);
	}

	public void mouseWheelMoved(MouseWheelEvent aE)
	{
	}
}
