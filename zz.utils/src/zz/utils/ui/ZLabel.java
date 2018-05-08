/*
 * Created on Feb 6, 2007
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import zz.utils.SimpleAction;
import zz.utils.ui.text.TextPainter;
import zz.utils.ui.text.XFont;

/**
 * A swing component similar to JLabel but that uses
 * a {@link XFont} and has no icon.
 * @author gpothier
 */
public class ZLabel extends JComponent implements MouseListener 
{
	private String itsText;
	private XFont itsFont;
	
	private HorizontalAlignment itsHorizontalAlignment =
		HorizontalAlignment.LEFT;
	
	private VerticalAlignment itsVerticalAlignment =
		VerticalAlignment.CENTER;

	
	public ZLabel(String aText, XFont aFont, Color aColor)
	{
		itsText = aText;
		itsFont = aFont;
		setOpaque(false);
		setForeground(aColor);
		updateSize();
		setComponentPopupMenu(createPopupMenu());
		addMouseListener(this);
	}
	
	public void setAlignment(HorizontalAlignment aHorizontalAlignment, VerticalAlignment aVerticalAlignment)
	{
		itsHorizontalAlignment = aHorizontalAlignment;
		itsVerticalAlignment = aVerticalAlignment;
	}
	
	private void updateSize()
	{
		Point2D theSize = TextPainter.computeSize(
				TextPainter.getDefaultGraphics(), 
				itsFont.getAWTFont(),
				false,
				itsText);
		
		setPreferredSize(new Dimension(
				(int) theSize.getX()+1, 
				(int) theSize.getY()+1));
	}
	
	public XFont getXFont()
	{
		return itsFont;
	}

	public void setXFont(XFont aFont)
	{
		itsFont = aFont;
		updateSize();
	}
	
	@Override
	public void setFont(Font aFont)
	{
		throw new UnsupportedOperationException("use setXFont()");
	}
	
	@Override
	public Font getFont()
	{
		return getXFont().getAWTFont();
	}

	public String getText()
	{
		return itsText;
	}

	public void setText(String aText)
	{
		itsText = aText;
		updateSize();
	}

	@Override
	protected void paintComponent(Graphics aG)
	{
		super.paintComponent(aG);
		Graphics2D theGraphics = (Graphics2D) aG;
		theGraphics.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		TextPainter.paint(
				theGraphics, 
				getXFont(), 
				getForeground(), 
				getText(), 
				new Rectangle(0, 0, getWidth(), getHeight()), 
				itsVerticalAlignment,
				itsHorizontalAlignment);
	}
	
	public static ZLabel create(String aText, XFont aFont, Color aColor)
	{
		return new ZLabel(aText, aFont, aColor);
	}
	
	private JPopupMenu createPopupMenu()
	{
		JPopupMenu theMenu = new JPopupMenu();
		theMenu.add(new SimpleAction("Copy")
		{
			public void actionPerformed(ActionEvent aE)
			{
				StringSelection theContent = new StringSelection(itsText);
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
						theContent,
						theContent);
			}
		});
		
		return theMenu;
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mouseEntered(MouseEvent e)
	{
	}

	public void mouseExited(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
	}

	public void mouseReleased(MouseEvent e)
	{
	}
}
