package zz.utils.ui.mvc.view.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import zz.utils.Utils;
import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.UniversalRenderer;
import zz.utils.ui.mvc.model.ChoicePropertyModel;
import zz.utils.ui.propertyeditors.PropertyComboBoxModel;

public class JComboBoxView<T> extends JComboBox<T>
{
	private ChoicePropertyModel<T> model;
	
	private final IPropertyListener<Boolean> enabledListener = new IPropertyListener<Boolean>() {
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue) {
			setEnabled(aNewValue);
		}
	};
	
	private final IPropertyListener<Boolean> validListener = new IPropertyListener<Boolean>() {
		@Override
		public void propertyChanged(IProperty<Boolean> aProperty, Boolean aOldValue, Boolean aNewValue) {
			setValid(aNewValue);
		}
	};
	
	private final FocusListener focusListener = new FocusListener()
	{
		@Override
		public void focusLost(FocusEvent aE)
		{
		}
		
		@Override
		public void focusGained(FocusEvent aE)
		{
			((JComponent)getParent()).scrollRectToVisible(getBounds());
		}
	};
	
	
	public JComboBoxView(ChoicePropertyModel<T> model) 
	{
		setModel(new PropertyComboBoxModel<T>(model.pChoices, model.pValue, true));
		this.model = model;
		setEnabled(model.pEnabled.get());
		setValid(model.pValid.get());
		setSelectedItem(model.pValue.get());
		addFocusListener(focusListener);
		
		setRenderer(new UniversalRenderer<T>()
		{
			protected String getName(T value) {
				return JComboBoxView.this.format(value);
			};
		});
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		model.pEnabled.addHardListener(enabledListener);
		model.pValid.addHardListener(validListener);
	}
	
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		model.pEnabled.removeListener(enabledListener);
		model.pValid.removeListener(validListener);
	}

	
	private void setValid(boolean valid) {
		repaint();
	}

	protected String format(T value) {
		return value != null ? ""+value : "";
	}
	
	@Override
	protected void paintComponent(Graphics g0)
	{
		Graphics2D g = (Graphics2D) g0;
		super.paintComponent(g);
		if (! model.pValid.get()) {
			g.setColor(Color.PINK);
			Composite oldComposite = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setComposite(oldComposite);
		}
	}
}
