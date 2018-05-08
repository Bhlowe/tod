/**
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Aug 27, 2003
 * Time: 4:39:22 PM
 * To change this template use Options | File Templates.
 */
package zz.utils;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

/**
 * This subclass of {@link DefaultTreeModel} makes public the methods
 * that permit to fire modification events.
 */
public class FireableTreeModel extends DefaultTreeModel
{
	public FireableTreeModel (TreeNode root)
	{
		super (root);
	}

	public FireableTreeModel (TreeNode root, boolean asksAllowsChildren)
	{
		super (root, asksAllowsChildren);
	}

	public void fireTreeNodesChanged (Object source, Object[] path, int[] childIndices, Object[] children)
	{
		super.fireTreeNodesChanged (source, path, childIndices, children);
	}

	public void fireTreeNodesInserted (Object source, Object[] path, int[] childIndices, Object[] children)
	{
		super.fireTreeNodesInserted (source, path, childIndices, children);
	}

	public void fireTreeNodesRemoved (Object source, Object[] path, int[] childIndices, Object[] children)
	{
		super.fireTreeNodesRemoved (source, path, childIndices, children);
	}

	public void fireTreeStructureChanged (Object source, Object[] path, int[] childIndices, Object[] children)
	{
		super.fireTreeStructureChanged (source, path, childIndices, children);
	}
}
