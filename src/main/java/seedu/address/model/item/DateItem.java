package seedu.address.model.item;

import java.util.Date;

public abstract class DateItem implements Comparable<DateItem> {
    protected Name itemName;
    protected Date dateToOrderBy;
    
    /**
     * Orders the Date objects in ascending order  
     */
    public int compareTo(Date otherDate) {
        return dateToOrderBy.compareTo(otherDate);
    }
    
    public Name getName() {      
        return itemName;
    }
    
    public Date getDateToOrderBy() {
        return dateToOrderBy;
    }
}
