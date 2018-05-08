/*
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 23 oct. 2002
 * Time: 22:50:44
 * To change this template use Options | File Templates.
 */
package zz.utils.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;

public class FocusValidatedCheckBox extends JCheckBox implements ActionListener, FocusListener
{
	private List itsValidateListeners = new ArrayList ();
	private boolean itsCurrentValue;

	public FocusValidatedCheckBox ()
	{
		init (false);
	}

	public FocusValidatedCheckBox (Action a)
	{
		super (a);
		init (false);
	}

	public FocusValidatedCheckBox (Icon icon)
	{
		super (icon);
		init (false);
	}

	public FocusValidatedCheckBox (Icon icon, boolean selected)
	{
		super (icon, selected);
		init (selected);
	}

	public FocusValidatedCheckBox (String text)
	{
		super (text);
		init (false);
	}

	public FocusValidatedCheckBox (String text, Icon icon)
	{
		super (text, icon);
		init (false);
	}

	public FocusValidatedCheckBox (String text, Icon icon, boolean selected)
	{
		super (text, icon, selected);
		init (selected);
	}

	public FocusValidatedCheckBox (String text, boolean selected)
	{
		super (text, selected);
		init (selected);
	}

	private void init (boolean aValue)
	{
		addActionListener(this);
		addFocusListener(this);
		itsCurrentValue = aValue;
	}

	public void actionPerformed (ActionEvent e)
	{
		save ();
	}

	public void focusGained (FocusEvent e)
	{
		itsCurrentValue = isSelected();
	}

	public void focusLost (FocusEvent e)
	{
		save ();
	}

	private void save ()
	{
		boolean theNewValue = isSelected();
		if (theNewValue != itsCurrentValue) fireValidate();
		itsCurrentValue = theNewValue;
	}


	public void addValidateListener (ValidateListener aListener)
	{
		itsValidateListeners.add (aListener);
	}

	public void removeValidateListener (ValidateListener aListener)
	{
		itsValidateListeners.remove (aListener);
	}

	private void fireValidate ()
	{
		for (Iterator theIterator = itsValidateListeners.iterator (); theIterator.hasNext ();)
		{
			ValidateListener theListener = (ValidateListener) theIterator.next ();
			theListener.validateInput();
		}
	}
}
