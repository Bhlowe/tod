/*
 * Created on Nov 19, 2007
 */
package zz.utils.primitive;

import java.util.NoSuchElementException;

/**
 * Implements a fixed-size stack of primitive integers.
 * @author gpothier
 */
public class FixedIntStack
{
	private int[] itsData;
	private int itsHeight;
	
	public FixedIntStack(int aInitialSize)
	{
		itsData = new int[aInitialSize];
		itsHeight = 0;
	}
	
	public void push(int aValue)
	{
		itsData[itsHeight++] = aValue;
	}
	
	public int pop()
	{
		return itsData[--itsHeight];
	}
	
	public int peek()
	{
		if (itsHeight == 0) throw new NoSuchElementException();
		return itsData[itsHeight-1];
	}
	
	public boolean isEmpty()
	{
		return itsHeight == 0;
	}
	
	public boolean isFull()
	{
		return itsHeight == itsData.length;
	}
	
	
	public void clear()
	{
		itsHeight = 0;
	}
}
