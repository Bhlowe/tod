/*
 * Created on Jul 13, 2005
 */
package zz.utils.tree;

/**
 * A simple implementation of tree in which all nodes
 * implement {@link ITreeNode}
 * @author gpothier
 */
public class Tree<V> extends AbstractTree<ITreeNode<V>, V>
{
	private ITreeNode<V> itsRoot;
	
	public Tree(ITreeNode<V> aRoot)
	{
		itsRoot = aRoot;
	}

	public ITreeNode<V> getChild(ITreeNode<V> aParent, int aIndex)
	{
		return aParent.getChild(aIndex);
	}

	public int getChildCount(ITreeNode<V> aParent)
	{
		return aParent.getChildCount();
	}

	public int getIndexOfChild(ITreeNode<V> aParent, ITreeNode<V> aChild)
	{
		return aParent.getIndexOfChild(aChild);
	}

	public ITreeNode<V> getParent(ITreeNode<V> aNode)
	{
		return aNode.getParent();
	}

	public ITreeNode<V> getRoot()
	{
		return itsRoot;
	}

	public V getValue(ITreeNode<V> aNode)
	{
		return aNode.getValue();
	}

	@Override
	public boolean isLeaf(ITreeNode<V> aNode)
	{
		return aNode.isLeaf();
	}
}
