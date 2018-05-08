/*
 * Created on Apr 6, 2005
 */
package zz.utils.list;

import javax.swing.AbstractListModel;
import javax.swing.event.ListDataListener;

/**
 * Wraps an {@link zz.utils.list.IList} into an {@link javax.swing.ListModel}
 * @author gpothier
 */
public class SwingListModel<T> extends AbstractListModel
implements IListListener<T>
{
	private IList<T> itsList;
	private boolean itsActive = false;
	
	public SwingListModel()
	{
	}

	public SwingListModel(IList<T> aList)
	{
		setList(aList);
	}
	
	protected void activate()
	{
		itsActive = true;
		if (itsList != null) itsList.addListener(this);
	}
	
	protected void deactivate()
	{
		itsActive = false;
		if (itsList != null) itsList.removeListener(this);
	}
	
	@Override
	public void addListDataListener(ListDataListener aL)
	{
		if (listenerList.getListenerCount() == 0) activate();
		super.addListDataListener(aL);
	}
	
	@Override
	public void removeListDataListener(ListDataListener aL)
	{
		super.removeListDataListener(aL);
		if (listenerList.getListenerCount() == 0) deactivate();
	}

	protected IList<T> getList()
	{
		return itsList;
	}

	protected void setList(IList<T> aList)
	{
		if (itsActive && itsList != null) itsList.removeListener(this);
		itsList = aList;
		if (itsActive && itsList != null) itsList.addListener(this);
		fireContentsChanged();
	}
	
	public T getElementAt(int aIndex)
	{
		return itsList.get(aIndex);
	}

	public int getSize()
	{
		return itsList != null ? itsList.size() : 0;
	}

	public void fireContentsChanged()
	{
		fireContentsChanged(this, 0, getSize());
	}

	public void elementAdded(IList<T> aList, int aIndex, T aElement)
	{
		fireContentsChanged();
	}

	public void elementRemoved(IList<T> aList, int aIndex, T aElement)
	{
		fireContentsChanged();
	}
	
	
}
