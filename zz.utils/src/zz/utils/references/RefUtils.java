/*
 * Created on Dec 16, 2004
 */
package zz.utils.references;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import zz.utils.properties.IPropertyListener;

/**
 * @author gpothier
 */
public class RefUtils
{
	/**
	 * Returns a list containing dereferenced elements from the given
	 * list. This method also remove GCed elements from the references list
	 */
	public static <E> List<E> dereference (List<IRef<E>> aList)
	{
		if (aList.isEmpty()) return Collections.EMPTY_LIST;
		
		List<E> theResult = new ArrayList<E>(aList.size());
		
		for (Iterator<IRef<E>> theIterator = aList.iterator();theIterator.hasNext();)
		{
			IRef<E> theRef = theIterator.next();
			E theElement = theRef.get();
			if (theElement == null) theIterator.remove();
			else theResult.add (theElement);
		}
		
		return theResult;
	}
	
	/**
	 * Removes an element from a reference list.
	 * Elements are removed through the iterator.
	 * @return Whether the element was found
	 */
	public static <E> boolean remove (Iterable<IRef<E>> aList, E aElement)
	{
		for (Iterator<IRef<E>> theIterator = aList.iterator();theIterator.hasNext();)
		{
			IRef<E> theRef = theIterator.next();
			E theListener = theRef.get();
			if (theListener == null || theListener == aElement) 
			{
				theIterator.remove();
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Same as {@link #remove(Iterable, Object)} but doesn't use the iterator to
	 * remove the elements (useful for {@link CopyOnWriteArrayList}).
	 */
	public static <E> boolean removeFromList (List<IRef<E>> aList, E aElement)
	{
		int i=0;
		for (Iterator<IRef<E>> theIterator = aList.iterator();theIterator.hasNext();)
		{
			IRef<E> theRef = theIterator.next();
			E theListener = theRef.get();
			if (theListener == null || theListener == aElement) 
			{
				aList.remove(i);
				return true;
			}
			i++;
		}
		return false;
	}
	

}
