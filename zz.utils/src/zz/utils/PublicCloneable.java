/*
 * Created on Apr 15, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils;

import java.util.List;
import java.util.Map;


/**
 * Makes the {@link #clone()} method public.
 * Also provides utility methods to deep-clone collections
 * @author gpothier
 */
public class PublicCloneable implements IPublicCloneable
{
	public Object clone ()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			return null;
		}
	}
	
	/**
	 * Deeply clones a list.
	 * @param aList A list whose elements must implement {@link IPublicCloneable}
	 */
	public static <T extends IPublicCloneable> List<T> cloneList (List<T> aList)
	{
		try
		{
			List<T> theResult = (List<T>) aList.getClass().newInstance();
			for (IPublicCloneable theElement : aList)
			{
				theResult.add ((T) theElement.clone());
			}
			
			return theResult;
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException("Could not instantiate clone", e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("Could not instantiate clone", e);
		}
	}
	
	/**
	 * Deeply clones a map. Values a re cloned; keys are not.
	 */
	public static <K, V extends IPublicCloneable> Map<K, V> cloneMap (Map<K, V> aMap)
	{
		try
		{
			Map<K, V> theResult = (Map<K, V>) aMap.getClass().newInstance();
			for (Map.Entry<K, V> theEntry : aMap.entrySet())
			{
				K theKey = theEntry.getKey();
				V theValue = (V) theEntry.getValue().clone();
				theResult.put (theKey, theValue);
			}
			
			return theResult;
		}
		catch (InstantiationException e)
		{
			throw new RuntimeException("Could not instantiate clone", e);
		}
		catch (IllegalAccessException e)
		{
			throw new RuntimeException("Could not instantiate clone", e);
		}
	}
}
