/*
 * Created on Mar 7, 2008
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

/**
 * A layout manager that does nothing.
 * It is better to use this than no layout manager at all
 * @author gpothier
 */
public class NullLayout implements LayoutManager2
{

	public void addLayoutComponent(Component aComp, Object aConstraints)
	{
	}

	public float getLayoutAlignmentX(Container aTarget)
	{
		return 0.5f;
	}

	public float getLayoutAlignmentY(Container aTarget)
	{
		return 0.5f;
	}

	public void invalidateLayout(Container aTarget)
	{
	}

	public Dimension maximumLayoutSize(Container aTarget)
	{
		return new Dimension(0, 0);
	}

	public void addLayoutComponent(String aName, Component aComp)
	{
	}

	public void layoutContainer(Container aParent)
	{
	}

	public Dimension minimumLayoutSize(Container aParent)
	{
		return new Dimension(0, 0);
	}

	public Dimension preferredLayoutSize(Container aParent)
	{
		return new Dimension(0, 0);
	}

	public void removeLayoutComponent(Component aComp)
	{
	}

}
