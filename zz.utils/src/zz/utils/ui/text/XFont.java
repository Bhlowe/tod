/*
 * Created on Aug 21, 2004
 */
package zz.utils.ui.text;

import java.awt.Font;

/**
 * This class represents a font. It aggregates various information
 * to the standard awt {@link java.awt.Font} object.
 * @author gpothier
 */
public class XFont
{
	public static final Font DEFAULT_PLAIN = new Font("SansSerif", Font.PLAIN, 8);
	public static final XFont DEFAULT_XPLAIN = new XFont(DEFAULT_PLAIN, false);
	public static final XFont DEFAULT_XUNDERLINED = new XFont(DEFAULT_PLAIN, true);
	public static final Font DEFAULT_BOLD = new Font("SansSerif", Font.BOLD, 12);
	public static final Font DEFAULT_ITALIC = new Font("SansSerif", Font.ITALIC, 12);

	
	private Font itsAWTFont;
	private boolean itsUnderline;

	public XFont()
	{
		super();
	}
	
	public XFont(Font aAWTFont, boolean aUnderline)
	{
		itsAWTFont = aAWTFont;
		itsUnderline = aUnderline;
	}
	
	public Font getAWTFont()
	{
		return itsAWTFont;
	}
	
	public void setAWTFont(Font aAWTFont)
	{
		itsAWTFont = aAWTFont;
	}
	
	public boolean isUnderline()
	{
		return itsUnderline;
	}

	public void setUnderline(boolean aUnderline)
	{
		itsUnderline = aUnderline;
	}
	
	/**
	 * Creates a derived font of the given size.
	 * @see Font#deriveFont(float)
	 */
	public XFont deriveFont (float aSize)
	{
		return new XFont (getAWTFont().deriveFont(aSize), isUnderline());
	}
	
	/**
	 * Creates a derived font of the given size and style.
	 * @see Font#deriveFont(int, float)
	 */
	public XFont deriveFont (int aStyle, float aSize)
	{
		return new XFont (getAWTFont().deriveFont(aStyle, aSize), isUnderline());
	}
}
