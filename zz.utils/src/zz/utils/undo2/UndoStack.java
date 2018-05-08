package zz.utils.undo2;

import java.util.ArrayList;

import javax.swing.Action;

import zz.utils.ArrayStack;
import zz.utils.IProducer;
import zz.utils.ITask;
import zz.utils.Stack;
import zz.utils.notification.IEvent;
import zz.utils.notification.SimpleEvent;

public class UndoStack
{
	private static ThreadLocal<UndoStack> itsCurrentStack = new ThreadLocal<UndoStack>();
	
	private Stack<Operation> itsUndoStack = new ArrayStack<Operation>();
	private Stack<Operation> itsRedoStack = new ArrayStack<Operation>();
	private Operation itsCurrentOperation;
	private boolean itsReplaying = false;
	
	protected Action itsUndoAction;
	protected Action itsRedoAction;
	
	public final IEvent<Void> eActionPerformed = new SimpleEvent<Void>(); 

	public static UndoStack getCurrent()
	{
		return itsCurrentStack.get();
	}
	
	public void startOperation()
	{
		if (itsReplaying) return;
		if (itsCurrentOperation != null) throw new IllegalStateException();
		itsCurrentStack.set(this);
		itsCurrentOperation = new Operation();
	}
	
	public void commitOperation()
	{
		if (itsReplaying) return;
		if (itsCurrentOperation == null) throw new IllegalStateException();
		itsCurrentStack.set(null);
		itsUndoStack.push(itsCurrentOperation);
		itsRedoStack.clear();
		itsCurrentOperation = null;
		updateUndoActions();
	}
	
	public void cancelOperation()
	{
		if (itsReplaying) return;
		if (itsCurrentOperation == null) throw new IllegalStateException();
		itsCurrentStack.set(null);
		itsCurrentOperation.undo();
		itsCurrentOperation = null;
	}

	public void setActions (Action aUndoAction, Action aRedoAction)
	{
		itsUndoAction = aUndoAction;
		itsRedoAction = aRedoAction;
		updateUndoActions();
	}
	
	/**
	 * Undoes the command on the top of the stack
	 * @return The executed command
	 */
	public void undo ()
	{
		if (getCurrent() != null) throw new IllegalStateException("Cannot undo or redo while an operation is in progress");
		if (itsUndoStack.isEmpty()) return;

		itsReplaying = true;
		try
		{
			Operation theOperation = itsUndoStack.pop();
			theOperation.undo();
			itsRedoStack.push(theOperation);
			updateUndoActions();
			((SimpleEvent<Void>) eActionPerformed).fire(null);
		}
		finally
		{
			itsReplaying = false;
		}
	}

	/**
	 * Redoes the command on the top of the redo stack
	 * @return the executed command
	 */
	public void redo ()
	{
		if (getCurrent() != null) throw new IllegalStateException("Cannot undo or redo while an operation is in progress");
		if (itsRedoStack.isEmpty()) return;

		itsReplaying = true;
		try
		{
			Operation theOperation = itsRedoStack.pop();
			theOperation.perform();
			itsUndoStack.push(theOperation);
			updateUndoActions();
			((SimpleEvent<Void>) eActionPerformed).fire(null);
		}
		finally
		{
			itsReplaying = false;
		}
	}

	protected void updateUndoActions ()
	{
		if (itsUndoAction != null) itsUndoAction.setEnabled(! itsUndoStack.isEmpty());
		if (itsRedoAction != null) itsRedoAction.setEnabled(! itsRedoStack.isEmpty());
	}
	
	public void addCommand(Command aCommand)
	{
		if (itsReplaying) return;
		if (itsCurrentOperation == null) throw new IllegalStateException("No current operation");
		itsCurrentOperation.addCommand(aCommand);
		((SimpleEvent<Void>) eActionPerformed).fire(null);
	}
	
	/**
	 * Runs a piece of code without recording operations.
	 */
	public static void runWithoutUndo(Runnable aRunnable)
	{
		UndoStack theCurrentStack = itsCurrentStack.get();
		itsCurrentStack.set(null);
		aRunnable.run();
		itsCurrentStack.set(theCurrentStack);
	}
	
	public static <T> T runWithoutUndo(IProducer<T> aTask)
	{
		UndoStack theCurrentStack = itsCurrentStack.get();
		itsCurrentStack.set(null);
		T theResult = aTask.produce();
		itsCurrentStack.set(theCurrentStack);
		return theResult;
	}
	
	/**
	 * An operation groups a list of commands that are performed or undone together.
	 * @author gpothier
	 */
	private static class Operation
	{
		private ArrayList<Command> itsCommands = new ArrayList<Command>();
		
		public void addCommand(Command aCommand)
		{
			itsCommands.add(aCommand);
		}
		
		public void perform()
		{
			for (int i=0;i<itsCommands.size();i++) itsCommands.get(i).perform();
		}
		
		public void undo()
		{
			for (int i=itsCommands.size()-1;i>=0;i--) itsCommands.get(i).undo();
		}
	}
}
