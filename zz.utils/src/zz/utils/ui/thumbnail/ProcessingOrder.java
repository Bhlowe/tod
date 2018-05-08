package zz.utils.ui.thumbnail;

/**
 * Defines the different prioity schemes for processing requests.
 * @author gpothier
 */
public enum ProcessingOrder 
{
	/**
	 * Last in, first out: the most recent requests are processed first.
	 */
	LIFO,
	
	/**
	 * First in, first out: the oldest requests are processed first
	 */
	FIFO
}