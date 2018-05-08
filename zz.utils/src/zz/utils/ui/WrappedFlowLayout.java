/*
 * Created on Apr 4, 2007
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * A layout manager similar to {@link FlowLayout} but that wraps components
 * to a given width. The width must be explicitly specified by the client.
 * @author gpothier
 */
public class WrappedFlowLayout 
implements LayoutManager
{
	public void addLayoutComponent(String aName, Component aComp)
	{
	}

	public void removeLayoutComponent(Component aComp)
	{
	}
	
	public void layoutContainer(Container aParent)
	{
		int theWidth = aParent.getWidth();
		int theCurrentHeight = 0;
		int theX = 0;
		int theY = 0;
		
		for(int i=0;i<aParent.getComponentCount();i++)
		{
			Component theComponent = aParent.getComponent(i);
			if (! theComponent.isVisible()) continue;
			
			Dimension theSize = theComponent.getPreferredSize();
			
			if (theX + theSize.width > theWidth)
			{
				theY += theCurrentHeight;
				theCurrentHeight = theSize.height;
				theX = 0;				
			}
			
			theComponent.setBounds(theX, theY, theSize.width, theSize.height);
			theCurrentHeight = Math.max(theCurrentHeight, theSize.height);
			
			theX += theSize.width;
		}
	}

	public Dimension preferredLayoutSize(Container aParent)
	{
		int theWidth = aParent.getWidth();
		int theCurrentHeight = 0;
		int theX = 0;
		int theY = 0;
		
		for(int i=0;i<aParent.getComponentCount();i++)
		{
			Component theComponent = aParent.getComponent(i);
			if (! theComponent.isVisible()) continue;
			
			Dimension theSize = theComponent.getPreferredSize();
			
			if (theX + theSize.width > theWidth)
			{
				theY += theCurrentHeight;
				theCurrentHeight = theSize.height;
				theX = 0;				
			}
			
			theCurrentHeight = Math.max(theCurrentHeight, theSize.height);
			
			theX += theSize.width;
		}
		
		return new Dimension(theWidth, theY + theCurrentHeight);
	}

	public Dimension minimumLayoutSize(Container aParent)
	{
		return preferredLayoutSize(aParent);
	}

	public static void main(String[] args)
	{
		JFrame theFrame = new JFrame("WrappedFlowLayout test");
		JPanel thePanel = new JPanel(new WrappedFlowLayout());
		theFrame.setContentPane(thePanel);
		
		thePanel.add(new JLabel("1haha"));
		thePanel.add(new JLabel("2werwer"));
		thePanel.add(new JLabel("3wre"));
		thePanel.add(new JLabel("4haghfghfhha"));
		thePanel.add(new JLabel("5rtyughsd"));
		thePanel.add(new JLabel("6rt"));
		thePanel.add(new JLabel("7tyrtyry"));
		thePanel.add(new JLabel("8efsdf"));
		thePanel.add(new JLabel("9fgdg"));
		thePanel.add(new JLabel("10utyu"));
		thePanel.add(new JLabel("11hsdsaha"));
		
		theFrame.pack();
		theFrame.setVisible(true);
	}
	
}
