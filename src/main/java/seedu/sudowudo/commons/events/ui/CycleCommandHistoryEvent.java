package seedu.sudowudo.commons.events.ui;

import seedu.sudowudo.commons.events.BaseEvent;

//@@author A0144750J
/**
 * Indicates a request to display an archived command in the command box
 */
public class CycleCommandHistoryEvent extends BaseEvent {
    
    public String userIput;

    public CycleCommandHistoryEvent(String userInput) {
        this.userIput = userInput;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
