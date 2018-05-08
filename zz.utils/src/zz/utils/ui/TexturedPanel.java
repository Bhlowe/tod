/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 6, 2001
 * Time: 4:55:20 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import zz.utils.ResourceManager;

public class TexturedPanel extends JPanel
{
	protected Image itsImage;

	protected BufferedImage itsShadedImage;

	protected Color itsShade;

	public TexturedPanel (LayoutManager layout, boolean isDoubleBuffered)
	{
		super (layout, isDoubleBuffered);
		init ();
	}

	public TexturedPanel (LayoutManager layout)
	{
		super (layout);
		init ();
	}

	public TexturedPanel (boolean isDoubleBuffered)
	{
		super (isDoubleBuffered);
		init ();
	}

	public TexturedPanel ()
	{
		init ();
	}

	protected void init ()
	{
		setImage (ResourceManager.getInstance ().getIcon ("LightLeather").getImage ());
	}

	public void setImage (Image aImage)
	{
		itsImage = aImage;
		createShadedImage ();
	}

	public void setShade (Color aShade)
	{
		itsShade = aShade;
		createShadedImage ();
	}

	protected void createShadedImage ()
	{
		itsShadedImage = null;
		if (itsImage == null) return;

		int w = itsImage.getWidth (null);
		int h = itsImage.getHeight (null);

		itsShadedImage = new BufferedImage (w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = itsShadedImage.createGraphics ();

		g.drawImage (itsImage, 0, 0, null);
		if (itsShade != null)
		{
			g.setComposite (itsComposite);
			g.setColor (itsShade);
			g.fillRect (0, 0, w, h);
		}
	}

	private static Composite itsComposite = AlphaComposite.getInstance (AlphaComposite.SRC_OVER, 0.5f);

	protected void paintComponent (Graphics g1)
	{
		Graphics2D g = (Graphics2D) g1.create ();

		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

		int thePanelWidth = getWidth ();
		int thePanelHeight = getHeight ();

		if (itsShadedImage != null)
		{
			int theImageWidth = itsShadedImage.getWidth ();
			int theImageHeight = itsShadedImage.getHeight ();

			for (int x = 0; x < thePanelWidth; x += theImageWidth)
			{
				for (int y = 0; y < thePanelHeight; y += theImageHeight)
				{
					g.drawImage (itsShadedImage, x, y, null);
				}
			}
		}
		else
		{
			g.setColor (itsShade);
			g.fillRect(0, 0, thePanelWidth, thePanelHeight);
		}
	}
}
