/*
 * Created on Mar 25, 2005
 */
package zz.utils.ui.thumbnail;

import java.awt.image.BufferedImage;

import zz.utils.ui.thumbnail.ThumbnailCache.Key;

/**
 * Synchronous implementation of {@link zz.utils.ui.thumbnail.ThumbnailCache}
 * @author gpothier
 */
public abstract class SyncThumbnailCache<T> extends ThumbnailCache<T>
{

	protected BufferedImage getThumbnail(Key<T> aKey)
	{
		BufferedImage theImage = getCached(aKey);
		
		if (theImage == null) 
		{
			theImage = createThumbnail(aKey);
			cache(aKey, theImage);
		}
		
		return theImage;
	}

}
