/*
 * Created on Jul 13, 2005
 */
package zz.utils.tree;

/**
 * An interface that represents a tree node for {@link Tree}
 * @author gpothier
 */
public interface ITreeNode<V>
{
	/**
	 * @see ITree#isLeaf(N)
	 */
	public boolean isLeaf();
	
	/**
	 * @see ITree#getChild(N, int)
	 */
	public ITreeNode<V> getChild(int aIndex);
	
	/**
	 * @see ITree#getChildCount(N)
	 */
	public int getChildCount();
	
	/**
	 * @see ITree#getIndexOfChild(N, N)
	 */
	public int getIndexOfChild(ITreeNode<V> aChild);
	
	/**
	 * @see ITree#getParent(N)
	 */
	public ITreeNode<V> getParent();

	/**
	 * Returns the value associated with this node.
	 */
	public V getValue();
}
