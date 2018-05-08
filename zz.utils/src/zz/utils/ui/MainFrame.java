/*
 * Created on 14-may-2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;


/**
 * @author Rodrigo Rodriguez
 * 
 */
public class MainFrame extends JFrame
{
	public MainFrame()
	{
		super("INA 2004");
		createUI();
	}
	
	private void createUI() 
	{ 
		
		addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent evt)
			{
				System.exit(0);
            }
        });
		
		setSize();
		pack();
	}
	
	public void setSize()
	{
		Dimension theScreenDimension = Toolkit.getDefaultToolkit().getScreenSize(); 
		setSize(theScreenDimension);
		setLocation(0,0);
		
	}
}
