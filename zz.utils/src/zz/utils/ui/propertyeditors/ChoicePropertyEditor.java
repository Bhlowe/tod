package zz.utils.ui.propertyeditors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import zz.utils.properties.IRWProperty;
import zz.utils.ui.StackLayout;
import zz.utils.undo2.UndoStack;

public abstract class ChoicePropertyEditor
{
	@SuppressWarnings("serial")
	public static class ComboBox<T> extends SimplePropertyEditor<T> implements ActionListener
	{
		private JComboBox itsComboBox;
		
		public ComboBox(UndoStack aUndoStack, IRWProperty<T> aProperty, T[] aValues)
		{
			super(aUndoStack, aProperty);
			setLayout(new StackLayout());
			itsComboBox = new JComboBox(aValues);
			add(itsComboBox);
			itsComboBox.addActionListener(this);
		}

		@Override
		protected T uiToValue()
		{
			return (T) itsComboBox.getSelectedItem();
		}

		@Override
		protected void valueToUi(T aValue)
		{
			itsComboBox.setSelectedItem(aValue);
		}

		public void actionPerformed(ActionEvent aE)
		{
			uiToProperty();
		}
	}
}
