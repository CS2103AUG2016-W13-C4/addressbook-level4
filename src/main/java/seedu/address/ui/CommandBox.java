package seedu.address.ui;

import com.google.common.eventbus.Subscribe;
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
import seedu.address.history.History;
import seedu.address.commons.core.LogsCenter;

import java.util.logging.Logger;

public class CommandBox extends UiPart {
    private final Logger logger = LogsCenter.getLogger(CommandBox.class);
    private static final String FXML = "CommandBox.fxml";

    private AnchorPane placeHolderPane;
    private AnchorPane commandPane;
    private ResultDisplay resultDisplay;
    String previousCommandTest;

    private Logic logic;
    private History history;

    @FXML
    private TextField commandTextField;
    private CommandResult mostRecentResult;

    public static CommandBox load(Stage primaryStage, AnchorPane commandBoxPlaceholder,
            ResultDisplay resultDisplay, Logic logic, History history) {
        CommandBox commandBox = UiPartLoader.loadUiPart(primaryStage, commandBoxPlaceholder, new CommandBox());
        commandBox.configure(resultDisplay, logic, history);
        commandBox.addToPlaceholder();
        return commandBox;
    }

    public void configure(ResultDisplay resultDisplay, Logic logic, History history) {
        this.resultDisplay = resultDisplay;
        this.logic = logic;
        this.history = history;
        registerAsAnEventHandler(this);
    }

    private void addToPlaceholder() {
        SplitPane.setResizableWithParent(placeHolderPane, false);
        placeHolderPane.getChildren().add(commandTextField);
        FxViewUtil.applyAnchorBoundaryParameters(commandPane, 0.0, 0.0, 0.0, 0.0);
        FxViewUtil.applyAnchorBoundaryParameters(commandTextField, 0.0, 0.0, 0.0, 0.0);
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
     * Attempt to parse a possibly incomplete command in the command box and display the command format matching that.
     */
    @FXML
    private void handleCommandInputChanged(KeyEvent event){
        KeyCode keyCode = event.getCode();
        
        // handle event for arrow keys
        if (keyCode.isArrowKey()){
            handleArrowKeyEvent(keyCode);
        }
        
        // do not update tooltip if user clears textfield
        if (commandTextField.getText().equals("")) return;
               
        // only update if user uses a backspace or enters a valid character
        if (keyCode != KeyCode.BACK_SPACE && !keyCode.isDigitKey() && !keyCode.isLetterKey()) return;
        
        String toDisplay = logic.decideToolTip(commandTextField.getText());
        resultDisplay.postMessage(toDisplay);
    }

    
    private void handleArrowKeyEvent(KeyCode keyCode) {
        boolean wantPrevious = keyCode == KeyCode.UP || keyCode == KeyCode.KP_UP || keyCode == KeyCode.LEFT || keyCode == KeyCode.KP_LEFT;
        boolean wantNext = !wantPrevious;
        
        // if attempt to get next command while at latest command input or prev while at earliest, return
        if ((history.isLatestCommand() && wantNext) || (history.isEarliestCommand() && wantPrevious)) {
            return;
        }
        
        String currentInput = commandTextField.getText();
        
        // handle differently depending on up or left arrow
        if (wantPrevious){
            // store the current input into the next first
            history.pushNextCommandInput(currentInput);
            
            // get a previous command input and replace current input
            commandTextField.setText(history.popPrevCommandInput());
        }
        
        // or down or right arrow
        else {
            // store the current input into the prev first
            history.pushPrevCommandInput(currentInput);
            
            // get a next command input and replace current input
            commandTextField.setText(history.popNextCommandInput());
        }
        
        String currentInputShown = commandTextField.getText();
        // positions the caret at the end of the string for easy edit
        commandTextField.positionCaret(currentInputShown.length());
    }

    @FXML
    private void handleCommandInputEntered() {
        //Take a copy of the command text
        previousCommandTest = commandTextField.getText();
       
        // immediately add it to the history of command inputs
        history.updateInputHistory(previousCommandTest); 

        /* We assume the command is correct. If it is incorrect, the command box will be changed accordingly
         * in the event handling code {@link #handleIncorrectCommandAttempted}
         */
        setStyleToIndicateCorrectCommand();
        mostRecentResult = logic.execute(previousCommandTest);
        resultDisplay.postMessage(mostRecentResult.feedbackToUser);
        logger.info("Result: " + mostRecentResult.feedbackToUser);
    }


    /**
     * Sets the command box style to indicate a correct command.
     */
    private void setStyleToIndicateCorrectCommand() {
        commandTextField.getStyleClass().remove("error");
        commandTextField.setText("");
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
     * Sets the command box style to indicate an error
     */
    private void setStyleToIndicateIncorrectCommand() {
        commandTextField.getStyleClass().add("error");
    }

}
