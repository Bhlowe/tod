/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 8, 2001
 * Time: 5:22:04 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;

import zz.utils.Utils;

/**
 * A class similar to JDK's BorderLayout (and partially copied from it) but which can have
 * components in the corners. If no component lies in a corner, the corner is left
 * empty, and is not taken over by an other border component.
 */
public class BorderAndCornerLayout implements LayoutManager2
{
	public static final String CENTER = "center";
	public static final String NORTH = "north";
	public static final String SOUTH = "south";
	public static final String EAST = "east";
	public static final String WEST = "west";
	public static final String NE = "ne";
	public static final String NW = "nw";
	public static final String SE = "se";
	public static final String SW = "sw";

	protected Component itsNorthComponent;
	protected Component itsWestComponent;
	protected Component itsEastComponent;
	protected Component itsSouthComponent;
	protected Component itsCenterComponent;
	protected Component itsNEComponent;
	protected Component itsNWComponent;
	protected Component itsSEComponent;
	protected Component itsSWComponent;

	protected int itsNorthBorderHeight;
	protected int itsSouthBorderHeight;
	protected int itsWestBorderWidth;
	protected int itsEastBorderWidth;
	protected int itsCenterWidth;
	protected int itsCenterHeight;
	protected int itsTotalVerticalGap;
	protected int itsTotalHorizontalGap;

	/**
	 * If true, the layout is performed twice. This is useful if the dimension of a component
	 * are interdependant
	 */
	protected boolean itsTwoPassesLayout = false;

	protected int itsHGap = 0;
	protected int itsVGap = 0;

	public BorderAndCornerLayout ()
	{
	}

	public BorderAndCornerLayout (int aHGap, int aVGap)
	{
		itsHGap = aHGap;
		itsVGap = aVGap;
	}

	public BorderAndCornerLayout (boolean aTwoPassesLayout)
	{
		itsTwoPassesLayout = aTwoPassesLayout;
	}

	public BorderAndCornerLayout (int aHGap, int aVGap, boolean aTwoPassesLayout)
	{
		itsHGap = aHGap;
		itsVGap = aVGap;
		itsTwoPassesLayout = aTwoPassesLayout;
	}

	public void addLayoutComponent (Component comp, Object constraints)
	{
		synchronized (comp.getTreeLock ())
		{
			if ((constraints == null) || (constraints instanceof String))
			{
				addLayoutComponent ((String) constraints, comp);
			}
			else
			{
				throw new IllegalArgumentException ("cannot add to layout: constraint must be a string (or null)");
			}
		}
	}

	public void addLayoutComponent (String name, Component comp)
	{
		synchronized (comp.getTreeLock ())
		{
			if (name == null) name = CENTER;

			if (name == CENTER)
				itsCenterComponent = comp;
			else if (name == NORTH)
				itsNorthComponent = comp;
			else if (name == SOUTH)
				itsSouthComponent = comp;
			else if (name == EAST)
				itsEastComponent = comp;
			else if (name == WEST)
				itsWestComponent = comp;
			else if (name == NE)
				itsNEComponent = comp;
			else if (name == NW)
				itsNWComponent = comp;
			else if (name == SE)
				itsSEComponent = comp;
			else if (name == SW)
				itsSWComponent = comp;
			else
				throw new IllegalArgumentException ("cannot add to layout: unknown constraint: " + name);
		}
	}

	public void removeLayoutComponent (Component comp)
	{
		synchronized (comp.getTreeLock ())
		{
			if (comp == itsCenterComponent)
				itsCenterComponent = null;
			else if (comp == itsNorthComponent)
				itsNorthComponent = null;
			else if (comp == itsSouthComponent)
				itsSouthComponent = null;
			else if (comp == itsEastComponent)
				itsEastComponent = null;
			else if (comp == itsWestComponent)
				itsWestComponent = null;
			else if (comp == itsNEComponent)
				itsNEComponent = null;
			else if (comp == itsNWComponent)
				itsNWComponent = null;
			else if (comp == itsSEComponent)
				itsSEComponent = null;
			else if (comp == itsSWComponent) itsSWComponent = null;
		}
	}

	public Dimension minimumLayoutSize (Container target)
	{
		return layoutSize (target, true);
	}

	public Dimension preferredLayoutSize (Container target)
	{
		return layoutSize (target, false);
	}

	public Dimension layoutSize (Container target, boolean minimum)
	{
		synchronized (target.getTreeLock ())
		{
			Dimension dim = new Dimension (0, 0);

			updateBorderSizes (minimum);

			dim.height += itsNorthBorderHeight + itsCenterHeight + itsSouthBorderHeight;
			dim.width += itsWestBorderWidth + itsCenterWidth + itsEastBorderWidth;

			Insets insets = target.getInsets ();
			dim.width += insets.left + insets.right + itsTotalHorizontalGap;
			dim.height += insets.top + insets.bottom + itsTotalVerticalGap;

			return dim;
		}
	}

	protected void updateBorderSizes (boolean minimum)
	{
		Dimension dimCenter = getDimension (itsCenterComponent, minimum);
		Dimension dimNorth = getDimension (itsNorthComponent, minimum);
		Dimension dimSouth = getDimension (itsSouthComponent, minimum);
		Dimension dimEast = getDimension (itsEastComponent, minimum);
		Dimension dimWest = getDimension (itsWestComponent, minimum);
		Dimension dimNE = getDimension (itsNEComponent, minimum);
		Dimension dimNW = getDimension (itsNWComponent, minimum);
		Dimension dimSE = getDimension (itsSEComponent, minimum);
		Dimension dimSW = getDimension (itsSWComponent, minimum);

		itsNorthBorderHeight = Utils.max (dimNW.height, dimNorth.height, dimNE.height);
		itsSouthBorderHeight = Utils.max (dimSW.height, dimSouth.height, dimSE.height);
		itsWestBorderWidth = Utils.max (dimNW.width, dimWest.width, dimSW.width);
		itsEastBorderWidth = Utils.max (dimNE.width, dimEast.width, dimSE.width);
		itsCenterWidth = dimCenter.width;
		itsCenterHeight = dimCenter.height;

		int theNHorizontalBands = (itsNorthBorderHeight > 0 ? 1 : 0) + (itsCenterHeight > 0 ? 1 : 0) + (itsSouthBorderHeight > 0 ? 1 : 0);
		itsTotalVerticalGap = Math.max (0, theNHorizontalBands - 1) * itsVGap;
		int theNVerticalBands = (itsWestBorderWidth > 0 ? 1 : 0) + (itsCenterWidth > 0 ? 1 : 0) + (itsEastBorderWidth > 0 ? 1 : 0);
		itsTotalHorizontalGap = Math.max (0, theNVerticalBands - 1) * itsHGap;
	}

	protected static final Dimension empty = new Dimension (0, 0);

	protected Dimension getDimension (Component c, boolean minimum)
	{
		if (c == null) return empty;
		if (minimum)
			return c.getMinimumSize ();
		else
			return c.getPreferredSize ();
	}


	public Dimension maximumLayoutSize (Container target)
	{
		return new Dimension (Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public float getLayoutAlignmentX (Container parent)
	{
		return 0.5f;
	}

	public float getLayoutAlignmentY (Container parent)
	{
		return 0.5f;
	}

	/**
	 * Invalidates the layout, indicating that if the layout manager
	 * has cached information that should be discarded.
	 */
	public void invalidateLayout (Container target)
	{
	}

	public void layoutContainer (Container target)
	{
		synchronized (target.getTreeLock ())
		{
			doLayoutContainer (target);
			if (itsTwoPassesLayout) doLayoutContainer (target);
		}
	}

	public void doLayoutContainer (Container target)
	{
		updateBorderSizes (false);

		Insets insets = target.getInsets ();
		int tw = target.getWidth ();
		int th = target.getHeight ();

		int width = tw - insets.left - insets.right;
		int height = th - insets.bottom - insets.top;
		int top = insets.top;
		int bottom = th - insets.bottom;
		int left = insets.left;
		int right = tw - insets.right;

		int w = width - itsWestBorderWidth - itsEastBorderWidth - itsTotalHorizontalGap;
		int h = height - itsNorthBorderHeight - itsSouthBorderHeight - itsTotalVerticalGap;

		if (itsNWComponent != null) itsNWComponent.setBounds (left, top, itsWestBorderWidth, itsNorthBorderHeight);
		left += itsWestBorderWidth;
		if (itsWestBorderWidth > 0) left += itsHGap;
		if (itsNorthComponent != null) itsNorthComponent.setBounds (left, top, w, itsNorthBorderHeight);
		left += w;
		if (w > 0) left += itsHGap;
		if (itsNEComponent != null) itsNEComponent.setBounds (left, top, itsEastBorderWidth, itsNorthBorderHeight);

		top += itsNorthBorderHeight;
		if (itsNorthBorderHeight > 0) top += itsVGap;
		left = insets.left;

		if (itsWestComponent != null) itsWestComponent.setBounds (left, top, itsWestBorderWidth, h);
		left += itsWestBorderWidth;
		if (itsWestBorderWidth > 0) left += itsHGap;
		if (itsCenterComponent != null) itsCenterComponent.setBounds (left, top, w, h);
		left += w;
		if (w > 0) left += itsHGap;
		if (itsEastComponent != null) itsEastComponent.setBounds (left, top, itsEastBorderWidth, h);

		top += h;
		if (h > 0) top += itsVGap;
		left = insets.left;

		if (itsSWComponent != null) itsSWComponent.setBounds (left, top, itsWestBorderWidth, itsSouthBorderHeight);
		left += itsWestBorderWidth;
		if (itsWestBorderWidth > 0) left += itsHGap;
		if (itsSouthComponent != null) itsSouthComponent.setBounds (left, top, w, itsSouthBorderHeight);
		left += w;
		if (w > 0) left += itsHGap;
		if (itsSEComponent != null) itsSEComponent.setBounds (left, top, itsEastBorderWidth, itsSouthBorderHeight);
	}
}
