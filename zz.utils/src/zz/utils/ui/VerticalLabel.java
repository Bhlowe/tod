/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 7, 2001
 * Time: 11:24:11 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;

import javax.swing.Icon;
import javax.swing.JLabel;

public class VerticalLabel extends JLabel
{
	protected Color itsColor1;
	protected Color itsColor2;

	public VerticalLabel (String text, Icon icon, int horizontalAlignment)
	{
		super (text, icon, horizontalAlignment);
		init ();
	}

	public VerticalLabel (String text, int horizontalAlignment)
	{
		super (text, horizontalAlignment);
		init ();
	}

	public VerticalLabel (String text)
	{
		super (text);
		init ();
	}

	public VerticalLabel (Icon image, int horizontalAlignment)
	{
		super (image, horizontalAlignment);
		init ();
	}

	public VerticalLabel (Icon image)
	{
		super (image);
		init ();
	}

	public VerticalLabel ()
	{
		init ();
	}

	protected void init ()
	{
		setOpaque (false);
	}

	public void setBackground (Color bg)
	{
		super.setBackground (bg);
		setGradient(null, null);
	}

	public void setGradient (Color aColor1, Color aColor2)
	{
		itsColor1 = aColor1;
		itsColor2 = aColor2;
	}

	public Dimension getPreferredSize ()
	{
		Dimension theSize = super.getPreferredSize ();
		return new Dimension (theSize.height, theSize.width);
	}

	public Dimension getMaximumSize ()
	{
		Dimension theSize = super.getMaximumSize ();
		return new Dimension (theSize.height, theSize.width);
	}

	public Dimension getMinimumSize ()
	{
		Dimension theSize = super.getMinimumSize ();
		return new Dimension (theSize.height, theSize.width);
	}

	public void paintComponent (Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1.create();

		AffineTransform theVerticalize = AffineTransform.getTranslateInstance (0, getHeight());
		theVerticalize.concatenate (AffineTransform.getRotateInstance (-Math.PI / 2.0));
		g.transform(theVerticalize);
//		super.paintComponent(g);

		// Without antialiasing vertical text looks ugly...
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Remember we are vertical...
		int theWidth = getHeight ();
		int theHeight = getWidth ();

		if (itsColor1 != null && itsColor2 != null)
		{
			GradientPaint theGradientPaint = new GradientPaint(0, 0, itsColor1, theWidth, 0, itsColor2);
			g.setPaint(theGradientPaint);
		}
		else g.setColor (getBackground());
		g.fillRect(0, 0, theWidth, theHeight);

		FontMetrics theFontMetrics = g.getFontMetrics();
		int theAscent = theFontMetrics.getAscent();
		int theDescent = theFontMetrics.getDescent();

		int theX = 0;
		int theY = theAscent + (theHeight - (theAscent + theDescent)) / 2;

		int theIconWidth = 0;
		int theIconHeight = 0;
		Icon theIcon = getIcon();
		if (theIcon != null)
		{
			theIconWidth = theIcon.getIconWidth();
			theIconHeight = theIcon.getIconHeight();
			theIcon.paintIcon(this, g, 0, (theHeight - theIconHeight) / 2);
			theX += theIconWidth + 3;
		}

		g.setColor (getForeground());
		g.drawString(getText (), theX, theY);
	}
}
