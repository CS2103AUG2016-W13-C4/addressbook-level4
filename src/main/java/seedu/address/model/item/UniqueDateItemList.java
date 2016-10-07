package seedu.address.model.item;

import java.util.Iterator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.address.commons.exceptions.DuplicateDataException;

public class UniqueDateItemList implements Iterable<DateItem> {
    /**
     * Signals that an operation would have violated the 'no duplicates' property of the list.
     */
    public static class DuplicateDateItemException extends DuplicateDataException {
        protected DuplicateDateItemException() {
            super("Operation would result in duplicate floating tasks");
        }
    }

    /**
     * Signals that an operation targeting a specified person in the list would fail because
     * there is no such matching person in the list.
     */
    public static class DateItemNotFoundException extends Exception {}

    private final ObservableList<DateItem> internalList = FXCollections.observableArrayList();

    /**
     * Constructs empty PersonList.
     */
    public UniqueDateItemList() {}

    /**
     * Returns true if the list contains an equivalent person as the given argument.
     */
    public boolean contains(ReadOnlyDateItem toCheck) {
        assert toCheck != null;
        return internalList.contains(toCheck);
    }

    /**
     * Adds a person to the list.
     *
     * @throws DuplicatePersonException if the person to add is a duplicate of an existing person in the list.
     */
    public void add(DateItem toAdd) throws DuplicateDateItemException {
        assert toAdd != null;
        /*
        if (contains(toAdd)) {
            throw new DuplicateDateItemException();
        }
        */
        internalList.add(toAdd);
    }

    /**
     * Removes the equivalent person from the list.
     *
     * @throws PersonNotFoundException if no such person could be found in the list.
     */
    public boolean remove(ReadOnlyDateItem toRemove) throws DateItemNotFoundException {
        assert toRemove != null;
        final boolean personFoundAndDeleted = internalList.remove(toRemove);
        if (!personFoundAndDeleted) {
            throw new DateItemNotFoundException();
        }
        return personFoundAndDeleted;
    }

    public ObservableList<DateItem> getInternalList() {
        return internalList;
    }

    @Override
    public Iterator<DateItem> iterator() {
        return internalList.iterator();
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof UniqueDateItemList // instanceof handles nulls
                && this.internalList.equals(
                ((UniqueDateItemList) other).internalList));
    }

    @Override
    public int hashCode() {
        return internalList.hashCode();
    }
}
