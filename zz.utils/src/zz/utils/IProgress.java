/*
 * Created on Oct 28, 2005
 */
package zz.utils;

/**
 * A generic interface for progress monitoring
 * @author gpothier
 */
public interface IProgress
{
	/**
	 * Called to indicates what is the total amounts of steps to perform
	 */
	public void setTotal(int aTotal);
	
	/**
	 * Called to indicate that a step was performed 
	 */
	public void inc();
	
	/**
	 * The worker can query this method to check if work should be cancelled.
	 */
	public boolean isCancelled();
}
