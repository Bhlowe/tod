/*
 * Created on Dec 21, 2004
 */
package zz.utils.tree;


/**
 * Simple implementation of {@link zz.utils.tree.ITree}, based
 * on {@link zz.utils.tree.SimpleTreeNode}.
 * @author gpothier
 */
public class SimpleTree<V> extends AbstractTree<SimpleTreeNode<V>, V>
{
	private final SimpleTreeNode<V> itsRootNode;

	public SimpleTree()
	{
		itsRootNode = createRoot();
	}
	
	public SimpleTree(SimpleTreeNode<V> aRootNode)
	{
		itsRootNode = aRootNode;
	}

	public SimpleTree(V aRootNodeValue)
	{
		itsRootNode = createNode(aRootNodeValue);
	}

	public SimpleTreeNode<V> getRoot()
	{
		return itsRootNode;
	}

	public SimpleTreeNode<V> getParent(SimpleTreeNode<V> aNode)
	{
		return aNode.getParent();
	}

	public int getChildCount(SimpleTreeNode<V> aParent)
	{
		return aParent.pChildren() != null ? aParent.pChildren().size() : 0;
	}
	
	public boolean isLeaf(SimpleTreeNode<V> aNode)
	{
		return aNode.pChildren() == null;
	}

	public SimpleTreeNode<V> getChild(SimpleTreeNode<V> aParent, int aIndex)
	{
		return aParent.pChildren().get(aIndex);
	}
	
	public Iterable<SimpleTreeNode<V>> getChildren(SimpleTreeNode<V> aParent)
	{
		return aParent.pChildren();
	}

	public int getIndexOfChild(SimpleTreeNode<V> aParent, SimpleTreeNode<V> aChild)
	{
		return aParent.pChildren().indexOf(aChild);
	}

	public V getValue(SimpleTreeNode<V> aNode)
	{
		return aNode.pValue().get();
	}
	
	public V setValue(SimpleTreeNode<V> aNode, V aValue)
	{
		V thePreviousValue = aNode.pValue().get();
		aNode.pValue().set(aValue);
		return thePreviousValue;
	}
	
	/**
	 * Creates the root node of the tree. Used only if no root node was
	 * passed in the constructor.
	 * @return By defaut, a simple node with a null value.
	 */
	protected SimpleTreeNode<V> createRoot()
	{
		return  createNode(null);
	}

	/**
	 * Creates a new node for this tree, without adding it to the tree.
	 */
	public SimpleTreeNode<V> createNode(V aValue)
	{
		SimpleTreeNode<V> theNode = new SimpleTreeNode<V>(this, false);
		theNode.pValue().set(aValue);
		return theNode;
	}
	
	/**
	 * Creates a new leaf node for this tree, without adding it to the tree.
	 */
	public SimpleTreeNode<V> createLeafNode(V aValue)
	{
		SimpleTreeNode<V> theNode = new SimpleTreeNode<V>(this, true);
		theNode.pValue().set(aValue);
		return theNode;
	}
	
	/**
	 * Adds a child node to a node.
	 */
	public void addChild(SimpleTreeNode<V> aParent, int aIndex, SimpleTreeNode<V> aChild)
	{
		aParent.pChildren().add(aIndex, aChild);
	}

	/**
	 * Removes a child node from a node.
	 */
	public void removeChild(SimpleTreeNode<V> aParent, int aIndex)
	{
		aParent.pChildren().remove(aIndex);
	}
	
}
