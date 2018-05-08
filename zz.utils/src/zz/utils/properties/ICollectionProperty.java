/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.Collection;

import zz.utils.list.ICollection;

/**
 * A property whose value is a collection. Additional features are:
 * <li>Collection manipulation methods
 * <li>List listeners.
 * <li>Implements the {@link Iterable} interface.
 * 
 * @param <C> The collection type
 * @param <E> The element type
 * @author gpothier
 */
public interface ICollectionProperty<C extends Collection<E>, E> 
extends IProperty<C>, ICollection<E>
{
}
