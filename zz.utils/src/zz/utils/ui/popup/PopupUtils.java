/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 27 f�vr. 2003
 * Time: 15:02:03
 */
package zz.utils.ui.popup;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;

import zz.utils.Utils;
import zz.utils.ui.UIUtils;

/**
 * Provides utilities for the popup classes.
  */
public class PopupUtils
{


	/**
	 * A method that computes the bounds (in screen coordinates) of a popup component
	 * according to the size & position of a popup trigger component.
	 * @param aPopupTriggerComponent The component that triggered the popup.
	 * The returned bounds are calculated so that the popup component appears next to
	 * the popup trigger component
	 * @param aPreferredDirection Specifies in which direction the popup should open if
	 * there is no space constraint.
	 * One of JLabel.TOP, JLabel.BOTTOM, JLabel.LEFT, JLabel.RIGHT
	 * @return Suggested screen coordinates for the popup
	 */
	public static Rectangle computePopupBounds (JComponent aPopupTriggerComponent, int aPreferredDirection, Dimension aPopupDimension)
	{
		return computePopupBounds(aPopupTriggerComponent.getRootPane(), aPopupTriggerComponent, aPreferredDirection, aPopupDimension);
	}

	public static Rectangle computePopupBounds (JFrame aOwner, JComponent aPopupTriggerComponent, int aPreferredDirection, Dimension aPopupDimension)
	{
		return computePopupBounds(aOwner.getRootPane(), aPopupTriggerComponent, aPreferredDirection, aPopupDimension);
	}

	public static Rectangle computePopupBounds (JRootPane aRootPane, JComponent aPopupTriggerComponent, int aPreferredDirection, Dimension aPopupDimension)
	{
		JRootPane rp = aRootPane;
		JLayeredPane lp = rp.getLayeredPane ();

		Point lpls = lp.getLocationOnScreen();
		Point ls = aPopupTriggerComponent.getLocationOnScreen();
		int myW = aPopupTriggerComponent.getWidth ();
		int myH = aPopupTriggerComponent.getHeight ();
		Rectangle theScreenBounds = UIUtils.getMaximumWindowBounds();
		int availableWidth = (int) (theScreenBounds.width);
		int availableHeight = (int) (theScreenBounds.height);

		int spaceAbove = ls.y - lpls.y;
		int spaceUnder = (lpls.y + availableHeight) - (ls.y + myH);
		int spaceBefore = ls.x - lpls.x;
		int spaceAfter = (lpls.x + availableWidth) - (ls.x + myW);

		int x;
		int y;
		int w = aPopupDimension.width;
		int h = aPopupDimension.height;

		if (w > availableWidth) w = availableWidth;
		if (h > availableHeight) h = availableHeight;

		x = ls.x;
		y = ls.y;

		switch (aPreferredDirection)
		{
			case JLabel.TOP:
				if (h <= spaceAbove) y -= h;
				else if (h <= spaceUnder) y += myH;
				else
				{
					y = 0;
					if (w <= spaceAfter) x += myW;
					else if (w <= spaceBefore) x -= w;
					else x = 0;
				}
				break;

			case JLabel.BOTTOM:
				if (h <= spaceUnder) y += myH;
				else if (h <= spaceAbove) y -= h;
				else
				{
					y = 0;
					if (w <= spaceAfter) x += myW;
					else if (w <= spaceBefore) x -= w;
					else x = 0;
				}
				break;

			case JLabel.LEFT:
				if (w <= spaceBefore) x -= w;
				else if (w <= spaceAfter) x += myW;
				else
				{
					x = 0;
					if (h <= spaceUnder) y += myH;
					else if (h <= spaceAbove) y -= h;
					else y = 0;
				}
				break;

			case JLabel.RIGHT:
				if (w <= spaceAfter) x += myW;
				else if (w <= spaceBefore) x -= w;
				else
				{
					x = 0;
					if (h <= spaceUnder) y += myH;
					else if (h <= spaceAbove) y -= h;
					else y = 0;
				}
				break;
		}

		if (h < aPopupDimension.height) w += 30; // To compensate for a scrollbar
		if (w < aPopupDimension.width) h += 30;

		if (x+w > availableWidth) x = availableWidth - w;
		if (y+h > availableHeight) y = availableHeight - h;

		return new Rectangle (x, y, w, h);
	}

	/**
	 * Displays the sepcified popup menu next to the specified popup trigger component.
	 */
	public static void showPopupMenu (JComponent aPopupTriggerComponent, int aPreferredDirection, JPopupMenu aPopupMenu)
	{
		Rectangle theScreenBounds = computePopupBounds(aPopupTriggerComponent, aPreferredDirection, aPopupMenu.getPreferredSize());

		Point theLocationOnScreen = aPopupTriggerComponent.getLocationOnScreen();
		int x = theScreenBounds.x - theLocationOnScreen.x;
		int y = theScreenBounds.y - theLocationOnScreen.y;
		aPopupMenu.show(aPopupTriggerComponent, x, y);
	}
	
}
