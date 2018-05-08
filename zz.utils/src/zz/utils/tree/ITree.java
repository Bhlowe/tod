/*
 * Created on Dec 16, 2004
 */
package zz.utils.tree;

import java.util.List;

/**
 * Abstract model of a tree.
 * It lets implementations completely free of how to design the tree structure
 * @param N The type for nodes.
 * @param V The type for node values
 * @author gpothier
 */
public interface ITree<N, V>
{
	/**
	 * Returns the root node of the tree.
	 */
	public N getRoot();
	
	/**
	 * Returns the path of the given node
	 */
	public List<N> getPath (N aNode);
	
	/**
	 * Returns the parent of the given node.
	 */
	public N getParent (N aNode);

	/**
	 * Returns the number of children in a given node.
	 */
	public int getChildCount(N aParent);

	/**
	 * Whether the specified node is a leaf node.
	 */
	public boolean isLeaf(N aNode);
	
	/**
	 * Returns an iterable of the specified node's children.
	 * @return An iterable. If the node is a leaf node, this method should return null.
	 */
	public Iterable<N> getChildren (N aParent);

	/**
	 * Returns the Nth child of a node.
	 */
	public N getChild(N aParent, int aIndex);
	
	/**
	 * Adds a new child to a given node.
	 */
	public void addChild (N aParent, int aIndex, N aChild);
	
	/**
	 * Removes child from a node.
	 */
	public void removeChild (N aParent, int aIndex);
	
	/**
	 * Removes the given node from the tree.
	 */
	public void removeNode(N aNode);

	/**
	 * Returns the index of a child within a node, or -1 if the child
	 * doesn't belong to the node.
	 */
	public int getIndexOfChild(N aParent, N aChild);

	/**
	 * Returns the value associated with the node.
	 */
	public V getValue (N aNode);
	
	/**
	 * Sets the value of the given node.
	 * @return The previous value of the node.
	 */
	public V setValue (N aNode, V aValue);
	
	/**
	 * Creates a new node for this tree, with the specified value.
	 */
	public N createNode (V aValue);
	
	/**
	 * Adds a listener that will be notified each time 
	 * the structure of this tree changes.
	 * The property will maintains a weak reference to the listener,
	 * so the programmer should ensure that the listener is strongly
	 * referenced somewhere.
	 * In particular, this kind of construct should not be used:
	 * <pre>
	 * prop.addListener (new MyListener());
	 * </pre>
	 * In this case, use {@link #addHardListener(IListPropertyListener)}
	 * instead.
	 */
	public void addListener (ITreeListener<N, V> aListener);

	/**
	 * Adds a listener that will be notified each time 
	 * the structure of this tree changes.
	 * The listener will be referenced through a strong reference.
	 * @see #addListener(IPropertyListener)
	 */
	public void addHardListener (ITreeListener<N, V> aListener);
	
	/**
	 * Removes a previously added listener.
	 */
	public void removeListener (ITreeListener<N, V> aListener);


}
