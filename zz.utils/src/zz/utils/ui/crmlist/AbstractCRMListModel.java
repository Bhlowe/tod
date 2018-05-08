/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui.crmlist;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

/**
 * Abstract class to help implementing editable list models.
 * Subclasses can provide two levels of implementation:
 * <li>An abstract subclass can simply implement the access to the backing list
 * through the {@link javax.swing.ListModel#getSize()},
 * {@link javax.swing.ListModel#getElementAt(int)},
 * {@link #hasList0()}, {@link #addElement0(int, Object)} and 
 * {@link #removeElement0(int)} methods.
 * <li>Concrete subclasses must also implement {@link #newElement()}.
 * @author gpothier
 */
public abstract class AbstractCRMListModel<E> extends AbstractListModel 
implements CRMListModel
{

	/**
	 * This is the method that subclasses must implement. It simply
	 * instantiates a new element.
	 */
	protected abstract E newElement ();
	
	/**
	 * Indicates if this model currently has its backing list.
	 */
	protected abstract boolean hasList0 ();
	
	protected abstract void addElement0 (int aIndex, E aElement);
	protected abstract E removeElement0 (int aIndex);
	
	public boolean canCreateElement()
	{
		return hasList0();
	}

	public boolean canMoveElement(int aSourceIndex, int aTargetIndex)
	{
		return hasList0() 
			&&aSourceIndex != aTargetIndex
			&& aSourceIndex >= 0
			&& aTargetIndex >= 0
			&& aTargetIndex < getSize();
	}

	public boolean canRemoveElement(int aIndex)
	{
		return hasList0()
			&& aIndex >= 0 
			&& aIndex < getSize();
	}

	public int createElement()
	{
		E theElement = newElement();
		if (theElement == null) return -1;
		
		addElement(theElement);
		
		return getSize()-1;
	}

	public void moveElement(int aSourceIndex, int aTargetIndex)
	{
		E theElement = removeElement0(aSourceIndex);
		addElement0 (aTargetIndex, theElement);
		fireContentsChanged(this, aSourceIndex, aTargetIndex);
	}
	
	public void addElement(E aElement)
	{
		addElement0(getSize(), aElement);
		fireIntervalAdded(this, getSize()-1, getSize()-1);		
	}

	public void removeElement(int aIndex)
	{
		removeElement0(aIndex);
		fireIntervalRemoved(this, aIndex, aIndex);
	}

	/**
	 * Notifies listeners that the whole content changed,
	 */
	protected void fireContentsChanged ()
	{
		fireContentsChanged(this, 0, getSize()-1);
	}
}
