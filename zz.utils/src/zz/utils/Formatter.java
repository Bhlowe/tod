/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 30 oct. 2002
 * Time: 17:59:10
 */
package zz.utils;

import javax.swing.ImageIcon;

/**
 * Serves to give a textual representation for an object.
 */
public interface Formatter<T>
{
	/**
	 * Returns a plain text representation of the object.
	 */
	public String getPlainText (T aObject);
	
	/**
	 * Returns an html representation of the object.
	 */
	public String getHtmlText (T aObject);
	
	/**
	 * Returns an icon that represents the object
	 */
	public ImageIcon getIcon (T aObject);
}
