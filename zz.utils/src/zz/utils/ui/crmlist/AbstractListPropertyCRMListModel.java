package zz.utils.ui.crmlist;

import javax.swing.event.ListDataListener;

import zz.utils.list.IList;
import zz.utils.list.IListListener;
import zz.utils.properties.IListProperty;

public abstract class AbstractListPropertyCRMListModel<E> extends AbstractCRMListModel<E>
implements IListListener<E>
{
	private final IListProperty<E> itsProperty;
	
	public AbstractListPropertyCRMListModel(IListProperty<E> aProperty)
	{
		itsProperty = aProperty;
	}

	@Override
	public void addListDataListener(ListDataListener aListener)
	{
		if (listenerList.getListenerCount() == 0) itsProperty.addHardListener(this);
		super.addListDataListener(aListener);
	}

	@Override
	public void removeListDataListener(ListDataListener aListener)
	{
		super.removeListDataListener(aListener);
		if (listenerList.getListenerCount() == 0) itsProperty.removeListener(this);
	}

	public void elementAdded(IList<E> aList, int aIndex, E aElement)
	{
		fireIntervalAdded(this, aIndex, aIndex);
	}

	public void elementRemoved(IList<E> aList, int aIndex, E aElement)
	{
		fireIntervalRemoved(this, aIndex, aIndex);
	}

	protected void addElement0(int aIndex, E aElement)
	{
		itsProperty.add(aIndex, aElement);
	}
	
	protected boolean hasList0()
	{
		return itsProperty != null;
	}
	
	protected E removeElement0(int aIndex)
	{
		return itsProperty.remove(aIndex);
	}
	
	public E getElementAt(int aIndex)
	{
		return itsProperty.get(aIndex);
	}
	
	public int getSize()
	{
		return itsProperty.size();
	}

}
