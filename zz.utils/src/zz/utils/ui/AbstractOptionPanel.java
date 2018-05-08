/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Apr 22, 2003
 * Time: 4:49:35 PM
 */
package zz.utils.ui;

import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import zz.utils.IPublicCloneable;


public abstract class AbstractOptionPanel extends JPanel implements IPublicCloneable
{
	private JButton itsOKButton;
	private JButton itsCancelButton;
	private List itsOptionListeners = new ArrayList ();
	private JPanel itsButtonsPanel;

	private JComponent itsComponent;

	public AbstractOptionPanel()
	{
		this (false);
	}
	
	/**
	 * This constructor permits to defer the creation of the UI.
	 * A subclass that uses this constructor should take care of
	 * calling {@link #createUI()} itself.
	 */
	public AbstractOptionPanel(boolean aDeferCreation)
	{
		if (! aDeferCreation) createUI();
	}
	
	/**
	 * This abstract method create and returns the main component of the panel.
	 * If an implementation of this method must access fields that are initialized
	 * in the constructor, it should use the {@link #AbstractOptionPanel(boolean)}
	 * constructor, passing <code>true</false> so that this method
	 * doesn't get called immediately. Then, the subclass must manually invoke
	 * {@link #createUI()}.
	 */
	protected abstract JComponent createComponent ();

	protected void createUI ()
	{
		setLayout(new BorderLayout ());
		setBorder (BorderFactory.createEtchedBorder());

		itsButtonsPanel = new JPanel();
		setComponent(createComponent());


		itsOKButton = new JButton ("OK");
		itsOKButton.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				ok ();
			}
		});
		itsCancelButton = new JButton ("Cancel");
		itsCancelButton.addActionListener(new ActionListener ()
		{
			public void actionPerformed (ActionEvent e)
			{
				cancel ();
			}
		});
		itsButtonsPanel.add (itsOKButton);
		itsButtonsPanel.add (itsCancelButton);

		add (itsButtonsPanel, BorderLayout.SOUTH);
		
		updateEnabled();
	}

	protected void setComponent (JComponent aComponent)
	{
		if (itsComponent != null) remove(itsComponent);
		itsComponent = aComponent;
		if (itsComponent != null) add (itsComponent, BorderLayout.CENTER);
	}

	/**
	 * Adds the specified component to the buttons panel.
	 * Should be used by subclasses within the {@link #createComponent} method.
	 */
	protected void addToButtonsPanel (JComponent aComponent)
	{
		itsButtonsPanel.add (aComponent);
	}

	public void addOptionListener (OptionListener aListener)
	{
		itsOptionListeners.add (aListener);
	}

	public void removeOptionListener (OptionListener aListener)
	{
		itsOptionListeners.remove (aListener);
	}

	protected void fireOptionSelected (OptionListener.Option aOption)
	{
		for (Iterator theIterator = itsOptionListeners.iterator (); theIterator.hasNext ();)
		{
			OptionListener theListener = (OptionListener) theIterator.next ();
			theListener.optionSelected(aOption);
		}
	}

	protected void ok ()
	{
		fireOptionSelected(OptionListener.Option.OK);
	}

	protected void cancel ()
	{
		fireOptionSelected(OptionListener.Option.CANCEL);
	}
	
	protected boolean isOkEnabled()
	{
		return true;
	}
	
	public void updateEnabled()
	{
		itsOKButton.setEnabled(isOkEnabled());
	}
	
	public Object clone ()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)	
		{
			return null;
		}
	}
	
	
	
}
