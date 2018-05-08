/*
 * Created on Sep 20, 2004
 */
package zz.utils;

import java.util.Iterator;

/**
 * Helps iterate on iterators
 * @author gpothier
 */
public class I<E> implements Iterable<E>
{
	private Iterator<E> itsIterator;
	
	public I(Iterator<E> aIterator)
	{
		itsIterator = aIterator;
	}

	public Iterator<E> iterator()
	{
		return itsIterator;
	}
}
