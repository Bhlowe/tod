/*
 * Created on Apr 7, 2005
 */
package zz.utils.tree;

import java.util.Iterator;

/**
 * This class can be used to implement {@link zz.utils.tree.ITree#getChildren(N)}
 * @author gpothier
 */
public class DefaultChildrenIterator<N> implements Iterator<N>, Iterable<N>
{
	private ITree<N, ?> itsTree;
	private N itsParentNode;
	private int itsIndex = 0;
	private int itsLastIndex = -1;

	public DefaultChildrenIterator(ITree<N, ? > aTree, N aParentNode)
	{
		itsTree = aTree;
		itsParentNode = aParentNode;
	}

	public Iterator<N> iterator()
	{
		return this;
	}

	public boolean hasNext()
	{
		return itsIndex < itsTree.getChildCount(itsParentNode);
	}

	public N next()
	{
		itsLastIndex = itsIndex++;
		return itsTree.getChild(itsParentNode, itsLastIndex);
	}

	public void remove()
	{
		if (itsLastIndex < 0) throw new IllegalStateException();
		itsTree.removeChild(itsParentNode, itsIndex);
	}
}
