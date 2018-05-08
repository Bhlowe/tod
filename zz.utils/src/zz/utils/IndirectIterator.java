/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: May 14, 2003
 * Time: 6:52:47 PM
 */
package zz.utils;

import java.util.Collection;
import java.util.Iterator;

/**
 * This class provides a wrapper arround an iterator that permits to
 * perform a computation on the original items, and return a result
 * of this computation instead of the original item.
 */
public class IndirectIterator implements Iterator
{
	private Iterator itsIterator;
	private Accessor itsAccessor;

	public IndirectIterator (Iterator aIterator, Accessor aAccessor)
	{
		itsIterator = aIterator;
		itsAccessor = aAccessor;
	}

	public IndirectIterator (Collection aCollection, Accessor aAccessor)
	{
		this (aCollection.iterator(), aAccessor);
	}

	public boolean hasNext ()
	{
		return itsIterator.hasNext();
	}

	public Object next ()
	{
		return itsAccessor.get (itsIterator.next ());
	}

	public void remove ()
	{
		itsIterator.remove();
	}

	/**
	 * Permits to perform a computation before returning the item.
	 */
	public static interface Accessor
	{
		/**
		 * Performs the computation and returns what the {@link #next} method must return.
		 * @param aObject The original item.
		 */ 
		public Object get (Object aObject);
	}
}
