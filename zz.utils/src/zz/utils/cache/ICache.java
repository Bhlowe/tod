package zz.utils.cache;

/**
 * Abstract definition of a cache.
 * @author gpothier
 *
 * @param <K> Type of the keys
 * @param <V> Type of the values
 */
public interface ICache<K, V>
{

	/**
	 * Gets the value that corresponds to the specified key from this cache.
	 * If the value is not in the cache, the {@link #fetch(Object) } method
	 * is called and the fetched object is cached.
	 */
	public V get(K aKey);

	/**
	 * Retrieves the value that corresponds to the specified key from this
	 * cache. If the value is not present in the cache, this method returns
	 * null (and doesn't call {@link #fetch(Object) }).
	 */
	public V getCached(K aKey);

	/**
	 * Returns an iterable over all currently cached values
	 */
	public Iterable<V> getCachedValues();

	/**
	 * Forces a value into the cache.
	 * @return The value previously associated with the given key, or null
	 * if there was none.
	 */
	public V put(K aKey, V aValue);

	/**
	 * Invalidates (removes from the cache) the entry corresponding to
	 * the given key.
	 */
	public void invalidate(K aKey);

	/**
	 * Removes all the entries from this cache. 
	 */
	public void clear();

}