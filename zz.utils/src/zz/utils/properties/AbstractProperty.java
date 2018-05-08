/*
 * Created on Nov 15, 2004
 */
package zz.utils.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import zz.utils.PublicCloneable;
import zz.utils.references.HardRef;
import zz.utils.references.IRef;
import zz.utils.references.RefUtils;
import zz.utils.references.WeakRef;

/**
 * Can be used as a base to implement properties.
 * Takes care of listeners and owner, only leaves
 * the handling of the value to subclasses.
 * @author gpothier
 */
public abstract class AbstractProperty<T> extends PublicCloneable implements IProperty<T>
{
	protected static Object ACCEPT = new Object();
	protected static Object REJECT = new Object();
	
	/**
	 * We maintain the number of veto listeners for optimization purposes.
	 * This is an approximation, as weakly referenced veto listsners can be garbage collected.
	 */
	private int itsVetoCount = 0;
	
	private List<IRef<IPropertyListener<? super T>>> itsListeners; 
	
	/**
	 * Adds this property to the current ComputedProperty's dependency list, if any.
	 */
	protected void addToDeps() 
	{
		Set<IProperty> deps = ComputedProperty.peekDeps();
		if (deps != null) deps.add(this);
	}
	
	/**
	 * This method is called whenever the value of this property changes.
	 * It does nothing by default, but subclasses can override it.
	 */
	protected void changed (T aOldValue, T aNewValue)
	{
	}

	private synchronized List<IPropertyListener<? super T>> dereferenceListeners()
	{
		return RefUtils.dereference(itsListeners);
	}
	
	protected synchronized void firePropertyChanged (T aOldValue, T aNewValue)
	{
		changed(aOldValue, aNewValue);
		if (itsListeners != null)
		{
			List<IPropertyListener<? super T>> theListeners = dereferenceListeners(); 
	
			for (IPropertyListener<? super T> theListener : theListeners) {
				theListener.propertyChanged((IProperty) this, aOldValue, aNewValue);
			}
		}
		
//		ObservationCenter.getInstance().requestObservation(getOwner(), this);
	}
	
	/**
	 * This method is called before the property is changed and gives
	 * the property the opportunity to refuse or adapt a change request.
	 * @return {@link #ACCEPT} if the new value should be accepted (if 
	 * also accepted by veto listeners),
	 * {@link #REJECT} if the new value should be rejected. 
	 * Another value will set the property to that value instead of the 
	 * requested value, bypassing the veto listeners.
	 */
	protected Object canChange (T aOldValue, T aNewValue)
	{
		return ACCEPT;
	}
	
	protected final Object canChangeProperty (T aOldValue, T aNewValue)
	{
		Object theCanChange = canChange(aOldValue, aNewValue);
		if (theCanChange == REJECT) return REJECT;
		if (theCanChange != ACCEPT) return theCanChange;
		
		if (itsVetoCount <= 0) return ACCEPT;
		List<IPropertyListener<? super T>> theListeners = dereferenceListeners();

		for (IPropertyListener<? super T> theListener : theListeners)
		{
			if (theListener instanceof IPropertyVeto)
			{
				IPropertyVeto<? super T> theVeto = (IPropertyVeto<? super T>) theListener;
				if (! theVeto.canChangeProperty((IProperty) this, aOldValue, aNewValue)) return REJECT;
			}
		}
		
		return ACCEPT;
	}
	
	public synchronized void addListener (IPropertyListener<? super T> aListener)
	{
		if (itsListeners == null) itsListeners = new ArrayList<IRef<IPropertyListener<? super T>>>();
		if (aListener instanceof IPropertyVeto) itsVetoCount++;
		itsListeners.add (new WeakRef<IPropertyListener<? super T>>(aListener));
	}

	public synchronized void addHardListener (IPropertyListener<? super T> aListener)
	{
		if (itsListeners == null) itsListeners = new ArrayList<IRef<IPropertyListener<? super T>>>();
		if (aListener instanceof IPropertyVeto) itsVetoCount++;
		itsListeners.add (new HardRef<IPropertyListener<? super T>>(aListener));
	}
	
	public void addListener(IPropertyListener< ? super T> aListener, boolean aHard)
	{
		if (aHard) addHardListener(aListener);
		else addListener(aListener);
	}

	public synchronized void removeListener (IPropertyListener<? super T> aListener)
	{
		if (itsListeners != null) 
		{
			if (RefUtils.removeFromList(itsListeners, aListener))
			{
				if (itsListeners.size() == 0) itsListeners = null;
				if (aListener instanceof IPropertyVeto) itsVetoCount--;				
			}
		}
	}

	public String toString()
	{
		return String.format ("Property (value: %s)", get());
	}
	
}
