/*
 * Created on Mar 25, 2005
 */
package zz.utils.tree;

import java.util.List;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 * A swing tree model that wraps an {@link ITree}
 * 
 * @author gpothier
 */
public class SwingTreeModel<N, V> extends DefaultTreeModel implements ITreeListener<N, V>
{
	private ITree<N, V> itsTree;

	public SwingTreeModel()
	{
		this (null);
	}
	
	public SwingTreeModel(ITree<N, V> aTree)
	{
		super(null);
		setTree(aTree);
	}
	
	public ITree<N, V> getTree()
	{
		return itsTree;
	}

	public void setTree(ITree<N, V> aTree)
	{
		if (itsTree != null) itsTree.removeListener(this);
		itsTree = aTree;
		if (itsTree != null) itsTree.addListener(this);
	}

	public void fireTreeStructureChanged()
	{
		TreePath thePath = getTreePath(itsTree.getRoot());
		fireTreeStructureChanged(this, thePath.getPath(), null, null);
	}	

	public void childAdded(ITree<N, V> aTree, N aNode, int aIndex, N aChild)
	{
		fireTreeStructureChanged();
	}

	public void childRemoved(ITree<N, V> aTree, N aNode, int aIndex, N aChild)
	{
		fireTreeStructureChanged();
	}

	public void valueChanged(ITree<N, V> aTree, N aNode, V aNewValue)
	{
		fireTreeStructureChanged();
	}

	public Object getRoot()
	{
		return itsTree != null ? itsTree.getRoot() : null;
	}

	public int getChildCount(Object aParent)
	{
		return itsTree.getChildCount((N) aParent);
	}

	public boolean isLeaf(Object aNode)
	{
		return itsTree.isLeaf((N) aNode);
	}

	public Object getChild(Object aParent, int aIndex)
	{
		return itsTree.getChild((N) aParent, aIndex);
	}

	public int getIndexOfChild(Object aParent, Object aChild)
	{
		return itsTree.getIndexOfChild((N) aParent, (N) aChild);
	}

	/**
	 * Computes the tree path corresponding to a given node.
	 */
	public TreePath getTreePath(N aNode)
	{
		List<N> thePath = itsTree.getPath(aNode);
		return new TreePath(thePath.toArray());
	}
	
	/**
	 * Tries to find the tree path that corresponds to the node
	 * that has the specified value
	 */
	public TreePath getTreePathForValue (V aValue)
	{
		N theNode = TreeUtils.findNodeByValue(itsTree, aValue);
		return theNode != null ? getTreePath(theNode) : null;
	}

	/**
	 * Returns the value associated with the given path
	 */
	public V getValue(TreePath aPath)
	{
		N theNode = (N) aPath.getLastPathComponent();
		return itsTree.getValue(theNode);
	}
}
