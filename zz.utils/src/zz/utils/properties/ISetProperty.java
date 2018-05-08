/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A property whose value is a {@link java.util.Set}. Additional features are:
 * <li>Set manipulation methods
 * 
 * @author gpothier
 */
public interface ISetProperty<E> extends ICollectionProperty<Set<E>, E>
{
	/**
	 * Indicates if this set property contains the specified object.
	 */
	public boolean contains (Object aObject);
}
