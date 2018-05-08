/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 oct. 2002
 * Time: 18:09:53
 */
package zz.utils.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 * A multipurpose renderer: can be used for list, tree and tables.
 * Subclasses should at least override {@link #getName(Object)}, and
 * can optionally override {@link #getIcon(Object)}, {@link #getTextColor(Object)},
 * {@link #getToolTipText(Object)}, {@link #getBackgroundColor(Object)} to further
 * customize the rendering.
 * For full coontrol over the rendering, override {@link #setupLabel(JLabel, Object)}.
 * 
 * @param <V> Type of the objects that are found in the underlying model.
 * 
 * @author gpothier
 */
public abstract class UniversalRenderer<V> implements ListCellRenderer, TreeCellRenderer, TableCellRenderer
{
	private ListCellRenderer itsListCellRenderer;
	private TreeCellRenderer itsTreeCellRenderer;
	private TableCellRenderer itsTableCellRenderer;
	
	private boolean itsOpaque;
	private int itsHorizontalAlignment;
	private int itsVerticalAlignment;

	public UniversalRenderer()
	{
		this (true);
	}

	/**
	 * Creates a renderer.
	 * @param aOpaque Indicates if this renderer should produce opaque components.
	 */
	public UniversalRenderer(boolean aOpaque)
	{
		this (aOpaque, JLabel.LEADING, JLabel.CENTER);
	}
	
	public UniversalRenderer(
			boolean aOpaque, 
			int aHorizontalAlignment, 
			int aVerticalAlignment)
	{
		itsOpaque = aOpaque;
		itsHorizontalAlignment = aHorizontalAlignment;
		itsVerticalAlignment = aVerticalAlignment;
	}
	
	/**
	 * Initial setup of the renderers.
	 */
	private void setupRenderer(JLabel aRenderer)
	{
		aRenderer.setHorizontalAlignment(itsHorizontalAlignment);
		aRenderer.setVerticalAlignment(itsVerticalAlignment);
	}
	
	protected ListCellRenderer createListCellRenderer ()
	{
		DefaultListCellRenderer theRenderer = new DefaultListCellRenderer ();
		setupRenderer(theRenderer);
		theRenderer.setOpaque(itsOpaque);
		return theRenderer;
	}

	protected TreeCellRenderer createTreeCellRenderer ()
	{
		DefaultTreeCellRenderer theRenderer = new DefaultTreeCellRenderer ();
		setupRenderer(theRenderer);
		// Setting the opacity on tree renderers causes probles with selection.
		return theRenderer;
	}

	protected TableCellRenderer createTableCellRenderer ()
	{
		DefaultTableCellRenderer theRenderer = new DefaultTableCellRenderer ();
		setupRenderer(theRenderer);
		theRenderer.setOpaque(itsOpaque);
		return theRenderer;
	}

	public Component getListCellRendererComponent (
			JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
	{
		if (itsListCellRenderer == null) itsListCellRenderer = createListCellRenderer();

		JLabel theLabel = (JLabel) itsListCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setupLabel(theLabel, (V) value);
		return theLabel;
	}
	
	public Component getTableCellRendererComponent(
			JTable aTable, 
			Object aValue, 
			boolean aIsSelected,
			boolean aHasFocus, 
			int aRow, 
			int aColumn)
	{
		if (itsTableCellRenderer == null) itsTableCellRenderer = createTableCellRenderer();

		JLabel theLabel = (JLabel) itsTableCellRenderer.getTableCellRendererComponent(aTable, aValue, aIsSelected, aHasFocus, aRow, aColumn);

		setupLabel(theLabel, (V) aValue);

		return theLabel;
	}
	
	public Component getTreeCellRendererComponent (JTree tree, Object value,
												   boolean selected, boolean expanded,
												   boolean leaf, int row, boolean hasFocus)
	{
		if (itsTreeCellRenderer == null) itsTreeCellRenderer = createTreeCellRenderer();

		JLabel theLabel = (JLabel) itsTreeCellRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);

		if (value instanceof DefaultMutableTreeNode)
		{
			DefaultMutableTreeNode theNode = (DefaultMutableTreeNode) value;
			value = theNode.getUserObject();
		}
		if (value != null) setupLabel(theLabel, (V) value);

		return theLabel;
	}

	/**
	 * Returns the text to display in the control.
	 * @param aObject The object that is being rendered
	 */
	protected String getName (V aObject)
	{
		throw new UnsupportedOperationException("This method must be overridden if setupLabel is not overridden");
	}

	/**
	 * Returns an optional tooltip text
	 * @param aObject The object that is being rendered
	 */
	protected String getToolTipText (V aObject)
	{
		return null;
	}

	/**
	 * Returns an optional icon
	 * @param aObject The object that is being rendered
	 */
	protected Icon getIcon (V aObject)
	{
		return null;
	}

	/**
	 * Returns an optional color for the text.
	 * @param aObject The object that is being rendered
	 */
	protected Color getTextColor (V aObject)
	{
		return null;
	}
	
	/**
	 * Returns an optional background color.
	 * @param aObject The object that is being rendered.
	 */
	protected Color getBackgroundColor (V aObject)
	{
		return null;
	}


	/**
	 * This method sets up the specified label so that it renders properly the specified object.
	 * @param aLabel A label to set up.
	 */
	protected void setupLabel (JLabel aLabel, V aValue)
	{
		String theName = getName(aValue);
		aLabel.setText(theName != null ? theName : "");

		String theToolTipText = getToolTipText(aValue);
		aLabel.setToolTipText(theToolTipText);

		Icon theIcon = getIcon(aValue);
		aLabel.setIcon(theIcon);

		Color theTextColor = getTextColor (aValue);
		if (theTextColor != null) aLabel.setForeground(theTextColor);
		
		Color theBackgroundColor = getBackgroundColor(aValue);
		if (theBackgroundColor != null) aLabel.setBackground(theBackgroundColor);
	}
}
