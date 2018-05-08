package zz.utils.undo2;

import zz.utils.properties.IRWProperty;

/**
 * Represents an undoable command
 * @author gpothier
 *
 */
public abstract class Command
{
	public abstract void perform();
	public abstract void undo();
}
