/*
 * Created on Dec 14, 2004
 */
package zz.utils.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import zz.utils.ReverseIteratorWrapper;

/**
 * Implementation of {@link zz.utils.list.IList} based on an
 * {@link java.util.ArrayList}
 * @author gpothier
 */
public class ZArrayList<E> extends AbstractList<E> 
{
	private List<E> itsList = new MyList ();
	
	public List<E> get()
	{
		return itsList;
	}
	
	/**
	 * Changes the list that backs the property.
	 * This should be used with care, as it will not send any notification.
	 */
	protected void set (List<E> aList)
	{
		// The below code is a workaround for a strange jdk compile error.
		if (MyList.class.isAssignableFrom(aList.getClass()))
//		if (aList instanceof MyList) This is the original code
			itsList = ((MyList) aList);
			
		else itsList = aList != null ? new MyList (aList) : null;
	}
	
	public boolean add(E aElement)
	{
		return get().add (aElement);
	}

	public void add(int aIndex, E aElement)
	{
		get().add (aIndex, aElement);
	}

	public E set(int aIndex, E aElement)
	{
		return get().set(aIndex, aElement);
	}
	
	public boolean remove(Object aElement)
	{
		return get().remove (aElement);
	}

	public E remove(int aIndex)
	{
		return get().remove(aIndex);
	}

	public void clear()
	{
		for (int i = size()-1;i>=0;i--)
			remove(i);
	}
	
	public int size()
	{
		List<E> theList = get();
		return theList != null ? theList.size() : 0;
	}

	public E get(int aIndex)
	{
		return get().get (aIndex);
	}

	public int indexOf(Object aElement)
	{
		return get().indexOf(aElement);
	}
	
	public boolean contains(Object aElement)
	{
		return get().contains(aElement);
	}

	public Iterator<E> iterator()
	{
		return get().iterator();
	}
	
	public Object[] toArray()
	{
		return get().toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return get().toArray(a);
	}

	public Iterator<E> reverseIterator()
	{
		return new ReverseIteratorWrapper (get());
	}
	
	


	/**
	 * This is our implementation of List, which override some methods in
	 * order to send notifications. 
	 * @author gpothier
	 */
	private class MyList extends ArrayList<E>
	{
		public MyList()
		{
		}
		
		public MyList(Collection<? extends E> aContent)
		{
			addAll(aContent);
		}
		
		public boolean remove(Object aO)
		{
			int theIndex = indexOf(aO);
			if (theIndex >= 0) 
			{
				remove(theIndex);
				return true;
			}
			else return false;
		}
		
		public E remove(int aIndex)
		{
			E theElement = super.remove(aIndex);
			fireElementRemoved(aIndex, theElement);
			return theElement;
		}
		
		public boolean add(E aElement)
		{
			boolean theResult = super.add(aElement);
			fireElementAdded(size()-1, aElement);
			return theResult;
		}
		
		public void add(int aIndex, E aElement)
		{
			super.add(aIndex, aElement);
			fireElementAdded(aIndex, aElement);
		}
		
		public boolean addAll(Collection<? extends E> aCollection)
		{
			return addAll(0, aCollection);
		}
		
		public boolean addAll(int aIndex, Collection<? extends E> aCollection)
		{
			int theIndex = aIndex;
			for (E theElement : aCollection)
				add (theIndex++, theElement);
			
			return aCollection.size() > 0;
		}
		
		public E set(int aIndex, E aElement)
		{
			E theElement = super.set(aIndex, aElement);

			fireElementRemoved(aIndex, theElement);
			fireElementAdded(aIndex, aElement);

			return theElement;
		}
		
		@Override
		public void clear()
		{
			while (! isEmpty()) remove(size()-1);
		}
	}

}
