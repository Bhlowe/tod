/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 26, 2001
 * Time: 3:58:05 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * A class able to display content and to get mouse input. Layers are supposed
 * to be stacked in a component (see LayeredComponent).<p>
 * Subclasses can override the paint method and any of the
 * mouse event methods. If an event is not consumed, it is propagated
 * to the next layer in the stack.<p>
 * A layer can be disabled, which prevents it from painting itself and from receiving
 * mouse events (this is handled by LayeredComponent).
 */
public abstract class Layer implements MouseListener, MouseMotionListener, KeyListener
{
	protected JComponent itsComponent;
	protected boolean itsEnabled = true;

	public Layer ()
	{
	}

	public Layer (JComponent aComponent)
	{
		setComponent(aComponent);
	}

	public int getWidth ()
	{
		return itsComponent.getWidth();
	}

	public int getHeight ()
	{
		return itsComponent.getHeight();
	}

	protected void setCursor (Cursor aCursor)
	{
		itsComponent.setCursor(aCursor);
	}

	public JComponent getComponent ()
	{
		return itsComponent;
	}

	public void setComponent (JComponent aComponent)
	{
		itsComponent = aComponent;
	}

	public void enable ()
	{
		itsEnabled = true;
	}

	public void disable ()
	{
		itsEnabled = false;
	}

	public boolean isEnabled ()
	{
		return itsEnabled;
	}

	public void repaint ()
	{
		if (itsEnabled && itsComponent != null) itsComponent.repaint();
	}

	public void grabFocus ()
	{
		if (itsEnabled && itsComponent != null) itsComponent.grabFocus();
	}

	public void mouseClicked (MouseEvent event)
	{
	}

	public void mouseDragged (MouseEvent event)
	{
	}

	public void mousePressed (MouseEvent event)
	{
	}

	public void mouseMoved (MouseEvent event)
	{
	}

	public void mouseReleased (MouseEvent event)
	{
	}

	public void mouseEntered (MouseEvent event)
	{
	}

	public void mouseExited (MouseEvent event)
	{
	}

	public void keyTyped (KeyEvent e)
	{
	}

	public void keyReleased (KeyEvent e)
	{
	}

	public void keyPressed (KeyEvent e)
	{
	}

	public void paint (Graphics2D g)
	{
	}
}
