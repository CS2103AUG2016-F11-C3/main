//@@author A0144750J
package seedu.sudowudo.commons.events.ui;

import seedu.sudowudo.commons.events.BaseEvent;

/**
 * Indicates a request to cycle to the previous command
 */
public class PreviousCommandEvent extends BaseEvent {

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
