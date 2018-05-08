package zz.utils.ui.mvc.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.mvc.model.ActionModel;

public class JButtonView extends JButton
{
	private ActionModel model;
	
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
	
	private final ActionListener actionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			model.action.run();
		}
	};
	
	public JButtonView(ActionModel model) {
		super(model.pLabel.get());
		this.model = model;
		setEnabled(model.pEnabled.get());
		addActionListener(actionListener);
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		model.pEnabled.addHardListener(enabledListener);
		model.pLabel.addHardListener(labelListener);
	}
	
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		model.pEnabled.removeListener(enabledListener);
		model.pLabel.removeListener(labelListener);
	}
}
