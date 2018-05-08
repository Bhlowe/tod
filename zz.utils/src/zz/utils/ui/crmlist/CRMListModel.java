/*
 * Created on Feb 24, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package zz.utils.ui.crmlist;

import javax.swing.ListModel;

/**
 * Model for an editable list. Supports creation, deletion and reordering
 * of elements.
 * @author gpothier
 */
public interface CRMListModel extends ListModel
{
	/**
	 * Whether it is possible to create an element.
	 */
	public boolean canCreateElement ();
	
	/**
	 * Creates a new element and inserts it in the list.
	 * @return The position of the new element, or -1 if not applicable
	 */
	public int createElement ();
	
	/**
	 * Determines if it is possible to remove an element.
	 * @param aIndex The index of the element to check.
	 */
	public boolean canRemoveElement (int aIndex);
	
	/**
	 * Remove an object from the list.
	 * @param aIndex The index of the object to remove
	 */
	public void removeElement (int aIndex);
	
	/**
	 * Determines if it is possible to move an element.
	 * @param aSourceIndex The index of the element to move
	 * @param aTargetIndex The desired new position of the element.
	 */
	public boolean canMoveElement (int aSourceIndex, int aTargetIndex);
	
	/**
	 * Moves an element to a specified position.
	 * @param aSourceIndex The index of the element to move
	 * @param aTargetIndex The desired new position of the element.
	 */
	public void moveElement (int aSourceIndex, int aTargetIndex);
}
