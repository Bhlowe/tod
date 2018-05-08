/*
 * Created on Apr 5, 2005
 */
package zz.utils.list;

/**
 * An adapter that can be used as a list listener when the listsner just needs to
 * know that the list changed.
 * @author gpothier
 */
public abstract class SimpleListListener<T> implements IListListener<T>
{

	public void elementAdded(IList<T> aList, int aIndex, T aElement)
	{
		listChanged(aList);
	}

	public void elementRemoved(IList<T> aList, int aIndex, T aElement)
	{
		listChanged(aList);
	}

	protected abstract void listChanged(IList<T> aList);
}
