package seedu.address.model.item;

import java.util.Date;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.CollectionUtil;

public class Task extends DateItem {
    
    private final Date deadline;
    private final RecurrenceRate recurrenceRate;

    public Task(Name taskName, Date deadline) throws IllegalValueException {
        assert !CollectionUtil.isAnyNull(taskName, deadline);
  
        itemName = taskName;
        this.deadline = deadline;
        dateToOrderBy = deadline;
        recurrenceRate = new RecurrenceRate(0);
        System.out.println(itemName.toString() + "\n" + deadline.toString() + "\n" + recurrenceRate.toString());
    }
    
    public Task(Name taskName, Date deadline, RecurrenceRate recurrenceRate) throws IllegalValueException {
        assert !CollectionUtil.isAnyNull(taskName, deadline);
  
        itemName = taskName;
        this.deadline = deadline;
        dateToOrderBy = deadline;
        this.recurrenceRate = recurrenceRate;
    }
    
    /** TODO: Not sure whether we creating superclass ReadOnlyDateItem or creating individual classes
     * Copy constructor.
     * @throws IllegalValueException 
     */
    /*public Task(ReadOnlyTask source) {
        this(source.getName(), source.getDeadline());
    }*/
    
    //TODO: Not putting setter methods because I'm assuming that for 'edit', we will be deleting and recreating a new Task
    
    //TODO: Not sure how we are using toString() yet, so I'll return a dummy value
    @Override
    public String toString() {
        return deadline.getDate() + " ";
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof Task // instanceof handles nulls
                && this.itemName.equals(((Task) other).getName()) // state check
                && this.deadline.equals(((Task) other).deadline) 
                && this.dateToOrderBy.equals(((Task) other).getDateToOrderBy())
                && this.recurrenceRate.equals(((Task) other).getRecurrenceRate()));
                
                
    }

    public Date getDeadline() {
        return deadline;
    }
    
    public RecurrenceRate getRecurrenceRate() {
        return recurrenceRate;
    }

    // TODO Update this method
    @Override
    public int compareTo(DateItem o) {
        return 0;
    }
}
