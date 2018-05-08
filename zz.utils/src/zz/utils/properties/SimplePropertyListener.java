/*
 * Created on Dec 15, 2004
 */
package zz.utils.properties;

/**
 * An adapter class for {@link zz.utils.properties.IPropertyListener}.
 * @author gpothier
 */
public abstract class SimplePropertyListener<T> implements IPropertyListener<T>
{
	public void propertyChanged(IProperty<T> aProperty, T aOldValue, T aNewValue)
	{
		changed(aProperty);
	}

	public void propertyValueChanged(IProperty<T> aProperty)
	{
		changed(aProperty);
	}
	
	protected abstract void changed(IProperty<T> aProperty);


}
