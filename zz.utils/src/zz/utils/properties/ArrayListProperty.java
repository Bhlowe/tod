/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import zz.utils.ReverseIteratorWrapper;

/**
 * @author gpothier
 */
public class ArrayListProperty<E> extends AbstractListProperty<E> 
{
	private List<E> itsList;
	
	public ArrayListProperty() {
	}
	
	public ArrayListProperty(E... values) {
		set(Arrays.asList(values));
	}
	
	public ArrayListProperty(Collection<E> values) {
		set(values);
	}
	
	protected List<E> get0()
	{
		if (itsList == null)
		{
			itsList = new MyList ();
			init();
		}
		return itsList;
	}

	/**
	 * Subclasses can override this method if they need to be notified when the 
	 * list is first accessed.
	 */
	protected void init()
	{
	}
	
	/**
	 * Changes the list that backs the property.
	 * This should be used with care, as it will not send any notification.
	 */
	protected void set (Collection<E> aList)
	{
		// The below code is a workaround for a strange jdk compile error.
		if (aList != null && MyList.class.isAssignableFrom(aList.getClass()))
//		if (aList instanceof MyList) This is the original code
			itsList = ((MyList) aList);

		else itsList = aList != null ? new MyList (aList) : null;
	}
	
	/**
	 * Resets the list.
	 * This should be used with care, as it will not send any notification.
	 */
	protected void reset()
	{
		set(new MyList());
	}
	
	public void clear()
	{
		while (! isEmpty()) remove(size()-1);
	}

	public Iterator<E> reverseIterator()
	{
		addToDeps();
		return new ReverseIteratorWrapper (get());
	}
	
	
	/**
	 * This is our implementation of List, which override some methods in
	 * order to send notifications. 
	 * @author gpothier
	 */
	protected class MyList extends ArrayList<E>
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
	}

}
