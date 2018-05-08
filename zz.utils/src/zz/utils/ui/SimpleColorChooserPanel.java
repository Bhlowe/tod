/*
 * Created on Jun 18, 2008
 */
package zz.utils.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import zz.utils.notification.IEvent;
import zz.utils.notification.IFireableEvent;
import zz.utils.notification.SimpleEvent;
import zz.utils.properties.IProperty;
import zz.utils.properties.IRWProperty;
import zz.utils.properties.SimpleRWProperty;

/**
 * A simple color chooser that only proposes a small set of colors.
 * @author gpothier
 */
public class SimpleColorChooserPanel extends JPanel
{	
	private static final Color[] COLORS =
	{ 
		Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA, Color.ORANGE,
		Color.PINK, Color.RED, Color.YELLOW, Color.GRAY
	};

	private static final Border BORDER1 = BorderFactory.createLineBorder(Color.BLACK, 1);
	private static final Border BORDER2 = BorderFactory.createLineBorder(Color.BLACK, 2);

	private final IRWProperty<Color> pColor = new SimpleRWProperty<Color>();
	private final IFireableEvent<Color> eChanged = new SimpleEvent<Color>();
	
	/**
	 * Text for the "no color" choice.
	 */
	private String itsNullText;
	
	public SimpleColorChooserPanel(String aNullText)
	{
		itsNullText = aNullText;

		setLayout(new BorderLayout());

		MouseListener theListener = new MouseAdapter()
		{
			@Override
			public void mousePressed(MouseEvent e)
			{
				Object theSource = e.getSource();
				if (theSource instanceof JLabel) selectColor(null);
				else selectColor(((JPanel) theSource).getBackground());
			}
		};

		JLabel theDisableLabel = new JLabel(itsNullText);
		theDisableLabel.setBorder(BORDER1);
		theDisableLabel.addMouseListener(theListener);

		JPanel theNorthPanel = new JPanel();
		theNorthPanel.add(theDisableLabel);
		add(theNorthPanel, BorderLayout.NORTH);

		JPanel theCenterPanel = new JPanel(new GridLayout(0, 5, 5, 5));

		for (Color theColor : COLORS)
		{
			JPanel theColorPanel = new JPanel(null);
			theColorPanel.setPreferredSize(new Dimension(20, 20));
			theColorPanel.setBackground(theColor);
			theColorPanel.setBorder(BORDER1);
			theColorPanel.addMouseListener(theListener);

			theCenterPanel.add(theColorPanel);
		}

		add(theCenterPanel, BorderLayout.CENTER);
	}
	
	private void selectColor(Color aColor)
	{
		pColor.set(aColor);
		eChanged.fire(aColor);
	}

	public IProperty<Color> pColor()
	{
		return pColor;
	}

	public IEvent<Color> eChanged()
	{
		return eChanged;
	}
	
	

}
