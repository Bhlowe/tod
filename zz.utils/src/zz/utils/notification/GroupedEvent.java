/*
 * Created on Mar 28, 2005
 */
package zz.utils.notification;

/**
 * Use this implementation of {@link zz.utils.notification.IEvent} for objects that use
 * many events, so as to reduce the memory footprint.
 * Usage:
 * <br/>
 * <pre>
 * private EventGroup itsGroup = new EventGroup();
 * public final IEvent<T> eXxxx = new GroupedEvent (itsGroup);
 * ...
 * public final IEvent<T> eYyyy = new GroupedEvent (itsGroup);
 * </pre>
 * 
 * See {@link zz.utils.notification.EventGroup}.
 * @author gpothier
 */
public class GroupedEvent<T> extends AbstractEvent<T>
{
	private EventGroup itsGroup;

	public GroupedEvent(EventGroup aGroup)
	{
		itsGroup = aGroup;
	}

	public void fire(T aData)
	{
		itsGroup.fire(this, aData);
	}

	public void addListener(IEventListener< ? super T> aListener)
	{
		itsGroup.addListener(this, aListener);
	}

	public void removeListener(IEventListener< ? super T> aListener)
	{
		itsGroup.removeListener(this, aListener);
	}

	public void fired(IEvent< ? extends T> aEvent, T aData)
	{
		fire(aData);
	}
	
	
}
