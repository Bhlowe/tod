package zz.utils.ui.propertyeditors;

import javax.swing.AbstractListModel;
import javax.swing.ListModel;

import zz.utils.list.IList;
import zz.utils.list.IListListener;
import zz.utils.properties.IListProperty;

/**
 * Wraps an {@link IListProperty} into a Swing {@link ListModel}
 * @author gpothier
 */
public class PropertyListModel<T> extends AbstractListModel<T>
implements IListListener<T>
{
	private final IListProperty<T> itsListProperty;

	public PropertyListModel(IListProperty<T> aListProperty)
	{
		itsListProperty = aListProperty;
		itsListProperty.addListener(this);
	}
	
	public void elementAdded(IList<T> aList, int aIndex, T aElement)
	{
		fireIntervalAdded(this, aIndex, aIndex);
	}

	public void elementRemoved(IList<T> aList, int aIndex, T aElement)
	{
		fireIntervalRemoved(this, aIndex, aIndex);
	}

	public T getElementAt(int aIndex)
	{
		return itsListProperty.get(aIndex);
	}

	public int getSize()
	{
		return itsListProperty.size();
	} 
}
