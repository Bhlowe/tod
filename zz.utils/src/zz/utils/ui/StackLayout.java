/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 4, 2001
 * Time: 3:30:48 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;


import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A LayoutManager similar but much dumber than {@link javax.swing.OverlayLayout}. All children
 * are set to be the same size as the container.
 */
public class StackLayout implements LayoutManager2
{
	public void addLayoutComponent (Component aComponent, Object aConstraints)
	{
	}

	public void addLayoutComponent (String aName, Component aComponent)
	{
	}

	public void removeLayoutComponent (Component aComponent)
	{
	}

	public float getLayoutAlignmentX (Container aTarget)
	{
		return 0.5f;
	}

	public float getLayoutAlignmentY (Container aTarget)
	{
		return 0.5f;
	}

	public void invalidateLayout (Container aTarget)
	{
	}

	public void layoutContainer (Container aTarget)
	{
		Dimension theSize = aTarget.getSize();

		Component[] theChildren = aTarget.getComponents();
		int theNChildren = theChildren.length;
		for (int i = 0; i < theNChildren; i++)
		{
			Component theChild = theChildren[i];
			theChild.setBounds(0, 0, theSize.width, theSize.height);
		}

	}

	public Dimension maximumLayoutSize (Container aTarget)
	{
		int theMaxWidth = Integer.MAX_VALUE;
		int theMaxHeight = Integer.MAX_VALUE;

		Component[] theChildren = aTarget.getComponents();
		int theNChildren = theChildren.length;
		for (int i = 0; i < theNChildren; i++)
		{
			Component theChild = theChildren[i];
			Dimension theMaximumSize = theChild.getMaximumSize();
			theMaxWidth = Math.min (theMaxWidth, theMaximumSize.width);
			theMaxHeight = Math.min (theMaxHeight, theMaximumSize.height);
		}

		return new Dimension(theMaxWidth, theMaxHeight);
	}

	public Dimension minimumLayoutSize (Container aTarget)
	{
		int theMinWidth = 0;
		int theMinHeight = 0;

		Component[] theChildren = aTarget.getComponents();
		int theNChildren = theChildren.length;
		for (int i = 0; i < theNChildren; i++)
		{
			Component theChild = theChildren[i];
			Dimension theMinimumSize = theChild.getMinimumSize();
			theMinWidth = Math.max (theMinWidth, theMinimumSize.width);
			theMinHeight = Math.max (theMinHeight, theMinimumSize.height);
		}

		return new Dimension(theMinWidth, theMinHeight);
	}

	public Dimension preferredLayoutSize (Container aTarget)
	{
		int theWidth = 0;
		int theHeight = 0;

		Component[] theChildren = aTarget.getComponents();
		int theNChildren = theChildren.length;
		for (int i = 0; i < theNChildren; i++)
		{
			Component theChild = theChildren[i];
			Dimension thePreferedSize = theChild.getPreferredSize();
			theWidth = Math.max (theWidth, thePreferedSize.width);
			theHeight = Math.max (theHeight, thePreferedSize.height);
		}

		return new Dimension(theWidth, theHeight);
	}
}
