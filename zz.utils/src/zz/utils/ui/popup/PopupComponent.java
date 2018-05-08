/**
 * PopupComponent.java
 *
 * @author Guillaume POTHIER
 */

package zz.utils.ui.popup;

import java.awt.BorderLayout;

import javax.swing.JComponent;

import zz.utils.notification.IEvent;
import zz.utils.ui.StackLayout;
import zz.utils.ui.TransparentPanel;

/**
 * A component that handles a popup component. PopupComponent has a content, which
 * is the component that is always displayed, and a popup, the component that pops
 * up on the screen when requested.<p>
 * If the popup implements the interface {@link PopupInterface}, it can have access to its PopupComponent.
 * If it implements the interface {@link PopupListener}, it is automatically notified
 * when the popup is shown/hidden.
 */
public class PopupComponent extends TransparentPanel 
{
	private StickyPopup itsPopup;
	private JComponent itsPopupComponent;

	public PopupComponent ()
	{
		this (null, null);
	}

	public PopupComponent (JComponent popup)
	{
		this (popup, null);
	}

	public PopupComponent (JComponent popup, JComponent content)
	{
		itsPopupComponent = popup;
		itsPopup = new StickyPopup (null, content)
		{
			@Override
			public JComponent getContent()
			{
				return getPopupComponent();
			}
		};
		setLayout (new StackLayout ());
		setContent (content);
	}

	public IEvent<Void> ePopupShowing()
	{
		return itsPopup.ePopupShowing();
	}

	public IEvent<Void> ePopupShown()
	{
		return itsPopup.ePopupShown();
	}

	public IEvent<Void> ePopupHidden()
	{
		return itsPopup.ePopupHidden();
	}

	public void setPreferredDirection (int aPreferredDirection)
	{
		itsPopup.setPreferredDirection(aPreferredDirection);
	}

	public void setPopupComponent (JComponent aPopup)
	{
		itsPopup.setContent(aPopup);
		itsPopupComponent = aPopup;
	}

	/**
	 * Returns the content displayed by the popup.
	 */
	public JComponent getPopupComponent ()
	{
		return itsPopupComponent;
	}

	public StickyPopup getPopup ()
	{
		return itsPopup;
	}

	public void setContent (JComponent aContent)
	{
		removeAll ();
		if (aContent != null) add (aContent, BorderLayout.CENTER);
		itsPopup.setTriggerComponent(aContent);
	}
	
	/**
	 * Returns the component that is always visible.
	 */
	public JComponent getContent()
	{
		return (JComponent) getComponent(0);
	}

	public boolean isPopupShown ()
	{
		return itsPopup.isPopupShown();
	}

	public void togglePopup ()
	{
		itsPopup.togglePopup();
	}


	public void showPopup ()
	{
		itsPopup.show();
	}

	public void hidePopup ()
	{
		itsPopup.hide();
	}
}

