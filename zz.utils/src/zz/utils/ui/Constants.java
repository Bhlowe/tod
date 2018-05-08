/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 oct. 2002
 * Time: 14:20:00
 */
package zz.utils.ui;

import java.awt.Cursor;
import java.awt.Font;

/**
 * A few constants for fonts and cursors. 
 * @author gpothier
 */
public class Constants
{

	public static final Font DEFAULT_FONT_PLAIN = new Font("SansSerif", Font.PLAIN, 12);
	public static final Font DEFAULT_FONT_BOLD = new Font("SansSerif", Font.BOLD, 12);
	public static final Font DEFAULT_FONT_ITALIC = new Font("SansSerif", Font.ITALIC, 12);

	public static final Font SMALL_FONT_PLAIN = new Font("SansSerif", Font.PLAIN, 9);


	public static final Cursor DEFAULT_CURSOR = Cursor.getDefaultCursor();
	public static final Cursor LINK_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	public static final Cursor BUSY_CURSOR = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
	public static final Cursor CROSS_CURSOR = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	public static final Cursor TEXT_CURSOR = Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR);
	public static final Cursor MOVE_CURSOR = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
	public static final Cursor SW_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
	public static final Cursor SE_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
	public static final Cursor NW_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
	public static final Cursor NE_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
	public static final Cursor N_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
	public static final Cursor S_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
	public static final Cursor E_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
	public static final Cursor W_RESIZE_CURSOR = Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
}
