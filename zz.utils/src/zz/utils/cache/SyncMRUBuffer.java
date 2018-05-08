/*
 * Created on Feb 1, 2007
 */
package zz.utils.cache;

import zz.utils.list.NakedLinkedList.Entry;

/**
 * A thread-safe version of {@link MRUBuffer}
 * @author gpothier
 */
public abstract class SyncMRUBuffer<K, V> extends MRUBuffer<K, V>
{

	public SyncMRUBuffer(int aCacheSize, boolean aUseMap)
	{
		super(aCacheSize, aUseMap);
	}

	public SyncMRUBuffer(int aCacheSize)
	{
		super(aCacheSize);
	}

	@Override
	public synchronized Entry<V> add(V aValue)
	{
		return super.add(aValue);
	}

	@Override
	public synchronized void drop(K aKey)
	{
		super.drop(aKey);
	}
	
	@Override
	public synchronized void dropAll()
	{
		super.dropAll();
	}
	
	@Override
	public synchronized V get(K aKey, boolean aFetch)
	{
		return super.get(aKey, aFetch);
	}

	@Override
	public synchronized Entry<V> getEntry(K aKey, boolean aFetch)
	{
		return super.getEntry(aKey, aFetch);
	}

	@Override
	public synchronized void use(Entry<V> aEntry)
	{
		super.use(aEntry);
	}
}
