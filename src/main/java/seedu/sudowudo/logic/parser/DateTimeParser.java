package seedu.sudowudo.logic.parser;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;

import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.nlp.PrettyTimeParser;
import org.ocpsoft.prettytime.nlp.parse.DateGroup;

//@@author A0147609X
/**
 * For parsing dates and times in Sudowudo command input. Singleton pattern!
 * 
 * @author darren
 */
public class DateTimeParser {
    // handy strings for making pretty dates
    public static final String EMPTY_STRING = "";
    public static final String SINGLE_WHITESPACE = " ";
    public static final String YESTERDAY_DATE_REF = "Yesterday";
    public static final String TODAY_DATE_REF = "Today";
    public static final String TOMORROW_DATE_REF = "Tomorrow";
    public static final String LAST_WEEK_REF = "Last" + SINGLE_WHITESPACE;
    public static final String NEXT_WEEK_REF = "Next" + SINGLE_WHITESPACE;
    public static final String THIS_WEEK_REF = "This" + SINGLE_WHITESPACE;
    public static final String PRETTY_COMMA_DELIMITER = "," + SINGLE_WHITESPACE;
    public static final String PRETTY_TO_DELIMITER = SINGLE_WHITESPACE + "-" + SINGLE_WHITESPACE;

    // DateTime formatting patterns
    public static final DateTimeFormatter ABRIDGED_DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM");
    public static final DateTimeFormatter EXPLICIT_DATE_FORMAT = DateTimeFormatter.ofPattern("d MMM yyyy");
    public static final DateTimeFormatter TWELVE_HOUR_TIME = DateTimeFormatter.ofPattern("h:mma");
    public static final DateTimeFormatter LONG_DAYOFWEEK = DateTimeFormatter.ofPattern("EEEE");
    public static final DateTimeFormatter SHORT_DAYOFWEEK = DateTimeFormatter.ofPattern("EEE");

    public static DateTimeParser instance = new DateTimeParser();

    private static final int FIRST_DATETIME_TOKEN = 0;
    private static final int SECOND_DATETIME_TOKEN = 1;

    // PrettyTimeParser object
    // careful of name collision with our own Parser object
    private static PrettyTimeParser parser = new PrettyTimeParser();

    // PrettyTime formatter
    private static PrettyTime prettytime = new PrettyTime();

    // the part of the command that contains the temporal part of the command
    private String datetime;

    // result from parser
    private List<DateGroup> dategroups;
    private List<Date> dates;

    private DateTimeParser() {
    }

    public static DateTimeParser getInstance() {
        return instance;
    }

    /**
     * Uses the DateTimeParser service to parse a string containing possible
     * datetime tokens (in natural language).
     * 
     * @param input
     */
    public DateTimeParser parse(String input) {
        assert input != null;

        this.datetime = input;

        // perform parsing
        this.dategroups = DateTimeParser.parser.parseSyntax(input);
        this.dates = DateTimeParser.parser.parse(input);

        return this;
    }

    public LocalDateTime extractStartDate() {
        assert this.dates != null;

        if (this.dategroups.isEmpty()) {
            return null;
        }

        return changeDateToLocalDateTime(this.dates.get(FIRST_DATETIME_TOKEN));
    }

    public LocalDateTime extractEndDate() {
        assert this.dates != null;

        if (this.dates.size() < 2) {
            return null;
        }

        return changeDateToLocalDateTime(this.dates.get(SECOND_DATETIME_TOKEN));
    }

    public boolean isRecurring() {
        return this.dategroups.get(FIRST_DATETIME_TOKEN).isRecurring();
    }

    public LocalDateTime getRecurEnd() {
        return changeDateToLocalDateTime(this.dategroups.get(FIRST_DATETIME_TOKEN).getRecursUntil());
    }

    /**
     * Makes the pretty datetime line for an Item's card on the UI.
     * 
     * The UI should only be calling this method for displaying datetime of
     * event on the Item's card on the agenda pane.
     * 
     * @return
     * @author darren
     */
    public static String extractPrettyItemCardDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null && end == null) {
            return EMPTY_STRING;
        }

        if (start == null) {
            return extractPrettyDateTime(end);
        }

        if (end == null) {
            return extractPrettyDateTime(start);
        }

        // is an event with a definite start and end datetime
        if (isSameDay(start, end)) {
            return extractPrettyDateTime(start) + PRETTY_TO_DELIMITER + extractTwelveHourTime(end);
        }

        // not same day
        return extractPrettyDateTime(start) + PRETTY_TO_DELIMITER + extractPrettyDateTime(end);
    }

    /**
     * Checks if two given java.time.LocalDateTime objects are of the same day.
     * 
     * @param ldt1
     * @param ldt2
     * @return true if they are both the same day, false otherwise
     * @author darren
     */
    public static boolean isSameDay(LocalDateTime ldt1, LocalDateTime ldt2) {
        return ldt1.toLocalDate().equals(ldt2.toLocalDate());
    }

    /**
     * Check if the given java.time.LocalDateTime object is the same date as the
     * current date on local system time
     * 
     * @param ldt
     * @return true if the LocalDateTime is for today, false otherwise
     * @author darren
     */
    public static boolean isToday(LocalDateTime ldt) {
        return isSameDay(ldt, LocalDateTime.now());
    }

    /**
     * Check if the given java.time.LocalDateTime object is the same date as the
     * yesterday relative to the local system time
     * 
     * @param ldt
     * @return true if the LocalDateTime is for yesterday, false otherwise
     * @author darren
     */
    public static boolean isYesterday(LocalDateTime ldt) {
        return isSameDay(ldt, LocalDateTime.now().minusDays(1));
    }

    /**
     * Check if the given java.time.LocalDateTime object is the same date as the
     * next day relative to the local system time
     * 
     * @param ldt
     * @return true if the LocalDateTime is for tomorrow, false otherwise
     * @author darren
     */
    public static boolean isTomorrow(LocalDateTime ldt) {
        return isSameDay(ldt, LocalDateTime.now().plusDays(1L));
    }

    /**
     * Check if the given java.time.LocalDateTime object is within two weeks of
     * the local system time
     * 
     * @param ldt
     * @return true if the LocalDateTime is within two weeks of local system
     *         time
     * @author darren
     */
    public static boolean isWithinTwoWeeks(LocalDateTime ldt) {
        return computeDaysTo(ldt) < 14 && computeDaysTo(ldt) > -14;
    }

    /**
     * Check if the given java.time.LocalDateTime object is within the same year
     * as the local system time.
     * 
     * @param ldt
     * @return true if the LocalDateTime is within the same year as local system
     *         time
     * @author darren
     */
    public static boolean isWithinThisYear(LocalDateTime ldt) {
        LocalDate currentDate = ldt.toLocalDate();
        LocalDate firstDayOfNextYear = LocalDate.now().with(TemporalAdjusters.firstDayOfNextYear());
        LocalDate lastDayOfLastYear = LocalDate.now().with(TemporalAdjusters.firstDayOfYear()).minusDays(1);
        return currentDate.isBefore(firstDayOfNextYear) && currentDate.isAfter(lastDayOfLastYear);
    }

    /**
     * Check if the given java.time.LocalDateTime object is within the previous
     * week from the local system time.
     * 
     * A week starts on Monday.
     * 
     * @param ldt
     * @return true if the LocalDateTime is within the previous week from local
     *         system time.
     * @author darren
     */
    public static boolean isLastWeek(LocalDateTime ldt) {
        LocalDate firstDayOfThisWeek = LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate lastDayOfLastLastWeek = firstDayOfThisWeek.minusDays(8);
        return ldt.toLocalDate().isBefore(firstDayOfThisWeek)
                && ldt.toLocalDate().isAfter(lastDayOfLastLastWeek);
    }

    /**
     * Check if the given java.time.LocalDateTime object is within this week of
     * the local system time.
     * 
     * A week starts on Monday.
     * 
     * @param ldt
     * @return true if the LocalDateTime is within this week in local system
     *         time.
     * @author darren
     */
    public static boolean isThisWeek(LocalDateTime ldt) {
        LocalDate lastDayOfLastWeek = LocalDate.now().with(DayOfWeek.MONDAY).minusDays(1);
        LocalDate firstDayOfNextWeek = lastDayOfLastWeek.plusDays(8);
        return ldt.toLocalDate().isAfter(lastDayOfLastWeek) && ldt.toLocalDate().isBefore(firstDayOfNextWeek);
    }

    /**
     * Check if the given java.time.LocalDateTime object is within next week of
     * the local system time.
     * 
     * A week starts on Monday.
     * 
     * @param ldt
     * @return true if the LocalDateTime is within the next week from local
     *         system time.
     * @author darren
     */
    public static boolean isNextWeek(LocalDateTime ldt) {
        LocalDate endOfCurrentWeek = LocalDate.now().with(DayOfWeek.SUNDAY);
        LocalDate firstDayOfNextNextWeek = endOfCurrentWeek.plusDays(8);
        return ldt.toLocalDate().isAfter(endOfCurrentWeek)
                && ldt.toLocalDate().isBefore(firstDayOfNextNextWeek);
    }

    /**
     * Helper method for casting java.util.Date to java.time.LocalDateTime
     * 
     * @param date
     * @return
     * @author darren
     */
    public static LocalDateTime changeDateToLocalDateTime(Date date) {
        Instant instant = date.toInstant().truncatedTo(ChronoUnit.SECONDS); // strip
                                                                            // milliseconds
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Helper method for casting java.time.LocalDateTime to java.util.Date
     * 
     * @param ldt
     * @return
     * @author darren
     */
    public static Date changeLocalDateTimeToDate(LocalDateTime ldt) {
        Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }

    /**
     * Helper method for determining a human-readable relative date from the
     * date tokens in the input string
     * 
     * Note that this is dependent on the local system time, e.g. the output
     * from java.util.Date()
     * 
     * Examples of pretty relative dates: (for future dates) "3 weeks from now",
     * "2 days from now", "12 minutes from now", "moments from now"
     * 
     * (for past dates) "3 weeks ago", "2 days ago", "12 minutes ago", "moments
     * ago"
     * 
     * @param index
     *            index of target java.util.Date inside DateTimeParser's
     *            List<Date>
     * @return pretty relative date
     * @author darren
     */
    public static String extractPrettyRelativeDateTime(LocalDateTime ldt) {
        if (ldt == null) {
            return EMPTY_STRING;
        }
        return prettytime.format(changeLocalDateTimeToDate(ldt));
    }

    /**
     * Helper method for determining a human-readable pretty date from date
     * tokens in the input string
     * 
     * Examples: "This Monday, 6:30AM", "Next Saturday, 12:37PM", "Mon 27
     * November, 9:30PM", "Today, 3:57PM"
     * 
     * @param index
     * @return pretty date for this week
     */
    public static String extractPrettyDateTime(LocalDateTime ldt) {
        // special case for yesterday/today/tomorrow relative to local system
        // time
        if (isYesterday(ldt)) {
            return YESTERDAY_DATE_REF + PRETTY_COMMA_DELIMITER + extractTwelveHourTime(ldt);
        }

        if (isToday(ldt)) {
            return TODAY_DATE_REF + PRETTY_COMMA_DELIMITER + extractTwelveHourTime(ldt);
        }

        if (isTomorrow(ldt)) {
            return TOMORROW_DATE_REF + PRETTY_COMMA_DELIMITER + extractTwelveHourTime(ldt);
        }

        // add relative prefix (this/next <day of week>) if applicable
        if (isWithinTwoWeeks(ldt)) {
            // is within the past/next two weeks
            return makeRelativePrefix(ldt) + extractLongDayOfWeek(ldt) + PRETTY_COMMA_DELIMITER
                    + extractTwelveHourTime(ldt);
        }

        // explicit date; no relative prefix
        String prettyDate;
        if (isWithinThisYear(ldt)) {
            // LDT is in current year
            prettyDate = ldt.toLocalDate().format(ABRIDGED_DATE_FORMAT);
        } else {
            // LDT is in another year
            prettyDate = ldt.toLocalDate().format(EXPLICIT_DATE_FORMAT);
        }
        return extractShortDayOfWeek(ldt) + SINGLE_WHITESPACE + prettyDate + PRETTY_COMMA_DELIMITER
                + extractTwelveHourTime(ldt);
    }

    /**
     * Extracts the time component of a java.time.LocalDateTime object and
     * returns it in 12-hour format.
     * 
     * @param ldt
     * @return
     * @author darren
     */
    public static String extractTwelveHourTime(LocalDateTime ldt) {
        return ldt.toLocalTime().format(TWELVE_HOUR_TIME);
    }

    /**
     * Extracts the day-of-week component of a java.time.LocalDateTime object
     * and returns it in long format (e.g. Monday)
     * 
     * @param ldt
     * @return day-of-week in long format
     * @author darren
     */
    public static String extractLongDayOfWeek(LocalDateTime ldt) {
        return ldt.toLocalDate().format(LONG_DAYOFWEEK);
    }

    /**
     * Extracts the day-of-week component of a java.time.LocalDateTime object
     * and returns it in short format (e.g. Mon)
     * 
     * @param ldt
     * @return day-of-week in short format
     * @author darren
     */
    public static String extractShortDayOfWeek(LocalDateTime ldt) {
        return ldt.toLocalDate().format(SHORT_DAYOFWEEK);
    }

    /**
     * Determine the appropriate relative prefix to use for reference to a
     * DayOfWeek enum
     * 
     * @param ldt
     * @return
     * @author darren
     */
    private static String makeRelativePrefix(LocalDateTime ldt) {
        if (isLastWeek(ldt)) {
            return LAST_WEEK_REF;
        } else if (isThisWeek(ldt)) {
            return THIS_WEEK_REF;
        } else if (isNextWeek(ldt)) {
            return NEXT_WEEK_REF;
        }

        // we should never reach this point
        return EMPTY_STRING;
    }

    /**
     * Computes number of days between current system time to the given
     * java.time.LocalDateTime
     * 
     * @param ldt
     *            future LocalDateTime
     * @return number of days between now to future LocalDateTime
     * @author darren
     */
    public static long computeDaysTo(LocalDateTime ldt) {
        return ChronoUnit.DAYS.between(LocalDate.now(), ldt.toLocalDate());
    }

    public String getDateTime() {
        return this.datetime;
    }

}
