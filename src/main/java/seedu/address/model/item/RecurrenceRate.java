package seedu.address.model.item;

import java.util.HashMap;

import seedu.address.commons.exceptions.IllegalValueException;

public class RecurrenceRate {
    
    private static final String STRING_CONSTANT_ONE = "1";

    public static final String MESSAGE_VALUE_CONSTRAINTS = "RECURRING_INTERVAL Format : repeat every [RATE] TIME_PERIOD\n"
            + "RATE must be a positive integer and TIME_PERIOD must be in one of the formats: "
            + "\"hour(s)\", \"day(s)\", \"week(s)\", \"month(s)\", \"year(s)\", "
            + "or days of the week such as \"Monday\", \"Wed\"\n"
            + "For example: \"repeat every 3 days\", \"repeat every week\", \"repeat every Wed\"";
    
    public static final HashMap<String, TimePeriod> INPUT_TO_TIME_PERIOD_MAP = new HashMap<String, TimePeriod>() {{
        put("hour", TimePeriod.HOUR);
        put("hours", TimePeriod.HOUR);
        put("day", TimePeriod.DAY);
        put("days", TimePeriod.DAY);
        put("week", TimePeriod.WEEK);
        put("weeks", TimePeriod.WEEK);
        put("month", TimePeriod.MONTH);
        put("months", TimePeriod.MONTH);
        put("year", TimePeriod.YEAR);
        put("years", TimePeriod.YEAR);
        put("mon", TimePeriod.MONDAY);
        put("monday", TimePeriod.MONDAY);
        put("tues", TimePeriod.TUESDAY);
        put("tuesday", TimePeriod.TUESDAY);
        put("wed", TimePeriod.WEDNESDAY);
        put("wednesday", TimePeriod.WEDNESDAY);
        put("thur", TimePeriod.THURSDAY);
        put("thurs", TimePeriod.THURSDAY);
        put("thursday", TimePeriod.THURSDAY);
        put("fri", TimePeriod.FRIDAY);
        put("friday", TimePeriod.FRIDAY);
        put("sat", TimePeriod.SATURDAY);
        put("saturday", TimePeriod.SATURDAY);
        put("sun", TimePeriod.SUNDAY);
        put("sunday", TimePeriod.SUNDAY);
    }};
    
    public Integer rate;
    public TimePeriod timePeriod;

    /**
     * Validates given rate and timePeriod.
     *
     * @throws IllegalValueException if either values are invalid.
     */
    public RecurrenceRate(String rate, String timePeriod) throws IllegalValueException {
        assert timePeriod != null && rate != null;
        
        if (!isValidTimePeriod(timePeriod.trim())) {
            throw new IllegalValueException(MESSAGE_VALUE_CONSTRAINTS);
        }
        
        if (Integer.valueOf(rate) <= 0) {   
            throw new IllegalValueException(MESSAGE_VALUE_CONSTRAINTS);
        }
        
        try {
            this.rate = Integer.valueOf(rate);
        } catch (NumberFormatException nfe) {
            throw new IllegalValueException(MESSAGE_VALUE_CONSTRAINTS);
        }
    }
    
    public RecurrenceRate(String timePeriod) throws IllegalValueException {
        this(STRING_CONSTANT_ONE, timePeriod);
    }

    /**
     * Validates user input and converts it into TimePeriod.
     *
     * @return true if user input is recognised as a valid TimePeriod.
     */
    private boolean isValidTimePeriod(String timePeriod) {
        for (String key : INPUT_TO_TIME_PERIOD_MAP.keySet()) {
            if (key.equals(timePeriod.toLowerCase())) {
                this.timePeriod = INPUT_TO_TIME_PERIOD_MAP.get(key);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return Integer.toString(rate) + " " + timePeriod.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof RecurrenceRate // instanceof handles nulls
                && this.rate.equals(((RecurrenceRate) other).rate) // state check
                && this.timePeriod.equals(((RecurrenceRate) other).timePeriod));
    }

    @Override
    public int hashCode() {
        return rate.hashCode();
    }
}