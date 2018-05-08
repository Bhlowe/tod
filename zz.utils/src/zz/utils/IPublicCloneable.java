/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Sep 29, 2003
 * Time: 1:32:05 AM
 */
package zz.utils;

/**
 * This interface makes the {@link #clone()} method public
 * and not throwing exceptions.
 * @author gpothier
 */
public interface IPublicCloneable extends Cloneable
{
	public Object clone ();
}
