/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 6 nov. 2002
 * Time: 11:34:23
 */
package zz.utils.ui;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

public class AutoResizeTextField extends JTextField implements DocumentListener
{
	public static final int MIN_SIZE = 10;

	public AutoResizeTextField ()
	{
	}

	public AutoResizeTextField (int columns)
	{
		super (columns);
	}

	public AutoResizeTextField (Document doc, String text, int columns)
	{
		super (doc, text, columns);
	}

	public AutoResizeTextField (String text)
	{
		super (text);
	}

	public AutoResizeTextField (String text, int columns)
	{
		super (text, columns);
	}

	public void addNotify ()
	{
		super.addNotify ();
		getDocument().addDocumentListener(this);
		updateSize();
	}

	public void removeNotify ()
	{
		super.removeNotify ();
		getDocument().removeDocumentListener(this);
	}

	public void changedUpdate (DocumentEvent e)
	{
	}

	public void insertUpdate (DocumentEvent e)
	{
		updateSize();
	}

	public void removeUpdate (DocumentEvent e)
	{
		updateSize();
	}

	private void updateSize ()
	{
		Graphics2D g = (Graphics2D) getGraphics();
		FontMetrics theFontMetrics = g.getFontMetrics();
		Rectangle2D theBounds = theFontMetrics.getStringBounds(getText(), g);

		setPreferredSize(new Dimension((int)Math.max (theBounds.getWidth(), MIN_SIZE), (int)theBounds.getHeight()));
		revalidate();
		repaint();
	}

	public boolean isValidateRoot ()
	{
		return false;
	}
}
