/*
 * Created on Dec 15, 2004
 */
package zz.utils.properties;

/**
 * An adapter class for {@link zz.utils.properties.IPropertyListener}.
 * @author gpothier
 */
public class PropertyListener<T> implements IPropertyListener<T>
{
	public void propertyChanged(IProperty<T> aProperty, T aOldValue, T aNewValue)
	{
	}

	public void propertyValueChanged(IProperty<T> aProperty)
	{
	}
}
