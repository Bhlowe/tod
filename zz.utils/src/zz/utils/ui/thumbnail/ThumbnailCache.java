/*
 * Created on Mar 25, 2005
 */
package zz.utils.ui.thumbnail;

import java.awt.image.BufferedImage;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import zz.utils.ArrayStack;
import zz.utils.Stack;

/**
 * A cache for thumbnails.
 * @author gpothier
 */
public abstract class ThumbnailCache<T>
{
	private Map<Key<T>, Reference<BufferedImage>> itsCache = new HashMap<Key<T>, Reference<BufferedImage>>();
	private Stack<BufferedImage> itsKeptImages;


	public ThumbnailCache()
	{
		this(5);
	}

	/**
	 * Creates a new asynchronous thumbnails cache.
	 * @param aMaxPermanentThumbnails Number of thumbnails that should be kept out of 
	 * the reach of GC.
	 */
	public ThumbnailCache(int aMaxPermanentThumbnails)
	{
		if (aMaxPermanentThumbnails > 0) 
			itsKeptImages = new ArrayStack<BufferedImage>(aMaxPermanentThumbnails);
	}

	/**
	 * Retruns a thumbnail that represents the given file
	 * @param aMaxSize Maximum size of the thumbnail
	 */
	public BufferedImage getThumbnail (T aId, int aMaxSize)
	{
		Key<T> theKey = new Key<T>(aId, aMaxSize);
		return getThumbnail(theKey);
	}
	
	protected abstract BufferedImage getThumbnail(Key<T> aKey);
	
	protected BufferedImage getCached(Key<T> aKey)
	{
		BufferedImage theImage = null;
		Reference<BufferedImage> theReference = itsCache.get(aKey);
		if (theReference != null) theImage = theReference.get();
		
		return theImage;
	}
	
	protected void cache (Key<T> aKey, BufferedImage aImage)
	{
		itsCache.put(aKey, new SoftReference<BufferedImage>(aImage));
		if (itsKeptImages != null) itsKeptImages.push (aImage);
	}

	/**
	 * Convenience method for calling {@link #createThumbnail(T, int)}
	 */
	protected BufferedImage createThumbnail (Key<T> aKey)
	{
		return createThumbnail(aKey.getId(), aKey.getSize());
	}
	
	/**
	 * Creates a thumbnail for the given objects and sizes.
	 * @param aId The object for which to create a thumbnail. A null value means that the method
	 * should return a "loading in progress" thumbnail 
	 * @param aMaxSize Maximum size of the thumbnail.
	 */
	protected abstract BufferedImage createThumbnail (T aId, int aMaxSize);

	protected static class Key<T>
	{
		private T itsId;
		private int itsSize;
		
		public Key(T aId, int aSize)
		{
			itsId = aId;
			itsSize = aSize;
		}
		
		public T getId()
		{
			return itsId;
		}

		public int getSize()
		{
			return itsSize;
		}

		public boolean equals(Object aObj)
		{
			if (aObj instanceof Key)
			{
				Key theKey = (Key) aObj;
				return itsId.equals(theKey.itsId) && itsSize == theKey.itsSize;
			}
			else return false;
		}
		
		public int hashCode()
		{
			return itsId.hashCode() + itsSize;
		}
	}
}
