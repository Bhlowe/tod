/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 28, 2001
 * Time: 10:19:29 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;

import zz.utils.Utils;

/**
 * A flat button that changes appearance when the mouse enters/exits it.
 * It can send actionPerformed messages either when the mouse is pressed
 * or when the mouse is clicked, according to the value set by setFireCondition.
 */
public class FlatButton extends JLabel
{
	protected static Border itsEmptyBorder2 = BorderFactory.createEmptyBorder(2, 2, 2, 2);
	protected static Border itsEmptyBorder1 = BorderFactory.createEmptyBorder(1, 1, 1, 1);

	public static int FIRE_ON_CLICK = 1;
	public static int FIRE_ON_PRESS = 2;

	protected List itsActionListeners = new ArrayList ();

	protected int itsFireCondition = FIRE_ON_CLICK;

	protected Color itsNormalColor;

	protected Color itsNormalBackgroundColor;

	protected Color itsRollOverColor;

	protected Color itsRollOverBackgroundColor;

	protected Border itsNormalBorder;

	protected Border itsRollOverBorder;

	protected boolean itsShowingRollOver = false;

	public FlatButton (Icon image)
	{
		super (image);
		init ();
	}

	public FlatButton (Icon image, int horizontalAlignment)
	{
		super (image, horizontalAlignment);
		init ();
	}

	public FlatButton (String text)
	{
		super (text);
		init ();
	}

	public FlatButton (String text, int horizontalAlignment)
	{
		super (text, horizontalAlignment);
		init ();
	}

	public FlatButton (String text, Icon icon, int horizontalAlignment)
	{
		super (text, icon, horizontalAlignment);
		init ();
	}

	public FlatButton ()
	{
		init ();
	}

	protected void init ()
	{
		setNormalColor(Color.gray);
		setRollOverColor(Color.blue.darker ());

		addMouseListener(new MouseAdapter ()
		{
			public void mousePressed (MouseEvent e)
			{
				if (itsFireCondition == FIRE_ON_PRESS) fireActionPerformed(new ActionEvent (this, ActionEvent.ACTION_PERFORMED, ""));
			}

			public void mouseClicked (MouseEvent e)
			{
				if (itsFireCondition == FIRE_ON_CLICK) fireActionPerformed(new ActionEvent (this, ActionEvent.ACTION_PERFORMED, ""));
			}

			public void mouseExited (MouseEvent e)
			{
				showNormal();
			}

			public void mouseEntered (MouseEvent e)
			{
				showRollOver();
			}
		});
		showNormal();
	}

	protected void showNormal ()
	{
		if (itsNormalBackgroundColor != null)
		{
			setBackground(itsNormalBackgroundColor);
			setOpaque (true);
		}
		else setOpaque(false);

		setBorder(itsNormalBorder);
		itsShowingRollOver = false;
	}

	protected void showRollOver ()
	{
		if (itsRollOverBackgroundColor != null)
		{
			setBackground(itsRollOverBackgroundColor);
			setOpaque(true);
		}
		else setOpaque(false);

		setBorder (itsRollOverBorder);
		itsShowingRollOver = true;
	}

	public void addActionListener (ActionListener aListener)
	{
		itsActionListeners.add (aListener);
	}

	public void removeActionListener (ActionListener aListener)
	{
		itsActionListeners.remove (aListener);
	}

	protected void fireActionPerformed (ActionEvent anEvent)
	{
		for (Iterator theIterator = itsActionListeners.iterator (); theIterator.hasNext ();)
		{
			ActionListener theListener = (ActionListener) theIterator.next ();
			theListener.actionPerformed(anEvent);
		}
	}

	/**
	 * @param aFireCondition One of FIRE_ON_CLICK, FIRE_ON_PRESS
	 */
	public void setFireCondition (int aFireCondition)
	{
		itsFireCondition = aFireCondition;
	}

	protected Border makeBorder (Color aColor)
	{
		Border theOuterBorder = aColor != null
				? BorderFactory.createLineBorder(aColor, 1)
				: itsEmptyBorder1;
		return BorderFactory.createCompoundBorder(theOuterBorder, itsEmptyBorder2);
	}

	public void setRollOverColor (Color aRollOverColor)
	{
		itsRollOverColor = aRollOverColor;
		itsRollOverBackgroundColor = UIUtils.getLighterColor(aRollOverColor, 0.6f);
		itsRollOverBorder = makeBorder(aRollOverColor);
		if (itsShowingRollOver) showRollOver();
	}

	public Color getRollOverColor ()
	{
		return itsRollOverColor;
	}

	public void setNormalColor (Color aNormalColor)
	{
		itsNormalColor = aNormalColor;
		itsNormalBackgroundColor = UIUtils.getLighterColor(aNormalColor, 0.6f);
		itsNormalBorder = makeBorder(aNormalColor);
		if (! itsShowingRollOver) showNormal();
	}

	public Color getNormalColor ()
	{
		return itsNormalColor;
	}
}
