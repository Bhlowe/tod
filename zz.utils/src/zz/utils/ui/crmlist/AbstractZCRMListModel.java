/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui.crmlist;

import zz.utils.list.IList;


/**
 * Abstract class to help implementing editable list models.
 * The only method left to implement is {@link #newElement()}.
 * The elements are backed in a {@link zz.utils.list.IList}
 * @author gpothier
 */
public abstract class AbstractZCRMListModel<E> extends AbstractCRMListModel<E>
{
	private IList<E> itsList;
	
	public AbstractZCRMListModel ()
	{
		this (null);
	}
	
	public AbstractZCRMListModel (IList<E> aList) 
	{
		itsList = aList;
	}

	public IList<E> getList()
	{
		return itsList;
	}

	public void setList(IList<E> aList)
	{
		itsList = aList;
		fireContentsChanged();
	}

	protected void addElement0(int aIndex, E aElement)
	{
		itsList.add(aIndex, aElement);
	}
	
	protected boolean hasList0()
	{
		return itsList != null;
	}
	
	protected E removeElement0(int aIndex)
	{
		return itsList.remove(aIndex);
	}
	
	public E getElementAt(int aIndex)
	{
		return itsList.get(aIndex);
	}
	
	public int getSize()
	{
		return itsList.size();
	}
}
