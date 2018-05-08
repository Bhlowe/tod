/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: 21 mars 02
 * Time: 15:33:57
 * To change template for new interface use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.Iterator;

/**
 * A stack onto which elements can be pushed, popped or peeked.
 * @author gpothier
 */
public interface Stack<T>
{
	/**
	 * Pushes an element at the top of the stack (highest index).
	 */
	public void push (T aObject);
	
	/**
	 * Pushes all the elements provided by the iterable.
	 */
	public void pushAll(Iterable<T> aSource);
	
	/**
	 * Retrieves the element at the top of the stack and removes it.
	 * If the stack is empty throws an exception.
	 */
	public T pop ();
	
	/**
	 * Retrieves the element at the top of the stack without removing it.
	 * If the stack is empty returns null.
	 */
	public T peek ();
	
	/**
	 * Retuns the element at the given index withou removing it.
	 */
	public T peek(int aIndex);
	
	/**
	 * Removes all the elements of the stack
	 */
	public void clear ();
	
	/**
	 * Returns the number of elements of the stack.
	 */
	public int size ();
	
	/**
	 * Indicates if this stack is empty.
	 */
	public boolean isEmpty ();
	
	/**
	 * Returns an iterator over the elements of this stack, starting from the bottom. 
	 */
	public Iterator<T> iterator ();
}
