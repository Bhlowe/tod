package zz.utils.ui.mvc.view.swing;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.mvc.model.PropertyModel;

public class JCheckBoxView extends JCheckBox
{
	private PropertyModel<Boolean> model;
	
	private final IPropertyListener<String> labelListener = new IPropertyListener<String>() {
		@Override
		public void propertyChanged(IProperty<String> aProperty, String aOldValue, String aNewValue) {
			setText(aNewValue);
		}
	};
	
	private final IPropertyListener<Boolean> enabledListener = new IPropertyListener<Boolean>() {
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue) {
			setEnabled(aNewValue);
		}
	};
	
	private final IPropertyListener<Boolean> valueListener = new IPropertyListener<Boolean>()
	{
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue)
		{
			if (aOldValue != aNewValue) setSelected(aNewValue);
		}
	};
	
	private final ChangeListener changeListener = new ChangeListener()
	{
		@Override
		public void stateChanged(ChangeEvent aE)
		{
			updateProperty();
		}
	};
	
	public JCheckBoxView(PropertyModel<Boolean> model) {
		super(model.pLabel.get());
		this.model = model;
		setEnabled(model.pEnabled.get());
		setSelected(model.pValue.get());
		addChangeListener(changeListener);
	}

	@Override
	public void addNotify()
	{
		super.addNotify();
		model.pEnabled.addHardListener(enabledListener);
		model.pLabel.addHardListener(labelListener);
		model.pValue.addHardListener(valueListener);
	}
	
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		model.pEnabled.removeListener(enabledListener);
		model.pLabel.removeListener(labelListener);
		model.pValue.removeListener(valueListener);
	}

	
	private void updateProperty() {
		if (model.pValue.get() != isSelected())
			model.pValue.set(isSelected());
	}
}
