/*
 * Created on Sep 29, 2005
 */
package zz.utils.ui;

import java.awt.event.InputEvent;

/**
 * Utility class to help dealing with key modifiers
 * used with input events. It is helpful if one doesn't 
 * want to deal with each modifier independently but rather
 * consider their combinations.
 * <br/>
 * There are also utility methods for determining the state of 
 * a particular modifier.
 * @author gpothier
 */
public enum InputModifiers
{
	NONE,
	CTRL, SHIFT, ALT,
	CTRL_SHIFT, CTRL_ALT, SHIFT_ALT,
	CTRL_SHIFT_ALT;
	
	/**
	 * Returns the modifier corresponding to the given event.
	 */
	public static InputModifiers getModifiers (InputEvent aEvent)
	{
		int theModifiers = aEvent.getModifiersEx() 
			& (InputEvent.SHIFT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK);
		
		if (theModifiers == 0) return NONE;
		else if (theModifiers == InputEvent.CTRL_DOWN_MASK) return CTRL;
		else if (theModifiers == InputEvent.SHIFT_DOWN_MASK) return SHIFT;
		else if (theModifiers == InputEvent.ALT_DOWN_MASK) return ALT;
		else if (theModifiers == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)) return CTRL_SHIFT;
		else if (theModifiers == (InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) return CTRL_ALT;
		else if (theModifiers == (InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) return SHIFT_ALT;
		else if (theModifiers == (InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) return CTRL_SHIFT_ALT;
		else throw new RuntimeException("Not handled: "+theModifiers);
	}
	
	public static boolean hasShift (InputEvent aEvent)
	{
		return (aEvent.getModifiersEx() & InputEvent.SHIFT_DOWN_MASK) != 0;
	}
	
	public static boolean hasCtrl (InputEvent aEvent)
	{
		return (aEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;		
	}
	
	public static boolean hasAlt (InputEvent aEvent)
	{
		return (aEvent.getModifiersEx() & InputEvent.ALT_DOWN_MASK) != 0;
	}
}
