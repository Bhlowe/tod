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
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import zz.utils.NamedObject;

/**
 * A renderer (both tree & list) for NamedObjects.
 * Subclasses can override setupLabel to customize the rendering.
 */
public class NamedObjectRenderer implements ListCellRenderer, TreeCellRenderer
{
	private static final NamedObjectRenderer INSTANCE = new NamedObjectRenderer();

	public static NamedObjectRenderer getInstance ()
	{
		return INSTANCE;
	}

	private NamedObjectRenderer ()
	{
	}

	protected DefaultListCellRenderer itsDefaultListCellRenderer;
	protected DefaultTreeCellRenderer itsDefaultTreeCellRenderer;

	public Component getListCellRendererComponent (
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
	{
		if (itsDefaultListCellRenderer == null) itsDefaultListCellRenderer = new DefaultListCellRenderer();

		JLabel theLabel = (JLabel) itsDefaultListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

		NamedObject theNamedObject = null;
		if (value instanceof NamedObject) theNamedObject = (NamedObject) value;
		else if (value instanceof NamedObjectHolder) theNamedObject = ((NamedObjectHolder)value).getNamedObject();

		if (theNamedObject != null) setupLabel(theLabel, theNamedObject);

		return theLabel;
	}

	public Component getTreeCellRendererComponent (JTree tree, Object value,
												   boolean selected, boolean expanded,
												   boolean leaf, int row, boolean hasFocus)
	{
		if (itsDefaultTreeCellRenderer == null) itsDefaultTreeCellRenderer = new DefaultTreeCellRenderer();

		JLabel theLabel = (JLabel) itsDefaultTreeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		NamedObject theNamedObject = null;
		if (value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) value;
			value = theNode.getUserObject();
		}
		if (value instanceof NamedObject) theNamedObject = (NamedObject) value;
		else if (value instanceof NamedObjectHolder) theNamedObject = ((NamedObjectHolder)value).getNamedObject();

		if (theNamedObject != null) setupLabel(theLabel, theNamedObject);

		return theLabel;
	}

	/**
	 *  This method sets up the specified label so that it renders properly the specified NamedObject.
	 * @param aLabel A label to set up.
	 * @param aNamedObject The NamedObject to render.
	 */
	protected void setupLabel (JLabel aLabel, NamedObject aNamedObject)
	{
		String theName = aNamedObject.getName();
		if (theName != null) aLabel.setText(theName);

		String theDescription = aNamedObject.getDescription();
		if (theDescription != null) aLabel.setToolTipText(theDescription);

		ImageIcon theIcon = aNamedObject.getIcon();
		if (theIcon != null) aLabel.setIcon(theIcon);
	}
}
