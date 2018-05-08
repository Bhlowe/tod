/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 11, 2001
 * Time: 3:44:53 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import zz.utils.Utils;

/**
 * A layout manager shared by several containers. Places the components of the
 * containers into columns according to the preferred size of each component.
 */
public class ColumnsLayout implements LayoutManager2
{
	/**
	 * The container that called invalidateLayout. Used to avoid infinite recursion
	 * when invalidating containers.
	 */
	protected Container itsLayoutInvalidater;

	protected List itsContainersList = new ArrayList ();

	protected int itsHGap;

	protected int[] itsColumnsWidths;

	protected int itsTotalWidth;

	public ColumnsLayout ()
	{
	}

	public ColumnsLayout (int aHGap)
	{
		itsHGap = aHGap;
	}

	public void addContainer (Container aContainer)
	{
		itsContainersList.add (aContainer);
	}

	public void removeContainer (Container aContainer)
	{
		itsContainersList.remove (aContainer);
	}

	public void clearContainers ()
	{
		itsContainersList.clear();
	}

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
		if (itsLayoutInvalidater != null) return;
		itsLayoutInvalidater = aTarget;

		for (Iterator theIterator = itsContainersList.iterator (); theIterator.hasNext ();)
		{
			Container theContainer = (Container) theIterator.next ();
            if (theContainer != aTarget && theContainer.isValid()) theContainer.invalidate();
		}

		itsLayoutInvalidater = null;
	}

	public void layoutContainer (Container aTarget)
	{
		computeColumnsSizes(UIUtils.PREFERRED_SIZE);

		Insets theInsets = aTarget.getInsets();

		Dimension theSize = aTarget.getSize();
		int h = theSize.height - theInsets.top - theInsets.bottom;

		int x = theInsets.left;
		int y = theInsets.top;
		Component[] theChildren = aTarget.getComponents();
		for (int i = 0; i < theChildren.length; i++)
		{
			Component theChild = theChildren[i];
			int w = itsColumnsWidths[i];
			theChild.setBounds(x, y, w, h);
			x += w + itsHGap;
		}
	}

	protected void computeColumnsSizes (int aType)
	{
		// count columns

		int theNColumns = 0;
		for (Iterator theIterator = itsContainersList.iterator (); theIterator.hasNext ();)
		{
			Container theContainer = (Container) theIterator.next ();
			theNColumns = Math.max (theNColumns, theContainer.getComponentCount());
		}

		itsColumnsWidths = new int[theNColumns];
		for (int i=0;i<theNColumns;i++) itsColumnsWidths[i] = 0;

		// compute columns size
		for (Iterator theIterator = itsContainersList.iterator (); theIterator.hasNext ();)
		{
			Container theContainer = (Container) theIterator.next ();
			Component[] theCildren = theContainer.getComponents();
			for (int i = 0; i < theCildren.length; i++)
			{
				Component theChild = theCildren[i];
				Dimension theSize = UIUtils.getASize (theChild, aType);
				itsColumnsWidths[i] = Math.max (itsColumnsWidths[i], theSize.width);
			}
		}

		// compute total width
		itsTotalWidth = 0;
		for (int i = 0; i < itsColumnsWidths.length; i++)
		{
			int theWidth = itsColumnsWidths[i];
			itsTotalWidth += theWidth;
			if (i != 0) itsTotalWidth += itsHGap;
		}
	}

	public Dimension layoutSize (Container aTarget, int aType)
	{
		computeColumnsSizes(aType);

		Insets theInsets = aTarget.getInsets();

		int theHeight = 0;
		Component[] theChildren = aTarget.getComponents();
		for (int i = 0; i < theChildren.length; i++)
		{
			Component theChild = theChildren[i];
			Dimension theSize = UIUtils.getASize(theChild, aType);
			theHeight = Math.max (theHeight, theSize.height);
		}

		return new Dimension(
				itsTotalWidth+theInsets.left+theInsets.right, 
				theHeight+theInsets.top+theInsets.bottom);
		
//		Insets theInsets = aTarget.getInsets();
//
//		int theHeight = 0;
//		int theWidth = 0;
//		Component[] theChildren = aTarget.getComponents();
//		for (int i = 0; i < theChildren.length; i++)
//		{
//			Component theChild = theChildren[i];
//			Dimension theSize = Utils.getASize(theChild, aType);
//			theHeight = Math.max (theHeight, theSize.height);
//			theWidth += theSize.width;
//			if (i != 0) theWidth += itsHGap;
//		}
//
//		return new Dimension(theWidth+theInsets.left+theInsets.right, theHeight+theInsets.top+theInsets.bottom);
	}

	public Dimension maximumLayoutSize (Container aTarget)
	{
		return layoutSize(aTarget, UIUtils.MAXIMUM_SIZE);
	}

	public Dimension minimumLayoutSize (Container aTarget)
	{
		return layoutSize(aTarget, UIUtils.MINIMUM_SIZE);
	}

	public Dimension preferredLayoutSize (Container aTarget)
	{
		return layoutSize(aTarget, UIUtils.PREFERRED_SIZE);
	}
}
