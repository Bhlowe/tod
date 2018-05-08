package zz.utils.cache;

import java.lang.ref.ReferenceQueue;

import zz.utils.cache.SimpleCache.CacheEntry;

/**
 * A cache that keeps strong references to some entries
 * according to a certain user-specified policy.
 * @param <M> Instrumentation class: the class used to store metrics
 * of cache entries.
 * @author gpothier
 */
public abstract class StrongPolicyCache<K, V, M> extends SimpleCache<K, V>
{

	@Override
	public V get(K aKey)
	{
		return super.get(aKey);
	}
	
	protected abstract boolean useStrongRef(K aKey, V aValue, M aMetrics);
	
	/**
	 * Policy-based cache do not use this method. 
	 * See {@link #useStrongRef(Object, Object, Object)}
	 */
	@Override
	protected final boolean useStrongRef(V aValue)
	{
		return false;
	}
	
	/**
	 * Creates the metrics object that will be associated to
	 * the entry corresponding to the given key.
	 * @param aValue The value of the entry
	 */
	protected abstract M createMetrics(K aKey, V aValue);
	
	/**
	 * Called when the entry that corresponds to the given metrics is
	 * accessed.
	 */
	protected abstract void updateMetricsForAccess(M aMetrics);
	
	@Override
	protected void getHook(CacheEntry<K, V> aEntry)
	{
		InstrumentedCacheEntry<K, V, M> theEntry = (InstrumentedCacheEntry<K, V, M>) aEntry;
		M theMetrics = theEntry.getMetrics();
		updateMetricsForAccess(theMetrics);
	}
	
	@Override
	protected CacheEntry<K, V> createEntry(ReferenceQueue<V> aQueue, K aKey, V aReferent, boolean aStrongRef)
	{
		return new InstrumentedCacheEntry<K, V, M>(
				aQueue, 
				aKey, 
				aReferent, 
				aStrongRef, 
				createMetrics(aKey, aReferent));
	}
	
	protected static class InstrumentedCacheEntry<K, V, M> extends CacheEntry<K, V>
	{
		private M itsMetrics;

		public InstrumentedCacheEntry(ReferenceQueue<V> aQueue, K aKey, V aReferent, boolean aStrongRef, M aMetrics)
		{
			super(aQueue, aKey, aReferent, aStrongRef);
			itsMetrics = aMetrics;
		}

		public M getMetrics()
		{
			return itsMetrics;
		}
		
		
	}
}
