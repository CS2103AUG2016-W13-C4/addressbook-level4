package seedu.address.ui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.events.ui.PersonPanelSelectionChangedEvent;
import seedu.address.model.item.ReadOnlyTask;
import seedu.address.model.item.Task;
import seedu.address.commons.core.LogsCenter;

import java.util.logging.Logger;

/**
 * Panel containing the list of persons.
 */
public class TaskListPanel extends UiPart {
    private final Logger logger = LogsCenter.getLogger(TaskListPanel.class);
    private static final String FXML = "PersonListPanel.fxml";
    private VBox panel;
    private AnchorPane placeHolderPane;
    
    // store reference to undone and done task list for switching between views
    private ObservableList<ReadOnlyTask> undoneTaskList;
    private ObservableList<ReadOnlyTask> doneTaskList;

    @FXML
    private ListView<ReadOnlyTask> taskListView;

    public TaskListPanel() {
        super();
    }

    @Override
    public void setNode(Node node) {
        panel = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }

    public static TaskListPanel load(Stage primaryStage, AnchorPane personListPlaceholder,
                                       ObservableList<ReadOnlyTask> undoneTaskList, ObservableList<ReadOnlyTask> doneTaskList) {
        TaskListPanel personListPanel =
                UiPartLoader.loadUiPart(primaryStage, personListPlaceholder, new TaskListPanel());
        personListPanel.configure(undoneTaskList, doneTaskList);

        return personListPanel;
    }

    private void configure(ObservableList<ReadOnlyTask> undoneTaskList, ObservableList<ReadOnlyTask> doneTaskList) {
        //set up references
        this.undoneTaskList = undoneTaskList;
        this.doneTaskList = doneTaskList;
        
        setConnections(undoneTaskList);
        addToPlaceholder();
    }

    // can use this for init and when switching between undone and done task view.
    private void setConnections(ObservableList<ReadOnlyTask> personList) {
        taskListView.setItems(personList);
        taskListView.setCellFactory(listView -> new PersonListViewCell());
        setEventHandlerForSelectionChangeEvent();
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(panel);
    }

    private void setEventHandlerForSelectionChangeEvent() {
        taskListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                logger.fine("Selection in person list panel changed to : '" + newValue + "'");
                raise(new PersonPanelSelectionChangedEvent(newValue));
            }
        });
    }
        
    //@@author A0139498J
    public void switchToDoneTaskListView() {
        taskListView.setItems(doneTaskList);
    }
    
    public void switchToUndoneTaskListView() {
        taskListView.setItems(undoneTaskList);
    }

    //@@author
    public void scrollTo(int index) {
        Platform.runLater(() -> {
            taskListView.scrollTo(index);
            taskListView.getSelectionModel().clearAndSelect(index);
        });
    }

    class PersonListViewCell extends ListCell<ReadOnlyTask> {

        public PersonListViewCell() {
        }

        @Override
        protected void updateItem(ReadOnlyTask person, boolean empty) {
            super.updateItem(person, empty);

            if (empty || person == null) {
                setGraphic(null);
                setText(null);
            } else {
                setGraphic(TaskCard.load(person, getIndex() + 1).getLayout());
            }
        }
    }

    public void reloadLists(ObservableList<ReadOnlyTask> undoneTaskList, ObservableList<ReadOnlyTask>doneTaskList, boolean isDoneList) {
        this.undoneTaskList = undoneTaskList;
        this.doneTaskList = doneTaskList;
        
        if (isDoneList) {
            setConnections(doneTaskList);
        }
        else {
            setConnections(undoneTaskList);
        }
    }

}
