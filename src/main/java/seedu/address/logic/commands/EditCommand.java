package seedu.address.logic.commands;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.history.ReversibleEffect;
import seedu.address.logic.parser.DateParser;
import seedu.address.model.item.Task;
import seedu.address.model.item.Name;
import seedu.address.model.item.Priority;
import seedu.address.model.item.ReadOnlyTask;
import seedu.address.model.item.RecurrenceRate;

public class EditCommand extends Command{

    public static final String COMMAND_WORD = "edit";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Edit an item in the To-Do List. ";
              
    public static final String TOOL_TIP = "edit INDEX [NAME], [from/at START_DATE START_TIME][to/by END_DATE END_TIME][repeat every RECURRING_INTERVAL][-PRIORITY]";

    public static final String MESSAGE_DUPLICATE_FLOATING_TASK = "This task already exists in the task manager";

    public static final String MESSAGE_SUCCESS = "Item edited: %1$s";

    public final int targetIndex;
    
    private Task toEdit;
    
    Name taskName;
	Date startDate ;
    Date endDate;
    RecurrenceRate recurrenceRate;
    Priority priority;
    
	public EditCommand(int targetIndex,String taskNameString, String startDateString, String endDateString, 
            String recurrenceRateString, String timePeriodString, String priorityString)  throws IllegalValueException {
        	
		this.targetIndex = targetIndex;
		taskName = null;
		startDate = null;
        endDate = null;
        priority = null;
        
        if ( taskNameString!=null && !taskNameString.trim().equals("")) {
    		taskName = new Name(taskNameString);
        } else {
            System.out.println("TaskName is " + taskNameString);
        }
       /* 
        if(taskName == null){
            System.out.println("TaskName = null");
        }
        */
        if (startDateString != null) {
            DateParser dp = new DateParser(startDateString);
            startDate = dp.parseDate();
        }
        
        if (endDateString != null) {
            DateParser dp = new DateParser(endDateString);
            endDate = dp.parseDate();
        }
        
        if (recurrenceRateString == null && timePeriodString == null) {
            recurrenceRate = null;
        } else if (recurrenceRateString == null) {
            recurrenceRate = new RecurrenceRate("1", timePeriodString);
        } else {
            recurrenceRate = new RecurrenceRate(recurrenceRateString, timePeriodString);
        }

        //TODO: Throw IllegalValueException for default cases?
        if(priorityString != null){
        	switch (priorityString) {
            	case ("low"): case ("l"): priority = Priority.LOW; break; 
            	case ("high"): case ("h"): priority = Priority.HIGH; break;
            	case ("medium"): case ("m"): case ("med"): priority = Priority.MEDIUM; break;
        	}
        } 

        this.toEdit = new Task(taskName, startDate, endDate, recurrenceRate, priority);      
	}

	@Override
	public CommandResult execute() {	    
		assert model != null;
        UnmodifiableObservableList<ReadOnlyTask> lastShownList = model.getFilteredUndoneTaskList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_ITEM_DISPLAYED_INDEX);
        }

        ReadOnlyTask taskToEdit = lastShownList.get(targetIndex - 1);
        
        // Copy this task for history usage
        Task affectedTaskToEdit = new Task(taskToEdit);  
        
        if (taskName != null) {        
            model.editName(taskToEdit, taskName);
        }
        
        if (startDate != null) {
            model.editStartDate(taskToEdit, startDate);
        }

        if (endDate != null) {
            model.editEndDate(taskToEdit, endDate);
        }

        if (priority != null){
            model.editPriority(taskToEdit, priority);
        }
        
        if (recurrenceRate != null) {
            model.editRecurrence(taskToEdit, recurrenceRate);
        }
        
        // update the history
        List<Task> affectedTasks = new ArrayList<Task>();
        
        // add the original, unmodified task
        affectedTasks.add(affectedTaskToEdit);
        
        // add the updated task
        Task updatedTask = new Task(taskToEdit);
        affectedTasks.add(updatedTask);
        System.out.println("What edit inserted into history" + affectedTasks);
        history.update(new ReversibleEffect(COMMAND_WORD, affectedTasks));
        history.resetRedo();

        
        return new CommandResult(String.format(MESSAGE_SUCCESS, toEdit));
        
	}

}
