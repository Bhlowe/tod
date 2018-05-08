/*
 * Created on Mar 25, 2005
 */
package zz.utils.ui.thumbnail;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ThumbnailUtils
{
	/**
	 * Creates a scaled version of the image provided by the specified input stream.
	 * This method tries to use thumbnails in the original image: If none fits, the full image
	 * is read and scaled.
	 */
	public static BufferedImage createScaledImage(
			InputStream aInputStream, 
			int aMaxSize, 
			boolean aWithAlpha,
			Object aInterpolationQuality) throws IOException
	{
		ImageInputStream theImageInputStream = ImageIO.createImageInputStream(aInputStream);
		Iterator<ImageReader> theImageReaders = ImageIO.getImageReaders(theImageInputStream);
		
		ImageReader theReader;
		if (theImageReaders.hasNext()) theReader = theImageReaders.next();
		else return null;
		
		theReader.setInput(theImageInputStream);
		
		BufferedImage theOriginalImage = null;
		if (theReader.readerSupportsThumbnails())
		{
			// Find the most suitable thumbnail, ie. the smallest that is bigger than requested size.
			int theThumbnailsCount = theReader.getNumThumbnails(0);
			int theFittest = -1; // Which is the fittest thumbnail?
			int theFittestScore = Integer.MAX_VALUE; // Score of the current fittest. Lower is better.
			
			for (int i=0;i<theThumbnailsCount;i++)
			{
				int theW = theReader.getThumbnailWidth(0, i);
				int theH = theReader.getThumbnailHeight(0, i);
				
				int theScore;
				if (theW < aMaxSize & theH < aMaxSize) theScore = Integer.MAX_VALUE;
				else theScore = Math.max(theW, theH) - aMaxSize;
				
				if (theScore < theFittestScore)
				{
					theFittest = i;
					theFittestScore = theScore;
					if (theScore == 0) break; // Can't improve, so don't iterate any more.
				}
			}
			
			// Get the thumbnail
			if (theFittest >= 0) theOriginalImage = theReader.readThumbnail(0, theFittest); 
		}
		
		// Fallback if no thumbnail is found
		if (theOriginalImage == null) theOriginalImage = theReader.read(0);
		
		return createScaledImage(theOriginalImage, aMaxSize, aWithAlpha, aInterpolationQuality);
	}
	/**
	 * Creates a scaled version of the given image.
	 * @param aInterpolationQuality Quality of the scaling algorithm. Use rendering hint values of
	 * {@link RenderingHints#KEY_INTERPOLATION} 
	 * @return The scaled image, or null if the image is already smaller than max size.
	 */
	public static BufferedImage createScaledImage(
			BufferedImage aImage, 
			int aMaxSize, 
			boolean aWithAlpha, 
			Object aInterpolationQuality) 
	{
		// Determine the scale.
		double theScale = 1;
		int theOriginalW = aImage.getWidth();
		int theOriginalH = aImage.getHeight();
		if (theOriginalW > theOriginalH)
		{
			theScale = (double) aMaxSize / (double) theOriginalW;
		}
		else
		{
			theScale = (double) aMaxSize / (double) theOriginalH;
		}
		
		// If the original image is already smaller than required, then just return it.
		if (theScale >= 1) return aImage;
		
		// Determine size of new image.
		// One of them should equal maxSize.
		int theW = (int) (theScale * theOriginalW);
		int theH = (int) (theScale * theOriginalH);

		BufferedImage theScaledImage = new BufferedImage(
				theW, 
				theH, 
				aWithAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
		
		Graphics2D theGraphics = theScaledImage.createGraphics();
		theGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, aInterpolationQuality);
		
		theGraphics.drawImage(aImage, 0, 0, theW, theH, null);
		
		return theScaledImage;
	}

	public static BufferedImage getBufferedImage(Image aImage, boolean aWithAlpha)
	{
		if (aImage instanceof BufferedImage)
		{
			return (BufferedImage) aImage;
		}
		else
		{
			int theW = aImage.getWidth(null);
			int theH = aImage.getHeight(null);
			BufferedImage theImage = new BufferedImage(
					theW, 
					theH, 
					aWithAlpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);

			Graphics2D theGraphics = theImage.createGraphics();
			theGraphics.drawImage(aImage, 0, 0, null);
			theGraphics.dispose();
			
			return theImage;
		}
	}

}
