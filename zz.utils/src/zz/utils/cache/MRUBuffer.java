/*
 * Created on Aug 10, 2006
 */
package zz.utils.cache;

import java.util.HashMap;
import java.util.Map;

import zz.utils.list.NakedLinkedList;
import zz.utils.list.NakedLinkedList.Entry;

/**
 * A buffer that keeps the most recently used items in memory. Subclasses can be
 * notified of dropped items by overriding the {@link #dropped(Object)} method.
 * 
 * @author gpothier
 */
public abstract class MRUBuffer<K, V>
{
	private int itsCacheSize;

	private Map<K, Entry<V>> itsCache;

	/**
	 * Most recently used items list
	 */
	private NakedLinkedList<V> itsItemsList = new NakedLinkedList<V>();

	public MRUBuffer(int aCacheSize, boolean aUseMap)
	{
		itsCacheSize = aCacheSize;
		itsCache = aUseMap ? new HashMap<K, Entry<V>>() : null;
	}

	public MRUBuffer(int aCacheSize)
	{
		this(aCacheSize, true);
	}

	/**
	 * Returns the key that corresponds to the given value.
	 */
	protected abstract K getKey(V aValue);

	private Thread itsThread = null;

	// private void checkThread()
	// {
	// Thread theCurrentThread = Thread.currentThread();
	// if (itsThread != theCurrentThread)
	// {
	// System.out.println("MRUBuffer.checkThread()");
	// itsThread = theCurrentThread;
	// }
	// }
	//	
	/**
	 * Brings the specified entry to the top of the buffer. This is done
	 * automatically when an entry is retrieved or added.
	 */
	public void use(Entry<V> aEntry)
	{
		// checkThread();

		if (aEntry.isAttached())
		{
			itsItemsList.moveLast(aEntry);
		}
		else
		{
			itsItemsList.addLast(aEntry);

			while (shouldDrop(itsItemsList.size()))
			{
				Entry<V> theFirst = itsItemsList.getFirstEntry();
				itsItemsList.remove(theFirst);

				V theValue = theFirst.getValue();
				if (itsCache != null) itsCache.remove(getKey(theValue));
				dropped(theValue);
			}
		}
	}

	/**
	 * Determines if the oldest element should be dropped, given the current
	 * number of cached items.
	 */
	protected boolean shouldDrop(int aCachedItems)
	{
		return aCachedItems >= itsCacheSize;
	}

	/**
	 * This method is called when an item is dropped. This method does nothing
	 * by default.
	 */
	protected void dropped(V aValue)
	{
	}

	/**
	 * Called when a value that was not previously in the cache is now included
	 * in thecache. This method does nothing by default.
	 */
	protected void added(V aValue)
	{
	}

	/**
	 * Retrieves an item from this buffer. If the item is not in the buffer, it
	 * is fetched with the {@link #fetch(Object)} method.
	 */
	public V get(K aKey)
	{
		return get(aKey, true);
	}

	/**
	 * Retrieves an item from this buffer. If the item is not in the buffer and
	 * aFetch is true, then the item is fetched with the {@link #fetch(Object)}
	 * method. Otherwise, the method returns null.
	 */
	public V get(K aKey, boolean aFetch)
	{
		Entry<V> theEntry = getEntry(aKey, aFetch);
		return theEntry != null ? theEntry.getValue() : null;
	}

	public Entry<V> getEntry(K aKey)
	{
		return getEntry(aKey, true);
	}

	public Entry<V> getEntry(K aKey, boolean aFetch)
	{
		// checkThread();
		if (itsCache == null) throw new UnsupportedOperationException(
				"This MRU buffer does not have a map");
		Entry<V> theEntry = itsCache.get(aKey);
		if (theEntry == null && aFetch)
		{
			V theValue = fetch(aKey);
			theEntry = new Entry<V>(theValue);
			itsCache.put(aKey, theEntry);
			added(theValue);
		}

		if (theEntry != null) use(theEntry);

		return theEntry;
	}

	/**
	 * Returns the currently cached entries
	 */
	public Iterable<Entry<V>> getEntries()
	{
		if (itsCache == null) throw new UnsupportedOperationException(
				"This MRU buffer does not have a map");
		
		return itsCache.values();
	}
	
	/**
	 * Removes the mapping corresponding to the given key from this buffer
	 */
	public void drop(K aKey)
	{
		// checkThread();
		if (itsCache == null) throw new UnsupportedOperationException(
				"This MRU buffer does not have a map");
		Entry<V> theEntry = itsCache.remove(aKey);
		if (theEntry != null)
		{
			itsItemsList.remove(theEntry);
			dropped(theEntry.getValue());
		}
	}

	/**
	 * Drop all mappings from this buffer.
	 */
	public void dropAll()
	{
		if (itsCache != null) itsCache.clear();
		while (itsItemsList.size() > 0)
		{
			Entry<V> theFirstEntry = itsItemsList.getFirstEntry();
			itsItemsList.remove(theFirstEntry);
			dropped(theFirstEntry.getValue());
		}
	}

	/**
	 * Forcefully adds an item to this cache
	 * 
	 * @return The newly created entry.
	 */
	public Entry<V> add(V aValue)
	{
		// checkThread();
		Entry<V> theEntry = new Entry<V>(aValue);
		if (itsCache != null) itsCache.put(getKey(aValue), theEntry);
		added(aValue);
		use(theEntry);

		return theEntry;
	}

	/**
	 * Fetches the value identified by the given key.
	 */
	protected abstract V fetch(K aId);
}
