/*
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 23 oct. 2002
 * Time: 22:39:48
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

import javax.swing.JTextField;
import javax.swing.text.Document;

public class FocusValidatedTextField extends JTextField implements ActionListener, FocusListener
{
	private List itsValidateListeners = new ArrayList ();
	private String itsCurrentValue = null;

	public FocusValidatedTextField ()
	{
		init(null);
	}

	public FocusValidatedTextField (String text)
	{
		super (text);
		init(text);
	}

	public FocusValidatedTextField (int columns)
	{
		super (columns);
		init(null);
	}

	public FocusValidatedTextField (String text, int columns)
	{
		super (text, columns);
		init(text);
	}

	public FocusValidatedTextField (Document doc, String text, int columns)
	{
		super (doc, text, columns);
		init(text);
	}

	private void init (String aValue)
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
		itsCurrentValue = getText();
	}

	public void focusLost (FocusEvent e)
	{
		save ();
	}

	private void save ()
	{
		String theNewValue = getText();
		if (! theNewValue.equals(itsCurrentValue)) fireValidate();
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
