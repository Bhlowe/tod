/*
 * Created on 20-may-2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui.popup;

import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import zz.utils.notification.IEvent;
import zz.utils.notification.IFireableEvent;
import zz.utils.notification.SimpleEvent;
import zz.utils.ui.UIUtils;


/**
 * Base class for displaying popups.
 * Delegates to subclasses the determination of where to display the popup.
 * @see #show
 * @see #hide
 */
public abstract class AbstractPopup 
{
	private JComponent itsContent;
	private boolean itsShown = false;
	private JPopupMenu itsPopupMenu;
	
	private final IFireableEvent<Void> ePopupShowing = new SimpleEvent<Void>();
	private final IFireableEvent<Void> ePopupShown = new SimpleEvent<Void>();
	private final IFireableEvent<Void> ePopupHidden = new SimpleEvent<Void>();

	/**
	 * Creates a popup object.
	 *
	 * @param aContent The content of the popup window.
	 */
	public AbstractPopup (JComponent aContent)
	{
		setContent (aContent);
		PopupManager.getInstance().registerPopup(this);
	}
	
	public IEvent<Void> ePopupShowing()
	{
		return ePopupShowing;
	}

	public IEvent<Void> ePopupShown()
	{
		return ePopupShown;
	}

	public IEvent<Void> ePopupHidden()
	{
		return ePopupHidden;
	}

	/**
	 * Returns the component that owns this popup. When the owner component is 
	 * removed, the popup should be hidden.
	 */
	public abstract JComponent getOwner ();
	
	public void setContent (JComponent aContent)
	{
		if (itsContent != aContent)
		{
			itsContent = aContent;
			if (isPopupShown())
			{
				hide();
				show();
			}
		}
	}

	/**
	 * Returns the content displayed by the popup.
	 */
	public JComponent getContent ()
	{
		return itsContent;
	}

	public boolean isPopupShown ()
	{
		return itsShown;
	}

	public void togglePopup ()
	{
		if (itsShown) hide ();
		else show ();
	}

	/**
	 * Returns the bounds where to display the popup.
	 */
	protected abstract Rectangle getPopupBounds ();
	
	/**
	 * Returns the frame that contains the popup.
	 * Might return null (eg. with SWT_AWT). In this case, {@link #getRootPane()} should
	 * be overridden.
	 */
	protected Frame getOwnerFrame ()
	{
		return UIUtils.getFrame(getRootPane());
	}
	
	/**
	 * Returns the root pane of the popup's main component
	 */
	protected abstract JRootPane getRootPane();

	/**
	 * Displays the popup window on the screen, next to the trigger component.
	 * Tries to respect the preferred direction.
	 */
	public void show ()
	{
		show (true);
	}

	/**
	 * Displays the popup window on the screen, next to the trigger component.
	 * Tries to respect the preferred direction.
	 */
	public void show (boolean aNotify)
	{
		if (itsShown) return;
		if (getContent () == null) return;

		itsPopupMenu = new JPopupMenu()
		{
			@Override
			public void menuSelectionChanged(boolean aIsIncluded)
			{
				// Avoid cancelling the menu
				super.menuSelectionChanged(aIsIncluded);
			}
		};
		itsPopupMenu.add(getContent());
		
		itsPopupMenu.addPopupMenuListener(new PopupMenuListener()
		{
			public void popupMenuCanceled(PopupMenuEvent aE)
			{
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent aE)
			{
				itsShown = false;
				ePopupHidden.fire(null);
			}

			public void popupMenuWillBecomeVisible(PopupMenuEvent aE)
			{
			}
			
		});

		if (aNotify) ePopupShowing.fire(null);
		
		Rectangle thePopupBounds = getPopupBounds();
		Point theLocationOnScreen = getOwner().getLocationOnScreen();
		itsPopupMenu.show(getOwner(), thePopupBounds.x-theLocationOnScreen.x, thePopupBounds.y-theLocationOnScreen.y);

		itsShown = true;

		if (aNotify) ePopupShown.fire(null);
	}

	/**
	 * Hides the popup.
	 */
	public void hide ()
	{
		hide (true);
	}

	/**
	 * Hides the popup.
	 */

	public void hide (boolean aNotify)
	{
		if (! itsShown) return;
//		if (itsAutoHide)
//		{
//			getRootPane().getLayeredPane().remove (itsScreen);
//		}
//		itsPopupWindow.setVisible(false);
		itsPopupMenu.setVisible(false);
		itsShown = false;

		if (aNotify) ePopupHidden.fire(null);
	}

}