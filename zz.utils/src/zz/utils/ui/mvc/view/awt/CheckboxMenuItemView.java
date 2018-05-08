package zz.utils.ui.mvc.view.awt;

import java.awt.CheckboxMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.mvc.model.ActionModel;
import zz.utils.ui.mvc.model.PropertyModel;

public class CheckboxMenuItemView extends CheckboxMenuItem 
{
	private PropertyModel<Boolean> model;
	
	private final IPropertyListener<String> labelListener = new IPropertyListener<String>() 
	{
		@Override
		public void propertyChanged(IProperty<String> aProperty, String aOldValue, String aNewValue) 
		{
			setLabel(aNewValue);
		}
	};
	
	private final IPropertyListener<Boolean> enabledListener = new IPropertyListener<Boolean>() 
	{
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue) 
		{
			setEnabled(aNewValue);
		}
	};
	
	private final IPropertyListener<Boolean> valueListener = new IPropertyListener<Boolean>()
	{
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue)
		{
			setState(aNewValue);
		}
	};
	
	private final ActionListener actionListener = new ActionListener() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			if (getState() != model.pValue.get()) 
				model.pValue.set(getState());
		}
	};
	
	private final ItemListener itemListener = new ItemListener()
	{
		@Override
		public void itemStateChanged(ItemEvent aE)
		{
			if (getState() != model.pValue.get()) 
				model.pValue.set(getState());
		}
	};
	
	public CheckboxMenuItemView(PropertyModel<Boolean> model) 
	{
		super(model.pLabel.get());
		this.model = model;
		setEnabled(model.pEnabled.get());
		setState(model.pValue.get());
		model.pValue.addListener(valueListener);
		model.pEnabled.addListener(enabledListener);
		model.pLabel.addListener(labelListener);
		addActionListener(actionListener);
		addItemListener(itemListener);
	}

}
