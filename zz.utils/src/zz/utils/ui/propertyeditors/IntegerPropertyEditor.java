package zz.utils.ui.propertyeditors;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zz.utils.properties.IRWProperty;
import zz.utils.ui.StackLayout;
import zz.utils.undo2.UndoStack;

public abstract class IntegerPropertyEditor
{
	@SuppressWarnings("serial")
	public static class Spinner extends SimplePropertyEditor<Integer>
	implements ChangeListener, FocusListener
	{
		private final JSpinner itsSpinner = new JSpinner(new SpinnerNumberModel());
		
		public Spinner(UndoStack aUndoStack, IRWProperty<Integer> aProperty)
		{
			super(aUndoStack, aProperty);
			setLayout(new StackLayout());
			add(itsSpinner);
			itsSpinner.addChangeListener(this);
			itsSpinner.addFocusListener(this);
		}
		
		public void focusGained(FocusEvent aE)
		{
		}

		public void focusLost(FocusEvent aE)
		{
			uiToProperty();
		}

		public void stateChanged(ChangeEvent aE)
		{
			uiToProperty();
		}
		
		@Override
		protected void valueToUi(Integer aValue)
		{
			itsSpinner.setValue(aValue != null ? aValue : 0);
		}
		
		@Override
		protected Integer uiToValue()
		{
			return (Integer) itsSpinner.getValue();
		}

	}

}
