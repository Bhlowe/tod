/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 20, 2001
 * Time: 5:39:02 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import zz.utils.Utils;

public class TreeLayout implements LayoutManager2
{
	protected Map itsNodeMap = new HashMap ();
	protected Node itsRootNode;

	/**
	 * Number of pixels separating a parent from its children
	 */
	protected int itsParentChildGap;

	/**
	 * Number of pixels separating two children from the same parent
	 */
	protected int itsInterSiblingGap;

	/**
	 * Number of pixels separating the last child of a node from the first child of the next node.
	 */
	protected int itsInterCousinGap;

	public TreeLayout ()
	{
		this (40, 10, 20);
	}

	public TreeLayout (int aParentChildGap, int anInterSiblingGap, int anInterCousinGap)
	{
		itsInterCousinGap = anInterCousinGap;
		itsInterSiblingGap = anInterSiblingGap;
		itsParentChildGap = aParentChildGap;
	}

	public void addLayoutComponent (Component comp, Object constraints)
	{
		JComponent theComponent = (JComponent) comp;
		JComponent theParent = (JComponent) constraints;

		Node theNode = new Node (theComponent);
		if (theParent != null)
		{
			Node theParentNode = getNode(theParent);
			if (theParentNode == null) throw new IllegalArgumentException("The specified parent is absent");
			theParentNode.addChild(theNode);
		}
		else
		{
			itsRootNode = theNode;
		}

		itsNodeMap.put (theComponent, theNode);
	}

	public void removeLayoutComponent (Component comp)
	{
		JComponent theComponent = (JComponent) comp;
		Node theNode = getNode (theComponent);
		if (theNode != null)
		{
			theNode.detach();
			itsNodeMap.remove (theComponent);
		}
	}

	public void addLayoutComponent (String name, Component comp)
	{
		throw new UnsupportedOperationException("Constraint cannot be a String");
	}

	protected Node getNode (JComponent aComponent)
	{
		return (Node) itsNodeMap.get (aComponent);
	}

	public float getLayoutAlignmentX (Container target)
	{
		return 0.5f;
	}

	public float getLayoutAlignmentY (Container target)
	{
		return 0.5f;
	}

	public void invalidateLayout (Container target)
	{
	}

	public Dimension maximumLayoutSize (Container aTarget)
	{
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	public Dimension minimumLayoutSize (Container aTarget)
	{
		return layoutSize(aTarget, UIUtils.MINIMUM_SIZE);
	}

	public Dimension preferredLayoutSize (Container aTarget)
	{
		return layoutSize(aTarget, UIUtils.PREFERRED_SIZE);
	}

	protected Dimension layoutSize (Container aTarget, int aType)
	{
		if (itsRootNode == null) return new Dimension (0, 0);
		else return itsRootNode.getHierarchySize(aType);
	}

	public void layoutContainer (Container aTarget)
	{
		if (itsRootNode != null)
		{
			itsRootNode.prepareLayout(0, 0);
			itsRootNode.doLayout();
		}
	}

	public void paintLines (Graphics g)
	{
		if (itsRootNode != null)
		{
			g.setColor(Color.black);
			itsRootNode.paintLines (g);
		}
	}

	class Node
	{
		protected Node itsParent;
        protected JComponent itsComponent;
		protected List itsChildrenList;

		protected int itsPreparedU;
		protected int itsPreparedV;

		public Node (JComponent aComponent)
		{
			itsComponent = aComponent;
		}

		public void addChild (Node aChild)
		{
			if (itsChildrenList == null) itsChildrenList = new ArrayList ();
			itsChildrenList.add (aChild);
			aChild.itsParent = this;
		}

		public void removeChild (Node aChild)
		{
			if (itsChildrenList != null)
			{
				itsChildrenList.remove(aChild);
				aChild.itsParent = null;
			}
		}

		public boolean hasChildren ()
		{
			return itsChildrenList != null && itsChildrenList.size () > 0;
		}

		public void detach ()
		{
			if (itsParent != null) itsParent.removeChild(this);
		}

		public Node getParent ()
		{
			return itsParent;
		}

		public JComponent getComponent ()
		{
			return itsComponent;
		}

		public List getChildrenList ()
		{
			return itsChildrenList;
		}

		/**
		 * @param aType One of Utils.MINIMUM_SIZE and Utils.PREFERRED_SIZE
		 */
		public Dimension getHierarchySize (int aType)
		{
			int theUSpan = 0;
			int theVSpan = 0;

			if (hasChildren())
			{
				int i = 0;
				for (Iterator theIterator = itsChildrenList.iterator (); theIterator.hasNext ();)
				{
					Node theNode = (Node) theIterator.next ();
					Dimension theDimension = theNode.getHierarchySize(aType);
					int theChildUSpan = theDimension.width;
					int theChildVSpan = theDimension.height;

					theUSpan = Math.max(theUSpan, theChildUSpan);
					theVSpan += theChildVSpan;

					if (i > 0)
					{
						if (theNode.hasChildren()) theVSpan += itsInterCousinGap;
						else theVSpan += itsInterSiblingGap;
					}

					i++;
				}

				theUSpan += itsParentChildGap;
			}

            theUSpan += getPreferredComponentUSpan();
			theVSpan = Math.max (getPreferredComponentVSpan(), theVSpan);

			return new Dimension(theUSpan, theVSpan);
		}

		public int prepareLayout (int aU, int aV)
		{
			itsPreparedU = aU;

			int theUSpan = getPreferredComponentUSpan() + itsParentChildGap;
			int theVSpan = 0;
			if (hasChildren())
			{
				int i = 0;
				for (Iterator theIterator = itsChildrenList.iterator (); theIterator.hasNext ();)
				{
					Node theChild = (Node) theIterator.next ();

					if (i > 0)
					{
						if (theChild.hasChildren()) theVSpan += itsInterCousinGap;
						else theVSpan += itsInterSiblingGap;
					}

					int theChildVSpan = theChild.prepareLayout(aU + theUSpan, aV + theVSpan);

					theVSpan += theChildVSpan;

					i++;
				}

				Node theFirstChild = (Node) itsChildrenList.get (0);
				Node theLastChild = (Node) itsChildrenList.get(itsChildrenList.size()-1);
				int theFirstChildV = theFirstChild.itsPreparedV;
				int theLastChildV = theLastChild.itsPreparedV;
				int theLastChildVSpan = theLastChild.getPreferredComponentVSpan();
				itsPreparedV = (theFirstChildV + theLastChildV + theLastChildVSpan)/2;
				itsPreparedV -= getPreferredComponentVSpan() / 2;
			}
			else
			{
				itsPreparedV = aV;
			}

			theVSpan = Math.max (theVSpan, getPreferredComponentVSpan());
			return theVSpan;
		}

		public void doLayout ()
		{
			int theUSpan = getPreferredComponentUSpan();
			int theVSpan = getPreferredComponentVSpan();

			itsComponent.setBounds(itsPreparedU, itsPreparedV, theUSpan, theVSpan);

			if (hasChildren())
			{
				for (Iterator theIterator = itsChildrenList.iterator (); theIterator.hasNext ();)
				{
					Node theNode = (Node) theIterator.next ();
					theNode.doLayout();
				}
			}
		}

		public void paintLines (Graphics g)
		{
			if (hasChildren())
			{
				int theFirstV = 0;
				int theLastV = 0;
				int i = 0;

				int theHalfUGap = itsParentChildGap/2;
				int theCenterU = getComponentUPosition() + getComponentUSpan() + theHalfUGap;

				for (Iterator theIterator = itsChildrenList.iterator (); theIterator.hasNext ();)
				{
					Node theNode = (Node) theIterator.next ();
					int theV = theNode.getComponentVPosition();
					int theVSpan = theNode.getComponentVSpan();

					if (i == 0) theFirstV = theV + theVSpan/2;
					if (! theIterator.hasNext()) theLastV = theV + theVSpan/2;
					i++;

					paintULine (g, theCenterU, theV + theVSpan/2, theHalfUGap);

					theNode.paintLines(g);
				}

				int theLineV = getComponentVPosition() + getComponentVSpan()/2;
				paintULine (g, theCenterU - theHalfUGap, theLineV, theHalfUGap);
				paintVLine (g, theCenterU, theFirstV, theLastV - theFirstV);
			}
		}

		protected void paintULine (Graphics g, int aU, int aV, int aUSpan)
		{
			g.fillRect(aU, aV, aUSpan, 2);
		}

		protected void paintVLine (Graphics g, int aU, int aV, int aVSpan)
		{
			g.fillRect(aU, aV, 2, aVSpan);
		}

		/**
		 * Direction U is from root to leaves
		 */
		protected int getPreferredComponentUSpan ()
		{
			return itsComponent.getPreferredSize().width;
		}

		/**
		 * Direction V is from first child to last child
		 */
		protected int getPreferredComponentVSpan ()
		{
			return itsComponent.getPreferredSize().height;
		}

		protected Rectangle itsBoundsBuffer = new Rectangle ();

		protected int getComponentUPosition ()
		{
			return itsComponent.getBounds (itsBoundsBuffer).x;
		}

		protected int getComponentUSpan ()
		{
			return itsComponent.getBounds (itsBoundsBuffer).width;
		}

		protected int getComponentVPosition ()
		{
			return itsComponent.getBounds (itsBoundsBuffer).y;
		}

		protected int getComponentVSpan ()
		{
			return itsComponent.getBounds (itsBoundsBuffer).height;
		}
	}
}