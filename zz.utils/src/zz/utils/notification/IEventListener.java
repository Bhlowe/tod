/*
 * Created on Mar 28, 2005
 */
package zz.utils.notification;

/**
 * A listener that is notified whenever an event is fired.
 * @author gpothier
 */
public interface IEventListener<T>
{
	public void fired(IEvent<? extends T> aEvent, T aData);
}
