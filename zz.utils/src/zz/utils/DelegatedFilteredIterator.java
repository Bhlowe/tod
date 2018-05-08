/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 7, 2002
 * Time: 3:03:17 AM
 */
package zz.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that returns a subset of the elements of the underlying iterator.
 * Only elements for which {@link Filter#accept} returns true are returned by
 * this iterator's {@link #next} method.
 */
public class DelegatedFilteredIterator<T> extends AbstractFilteredIterator<T, T>
{
	private IFilter<T> itsFilter;

	public DelegatedFilteredIterator (Collection<T> aCollection, IFilter<T> aFilter)
	{
		this (aCollection.iterator(), aFilter);
	}
	
	public DelegatedFilteredIterator (Iterator<T> aIterator, IFilter<T> aFilter)
	{
		super(aIterator);
		itsFilter = aFilter;
		findNext();
	}
	
	@Override
	protected void findNext()
	{
		if (itsFilter != null) super.findNext();
	}
	
	@Override
	protected Object transform(T aIn)
	{
		return itsFilter.accept(aIn) ? aIn : REJECT;
	}
	
	public static class ClassFilter implements IFilter<Object>
	{
		private Class itsClass;
		
		public ClassFilter (Class aClass)
		{
			itsClass = aClass;
		}
		
		public boolean accept(Object aObject)
		{
			return aObject == null || itsClass.isAssignableFrom(aObject.getClass());
		}
	}
}
