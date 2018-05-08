/*
 * Created on Oct 15, 2006
 */
package zz.utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

/**
 * An action whose attributes can be conveniently specified in the constructor.
 * @author gpothier
 */
public abstract class SimpleAction extends AbstractAction
{
	public SimpleAction(Icon aIcon, String aDescription)
	{
		this("", aIcon, aDescription);
	}
	
	public SimpleAction(String aTitle, Icon aIcon, String aDescription)
	{
		putValue(Action.NAME, aTitle);
		putValue(Action.SHORT_DESCRIPTION, aDescription);
		putValue(Action.SMALL_ICON, aIcon);
	}
	
	public SimpleAction(String aTitle, String aDescription)
	{
		this(aTitle, null, aDescription);
	}
	
	public SimpleAction(String aTitle)
	{
		this(aTitle, null);
	}
}
