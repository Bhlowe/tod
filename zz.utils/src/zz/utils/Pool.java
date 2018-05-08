/*
 * Created on Apr 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils;


/**
 * Manages a pool of objects:
 * {@link #request()} returns a pooled object that is available to use;
 * when the object isn't needed any more, call {@link #release(T)}.
 * <p>
 * The typical use is to create an anonymous inner class that implements the 
 * {@link #create()} mehtod:
 * <pre>
 * 	private static final Pool ARGUMENT_CONTROLLER_POOL = new Pool ()
 *	{
 *		protected Object create()
 *		{
 *			return new ArgumentEditorController ();
 *		}
 *	};
 * </pre>
 * @author gpothier
 */
public abstract class Pool<T>
{
	private Stack<T> itsObjects = new ArrayStack<T>();
	
	/**
	 * Returns a pool object, creating a new one if necessary.
	 * Once the object is not needed any more, call
	 * {@link #release(Object)}.
	 */
	public T request ()
	{
		if (itsObjects.isEmpty()) return create ();
		else return itsObjects.pop ();
	}
	
	/**
	 * Notifies this pool that the specified object is not used any more.
	 * @param aObject A pool object, which should (must...) have been
	 * obtained by the {@link #request()} method.
	 */
	public void release (T aObject)
	{
		assert aObject != null;
		itsObjects.push (aObject);
	}
	
	/**
	 * Creates a new pooled object.
	 */
	protected abstract T create ();
}
