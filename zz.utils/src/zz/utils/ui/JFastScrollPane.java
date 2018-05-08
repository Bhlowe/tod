package zz.utils.ui;

import java.awt.Component;

import javax.swing.JScrollPane;

/**
 * A scrollpane with a faster wheel scroll speed
 * @author gpothier
 */
public class JFastScrollPane extends JScrollPane
{
	public static int UNIT_INCREMENT = 16;
	
	
	public JFastScrollPane()
	{
	}

	public JFastScrollPane(Component aView, int aVsbPolicy, int aHsbPolicy)
	{
		super(aView, aVsbPolicy, aHsbPolicy);
	}

	public JFastScrollPane(Component aView)
	{
		super(aView);
	}

	public JFastScrollPane(int aVsbPolicy, int aHsbPolicy)
	{
		super(aVsbPolicy, aHsbPolicy);
	}
	
	@Override
	public void addNotify()
	{
		super.addNotify();
		getVerticalScrollBar().setUnitIncrement(UNIT_INCREMENT);
		getHorizontalScrollBar().setUnitIncrement(UNIT_INCREMENT);
	}

}
