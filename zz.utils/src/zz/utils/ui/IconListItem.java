/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 1 mars 2003
 * Time: 17:37:35
 */
package zz.utils.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;

/**
 * This class prerenders list items. Useful for HTML items.
 *
 */
public abstract class IconListItem
{
	private Icon itsIcon;

	/**
	 * Returns the text to use within the list.
	 */
	public abstract String getListText ();

	private void createIcon ()
	{
		JLabel theLabel = new JLabel (getListText ())
		{
			protected void paintComponent (Graphics g)
			{
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint (RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				super.paintComponent (g);
			}
		};
		Dimension theSize = theLabel.getPreferredSize ();
		theLabel.setSize (theSize);
		BufferedImage theImage = new BufferedImage (theSize.width, theSize.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D theGraphics = theImage.createGraphics ();
		theLabel.paint (theGraphics);
		itsIcon = new ImageIcon (theImage);
	}

	public Icon getIcon ()
	{
		if (itsIcon == null) createIcon ();
		return itsIcon;
	}

	/**
	 * This is the renderer to use with this class.
	 * Antialiases the item.
	 */
	public static class ListItemRenderer extends DefaultListCellRenderer
	{
		private static final ListItemRenderer INSTANCE = new ListItemRenderer ();

		public static ListItemRenderer getInstance ()
		{
			return INSTANCE;
		}

		private ListItemRenderer ()
		{
		}


		public Component getListCellRendererComponent (
				JList list,
				Object value,
				int index,
				boolean isSelected,
				boolean cellHasFocus)
		{
			JLabel theLabel = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			IconListItem theItem = (IconListItem) value;
			theLabel.setText(null);
			theLabel.setIcon(theItem.getIcon());
			return theLabel;
		}
	}



}
