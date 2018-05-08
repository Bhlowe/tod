/*
 * Created on 10-jun-2004
 */
package zz.utils.list;

/**
 * A listener that is notified of modifications of a {@link IList}
 * @author gpothier
 */
public interface IListListener<T>
{
	/**
	 * This method is called whenever an element is added to the list.
	 * @param aList The list whose content changed.
	 * @param aIndex The index of the newly added element
	 * @param aElement The added element.
	 */
	public void elementAdded (IList<T> aList, int aIndex, T aElement);
	
	/**
	 * This method is called whenever an element is removed from the list.
	 * @param aList The list whose content changed.
	 * @param aIndex The index previously occupied by the removed element
	 * @param aElement The removed element.
	 */
	public void elementRemoved (IList<T> aList, int aIndex, T aElement);
}
