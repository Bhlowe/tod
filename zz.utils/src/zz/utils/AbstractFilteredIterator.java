/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 7, 2002
 * Time: 3:03:17 AM
 */
package zz.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that peforms two levels of filtering:
 * <li>Input elements can be accepted or rejected by a filter.
 * <li>Accepted input elements are transformed into output elements by the
 * {@link #transform(Object)} method.
 * 
 * @author gpothier
 *
 * @param <I> Input type
 * @param <O> Output type
 */
public abstract class AbstractFilteredIterator<I, O> 
implements Iterator<O>, Iterable<O>
{
	protected static final Object REJECT = new Object();
	
	private Iterator<I> itsIterator;
	
	private O itsNext;
	private boolean itsHasNext;

	/**
	 * @param aCollection A collection from which to retrieve the underlying iterator
	 */
	public AbstractFilteredIterator (Collection<I> aCollection)
	{
		this (aCollection.iterator());
	}
	
	public AbstractFilteredIterator (Iterator<I> aIterator)
	{
		itsIterator = aIterator;
		init();
		findNext();
	}

	
	/**
	 * Subclasses can override this method if they need to perform some
	 * initialization before {@link #findNext()} is called for the first time
	 * in the constructor.
	 */
	protected void init()
	{
	}
	
	/**
	 * Tranforms an input element into an output element, and acts as a filter.
	 * @return The transformed element (which should be castable to <O>), or 
	 * {@link #REJECT} if the input element is not accepted.
	 * Note that using null as a rejection value would prohibit the iterator
	 * from returning null values, which may be desired.
	 */
	protected abstract Object transform(I aIn);

	protected void findNext ()
	{
		itsHasNext = false;
		while (itsIterator.hasNext())
		{
			I theIn = itsIterator.next();
			Object theTransformed = transform(theIn);
			if (theTransformed == REJECT) continue;
			
			itsNext = (O) theTransformed;
			itsHasNext = true;
			break;
		}
	}

	public boolean hasNext ()
	{
		return itsHasNext;
	}

	public O next ()
	{
		if (hasNext())
		{
			O theResult = itsNext;
			findNext();
			return theResult;
		}
		else throw new NoSuchElementException();
	}

	public void remove ()
	{
		itsIterator.remove();
	}

	public Iterator<O> iterator()
	{
		return this;
	}
}
