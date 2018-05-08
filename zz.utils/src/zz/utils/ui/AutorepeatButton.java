/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Nov 29, 2001
 * Time: 4:07:05 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.Timer;


/**
 * A button that fires ActionEvents repeatedly while its is pressed.
 * The button will send actionPerformed events with AUTOREPEAT as action command while the button
 * is pressed. When the button is released, a normal actionPerformed is sent.
 */
public class AutorepeatButton extends JButton
{
	protected Timer itsTimer;

	public static final String AUTOREPEAT = "autoRepeat";

	public AutorepeatButton ()
	{
		init ();
	}

	public AutorepeatButton (Action a)
	{
		super (a);
		init ();
	}

	public AutorepeatButton (Icon icon)
	{
		super (icon);
		init ();
	}

	public AutorepeatButton (String text)
	{
		super (text);
		init ();
	}

	public AutorepeatButton (String text, Icon icon)
	{
		super (text, icon);
		init ();
	}

	public void removeNotify ()
	{
		super.removeNotify ();
		itsTimer.stop ();
	}

	protected void init ()
	{
		itsTimer = new Timer (100, new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				fire ();
			}
		});
		itsTimer.setInitialDelay(300);

		addMouseListener (new MouseAdapter ()
		{
			public void mousePressed (MouseEvent e)
			{
				if (isEnabled())
				{
					fire ();
					itsTimer.start();
				}
			}

			public void mouseReleased (MouseEvent e)
			{
				itsTimer.stop ();
			}
		});
	}

	protected void fire ()
	{
		System.out.println ("AutorepeatButton.fire");
		fireActionPerformed(new ActionEvent (AutorepeatButton.this, ActionEvent.ACTION_PERFORMED, AUTOREPEAT));
	}
}
