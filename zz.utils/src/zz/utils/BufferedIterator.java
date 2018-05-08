/*
 * Created on Sep 12, 2006
 */
package zz.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that fetches data by blocks.
 * Subclasses should call {@link #reset()} in their constructor.
 * @param <B> The type of the buffer (might be a list, an array, or
 * anything that provides random access).
 * @param <I> The type of the items
 * @author gpothier
 */
public abstract class BufferedIterator<B, I> implements Iterator<I>
{	
	private I itsNext;
	private I itsPrevious;
	private boolean itsNextReady = false;
	private boolean itsPreviousReady = false;
	private B itsBuffer;
	
	private int itsIndex;
	private int itsSize;
	
	/**
	 * Fetches the next available buffer.
	 * @return A buffer, or null if no more elements are available.
	 */
	protected abstract B fetchNextBuffer();
	
	/**
	 * Fetches the previous available buffer.
	 * Returns null by default; subclasses that need support for bidirectional
	 * iteration should provide an implementation. 
	 * @return A buffer, or null if no more elements are available.
	 */
	protected B fetchPreviousBuffer()
	{
		return null;
	}
	
	/**
	 * Returns an item of the given buffer.
	 */
	protected abstract I get(B aBuffer, int aIndex);
	
	/**
	 * Returns the size of the given buffer.
	 */
	protected abstract int getSize(B aBuffer);
	
	protected void reset()
	{
		itsIndex = 0;
		itsSize = 0;
		itsBuffer = null;
		itsNext = null;
		fetchNext();
		if (itsBuffer == null) fetchPrevious();
	}
	

	private void fetchNext()
	{
		if (itsIndex == itsSize) 
		{
			itsBuffer = fetchNextBuffer();
			itsSize = itsBuffer != null ? getSize(itsBuffer) : 0;
			itsIndex = 0;
		}
		itsNextReady = false;
	}
	
	private void fetchPrevious()
	{
		if (itsIndex == 0) 
		{
			itsBuffer = fetchPreviousBuffer();
			itsSize = itsBuffer != null ? getSize(itsBuffer) : 0;
			itsIndex = itsSize;
		}
		itsPreviousReady = false;
	}
	
	protected I getNext(boolean aFetch)
	{
		if (! itsNextReady && aFetch)
		{
			itsPrevious = itsNext;
			itsPreviousReady = true;
			itsNext = itsBuffer != null ? get(itsBuffer, itsIndex++) : null;
			itsNextReady = true;
		}
		return itsNext;
	}

	protected I getPrevious(boolean aFetch)
	{
		if (! itsPreviousReady && aFetch)
		{
			itsNext = itsPrevious;
			itsNextReady = true;
			itsPrevious = itsBuffer != null ? get(itsBuffer, --itsIndex) : null;
			itsPreviousReady = true;
		}
		return itsPrevious;
	}
	
	public boolean hasNext()
	{
		return getNext(true) != null;
	}

	public boolean hasPrevious()
	{
		return getPrevious(true) != null;
	}
	
	public I next()
	{
		if (! hasNext()) throw new NoSuchElementException();
		I theResult = getNext(true);
		fetchNext();
		return theResult;
	}

	public I previous()
	{
		if (! hasPrevious()) throw new NoSuchElementException();
		I theResult = getPrevious(true);
		fetchPrevious();
		return theResult;
	}
	
	public void remove()
	{
		throw new UnsupportedOperationException();
	}
	
	
}
