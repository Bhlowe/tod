/*
 * Created on Oct 26, 2005
 */
package zz.utils;

/**
 * Abstract definition of a filter
 * @author gpothier
 */
public interface IFilter<T>
{
	/**
	 * This method defines the filter: it indicates whether a given element
	 * is accepted or not by the filter.
	 */
	public boolean accept (T aValue);
}
