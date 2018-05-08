/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 15, 2002
 * Time: 3:44:10 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * This frame displays an image.
 */
public class ImageFrame extends JFrame
{
	protected Image itsImage;
	protected ImagePanel itsImagePanel;

	public ImageFrame ()
	{
		this (null);
	}

	public ImageFrame (Image aImage)
	{
		itsImagePanel = new ImagePanel();
		itsImagePanel.setPreferredSize(new Dimension(300, 300));
		setImage(aImage);

		setContentPane(itsImagePanel);
		pack ();
		setVisible (true);
	}

	public void setImage (Image aImage)
	{
		itsImage = aImage;
		itsImagePanel.paintImmediately(0, 0, itsImagePanel.getWidth(), itsImagePanel.getHeight());
	}

	class ImagePanel extends JPanel
	{
		protected void paintComponent (Graphics g)
		{
			if (itsImage != null) g.drawImage(itsImage, 0, 0, getWidth(), getHeight(), null);
			else super.paintComponent(g);
		}
	}
	
	public static void showImage(Image aImage)
	{
		ImageFrame theFrame = new ImageFrame(aImage);
		theFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
