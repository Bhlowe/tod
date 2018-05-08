package zz.utils.ui.mvc.view.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.font.TextAttribute;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;

import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.ui.mvc.model.ActionModel;

public class JLinkLabelView extends JLabel
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
	
	private final MouseListener mouseListener = new MouseAdapter() {
		public void mouseClicked(java.awt.event.MouseEvent e) {
			model.action.run();
		}
	};
	
	public JLinkLabelView(ActionModel model) {
		super(model.pLabel.get());
		this.model = model;
		
		setForeground(Color.BLUE);
		
		Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
		fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		setFont(getFont().deriveFont(fontAttributes));
		
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		
		setEnabled(model.pEnabled.get());
		addMouseListener(mouseListener);
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
