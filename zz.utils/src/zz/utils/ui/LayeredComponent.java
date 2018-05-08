/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 26, 2001
 * Time: 4:15:49 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import javax.swing.JPanel;

import zz.utils.ReverseIteratorWrapper;

/**
 * A component that manages layers. Front layers are displayed last and receive events first.
 */
public class LayeredComponent extends JPanel implements MouseListener, MouseMotionListener, KeyListener
{
	/**
	 * The layers groups list. Front layer groups come first.
	 */
	protected List itsLayerGroups = new ArrayList ();

	protected Cursor itsCursor = null;

	public LayeredComponent ()
	{
		this(1);
	}
	public LayeredComponent (int nLayerGroups)
	{
		super (null);

		for (int i=0;i<nLayerGroups;i++) itsLayerGroups.add (new LayerGroup());

		addComponentListener(new ComponentAdapter()
		{
			public void componentShown (ComponentEvent event)
			{
				relayout();
			}

			public void componentResized (ComponentEvent event)
			{
				relayout ();
			}
		});

		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	protected void relayout ()
	{
		int w = getWidth();
		int h = getHeight();

        Component[] theChildren = getComponents();
		for (int i = 0; i < theChildren.length; i++)
		{
			Component theChild = theChildren[i];
			theChild.setBounds(0, 0, w, h);
		}
	}

	public void addLayerToFront (int aGroup, Layer aLayer)
	{
		LayerGroup theLayerGroup = (LayerGroup) itsLayerGroups.get (aGroup);
		theLayerGroup.addLayerToFront(aLayer);
	}

	public void addLayerToBack (int aGroup, Layer aLayer)
	{
		LayerGroup theLayerGroup = (LayerGroup) itsLayerGroups.get (aGroup);
		theLayerGroup.addLayerToBack(aLayer);
	}

	/**
	 * Inserts a layer in the stack.
	 * If anIndex is >= 0, it is relative to the front of the stack.
	 * If anIndex is < 0, its absolute value is an index relative to the back of the stack.
	 * Having anIndex == 0 is equivalent to addLayerToFront.
	 * Having anIndex == -1 is equivalent to addLayerToBack.
	 */
	public void insertLayer (int aGroup, int anIndex, Layer aLayer)
	{
		LayerGroup theLayerGroup = (LayerGroup) itsLayerGroups.get (aGroup);
		theLayerGroup.insertLayer(anIndex, aLayer);
	}


	public void removeLayer (Layer aLayer)
	{
		for (Iterator theIterator = itsLayerGroups.iterator (); theIterator.hasNext ();)
		{
			LayerGroup theLayerGroup = (LayerGroup) theIterator.next ();
			theLayerGroup.removeLayer(aLayer);
		}
	}

	/**
	 * We overload this method so that we know which cursor to use.
	 * See mouseMoved
	 */
	public void setCursor (Cursor aCursor)
	{
		itsCursor = aCursor;
		super.setCursor(aCursor);
	}

	public void mouseClicked (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseClicked(event);
			if (event.isConsumed()) break;
		}
	}

	public void mouseDragged (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseDragged(event);
			if (event.isConsumed()) break;
		}
	}

	public void mousePressed (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mousePressed(event);
			if (event.isConsumed()) break;
		}
	}

	public void mouseMoved (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseMoved(event);
			if (event.isConsumed()) return;
		}

		setCursor (itsCursor); // if no layer consumed the event...
	}

	public void mouseReleased (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseReleased(event);
			if (event.isConsumed()) break;
		}
	}

	public void mouseEntered (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseEntered(event);
			if (event.isConsumed()) break;
		}
	}

	public void mouseExited (MouseEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.mouseExited(event);
			if (event.isConsumed()) break;
		}
	}

	public void keyTyped (KeyEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.keyTyped(event);
			if (event.isConsumed()) break;
		}
	}

	public void keyReleased (KeyEvent event)
	{
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.keyReleased(event);
			if (event.isConsumed()) break;
		}
	}

	public void keyPressed (KeyEvent event)
	{
		System.out.println ("LayeredComponent.keyPressed");
		for (Iterator theIterator = getIterator(); theIterator.hasNext ();)
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.keyPressed(event);
			if (event.isConsumed()) break;
		}
	}

	protected void paintComponent (Graphics aGraphics)
	{
		super.paintComponent (aGraphics);
		paintLayers(aGraphics);
	}

	/**
	 * Paints the layers.
	 */
    protected void paintLayers (Graphics aGraphics)
	{
		Iterator theIterator = getReverseIterator();
		while (theIterator.hasNext ())
		{
			Layer theLayer = (Layer) theIterator.next ();
			if (theLayer.isEnabled()) theLayer.paint((Graphics2D)aGraphics);
		}
	}

	protected Iterator getIterator ()
	{
		return new MyIterator(false);
	}

	protected Iterator getReverseIterator ()
	{
		return new MyIterator(true);
	}

	class MyIterator implements Iterator
	{
		protected boolean itsReverse;

		protected Iterator itsGroupIterator;

		protected Iterator itsLayerIterator;

		public MyIterator (boolean aReverse)
		{
			itsReverse = aReverse;

			if (itsReverse) itsGroupIterator = new ReverseIteratorWrapper(itsLayerGroups);
			else itsGroupIterator = itsLayerGroups.iterator();

			itsLayerIterator = getNextLayerIterator();
		}

		protected Iterator getNextLayerIterator ()
		{
			Iterator theResult = null;

			if (itsGroupIterator.hasNext())
			{
				LayerGroup theLayerGroup = (LayerGroup) itsGroupIterator.next();
				if (itsReverse) theResult = new ReverseIteratorWrapper (theLayerGroup.itsLayers);
				else theResult = theLayerGroup.itsLayers.iterator();
			}

			return theResult;
		}

		public boolean hasNext ()
		{
			return itsLayerIterator != null && itsLayerIterator.hasNext();
		}

		public Object next ()
		{
			if (itsLayerIterator == null) throw new NoSuchElementException();

			Object theResult = itsLayerIterator.next();

			if (! itsLayerIterator.hasNext()) itsLayerIterator = getNextLayerIterator();

			return theResult;
		}

		public void remove ()
		{
			throw new UnsupportedOperationException();
		}
	}

	public class LayerGroup
	{
		protected List itsLayers = new ArrayList ();

		public void addLayerToFront (Layer aLayer)
		{
			itsLayers.add (0, aLayer);
		}

		public void addLayerToBack (Layer aLayer)
		{
			itsLayers.add (aLayer);
		}

		/**
		 * Inserts a layer in the stack.
		 * If anIndex is >= 0, it is relative to the front of the stack.
		 * If anIndex is < 0, its absolute value is an index relative to the back of the stack.
		 * Having anIndex == 0 is equivalent to addLayerToFront.
		 * Having anIndex == -1 is equivalent to addLayerToBack.
		 */
		public void insertLayer (int anIndex, Layer aLayer)
		{
			if (anIndex >= 0) itsLayers.add (anIndex, aLayer);
			else itsLayers.add (itsLayers.size()+anIndex+1, aLayer);
		}

		public void removeLayer (Layer aLayer)
		{
			itsLayers.remove(aLayer);
		}

		public Iterator getIterator ()
		{
			return itsLayers.iterator();
		}

		public Iterator getReverseIterator ()
		{
			return new ReverseIteratorWrapper (itsLayers);
		}
	}

}
