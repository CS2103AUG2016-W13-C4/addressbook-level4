package seedu.address.model.item;

import java.util.Date;

public abstract class DateItem implements Comparable<DateItem> {
    protected Date mainDateToOrderBy;
    
    /**
     * Orders the Date objects in ascending order  
     */
    public int compareTo(Date otherDate) {
        return mainDateToOrderBy.compareTo(otherDate);
    }
}
