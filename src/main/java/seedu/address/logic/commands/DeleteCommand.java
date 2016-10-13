package seedu.address.logic.commands;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.UnmodifiableObservableList;
import seedu.address.model.item.ReadOnlyItem;
import seedu.address.model.item.UniqueItemList.ItemNotFoundException;

/**
 * Deletes a person identified using it's last displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the task identified by its event name or "
            + "the index number used in the last task listing.\n"
            + "Parameters: INDEX (must be a positive integer) or EVENT_NAME (must be exact)\n"
            + "Example 1: " + COMMAND_WORD + " 1\n"
            + "Example 2: " + COMMAND_WORD + " \"Be awesome\"";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Task: %1$s";

    public final int targetIndex;

    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }


    @Override
    public CommandResult execute() {

		UnmodifiableObservableList<ReadOnlyItem> lastShownList = model.getFilteredItemList();

        if (lastShownList.size() < targetIndex) {
            indicateAttemptToExecuteIncorrectCommand();
            return new CommandResult(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

		ReadOnlyItem itemToDelete = lastShownList.get(targetIndex - 1);

        try {
			model.deleteItem(itemToDelete);
		} catch (ItemNotFoundException pnfe) {
            assert false : "The target person cannot be missing";
        }

		return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, itemToDelete));
    }

}
