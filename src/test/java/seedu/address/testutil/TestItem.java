package seedu.address.testutil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Observable;

import seedu.address.logic.parser.DateTimeParser;
import seedu.address.model.item.Description;
import seedu.address.model.item.ReadOnlyItem;
import seedu.address.model.tag.UniqueTagList;

public class TestItem extends Observable implements ReadOnlyItem {


    private UniqueTagList tags;
    private Description description;
    private boolean isDone;
    private LocalDateTime startDate;
    private LocalDateTime endDate; 
    
    /**
     * Every field must be present and not null.
     */
    public TestItem() {
        tags = new UniqueTagList();
        this.isDone = false;
    }

    public void setDescription(Description description){
        this.description = description;
    }

    @Override
	public Description getDescription() {
        return description;
    }
    
    @Override
   	public LocalDateTime getStartDate() {
   		return startDate;
   	}

   	public void setStartDate(LocalDateTime startDate) {
   		this.startDate = startDate;
   	}
   	
   	@Override
   	public LocalDateTime getEndDate() {
   		return endDate;
   	}

   	public void setEndDate(LocalDateTime endDate) {
   		this.endDate = endDate;
   	}
    
    public void setDone(){
        this.isDone = true;
    }
    
    public void setUndone(){
        this.isDone = false;
    }
    
    @Override
	public boolean getIsDone() {
        return this.isDone;
    }

    @Override
    public UniqueTagList getTags() {
        return new UniqueTagList(tags);
    }

    /**
     * Replaces this Item's tags with the tags in the argument tag list.
     */
    public void setTags(UniqueTagList replacement) {
        tags.setTags(replacement);
    }
    
    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(description);
    }

    @Override
    public String toString() {
        return description.toString();
    }

    @Override
    public void setIsDone(boolean doneness) {
        this.isDone = doneness;
    }
    
    public String getAddCommand() {
    	assert this.getDescription() != null;
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String startTime = new String();
        String endTime = new String();
        if (this.getStartDate() == null) {
        	startTime = " ";
        } else {
        	startTime = this.getStartDate().format(formatter);
        }
        if (this.getEndDate() == null) {
        	endTime = " ";
        } else {
        	endTime = this.getEndDate().format(formatter);
        }
        sb.append("add \"" + this.getDescription().getFullDescription() + "\" from " + startTime + " to " + endTime);
        return sb.toString();
    }

    @Override
    public boolean is(String query) {
        query = query.toLowerCase();
        switch (query) {
        case "done":
            return this.getIsDone();
        case "undone":
            return !this.getIsDone();
        case "event":
            return this.getStartDate() != null;
        case "task":
            return this.getStartDate() == null;
        case "overdue":
            return this.getEndDate() != null && this.getIsDone() == false
                    && this.getEndDate().isAfter(LocalDateTime.now());
        case "item":
            return true;
        default:
            return false;
        }

    }


    /**
     * Gets the pretty explicit datetime for this Item's end datetime
     * e.g. "This Monday, 7:30PM" or "Mon 27 Nov, 9:30AM"
     * @return
     */
    public String extractPrettyEndDateTime() {
        return DateTimeParser.extractPrettyDateTime(this.endDate);
    }
    
    /**
     * Gets the pretty relative datetime for this Item's start datetime
     * e.g. "3 weeks from now"
     * @return EMPTY_STRING if datetime is null
     * @author darren
     */
    public String extractPrettyRelativeStartDateTime() {
        return DateTimeParser.extractPrettyRelativeDateTime(this.startDate);
    }

    /**
     * Gets the pretty relative datetime for this Item's end datetime
     * e.g. "3 weeks from now"
     * @return EMPTY_STRING if datetime is null
     * @author darren
     */
    @Override
    public String extractPrettyRelativeEndDateTime() {
        if(this.endDate == null) {
            return extractPrettyRelativeStartDateTime();
        }
        return DateTimeParser.extractPrettyRelativeDateTime(this.endDate);
    }

    @Override
    public String extractPrettyItemCardDateTime() {
        return DateTimeParser.extractPrettyDateTime(this.startDate);
    }
}
