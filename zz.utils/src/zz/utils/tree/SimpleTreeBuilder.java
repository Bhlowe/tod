/*
 * Created on Dec 22, 2004
 */
package zz.utils.tree;

/**
 * Helper class using varargs methods to build a {@link zz.utils.tree.SimpleTree}
 * @author gpothier
 */
public class SimpleTreeBuilder<V>
{
	private SimpleTree<V> itsTree = new SimpleTree<V>();
	
	public SimpleTree<V> getTree()
	{
		return itsTree;
	}
	
	/**
	 * Sets the children of the root node.
	 */
	public void root(SimpleTreeNode<V>... aNodes)
	{
		root(null, aNodes);
	}

	/**
	 * Sets the value and the children of the root node.
	 */
	public void root(V aValue, SimpleTreeNode<V>... aNodes)
	{
		itsTree.setValue(itsTree.getRoot(), aValue);
		for (SimpleTreeNode<V> theNode : aNodes) itsTree.getRoot().pChildren().add (theNode);
	}
	
	/**
	 * Creates a leaf node
	 */
	public SimpleTreeNode<V> leaf(V aValue)
	{
		return itsTree.createLeafNode(aValue);
	}

	/**
	 * Creates a non-leaf node.
	 */
	public SimpleTreeNode<V> node(V aValue, SimpleTreeNode<V>... aChildren)
	{
		SimpleTreeNode<V> theNode = itsTree.createNode(aValue);
		for (SimpleTreeNode<V> theChild : aChildren) theNode.pChildren().add (theChild);
		return theNode;
	}
	
	public void add(SimpleTreeNode<V> aParent, SimpleTreeNode<V>... aChildren)
	{
		for (SimpleTreeNode<V> theChild : aChildren) aParent.pChildren().add (theChild);
	}
}
