/*
 * Created on Nov 27, 2008
 */
package zz.utils;


/**
 * Keeps the best of a set of scored elements
 * @author gpothier
 */
public abstract class Sieve<T>
{
	private T itsBest = null;
	private long itsBestScore = 0;
	
	public void add(T aElement)
	{
		long theScore = getScore(aElement);
		if (theScore > itsBestScore)
		{
			itsBest = aElement;
			itsBestScore = theScore;
		}
	}
	
	protected abstract long getScore(T aElement);
	
	public T getBest()
	{
		return itsBest;
	}
}
