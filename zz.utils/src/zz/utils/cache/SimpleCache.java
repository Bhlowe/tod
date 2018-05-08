package zz.utils.cache;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import zz.utils.AbstractFilteredIterator;

/**
 * A generic cache implementation that maps keys to values.
 * Concrete subclasses must be implemented that provide the value
 * fetching behavior (see {@link #fetch(Object) }).
 * @author gpothier
 */
public abstract class SimpleCache<K, V> implements ICache<K, V>
{
	private Map<K, CacheEntry<K, V>> itsEntries = 
		new HashMap<K, CacheEntry<K, V>>();
	
	private Set<K> itsNullValues =
		new HashSet<K>();
	
	private ReferenceQueue<V> itsReferenceQueue = 
		new ReferenceQueue<V>();

	/**
	 * A hook called whenever an already cached entry is accessed.
	 * Does nothing by default.
	 */
	protected void getHook(CacheEntry<K, V> aEntry)
	{
	}
	
	public V get(K aKey)
	{
		if (itsNullValues.contains(aKey)) return null;
		
		V theValue = getCached(aKey);
		
		if (theValue == null)
		{
			theValue = fetch(aKey);
			put(aKey, theValue);
			postFetch(aKey, theValue);
		}
		
		return theValue;
	}
	
	public V getCached(K aKey)
	{
		CacheEntry<K, V> theEntry = getCachedEntry(aKey);
		if (theEntry != null) 
		{
			getHook(theEntry);
			return theEntry.get();
		}
		else return null;
	}
	
	/**
	 * Returns the cached entry corresponding to the given key. As with
	 * {@link #getCached(Object)}, the associated value is not fetched.
	 */
	protected CacheEntry<K, V> getCachedEntry(K aKey)
	{
		checkQueue();
		
		if (itsNullValues.contains(aKey)) return null;
		
		return itsEntries.get(aKey);
	}
	
	public Iterable<V> getCachedValues()
	{
		return new CachedValuesIterator<K, V>(itsEntries.values().iterator());
	}
	
	public V put (K aKey, V aValue)
	{
		checkQueue();
		
		if (aValue != null)
		{
			itsNullValues.remove(aKey);
			
			CacheEntry<K, V> theEntry = createEntry(
					itsReferenceQueue, 
					aKey, 
					aValue,
					useStrongRef(aValue));
			
			CacheEntry<K, V> thePreviousEntry = itsEntries.put(aKey, theEntry);
			return thePreviousEntry != null ? thePreviousEntry.get() : null;
		}
		else
		{
			itsNullValues.add(aKey);
			CacheEntry<K, V> thePreviousEntry = itsEntries.remove(aKey);
			return thePreviousEntry != null ? thePreviousEntry.get() : null;
		}
	}
	
	/**
	 * Indicates if a strong reference should be kept for the
	 * specified value.
	 */
	protected boolean useStrongRef(V aValue)
	{
		return false;
	}

	public void invalidate(K aKey) 
	{
		checkQueue();
		
		itsEntries.remove(aKey);
		itsNullValues.remove(aKey);
	}
	
	public void clear()
	{
		checkQueue();
		itsEntries.clear();
		itsNullValues.clear();
	}

	/**
	 * Fetches a value that is not available in the cache.
	 * Concrete subclasses must implement this method.
	 */
	protected abstract V fetch(K aKey);

	/**
	 * This method is called after {@link #fetch(Object)}, when the fetched
	 * object has been added to the cache.
	 * This method does nothing by default.
	 */
	protected void postFetch(K aKey, V aValue)
	{
	}

	/**
	 * Polls the reference queue to see if some references have been
	 * garbage collected.
	 */
	private void checkQueue()
	{
		CacheEntry<K, V> theEntry;
		while ((theEntry = (CacheEntry<K, V>) itsReferenceQueue.poll()) != null)
		{
			referenceLost(theEntry.getKey());
		}
	}
	
	/**
	 * This method is called whenever a value of this cache has been garbage
	 * collected.
	 * This method is called synchronously when some operation is
	 * performed on the cache.
	 * By default this method does nothing.
	 * @param aKey The key of the garbage collected value.
	 */
	protected void referenceLost (K aKey)
	{
	}
	
	protected CacheEntry<K, V> createEntry(ReferenceQueue<V> aQueue, K aKey, V aReferent, boolean aStrongRef)
	{
		return new CacheEntry<K, V>(aQueue, aKey, aReferent, aStrongRef);
	}
	
	protected static class CacheEntry<K, V> extends SoftReference<V>
	{
		private K itsKey;
		private V itsStrongRef;

		public CacheEntry(ReferenceQueue<V> aQueue, K aKey, V aReferent, boolean aStrongRef)
		{
			super(aReferent, aQueue);
			itsKey = aKey;
			if (aStrongRef) itsStrongRef = aReferent;
		}

		public K getKey()
		{
			return itsKey;
		}
		
		public void clearStrongRef()
		{
			itsStrongRef = null;
		}
	}

	/**
	 * An iterator over cached values.
	 * @author gpothier
	 */
	private static class CachedValuesIterator<K, V> 
	extends AbstractFilteredIterator<CacheEntry<K, V>, V>
	{
		public CachedValuesIterator(Iterator<CacheEntry<K, V>> aBaseIterator)
		{
			super(aBaseIterator);
		}

		@Override
		protected Object transform(CacheEntry<K, V> aIn)
		{
			if (aIn != null)
			{
				V theValue = aIn.get();
				if (theValue != null) return theValue;
			}
			
			return REJECT;
		}
	}


}
