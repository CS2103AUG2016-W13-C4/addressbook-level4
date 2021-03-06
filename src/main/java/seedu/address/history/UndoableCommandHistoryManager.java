package seedu.address.history;

import java.util.Stack;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.commands.UndoableCommand;

//@@author A0093960X
/**
 * Stores the history of undoable and redoable commands.
 */
public class UndoableCommandHistoryManager implements UndoableCommandHistory {

    private static final Logger logger = LogsCenter.getLogger(UndoableCommandHistoryManager.class);

    private static UndoableCommandHistoryManager theUndoableCommandHistory;

    // command effects
    private Stack<UndoableCommand> undoableCommands;
    private Stack<UndoableCommand> redoableCommands;

    // Private constructor for Singleton Pattern
    private UndoableCommandHistoryManager() {
        undoableCommands = new Stack<UndoableCommand>();
        redoableCommands = new Stack<UndoableCommand>();
    }

    // Use Singleton Pattern here
    public static UndoableCommandHistoryManager getInstance() {
        if (theUndoableCommandHistory == null) {
            theUndoableCommandHistory = new UndoableCommandHistoryManager();
        }
        return theUndoableCommandHistory;
    }

    @Override
    public void updateCommandHistory(UndoableCommand undoableCommand) {
        assert undoableCommands != null;
        undoableCommands.push(undoableCommand);
        resetRedo();
    }

    @Override
    public boolean isEarliestCommand() {
        assert undoableCommands != null;
        return undoableCommands.isEmpty();
    }

    @Override
    public boolean isLatestCommand() {
        assert redoableCommands != null;
        return redoableCommands.isEmpty();
    }

    @Override
    public UndoableCommand undoStep() {
        assert redoableCommands != null && undoableCommands != null;
        logger.info("Executing the undoStep method to undo the previous undoable command.");

        UndoableCommand undoneCmd = undoableCommands.pop();
        return redoableCommands.push(undoneCmd);
    }

    @Override
    public UndoableCommand redoStep() {
        assert redoableCommands != null && undoableCommands != null;
        logger.info("Executing the redoStep method to redo the last undo.");

        UndoableCommand redoneCmd = redoableCommands.pop();
        return undoableCommands.push(redoneCmd);
    }

    /**
     * Resets the redoable command history, erasing all redoable command
     * history.
     */
    private void resetRedo() {
        redoableCommands = new Stack<UndoableCommand>();
    }

}
