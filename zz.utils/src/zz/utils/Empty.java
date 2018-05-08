/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: 21 f�vr. 02
 * Time: 15:10:18
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This class contains an empty iterator and an empty iterable
 */
public class Empty 
{

	public static final Iterator ITERATOR = new Iterator()
	{
		public boolean hasNext ()
		{
			return false;
		}
		
		public Object next ()
		{
			throw new NoSuchElementException ();
		}
		
		public void remove ()
		{
			throw new UnsupportedOperationException ();
		}
	};
	
	public static final Iterable ITERABLE = new Iterable()
	{
		public Iterator iterator()
		{
			return ITERATOR;
		}
		
	};
}
