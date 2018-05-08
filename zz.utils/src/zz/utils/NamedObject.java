/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Dec 4, 2001
 * Time: 11:05:25 AM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import javax.swing.ImageIcon;

/**
 * This interface is for objects that have a name, icon, etc. and don't want that
 * their toString method is used to determine their name.<p>
 * They can be used with NamedObjectHolder, the toString method of which returns
 * the name of the held object.<p>
 * This is useful for example to put ALMModelObjects in a JList.
 */
public interface NamedObject
{
	public String getName ();
	public ImageIcon getIcon ();
	public String getDescription ();
}
