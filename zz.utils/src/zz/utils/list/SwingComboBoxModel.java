/*
 * Created on Mar 2, 2008
 */
package zz.utils.list;

import javax.swing.ComboBoxModel;

import zz.utils.properties.IRWProperty;
import zz.utils.properties.SimpleRWProperty;

public class SwingComboBoxModel<T> extends SwingListModel<T> 
implements ComboBoxModel
{
	public final IRWProperty<T> pSelectedItem = new SimpleRWProperty<T>()
	{
		@Override
		protected void changed(T aOldValue, T aNewValue)
		{
			itemSelected(aNewValue);
		}
	};

	public SwingComboBoxModel()
	{
	}

	public SwingComboBoxModel(IList<T> aItsList)
	{
		super(aItsList);
	}

	public Object getSelectedItem()
	{
		return pSelectedItem.get();
	}

	public void setSelectedItem(Object aItem)
	{
		pSelectedItem.set((T) aItem);
	}

	/**
	 * Called whenever the selected item changes.
	 */
	protected void itemSelected(T aItem)
	{
	}
}
