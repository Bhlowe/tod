/*
 * Created on Mar 29, 2005
 */
package zz.utils;

import zz.utils.properties.IRWProperty;
import zz.utils.properties.SimpleRWProperty;

/**
 * This class models a progress meter, with a current value and a final, or total value.
 * @author gpothier
 */
public class ProgressModel<T>
{
	public final IRWProperty<T> pCurrent = new SimpleRWProperty<T>(); 
	public final IRWProperty<T> pTotal = new SimpleRWProperty<T>();
	
	public ProgressModel ()
	{
	}
	
	public ProgressModel (T aCurrent, T aTotal)
	{
		pCurrent.set(aCurrent);
		pTotal.set(aTotal);
	}
}
