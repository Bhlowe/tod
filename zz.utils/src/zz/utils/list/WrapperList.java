/*
 * Created on May 25, 2005
 */
package zz.utils.list;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Implementation of a list that wraps another list,
 * wrapping each element as needed on the fly.
 * @author gpothier
 */
public abstract class WrapperList<E, E0> extends AbstractList<E> 
{
	/**
	 * Original list.
	 */
	private IList<E0> itsList;
	
	private Map<E0, E> itsWrappedElements = new WeakHashMap<E0, E>();
	
	public WrapperList(IList<E0> aList)
	{
		itsList = aList;
	}

	public void add(int aIndex, E aElement)
	{
		itsList.add (aIndex, cachedUnwrap(aElement));
	}

	public boolean add(E aElement)
	{
		add (size(), aElement);
		return true;
	}

	public E get(int aIndex)
	{
		E0 theElement = itsList.get(aIndex);
		return cachedWrap(theElement);
	}

	public int indexOf(Object aElement)
	{
		E0 theUnwrappedElement = unwrap((E) aElement);
		return itsList.indexOf(theUnwrappedElement);
	}

	public E remove(int aIndex)
	{
		return cachedWrap(itsList.remove(aIndex));
	}

	public Iterator<E> reverseIterator()
	{
		return new WrappedIterator(itsList.reverseIterator());
	}

	public E set(int aIndex, E aElement)
	{
		return cachedWrap(itsList.set (aIndex, cachedUnwrap(aElement)));
	}

	public void clear()
	{
		itsList.clear();
	}

	public boolean contains(Object aElement)
	{
		return itsList.contains(cachedUnwrap((E) aElement));
	}

	public boolean remove(Object aElement)
	{
		return itsList.remove(unwrap((E) aElement)); // We don't need to use cache for remove.
	}

	public int size()
	{
		return itsList.size();
	}

	public Iterator<E> iterator()
	{
		return new WrappedIterator(itsList.iterator());
	}

	public Object[] toArray()
	{
		return itsList.toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return itsList.toArray(a);
	}


	/**
	 * Returns a wrapped element, checking if there is a cheched version
	 * available first.
	 */
	private E cachedWrap (E0 aElement)
	{
		E theWrappedElement = itsWrappedElements.get (aElement);
		if (theWrappedElement == null)
		{
			theWrappedElement = wrap(aElement);
			itsWrappedElements.put (aElement, theWrappedElement);
		}
		return theWrappedElement;
	}
	
	private E0 cachedUnwrap (E aElement)
	{
		E0 theElement = unwrap(aElement);
		itsWrappedElements.put(theElement, aElement);
		return theElement;
	}

	/**
	 * Wraps an element of the original list into another objects.
	 * Implementors should not bother with caching wrapped values,
	 * as this class already does it.
	 * @param aElement Original element
	 * @return Wrapped element
	 */
	protected abstract E wrap (E0 aElement);
	
	/**
	 * Performs the opposite of {@link #wrap(E0)}.
	 * By default this method throws {@link UnsupportedOperationException},
	 * preventing all add methods from working.
	 * Subclasses that need to support them should
	 * implement this method.
	 */
	protected E0 unwrap (E aElement)
	{
		throw new UnsupportedOperationException();
	}
	
	private class WrappedIterator implements Iterator<E>
	{
		private Iterator<E0> itsIterator;
		
		public WrappedIterator(Iterator<E0> aIterator)
		{
			itsIterator = aIterator;
		}

		public boolean hasNext()
		{
			return itsIterator.hasNext();
		}

		public E next()
		{
			return cachedWrap(itsIterator.next());
		}

		public void remove()
		{
			itsIterator.remove();
		}
		
	}
}
