/*
 * Created by IntelliJ IDEA.
 * User: gpothier
 * Date: Jan 9, 2002
 * Time: 3:50:30 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package zz.utils.undo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

/**
 * This class manages the stack of commands for an editor that needs to implement
 * an undo/redo mechanism.
 * It also takes care of enabling/disabling Action objects representing undo/redo buttons
 */
public class UndoEnvironment
{
	protected List itsUndoStack = new ArrayList ();
	protected List itsRedoStack = new ArrayList ();

	protected Action itsUndoAction;
	protected Action itsRedoAction;

	private Set itsAffectedObjects;

	public UndoEnvironment ()
	{
		this (null, null);
	}

	public UndoEnvironment (Action aUndoAction, Action aRedoAction)
	{
		setActions(aUndoAction, aRedoAction);
	}

	public void setActions (Action aUndoAction, Action aRedoAction)
	{
		itsUndoAction = aUndoAction;
		itsRedoAction = aRedoAction;
		updateUndoActions();
	}

	/**
	 * Executes the specified action and places it onto the undo stack
	 */
	public void execute (Command aCommand)
	{
		aCommand.perform();
		push(aCommand);
	}

	/**
	 * Pushes a command onto the undo stack without executing it.
	 */
	public synchronized void push (Command aCommand)
	{
		itsUndoStack.add (aCommand);
		itsRedoStack.clear();

		collectAffectedObjects(aCommand);

		updateUndoActions();
	}

	protected void collectAffectedObjects (Command aCommand)
	{
		Collection theAffectedObjects = aCommand.getAffectedObjects();
		if (theAffectedObjects != null)
		{
			if (itsAffectedObjects == null) itsAffectedObjects = new HashSet ();
			itsAffectedObjects.addAll(theAffectedObjects);
		}
	}

	/**
	 * Returns the current list of affected objects and resets the local list of affected
	 * objects. Might return null if there is no affected objects.
	 * The returned collection is actually a set so no objects are duplicated.
	 */
	public synchronized Collection takeAffectedObjects ()
	{
		Set theResult = itsAffectedObjects;
		itsAffectedObjects = null;
		return theResult;
	}

	/**
	 * Undoes the command on the top of the stack
	 * @return The executed command
	 */
	public Command undo ()
	{
		if (itsUndoStack.isEmpty()) return null;

		Command theCommand = (Command) itsUndoStack.remove (itsUndoStack.size()-1);
		theCommand.undo();
		itsRedoStack.add(theCommand);
		updateUndoActions();

		return theCommand;
	}

	/**
	 * Redoes the command on the top of the redo stack
	 * @return the executed command
	 */
	public Command redo ()
	{
		if (itsRedoStack.isEmpty()) return null;

		Command theCommand = (Command) itsRedoStack.remove (itsRedoStack.size()-1);
		theCommand.perform();
		itsUndoStack.add(theCommand);
		updateUndoActions();

		return theCommand;
	}

	protected void updateUndoActions ()
	{
		if (itsUndoAction != null) itsUndoAction.setEnabled(! itsUndoStack.isEmpty());
		if (itsRedoAction != null) itsRedoAction.setEnabled(! itsRedoStack.isEmpty());
	}

}
