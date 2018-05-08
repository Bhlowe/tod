/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 4, 2001
 * Time: 11:12:09 AM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.ui;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import zz.utils.Formatter;

/**
 * A renderer (both tree & list) to be used with {@link Formatter}
 * Subclasses can override setupLabel to customize the rendering.
 */
public class FormatterRenderer implements ListCellRenderer, TreeCellRenderer
{
	private Formatter itsFormatter;
	private Formatter itsDescriptionFormatter;

	public FormatterRenderer (Formatter aFormatter)
	{
		this (aFormatter, null);
	}

	public FormatterRenderer (Formatter aFormatter, Formatter aDescriptionFormatter)
	{
		itsFormatter = aFormatter;
		itsDescriptionFormatter = aDescriptionFormatter;
	}

	public Formatter getFormatter ()
	{
		return itsFormatter;
	}

	public void setFormatter (Formatter aFormatter)
	{
		itsFormatter = aFormatter;
	}

	public Formatter getDescriptionFormatter ()
	{
		return itsDescriptionFormatter;
	}

	public void setDescriptionFormatter (Formatter aDescriptionFormatter)
	{
		itsDescriptionFormatter = aDescriptionFormatter;
	}

	private DefaultListCellRenderer itsDefaultListCellRenderer;
	private DefaultTreeCellRenderer itsDefaultTreeCellRenderer;

	public Component getListCellRendererComponent (
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
	{
		if (itsDefaultListCellRenderer == null) itsDefaultListCellRenderer = new DefaultListCellRenderer();

		JLabel theLabel = (JLabel) itsDefaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		setupLabel(theLabel, value);

		return theLabel;
	}

	public Component getTreeCellRendererComponent (JTree tree, Object value,
												   boolean selected, boolean expanded,
												   boolean leaf, int row, boolean hasFocus)
	{
		if (itsDefaultTreeCellRenderer == null) itsDefaultTreeCellRenderer = new DefaultTreeCellRenderer();

		JLabel theLabel = (JLabel) itsDefaultTreeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) value;
			value = theNode.getUserObject();
		}

		setupLabel(theLabel, value);

		return theLabel;
	}

	/**
	 *  This method sets up the specified label so that it renders properly the specified NamedObject.
	 * @param aLabel A label to set up.
	 * @param aValue The NamedObject to render.
	 */
	protected void setupLabel (JLabel aLabel, Object aValue)
	{
		String theText = getFormatter().getHtmlText(aValue);
		aLabel.setText(theText);

		String theDescription = getDescriptionFormatter() != null 
			? getDescriptionFormatter().getPlainText(aValue) 
			: "";
			
		aLabel.setToolTipText(theDescription);
	}
}
