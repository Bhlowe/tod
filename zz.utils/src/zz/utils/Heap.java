/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Feb 8, 2002
 * Time: 3:13:51 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class implements the traditional heap.
 * The smaller node is always at the root of the heap.
 */
public class Heap
{
    private List itsNodes = new ArrayList ();

	public Heap ()
	{
		itsNodes.add (null); // Element at index 0 is not used
	}

	public String toString ()
	{
		StringBuffer theBuffer = new StringBuffer();
		for (Iterator theIterator = itsNodes.iterator (); theIterator.hasNext ();)
		{
			HeapNode theNode = (HeapNode) theIterator.next ();
			if (theNode != null) theBuffer.append(" "+theNode.getValue());
		}
		return theBuffer.toString();
	}

	protected HeapNode getNode (int anIndex)
	{
		if (anIndex < 1) throw new RuntimeException ("Illegal access");
		if (anIndex >= itsNodes.size()) return null;
		return (HeapNode) itsNodes.get (anIndex);
	}

	public void insert (HeapNode aNode)
	{
		itsNodes.add (aNode);
		siftUp (itsNodes.size()-1);
	}

	protected void siftUp (int anIndex)
	{
		int theParentIndex = anIndex / 2;
		HeapNode theSiftedNode = getNode(anIndex);
		double theSiftedValue = theSiftedNode.getValue();

		while (theParentIndex > 0)
		{
			HeapNode theParentNode = getNode (theParentIndex);
			if (theSiftedValue < theParentNode.getValue())
			{
				// swap
				itsNodes.set (anIndex, theParentNode);

				// sift up
				anIndex = theParentIndex;
				theParentIndex /= 2;
			}
			else break;
		}

		itsNodes.set (anIndex, theSiftedNode);
	}

	public HeapNode removeRoot ()
	{
		int theSize = itsNodes.size();

		if (theSize < 2) return null;
		HeapNode theResult = getNode (1);

		HeapNode theLastNode = (HeapNode) itsNodes.remove (theSize-1);

		if (theSize >= 3) // means 2 or more items
		{
			itsNodes.set (1, theLastNode);
			siftDown (1);
		}

		return theResult;
	}

	protected void siftDown (int anIndex)
	{
		HeapNode theSiftedNode = getNode(anIndex);
		double theSiftedValue = theSiftedNode.getValue();

		while (true)
		{
			HeapNode theLeftChild = getNode (anIndex*2);
			HeapNode theRightChild = getNode (anIndex*2 + 1);

			double theLeftValue = theLeftChild != null ? theLeftChild.getValue() : Double.MAX_VALUE;
			double theRightValue = theRightChild != null ? theRightChild.getValue() : Double.MAX_VALUE;

			if (theSiftedValue < theLeftValue && theSiftedValue < theRightValue) break;

			if (theLeftValue < theRightValue)
			{
                itsNodes.set(anIndex, theLeftChild);
				anIndex = anIndex*2;
			}
			else
			{
				itsNodes.set (anIndex, theRightChild);
				anIndex = anIndex*2 + 1;
			}
		}

		itsNodes.set (anIndex, theSiftedNode);
	}

}