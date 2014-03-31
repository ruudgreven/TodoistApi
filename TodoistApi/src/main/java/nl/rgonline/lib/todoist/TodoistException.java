package nl.rgonline.lib.todoist;

public class TodoistException extends Exception {
	public TodoistException(String message) {
		super(message);
	}
	
	public TodoistException(String message, Exception e) {
		super(message, e);
	}
}
