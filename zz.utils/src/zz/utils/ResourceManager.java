/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 13, 2001
 * Time: 4:22:48 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import zz.utils.ui.UIUtils;

public class ResourceManager
{
	/**
	 * Contains all images extension available. <p>
	 */
	private final static String[] EXTENTION_IMG = new String[]{".png",".gif",".jpg"};

	/**
	 * The prefix of the file where all strings/messages are stored. The full name is
	 * "<BASENAME>.properties".
	 */
	protected static final String BASENAME = "Almonde";

	/**
	 * The key to use to get the MoveDrop cursor.
	 */
	public final static String CURSOR_KEY_MOVE_DROP = "Cursor.MoveDrop.32x32.File";

	public final static String CURSOR_KEY_COPY_DROP = "Cursor.CopyDrop.32x32.File";

	public final static String CURSOR_KEY_MOVE_NODROP = "Cursor.MoveNoDrop.32x32.File";

	public final static String CURSOR_KEY_COPY_NODROP = "Cursor.CopyNoDrop.32x32.File";

	protected static final String[] DEFAULT_CURSORS = {CURSOR_KEY_MOVE_DROP, CURSOR_KEY_COPY_DROP, CURSOR_KEY_MOVE_NODROP, CURSOR_KEY_COPY_NODROP};

	/**
	 * Blank & transparent icon. <p>
	 */
	private ImageIcon itsBlankIcon;

	/**
	 * Arrows icon. <p>
	 */
	private ImageIcon[] itsArrowIcons;


	private static final ResourceManager instance = new ResourceManager ();

	protected Map itsStringCache = new HashMap ();
	protected Map itsIconCache = new HashMap ();
	protected Map itsCursorCache = new HashMap ();

	protected Locale itsLocale;

	/**
	 * The suffixes to append to a file name (before th extension) in order of preference,
	 * i.e. if a file with the first suffix does not exist, the second suffix is tried
	 */
	protected String[] itsLocaleSuffixes;
	protected ResourceBundle itsResourceBundle;

	private ResourceManager ()
	{
		setLocale (Locale.getDefault ());
//		loadDefaultCursorImages ();
//		loadDefaultIcons ();
	}

	/**
	 * Changes the locale to the specified locale and updates all necessary information
	 * (discards caches, etc.)
	 */
	public void setLocale (Locale aLocale)
	{
		itsStringCache.clear ();
		itsIconCache.clear ();
		itsCursorCache.clear ();

		itsLocale = aLocale;
//		itsResourceBundle = ResourceBundle.getBundle (BASENAME, itsLocale);
		List theTempSuffixes = new ArrayList ();

		// Sets up itsLocaleSuffixes
		String theVariant = itsLocale.getVariant ();
		if (theVariant.length () == 0) theVariant = null;
		String theCountry = itsLocale.getCountry ();
		if (theCountry.length () == 0) theCountry = null;
		String theLanguage = itsLocale.getLanguage ();
		if (theLanguage.length () == 0) theLanguage = null;

		if (theVariant != null && theCountry != null && theLanguage != null)
			theTempSuffixes.add ("_" + theLanguage + "_" + theCountry + "_" + theVariant);
		if (theCountry != null && theLanguage != null)
			theTempSuffixes.add ("_" + theLanguage + "_" + theCountry);
		if (theLanguage != null) theTempSuffixes.add ("_" + theLanguage);
		theTempSuffixes.add ("");

		itsLocaleSuffixes = (String[]) theTempSuffixes.toArray (new String[0]);
	}

	/**
	 * Loads the images of the default cursors defined in the DEFAULT_CURSORS array.
	 * It is not 100% pure Java...<p>
	 * The images of the cursors are used to create compund cursors.
	 */
	protected void loadDefaultCursorImages ()
	{
		// Hack to retrieve the directory where the JDK stores cursors...
		String theJavaHome = System.getProperty ("java.home");
		String theResourceDir = theJavaHome + File.separator + "lib" + File.separator + "images" + File.separator + "cursors" + File.separator;
		String theBundleFilename = "cursors.properties";

		try
		{
			Properties p = new Properties ();
			FileInputStream fi = new FileInputStream (theResourceDir + theBundleFilename);
			p.load (fi);

			for (int i = 0; i < DEFAULT_CURSORS.length; i++)
			{
				String theKeyName = DEFAULT_CURSORS[i];
				String theFilename = p.getProperty (theKeyName);
				InputStream theInputStream = new FileInputStream (theResourceDir + theFilename);
				ImageIcon theIcon = new ImageIcon (readStream (theInputStream));
				itsIconCache.put (theKeyName, theIcon);
			}
		}
		catch (IOException io)
		{
			// We get here if we are a mac, or if we use a JVM that is not a Sun one
			// This should be hidden, and logged
			// io.printStackTrace();
		}
	}

	protected void loadDefaultIcons ()
	{
		itsBlankIcon = getIcon ("Blank");
		itsArrowIcons = new ImageIcon[] {getIcon ("Arrow1Up"), getIcon ("Arrow1Left"), getIcon ("Arrow1Down"), getIcon ("Arrow1Right")};
	}

	/**
	 * Returns the singleton instance of this class.
	 */
	public static ResourceManager getInstance ()
	{
		return instance;
	}

	/**
	 * Returns the string which has the specified key.
	 */
	public String getString (String aKey)
	{
		try
		{
			String theString = (String) itsStringCache.get (aKey);
			if (theString == null)
			{
				theString = itsResourceBundle.getString (aKey);
				itsStringCache.put (aKey, theString);
			}
			return theString;
		}
		catch (MissingResourceException e)
		{
			return "-*- "+aKey+" -*-";
		}
	}

	/**
	 * Returns the long that have the specified key
	 */
	public long getLong(String aKey)
	{
		return new Long( getString( aKey ) ).longValue();
	}

	/**
	 * Returns the int that have the specified key
	 */
	public int getInt(String aKey)
	{
		return new Integer( getString( aKey ) ).intValue();
	}

	/**
	 * Returns the message which has the specified key.
	 * Equivalent to getString (aKey).
	 */
	public String getMessage (String aKey)
	{
		return getString (aKey);
	}

	/**
	 * Formats the message which has the specified key with the specified arguments.
	 * See java.text.MessageFormat for an explanation of the formatting mechanism.
	 */
	public String getMessage (String aKey, Object[] anArgumentList)
	{
		String theMessage = null;
		try
		{
			theMessage = getMessage (aKey);
		}
		catch (MissingResourceException mre)
		{
			return "*-" + aKey + "-*";
		}
		if (anArgumentList != null) theMessage = MessageFormat.format (theMessage, anArgumentList);
		return theMessage;
	}

	public ImageIcon getBlankIcon ()
	{
		return itsBlankIcon;
	}

	/**
	 * Equivalent to a call to getIcon (aRequestingClass, <class name without package>);
	 * This returns the 'default' icon for the class.
	 */
	public ImageIcon getIcon (Class aRequestingClass)
	{
		String theClassName = aRequestingClass.getName ();
		int i = theClassName.lastIndexOf ('.');
		String theKey = theClassName.substring (i + 1); // when i == -1 it still works...
		return getIcon (aRequestingClass, theKey);
	}

	/**
	 * Returns the "global" icon which has the specified key. Equivalent to
	 * getIcon (ResourceManager.class, key)
	 */
	public ImageIcon getIcon (String aKey)
	{
		return getIcon (getClass (), aKey);
	}

	public ImageIcon getIcon (String aKey, int aWidth, int anHeight)
	{
		ImageIcon theIcon = getIcon(aKey);
		return UIUtils.resizeIcon(theIcon, aWidth, anHeight);
	}

	/**
	 * Returns the icon which has the specified key within the directory of the
	 * specified requesting class.<p>
	 * The returned icon is rescaled if needed to the specified size
	 */
	public ImageIcon getIcon (Class aRequestingClass, String aKey, int aWidth, int anHeight)
	{
		ImageIcon theIcon = getIcon (aRequestingClass, aKey);
		return UIUtils.resizeIcon(theIcon, aWidth, anHeight);
	}

	/**
	 * Returns the icon which has the specified key within the directory of the
	 * specified requesting class.
	 */
	public ImageIcon getIcon (Class aRequestingClass, String aKey)
	{
//		if (aKey == null || aKey.length() == 0) return getIcon (aRequestingClass);

//		Logger.getInstance().push( "ResourceManager::getIcon("+aRequestingClass+","+aKey+")" );

		String theClassName = aRequestingClass.getName ();
		String theFullKey = theClassName + "-" + aKey;
		ImageIcon theIcon = (ImageIcon) itsIconCache.get (theFullKey);

		// Not cached yet - we have to load it
		if (theIcon == null)
		{
			System.out.println ("Creating icon " + theFullKey);
			for (int i = 0; theIcon == null && i < EXTENTION_IMG.length; i++)
			{
				for (int j = 0; theIcon == null && j < itsLocaleSuffixes.length; j++)
				{
					String theSuffix = itsLocaleSuffixes[j];
					String theExtension = EXTENTION_IMG[i];
					theIcon = loadIcon (aRequestingClass, aKey + theSuffix + theExtension);
				}
			}

			if (theIcon != null)
			{
				theIcon.setDescription (theFullKey);
				itsIconCache.put (theFullKey, theIcon);
			}
		}

//		Logger.getInstance().pop();
		if (theIcon == null) return itsBlankIcon;
		return theIcon;
	}

	/**
	 * Actually loads an icon.
	 */
	protected ImageIcon loadIcon (Class aRequestingClass, String aKey)
	{
		InputStream theStream = aRequestingClass.getResourceAsStream (aKey);
		if (theStream == null) return null;
		try
		{
			return new ImageIcon (readStream (theStream));
		}
		catch (IOException e)
		{
			return null;
		}
	}

	/**
	 * Reads a stream into a byte array.
	 */
	protected byte[] readStream (InputStream anInputStream) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		byte[] buffer = new byte[1024];
		int r;
		while ((r = anInputStream.read (buffer)) != -1) baos.write (buffer, 0, r);
		return baos.toByteArray ();
	}

	/**
	 * Returns a compound cursor with the specified icon as background and the cursor
	 * with the specified key as foreground. The cursor key must be one of the cursor
	 * key constants defined in this class.
	 */
	public Cursor getCompoundCursor (ImageIcon anIcon, String aCursorKey)
	{
		Cursor theCursor = null;
		String theFullKey = null;
		if (anIcon instanceof ImageIcon)
		{
			String theMaybeKey = ((ImageIcon) anIcon).getDescription ();
			theFullKey = theMaybeKey + "-" + aCursorKey;
			theCursor = (Cursor) itsCursorCache.get (theFullKey);
		}

		if (theCursor == null)
		{
			System.out.println ("Creating compound cursor over an existing icon " + aCursorKey);
			ImageIcon theCursorIcon = getCursorIcon (aCursorKey);
			theCursor = createCompoundCursor (anIcon, theCursorIcon, aCursorKey);
			if (theFullKey != null) itsCursorCache.put (theFullKey, theCursor);
		}

		return theCursor;
	}

	/**
	 * Returns a compound cursor with the specified icon (see getIcon) as background
	 * and the cursor with the specified key as foreground.
	 * The cursor key must be one of the cursor key constants defined in this class.
	 */
	public Cursor getCompoundCursor (Class aRequestingClass, String anIconKey, String aCursorKey)
	{
		String theClassName = aRequestingClass.getName ();
		String theFullKey = theClassName + "-" + anIconKey + "-" + aCursorKey;
		Cursor theCursor = (Cursor) itsCursorCache.get (theFullKey);

		if (theCursor == null)
		{
			System.out.println ("Creating compound cursor " + theFullKey);

			ImageIcon theIcon = getIcon (aRequestingClass, anIconKey);
			ImageIcon theCursorIcon = getCursorIcon (aCursorKey);

			theCursor = createCompoundCursor (theIcon, theCursorIcon, aCursorKey);

			itsCursorCache.put (theFullKey, theCursor);
		}

		return theCursor;
	}

	/**
	 * Actually creates a compound cursor out of the two specified images.
	 */
	protected Cursor createCompoundCursor (ImageIcon anIcon, ImageIcon aCursorIcon, String aName)
	{
		// Seems like it wants a 32x32 and
		// it will scale it without any other questions, so in order to avoid
		// distortion, we create a 32x32 img and paint the 24x24 img in it
		BufferedImage bim = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics ();
		if (anIcon != null)
		{
			Image img = anIcon.getImage ();
			g.drawImage (img, 4, 4, anIcon.getIconWidth (), anIcon.getIconHeight (), null);
		}

		if (aCursorIcon != null)
		{
			Image img = aCursorIcon.getImage ();
			g.drawImage (img, 0, 0, aCursorIcon.getIconWidth (), aCursorIcon.getIconHeight (), null);
		}

		return Toolkit.getDefaultToolkit ().createCustomCursor (bim, new Point (0, 0), aName);
	}

	protected ImageIcon getCursorIcon (String aKey)
	{
		return (ImageIcon) itsIconCache.get (aKey);
	}

	public Cursor getCursor (Class aRequestingClass, String aKey)
	{
		return getCursor(aRequestingClass, aKey, 0, 0);
	}

	/**
	 * Creates or fetches from the cache a custom cursor that uses the specified icon.
	 * @param aRequestingClass The class in whose package the icon resides
	 * @param aKey The name of the icon
	 * @param aX Hot spot x coordinate
	 * @param aY Hot spot y coordinate
	 */
	public Cursor getCursor (Class aRequestingClass, String aKey, int aX, int aY)
	{
		ImageIcon theIcon = getIcon (aRequestingClass, aKey);

		BufferedImage bim = new BufferedImage (32, 32, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bim.createGraphics ();
		Image img = theIcon.getImage ();
		g.drawImage (img, 0, 0, theIcon.getIconWidth (), theIcon.getIconHeight (), null);

		return Toolkit.getDefaultToolkit ().createCustomCursor (bim, new Point (aX, aY), "aKey");
	}

	/**
	 * Returns an arrow icon of 24x24. <p>
	 *
	 * @param aSide the side of the arrow
	 * <ul>
	 *  <li>JLabel.LEFT (2)
	 *  <li>JLabel.RIGHT (4)
	 *  <li>JLabel.TOP (1)
	 *  <li>JLabel.BOTTOM (3)
	 * </ul>
	 * @return an arrow
	 */
	public ImageIcon getArrow (int aSide)
	{
		return itsArrowIcons[aSide - 1];
	}

	/**
	 * Returns a resized arrow.
	 */
	public ImageIcon getArrow (int aSide, int aSize)
	{
		ImageIcon theArrow = itsArrowIcons[aSide-1];
		return UIUtils.resizeIcon(theArrow, aSize, aSize);
	}


}
