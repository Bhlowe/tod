/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import zz.utils.IPublicCloneable;

/**
 * @author gpothier
 */
public class HashSetProperty<E> extends AbstractSetProperty<E> 
{
	private Set<E> itsSet = new MySet ();
	
	public Set<E> get()
	{
		return itsSet;
	}
	
	/**
	 * Changes the set that backs the property.
	 * This should be used with care, as it will not send any notification.
	 */
	protected void set (Set<E> aSet)
	{
		// The below code is a workaround for a strange jdk compile error.
		if (MySet.class.isAssignableFrom(aSet.getClass()))
//		if (aSet instanceof MySet) itsSet = (MySet) aSet;
			itsSet = (MySet) aSet;
		
		else itsSet = aSet != null ? new MySet (aSet) : null;
	}
	
	public boolean add(E aElement)
	{
		return get().add (aElement);
	}

	public boolean remove(Object aElement)
	{
		return get().remove (aElement);
	}
	
	public boolean contains(Object aObject)
	{
		return get().contains(aObject);
	}

	public void clear()
	{
		for (Iterator<E> theIterator = iterator();theIterator.hasNext();)
		{
			theIterator.next();
			theIterator.remove();
		}
	}
	
	public int size()
	{
		Set<E> theSet = get();
		return theSet != null ? theSet.size() : 0;
	}

	public Iterator<E> iterator()
	{
		return get().iterator();
	}
	
	/**
	 * This is our implementation of List, which override some methods in
	 * order to send notifications. 
	 * @author gpothier
	 */
	private class MySet extends HashSet<E>
	{
		public MySet()
		{
		}
		
		public MySet(Collection<? extends E> aContent)
		{
			addAll(aContent);
		}
		
		public boolean remove(Object aO)
		{
			if (super.remove(aO))
			{
				fireElementRemoved((E) aO);
				return true;
			}
			else return false;
		}
		
		public boolean add(E aElement)
		{
			if (super.add(aElement))
			{
				fireElementAdded(aElement);
				return true;
			}
			else return false;
		}
		
		public boolean addAll(Collection<? extends E> aCollection)
		{
			boolean theResult = false;
			for (E theElement : aCollection)
				theResult |= add (theElement);
			
			return theResult;
		}
		
	}

}
