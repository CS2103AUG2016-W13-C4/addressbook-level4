package seedu.address.logic.commands;

public abstract class UndoableCommand extends Command {
    
    private boolean isRedo;
    
    /**
     * Undoes the command and returns the result message.
     *
     * @return feedback message of the operation result for display
     */
    public abstract CommandResult undo();   
    
    /**
     * Updates the history of undoable commands with this command, if this command is not a 
     * command that was executed as part of a redo command.
     *
     */
    public void updateHistory(){
        
        if (isRedo) {
            return;
        }
        
        history.updateCommandHistory(this);
        history.resetRedo();
        
        isRedo = true;
    }
    
    /**
     * Getter method to check if the current command is a new command or is a command executed
     * as part of a redo command.
     * @return boolean representing if the current command is executed as part of a redo command
     */
    public boolean isRedoAction() {
        return isRedo;
    }

}
