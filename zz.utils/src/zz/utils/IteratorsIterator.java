/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jun 14, 2003
 * Time: 7:06:42 PM
 */
package zz.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * This iterator iterates over all the objects of a set of iterators.
 */
public class IteratorsIterator<T> implements Iterator<T>, Iterable<T>
{
	/**
	 * An iterator over iterators
	 */
	private Iterator<Iterator<T>> itsIteratorsIterator;

	private Iterator<T> itsCurrentIterator = null;

	private boolean itsDone = false;

	/**
	 * @param aIteratorsIterator An iterator over iterators.
	 */
	public IteratorsIterator (Iterator<Iterator<T>> aIteratorsIterator)
	{
		itsIteratorsIterator = aIteratorsIterator;
	}

	/**
	 * @param aIteratorsList A list of iterators.
	 */
	public IteratorsIterator (List<Iterator<T>> aIteratorsList)
	{
		this (aIteratorsList.iterator());
	}
	
	public IteratorsIterator (Iterator<T>... aIteratorsArray)
	{
		this (Arrays.asList(aIteratorsArray).iterator());
	}

	private void seek ()
	{
		if (! itsDone && (itsCurrentIterator == null || ! itsCurrentIterator.hasNext()))
		{
			while (itsIteratorsIterator.hasNext())
			{
				itsCurrentIterator = itsIteratorsIterator.next();
				if (itsCurrentIterator.hasNext()) return;
			}
			itsDone = true;
		}
	}

	public boolean hasNext ()
	{
		seek();
		return ! itsDone;
	}

	public T next ()
	{
		seek ();
		if (itsDone) throw new NoSuchElementException();
		else return itsCurrentIterator.next();
	}

	public void remove ()
	{
		if (itsCurrentIterator != null) itsCurrentIterator.remove ();
		else throw new IllegalStateException();
	}

	public Iterator<T> iterator()
	{
		return this;
	}
}
