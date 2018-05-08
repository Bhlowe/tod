/*
 * Created on Nov 15, 2004
 */
package zz.utils.properties;

/**
 * A listener that is notified of property reference or value changes.
 * @author gpothier
 */
public interface IPropertyListener<T>
{
	/**
	 * This method is called when the property acquires a new value.
	 * @param aProperty The property that changed.
	 * @param aOldValue The previous value of the property
	 * @param aNewValue The new value of the property.
	 */
	public void propertyChanged (IProperty<T> aProperty, T aOldValue, T aNewValue);
}
