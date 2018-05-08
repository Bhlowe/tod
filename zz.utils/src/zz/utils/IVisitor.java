/*
 * Created on Oct 26, 2005
 */
package zz.utils;

/**
 * Abstract definition of a simple visitor
 * @author gpothier
 */
public interface IVisitor<T>
{
	/**
	 * Visit method
	 */
	public void visit(T aValue);
}
