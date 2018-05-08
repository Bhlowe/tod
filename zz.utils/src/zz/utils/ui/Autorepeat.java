/*
 * Created on Jan 17, 2007
 */
package zz.utils.ui;

import java.awt.event.ActionListener;

import javax.swing.AbstractButton;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Provides mechanism to add auto-repeat behavior to buttons
 * @author gpothier
 */
public class Autorepeat
{
	public static void install(AbstractButton aButton, ActionListener aActionListener)
	{
		new Ticker(aButton, aActionListener);
	}
	
	private static class Ticker extends Timer implements ChangeListener 
	{
		private AbstractButton itsButton;
		
		public Ticker(AbstractButton aButton, ActionListener aActionListener)
		{
			super (200, aActionListener);
			setInitialDelay(300);
			setDelay(100);
			
			itsButton = aButton;
			itsButton.addChangeListener(this);
			
			setRepeats(true);
		}

		public void stateChanged(ChangeEvent aE)
		{
			if (itsButton.getModel().isPressed()) 
			{
				fireActionPerformed(null);
				restart();
			}
			else stop();
		}

		
	}
	

}
