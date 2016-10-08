package seedu.address.logic.commands;

import java.util.Calendar;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.item.DateItem;
import seedu.address.model.item.Name;
import seedu.address.model.item.RecurrenceRate;
import seedu.address.model.item.Task;
import seedu.address.model.item.UniqueDateItemList.DuplicateDateItemException;
import seedu.address.model.item.UniqueFloatingTaskList.DuplicateFloatingTaskException;

public class AddDateItemCommand extends Command {

    public static final String COMMAND_WORD = "add";

    //TODO: Update documentation
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds an item to To-Do List. "
            + "Parameters: NAME [rank PRIORITY_VALUE]\n"
            + "Example: " + COMMAND_WORD
            + " read Harry Potter and the Akshay rank 1";

    public static final String MESSAGE_SUCCESS = "New item added: %1$s";

    //TODO: Refactoring needed here
    private DateItem toAdd;

    /**
     * Convenience constructor using raw values.
     *
     * @throws IllegalValueException if any of the raw values are invalid
     */
    public AddDateItemCommand(String name, String date) throws IllegalValueException {
        //TODO: Parse Date
        System.out.println(name + ";" + date);
        this.toAdd = new Task(new Name(name), Calendar.getInstance().getTime());
    }
    
    public AddDateItemCommand(String name, String date, String recurrenceRate) throws IllegalValueException {
        //TODO: Parse Date
        System.out.println(name + ";" + date + ";" + recurrenceRate);
       this.toAdd = new Task(new Name(name), Calendar.getInstance().getTime(), new RecurrenceRate(Integer.parseInt(recurrenceRate)));
    }

    @Override
    public CommandResult execute() {
        assert model != null;
        try {
            model.addDateItem(toAdd);
        } catch (DuplicateDateItemException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return new CommandResult(String.format(MESSAGE_SUCCESS, toAdd));
    }

}
