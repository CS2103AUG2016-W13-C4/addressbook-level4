package seedu.address.logic.parser;

import java.util.Calendar;
import java.util.Date;
/*
 * Parses a string into a Date object
 * Give this a date string without name of task or recurrence interval
 * E.g. 8pm tmr, 7.20am next next wed, 7 July 6:30am, 920am this sunday
 * Current limitation: time must be in am or pm, with hour and minutes attached to this string (am/pm)
 * Supports date formats: relative days from today, days of week, month and day
 * Note: events can reuse this. split up string startDate startTime and endDate endTime, and parse each separately
 */
public class DateParser {
    
    private static final String[] days = {"tonight", "today",
                                          "tomorrow", "tmr",  
                                          "january", "february", "march", "april", "may", "june", 
                                          "july", "august", "september", "october", "november", "december",
                                          "monday", "tuesday", "wednesday", "thursday",
                                          "friday", "saturday", "sunday"};
    
    private static final String TYPE_TOMORROW = "tomorrow";
    private static final String TYPE_DAYS_OF_WEEK = "daysOfWeek";
    private static final String TYPE_MONTH = "month";
    private static final int MONTH_START_INDEX = 4;
    private static final int MONTH_END_INDEX = 15;
    private static final int DAYS_OF_WEEK_START_INDEX = 16;

    private Calendar cal;
    private String target;
    private String time;
    private String day;
    private boolean isMonth;
    private int monthIntValue;
    private boolean isDayOfWeek;
    private int weekNextValue;
    private String[] fields;
   
    public DateParser(String target) {
        this.target = target.toLowerCase().trim();
        fields = target.split(" ");
        time = "";
        day = "today";
        isMonth = false;
        monthIntValue = 0;
        isDayOfWeek = false;
        weekNextValue = 0;
        cal = Calendar.getInstance();
    }
    
    public Date parseDate() {

        initialiseTimeFromTargetString();
        initialiseDayFromTargetString();
      
        if (day.equals("tonight") || (day.equals("today"))) {
            setTimeFields(time);
        } else if (day.equals("tomorrow") || day.equals("tmr")) {
            setDayFields(day, 0, TYPE_TOMORROW);
            setTimeFields(time);
        } else if (isMonth) {
            setDayFields(day, monthIntValue, TYPE_MONTH);
            setTimeFields(time);
        } else if (isDayOfWeek) {
            setDayFields(day, weekNextValue, TYPE_DAYS_OF_WEEK);
            setTimeFields(time);
        }
        
        return cal.getTime();
    }
    
    private void setDayFields(String date, int value, String type) {
        if (type.equals("tomorrow")) {
            cal.add(Calendar.DATE, 1);
        } else if (type.equals("month")) {
            //e.g. July 7
            String[] dateFields = date.split(" ");
            int day = Integer.parseInt(dateFields[1]);
            int month = value;
            cal.set(Calendar.MONTH, month-1);
            cal.set(Calendar.DAY_OF_MONTH, day);
            //TODO: handle case where user refers to month in next year
            // Maybe even add functionality for user to specify year if wanted
        } else if (type.equals("daysOfWeek")) {
            // based on how many "next" user types, go through calendar
            System.out.println(value);
            int desiredDayOfWeek = 0;
            switch (date) {
                case "monday": desiredDayOfWeek = Calendar.MONDAY; break;
                case "tuesday": desiredDayOfWeek = Calendar.TUESDAY; break;
                case "wednesday": desiredDayOfWeek = Calendar.WEDNESDAY; break;
                case "thursday": desiredDayOfWeek = Calendar.THURSDAY; break;
                case "friday": desiredDayOfWeek = Calendar.FRIDAY; break;
                case "saturday": desiredDayOfWeek = Calendar.SATURDAY; break;
                case "sunday": desiredDayOfWeek = Calendar.SUNDAY; break;
            }
            // "this sunday"
            if (value == 0) {
                while (cal.get(Calendar.DAY_OF_WEEK)!= desiredDayOfWeek) {
                    cal.add(Calendar.DATE, 1);
                }
            } else {
                while (value-->0) {
                    cal.add(Calendar.DATE, 1);
                    while (cal.get(Calendar.DAY_OF_WEEK)!= desiredDayOfWeek) {
                        cal.add(Calendar.DATE, 1);
                    }
                } 
            }
        }
    }
    
    // takes in string like 7.30pm, 7am, 6:45pm
    // and sets the time field of calendar properly.
    private void setTimeFields(String time) {
        int hour = 0;
        int minute = 0;
        if (time.contains("pm")) {
            // check which format user gives time in
            // 7.30pm, 7:30pm, or 730pm       
            String timeDigits = time.substring(0, time.indexOf("pm"));
            // only hour specified
            if (timeDigits.length()<3) {
                hour += Integer.parseInt(timeDigits);
                if (hour!=12) {
                    hour += 12;
                }
            } else {
                if (timeDigits.contains(".") || timeDigits.contains(":")) {
                    hour += Integer.parseInt(timeDigits.substring(0, timeDigits.length()-3));
                    if (hour!=12) {
                        hour += 12;
                    }
                    minute = Integer.parseInt(timeDigits.substring(timeDigits.length()-2));
                    // 730
                } else {
                    String minuteString = timeDigits.substring(timeDigits.length()-2);
                    String hourString = timeDigits.substring(0, timeDigits.length()-2);
                    hour += Integer.parseInt(hourString);
                    if (hour!=12) {
                        hour += 12;
                    }
                    minute = Integer.parseInt(minuteString);
                }
            }
        } else if (time.contains("am")) {
            
            String timeDigits = time.substring(0, time.indexOf("am"));
            if (timeDigits.length()<3) {
                hour += Integer.parseInt(timeDigits);
                if (hour==12) {
                    hour = 0;
                }
            } else {
                if (timeDigits.contains(".") || timeDigits.contains(":")) {
                    hour += Integer.parseInt(timeDigits.substring(0, timeDigits.length()-3));
                    if (hour==12) {
                        hour = 0;
                    }
                    minute = Integer.parseInt(timeDigits.substring(timeDigits.length()-2));
                    // 730
                } else {
                    String minuteString = timeDigits.substring(timeDigits.length()-2);
                    String hourString = timeDigits.substring(0, timeDigits.length()-2);
                    hour += Integer.parseInt(hourString);
                    if (hour==12) {
                        hour = 0;
                    }
                    minute = Integer.parseInt(minuteString);
                }
            }
        } else {
            // something wrong
            // TODO: handle properly
            System.out.println("ERROR! haha");
        }
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);
    }
    
    private void initialiseTimeFromTargetString() {
        if (target.contains("pm") || target.contains("am")) {
            for (String f: fields) {
                if (f.contains("pm") || (f.contains("am"))) {
                    time = f;
                }
            }
        }
    }
    
    private void initialiseDayFromTargetString() {
        for (int i = 0; i < fields.length; i++) {
            for (int j = 0; j < days.length; j++) {
                String supportedWord = days[j];
                String inputWord = fields[i];
                if (supportedWord.contains(inputWord)) {
                    day = supportedWord;
                    // if its a month e.g. 7 July or July 7
                    // have to grab the day as well
                    // look for the day before/after this
                    if (j>=MONTH_START_INDEX && j<=MONTH_END_INDEX) {
                        isMonth = true;
                        monthIntValue = j-3;
                        if (i-1 >= 0 && fields[i-1].matches("^\\d+$")) {
                            day += " " + fields[i-1];
                        } else if (i+1 < fields.length && fields[i+1].matches("^\\d+$")) {
                            day += " " + fields[i+1];
                        } else {
                            // no day present
                            // TODO: show some invalid input thing
                            System.out.println("ERROR!");
                        }
                    } else if (j>=DAYS_OF_WEEK_START_INDEX) {
                        // if is days of the week e.g. wed, thurs
                        isDayOfWeek = true;
                        // check for "next" keyword
                        int k = i;
                        while (fields[k-1].equals("next")) {
                            weekNextValue++;
                            k--;
                        }
                    }
                    break;
                }
            }
        }
    }
}
