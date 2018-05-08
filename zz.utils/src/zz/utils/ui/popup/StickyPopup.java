/*
 * Created on 20-may-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui.popup;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRootPane;

import zz.utils.Utils;
import zz.utils.ui.UIUtils;


/**
 * A popup that displays next to a given component.
 */
public class StickyPopup extends AbstractPopup 
{
	private JComponent itsTriggerComponent;

	/**
	 * The direction where to place the popup. Must be one of JLabel.TOP, JLabel.BOTTOM, JLabel.LEFT,
	 * JLabel.RIGHT
	 */
	private int itsPreferredDirection = JLabel.BOTTOM;


	public StickyPopup (JComponent aContent, JComponent aTriggerComponent)
	{
		super (aContent);
		setTriggerComponent(aTriggerComponent);
	}

	
	public JComponent getOwner()
	{
		return itsTriggerComponent;
	}
	
	/**
	 * @param aPreferredDirection Specifies in which direction the popup should open if
	 * there is no space constraint.
	 * One of JLabel.TOP, JLabel.BOTTOM, JLabel.LEFT, JLabel.RIGHT
	 */
	public void setPreferredDirection (int aPreferredDirection)
	{
		itsPreferredDirection = aPreferredDirection;
	}

	public JComponent getTriggerComponent ()
	{
		return itsTriggerComponent;
	}

	public void setTriggerComponent (JComponent aTriggerComponent)
	{
		itsTriggerComponent = aTriggerComponent;
	}


	protected Rectangle getPopupBounds ()
	{
		return PopupUtils.computePopupBounds(getRootPane(), itsTriggerComponent, 
				itsPreferredDirection, getContent().getPreferredSize());
	}
	
	@Override
	protected JRootPane getRootPane()
	{
		return itsTriggerComponent.getRootPane();
	}

}