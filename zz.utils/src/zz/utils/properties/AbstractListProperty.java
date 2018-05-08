/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import zz.utils.IPublicCloneable;
import zz.utils.list.ICollectionListener;
import zz.utils.list.IListListener;
import zz.utils.references.HardRef;
import zz.utils.references.IRef;
import zz.utils.references.RefUtils;
import zz.utils.references.WeakRef;

/**
 * Can be used as a base for implementing {@link zz.utils.properties.IListProperty}.
 * Manages listeners.
 * TODO: Use a trait to share with AbstractList
 * @author gpothier
 */
public abstract class AbstractListProperty<E> extends AbstractProperty<List<E>> 
implements IListProperty<E>
{
	/**
	 * We store all the listeners here, be they collection or
	 * list listeners
	 */
	private List<IRef<Object>> itsListListeners;

	public final List<E> get()
	{
		throw new UnsupportedOperationException();
	}
	
	protected abstract List<E> get0(); 
	
	public boolean add(E aElement)
	{
		return get0().add (aElement);
	}

	public void add(int aIndex, E aElement)
	{
		get0().add (aIndex, aElement);
	}

	public boolean addAll(Iterable<? extends E> aCollection)
	{
		boolean theResult = false;
		
		for (E theElement : aCollection) 
		{
			if (add (theElement)) theResult = true;
		}

		return theResult;
	}
	
	public boolean addAll(Collection< ? extends E> aCollection)
	{
		return addAll ((Iterable<? extends E>) aCollection);
	}
	
	public E set(int aIndex, E aElement)
	{
		return get0().set(aIndex, aElement);
	}
	
	public boolean remove(Object aElement)
	{
		return get0().remove (aElement);
	}

	public E remove(int aIndex)
	{
		return get0().remove(aIndex);
	}

	public boolean removeAll(Collection< ? > aC)
	{
		boolean theResult = false;
		
		for (Object theObject : aC)
		{
			if (remove(theObject)) theResult = true;
		}
		
		return theResult;
	}

	public boolean retainAll(Collection< ? > aC)
	{
		boolean theResult = false;

		for (Iterator<E> theIterator = get0().iterator(); theIterator.hasNext();)
		{
			E theElement = theIterator.next();
			if (aC.contains(theElement))
			{
				theIterator.remove();
				theResult = true;
			}
		}
		
		return theResult;
	}

	public int size()
	{
		addToDeps();
		List<E> theList = get0();
		return theList != null ? theList.size() : 0;
	}

	public E get(int aIndex)
	{
		addToDeps();
		return get0().get (aIndex);
	}

	public int indexOf(Object aElement)
	{
		addToDeps();
		return get0().indexOf(aElement);
	}
	
	public boolean contains(Object aElement)
	{
		addToDeps();
		return get0().contains(aElement);
	}

	public boolean containsAll(Collection< ? > aC)
	{
		addToDeps();
		return get0().containsAll(aC);
	}

	public boolean isEmpty()
	{
		addToDeps();
		return get0().isEmpty();
	}

	public Iterator<E> iterator()
	{
		addToDeps();
		return get0().iterator();
	}

	public Object[] toArray()
	{
		addToDeps();
		return get0().toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		addToDeps();
		return get0().toArray(a);
	}

	
	public void addListener (IListListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new WeakRef<Object>(aListener));
	}

	public void addHardListener (IListListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new HardRef<Object>(aListener));
	}
	
	public void removeListener (IListListener<E> aListener)
	{
		if (itsListListeners != null) 
		{
			RefUtils.remove(itsListListeners, aListener);
			if (itsListListeners.size() == 0) itsListListeners = null;
		}
	}

	public void addListener (ICollectionListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new WeakRef<Object>(aListener));
	}

	public void addHardListener (ICollectionListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new HardRef<Object>(aListener));
	}
	
	public void removeListener (ICollectionListener<E> aListener)
	{
		if (itsListListeners != null) 
		{
			RefUtils.remove(itsListListeners, aListener);
			if (itsListListeners.size() == 0) itsListListeners = null;
		}
	}

	/**
	 * This method is called whenever an element is added to this list.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void elementAdded (int aIndex, E aElement)
	{
	}
	
	/**
	 * This method is called whenever an element is removed from this list.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void elementRemoved (int aIndex, E aElement)
	{
	}

	/**
	 * Called whenever an element is added or removed from this list.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void contentChanged ()
	{
	}
	
	
	protected void fireElementAdded (int aIndex, E aElement)
	{
		elementAdded(aIndex, aElement);
		contentChanged();
	
//		ObservationCenter.getInstance().requestObservation(getOwner(), this);
		
		if (itsListListeners == null) return;
		List<Object> theListeners = RefUtils.dereference(itsListListeners);
		
		for (Object theListener : theListeners)
		{
			if (theListener instanceof IListListener)
			{
				IListListener<E> theListPropertyListener = 
					(IListListener) theListener;
				theListPropertyListener.elementAdded(this, aIndex, aElement);
			}
			else if (theListener instanceof ICollectionListener)
			{
				ICollectionListener<E> theCollectionPropertyListener = 
					(ICollectionListener) theListener;
				theCollectionPropertyListener.elementAdded(this, aElement);
			}
		}
	}
	
	protected void fireElementRemoved (int aIndex, E aElement)
	{
		elementRemoved(aIndex, aElement);
		contentChanged();

//		ObservationCenter.getInstance().requestObservation(getOwner(), this);
		
		if (itsListListeners == null) return;
		List<Object> theListeners = RefUtils.dereference(itsListListeners);
		
		for (Object theListener : theListeners)
		{
			if (theListener instanceof IListListener)
			{
				IListListener<E> theListPropertyListener = 
					(IListListener) theListener;
				theListPropertyListener.elementRemoved(this, aIndex, aElement);
			}
			else if (theListener instanceof ICollectionListener)
			{
				ICollectionListener<E> theCollectionPropertyListener = 
					(ICollectionListener) theListener;
				theCollectionPropertyListener.elementRemoved(this, aElement);
			}
		}

	}
	
	/**
	 * Internal method used to clone individual values of this list property.
	 * By default it handles {@link IPublicCloneable} objects and fails in
	 * other cases.
	 * <p>
	 * Normally the provided value pertains to another property and is cloned
	 * so as to become a value of this property.
	 * @see #cloneForOwner(Object, boolean)
	 */
	protected E cloneValue(E aValue)
	{
		if (aValue instanceof IPublicCloneable)
		{
			IPublicCloneable theCloneable = (IPublicCloneable) aValue;
			return (E) theCloneable.clone();
		}
		else throw new UnsupportedOperationException();
	}
	
	public boolean addAll(int aIndex, Collection< ? extends E> aC)
	{
		throw new UnsupportedOperationException();
	}

	public int lastIndexOf(Object aO)
	{
		throw new UnsupportedOperationException();
	}

	public ListIterator<E> listIterator()
	{
		throw new UnsupportedOperationException();
	}

	public ListIterator<E> listIterator(int aIndex)
	{
		throw new UnsupportedOperationException();
	}

	public List<E> subList(int aFromIndex, int aToIndex)
	{
		throw new UnsupportedOperationException();
	}


}
