package zz.utils.ui.mvc.view.awt;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.mvc.model.ActionModel;

public class MenuItemView extends MenuItem 
{
	private ActionModel model;
	
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
	
	private final ActionListener actionListener = new ActionListener() 
	{
		@Override
		public void actionPerformed(ActionEvent e) 
		{
			model.action.run();
		}
	};
	
	public MenuItemView(ActionModel model) 
	{
		super(model.pLabel.get());
		this.model = model;
		setEnabled(model.pEnabled.get());
		model.pEnabled.addListener(enabledListener);
		model.pLabel.addListener(labelListener);
		addActionListener(actionListener);
	}
}