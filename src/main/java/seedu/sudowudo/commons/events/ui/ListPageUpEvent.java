package seedu.sudowudo.commons.events.ui;

import seedu.sudowudo.commons.events.BaseEvent;

//@@author A0144750J
/**
 * Indicates a request to jump up 5 places
 */
public class ListPageUpEvent extends BaseEvent {

    public ListPageUpEvent() {

    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}