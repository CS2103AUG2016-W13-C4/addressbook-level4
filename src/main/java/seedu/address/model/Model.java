package seedu.address.model;

import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.model.item.Task;
import seedu.address.model.item.Name;
import seedu.address.model.item.Priority;
import seedu.address.model.item.ReadOnlyTask;
import seedu.address.model.item.RecurrenceRate;
import seedu.address.model.item.UniqueTaskList;
import seedu.address.model.item.UniqueTaskList.TaskNotFoundException;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javafx.collections.ObservableList;

/**
 * The API of the Model component.
 */
public interface Model {
    /** Clears existing backing model and replaces with the provided new data. */
    void resetData(ReadOnlyTaskManager newData);

    /** Returns the TaskManager */
    ReadOnlyTaskManager getTaskManager();

    /** Adds the given floating task */
    void addTask(Task task);
    
    /** Deletes the given floating task. */
    void deleteUndoneTask(ReadOnlyTask target) throws UniqueTaskList.TaskNotFoundException;

    /** Add the given floating tasks */
    void addTasks(List<Task> tasks);
       
    /** Archives the task by adding it into DoneTaskList */
    void addDoneTask(Task task);

    /** Removes the task permanently from the archive DoneTaskList **/
    void deleteDoneTask(ReadOnlyTask floatingTask) throws TaskNotFoundException;
    
    /** Archives the tasks by adding them into DoneTaskList */
    void addDoneTasks(List<Task> task);
    
    /** Returns the filtered undone task list as an {@code UnmodifiableObservableList<ReadOnlyPerson>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredUndoneTaskList();
    
    /** Returns the filtered done task list as an {@code UnmodifiableObservableList<ReadOnlyPerson>} */
    UnmodifiableObservableList<ReadOnlyTask> getFilteredDoneTaskList();

    /** Updates the filter of the filtered task lists to show all tasks */
    void updateFilteredListsToShowAll();

    /** Updates the filter of the filtered task list to filter by the given keywords*/
    void updateFilteredUndoneTaskListNamePred(Set<String> keywords);

    /** Updates the filter of the filtered done task list to filter by the given keywords*/
    void updateFilteredDoneTaskListNamePred(Set<String> keywords);
    
    /** Returns true is current list is done task list, false if current list is undone task list*/
    Boolean isCurrentListDoneList();
    
    /** Sets current list to be done list*/
    public void setCurrentListToBeDoneList();
    
    /** Sets current list to be undone list*/
    public void setCurrentListToBeUndoneList();
        
    /** Edits the parameters of the given floating task*/
	void editTask(ReadOnlyTask taskToEdit, Name taskName, Date startDate, Date endDate, Priority priority,
			RecurrenceRate recurrenceRate);
	
    /** Clears existing backing model of the done task data and replaces with the provided new data. */
    void resetDoneData(ReadOnlyTaskManager newData);
    
    /** Clears existing backing model if the undone task data and replaces with the provided new data. */
    void resetUndoneData(ReadOnlyTaskManager newData);
    
    void setTaskManagerUndoneList(ObservableList<Task> list);

    void setTaskManagerDoneList(ObservableList<Task> list);
    
    void clearTaskManagerUndoneList();
    
    void clearTaskManagerDoneList();
    
    ObservableList<Task> getTaskManagerUndoneList();
    
    ObservableList<Task> getTaskManagerDoneList();

    void updateFilteredUndoneTaskListDatePred(Set<String> keywords);

    void updateFilteredDoneTaskListDatePred(Set<String> keywords);





}
