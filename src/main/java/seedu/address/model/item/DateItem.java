package seedu.address.model.item;

import java.util.Date;

public abstract class DateItem implements Comparable<DateItem> {
    protected Name itemName;
    protected Date dateToOrderBy;
    
    /**
     * Orders the Date objects in ascending order  
     */
    @Override
    public int compareTo(DateItem otherDate) {
        if (dateToOrderBy.equals(otherDate.dateToOrderBy)){
            return itemName.name.compareTo(otherDate.itemName.name);
        }
        return dateToOrderBy.compareTo(otherDate.dateToOrderBy);
    }
    
    public Name getName() {      
        return itemName;
    }
    
    public Date getDateToOrderBy() {
        return dateToOrderBy;
    }
}
