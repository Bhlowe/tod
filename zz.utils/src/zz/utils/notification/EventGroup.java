/*
 * Created on Mar 28, 2005
 */
package zz.utils.notification;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class permits to reduce the memory footprint for objects that need several events
 * by sharing the listeners list for all the objects.
 * It should be used in conjunction with {@link GroupedEvent}. 
 * @author gpothier
 */
public class EventGroup
{
	private List<Entry<?>> itsListeners;
	
	public <T> void addListener(IEvent<T> aEvent, IEventListener<? super T> aListener)
	{
		if (itsListeners == null) itsListeners = new ArrayList<Entry<?>>(3);
		itsListeners.add (new Entry<T>(aEvent, aListener));
	}

	public <T> void removeListener(IEvent<T> aEvent, IEventListener<? super T> aListener)
	{
		if (itsListeners != null) for (Iterator theIterator = itsListeners.iterator(); theIterator.hasNext();)
		{
			Entry<?> theEntry = (Entry<?>) theIterator.next();
			if (theEntry.equals(aEvent, aListener)) 
			{
				theIterator.remove();
				break;
			}
		}
	}
	
	public <T> void fire(IEvent<T> aEvent, T aData)
	{
		if (itsListeners != null) for (Entry<?> theEntry : itsListeners)
		{
			if (theEntry.isFor(aEvent)) theEntry.fire(aData); 
		}
	}
	
	
	/**
	 * A listsner entry
	 */
	private static class Entry<T>
	{
		private IEvent<T> itsEvent;
		private IEventListener<? super T> itsListener;
		
		public Entry(IEvent<T> aEvent, IEventListener<? super T> aListener)
		{
			itsEvent = aEvent;
			itsListener = aListener;
		}
		
		public void fire (Object aData)
		{
			itsListener.fired(itsEvent, (T) aData);
		}
		
		public boolean equals (IEvent<?> aEvent, IEventListener<?> aListener)
		{
			return itsListener == aListener && itsEvent == aEvent;
		}
		
		public boolean isFor (IEvent<?> aEvent)
		{
			return itsEvent == aEvent;
		}
	}
}
