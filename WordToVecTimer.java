
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * WordToVecTimer is a simple little timer class, written so no external library is needed to do this.
 * 
 * @author hulles
 *
 */
final class WordToVecTimer {
	private final static Level LOGLEVEL = Level.INFO;
	private final static Map<String, Long> timerMap;
	
	private WordToVecTimer() {
		// only static methods, no need to instantiate it
	}
	
	static {
		timerMap = new HashMap<String, Long>();
	}
	
	static void startTimer(String timerName) {
		
		SharedUtils.checkNotNull(timerName);
		timerMap.put(timerName, System.currentTimeMillis());
	}
	
	static Long stopTimer(String timerName) {
		Long endTime;
		Long startTime;
		Long elapsedMillis;
		
		SharedUtils.checkNotNull(timerName);
		endTime = System.currentTimeMillis();
		startTime = timerMap.remove(timerName);
		if (startTime == null) {
			System.err.println("Bad map start time in WordToVecTimer");
			return null;
		}
		elapsedMillis = endTime - startTime;
		SharedUtils.log(LOGLEVEL, "Timer " + timerName + ": " + SharedUtils.formatElapsedMillis(elapsedMillis));
		return elapsedMillis;
	}

}
