/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.Iterator;
import java.util.List;

import zz.utils.list.IList;
import zz.utils.list.IListListener;

/**
 * A property whose value is a list. Additional features are:
 * <li>List manipulation methods
 * <li>List listeners.
 * <li>Implements the {@link Iterable} interface.
 * 
 * @author gpothier
 */
public interface IListProperty<E> 
extends ICollectionProperty<List<E>, E>, IList<E>
{
}
