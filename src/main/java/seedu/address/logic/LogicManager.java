package seedu.address.logic;

import javafx.collections.ObservableList;
import seedu.address.commons.core.ComponentManager;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.Messages;
import seedu.address.history.UndoableCommandHistory;
import seedu.address.history.UndoableCommandHistoryManager;
import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.UndoableCommand;
import seedu.address.logic.parser.CommandParser;
import seedu.address.model.Model;
import seedu.address.model.item.ReadOnlyTask;
import seedu.address.storage.Storage;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.logging.Logger;

/**
 * The main LogicManager of the app.
 */
public class LogicManager extends ComponentManager implements Logic {
    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final UndoableCommandHistory history;
    private final CommandParser parser;

    public LogicManager(Model model, Storage storage, UndoableCommandHistory history) {
        this.model = model;
        this.history = history;
        this.parser = new CommandParser();
    }

    @Override
    public CommandResult execute(String commandText) {
        logger.info("----------------[USER COMMAND][" + commandText + "]");
        Command command = parser.parseCommand(commandText);    
        command.setData(model, history); 
        return command.execute();
    }

    @Override
    public ObservableList<ReadOnlyTask> getFilteredUndoneTaskList() {
        return model.getFilteredUndoneTaskList();
    }
    
    @Override
    public ObservableList<ReadOnlyTask> getFilteredDoneTaskList() {
        return model.getFilteredDoneTaskList();
    }
    
    /**
     * Generates the tool tip for the current user input.
     * 
     * @param commandText the user input string
     * @return the tooltip that fits the user input string
     */
    @Override
    public String generateToolTip(String commandText){
        assert commandText != null; 
        
        boolean viewingDoneList = model.isCurrentListDoneList();
        return parser.parseForTooltip(commandText, viewingDoneList);    
    }
}
