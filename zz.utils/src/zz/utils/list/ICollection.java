/*
 * Created on Dec 14, 2004
 */
package zz.utils.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Models a collection of objects.
 * This is similar to Java's collections, but it includes
 * support for listeners.
 * 
 * @param <C> The collection type
 * @param <E> The element type
 * @author gpothier
 */
public interface ICollection<E> extends Collection<E>
{
	/**
	 * Adds a listener that will be notified each time this
	 * property changes.
	 * The property will maintains a weak reference to the listener,
	 * so the programmer should ensure that the listener is strongly
	 * referenced somewhere.
	 * In particular, this kind of construct should not be used:
	 * <pre>
	 * prop.addListener (new MyListener());
	 * </pre>
	 * In this case, use {@link #addHardListener(IListPropertyListener)}
	 * instead.
	 */
	public void addListener (ICollectionListener<E> aListener);

	/**
	 * Adds a listener that will be notified each time this
	 * property changes.
	 * The listener will be referenced through a strong reference.
	 * @see #addListener(IPropertyListener)
	 */
	public void addHardListener (ICollectionListener<E> aListener);
	
	/**
	 * Removes a previously added listener.
	 */
	public void removeListener (ICollectionListener<E> aListener);


}
