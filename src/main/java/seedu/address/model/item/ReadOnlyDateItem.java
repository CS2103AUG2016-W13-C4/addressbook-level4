package seedu.address.model.item;

import java.util.Date;

public interface ReadOnlyDateItem {
    Name getName();
    Date getDateToOrderBy(); 
    
    /**
     * Returns true if both have the same state. (interfaces cannot override .equals)
     */
    default boolean isSameStateAs(ReadOnlyFloatingTask other) {
        return other == this // short circuit if same object
                || (other != null // this is first to avoid NPE below
                && other.getName().equals(this.getName()) // state checks here onwards
                && other.getPriorityValue().equals(this.getDateToOrderBy()));
    }
    
    /**
     * Formats the floating task as text, showing all details.
     */
    default String getAsText() {
        final StringBuilder builder = new StringBuilder();
        builder.append(getName())
                .append(" Date: ")
                .append(getDateToOrderBy());
        return builder.toString();
    }
}
