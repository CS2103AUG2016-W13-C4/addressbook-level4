package seedu.address.logic.commands;

import seedu.address.commons.core.EventsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.commons.events.ui.JumpToListRequestEvent;
import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.model.item.ReadOnlyTask;

//@@author A0139498J
/**
 * Selects a task identified using its last displayed index from the task manager.
 */
public class SelectCommand extends Command {

    public final int targetIndex;

    public static final String COMMAND_WORD = "select";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Selects the task identified by the index number used in the last task listing.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    
    public static final String TOOL_TIP = "select";
    
    public static final String MESSAGE_SELECT_TASK_SUCCESS = "Selected Task: %1$s";

    public SelectCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = getLastShownList();

        boolean isTaskTargetIndexOutOfBounds = (lastShownList.size() < targetIndex);
        
        if (isTaskTargetIndexOutOfBounds) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_TASK_DISPLAYED_INDEX);
        }

        EventsCenter.getInstance().post(new JumpToListRequestEvent(targetIndex - 1));
        return new CommandResult(String.format(MESSAGE_SELECT_TASK_SUCCESS, targetIndex));
    }

    /**
     * Returns the last shown list, depending on whether the current list view
     * is the done or undone list.
     */
    private UnmodifiableObservableList<ReadOnlyTask> getLastShownList() {
        if (model.isCurrentListDoneList()) {
            return model.getFilteredDoneTaskList();
        } else {
            return model.getFilteredUndoneTaskList();
        }
    }

}
