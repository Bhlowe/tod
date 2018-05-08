/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: 21 mars 02
 * Time: 15:34:49
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayStack<T> extends ArrayList<T> implements LimitableStack<T>
{
	/**
	 * The maximum number of elements in the stack. When elements are added beyond this limits,
	 * the older elements are discarded.
	 * If <=0, no limit is enforced.
	 * Note that this limit will be enforced only through the push () method.
	 */
	private int itsMaximumSize = 0;

	public ArrayStack()
	{
	}

	public ArrayStack(Collection<T> c)
	{
		super(c);
	}

	public ArrayStack(int aMaximumSize)
	{
		itsMaximumSize = aMaximumSize;
	}

	public int getMaximumSize()
	{
		return itsMaximumSize;
	}

	public void setMaximumSize(int aMaximumSize)
	{
		itsMaximumSize = aMaximumSize;
	}

	public T peek()
	{
		if (size() > 0)
			return get(size() - 1);
		else
			return null;
	}
	
	public T peek(int aIndex)
	{
		return get(aIndex);
	}

	public T pop()
	{
		if (size() > 0)
			return remove(size() - 1);
		else
			throw new RuntimeException ("Empty stack");
	}

	public void push(T aObject)
	{
		add(aObject);
		if (itsMaximumSize > 0)
			while (size() > itsMaximumSize)
				remove(0);
	}
	
	public void pushAll(Iterable<T> aSource)
	{
		for(T theItem : aSource) push(theItem);
	}

	public boolean isFull()
	{
		return itsMaximumSize > 0 && size() >= itsMaximumSize;
	}
}
