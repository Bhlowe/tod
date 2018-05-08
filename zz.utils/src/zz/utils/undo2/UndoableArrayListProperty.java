package zz.utils.undo2;

import zz.utils.properties.ArrayListProperty;

/**
 * An undoable version of {@link ArrayListProperty}.
 * @author gpothier
 */
public class UndoableArrayListProperty<T> extends ArrayListProperty<T>
{
	protected void elementAdded(int aIndex, T aElement) 
	{
		UndoStack theStack = UndoStack.getCurrent();
		if (theStack != null) theStack.addCommand(new AddCommand(aIndex, aElement));
	}
	
	protected void elementRemoved(int aIndex, T aElement) 
	{
		UndoStack theStack = UndoStack.getCurrent();
		if (theStack != null) theStack.addCommand(new RemoveCommand(aIndex, aElement));
	}
	
	private class AddCommand extends Command
	{
		private final int itsIndex;
		private final T itsElement;

		public AddCommand(int aIndex, T aElement)
		{
			itsIndex = aIndex;
			itsElement = aElement;
		}

		@Override
		public void perform()
		{
			add(itsIndex, itsElement);
		}

		@Override
		public void undo()
		{
			remove(itsIndex);
		}
	}
	
	private class RemoveCommand extends Command
	{
		private final int itsIndex;
		private final T itsElement;

		public RemoveCommand(int aIndex, T aElement)
		{
			itsIndex = aIndex;
			itsElement = aElement;
		}

		@Override
		public void perform()
		{
			remove(itsIndex);
		}

		@Override
		public void undo()
		{
			add(itsIndex, itsElement);
		}
	}
}
