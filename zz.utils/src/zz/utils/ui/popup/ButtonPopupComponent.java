/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 26 mars 2003
 * Time: 02:36:54
 */
package zz.utils.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import zz.utils.Utils;
import zz.utils.notification.IEvent;
import zz.utils.notification.IEventListener;
import zz.utils.ui.StackLayout;
import zz.utils.ui.UIUtils;


/**
 * A popup component that automatically handles click on a button to toggle the popup.
 */
public class ButtonPopupComponent extends PopupComponent implements ActionListener
{
	private final JButton itsButton;
	private long itsHiddenTime;
	
	private final IEventListener<Void> itsHiddenListener = new IEventListener<Void>()
	{
		public void fired(IEvent< ? extends Void> aEvent, Void aData)
		{
			itsHiddenTime = System.currentTimeMillis();
		}
	};
	
	public ButtonPopupComponent (JComponent popup, String aTitle)
	{
		this (popup, aTitle, null);
	}
	
	public ButtonPopupComponent (JComponent popup, String aTitle, Icon aIcon)
	{
		this (popup, new JButton(aTitle, aIcon));
	}
	
	public ButtonPopupComponent (JComponent popup, JButton aButton)
	{
		super (popup, aButton);
		setLayout(new StackLayout());
		if (aButton != null) aButton.addActionListener(this);
		itsButton = aButton;
	}

	public ButtonPopupComponent (JButton aButton)
	{
		this(null, aButton);
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		ePopupHidden().addListener(itsHiddenListener);
	}
	
	@Override
	public void removeNotify()
	{
		super.removeNotify();
		ePopupHidden().removeListener(itsHiddenListener);
	}

	public void actionPerformed (ActionEvent e)
	{
		if (! isPopupShown())
		{
			// This is a hack to avoid reshowing the popup after it was hidden by
			// clicking on the button.
			if (System.currentTimeMillis()-itsHiddenTime < 300) return;
		}
		togglePopup();
	}
	
	@Override
	public void setEnabled(boolean aEnabled)
	{
		getContent().setEnabled(aEnabled);
	}
	
	public JButton getButton()
	{
		return itsButton;
	}
}
