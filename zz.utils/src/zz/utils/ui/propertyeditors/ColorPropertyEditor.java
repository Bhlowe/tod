package zz.utils.ui.propertyeditors;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JComponent;

import zz.utils.properties.IRWProperty;
import zz.utils.ui.ColorChooserPanel;
import zz.utils.ui.OptionListener;
import zz.utils.ui.UIUtils;
import zz.utils.ui.popup.ButtonPopupComponent;
import zz.utils.undo2.UndoStack;

public abstract class ColorPropertyEditor
{
	@SuppressWarnings("serial")
	public static class ButtonPopup extends SimplePropertyEditor<Color> 
	implements OptionListener
	{
		private JButton itsButton;
		private ButtonPopupComponent itsButtonPopup;
		private ColorChooserPanel itsChooserPanel;
		
		public ButtonPopup(UndoStack aUndoStack, IRWProperty<Color> aProperty)
		{
			super(aUndoStack, aProperty);
			itsButton = new JButton();
			itsButton.setMargin(UIUtils.NULL_INSETS);
			itsButton.setPreferredSize(new Dimension(20, 20));
			itsButtonPopup = new ButtonPopupComponent(null, itsButton)
			{
				@Override
				public JComponent getPopupComponent()
				{
					if (itsChooserPanel == null) 
					{
						itsChooserPanel = new ColorChooserPanel();
						itsChooserPanel.addOptionListener(ButtonPopup.this);
					}
					return itsChooserPanel;
				}
			};
			setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
			add(itsButtonPopup);
		}

		@Override
		protected void valueToUi(Color aValue)
		{
			itsButton.setBackground(aValue);
			if (itsChooserPanel != null) itsChooserPanel.setSelectedColor(aValue);
		}

		@Override
		protected Color uiToValue()
		{
			return itsChooserPanel != null ? itsChooserPanel.getSelectedColor() : itsButton.getBackground();
		}

		public void optionSelected(Option aOption)
		{
			if (aOption == Option.OK) uiToProperty();
			itsButtonPopup.hidePopup();
		}
		
		
	}
}
