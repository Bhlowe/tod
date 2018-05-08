/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: 22 mars 02
 * Time: 15:53:06
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

/**
 * This interface is for stacks that support a size limit.
 */
public interface LimitableStack<T> extends Stack<T>
{
	public int getMaximumSize ();
	public void setMaximumSize (int aMaximumSize);
	public boolean isFull();
}
