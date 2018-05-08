/*
 * Created on Dec 16, 2004
 */
package zz.utils.tree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import zz.utils.references.HardRef;
import zz.utils.references.IRef;
import zz.utils.references.RefUtils;
import zz.utils.references.WeakRef;

/**
 * Abstract implementation of {@link zz.utils.tree.ITree}, taking
 * care of listeners and the {@link #getPath} method.
 * It also implements some methods either by throwing an exception, or 
 * by providing a sensible default behaviour.
 * @author gpothier
 */
public abstract class AbstractTree<N, V> implements ITree<N, V>
{
	private List<IRef<ITreeListener<N, V>>> itsListeners = 
		new ArrayList<IRef<ITreeListener<N, V>>>();
	
	public void addListener (ITreeListener<N, V> aListener)
	{
		itsListeners.add (new WeakRef<ITreeListener<N, V>>(aListener));
	}

	public void addHardListener (ITreeListener<N, V> aListener)
	{
		itsListeners.add (new HardRef<ITreeListener<N, V>>(aListener));
	}
	
	public void removeListener (ITreeListener<N, V> aListener)
	{
		RefUtils.remove(itsListeners, aListener);
	}

	/**
	 * Sends a child added message to all listeners.
	 * @param aNode The parent node to which a child was added.
	 * @param aIndex The index of the new child
	 * @param aChild The newly added child
	 */
	public void fireChildAdded (N aNode, int aIndex, N aChild)
	{
		List<ITreeListener<N, V>> theListeners = RefUtils.dereference(itsListeners);
		for (ITreeListener<N, V> theListener : theListeners)
			theListener.childAdded(this, aNode, aIndex, aChild);
	}
	
	/**
	 * Sends a child removed message to all listeners.
	 * @param aNode The parent node from which a child was removed.
	 * @param aIndex The index previously occupied by the child
	 * @param aChild The removed child
	 */
	public void fireChildRemoved (N aNode, int aIndex, N aChild)
	{
		List<ITreeListener<N, V>> theListeners = RefUtils.dereference(itsListeners);
		for (ITreeListener<N, V> theListener : theListeners)
			theListener.childRemoved(this, aNode, aIndex, aChild);
	}
	
	/**
	 * Sends a value changed message to all listeners
	 * @param aNode The node whose value changed
	 * @param aNewValue The new value of the node.
	 */
	public void fireValueChanged (N aNode, V aNewValue)
	{
		List<ITreeListener<N, V>> theListeners = RefUtils.dereference(itsListeners);
		for (ITreeListener<N, V> theListener : theListeners)
			theListener.valueChanged(this, aNode, aNewValue);
	}

    /**
     * Computes the tree path corresponding to a given node.
     */
    public List<N> getPath (N aNode)
    {
    	LinkedList<N> thePath = new LinkedList<N> ();
    	while (aNode != null)
    	{
    		thePath.addFirst(aNode);
    		aNode = getParent(aNode);
    	}
    	return thePath;
    }
    
    /**
     * Throws {@link UnsupportedOperationException}
     */
	public N createNode(V aValue)
	{
		throw new UnsupportedOperationException();
	}
	
    /**
     * Throws {@link UnsupportedOperationException}
     */
	public void addChild(N aParent, int aIndex, N aChild)
	{
		throw new UnsupportedOperationException();
	}
	
    /**
     * Throws {@link UnsupportedOperationException}
     */
	public void removeChild(N aParent, int aIndex)
	{
		throw new UnsupportedOperationException();
	}
	
	public void removeNode(N aNode)
	{
		N theParent = getParent(aNode);
		if (theParent == null) throw new IllegalArgumentException("Trying to remove root node");
		int theIndex = getIndexOfChild(theParent, aNode);
		removeChild(theParent, theIndex);
	}
	
	/**
	 * Returns true iff the node has no child.
	 */
	public boolean isLeaf(N aNode)
	{
		return getChildCount(aNode) == 0;
	}
	
	public Iterable<N> getChildren(N aParent)
	{
		return new DefaultChildrenIterator<N>(this, aParent);
	}
	
    /**
     * Throws {@link UnsupportedOperationException}
     */
	public V setValue(N aNode, V aValue)
	{
		throw new UnsupportedOperationException();
	}
}
