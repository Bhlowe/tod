/*
 * Created on Oct 5, 2007
 */
package zz.utils.ui;

import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

/**
 * A subclass of JPanel that implements {@link MouseListener},
 * {@link MouseMotionListener} and {@link MouseWheelListener}, and registers
 * itself as a listener.
 * @author gpothier
 */
public class MousePanel extends JPanel
implements MouseListener, MouseMotionListener
{
	{
		addMouseListener(this);
		addMouseMotionListener(this);
	}

	public MousePanel()
	{
	}

	public MousePanel(boolean aIsDoubleBuffered)
	{
		super(aIsDoubleBuffered);
	}

	public MousePanel(LayoutManager aLayout, boolean aIsDoubleBuffered)
	{
		super(aLayout, aIsDoubleBuffered);
	}

	public MousePanel(LayoutManager aLayout)
	{
		super(aLayout);
	}

	public void mouseClicked(MouseEvent aE)
	{
	}

	public void mouseEntered(MouseEvent aE)
	{
	}

	public void mouseExited(MouseEvent aE)
	{
	}

	public void mousePressed(MouseEvent aE)
	{
	}

	public void mouseReleased(MouseEvent aE)
	{
	}

	public void mouseDragged(MouseEvent aE)
	{
	}

	public void mouseMoved(MouseEvent aE)
	{
	}
}
