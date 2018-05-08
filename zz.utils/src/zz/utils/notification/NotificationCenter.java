/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 18, 2002
 * Time: 2:15:59 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.notification;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zz.utils.ListMap;

/**
 * This class acts as a bridge between listenables and listeners.
 * Listeners ({@link Notifiable}) register themselves to a notification center, specifying their
 * interest:
 * <li> Messages that concern a specific object: {@link #addNotifiableForSource}
 * <li> Messages of a specific kind: {@link #addNotifiableForMessageClass}
 * Listeners simply send messages to the notification center, they don't need to
 * be aware of the message recipients.
 * <p>
 * Note that the notification center weakly references its notifiables, thus
 * allowing them to be garbage collected when they are not otherwise referenced.
 */
public class NotificationCenter
{
	private static final NotificationCenter itsDefaultNotificationCenter = new NotificationCenter ();

	public static NotificationCenter getDefaultNotificationCenter ()
	{
		return itsDefaultNotificationCenter;
	}

	/**
	 * Maps source objects to notifiable lists
	 */
	private ListMap itsSourceMap = new ListMap ();

	/**
	 * Maps message names to notifiable lists
	 */
	private ListMap itsMessageClassMap = new ListMap ();

	private ReferenceQueue itsReferenceQueue = new ReferenceQueue ();

	private boolean itsActive = true;

	public NotificationCenter ()
	{
//		javax.swing.Timer theTimer = new Timer(4000, new ActionListener ()
//		{
//			public void actionPerformed (ActionEvent e)
//			{
//				printStatus ();
//			}
//		});
//		theTimer.start();
	}

	private void printStatus ()
	{
		int theTotalNotifiables = 0;
		int theEmptyReferences = 0;
		int theNSources = 0;
		int theNMessages = 0;

		List theRefList = new ArrayList ();

		for (Iterator theIterator = itsSourceMap.values ().iterator (); theIterator.hasNext ();)
		{
			List theList = (List) theIterator.next ();
			for (Iterator theIterator2 = theList.iterator (); theIterator2.hasNext ();)
			{
				NotifiableReference theReference = (NotifiableReference) theIterator2.next ();

				theTotalNotifiables++;
				if (theReference.getNotifiable () == null)
					theEmptyReferences++;
				else
					theRefList.add (theReference.getNotifiable ());
			}

			theNSources++;
		}

		for (Iterator theIterator = itsMessageClassMap.values ().iterator (); theIterator.hasNext ();)
		{
			List theList = (List) theIterator.next ();
			for (Iterator theIterator2 = theList.iterator (); theIterator2.hasNext ();)
			{
				NotifiableReference theReference = (NotifiableReference) theIterator2.next ();

				theTotalNotifiables++;
				if (theReference.getNotifiable () == null)
					theEmptyReferences++;
				else
					theRefList.add (theReference.getNotifiable ());
			}

			theNMessages++;
		}

		System.out.println ("NotificationCenter status");
		System.out.println (" * total entries: " + theTotalNotifiables);
		System.out.println (" * empty entries: " + theEmptyReferences);
		System.out.println (" * n. sources: " + theNSources);
		System.out.println (" * n. msg classes: " + theNMessages);


	}

	private void printRefStatus (Object aRef)
	{

	}

	/**
	 * Activates or deactivates the notification center.
	 * When deactivated, the notification center doesn't dispatch any message.
	 */
	public void setActive (boolean aActive)
	{
		itsActive = aActive;
	}

	public boolean isActive ()
	{
		return itsActive;
	}

	/**
	 * Removes all Notifiables from the notification center
	 */
	public void clear ()
	{
		itsSourceMap.clear ();
		itsMessageClassMap.clear ();
	}

	public void addNotifiableForSource (Notifiable aNotifiable, Object aSource)
	{
		purge ();
		NotifiableReference theReference = new NotifiableReference (aNotifiable);
		theReference.setList (itsSourceMap.add (aSource, theReference));
	}

	public void addNotifiableForMessageClass (Notifiable aNotifiable, Class aMessageClass)
	{
		purge ();
		NotifiableReference theReference = new NotifiableReference (aNotifiable);
		theReference.setList (itsMessageClassMap.add (aMessageClass, theReference));
	}

	public void removeNotifiableForSource (Notifiable aNotifiable, Object aSource)
	{
		purge ();
		itsSourceMap._remove (aSource, new NotifiableReference (aNotifiable));
	}

	public void removeNotifiableForMessageClass (Notifiable aNotifiable, Class aMessageClass)
	{
		purge ();
		itsMessageClassMap._remove (aMessageClass, new NotifiableReference (aNotifiable));
	}

	public void notify (Message aMessage)
	{
		purge ();

		if (! isActive()) return;

		// First we collect all the notifiables that will recieve the message, and after we send the message.
		// This is to avoid ConcurrentModificationException, as the notify() method of the
		// notifiables can add other Notifiables to the NotificationCenter
		Set theNotifiables = new HashSet ();

		boolean hasPrioritizedNotifiable = false; // We keep this in order to avoid sorting if not necessary.

		List theSources = aMessage.getSources ();

		for (Iterator theIterator = theSources.iterator (); theIterator.hasNext ();)
		{
			Object theSource = theIterator.next ();

			for (Iterator theIterator2 = itsSourceMap.iterator (theSource); theIterator2.hasNext ();)
			{
				NotifiableReference theNotifiableReference = (NotifiableReference) theIterator2.next ();
				Notifiable theNotifiable = theNotifiableReference.getNotifiable ();
				if (theNotifiable == null)
					theIterator2.remove ();
				else
				{
					if (theNotifiable instanceof PrioritizedNotifiable) hasPrioritizedNotifiable = true;
					theNotifiables.add (theNotifiable);
				}
			}
		}

		Class theMessageClass = aMessage.getClass ();
		while (theMessageClass != null)
		{
			for (Iterator theIterator = itsMessageClassMap.iterator (theMessageClass); theIterator.hasNext ();)
			{
				NotifiableReference theNotifiableReference = (NotifiableReference) theIterator.next ();
				Notifiable theNotifiable = theNotifiableReference.getNotifiable ();
				if (theNotifiable == null)
					theIterator.remove ();
				else
				{
					if (theNotifiable instanceof PrioritizedNotifiable) hasPrioritizedNotifiable = true;
					theNotifiables.add (theNotifiable);
				}
			}

			theMessageClass = theMessageClass.getSuperclass ();
		}

		List theNotifiablesList = new ArrayList (theNotifiables);
		if (hasPrioritizedNotifiable) Collections.sort (theNotifiablesList, NotifiableSorter.INSTANCE);

		for (Iterator theIterator = theNotifiablesList.iterator (); theIterator.hasNext ();)
		{
			Notifiable theNotifiable = (Notifiable) theIterator.next ();
			theNotifiable.processMessage (aMessage);
		}
	}

	private void purge ()
	{
		// Get rid of garbage collected references
		NotifiableReference theReference;
		while ((theReference = (NotifiableReference) itsReferenceQueue.poll ()) != null)
		{
			theReference.detach ();
		}
	}

	class NotifiableReference extends WeakReference
	{
		/**
		 * The list where this object is referenced, if any.
		 */
		private List itsList = null;

		public NotifiableReference (Notifiable aNotifiable)
		{
			super (aNotifiable, itsReferenceQueue);
		}

		public List getList ()
		{
			return itsList;
		}

		public void setList (List aList)
		{
			itsList = aList;
		}

		public boolean equals (Object obj)
		{
			if (obj == this) return true;
			if (obj instanceof NotifiableReference)
			{
				NotifiableReference theReference = (NotifiableReference) obj;
				Object theReferent = get ();
				if (theReferent == null)
					return false;
				else
					return theReferent.equals (theReference.get ());
			}
			else
				return false;
		}

		public Notifiable getNotifiable ()
		{
			return (Notifiable) get ();
		}

		/**
		 * Removes this object from its owning list if any.
		 */
		public void detach ()
		{
			if (itsList != null) itsList.remove (this);
		}
	}

	private static class NotifiableSorter implements Comparator
	{
		public static final NotifiableSorter INSTANCE = new NotifiableSorter();

		private NotifiableSorter ()
		{
		}

		public int compare (Object o1, Object o2)
		{
			return getPriority(o2) - getPriority(o1);
		}

		private int getPriority (Object o)
		{
			if (o instanceof PrioritizedNotifiable)
			{
				PrioritizedNotifiable thePrioritizedNotifiable = (PrioritizedNotifiable) o;
				return thePrioritizedNotifiable.getPriority();
			}
			else return 0;
		}
	}
}
