/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 18, 2002
 * Time: 2:54:33 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * A Map in which all values are Lists, so that a single key
 * can map to any number of values.
 */
public class ListMap<K, V> extends HashMap<K, List<V>>
{
	/**
	 * Returns the element at the specified index of the list
	 * that corresponds to the given key. 
	 * If no element is found an exception is thrown.
	 */
	public V get (K aKey, int aIndex)
	{
		List<V> theList = getList(aKey);
		if (theList == null) throw new NoSuchElementException("No element for key "+aKey);
		else return theList.get(aIndex);
	}
	
	/**
	 * Adds aValue the the list of the specified key.
	 * @return The list into which the value was added.
	 */
	public List<V> add (K aKey, V aValue)
	{
		List<V> theList = getList (aKey);
		if (theList == null)
		{
			theList = new AwareList (aKey);
			put (aKey, theList);
		}

		theList.add (aValue);
		return theList;
	}

	public boolean _remove (K aKey, V aValue)
	{
		List<V> theList = getList (aKey);
		if (theList == null)
			return false;
		else
			return theList.remove (aValue);
	}
	
	
	@Override
	public List<V> remove(Object key)
	{
		return super.remove(key);
	}
	
	/**
	 * Tries to remove aValue from the list of the specified key.
	 * @return True if aValue was present in the list.
	 */
//BHL
	//	@Override
//	public boolean remove (Object aKey, Object aValue)
//	{
//		_remove(aKey, (V) aValue);
//	}


	/**
	 * Removes all occurences of aValue. Returns the number of occurences removed
	 */
	public int removeAll (Object aValue)
	{
		int theResult = 0;
		for (List<V> theList : values ())
		{
			while (theList.remove (aValue)) theResult++;
		}
		return theResult;
	}

	public List<V> getList (Object aKey)
	{
		return get (aKey);
	}

	public Iterator<V> iterator (Object aKey)
	{
		List<V> theList = getList (aKey);
		if (theList == null) return Empty.ITERATOR;
		else return theList.iterator ();
	}

	public Iterable<V> iterable (Object aKey)
	{
		List<V> theList = getList (aKey);
		if (theList == null) return Empty.ITERABLE;
		else return theList;
	}

	/**
	 * A list that knows which key references it.
	 * It removes the corresponding entry when its size reaches 0.
	 */
	private class AwareList extends ArrayList<V>
	{
		private K itsKey;

		public AwareList (K aKey)
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

		public V remove (int index)
		{
			V theResult = super.remove (index);
			checkEmpty();
			return theResult;
		}

		protected void removeRange (int fromIndex, int toIndex)
		{
			super.removeRange (fromIndex, toIndex);
			checkEmpty();
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
			if (size() == 0) ListMap.this.remove (itsKey);
		}
	}
}
