package zz.utils.notification;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;


/**
 * Maintains system-wide mapping between observable objects and a set of observers.
 * An observable object can request its observers to be notified with
 * the {@link #requestObservation(Object, Object)} method.
 * <br/>
 * Observables and observers are weakly referenced.
 * @author gpothier
 */
//public class ObservationCenter
//{
//	private static ObservationCenter INSTANCE = new ObservationCenter();
//
//	public static ObservationCenter getInstance()
//	{
//		return INSTANCE;
//	}
//
//	private ObservationCenter()
//	{
//	}
//	
//	private Map itsMap = new WeakHashMap ();
//	
//	/**
//	 * Registers an observer with the specified observable.
//	 */
//	public void registerListener (Object aObservable, Observer aObserver)
//	{
//		List theList = (List) itsMap.get(aObservable);
//		if (theList == null)
//		{
//			theList = new ArrayList ();
//			itsMap.put (aObservable, theList);
//		}
//		theList.add (new ObserverReference (aObserver));
//	}
//
//	public void unregisterListener (Object aObservable, Observer aObserver)
//	{
//		assert aObserver != null;
//		List theList = (List) itsMap.get(aObservable);
//		if (theList != null) 
//		{
//			for (Iterator theIterator = theList.iterator(); theIterator.hasNext();)
//			{
//				ObserverReference theReference = (ObserverReference) theIterator.next();
//				Observer theObserver = theReference.get();
//				if (theObserver == null) theIterator.remove();
//				else if (theObserver == aObserver) theIterator.remove();
//			}
//		}
//	}
//	
//	/**
//	 * Notifies all the observers that are observing the specified object.
//	 * @param aData An object that will be passed to the observers.
//	 */
//	public void requestObservation (Object aObservable, Object aData)
//	{
//		List theList = (List) itsMap.get(aObservable);
//		if (theList != null) 
//		{
//			// We clone the list in order to avoid comodifications
//			List theListClone = new ArrayList (theList);
//			for (Iterator theIterator = theListClone.iterator(); theIterator.hasNext();)
//			{
//				ObserverReference theReference = (ObserverReference) theIterator.next();
//				Observer theObserver = theReference.get();
//				if (theObserver == null) theIterator.remove();
//				else theObserver.observe(aObservable, aData);
//			}
//		}
//	}
//	
//	private static class ObserverReference extends WeakReference<Observer>
//	{
//		public ObserverReference (Observer aObserver)
//		{
//			super (aObserver);
//		}
//		
//	}
//}
