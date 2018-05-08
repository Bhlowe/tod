/*
 * Created on 10-jun-2004
 */
package zz.utils.list;

import zz.utils.properties.ICollectionProperty;


/**
 * A listener that is notified of modifications of a
 * {@link zz.utils.properties.ICollectionProperty}
 * @author gpothier
 */
public interface ICollectionListener<T>
{
	/**
	 * This method is called whenever an element is added to the collection.
	 * @param aCollection The collection whose content changed.
	 * @param aElement The added element.
	 */
	public void elementAdded (ICollection<T> aCollection, T aElement);
	
	/**
	 * This method is called whenever an element is removed from the list.
	 * @param aCollection The collection whose content changed.
	 * @param aElement The removed element.
	 */
	public void elementRemoved (ICollection<T> aCollection, T aElement);
}
