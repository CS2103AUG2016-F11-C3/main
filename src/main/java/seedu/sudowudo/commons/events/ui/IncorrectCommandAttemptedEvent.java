package seedu.sudowudo.commons.events.ui;

import seedu.sudowudo.commons.events.BaseEvent;
import seedu.sudowudo.logic.commands.Command;

/**
 * Indicates an attempt to execute an incorrect command
 */
public class IncorrectCommandAttemptedEvent extends BaseEvent {

    public IncorrectCommandAttemptedEvent(Command command) {}

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

}
