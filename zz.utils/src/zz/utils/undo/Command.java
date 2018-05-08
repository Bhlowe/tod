/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 8, 2002
 * Time: 5:16:49 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.undo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zz.utils.Empty;
import zz.utils.ReverseIteratorWrapper;
import zz.utils.notification.NotificationCenter;

/**
 * This class implements the basic behaviour for undo mechanism.
 * The command class contains two groups of primitives.
 * the 'perform' primitives perform an action while the 'undo' primitives undo this action.
 * @see Primitive
 * @see UndoEnvironment
 */
public class Command implements AffectedObjectsHolder
{
	/**
	 * The name of the command
	 */
	private String itsName;

	/**
	 * The sequence of primitives to execute to perform the command
	 */
	private List itsPerformPrimitives = new ArrayList ();

	/**
	 * The sequence of primitives to execute to undo the command
	 */
	private List itsUndoPrimitives = new ArrayList ();

	/**
	 * Indicates whether this command should execute its primitives while they are added.
	 * Warning: this will only work for primitives added through {@link #addPerformPrimitive}
	 * {@link #addPrimitives} and {@link #concatenate}
	 * One should call {@link #finish} when the command is terminated.
	 */
	private boolean itsAutoExecuting = false;

	private boolean itsFinished = false;

	private List itsChainedCommands;

	public Command (String aName)
	{
		itsName = aName;
	}

	/**
	 * Puts this command into auto-executing mode. This should end by a call to {@link #finish}.
	 * This will deactivate the notification center.
	 */
	public void setAutoExecuting ()
	{
		itsAutoExecuting = true;
		NotificationCenter.getDefaultNotificationCenter().setActive(false);
	}

	public boolean isAutoExecuting ()
	{
		return itsAutoExecuting;
	}



	/**
	 * Undo this command and reactivates the notification center.
	 * @see #itsAutoExecuting
	 * @see #setAutoExecuting
	 */
	public void finish ()
	{
		if (! isAutoExecuting()) throw new IllegalStateException ("Not an auto-executing command");

		undo ();
		NotificationCenter.getDefaultNotificationCenter().setActive(true);
		itsFinished = true;
	}

	public boolean isEmpty ()
	{
		return itsPerformPrimitives.size() == 0 && itsUndoPrimitives.size() == 0 && (itsChainedCommands == null || itsChainedCommands.size() == 0);
	}

	public List getPerformPrimitives ()
	{
		return itsPerformPrimitives;
	}

	public List getUndoPrimitives ()
	{
		return itsUndoPrimitives;
	}

	public void setPerformPrimitives (Primitive[] aPerformPrimitives)
	{
		if (isAutoExecuting()) throw new IllegalStateException ("Not supported");
		setPerformPrimitives(Arrays.asList(aPerformPrimitives));
	}

	public void setPerformPrimitives (List aPerformPrimitivesList)
	{
		if (isAutoExecuting()) throw new IllegalStateException ("Not supported");
		itsPerformPrimitives = aPerformPrimitivesList;
	}

	public void setUndoPrimitives (Primitive[] aUndoPrimitives)
	{
		if (isAutoExecuting()) throw new IllegalStateException ("Not supported");
		setUndoPrimitives(Arrays.asList(aUndoPrimitives));
	}

	/**
	 * Facility method that converts the list to an appropriate array
	 */
	public void setUndoPrimitives (List anUndoPrimitivesList)
	{
		if (isAutoExecuting()) throw new IllegalStateException ("Not supported");
		itsUndoPrimitives = anUndoPrimitivesList;
	}

	public void perform ()
	{
		perform (PrimitivePrePerformer.PRE_PERFORMER);
		perform (PrimitivePerformer.DEFAULT_PERFORMER);
		perform (PrimitivePostPerformer.POST_PERFORMER);
		perform (PrimitiveNotifier.NOTIFIER);
	}


	protected final void perform (PrimitivePerformer aPerformer)
	{
		for (Iterator theIterator = itsPerformPrimitives.iterator (); theIterator.hasNext ();)
		{
			Primitive thePrimitive = (Primitive) theIterator.next ();
			aPerformer.perform(thePrimitive);
		}

		if (itsChainedCommands != null) for (Iterator theIterator = itsChainedCommands.iterator (); theIterator.hasNext ();)
		{
			Command theCommand = (Command) theIterator.next ();
			theCommand.perform(aPerformer);
		}
	}

	public void undo ()
	{
		undo (PrimitivePrePerformer.PRE_PERFORMER);
		undo (PrimitivePerformer.DEFAULT_PERFORMER);
		undo (PrimitivePostPerformer.POST_PERFORMER);
		undo (PrimitiveNotifier.NOTIFIER);
	}

	protected final void undo (PrimitivePerformer aPerformer)
	{
		if (itsChainedCommands != null) for (Iterator theIterator = new ReverseIteratorWrapper(itsChainedCommands); theIterator.hasNext ();)
		{
			Command theCommand = (Command) theIterator.next ();
			theCommand.undo(aPerformer);
		}

		for (Iterator theIterator = new ReverseIteratorWrapper (itsUndoPrimitives); theIterator.hasNext ();)
		{
			Primitive thePrimitive = (Primitive) theIterator.next ();
			aPerformer.perform(thePrimitive);
		}
	}

	public String getName ()
	{
		return itsName;
	}

	public void setName (String aName)
	{
		itsName = aName;
	}

	public String toString ()
	{
		StringBuffer theBuffer = new StringBuffer();

		if (itsChainedCommands != null) for (Iterator theIterator = itsChainedCommands.iterator (); theIterator.hasNext ();)
		{
			Command theCommand = (Command) theIterator.next ();
			theBuffer.append(theCommand+" ");
		}
		return itsName + "["+theBuffer+"]";
	}

	
	public void addPerformPrimitive (Primitive aPrimitive)
	{
		addPerformPrimitive(aPrimitive, PrimitivePerformer.DEFAULT_PERFORMER);
	}

	protected final void addPerformPrimitive (Primitive aPrimitive, PrimitivePerformer aPerformer)
	{
		if (isAutoExecuting() && itsFinished) throw new IllegalStateException ("Not supported");
		if (isAutoExecuting()) aPerformer.perform(aPrimitive);
		itsPerformPrimitives.add (aPrimitive);
	}

	public void addUndoPrimitive (Primitive aPrimitive)
	{
		if (isAutoExecuting() && itsFinished) throw new IllegalStateException ("Not supported");
		itsUndoPrimitives.add (aPrimitive);
	}

	public void addPrimitives (Primitive aPerformPrimitive, Primitive aUndoPrimitive)
	{
		addPerformPrimitive(aPerformPrimitive);
		addUndoPrimitive(aUndoPrimitive);
	}

	/**
	 * Returns all the objects affected by this command, as determined calling
	 * {@link Primitive#getAffectedObjects() }. The objects in the returned collection
	 * are never duplicated.
	 * @return A collection of affected objects, or null if there is none.
	 */
	public Collection getAffectedObjects ()
	{
		Set theSet = null;
		theSet = collectAffectedObjects(theSet, itsPerformPrimitives.iterator());
		theSet = collectAffectedObjects(theSet, itsUndoPrimitives.iterator());
		theSet = collectAffectedObjects(theSet, getChainedCommandsIterator());
		return theSet;
	}

	private static Set collectAffectedObjects (Set aSet, Iterator aAffectedObjectsHolderIterator)
	{
		for (Iterator theIterator = aAffectedObjectsHolderIterator; theIterator.hasNext ();)
		{
			AffectedObjectsHolder theHolder = (AffectedObjectsHolder) theIterator.next ();
			Collection theAffectedObjects = theHolder.getAffectedObjects();
			if (theAffectedObjects != null)
			{
				if (aSet == null) aSet = new HashSet ();
				aSet.addAll(theAffectedObjects);
			}
		}
		return aSet;
	}

	/**
	 * Concatenates the specified command with this command.<p>
	 * Returns this command.<p>
	 * @param aCommand The command to concatenate. If null, nothing happens.
	 */
	public void concatenate (Command aCommand)
	{
		if (isAutoExecuting() && itsFinished) throw new IllegalStateException ("Not supported");
		if (aCommand == null) return;
		if (aCommand.isAutoExecuting() && ! aCommand.itsFinished) throw new IllegalStateException ("Not supported");

		if (itsChainedCommands == null) itsChainedCommands = new ArrayList ();
		itsChainedCommands.add (aCommand);
//		List theNewPerformPrimitives = new ArrayList (itsPerformPrimitives);
//		theNewPerformPrimitives.addAll (aCommand.itsPerformPrimitives);
//		itsPerformPrimitives = theNewPerformPrimitives;
//
//		List theNewUndoPrimitives = new ArrayList (itsUndoPrimitives);
//		theNewUndoPrimitives.addAll(aCommand.itsUndoPrimitives);
//		itsUndoPrimitives = theNewUndoPrimitives;

		if (isAutoExecuting() && ! aCommand.isAutoExecuting()) aCommand.perform();
	}

	public Iterator getChainedCommandsIterator ()
	{
		if (itsChainedCommands == null) return Empty.ITERATOR;
		else return itsChainedCommands.iterator();
	}

	public static class PrimitivePerformer
	{
		public static final PrimitivePerformer DEFAULT_PERFORMER = new PrimitivePerformer();

		protected PrimitivePerformer ()
		{
		}

		public void perform (Primitive aPrimitive)
		{
			aPrimitive.perform();
		}
	}

	private static class PrimitivePrePerformer extends PrimitivePerformer
	{
		public static final PrimitivePerformer PRE_PERFORMER = new PrimitivePrePerformer();

		private PrimitivePrePerformer ()
		{
		}

		public void perform (Primitive aPrimitive)
		{
			aPrimitive.prePerform();
		}
	}

	private static class PrimitivePostPerformer extends PrimitivePerformer
	{
		public static final PrimitivePerformer POST_PERFORMER = new PrimitivePostPerformer();

		private PrimitivePostPerformer ()
		{
		}

		public void perform (Primitive aPrimitive)
		{
			aPrimitive.postPerform();
		}
	}

	private static class PrimitiveNotifier extends PrimitivePerformer
	{
		public static final PrimitivePerformer NOTIFIER = new PrimitiveNotifier();

		private PrimitiveNotifier ()
		{
		}

		public void perform (Primitive aPrimitive)
		{
			aPrimitive.sendNotification();
		}
	}


}
