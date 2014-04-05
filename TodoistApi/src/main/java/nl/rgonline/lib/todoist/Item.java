package nl.rgonline.lib.todoist;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Observable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a todoist item. Items were considered equal when the id is the same.
 * Notice that children, date_added and due_date are unsupported at this moment
 *
 */
public class Item extends Observable {
	private TodoistData data;
	private boolean updated = false;
	private long temp_id = -1;
	
	private int sync_id;
	private int indent;
	private ArrayList<Long> labels;
	private Item children;
	private int responsible_uid;
	private boolean is_archived;
	private int day_order;
	private boolean collapsed;
	private long id;
	private boolean has_notifications;
	private String content;
	private int item_order;
	private boolean is_deleted;
	private int assigned_by_uid;
	private Date date_added;
	private int priority;
	private boolean in_history;
	private long project_id;
	private Date due_date;
	private String date_string;
	private int user_id;
	private boolean checked;
	
	protected Item(TodoistData data, String content, Project project, int indent, int priority, String date_string, Date due_date, int assigned_by_uid, int responsible_uid, int item_order) {
		this.data = data;
		this.content = content;
		this.project_id = project.getId();
		this.indent = indent;
		this.priority = priority;
		this.date_string = date_string;
		this.due_date = due_date;
		this.assigned_by_uid = assigned_by_uid;
		this.responsible_uid = responsible_uid;
		this.item_order = item_order;
		
		labels = new ArrayList<Long>();
		long unixtime = (int) (System.currentTimeMillis() / 1000L);
		temp_id = Long.parseLong("325" + unixtime);
	}
	
	protected Item(TodoistData data, JSONObject obj) throws JSONException {
		this.data = data;
		
		sync_id = obj.isNull("sync_id") ? -1: obj.getInt("sync_id");
		responsible_uid = obj.isNull("responsible_uid") ? -1 : obj.getInt("responsible_uid");
		indent = obj.getInt("indent");
		is_archived = obj.getInt("is_archived") == 1 ? true : false;
		day_order = obj.getInt("day_order");
		collapsed = obj.getInt("collapsed") == 1 ? true: false;
		id = obj.getLong("id");
		has_notifications = obj.getInt("has_notifications") == 1 ? true: false;
		content = obj.getString("content");
		item_order = obj.getInt("item_order");
		is_deleted = obj.getInt("is_deleted") == 1 ? true: false;
		assigned_by_uid = obj.getInt("assigned_by_uid");
		date_added = TodoistApi.parseDate(obj.getString("date_added"));
		priority = obj.getInt("priority");
		in_history = obj.getInt("in_history") == 1 ? true: false;
		project_id = obj.getInt("project_id");
		if (!obj.isNull("due_date")) { 
			due_date = TodoistApi.parseDate(obj.getString("due_date"));
		}
		date_string = obj.getString("date_string");
		user_id = obj.getInt("user_id");
		checked = obj.getInt("checked") == 1 ? true: false;
		
		//Read labels
		JSONArray labelArray = obj.getJSONArray("labels");
		labels = new ArrayList<Long>();
		for (int i=0; i < labelArray.length(); i++) {
			labels.add(labelArray.getLong(i));
		}
		
		//Read children
		//TODO: Children not supported (YET)
		
		//Read dates
		//DateFormat formatter = new SimpleDateFormat("MM/dd/yy");
		//date_added
		//due_date = obj.getString("due_date");
	}
	
	protected JSONObject writeJson() {
		if (temp_id != -1) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"item_add\",\"timestamp\": " + unixtime + ",\"temp_id\": \"$" + temp_id + "\",\"args\": {";
			jsonString +="\"content\": \"" + content + "\",";
			jsonString +="\"project_id\": " + project_id + ",";
			jsonString +="\"indent\": " + indent + ",";
			if (priority < 1) {
				jsonString +="\"priority\": 1,";
			} else {
				jsonString +="\"priority\": " + priority + ",";
			}
			if (date_string != null) {
				jsonString +="\"date_string\": \"" + date_string + "\",";
			}
			if (due_date != null) {
				jsonString +="\"due_date_utc\": \"" + TodoistApi.dateToDueDateString(due_date) + "\",";
			}
			jsonString +="\"item_order\": " + item_order + ",";
			jsonString +="\"assigned_by_uid\": " + assigned_by_uid + ",";
			jsonString +="\"responsible_uid\": " + responsible_uid;
			jsonString += "}}";
			
			updated = false;
			temp_id = -1;
			return new JSONObject(jsonString);
		} else if (updated) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"item_update\",\"timestamp\": " + unixtime + ",\"args\": {";
			jsonString +="\"content\": \"" + content + "\",";
			jsonString +="\"project_id\": " + project_id + ",";
			jsonString +="\"indent\": " + indent + ",";
			jsonString +="\"priority\": " + priority + ",";
			jsonString +="\"checked\": " + (checked ? 1: 0) + ",";
			jsonString +="\"in_history\": " + (in_history ? 1: 0) + ",";
			jsonString +="\"date_string\": " + date_string + ",";
			jsonString +="\"due_date_utc\": " + TodoistApi.dateToDueDateString(due_date) + ",";
			jsonString +="\"item_order\": " + item_order + ",";
			jsonString +="\"day_order\": " + day_order + ",";
			jsonString +="\"assigned_by_uid\": " + assigned_by_uid + ",";
			jsonString +="\"responsible_uid\": " + responsible_uid + ",";
			jsonString +="\"id\": \"" + id + "\"";
			jsonString += "}}";
			
			updated = false;
			temp_id = -1;
			return new JSONObject(jsonString);
		} else {
			return null;
		}
	}
	
	public boolean isTemporary() {
		return temp_id!=1;
	}
	
	protected long getId() {
		return id;
	}
	
	public int getIndent() {
		return indent;
	}
	
	public void setIndent(int indent) {
		if (this.indent!=indent) {
			this.indent = indent;
			this.updated();
		}
	}
	
	/**
	 * Returns the set of labels
	 * @return
	 */
	public HashSet<Label> getLabels() {
		HashSet<Label> retVal = new HashSet<Label>();
		for (long labelId: labels) {
			retVal.add(data.getLabel(labelId));
		}
		return retVal;
	}
	
	/**
	 * Adds a label to the list of labels
	 * @return
	 */
	public void addLabel(Label label) {
		labels.add(label.getId());
	}
	
	public boolean isArchived() {
		return is_archived;
	}
	
	public void setArchived(boolean is_archived) {
		if (this.is_archived!=is_archived) {
			this.is_archived = is_archived;
			this.updated();
		}
	}
	
	public int getDayOrder() {
		return day_order;
	}
	
	public void setDayOrder(int day_order) {
		if (this.day_order != day_order) {
			this.day_order = day_order;
			this.updated();
		}
	}
	
	public boolean isCollapsed() {
		return collapsed;
	}
	
	public void setCollapsed(boolean collapsed) {
		if (this.collapsed != collapsed) {
			this.collapsed = collapsed;
			this.updated();
		}
	}
	
	public boolean hasNotifications() {
		return has_notifications;
	}
	
	public void setNotifications(boolean has_notifications) {
		if (this.has_notifications != has_notifications) {
			this.has_notifications = has_notifications;
			this.updated();
		}
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		if (!this.content.equals(content)) {
			this.content = content;
			this.updated();
		}
	}
	
	public int getItemOrder() {
		return item_order;
	}
	
	public void setItemOrder(int item_order) {
		if (this.item_order != item_order) {
			this.item_order = item_order;
			this.updated();
		}
	}
	
	public boolean isDeleted() {
		return is_deleted;
	}
	
	public void setDeleted(boolean is_deleted) {
		if (this.is_deleted != is_deleted) {
			this.is_deleted = is_deleted;
			this.updated();
		}
	}
	
	public int getPriority() {
		return priority;
	}
	
	public void setPriority(int priority) {
		if (this.priority != priority) {
			this.priority = priority;
			this.updated();
		}
	}
	
	public boolean isInHistory() {
		return in_history;
	}
	
	public void setInHistory(boolean in_history) {
		if (this.in_history) {
			this.in_history = in_history;
			this.updated();
		}
	}
	
	public String getDateString() {
		return date_string;
	}
	
	public void setDateString(String date_string) {
		if (this.date_string.equals(date_string)) {
			this.date_string = date_string;
			this.updated();
		}
	}
	
	public boolean isChecked() {
		return checked;
	}
	
	public void setChecked(boolean checked) {
		if (this.checked != checked) {
			this.checked = checked;
			this.updated();
		}
	}
	
	public Project getProject() {
		return data.getProject(project_id);
	}
	
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Item)) {
			return false;
		}
		Item other = (Item)o;
		return id == other.id;
	}
	
	@Override
	public int hashCode() {
		return (int)id;
	}
	
	private void updated() {
		updated = true;
		this.setChanged();
		this.notifyObservers(this);
	}
	
	public boolean isUpdated() {
		return updated;
	}
	
	public String toString() {
		String retVal = "(" + id + ") " + content + ", done: " + (checked ? "yes" : "no") + ", labels: ";
		boolean first = true;
		for (Label label: this.getLabels()) {
			if (first) {
				retVal += label.getName();
				first = false;
			} else {
				retVal += ", " + label.getName();
			}
		}
		return retVal;
	}
	
}
