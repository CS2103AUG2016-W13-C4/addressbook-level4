package guitests.guihandles;

import guitests.GuiRobot;
import javafx.stage.Stage;

/**
 * A handle to the Command Box in the GUI.
 */
public class CommandBoxHandle extends GuiHandle{

    private static final String COMMAND_INPUT_FIELD_ID = "#commandTextField";

    public CommandBoxHandle(GuiRobot guiRobot, Stage primaryStage, String stageTitle) {
        super(guiRobot, primaryStage, stageTitle);
    }

    public void enterCommand(String command) {
        setTextField(COMMAND_INPUT_FIELD_ID, command);
    }

    public String getCommandInput() {
        return getTextFieldText(COMMAND_INPUT_FIELD_ID);
    }

    /**
     * Enters the given command in the Command Box and presses enter.
     */
    public void runCommand(String command) {
        enterCommand(command);
        pressEnter();
        guiRobot.sleep(200); //Give time for the command to take effect
    }
    
    /**
     * Retrieves the previous input entered before the current state in the Command Box.
     */
    public void getPreviousInput() {
        pressUpArrowKey(COMMAND_INPUT_FIELD_ID);
        guiRobot.sleep(200);
    }
    
    /**
     * Retrieves the next input entered after the current state in the Command Box.
     */
    public void getNextInput() {
        pressDownArrowKey(COMMAND_INPUT_FIELD_ID);
        guiRobot.sleep(200);
    }

    public HelpWindowHandle runHelpCommand() {
        enterCommand("help");
        pressEnter();
        return new HelpWindowHandle(guiRobot, primaryStage);
    }
    
    public HelpWindowHandle runHelpCommandWithArgs(String args) {
        enterCommand("help " + args);
        pressEnter();
        return new HelpWindowHandle(guiRobot, primaryStage);
    }
}
