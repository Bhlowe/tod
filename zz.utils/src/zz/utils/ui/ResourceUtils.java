/*
 * Created on Oct 15, 2006
 */
package zz.utils.ui;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class ResourceUtils
{
	private static final GraphicsConfiguration CONFIG = getConfig();
	
	private static GraphicsConfiguration getConfig()
	{
		GraphicsEnvironment theGraphicsEnvironment = 
			GraphicsEnvironment.getLocalGraphicsEnvironment();
		
		GraphicsConfiguration theConfig = 
			theGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration();
		
		return theConfig;
	}
	
	/**
	 * Creates an image with the same data as the specified image, but in
	 * a format that is more efficient.
	 */
	public static BufferedImage createCompatibleImage (BufferedImage aImage)
	{
		int theW = aImage.getWidth();
		int theH = aImage.getHeight();
		
		BufferedImage theImage = createCompatibleImage(theW, theH);
		
		Graphics2D theImgGraphics = theImage.createGraphics();
		theImgGraphics.drawImage(aImage, 0, 0, null);
		
		return theImage;
	}
	
	/**
	 * Creates an image in an efficient format
	 */
	public static BufferedImage createCompatibleImage (int aWidth, int aHeight)
	{
		return CONFIG.createCompatibleImage(aWidth, aHeight, Transparency.TRANSLUCENT);
	}
	
	
	/**
	 * Loads an image stored as a resource of the given class.
	 */
	public static ImageResource loadImageResource(Class aReferenceClass, String aName)
	{
		return new ImageResource(loadBufferedImage(aReferenceClass, aName));
	}
	
	public static BufferedImage loadBufferedImage(Class aReferenceClass, String aName)
	{
		InputStream theStream = aReferenceClass.getResourceAsStream (aName);
		if (theStream == null) throw new RuntimeException("Cannot find resource: "+aName);
		try
		{
			BufferedImage theImage = ImageIO.read(theStream);
			theImage = createCompatibleImage(theImage);
			
			return theImage;
		}
		catch (IOException e)
		{
			System.err.println("Could not load image: "+aName);
			e.printStackTrace();
			return null;
		}
	}
	
	public static ImageIcon loadIconResource (Class aReferenceClass, String aName)
	{
		return new ImageIcon (loadBufferedImage(aReferenceClass, aName));
	}
	
	public static BufferedImage offset(BufferedImage aImage, int aBorder, int aDX, int aDY)
	{
		int theW = aImage.getWidth();
		int theH = aImage.getHeight();
		
		BufferedImage theImage = createCompatibleImage(theW+2*aBorder, theH+2*aBorder);
		
		Graphics2D theImgGraphics = theImage.createGraphics();
		theImgGraphics.drawImage(aImage, aBorder+aDX, aBorder+aDY, null);
		
		return theImage;
		
	}
	
	/**
	 * Returns an overlay of all given images.
	 */
	public static ImageResource overlay(ImageResource... aLayers)
	{
		int theMaxWidth = 0;
		int theMaxHeight = 0;
		
		for (ImageResource theResource : aLayers)
		{
			theMaxWidth = Math.max(theMaxWidth, theResource.getImage().getWidth());
			theMaxHeight = Math.max(theMaxHeight, theResource.getImage().getHeight());
		}
		
		BufferedImage theResult = createCompatibleImage(theMaxWidth, theMaxHeight);
		
		Graphics2D theGraphics = theResult.createGraphics();
		
		for (ImageResource theResource : aLayers)
		{
			theGraphics.drawImage(theResource.getImage(), 0, 0, null);
		}
		
		return new ImageResource(theResult);
	}
	
	public static Cursor createCursor(ImageResource aImage, int aX, int aY)
	{
		return Toolkit.getDefaultToolkit().createCustomCursor(aImage.getImage(), new Point(aX, aY), "");
	}
	
	public static Cursor createCursor(Class aReferenceClass, String aName, int aX, int aY)
	{
		return createCursor(loadImageResource(aReferenceClass, aName), aX, aY);
	}


	public static class ImageResource
	{
		private BufferedImage itsImage;
		private ImageIcon itsIcon;

		public ImageResource(BufferedImage aImage)
		{
			itsImage = aImage;
		}
		
		public BufferedImage getImage()
		{
			return itsImage;
		}
		
		public ImageIcon asIcon()
		{
			if (itsIcon == null) itsIcon = new ImageIcon(itsImage);
			return itsIcon;
		}
		
		public ImageIcon asIcon(int aSize)
		{
			int theWidth = itsImage.getWidth();
			int theHeight = itsImage.getHeight();
			int theSide = Math.max(theWidth, theHeight);
			
			if (theSide != aSize)
			{
				float theRatio = 1f * aSize / theSide;
				
				Image theImage = itsImage.getScaledInstance(
						(int) (theWidth*theRatio), 
						(int) (theHeight*theRatio), 
						Image.SCALE_SMOOTH);
				
				return new ImageIcon(theImage);
			}
			else return new ImageIcon(itsImage);
		}
		
		/**
		 * Returns an icon of the specified height.
		 */
		public ImageIcon asIconH(int aHeight)
		{
			int theWidth = itsImage.getWidth();
			int theHeight = itsImage.getHeight();
			
			if (theHeight != aHeight)
			{
				float theRatio = 1f * aHeight / theHeight;
				
				Image theImage = itsImage.getScaledInstance(
						(int) (theWidth*theRatio), 
						(int) (theHeight*theRatio), 
						Image.SCALE_SMOOTH);
				
				return new ImageIcon(theImage);
			}
			else return new ImageIcon(itsImage);
		}
		
		/**
		 * Returns an icon of the specified width.
		 */
		public ImageIcon asIconW(int aWidth)
		{
			int theWidth = itsImage.getWidth();
			int theHeight = itsImage.getHeight();
			
			if (theWidth != aWidth)
			{
				float theRatio = 1f * aWidth / theWidth;
				
				Image theImage = itsImage.getScaledInstance(
						(int) (theWidth*theRatio), 
						(int) (theHeight*theRatio), 
						Image.SCALE_SMOOTH);
				
				return new ImageIcon(theImage);
			}
			else return new ImageIcon(itsImage);
		}
	}



}
