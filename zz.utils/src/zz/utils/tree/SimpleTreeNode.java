/*
 * Created on Dec 21, 2004
 */
package zz.utils.tree;

import zz.utils.properties.ArrayListProperty;
import zz.utils.properties.IListProperty;
import zz.utils.properties.IRWProperty;
import zz.utils.properties.SimpleRWProperty;

/**
 * Simple tree node implementation.
 * Instances are obtained through {@link zz.utils.tree.SimpleTree#createNode(V)}
 * @author gpothier
 */
public class SimpleTreeNode<V>
{
	private SimpleTree<V> itsTree;
	private SimpleTreeNode<V> itsParent;
	
	/**
	 * The children of this node. If this property is null, there are no children.
	 */
	private final IListProperty<SimpleTreeNode<V>> pChildren;
	
	private boolean itsInitialized = false;	
	
	private final IRWProperty<V> pValue = new SimpleRWProperty<V>()
		{
			@Override
			protected void changed(V aOldValue, V aNewValue)
			{
				//TODO: cast for compilation problem
				if (itsInitialized)
				{
					// Do not send notifications while we are initializing.
					// Otherwise this causes problems in SwingTreeModel
					getTree().fireValueChanged((SimpleTreeNode<V>) SimpleTreeNode.this, aNewValue);
				}
			}
		};
	
	
	protected SimpleTreeNode(SimpleTree<V> aTree, boolean aLeaf)
	{
		itsTree = aTree;
		if (! aLeaf)
		{
			pChildren = 
				new ArrayListProperty<SimpleTreeNode<V>>()
				{
					protected void elementAdded(int aIndex, SimpleTreeNode<V> aElement)
					{
						if (aElement.getParent() != null) throw new RuntimeException("node already has a parent: "+aElement);
						//TODO: cast for compilation problem 
						aElement.setParent((SimpleTreeNode<V>) SimpleTreeNode.this);
						//TODO: cast for compilation problem 
						if (itsInitialized) getTree().fireChildAdded((SimpleTreeNode<V>) SimpleTreeNode.this, aIndex, aElement);
					}
					
					protected void elementRemoved(int aIndex, SimpleTreeNode<V> aElement)
					{
						if (aElement.getParent() != SimpleTreeNode.this) throw new RuntimeException("node has awrong parent: "+aElement);
						aElement.setParent(null);
						//TODO: cast for compilation problem 
						if (itsInitialized) getTree().fireChildRemoved((SimpleTreeNode<V>) SimpleTreeNode.this, aIndex, aElement);
					}
					
					@Override
					protected void init()
					{
						SimpleTreeNode.this.init();
						itsInitialized = true;
					}
				};
		}
		else pChildren = null;
	}

	/**
	 * Subclasses can override this method if they want to perform lazy initialization
	 * of children.
	 */
	protected void init()
	{
	}
	
	public SimpleTree<V> getTree()
	{
		return itsTree;
	}
	
	public SimpleTreeNode<V> getParent()
	{
		return itsParent;
	}
	
	public void setParent(SimpleTreeNode<V> aParent)
	{
		itsParent = aParent;
	}
	
	public boolean isLeaf()
	{
		return pChildren == null;
	}
	
	public IRWProperty<V> pValue()
	{
		return pValue;
	}
	
	public IListProperty<SimpleTreeNode<V>> pChildren()
	{
		return pChildren;
	}
}
