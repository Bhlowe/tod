/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 18, 2002
 * Time: 2:54:33 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A Map in which all values are Sets, so that a single key
 * can map to any number of values.
 */
public class SetMap<K, V> extends HashMap<K, Set<V>>
{
	/**
	 * Adds aValue to the set corresponding to the specified key.
	 * @return The set into which the value was added.
	 */
	public Set<V> add (K aKey, V aValue)
	{
		Set<V> theSet = getSet (aKey);
		if (theSet == null)
		{
			theSet = new AwareSet (aKey);
			put (aKey, theSet);
		}

		theSet.add (aValue);
		return theSet;
	}

	// BHL - FIX
	/**
	 * Tries to remove aValue from the set corresponding to the specified key.
	 * @return True if aValue was present in the list.
	 */
	public boolean xxremove (K aKey, Object aValue)
	{
		Set<V> theSet = getSet (aKey);
		if (theSet == null)
			return false;
		else
			return theSet.remove (aValue);
	}


	/**
	 * Removes all occurences of aValue. Returns the number of occurences removed
	 */
	public int removeAll (Object aValue)
	{
		int theResult = 0;
		for (Set<V> theSet : values ())
		{
			while (theSet.remove (aValue)) theResult++;
		}
		return theResult;
	}

	public Set<V> getSet (Object aKey)
	{
		return get (aKey);
	}

	public Iterator<V> iterator (Object aKey)
	{
		Set<V> theSet = getSet (aKey);
		if (theSet == null) return Empty.ITERATOR;
		else return theSet.iterator ();
	}

	public Iterable<V> iterable (Object aKey)
	{
		Set<V> theSet = getSet (aKey);
		if (theSet == null) return Empty.ITERABLE;
		else return theSet;
	}

	/**
	 * A set that knows which key references it.
	 * It removes the corresponding entry when its size reaches 0.
	 */
	private class AwareSet extends HashSet<V>
	{
		private K itsKey;

		public AwareSet (K aKey)
		{
			itsKey = aKey;
		}

		public boolean remove (Object o)
		{
			boolean theResult = super.remove (o);
			checkEmpty();
			return theResult;
		}

		public boolean removeAll (Collection c)
		{
			boolean theResult = super.removeAll (c);
			checkEmpty();
			return theResult;
		}

		public boolean retainAll (Collection c)
		{
			boolean theResult = super.retainAll (c);
			checkEmpty();
			return theResult;
		}

		public void clear ()
		{
			super.clear ();
			checkEmpty();
		}

		private void checkEmpty ()
		{
			if (size() == 0) SetMap.this.remove (itsKey);
		}
	}
}
