/*
 * Created on Aug 19, 2006
 */
package zz.utils.list;

import java.util.NoSuchElementException;

/**
 * A re-implementation of a linked list, with the entry structure public.
 * @author gpothier
 */
public class NakedLinkedList<E>
{
	private int itsSize = 0;
	private Entry<E> itsRoot;
	
	
	public NakedLinkedList()
	{
		clear();
	}
	
	/**
	 * Removes all entries from the list. Note that the entries themselves are not cleared
	 * (they still have their next/prev pointers)
	 */
	public void clear()
	{
		itsRoot = new Entry<E>(null);
		itsRoot.setPrev(itsRoot);
		itsRoot.setNext(itsRoot);
	}
	
	/**
	 * Creates a new entry. Subclasses can override this method to create
	 * custom entries.
	 */
	public Entry<E> createEntry(E aElement)
	{
		return new Entry<E>(aElement);
	}
	
	public int size()
	{
		return itsSize;
	}
	
	public void addAfter(Entry<E> aBase, Entry<E> aEntry)
	{
		assert aEntry.getNext() == null;
		assert aEntry.getPrev() == null;
		
		aEntry.setPrev(aBase);
		aEntry.setNext(aBase.getNext());
		aBase.getNext().setPrev(aEntry);
		aBase.setNext(aEntry);
		
		itsSize++;
	}

	public E getLast()
	{
		return getLastEntry().getValue();
	}
	
	public Entry<E> getLastEntry()
	{
		Entry<E> theLast = itsRoot.getPrev();
		if (theLast == itsRoot) throw new NoSuchElementException();
		return theLast;
	}
	
	/**
	 * Checks if the given entry is the last entry of this list
	 */
	public boolean isLastEntry(Entry<E> aEntry)
	{
		return aEntry == itsRoot.getPrev();
	}
	
	public void addLast(E aElement)
	{
		addLast(createEntry(aElement));
	}
	
	public void addLast(Entry<E> aEntry)
	{
		addAfter(itsRoot.getPrev(), aEntry);
	}
	
	public E getFirst()
	{
		return getFirstEntry().getValue();
	}
	
	public Entry<E> getFirstEntry()
	{
		Entry<E> theFirst = itsRoot.getNext();
		if (theFirst == itsRoot) throw new NoSuchElementException();
		return theFirst;
	}
	
	/**
	 * Checks if the given entry is the last entry of this list
	 */
	public boolean isFirstEntry(Entry<E> aEntry)
	{
		return aEntry == itsRoot.getNext();
	}
	
	public void addFirst(E aElement)
	{
		addFirst(createEntry(aElement));
	}
	
	public void addFirst(Entry<E> aEntry)
	{
		addAfter(itsRoot, aEntry);
	}
	
	public Entry<E> getNextEntry(Entry<E> aEntry)
	{
		Entry<E> theNext = aEntry.getNext();
		return theNext != itsRoot ? theNext : null;
	}
	
	public Entry<E> getPrevEntry(Entry<E> aEntry)
	{
		Entry<E> thePrev = aEntry.getPrev();
		return thePrev != itsRoot ? thePrev : null;
	}
	
	public void remove(Entry<E> aEntry)
	{
		aEntry.getPrev().setNext(aEntry.getNext());
		aEntry.getNext().setPrev(aEntry.getPrev());
		aEntry.setPrev(null);
		aEntry.setNext(null);
		itsSize--;
	}
	
	public void moveFirst(Entry<E> aEntry)
	{
		if (isFirstEntry(aEntry)) return;
		aEntry.getPrev().setNext(aEntry.getNext());
		aEntry.getNext().setPrev(aEntry.getPrev());

		Entry<E> theFirst = itsRoot.getNext();
		aEntry.setPrev(itsRoot);
		aEntry.setNext(theFirst);
		itsRoot.setNext(aEntry);
		theFirst.setPrev(aEntry);
	}
	
	public void moveLast(Entry<E> aEntry)
	{
		if (isLastEntry(aEntry)) return;
		aEntry.getPrev().setNext(aEntry.getNext());
		aEntry.getNext().setPrev(aEntry.getPrev());
		
		Entry<E> theLast = itsRoot.getPrev();
		aEntry.setPrev(theLast);
		aEntry.setNext(itsRoot);
		itsRoot.setPrev(aEntry);
		theLast.setNext(aEntry);
	}
	
	public static class Entry<E>
	{
		private Entry<E> itsNext;
		private Entry<E> itsPrev;
		private E itsValue;
		
		public Entry(E aValue)
		{
			itsValue = aValue;
		}

		Entry<E> getNext()
		{
			return itsNext;
		}
		
		void setNext(Entry<E> aNext)
		{
			itsNext = aNext;
		}
		
		Entry<E> getPrev()
		{
			return itsPrev;
		}
		
		void setPrev(Entry<E> aPrev)
		{
			itsPrev = aPrev;
		}
		
		public E getValue()
		{
			return itsValue;
		}
		
		public void setValue(E aValue)
		{
			itsValue = aValue;
		}
		
		public boolean isAttached()
		{
			assert (getNext() == null) == (getPrev() == null);
			return getNext() != null;
		}
		
		@Override
		public String toString()
		{
			return "NLL.Entry: "+itsValue;
		}
	}
}
