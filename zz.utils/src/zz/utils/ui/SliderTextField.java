/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 22, 2002
 * Time: 3:59:55 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.BoundedRangeModel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This textfield follows a BoundedRangeModel to reflect/act upon the value of a JSlider
 * or any other component using a BoundedRangeModel.<p>
 * It can use an (optional) delegate to convert between slider values / textfield text.
 */
public class SliderTextField extends JPanel
{
	protected boolean itsUpdating = false;

	protected BoundedRangeModel itsBoundedRangeModel;

	protected Object itsDelegate;

	protected JTextField itsTextField;

	public SliderTextField (int columns, BoundedRangeModel aBoundedRangeModel)
	{
		super (new BorderLayout ());
		itsBoundedRangeModel = aBoundedRangeModel;

		itsTextField = new JTextField(columns);
		add (itsTextField, BorderLayout.CENTER);

		itsTextField.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				updateModel();
			}
		});

		itsTextField.addFocusListener(new FocusAdapter ()
		{
			public void focusLost (FocusEvent e)
			{
				updateModel();
			}
		});

		itsBoundedRangeModel.addChangeListener(new ChangeListener ()
		{
			public void stateChanged (ChangeEvent e)
			{
				updateText();
			}
		});

		updateText();
	}

	public void setDelegate (Object aDelegate)
	{
		itsDelegate = aDelegate;
	}

	protected void updateText ()
	{
		if (itsUpdating) return;

		itsUpdating = true;

		try
		{
			String theText = getText(itsBoundedRangeModel.getValue());
			itsTextField.setText(theText);
		}
		finally
		{
			itsUpdating = false;
		}
	}

	protected void updateModel ()
	{
		if (itsUpdating) return;

		itsUpdating = true;

		try
		{
			String theText = itsTextField.getText();
			int theValue = getValue(theText);
			itsBoundedRangeModel.setValue(theValue);
		}
		finally
		{
			itsUpdating = false;
		}
	}

	/**
	 * Uses the delegate if it is an instance of SliderTextFieldDelegate to retrieve
	 * the text for the specified value.
	 * Otherwise simply returns ""+aValue
	 */
	protected String getText (int aValue)
	{
		if (itsDelegate instanceof SliderTextFieldDelegate)
		{
			SliderTextFieldDelegate theDelegate = (SliderTextFieldDelegate) itsDelegate;
			return theDelegate.getText(aValue);
		}
		else return ""+aValue;
	}

	/**
	 * Uses the delegate if it is an instance of SliderTextFieldDelegate to retrieve
	 * the value for the specified text.
	 * Otherwise simply returns Integer.parseInt (aValue)
	 * @throws NumberFormatException
	 */
	protected int getValue (String aText)
	{
		if (itsDelegate instanceof SliderTextFieldDelegate)
		{
			SliderTextFieldDelegate theDelegate = (SliderTextFieldDelegate) itsDelegate;
			return theDelegate.getValue(aText);
		}
		else return Integer.parseInt (aText);
	}
}
