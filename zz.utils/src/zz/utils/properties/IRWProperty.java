/*
 * Created on Nov 15, 2004
 */
package zz.utils.properties;

/**
 * A read-write property.
 * @author gpothier
 */
public interface IRWProperty<T> extends IProperty<T>
{
	/**
	 * Sets the current value of the property.
	 * Note that the new value must be accepted by the property
	 * for the change to be effective.
	 * @param aValue The new value to set to the property
	 * @return The value actually set (as the property can alter the value).
	 */
	public T set (T aValue);
	
	/**
	 * Whether the property can be set to the given value.
	 */
	public boolean canSet(T aValue);
}
