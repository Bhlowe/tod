/*
 * Created on Dec 14, 2004
 */
package zz.utils.properties;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zz.utils.list.ICollectionListener;
import zz.utils.references.HardRef;
import zz.utils.references.IRef;
import zz.utils.references.RefUtils;
import zz.utils.references.WeakRef;

/**
 * Can be used as a base for building a {@link zz.utils.properties.ISetProperty}.
 * Manages listeners.
 * @author gpothier
 */
public abstract class AbstractSetProperty<E> extends AbstractProperty<Set<E>> 
implements ISetProperty<E>
{
	/**
	 * We store all the listeners here, be they collection or
	 * list listeners
	 */
	private List<IRef<ICollectionListener<E>>> itsListListeners;
	
	public boolean add(E aElement)
	{
		return get().add (aElement);
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
	
	public boolean remove(Object aElement)
	{
		return get().remove (aElement);
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

		for (Iterator<E> theIterator = get().iterator(); theIterator.hasNext();)
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
		Set<E> theSet = get();
		return theSet != null ? theSet.size() : 0;
	}

	public boolean contains(Object aElement)
	{
		return get().contains(aElement);
	}

	public boolean containsAll(Collection< ? > aC)
	{
		return get().containsAll(aC);
	}

	public boolean isEmpty()
	{
		return get().isEmpty();
	}

	public Iterator<E> iterator()
	{
		return get().iterator();
	}

	public Object[] toArray()
	{
		return get().toArray();
	}

	public <T> T[] toArray(T[] a)
	{
		return get().toArray(a);
	}
	
	public void addListener (ICollectionListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new WeakRef<ICollectionListener<E>>(aListener));
	}

	public void addHardListener (ICollectionListener<E> aListener)
	{
		if (itsListListeners == null) itsListListeners = new ArrayList(3);
		itsListListeners.add (new HardRef<ICollectionListener<E>>(aListener));
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
	 * This method is called whenever an element is added to this set.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void elementAdded (E aElement)
	{
	}
	
	/**
	 * This method is called whenever an element is removed from this set.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void elementRemoved (E aElement)
	{
	}
	
	/**
	 * Called whenever an element is added or removed from this list.
	 * By default it does nothing, but subclasses can override it to be notified.
	 */
	protected void contentChanged ()
	{
	}
	

	
	protected void fireElementAdded (E aElement)
	{
		elementAdded(aElement);
		contentChanged();
	
		if (itsListListeners == null) return;
		List<ICollectionListener<E>> theListeners = 
			RefUtils.dereference(itsListListeners);
		
		for (ICollectionListener<E> theListener : theListeners)
		{
			theListener.elementAdded(this, aElement);
		}
	}
	
	protected void fireElementRemoved (E aElement)
	{
		elementRemoved(aElement);
		contentChanged();
		
		if (itsListListeners == null) return;
		List<ICollectionListener<E>> theListeners = 
			RefUtils.dereference(itsListListeners);
		
		for (ICollectionListener<E> theListener : theListeners)
		{
			theListener.elementRemoved(this, aElement);
		}
	}
	
}
