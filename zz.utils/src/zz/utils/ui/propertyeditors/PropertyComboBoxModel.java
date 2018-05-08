package zz.utils.ui.propertyeditors;

import javax.swing.ComboBoxModel;

import zz.utils.list.IList;
import zz.utils.properties.IListProperty;
import zz.utils.properties.IProperty;
import zz.utils.properties.IPropertyListener;
import zz.utils.properties.IRWProperty;

/**
 * Wraps an {@link IListProperty} into a Swing {@link ComboBoxModel}.
 * Uses an extra property to indicate the current selection.
 * @author gpothier
 */
public class PropertyComboBoxModel<T> extends PropertyListModel<T>
implements ComboBoxModel<T>, IPropertyListener<T>
{
	private IRWProperty<T> itsSelectedItemProperty;
	private boolean itsAllowNull;

	public PropertyComboBoxModel(
			IListProperty<T> aListProperty, 
			IRWProperty<T> aSelectedItemProperty,
			boolean aAllowNull)
	{
		super(aListProperty);
		itsSelectedItemProperty = aSelectedItemProperty;
		itsSelectedItemProperty.addListener(this);
		itsAllowNull = aAllowNull;
	}

	@Override
	public void elementAdded(IList<T> aList, int aIndex, T aElement)
	{
		super.elementAdded(aList, itsAllowNull ? aIndex+1 : aIndex, aElement);
	}

	@Override
	public void elementRemoved(IList<T> aList, int aIndex, T aElement)
	{
		super.elementRemoved(aList, itsAllowNull ? aIndex+1 : aIndex, aElement);
	}

	@Override
	public T getElementAt(int aIndex)
	{
		if (itsAllowNull)
		{
			return aIndex == 0 ? null : (T) super.getElementAt(aIndex-1);
		}
		else return (T) super.getElementAt(aIndex);
	}



	@Override
	public int getSize()
	{
		return itsAllowNull ? super.getSize()+1 : super.getSize();
	}

	public Object getSelectedItem()
	{
		return itsSelectedItemProperty.get();
	}

	public void setSelectedItem(Object aItem)
	{
		itsSelectedItemProperty.set((T) aItem);
	    fireContentsChanged(this, -1, -1);
	}

	public void propertyChanged(IProperty<T> aProperty, T aOldValue, T aNewValue)
	{
	    fireContentsChanged(this, -1, -1);
	}
	
	
}
