/*
 * Created on Dec 14, 2004
 */
package zz.utils.list;

import java.util.Iterator;
import java.util.List;

import zz.utils.properties.IPropertyListener;

/**
 * Models a list. This is similar to Java's lists, but 
 * it additionally supports listeners
 * @author gpothier
 */
public interface IList<E> extends ICollection<E>, List<E>
{
	/**
	 * Returns the element at the specified index.
	 */
	public E get (int aIndex);
	
	public void add (int aIndex, E aElement);
	public E set (int aIndex, E aElement);
	
	/**
	 * Adds all the elements from the specified iterable into this list.
	 */
	public boolean addAll (Iterable<? extends E> aCollection);
	
	/**
	 * Returns the index of the specified element,
	 * or -1 if it is not present in the list.
	 */
	public int indexOf (Object aElement);
	
	/**
	 * Removes the element at the specified index.
	 * @return The removed element. 
	 */
	public E remove (int aIndex);
	
	/**
	 * Returns an iterator over all the elements of this  container,
	 * in reverse order.
	 */
	public Iterator<E> reverseIterator ();


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
	public void addListener (IListListener<E> aListener);

	/**
	 * Adds a listener that will be notified each time this
	 * property changes.
	 * The listener will be referenced through a strong reference.
	 * @see #addListener(IPropertyListener)
	 */
	public void addHardListener (IListListener<E> aListener);
	
	/**
	 * Removes a previously added listener.
	 */
	public void removeListener (IListListener<E> aListener);


}
