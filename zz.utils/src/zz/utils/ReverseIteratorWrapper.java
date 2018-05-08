/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 14, 2002
 * Time: 4:21:53 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Wraps a ListIterator to act as a reverse Iterator.
 */
public class ReverseIteratorWrapper<T> implements Iterator<T>
{
	protected ListIterator<T> itsListIterator;

	/**
	 * Creates a reverse iterator based on the specified list iterator,
	 * which must initially be positioned at the end of the list.
	 * @param aListIterator
	 */
	public ReverseIteratorWrapper (ListIterator<T> aListIterator)
	{
		itsListIterator = aListIterator;
	}

	/**
	 * Creates a reverse iterator on the specified list.
	 */
	public ReverseIteratorWrapper (List<T> aList)
	{
		this (aList.listIterator(aList.size()));
	}

	public boolean hasNext ()
	{
		return itsListIterator.hasPrevious();
	}

	public T next ()
	{
		return itsListIterator.previous();
	}

	public void remove ()
	{
		itsListIterator.remove();
	}
}
