package nl.rgonline.lib.todoist;

public class TodoistLogger {
	public static boolean LOG_DEBUG = true;
	public static boolean LOG_ERROR = true;
	
	
	protected static void debug(String classname, String method, String message) {
		if (LOG_DEBUG) {
			System.out.println("TODOISTAPI DEBUG: " + classname + ": " + method + ": " + message);
		}
	}
	
	protected static void error(String classname, String method, String message) {
		if (LOG_ERROR) {
			System.err.println("TODOISTAPI ERROR: " + classname + ": " + method + ": " + message);
		}
	}
}
