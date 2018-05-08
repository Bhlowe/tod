/*
 * Created on 05-nov-2004
 */
package zz.utils.ui;

import java.awt.Point;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import zz.utils.*;
import zz.utils.Stack;

/**
 * A utility class that can be used for mouse events handling
 * on scenegraphs. It transforms AWT mouse events into events 
 * suitable for {@link zz.utils.ui.IMouseAware} objects,
 * taking into account coordinates transformations and enhanced
 * drag and drop support.
 * @author gpothier
 */
public abstract class MouseHandler<T> 
implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener
{
	/**
	 * This flag determines what happens when the mouse enters an element:
	 * <li>If false, only the deepest element is notified.
	 * <li>If true, the deepest element and all its ancestors are notified.
	 * The implementation takes care of notifying state changes for ancestors
	 * when it actually changes.
	 */
	private boolean itsRecursiveRollover;
	
	/**
	 * Path of page elements to lead to the element currently under the mouse.
	 */
	private Stack<T> itsPathToCurrentElement = new ArrayStack<T> ();
	
	private T itsCurrentElement;
	private T itsPressedElement;

	/**
	 * X coordinate of last mouse pressed with left button.
	 */
	private int itsPressX = -1;

	/**
	 * Y coordinate of last mouse pressed with left button.
	 */
	private int itsPressY = -1;
	
	/**
	 * Timestamp of last left mouse button press.
	 */
	private long itsPressTime; 
	
	/**
	 * Whether a drag operation has started.
	 */
	private boolean itsDragging = false;

	/**
	 * The component that generates the events that are forwarded to this
	 * handler.
	 */
	private final JComponent itsComponent;

	
	public MouseHandler(JComponent aComponent, boolean aRecursiveRollover)
	{
		itsComponent = aComponent;
		itsRecursiveRollover = aRecursiveRollover;
	}
	
	/**
	 * Returns the mouse aware object associated to the specified element. 
	 */
	protected abstract IMouseAware getMouseAware (T aElement);
	
	/**
	 * Transforms a point in pixels into a point in the root
	 * coordinate system.
	 */
	protected abstract Point2D.Float pixelToRoot (Point aPoint);
	
	/**
	 * Transforms a point in the root coordinate system into
	 * a the coordinate system of the specified element.
	 */
	protected abstract Point2D.Float rootToLocal (T aElement, Point2D.Float aPoint);
	
	/**
	 * Returns the deepest element at the specified point
	 * @param aPoint A point in the root coordinate system.
	 * @param aAction The kind of mouse action for which the element is requested
	 */
	protected abstract T getElementAt (Point2D.Float aPoint, MouseAction aAction);

	/**
	 * Returns the parent element of the specified element.
	 */
	protected abstract T getParent (T aElement);

	/**
	 * Determines if the first parameter is an ancestor of the second one.
	 */
	private boolean isAncestor (T aAncestor, T aElement)
	{
		T theElement = getParent(aElement);
		while (theElement != null)
		{
			if (theElement.equals(aAncestor)) return true;
			theElement = getParent(theElement);
		}
		return false;
	}

	private Point2D.Float pixelToLocal (T aElement, Point aPoint)
	{
		return rootToLocal(aElement, pixelToRoot(aPoint));
	}
	
	private void setCurrentElement(T aElement)
	{
		if (itsCurrentElement != aElement)
		{
			T theOld = itsCurrentElement;
			itsCurrentElement = aElement;
			currentElementChanged(theOld, itsCurrentElement);
		}
	}
	
	protected void currentElementChanged(T aOld, T aNew)
	{
	}
	
	/**
	 * Sends appropriate mouse enter/exit events to elements.
	 */
	public void mouseMoved (MouseEvent e)
	{
		Point2D.Float thePoint = pixelToRoot(e.getPoint());
		setCurrentElement(getElementAt(thePoint, MouseAction.MOVE));
		if (itsCurrentElement == null) popElements (e);
		else 
		{
			do
			{
				if (itsPathToCurrentElement.size() == 0)
				{
					pushElements(null, itsCurrentElement, e);
					break;
				}
				else
				{
					T theCurrentElement = itsPathToCurrentElement.peek();
					
					if (itsCurrentElement.equals(theCurrentElement)) break;
					else if (isAncestor(theCurrentElement, itsCurrentElement))
					{
						pushElements(theCurrentElement, itsCurrentElement, e);
						break;
					}
					else popElement(e);
					
				}
			} while (itsPathToCurrentElement.size() > 0);
			
			IMouseAware theMouseAware = getMouseAware(itsCurrentElement);
			if (theMouseAware != null) theMouseAware.mouseMoved(e, thePoint);
		}
		
		e.consume();
	}
	
	/**
	 * Pushes and sends mouse entered events to the given element.
	 */
	private void pushElement (T aElement, MouseEvent aEvent)
	{
		itsPathToCurrentElement.push(aElement);
		IMouseAware theMouseAware = getMouseAware(aElement);
		if (theMouseAware != null) theMouseAware.mouseEntered(aEvent);
	}

	/**
	 * Pushes and sends mouse entered events to the element given as second argument
	 * and its ancestors, up to but not including the given encestor.
	 */
	private void pushElements (T aAncestor, T aElement, MouseEvent aEvent)
	{
		List<T> theElements = new ArrayList<T> ();
		T theElement = aElement;
		while (theElement != null && theElement != aAncestor)
		{
			theElements.add (theElement);
			if (! itsRecursiveRollover) break;
			theElement = getParent(theElement);
		}
		
		for (Iterator<T> theIterator = new ReverseIteratorWrapper<T>(theElements); theIterator.hasNext();)
		{
			theElement = theIterator.next();
			pushElement(theElement, aEvent);
		}
	}

	/**
	 * Pops an element and sends it a mouse exited message.
	 */
	private void popElement (MouseEvent aEvent)
	{
		T theElement = itsPathToCurrentElement.pop();
		IMouseAware theMouseAware = getMouseAware(theElement);
		if (theMouseAware != null) theMouseAware.mouseExited(aEvent);
	}

	/**
	 * Pops all elements (and sends them mouse exited.
	 */
	private void popElements (MouseEvent aEvent)
	{
		while (itsPathToCurrentElement.size() > 0) popElement(aEvent);
	}

	public void mouseClicked (MouseEvent e)
	{
	}

	public void mousePressed (MouseEvent e)
	{
		if (itsComponent != null) itsComponent.grabFocus();
		
		Point2D.Float thePoint = pixelToRoot(e.getPoint());
		setCurrentElement(getElementAt(thePoint, MouseAction.PRESS));
		itsPressedElement = itsCurrentElement;

		itsPressX = e.getX();
		itsPressY = e.getY();
		itsPressTime = System.currentTimeMillis();
		
		fireMousePressed(e, thePoint, itsCurrentElement);
	}
	
	public void mouseWheelMoved(MouseWheelEvent e)
	{
		Point2D.Float thePoint = pixelToRoot(e.getPoint());
		setCurrentElement(getElementAt(thePoint, MouseAction.WHEEL));

		fireMouseWheelMoved(e, thePoint, itsCurrentElement);
	}

	public void mouseReleased (MouseEvent e)
	{
		IMouseAware theMouseAware = getMouseAware(itsPressedElement);
		if (SwingUtilities.isLeftMouseButton(e))
		{
			if (theMouseAware != null)
			{
				Point2D.Float theRootPoint = pixelToRoot(e.getPoint());
				Point2D.Float theTransformedPoint = rootToLocal(itsPressedElement, theRootPoint);
				if (itsDragging)
				{
					theMouseAware.commitDrag(theTransformedPoint);
					itsDragging = false;
				}
				else
				{
					fireMouseClicked(e, theRootPoint, itsPressedElement);
				}
				fireMouseReleased(e, theRootPoint, itsPressedElement);
			}
		}
	}

	public void mouseDragged (MouseEvent e)
	{
		IMouseAware theMouseAware = getMouseAware(itsPressedElement);
		if (SwingUtilities.isLeftMouseButton(e) && theMouseAware != null)
		{
			if (itsDragging)
			{
				Point2D.Float theTransformedPoint = pixelToLocal(itsPressedElement, e.getPoint());
				theMouseAware.drag (theTransformedPoint);
			}
			else
			{
				int theDX = itsPressX - e.getX();
				int theDY = itsPressY - e.getY();
				int theDist = theDX * theDX + theDY * theDY;
				long theTime = System.currentTimeMillis() - itsPressTime;
				if (theDist > 4 || theTime > 300) 
				{
					itsDragging = true;
					Point2D.Float theTransformedPoint = pixelToLocal(itsPressedElement, new Point (itsPressX, itsPressY));
					theMouseAware.startDrag(e, theTransformedPoint);
				}
			}
		}
	}

	public void mouseEntered (MouseEvent e)
	{
		mouseMoved(e);
	}

	public void mouseExited (MouseEvent e)
	{
		popElements(e);
	}

	public void keyTyped (KeyEvent e)
	{
	}

	public void keyPressed (KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE && itsDragging)
		{
			IMouseAware theMouseAware = getMouseAware(itsPressedElement);
			theMouseAware.cancelDrag();
			itsDragging = false;
			itsPressedElement = null;
		}
	}

	public void keyReleased (KeyEvent e)
	{
	}
	
	private void fireMouseWheelMoved(MouseWheelEvent aEvent, Point2D.Float aRootPoint, T aElement)
	{
		while (aElement != null)
		{
			IMouseAware theMouseAware = getMouseAware(aElement);
			if (theMouseAware != null) 
			{
				Point2D.Float thePoint = rootToLocal(aElement, aRootPoint);
				if (theMouseAware.mouseWheelMoved(aEvent, thePoint)) break;
			}
			aElement = getParent(aElement);
		}
	}
	
	private void fireMousePressed (MouseEvent aEvent, Point2D.Float aRootPoint, T aElement)
	{
		while (aElement != null)
		{
			IMouseAware theMouseAware = getMouseAware(aElement);
			if (theMouseAware != null) 
			{
				Point2D.Float thePoint = rootToLocal(aElement, aRootPoint);
				if (theMouseAware.mousePressed(aEvent, thePoint)) break;
			}
			aElement = getParent(aElement);
		}
	}
	
	private void fireMouseReleased (MouseEvent aEvent, Point2D.Float aRootPoint, T aElement)
	{
		while (aElement != null)
		{
			IMouseAware theMouseAware = getMouseAware(aElement);
			if (theMouseAware != null) 
			{
				Point2D.Float thePoint = rootToLocal(aElement, aRootPoint);
				if (theMouseAware.mouseReleased(aEvent, thePoint)) break;
			}
			aElement = getParent(aElement);
		}
	}
	
	private void fireMouseClicked (MouseEvent aEvent, Point2D.Float aRootPoint, T aElement)
	{
		while (aElement != null)
		{
			IMouseAware theMouseAware = getMouseAware(aElement);
			if (theMouseAware != null) 
			{
				Point2D.Float thePoint = rootToLocal(aElement, aRootPoint);
				if (theMouseAware.mouseClicked(aEvent, thePoint)) break;
			}
			aElement = getParent(aElement);
		}
	}
	
	public static enum MouseAction
	{
		PRESS, MOVE, WHEEL;
	}
}
