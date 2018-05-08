/*
 * Created on Mar 20, 2008
 */
package zz.utils.primitive;

/**
 * A stack of primitive int values.
 * @author gpothier
 */
public class IntStack extends IntArray
{
	public IntStack()
	{
	}

	public IntStack(int aInitialSize)
	{
		super(aInitialSize);
	}

	public void push(int aValue)
	{
		set(size(), aValue);
	}
	
	public int pop()
	{
		int theValue = get(size()-1);
		setSize(size()-1);
		return theValue;
	}
	
	public int peek()
	{
		return get(size()-1);
	}
}
