/*
 * Created on Nov 15, 2004
 */
package zz.utils.properties;

import java.util.Set;


/**
 * Default implementation of {@link zz.utils.properties.IProperty} for simple values.
 * @author gpothier
 */
public class SimpleProperty<T> extends AbstractProperty<T> 
implements IProperty<T>
{
	
	/**
	 * The actual value of the property
	 */
	private T itsValue;
	
	public SimpleProperty()
	{
	}
	
	public SimpleProperty(T aValue)
	{
		itsValue = aValue;
	}
	
	/**
	 * Standard getter for this property.
	 */
	public T get()
	{
		return get0();
	}

	
	/**
	 * Internal getter for the property.
	 */
	protected final T get0()
	{
		addToDeps();
		return itsValue;
	}

	/**
	 * Indicates if two values are equal.
	 */
	protected boolean equalValues(T aValue1, T aValue2)
	{
		return aValue1 == aValue2;
	}
	
	/**
	 * Sets the property's value without firing listeners.
	 */
	protected final T set0(T aValue) 
	{
		return set0(aValue, true);
	}
	
	/**
	 * Internal setter for the property.
	 * It first checks if a veto rejects the new value. If not, it
	 * sets the current value and fires notifications.
	 * @param aValue The new value of the property.
	 */
	protected final T set0(T aValue, boolean aNotify)
	{
		if (aNotify) 
		{
			T theOldValue = get0();
			if (equalValues(theOldValue, aValue)) return aValue;
			
			Object theCanChange = canChangeProperty(theOldValue, aValue);
			if (theCanChange == REJECT) return theOldValue;
			if (theCanChange != ACCEPT) 
			{
				aValue = (T) theCanChange;
				if (equalValues(theOldValue, aValue)) return aValue;
			}
			
//		if (itsValue != null) ObservationCenter.getInstance().unregisterListener(itsValue, this);
			itsValue = aValue;
//		if (itsValue != null) ObservationCenter.getInstance().registerListener(itsValue, this);
			
			firePropertyChanged(theOldValue, aValue);
		} 
		else 
		{
			itsValue = aValue;
		}
		return itsValue;
	}
}
