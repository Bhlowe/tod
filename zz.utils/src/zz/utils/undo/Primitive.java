/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 8, 2002
 * Time: 5:20:45 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.undo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Reification of an atomic operation. Primitives are grouped in {@link Command} objects.
 * <p>
 * The most important method of a primitive is {@link #perform()}, which executes an operation
 * according to the attributes of the instance. It could be for instance, to set the value of
 * an attribute of some object. The constructor of the primitive subclass would take as arguments
 * the object that is going to be setted and the new value of its attribute. The perform () method
 * would the call the appropiate setter on the given object, with the given value.
 * <p>
 * The next most important method is {@link #sendNotification() }, which as the name suggests,
 * sends, if needed by the framework, the appropiate notification. Whatever means of notification
 * is possible; however, it might be interesting to use the {@link zz.utils.notification.NotificationCenter}.
 * <p>
 * Less important are the methods {@link #prePerform() } and {@link #postPerform() }, which are called
 * before and after perform (). Note that when a command has various primitives, it first call all
 * the prePerform, then all the perform, then all the postPerform and finally all the sendNotification. 
 */
public abstract class Primitive implements AffectedObjectsHolder
{
	/**
	 * This list contains all the objects that are modified by the primitive
	 */
	private List itsAffectedObjects;

	protected void addAffectedObject (Object aObject)
	{
		if (itsAffectedObjects == null) itsAffectedObjects = new ArrayList ();
		checkAffectedObject(aObject);
		itsAffectedObjects.add (aObject);
	}

	protected void addAffectedObjects (Collection aObjects)
	{
		for (Iterator theIterator = aObjects.iterator (); theIterator.hasNext ();)
		{
			Object o = theIterator.next ();
			addAffectedObject(o);
		}
	}

	/**
	 * Returns a list of the objects that were modified by ths primitive.
	 * The list can be empty, or null.
	 */
	public Collection getAffectedObjects ()
	{
		return itsAffectedObjects;
	}

	/**
	 * This method is called when an object is added to the list of affected objects.
	 * An overriding method could throw an exception or an assert error.
	 */
	protected void checkAffectedObject (Object aObject)
	{
	}


	public void prePerform ()
	{
	}

	/**
	 * Executes this primitive.
	 */
	public void perform ()
	{
	}

	public void postPerform ()
	{
	}

	public void sendNotification ()
	{
	}

	/**
	 * This primitive performs nothing but adds an entity to the list of affected objects.
	 */
	public static class Noop extends Primitive
	{
		public Noop (Object aObject)
		{
			addAffectedObject (aObject);
		}
	}

}
