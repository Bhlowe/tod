/*
 * Created on Jun 15, 2004
 */
package zz.utils.notification;


/**
 * A listener that is notified when an observed object changes.
 * @see zz.utils.notification.ObservationCenter
 * @author gpothier
 */
public interface Observer
{
	/**
	 * Called when an observation is requested for an observable object.
	 * @param aObservable The object for which the observation is requested.
	 * @param aData Additional data passed to the observer.
	 */
	public void observe (Object aObservable, Object aData);
}
