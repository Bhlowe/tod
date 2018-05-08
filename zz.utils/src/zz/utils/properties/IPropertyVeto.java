/*
 * Created on Nov 15, 2004
 */
package zz.utils.properties;

/**
 * A property veto is a special kind of property listener that 
 * is notified before the property changes so that it can reject the change.
 * @author gpothier
 */
public interface IPropertyVeto<T> extends IPropertyListener<T>
{
	public boolean canChangeProperty (IProperty<T> aProperty, T aOldValue, T aNewValue);
}
