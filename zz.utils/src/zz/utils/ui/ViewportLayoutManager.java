/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 26 mars 2003
 * Time: 15:19:41
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;

import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zz.utils.Utils;

/**
 * A layout manager that takes a container with a single component
 * and places this component so as to display it within a viewport window.
 */
public class ViewportLayoutManager implements LayoutManager2, ChangeListener
{
	public static final int WIDTH = 0;
	public static final int HEIGHT = 1;

	private int itsOrientation;

	private JViewport itsViewport;
	private Container itsContainer;

	public ViewportLayoutManager (int aOrientation)
	{
		itsOrientation = aOrientation;
	}

	private void setupListener (Container aContainer)
	{
		JViewport theViewport = UIUtils.getViewport (aContainer);
		if (itsViewport != theViewport)
		{
			if (itsViewport != null) itsViewport.removeChangeListener(this);
			itsViewport = theViewport;
			if (itsViewport != null) itsViewport.addChangeListener(this);
		}
		itsContainer = aContainer;
	}

	public void stateChanged (ChangeEvent e)
	{
		if (itsContainer != null) itsContainer.doLayout();
	}

	public void addLayoutComponent (Component comp, Object constraints)
	{
	}

	public float getLayoutAlignmentX (Container target)
	{
		return 0;
	}

	public float getLayoutAlignmentY (Container target)
	{
		return 0;
	}

	public void invalidateLayout (Container target)
	{
	}

	public void addLayoutComponent (String name, Component comp)
	{
	}

	public void removeLayoutComponent (Component comp)
	{
	}

	public Dimension preferredLayoutSize (Container aParent)
	{
		Rectangle theBounds = computeBounds (aParent);
		return new Dimension (theBounds.width, theBounds.height);
	}

	public Dimension minimumLayoutSize (Container aParent)
	{
		Rectangle theBounds = computeBounds (aParent);
		return new Dimension(theBounds.width, theBounds.height);
	}

	public Dimension maximumLayoutSize (Container aParent)
	{
		Rectangle theBounds = computeBounds (aParent);
		return new Dimension (theBounds.width, theBounds.height);
	}

	public void layoutContainer (Container aParent)
	{
		setupListener(aParent);
		if (itsViewport == null) return;

		Component[] theComponents = aParent.getComponents();
		if (theComponents.length >= 1)
		{
			Component theChild = theComponents[0];
			theChild.setBounds(computeBounds(aParent));
		}
	}

	private Rectangle computeBounds (Container aContainer)
	{
		if (itsViewport == null) itsViewport = UIUtils.getViewport (aContainer);
		
		Rectangle theViewRect = itsViewport.getViewRect();
		Component[] theComponents = aContainer.getComponents ();
		if (theComponents.length >= 1)
		{
			Component theChild = theComponents[0];
			Dimension thePreferredSize = theChild.getPreferredSize();
			if (itsOrientation == WIDTH)
				return new Rectangle(theViewRect.x, 0, theViewRect.width, thePreferredSize.height);
			else return new Rectangle(0, theViewRect.y, thePreferredSize.width, theViewRect.height);
		}
		else return new Rectangle();
	}
}
