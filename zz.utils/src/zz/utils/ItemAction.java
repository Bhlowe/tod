/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 27, 2001
 * Time: 12:25:04 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * A simple concrete subclass of AbstractAction that define useful setters. It delegates
 * actionPerformed events to another actionListener.
 */
public class ItemAction extends AbstractAction
{
	protected ActionListener itsActionListener;

	public void setActionListener (ActionListener aActionListener)
	{
		itsActionListener = aActionListener;
	}

	public void setTitle (String aTitle)
	{
		putValue(Action.NAME, aTitle);
	}

	public String getTitle ()
	{
		return (String) getValue(Action.NAME);
	}

	public void setDescription (String aDescription)
	{
		putValue(Action.SHORT_DESCRIPTION, aDescription);
	}
	
	public String getDescription ()
	{
		return (String) getValue(Action.SHORT_DESCRIPTION);
	}
	
	public void setIcon (Icon anIcon)
	{
		putValue(Action.SMALL_ICON, anIcon);
	}

	public Icon getIcon ()
	{
		return (Icon) getValue(Action.SMALL_ICON);
	}

	public void setCommandString (String aCommandString)
	{
		putValue(Action.ACTION_COMMAND_KEY, aCommandString);
	}

	public void actionPerformed (ActionEvent e)
	{
		if (itsActionListener != null) itsActionListener.actionPerformed(e);
	}
}
