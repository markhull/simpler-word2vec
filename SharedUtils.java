

import java.util.logging.Level;
import java.util.logging.Logger;

final public class SharedUtils {
	private static final Logger logger = Logger.getLogger("your.logger.name.here");
	private static final long MILLIS_IN_SECOND = 1000;
	private static final long MILLIS_IN_MINUTE = 60 * MILLIS_IN_SECOND;
	private static final long MILLIS_IN_HOUR = MILLIS_IN_MINUTE * 60;
	private static final long MILLIS_IN_DAY = MILLIS_IN_HOUR * 24;
	private static final String DAYFORMAT = "%,d days, %d hours, %d minutes";
	private static final String HOURFORMAT = "%d hours, %d minutes";
	private static final String MINUTEFORMAT = "%d minutes, %f seconds";
	private static final String SECONDFORMAT = "%.3f seconds";
	private static final String MILLIFORMAT = "%d ms";
	
	/**
	 * Check that the argument is not null
	 * see Guava Preconditions, this is just adapted from there, and by "adapted" I mean copied :)
	 * 
	 * @param reference the reference to check for null value
	 */
	// 
	public static <T> void checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
	}
    
	/**
	 * Transform the milliseconds value into a human-readable elapsed time; used for the array dump
	 * 
	 * @param millis The milliseconds to format
	 * @return The formatted string
	 */
    public static String formatElapsedMillis(long millis) {
    	long days = 0;
    	long hours = 0;
    	long minutes = 0;
    	float seconds = 0f;    	

    	
    	if (millis > MILLIS_IN_DAY) {
    		days = millis / MILLIS_IN_DAY;
    		millis -= (days * MILLIS_IN_DAY);
    	}
    	if (millis > MILLIS_IN_HOUR) {
    		hours = millis / MILLIS_IN_HOUR;
    		millis -= (hours * MILLIS_IN_HOUR);
    	}
    	if (millis > MILLIS_IN_MINUTE) {
    		minutes = millis / MILLIS_IN_MINUTE;
    		millis -= (minutes * MILLIS_IN_MINUTE);
    	}
    	if (millis > MILLIS_IN_SECOND) {
    		seconds = (float)millis / (float)MILLIS_IN_SECOND;
    	} else {
    		seconds = 0;
    	}
    	
    	if (days > 0) {
    		return String.format(DAYFORMAT, days, hours, minutes);
    	}
    	if (hours > 0) {
    		return String.format(HOURFORMAT, hours, minutes);
    	}
    	if (minutes > 0) {
    		return String.format(MINUTEFORMAT, minutes, seconds);
    	}
    	if (seconds > 0) {
    		return String.format(SECONDFORMAT, seconds);
    	}
		return String.format(MILLIFORMAT, millis);
    }

    /**
     * Log a message using the java.util logger; feel free to adapt it to yours
     * 
     * @param level The log level of the message, e.g. Level.INFO
     * @param message The message
     */
    public static void log(Level level, String message) {
    	logger.log(level, message);
    }
}
