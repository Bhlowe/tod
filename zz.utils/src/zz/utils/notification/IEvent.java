/*
 * Created on Mar 28, 2005
 */
package zz.utils.notification;

/**
 * Reifies an event. Listeners can be registered and will be notified whenever the event is fired.
 * The firing mewchanism is not exposed by this API.
 * @author gpothier
 */
public interface IEvent<T>
{
	public void addListener (IEventListener<? super T> aListener);
	public void removeListener (IEventListener<? super T> aListener);
}
