/*
 * Created on Nov 19, 2007
 */
package zz.utils.primitive;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A primitive int array that grows as needed.
 * @author gpothier
 */
public class IntArray
{
	private int[] itsData;
	private int itsSize;

	public IntArray()
	{
		this(16);
	}
	
	public IntArray(int aInitialSize)
	{
		itsData = new int[aInitialSize];
	}
	
	public int get(int aIndex)
	{
		return aIndex < itsSize ? itsData[aIndex] : 0;
	}
	
	public int size()
	{
		return itsSize;
	}
	
	public boolean isEmpty()
	{
		return itsSize == 0;
	}
	
	protected void setSize(int aSize)
	{
		itsSize = aSize;
	}
	
	public void set(int aIndex, int aValue)
	{
		ensureSize(aIndex+1);
		itsData[aIndex] = aValue;
		itsSize = Math.max(itsSize, aIndex+1);
	}
	
	public void add(int aValue)
	{
		set(size(), aValue);
	}
	
	public void clear()
	{
		itsSize = 0;
	}
	
	private void ensureSize(int aSize)
	{
		if (itsData.length >= aSize) return;
		
		int theNewSize = Math.max(aSize, itsData.length*2);
		int[] theNewData = new int[theNewSize];
		System.arraycopy(itsData, 0, theNewData, 0, itsData.length);
		itsData = theNewData;
	}

	public int[] toArray()
	{
		int[] theResult = new int[size()];
		System.arraycopy(itsData, 0, theResult, 0, size());
		return theResult;
	}
	
	/**
	 * Transforms a collection of {@link Integer}s to an array of native ints.
	 */
	public static int[] toIntArray(Collection<Integer> aCollection)
	{
		int[] theResult = new int[aCollection.size()];
		int i=0;
		for(Integer v : aCollection) theResult[i++] = v;
		return theResult;
	}
	
	public static List<Integer> toList(int[] aArray)
	{
		if (aArray == null) return null;
		List<Integer> theList = new ArrayList<Integer>();
		for(int i : aArray) theList.add(i);
		return theList;
	}
}
