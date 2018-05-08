/*
 * Created on Apr 12, 2005
 */
package zz.utils.tree;

/**
 * Visitor of an {@link zz.utils.tree.ITree}.
 * See {@link zz.utils.tree.TreeUtils#visit(ITree, ITreeVisitor)}
 * @author gpothier
 */
public interface ITreeVisitor<N, V>
{
	/**
	 * This method is called for each tree node.
	 * @param aNode The visited node
	 * @param aValue The visited node's value
	 */
	public void visit (N aNode, V aValue);
}
