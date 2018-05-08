/*
 * Created on 20-oct-2004
 */
package zz.utils.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Point2D;

/**
 * This interface should be implemented by objects
 * that need to receive mouse events. 
 * @author gpothier
 */
public interface IMouseAware 
{
	/**
	 * Indicates if this object is currently interested in mouse events.
	 * If not, the event will be passed to the closest mouse aware
	 * ancestor, exactly as if this object did not implement
	 * {@link IMouseAware}
	 */
	public boolean isMouseAware ();
	
	/**
	 * Called when the mouse button is pressed on this object.
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 * @return Whether to consume the event or not; if the event
	 * is not consumed it will be passed to another mouse aware object.
	 */
	public boolean mousePressed (MouseEvent aEvent, Point2D.Float aPoint);
	
	/**
	 * Called when the mouse button is released on this object.
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 * @return Whether to consume the event or not; if the event
	 * is not consumed it will be passed to another mouse aware object.
	 */
	public boolean mouseReleased (MouseEvent aEvent, Point2D.Float aPoint);
	
	/**
	 * Called when the mouse button is pressed & released on this object,
	 * but only if no dragging occured.
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 * @return Whether to consume the event or not; if the event
	 * is not consumed it will be passed to another mouse aware object.
	 */
	public boolean mouseClicked (MouseEvent aEvent, Point2D.Float aPoint);
	
	
	/**
	 * Called when the mouse enters the bounds of this object.
	 * @param aEvent TODO
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 */
	public void mouseEntered (MouseEvent aEvent);

	/**
	 * Called when the mouse exits the bounds of this object.
	 * @param aEvent TODO
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 */
	public void mouseExited (MouseEvent aEvent);
	
	/**
	 * Called when the mouse moves within the bounds of this object.
	 * @param aEvent TODO
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 * @return Whether to consume the event or not; if the event
	 * is not consumed it will be passed to another mouse aware object.
	 */
	public boolean mouseMoved (MouseEvent aEvent, Point2D.Float aPoint);

	/**
	 * Called when the mouse wheel is moved within the bounds of this object.
	 */
	public boolean mouseWheelMoved (MouseWheelEvent aEvent, Point2D.Float aPoint);
	
	/**
	 * Called when a drag gesture begins.
	 * Note that this method is called in addition to {@link #mousePressed(Point2D)},
	 * after it is detected that a drag operation starts.
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 * @return Whether to consume the event or not; if the event
	 * is not consumed it will be passed to another mouse aware object.
	 */
	public boolean startDrag (MouseEvent aEvent, Point2D.Float aPoint);
	
	/**
	 * Once a drag gesture has started, this method is called whenever
	 * the mouse moves.
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 */
	public void drag (Point2D.Float aPoint);

	/**
	 * Called when a drag gesture ends.
	 * Note that this method is called in addition to {@link #mouseReleased(Point2D)}
	 * @param aPoint The mouse coordinate in this object's coordinate system
	 */
	public void commitDrag (Point2D.Float aPoint);
	
	/**
	 * Called when the user cancels a drag gesture.
	 */
	public void cancelDrag(); 
}
