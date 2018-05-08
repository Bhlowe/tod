/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 29 oct. 2002
 * Time: 23:09:53
 */
package zz.utils;

import java.util.ListIterator;
import java.util.NoSuchElementException;

public class ArrayIterator implements ListIterator
{
	private Object[] itsArray;
	private int itsCursor = 0;
	private int itsLastReturnedIndex = -1;

	public ArrayIterator (Object[] aArray)
	{
		itsArray = aArray;
	}

	private Object returnElement (int aIndex)
	{
		itsLastReturnedIndex = aIndex;
		return itsArray[aIndex];
	}

	public boolean hasPrevious ()
	{
		return itsCursor > 0;
	}

	public int previousIndex ()
	{
		return itsCursor - 1;
	}

	public Object previous ()
	{
		if (! hasPrevious()) throw new NoSuchElementException();
		return returnElement(--itsCursor);
	}

	public boolean hasNext ()
	{
		return itsCursor < itsArray.length;
	}

	public int nextIndex ()
	{
		return itsCursor;
	}

	public Object next ()
	{
		if (! hasNext()) throw new NoSuchElementException();
		return returnElement(itsCursor++);
	}

	public void set (Object o)
	{
		if (itsLastReturnedIndex < 0) throw new IllegalStateException();
		itsArray[itsLastReturnedIndex] = o;
	}

	public void add (Object o)
	{
		itsLastReturnedIndex = -1;
		throw new UnsupportedOperationException();
	}

	public void remove ()
	{
		itsLastReturnedIndex = -1;
		throw new UnsupportedOperationException();
	}
}
