package zz.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Provides a method to sort a collection according to an external key provided by
 * a user-specified {@link KeyProvider}.
 * @author gpothier
 */
public class KeySorter
{
	/**
	 * Returns a list with the elements of the collection sorted in ascending key order,
	 * where keys are provided by the {@link KeyProvider}.
	 */
	public static <K extends Comparable<K>, T> List<T> sort(Collection<T> aCollection, KeyProvider<K, T> aKeyProvider)
	{
		List<T> theResult = new ArrayList<T>();
		
		SortedMap<K, List<T>> theMap = new TreeMap<K, List<T>>();
		for (T theItem : aCollection) 
		{
			K theKey = aKeyProvider.getKey(theItem);
			List<T> theItems = theMap.get(theKey);
			if (theItems == null)
			{
				theItems = new ArrayList<T>();
				theMap.put(theKey, theItems);
			}
			theItems.add(theItem);
		}

		for(Map.Entry<K, List<T>> theEntry : theMap.entrySet()) theResult.addAll(theEntry.getValue());
		
		return theResult;
	}
	
	public static interface KeyProvider<K extends Comparable<K>, T>
	{
		public K getKey(T aItem);
	}
}
