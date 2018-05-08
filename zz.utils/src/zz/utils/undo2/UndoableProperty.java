package zz.utils.undo2;

import zz.utils.properties.SimpleRWProperty;

/**
 * An undoable version of {@link SimpleRWProperty}
 * @author gpothier
 */
public class UndoableProperty<T> extends SimpleRWProperty<T>
{
	public UndoableProperty()
	{
		super();
	}

	public UndoableProperty(T aValue)
	{
		super(aValue);
	}

	protected void changed(T aOldValue, T aNewValue) 
	{
		UndoStack theStack = UndoStack.getCurrent();
		if (theStack != null) theStack.addCommand(new SetCommand(aOldValue, aNewValue));
	}
	
	/**
	 * Basic property set command
	 * @author gpothier
	 */
	private class SetCommand extends Command
	{
		private final T itsOldValue;
		private final T itsNewValue;

		public SetCommand(T aOldValue, T aNewValue)
		{
			itsOldValue = aOldValue;
			itsNewValue = aNewValue;
		}

		@Override
		public void perform()
		{
			set(itsNewValue);
		}

		@Override
		public void undo()
		{
			set(itsOldValue);
		}
	}

}
