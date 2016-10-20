package seedu.address.model.item;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;

import org.junit.Test;

import seedu.address.commons.exceptions.IllegalValueException;

public class ItemTest {

    public static LocalDateTime twelveNov = LocalDateTime.of(2016, 11, 12, 12, 0);
    public static LocalDateTime elevenNov = LocalDateTime.of(2016, 11, 11, 12, 0);

    @Test
    public void testCompareTo1() {
        
	    try {
	        Description dogWalk = new Description("walk the dog");
	        Description snortCrack = new Description("snort cocaine");
	        
	        Item dog = new Item(dogWalk, elevenNov, twelveNov);
	        Item crack = new Item(snortCrack, null, twelveNov);
	        
	        assertEquals(-1, dog.compareTo(crack));
	    } catch(IllegalValueException ive) {
	        System.out.println(ive.getMessage());
	    }
    }
    
    @Test
    public void testCompareTo2() {
        
	    try {
	        Description snortCrack = new Description("snort cocaine");
	        
	        Item crack = new Item(snortCrack, null, twelveNov);
	        
	        assertEquals(0, crack.compareTo(crack));
	    } catch(IllegalValueException ive) {
	        System.out.println(ive.getMessage());
	    }
    }

    @Test
    public void testCompareTo3() {
        
	    try {
	        Description dogWalk = new Description("walk the dog");
	        Description snortCrack = new Description("snort cocaine");
	        
	        Item dog = new Item(dogWalk, elevenNov, twelveNov);
	        Item crack = new Item(snortCrack, null, twelveNov);
	        
	        assertEquals(1, crack.compareTo(dog));
	    } catch(IllegalValueException ive) {
	        System.out.println(ive.getMessage());
	    }
    }

    @Test
    public void testCompareTo_allNullDateTimes() {
        
	    try {
	        Description dogWalk = new Description("walk the dog");
	        Description snortCrack = new Description("snort cocaine");
	        
	        Item dog = new Item(dogWalk, null, null);
	        Item crack = new Item(snortCrack, null, null);
	        
	        int result = crack.compareTo(dog)/(-1*crack.compareTo(dog));
	        
	        assertEquals(-1, result);

	    } catch(IllegalValueException ive) {
	        System.out.println(ive.getMessage());
	    }
    }
}
