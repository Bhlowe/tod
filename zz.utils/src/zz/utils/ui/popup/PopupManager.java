/*
 * Created on Jun 15, 2004
 */
package zz.utils.ui.popup;

import java.awt.Component;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;


/**
 * Manages all the popups of the system.
 * <li>Hide all popups that have as parent a specific component. 
 * @author gpothier
 */
public class PopupManager
{
	private static PopupManager INSTANCE = new PopupManager();

	public static PopupManager getInstance()
	{
		return INSTANCE;
	}

	private PopupManager()
	{
	}
	
	/**
	 * List of all the popup managed by this instance.
	 */
	private List itsPopups = new ArrayList ();
	
	/**
	 * Registers a popup. It will be weakly referenced.
	 */
	public void registerPopup (AbstractPopup aPopup)
	{
		itsPopups.add (new WeakReference(aPopup));
	}
	
	/**
	 * Hides all popups that have the specified component or one of its
	 * descendants as parent.
	 */
	public void hidePopupsOf (JComponent aComponent)
	{
		hideLocalPopupsOf(aComponent);
		int theCount = aComponent.getComponentCount();
		for (int i=0;i<theCount;i++)
		{
			Component theComponent = aComponent.getComponent(i);
			if (theComponent instanceof JComponent)
			{
				JComponent theChild = (JComponent) theComponent;
				hidePopupsOf(theChild);
			}
		}
	}
	
	/**
	 * Hides all popups that have the specified component as parent.
	 */
	private void hideLocalPopupsOf (JComponent aComponent)
	{
		for (Iterator theIterator = itsPopups.iterator(); theIterator.hasNext();)
		{
			WeakReference theReference = (WeakReference) theIterator.next();
			Object theObject = theReference.get();
			if (theObject == null) theIterator.remove();
			else
			{
				AbstractPopup thePopup = (AbstractPopup) theObject;
				if (thePopup.getOwner() == aComponent) thePopup.hide();
			}
			
		}
	}
}
