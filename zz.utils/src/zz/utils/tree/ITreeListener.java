/*
 * Created on 10-jun-2004
 */
package zz.utils.tree;


/**
 * A listener that is notified of modifications of a {@link ITree}
 * @author gpothier
 */
public interface ITreeListener<N, V>
{
	/**
	 * This method is called whenever a child is added to a node.
	 */
	public void childAdded (ITree<N, V> aTree, N aNode, int aIndex, N aChild);
	
	/**
	 * This method is called whenever a child is removed from a node
	 */
	public void childRemoved (ITree<N, V> aTree, N aNode, int aIndex, N aChild);
	
	/**
	 * Called whenever a node's value changes.
	 */
	public void valueChanged (ITree<N, V> aTree, N aNode, V aNewValue);
}
