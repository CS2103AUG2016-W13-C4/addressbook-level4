package seedu.address.ui;

import com.google.common.eventbus.Subscribe;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import seedu.address.commons.events.ui.IncorrectCommandAttemptedEvent;
import seedu.address.logic.Logic;
import seedu.address.logic.commands.*;
import seedu.address.commons.util.FxViewUtil;
import seedu.address.history.InputHistory;
import seedu.address.commons.core.LogsCenter;

import java.util.logging.Logger;

//@@author A0093960X
public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private ResultDisplay resultDisplay;
    String previousCommandTest;

    private Logic logic;
    private InputHistory inputHistory;
    
    private static final String BACKSPACE_UNICODE = "\u0008";
    private static final String SPACE_UNICODE = "\u0020";
    private static final String CARRIAGE_RETURN = "\r";
    private static final String NEW_LINE = "\n";


    @FXML
    private TextField commandTextField;
    private CommandResult mostRecentResult;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder,
            ResultDisplay resultDisplay, Logic logic, InputHistory history) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(resultDisplay, logic, history);
        commandBox.addToPlaceholder();
        return commandBox;
    }

    public void configure(ResultDisplay resultDisplay, Logic logic, InputHistory history) {
        this.resultDisplay = resultDisplay;
        this.logic = logic;
        this.inputHistory = history;
        registerAsAnEventHandler(this);
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(commandTextField);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
        
        commandTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case UP:
                        // fall through
                    case DOWN:                    
                        keyEvent.consume();
                        handleUpDownArrow(keyEvent);
                    default:
                        break;
                }
            }
        });
        
    }

    @Override
    public void setNode(Node node) {
        commandPane = (AnchorPane) node;
    }

    @Override
    public String getFxmlPath() {
        return FXML;
    }

    @Override
    public void setPlaceholder(AnchorPane pane) {
        this.placeHolderPane = pane;
    }
    
    /**
     * Handles the event where the user presses a key in the command box.
     */
    @FXML
    private void handleKeyInput(KeyEvent event) {       
        String keyInputAsString = event.getCharacter();
        
        boolean keyIsEnter = checkIfEnterPressed(keyInputAsString);
        
        if (keyIsEnter) {
            return;
        }
        
        // reset incorrect command style on command box
        setStyleToIndicateCorrectCommand();
        
        String userInput = getUserInputAfterKeyPressed(keyInputAsString);
        updateTooltipForUser(userInput);        

    }

    private boolean checkIfEnterPressed(String keyInputAsString) {
        // Enter is \r\n on windows, \n on unix
        return keyInputAsString.equals(CARRIAGE_RETURN) || keyInputAsString.equals(NEW_LINE);
    }
    
    private void handleUpDownArrow(KeyEvent event) {
        KeyCode key = getKeyCodeFromEvent(event);
        
        setStyleToIndicateCorrectCommand();
        handleInputHistoryNavigation(key);
        /*
        String userInput = getUserInputAfterKeyPressed(key);
        */
        String userInput = commandTextField.getText();
        updateTooltipForUser(userInput);    
    }

    /**
     * Get the complete user input taking into account the current key pressed as key pressed
     * event is triggered before the command box text is updated.
     * key is either a backspace, space, letter or digit key.
     * 
     * @param keyAsString the key that was pressed as string
     * @return the full user input taking into account the key pressed
     */
    private String getUserInputAfterKeyPressed(String keyAsString) {
        String userInput = commandTextField.getText();
        int caretPosition = commandTextField.getCaretPosition();
                
        switch (keyAsString) {
            case BACKSPACE_UNICODE:
                // backspace action occurs before event triggers, just return the user input
                return userInput;
            case SPACE_UNICODE:
                return applySpaceAtPosition(userInput, caretPosition);
            default:
                // is a normal letter/digit
                return applyKeyAtPosition(userInput, keyAsString, caretPosition);
        }
        
    }

    /**
     * Returns a string that is the result of adding the given key in the specified position of the string.
     * @param userInput the user input
     * @param keyString the key as a string
     * @param position the position to add the key
     * @return string with key at the specified position in the user input string
     */
    private String applyKeyAtPosition(String userInput, String keyString, int position) {
        return userInput.substring(0, position) + keyString + userInput.substring(position);
    }

    /**
     * Returns the key code associated with the Key Event
     * @param event the KeyEvent
     * @return the key code associated
     */
    private KeyCode getKeyCodeFromEvent(KeyEvent event) {
        return event.getCode();
    }

    /**
     * Updates the tooltip on the GUI for the user to see.
     */
    private void updateTooltipForUser(String userInput) {
        String toDisplay = logic.generateToolTip(userInput);
        resultDisplay.postMessage(toDisplay);
    }
    
    /**
     * Returns a string with a single whitespace character appended to the back of
     * the given user input string
     * @param userInput
     * @return
     */
    private String applySpaceAtPosition(String userInput, int position) {
        return userInput.substring(0, position) + " " + userInput.substring(position);
    }

    /**
     * Handles the event where the user is trying to navigate the input history.
     * keyCode must either be up or down arrow key.
     * 
     * @param keyCode the keycode associated with this event
     */
    private void handleInputHistoryNavigation(KeyCode keyCode) {
        assert keyCode == KeyCode.UP || keyCode == KeyCode.DOWN;
        
        boolean wantPrev = checkIfWantPrevInput(keyCode);
        
        // if attempt to get next command while at latest command input or prev while at earliest, return
        if (desiredInputHistoryUnavailable(wantPrev)) {
            return;
        }
                
        if (wantPrev){
            handleGetPreviousInput();
        }        
        else {
            // else the user wants next
            handleGetNextInput();
        }
        
        updateCaretPosition();
    }

    /**
     * Returns whether the user is trying to access a previous or next input in 
     * the input history but is already at the limit (either earliest history or latest
     * history respectively).
     * 
     * @param wantPrev boolean representing if the user wants the previous input
     * @return
     */
    private boolean desiredInputHistoryUnavailable(boolean wantPrev) {
        boolean wantNext = !wantPrev;
        boolean atEarliestHistoryButWantPrevInput = inputHistory.isEarliestInput() && wantPrev;
        boolean atLatestHistoryButWantNextInput = inputHistory.isLatestInput() && wantNext;
        
        return atEarliestHistoryButWantPrevInput || atLatestHistoryButWantNextInput;
    }

    /**
     * Updates the caret position to the end of the current text input in the command box.
     */
    private void updateCaretPosition() {
        String currentInputShown = commandTextField.getText();
        int positionAtEndOfString = currentInputShown.length();
        
        commandTextField.positionCaret(positionAtEndOfString);
    }

    /**
     * Handles the event where the user wants to get the next input from input history.
     */
    private void handleGetNextInput() {
        // store the current input into the prev first
        String nextInput = inputHistory.nextStep(); 
        
        // get a next command input and replace current input
        commandTextField.setText(nextInput);
    }

    /**
     * Handles the event where the user wants to get the previous input from input history.
     */
    private void handleGetPreviousInput() {
        // store the current input and get prev input
        String currentInput = commandTextField.getText();      
        String prevInput = inputHistory.prevStep(currentInput);
        
        // show user the prev input
        commandTextField.setText(prevInput);
    }

    /**
     * Returns whether the user wants to get the previous input from input history.
     * @param keyCode the key the user pressed to trigger the event
     * @return boolean representing the above
     */
    private boolean checkIfWantPrevInput(KeyCode keyCode) {
        return keyCode == KeyCode.UP;
    }

    @FXML
    private void handleCommandInputEntered() {
        //Take a copy of the command text
        previousCommandTest = commandTextField.getText();
        
        // first push back all 'next' commands into 'prev' command       
        // immediately add it to the history of command inputs
        inputHistory.updateInputHistory(previousCommandTest); 

        /* We assume the command is correct. If it is incorrect, the command box will be changed accordingly
         * in the event handling code {@link #handleIncorrectCommandAttempted}
         */
        setStyleToIndicateCorrectCommand();
        mostRecentResult = logic.execute(previousCommandTest);
        commandTextField.setText("");
        resultDisplay.postMessage(mostRecentResult.feedbackToUser);
        logger.info("Result: " + mostRecentResult.feedbackToUser);
    }

    @Subscribe
    private void handleIncorrectCommandAttempted(IncorrectCommandAttemptedEvent event){
        logger.info(LogsCenter.getEventHandlingLogMessage(event,"Invalid command: " + previousCommandTest));
        setStyleToIndicateIncorrectCommand();
        restoreCommandText();
    }

    /**
     * Restores the command box text to the previously entered command
     */
    private void restoreCommandText() {
        commandTextField.setText(previousCommandTest);
    }

    /**
     * Sets the command box style to indicate a correct command.
     */
    private void setStyleToIndicateCorrectCommand() {
        commandTextField.getStyleClass().remove("error");
    }
    
    /**
     * Sets the command box style to indicate an error
     */
    private void setStyleToIndicateIncorrectCommand() {
        commandTextField.getStyleClass().add("error");
    }
    


}
