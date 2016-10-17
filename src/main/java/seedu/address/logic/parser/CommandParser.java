package seedu.address.logic.parser;

import seedu.address.logic.commands.*;
import seedu.address.model.item.DateTime;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.exceptions.IllegalValueException;

import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.address.commons.core.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses user input.
 */
public class CommandParser {
    
    private final Logger logger = LogsCenter.getLogger(CommandParser.class);

    /**
     * Used for initial separation of command word and args.
     */
    private static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static final Pattern ITEM_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    private static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace
        
    private static final Pattern RECURRENCE_RATE_ARGS_FORMAT = Pattern.compile("(?<rate>\\d+)?(?<timePeriod>.*?)");
    
    private static final String REGEX_OPEN_BRACE = "(";
    private static final String REGEX_CASE_IGNORE = "?i:";
    private static final String REGEX_CLOSE_BRACE = ")";
    private static final String REGEX_GREEDY_SELECT = ".*?";
    private static final String REGEX_NAME = "?<taskName>.*?";

    private static final String REGEX_ADDITIONAL_KEYWORD = "(?:"
            +"(?: from )"
            +"|(?: at )"
            +"|(?: start )"
            +"|(?: by )"
            +"|(?: to )"
            +"|(?: end )"
            +")";

    private static final String REGEX_FIRST_DATE = "(?:"
            +"(?: from (?<startDateFormatOne>.*?))"
            +"|(?: at (?<startDateFormatTwo>.*?))"
            +"|(?: start (?<startDateFormatThree>.*?))"
            +"|(?: by (?<endDateFormatOne>.*?))"
            +"|(?: to (?<endDateFormatTwo>.*?))"
            +"|(?: end (?<endDateFormatThree>.*?))"
            +")";
    private static final String REGEX_SECOND_DATE = "(?:"
            +"(?: from (?<startDateFormatFour>.*?))"
            +"|(?: at (?<startDateFormatFive>.*?))"
            +"|(?: start (?<startDateFormatSix>.*?))"
            +"|(?: by (?<endDateFormatFour>.*?))"
            +"|(?: to (?<endDateFormatFive>.*?))"
            +"|(?: end (?<endDateFormatSix>.*?))"
            +")";
    private static final String REGEX_RECURRENCE_AND_PRIORITY = "(?: repeat every (?<recurrenceRate>.*?))?"
            +"(?: -(?<priority>.*?))?";
    
    private static final String REGEX_RECURRENCE_RESET_AND_PRIORITY = "(?: repeat every (?<recurrenceRate>.*?))?"
			//+"(?: -reset (?<resetField>\\S+))?"
    		+"(?: -reset (?<resetField>.*?))?"
			+"(?: -(?<priority>.*?))?";

    private static final String REGEX_OPEN_BRACE_CASE_IGNORE_NAME = REGEX_OPEN_BRACE + REGEX_CASE_IGNORE + REGEX_OPEN_BRACE + REGEX_NAME;
    private static final String REGEX_KEYWORD_GREEDY_SELECT = REGEX_ADDITIONAL_KEYWORD + REGEX_GREEDY_SELECT;
    private static final String REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE = REGEX_RECURRENCE_AND_PRIORITY + REGEX_CLOSE_BRACE;
    private static final String REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE = REGEX_RECURRENCE_RESET_AND_PRIORITY + REGEX_CLOSE_BRACE;

    public CommandParser() {}

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");
        switch (commandWord) {

        case AddCommand.COMMAND_WORD:
            return prepareAdd(arguments);

        case SelectCommand.COMMAND_WORD:
            return prepareSelect(arguments);

        case DeleteCommand.COMMAND_WORD:
            return prepareDelete(arguments);
            
        case DoneCommand.COMMAND_WORD:
            return prepareDone(arguments);

        case ClearCommand.COMMAND_WORD:
            return new ClearCommand();

        case FindCommand.COMMAND_WORD:
            return prepareFind(arguments);

        case ListCommand.COMMAND_WORD:
            return prepareList(arguments);

        case ExitCommand.COMMAND_WORD:
            return new ExitCommand();

        case HelpCommand.COMMAND_WORD:
            return new HelpCommand();

        case EditCommand.COMMAND_WORD:
            return prepareEdit(arguments);
            
        case UndoCommand.COMMAND_WORD:
            return new UndoCommand();
        
        case RedoCommand.COMMAND_WORD:
            return new RedoCommand();

        default:
            return prepareAdd(commandWord + arguments);
        }
    }

	/**
     * Parses arguments in the context of the add person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        
        String argsTrimmed = args.trim();
        
        if(argsTrimmed.isEmpty()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        
        String taskName = null;
        String startDate = null;
        String endDate = null;
        String rate = null;
        String timePeriod = null;
        String priority = null;  
        
        Pattern pattern = Pattern.compile(REGEX_ADDITIONAL_KEYWORD);
        Matcher matcher = pattern.matcher(argsTrimmed);

        int numberOfKeywords = 0;
        while(matcher.find()){
            numberOfKeywords++;
        }
        logger.info("Number of keywords in \"" + argsTrimmed + "\" = " + numberOfKeywords);
        
        assert numberOfKeywords >= 0;
        try {
            if (numberOfKeywords == 0) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE
                        + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
            } else if (numberOfKeywords == 1) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE +
                        REGEX_FIRST_DATE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
            
                if (startDate != null && !DateTime.isValidDate(startDate) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT + 
                            REGEX_CLOSE_BRACE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);
                } 
            } else if (numberOfKeywords == 2) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE +
                        REGEX_FIRST_DATE + REGEX_SECOND_DATE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
                
                boolean isValidEndDate = true;
                
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    isValidEndDate = false;
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT +
                            REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);

                    validateMatcherMatches(matcher);
                    
                    startDate = validateStartDateFormatsOneToThree(matcher); 
                    if (startDate == null) {
                        endDate = validateEndDateFormatsOneToThree(matcher); 
                    }
                }
                
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT + 
                            REGEX_KEYWORD_GREEDY_SELECT + REGEX_CLOSE_BRACE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);
                }
                
                if (isValidEndDate) {
                    endDate = validateEndDateFormatsFourToSix(matcher); 
                }
            } else if (numberOfKeywords > 2) {
                int numberOfAdditionalKeywords = numberOfKeywords - 2;
                String startOfRegex = REGEX_OPEN_BRACE_CASE_IGNORE_NAME;
                for (int i = 0; i < numberOfAdditionalKeywords; i++) {
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                }
                String regexCopy = startOfRegex;
                regexCopy += REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_SECOND_DATE + 
                        REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE;
               
                pattern = Pattern.compile(regexCopy);
                matcher = pattern.matcher(argsTrimmed);

                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
                
                boolean isValidEndDate = true;

                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    isValidEndDate = false;
                    startDate = null;
                    endDate = null;
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                    regexCopy = startOfRegex;
                    regexCopy += REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE;
                    pattern = Pattern.compile(regexCopy);
                    matcher = pattern.matcher(argsTrimmed);

                    validateMatcherMatches(matcher);
                    
                    startDate = validateStartDateFormatsOneToThree(matcher); 
                    if (startDate == null) {
                        endDate = validateEndDateFormatsOneToThree(matcher); 
                    }
                }
                
                // second keyword is part of the name
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                    regexCopy = startOfRegex;
                    regexCopy += REGEX_CLOSE_BRACE + REGEX_RECURRENCE_PRIORITY_CLOSE_BRACE;
                    pattern = Pattern.compile(regexCopy);
                    matcher = pattern.matcher(argsTrimmed);
                }
                
                if (isValidEndDate) {
                    endDate = validateEndDateFormatsFourToSix(matcher); 
                }
            } 
            
            validateMatcherMatches(matcher);
            
            assert matcher.group("taskName") != null;
            taskName = matcher.group("taskName").trim();

            if (matcher.group("recurrenceRate") != null) {
                final Matcher recurrenceMatcher = validateRecurrenceMatcher(matcher); 
            
                if (recurrenceMatcher.group("rate") != null) {
                    rate = recurrenceMatcher.group("rate").trim();
                }
            
                assert recurrenceMatcher.group("timePeriod") != null;
                timePeriod = recurrenceMatcher.group("timePeriod").trim();
            }
        
            priority = assignPriority(matcher);
            return new AddCommand(taskName, startDate, endDate, rate, timePeriod, priority);
            
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE + "\n" + ive.getMessage()));
        }
    }

    private String validateEndDateFormatsFourToSix(Matcher matcher) {
        if (matcher.group("endDateFormatFour") != null) {
            return matcher.group("endDateFormatFour").trim(); 
        } else if (matcher.group("endDateFormatFive") != null) {
            return matcher.group("endDateFormatFive").trim();
        } else if (matcher.group("endDateFormatSix") != null) {
            return matcher.group("endDateFormatSix").trim();
        } else {
            return null;
        }
    }

    private String validateEndDateFormatsOneToThree(Matcher matcher) {
        if (matcher.group("endDateFormatOne") != null) {
            return matcher.group("endDateFormatOne").trim(); 
        } else if (matcher.group("endDateFormatTwo") != null) {
            return matcher.group("endDateFormatTwo").trim();
        } else if (matcher.group("endDateFormatThree") != null) {
            return matcher.group("endDateFormatThree").trim();
        } else {
            return null;
        }
    }

    private String validateStartDateFormatsOneToThree(Matcher matcher) {
        if (matcher.group("startDateFormatOne") != null) {
            return matcher.group("startDateFormatOne").trim();
        } else if (matcher.group("startDateFormatTwo") != null) {
            return matcher.group("startDateFormatTwo").trim();
        } else if (matcher.group("startDateFormatThree") != null) {
            return matcher.group("startDateFormatThree").trim();
        } else {
            return null;
        }
    }

    // TODO: Update this
    private void validateMatcherMatches(Matcher matcher) throws IllegalValueException {
        if (!matcher.matches()) {
            throw new IllegalValueException("");
        }
    }

    private String assignPriority(Matcher matcher) {
        String priority;
        if (matcher.group("priority") != null) {
            priority = matcher.group("priority").trim();
        } else {
            priority = "medium";
        }
        return priority;
    }

    //TODO: To update this
    private Matcher validateRecurrenceMatcher(Matcher matcher) throws IllegalValueException {
        String recurrenceString = matcher.group("recurrenceRate");
        final Matcher recurrenceMatcher = RECURRENCE_RATE_ARGS_FORMAT.matcher(recurrenceString);
            
        if (!recurrenceMatcher.matches()) {
            throw new IllegalValueException("");
        }
        
        return recurrenceMatcher;
    }

    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" t/", "").split(" t/"));
        return new HashSet<>(tagStrings);
    }
    
    /**
     * Parses arguments in the context of the edit task command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareEdit(String args) {
		
    	int index = 0;
	 
   	 	args = args.trim();
   	 	System.out.println(args);
   	 
   	 	String[] parts = args.split(" ");
   	 	String indexNum = parts[0];

   	 	if(parts.length == 1){
   	 		return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
   	 	}
   	 
   	 	index = Integer.parseInt(indexNum);
   	 
   	 	String argsTrimmed = " " + args.substring(2);
        
        String taskName = null;
        String startDate = null;
        String endDate = null;
        String rate = null;
        String timePeriod = null;
        String priority = null;  
        String resetField = null;

        Pattern pattern = Pattern.compile(REGEX_ADDITIONAL_KEYWORD);
        Matcher matcher = pattern.matcher(argsTrimmed);

        int numberOfKeywords = 0;
        while(matcher.find()){
            numberOfKeywords++;
        }
        logger.info("Number of keywords in \"" + argsTrimmed + "\" = " + numberOfKeywords);
        
        assert numberOfKeywords >= 0;
        try {
            if (numberOfKeywords == 0) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE 
                        + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
            } else if (numberOfKeywords == 1) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE +
                        REGEX_FIRST_DATE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
            
                if (startDate != null && !DateTime.isValidDate(startDate) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT + 
                            REGEX_CLOSE_BRACE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);
                } 
            } else if (numberOfKeywords == 2) {
                pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_CLOSE_BRACE +
                        REGEX_FIRST_DATE + REGEX_SECOND_DATE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                matcher = pattern.matcher(argsTrimmed);
                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
                
                boolean isValidEndDate = true;
                
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    isValidEndDate = false;
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT +
                            REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);

                    validateMatcherMatches(matcher);
                    
                    startDate = validateStartDateFormatsOneToThree(matcher); 
                    if (startDate == null) {
                        endDate = validateEndDateFormatsOneToThree(matcher); 
                    }
                }
                
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    pattern = Pattern.compile(REGEX_OPEN_BRACE_CASE_IGNORE_NAME + REGEX_KEYWORD_GREEDY_SELECT + 
                            REGEX_KEYWORD_GREEDY_SELECT + REGEX_CLOSE_BRACE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE);
                    matcher = pattern.matcher(argsTrimmed);
                }
                
                if (isValidEndDate) {
                    endDate = validateEndDateFormatsFourToSix(matcher); 
                }
            } else if (numberOfKeywords > 2) {
                int numberOfAdditionalKeywords = numberOfKeywords - 2;
                String startOfRegex = REGEX_OPEN_BRACE_CASE_IGNORE_NAME;
                for (int i = 0; i < numberOfAdditionalKeywords; i++) {
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                }
                String regexCopy = startOfRegex;
                regexCopy += REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_SECOND_DATE + 
                		REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE;
               
                pattern = Pattern.compile(regexCopy);
                matcher = pattern.matcher(argsTrimmed);

                validateMatcherMatches(matcher);
            
                startDate = validateStartDateFormatsOneToThree(matcher); 
                if (startDate == null) {
                    endDate = validateEndDateFormatsOneToThree(matcher); 
                }
                
                boolean isValidEndDate = true;

                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    isValidEndDate = false;
                    startDate = null;
                    endDate = null;
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                    regexCopy = startOfRegex;
                    regexCopy += REGEX_CLOSE_BRACE + REGEX_FIRST_DATE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE;
                    pattern = Pattern.compile(regexCopy);
                    matcher = pattern.matcher(argsTrimmed);

                    validateMatcherMatches(matcher);
                    
                    startDate = validateStartDateFormatsOneToThree(matcher); 
                    if (startDate == null) {
                        endDate = validateEndDateFormatsOneToThree(matcher); 
                    }
                }
                
                // second keyword is part of the name
                if ((startDate != null && !DateTime.isValidDate(startDate)) || 
                        endDate != null && !DateTime.isValidDate(endDate)) {
                    startDate = null;
                    endDate = null;
                    startOfRegex += REGEX_KEYWORD_GREEDY_SELECT;
                    regexCopy = startOfRegex;
                    regexCopy += REGEX_CLOSE_BRACE + REGEX_RECURRENCE_RESET_PRIORITY_CLOSE_BRACE;
                    pattern = Pattern.compile(regexCopy);
                    matcher = pattern.matcher(argsTrimmed);
                }
                
                if (isValidEndDate) {
                    endDate = validateEndDateFormatsFourToSix(matcher); 
                }
            } 
            
            validateMatcherMatches(matcher);
            
            if(matcher.group("taskName") != null){
            	taskName = matcher.group("taskName").trim();
            }
            
            if (matcher.group("recurrenceRate") != null) {
                final Matcher recurrenceMatcher = validateRecurrenceMatcher(matcher); 
            
                if (recurrenceMatcher.group("rate") != null) {
                    rate = recurrenceMatcher.group("rate").trim();
                }
            
                assert recurrenceMatcher.group("timePeriod") != null;
                timePeriod = recurrenceMatcher.group("timePeriod").trim();
            }
            
            if (matcher.group("resetField") != null) {
           	 	resetField = matcher.group("resetField");
            }
            
            if (matcher.group("priority") != null) {
                priority = matcher.group("priority").trim();
            }
            
            System.out.println("Taskname = " + taskName);
            System.out.println("StartDate = " + startDate);
            System.out.println("EndDate = " + endDate);
            System.out.println("Rate = " + rate);
            System.out.println("TimePeiod = " + timePeriod);
            System.out.println("Reset = " + resetField);
            System.out.println("Priority = " + priority);
            
            return new EditCommand(index, taskName, startDate, endDate, rate, timePeriod, priority, resetField);
            
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE + "\n" + ive.getMessage()));
        }
    }

    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args) {

        Optional<List<Integer>> indexes = parseIndexes(args);
        if(!indexes.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }

        return new DeleteCommand(indexes.get());
    }
    
    /**
     * Parses arguments in the context of the done person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDone(String args) {

        Optional<List<Integer>> indexes = parseIndexes(args);
        if(!indexes.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DoneCommand.MESSAGE_USAGE));
        }

        return new DoneCommand(indexes.get());
    }

    /**
     * Parses arguments in the context of the select person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareSelect(String args) {
        Optional<Integer> index = parseIndex(args);
        if(!index.isPresent()){
            return new IncorrectCommand(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, SelectCommand.MESSAGE_USAGE));
        }

        return new SelectCommand(index.get());
    }
    
    /**
     * Parses arguments in the context of the select person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareList(String args) {
        Boolean isListDoneCommand = false;
        
        if (args != null && args.trim().toLowerCase().equals("done")) {
            isListDoneCommand = true;
        }

        return new ListCommand(isListDoneCommand);
    }

    /**
     * Returns the specified index in the {@code command} IF a positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<Integer> parseIndex(String command) {
        final Matcher matcher = ITEM_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String index = matcher.group("targetIndex");
        if(!StringUtil.isUnsignedInteger(index)){
            return Optional.empty();
        }
        return Optional.of(Integer.parseInt(index));

    }
    
    /**
     * Returns the specified indexes in the {@code command} IF any positive unsigned integer is given as the index.
     *   Returns an {@code Optional.empty()} otherwise.
     */
    private Optional<List<Integer>> parseIndexes(String command) {
        final Matcher matcher = ITEM_INDEX_ARGS_FORMAT.matcher(command.trim());
        if (!matcher.matches()) {
            return Optional.empty();
        }

        String indexes = matcher.group("targetIndex");
        String[] indexesArray = indexes.split(" ");
        List<Integer> indexesToHandle = new ArrayList<Integer>();
        for (String index: indexesArray) {
            if (StringUtil.isUnsignedInteger(index)) {
                indexesToHandle.add(Integer.parseInt(index));
            }
        }
        if (indexesToHandle.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(indexesToHandle);

    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFind(String args) {
        final Matcher matcher = KEYWORDS_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    FindCommand.MESSAGE_USAGE));
        }

        // keywords delimited by whitespace
        final String[] keywords = matcher.group("keywords").split("\\s+");
        final Set<String> keywordSet = new HashSet<>(Arrays.asList(keywords));
        return new FindCommand(keywordSet);
    }
    
    /**
     * Parses an incomplete user input into a list of Strings (which are the command usages) to determine tooltips to show the user.
     * 
     * @param userInput user input string
     * @return a list of Strings for tooltips
     */
    public List<String> parseIncompleteCommand(String userInput) {
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());
        ArrayList<String> toolTips = new ArrayList<String>();
        if (!matcher.matches()) {
            //TODO: make this thing make sense
            toolTips.add(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
            return toolTips;
        }

        final String commandWord = matcher.group("commandWord");
        // reserve this maybe can use next time to match more precisely
        // final String arguments = matcher.group("arguments");
        updateMatchedCommands(toolTips, commandWord);
        if (toolTips.isEmpty()){
            toolTips.add(AddCommand.TOOL_TIP);
        }
        return toolTips;      
    }

    /**
     * Updates the list of toolTips by checking if the user's input command word is a substring of the actual command word.
     * @param toolTips list of tooltips
     * @param commandWord the user input command word
     */
    // TODO: apply software eng to this shit
    private void updateMatchedCommands(List<String> toolTips, final String commandWord) {
        if (StringUtil.isSubstring(AddCommand.COMMAND_WORD, commandWord)){
            toolTips.add(AddCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(SelectCommand.COMMAND_WORD, commandWord)){
            toolTips.add(SelectCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(DeleteCommand.COMMAND_WORD, commandWord)){
            toolTips.add(DeleteCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(ClearCommand.COMMAND_WORD, commandWord)){
            toolTips.add(ClearCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(FindCommand.COMMAND_WORD, commandWord)){
            toolTips.add(FindCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(ListCommand.COMMAND_WORD, commandWord)){
            toolTips.add(ListCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(ExitCommand.COMMAND_WORD, commandWord)){
            toolTips.add(ExitCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(HelpCommand.COMMAND_WORD, commandWord)){
            toolTips.add(HelpCommand.TOOL_TIP);
        }
        if (StringUtil.isSubstring(EditCommand.COMMAND_WORD, commandWord)){
            toolTips.add(EditCommand.TOOL_TIP);
        }
    }
}