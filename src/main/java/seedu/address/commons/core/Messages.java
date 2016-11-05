package seedu.address.commons.core;

/**
 * Container for user visible messages.
 */
public class Messages {

    public static final String MESSAGE_UNKNOWN_COMMAND = "Unknown command";
    public static final String MESSAGE_TOOLTIP_INVALID_COMMAND_FORMAT = "Type any command to get a tooltip on the command format!";
    public static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format! \n%1$s";
    public static final String MESSAGE_INVALID_TASK_DISPLAYED_INDEX = "The task index provided is invalid";
    public static final String MESSAGE_ITEMS_LISTED_OVERVIEW = "%1$d tasks listed!";
    public static final String MESSAGE_DONE_LIST_RESTRICTED_COMMANDS = "The command you are trying to execute is not allowed on the done list.\n"
            + "Supported commands on done list: \n\t[clear][delete][exit][find]\n\t[help][list][redo][select][undo]";  
    //@@author A0139498J
    public static final String MESSAGE_DONE_LIST_RESTRICTION = "Cannot do this command on done list. Please switch back to normal list view by typing list.";

}
