/*
 * Created on Dec 14, 2004
 */
package zz.utils.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import zz.utils.references.HardRef;
import zz.utils.references.IRef;
import zz.utils.references.RefUtils;
import zz.utils.references.WeakRef;

/**
 * Can be used as a base for implementing {@link zz.utils.properties.IListProperty}.
 * Manages listeners.
 * @author gpothier
 */
public abstract class AbstractList<E>  
implements IList<E>
{
	/**
	 * We store all the listeners here, be they collection or
	 * list listeners
	 */
	private List<IRef<Object>> itsListListeners;
	
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
	
	public boolean containsAll(Collection< ? > aC)
	{
		for (Object theObject : aC) if (! contains(theObject)) return false;
		return true;
	}
	
	public boolean isEmpty()
	{
		return size() == 0;
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

		for (Iterator<E> theIterator = iterator(); theIterator.hasNext();)
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
	
	protected void fireElementAdded (int aIndex, E aElement)
	{
		elementAdded(aIndex, aElement);
	
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
