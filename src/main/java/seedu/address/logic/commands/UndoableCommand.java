package seedu.address.logic.commands;

//@@author A0093960X
public abstract class UndoableCommand extends Command {

    protected boolean isRedoAction;

    /**
     * Undoes the command and returns the result message.
     *
     * @return feedback message of the operation result for display
     */
    public abstract CommandResult undo();

    /**
     * Updates the history of undoable commands with this undoable command, if this
     * command is not a command that was executed as part of a redo command.
     */
    public void updateHistory() {

        if (isRedoAction) {
            return;
        }

        history.updateCommandHistory(this);
        isRedoAction = true;
    }

}
