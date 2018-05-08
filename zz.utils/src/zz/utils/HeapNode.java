/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 8, 2002
 * Time: 3:14:23 PM
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

/**
 * Node to be used by {@link Heap}
 */
public interface HeapNode
{
	/**
	 * Returns the value this is used to sort this node.
	 */
	public double getValue ();
}
