package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import seedu.address.commons.core.Config;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.events.ui.ExitAppRequestEvent;
import seedu.address.history.UndoableCommandHistoryManager;
import seedu.address.history.InputHistory;
import seedu.address.history.UndoableCommandHistory;
import seedu.address.logic.Logic;
import seedu.address.model.UserPrefs;
import seedu.address.model.item.ReadOnlyTask;

/**
 * The Main Window. Provides the basic application layout containing
 * a menu bar and space where other JavaFX elements can be placed.
 */
public class MainWindow extends UiPart {

    private static final String ICON = "/images/address_book_32.png";
    private static final String FXML = "MainWindow.fxml";
    public static final int MIN_HEIGHT = 600;
    public static final int MIN_WIDTH = 450;

    private Logic logic;
    private InputHistory inputHistory;

    // Independent Ui parts residing in this Ui container
    private TaskListPanel taskListPanel;
    private ResultDisplay resultDisplay;
    private StatusBarFooter statusBarFooter;
    private CommandBox commandBox;
    private SwitchViewButtons viewButtons;
    private Config config;
    private UserPrefs userPrefs;

    // Handles to elements of this Ui container
    private VBox rootLayout;
    private Scene scene;

    private String addressBookName;

    @FXML
    private AnchorPane browserPlaceholder;

    @FXML
    private AnchorPane commandBoxPlaceholder;

    @FXML
    private AnchorPane switchViewButtonsPlaceholder;
    
    @FXML
    private AnchorPane personListPanelPlaceholder;

    @FXML
    private AnchorPane resultDisplayPlaceholder;

    @FXML
    private AnchorPane statusbarPlaceholder;


    public MainWindow() {
        super();
    }

    @Override
    public void setNode(Node node) {
        rootLayout = (VBox) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    public static MainWindow load(Stage primaryStage, Config config, UserPrefs prefs, Logic logic, InputHistory history) {

        MainWindow mainWindow = UiPartLoader.loadUiPart(primaryStage, new MainWindow());
        mainWindow.configure(config.getAppTitle(), config.getAddressBookName(), config, prefs, logic, history);
        return mainWindow;
    }
       

    private void configure(String appTitle, String addressBookName, Config config, UserPrefs prefs,
                           Logic logic, InputHistory inputHistory) {

        //Set dependencies
        this.logic = logic;
        this.addressBookName = addressBookName;
        this.config = config;
        this.userPrefs = prefs;
        this.inputHistory = inputHistory;

        //Configure the UI
        setTitle(appTitle);
        setIcon(ICON);
        setWindowMinSize();
        setWindowDefaultSize(prefs);
        scene = new Scene(rootLayout);
        primaryStage.setScene(scene);

    }

    void fillInnerParts() {
        taskListPanel = TaskListPanel.load(primaryStage, getPersonListPlaceholder(), logic.getFilteredUndoneTaskList(), logic.getFilteredDoneTaskList());
        resultDisplay = ResultDisplay.load(primaryStage, getResultDisplayPlaceholder());
        statusBarFooter = StatusBarFooter.load(primaryStage, getStatusbarPlaceholder(), config.getTaskManagerFilePath());
        commandBox = CommandBox.load(primaryStage, getCommandBoxPlaceholder(), resultDisplay, logic, inputHistory);
        viewButtons = SwitchViewButtons.load(primaryStage, getSwitchViewButtonsPlaceholder(), resultDisplay, logic, inputHistory);
    }
    
    public void swapTaskList(boolean isDoneList) {
        taskListPanel.reloadLists(logic.getFilteredUndoneTaskList(), logic.getFilteredDoneTaskList(), isDoneList);
    }
    
    //@@author
    private AnchorPane getCommandBoxPlaceholder() {
        return commandBoxPlaceholder;
    }
    
    private AnchorPane getSwitchViewButtonsPlaceholder() {
        return switchViewButtonsPlaceholder;
    }

    private AnchorPane getStatusbarPlaceholder() {
        return statusbarPlaceholder;
    }

    private AnchorPane getResultDisplayPlaceholder() {
        return resultDisplayPlaceholder;
    }

    public AnchorPane getPersonListPlaceholder() {
        return personListPanelPlaceholder;
    }

    public void hide() {
        primaryStage.hide();
    }

    private void setTitle(String appTitle) {
        primaryStage.setTitle(appTitle);
    }

    /**
     * Sets the default size based on user preferences.
     */
    protected void setWindowDefaultSize(UserPrefs prefs) {
        primaryStage.setHeight(prefs.getGuiSettings().getWindowHeight());
        primaryStage.setWidth(prefs.getGuiSettings().getWindowWidth());
        if (prefs.getGuiSettings().getWindowCoordinates() != null) {
            primaryStage.setX(prefs.getGuiSettings().getWindowCoordinates().getX());
            primaryStage.setY(prefs.getGuiSettings().getWindowCoordinates().getY());
        }
    }

    private void setWindowMinSize() {
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
    }

    /**
     * Returns the current size and the position of the main Window.
     */
    public GuiSettings getCurrentGuiSetting() {
        return new GuiSettings(primaryStage.getWidth(), primaryStage.getHeight(),
                (int) primaryStage.getX(), (int) primaryStage.getY());
    }

    @FXML
    public void handleHelp() {
        HelpWindow helpWindow = HelpWindow.load(primaryStage);
        helpWindow.show();
    }

    //@@author
    public void show() {
        primaryStage.show();
    }

    //@@author
    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        raise(new ExitAppRequestEvent());
    }

    public TaskListPanel getTaskListPanel() {
        return this.taskListPanel;
    }

}
