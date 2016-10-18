package seedu.address.history;

import seedu.address.logic.commands.UndoableCommand;

public interface UndoableCommandHistory {

    /** Updates the command history with the given UndoableCommand **/
    public void updateCommandHistory(UndoableCommand undoableCommand);
    
    /** Returns whether we are already at the earliest command state (there is nothing to undo anymore) **/
    public boolean isEarliestCommand();
    
    /** Returns whether we are already at the latest command state (there is nothing to redo anymore) **/
    public boolean isLatestCommand();
    
    /** Executes an undo step on the command history, returning the UndoableCommand that was undone **/
    public UndoableCommand undoStep();
    
    /** Executes a redo step on the command history, returning the UndoableCommand that was redone **/
    public UndoableCommand redoStep();
    
    /** Resets the history of undos (the past undos that can be redone) **/
    public void resetRedo();
}
