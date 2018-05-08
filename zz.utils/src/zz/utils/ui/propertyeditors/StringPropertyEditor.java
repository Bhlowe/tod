package zz.utils.ui.propertyeditors;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import zz.utils.properties.IRWProperty;
import zz.utils.undo2.UndoStack;

public abstract class StringPropertyEditor
{
	@SuppressWarnings("serial")
	public static class TextField extends SimplePropertyEditor<String>
	implements ActionListener, FocusListener, DocumentListener
	{
		private final JTextField itsTextField = new JTextField();
		
		public TextField(UndoStack aUndoStack, IRWProperty<String> aProperty)
		{
			this(aUndoStack, aProperty, false);
		}
		
		public TextField(UndoStack aUndoStack, IRWProperty<String> aProperty, boolean aImmediate)
		{
			super(aUndoStack, aProperty);
			setLayout(new BorderLayout()); // Needed by subclasses
			add(itsTextField, BorderLayout.CENTER);
			itsTextField.addActionListener(this);
			itsTextField.addFocusListener(this);
			if (aImmediate) itsTextField.getDocument().addDocumentListener(this);
		}
		
		public void focusGained(FocusEvent aE)
		{
		}
	
		public void focusLost(FocusEvent aE)
		{
			uiToProperty();
		}
	
		public void actionPerformed(ActionEvent aE)
		{
			uiToProperty();
		}
		
		@Override
		protected void valueToUi(String aValue)
		{
			itsTextField.setText(aValue);
		}
		
		@Override
		protected String uiToValue()
		{
			return itsTextField.getText();
		}
		
		public JTextField getTextField()
		{
			return itsTextField;
		}

		@Override
		public void insertUpdate(DocumentEvent aE)
		{
			uiToProperty();
		}

		@Override
		public void removeUpdate(DocumentEvent aE)
		{
			uiToProperty();
		}

		@Override
		public void changedUpdate(DocumentEvent aE)
		{
			uiToProperty();
		}
	}
}
