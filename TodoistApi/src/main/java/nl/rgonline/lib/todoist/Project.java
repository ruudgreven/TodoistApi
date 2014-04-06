package nl.rgonline.lib.todoist;

import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

public class Project extends Observable {
	private TodoistData data;
	private boolean updated = false;
	
	private boolean shared;
	private int indent;
	private Date archive_date;
	private boolean is_archived;
	private boolean collapsed;
	private long id = -1;
	private boolean is_deleted;
	private int item_order;
	private int color;
	private String name;
	private int archived_timestamp;
	private double last_updated;
	private int user_id;
	private int cache_count;
	

	private long temp_id = -1;
	private static int temp_id_follow_nr = 1;
	
	protected Project(TodoistData data, String name, int color, int indent, int item_order) {
		this.data = data;
		this.name = name;
		this.color = color;
		this.indent = indent;
		this.item_order = item_order;
		
		long unixtime = (int) (System.currentTimeMillis() / 1000L);
		temp_id_follow_nr++;
		temp_id = Long.parseLong("325" + unixtime + temp_id_follow_nr);
	}
	
	
	protected Project(TodoistData data, JSONObject obj) throws JSONException {
		this.data = data;
		shared = obj.getBoolean("shared");
		indent = obj.getInt("indent");
		is_archived = obj.getInt("is_archived") == 1 ? true: false;
		collapsed = obj.getInt("collapsed") == 1 ? true: false;
		id = obj.getLong("id");
		is_deleted = obj.getInt("is_deleted") == 1 ? true : false;
		item_order = obj.getInt("item_order");
		color = obj.getInt("color");
		name = obj.getString("name");
		last_updated = obj.getDouble("last_updated");
		user_id = obj.getInt("user_id");
		cache_count = obj.getInt("cache_count");
		
		//Dates not supported yet
		//archive_date
		//archived_timestamp
	}
	
	protected JSONObject writeJson() {
		if (temp_id != -1) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"project_add\",\"timestamp\": " + unixtime + ",\"temp_id\": \"$" + temp_id + "\",\"args\": {";
			jsonString +="\"name\": \"" + name + "\",";
			jsonString +="\"color\": \"" + color + "\",";
			jsonString +="\"indent\": \"" + indent + "\",";
			jsonString +="\"item_order\": \"" + item_order + "\"";
			jsonString += "}}";
			
			updated = false;
			temp_id = -1;
			return new JSONObject(jsonString);
			
		} else if (updated) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"label_update\",\"timestamp\": " + unixtime + ",\"args\": {";
			jsonString +="\"name\": \"" + name + "\",";
			jsonString +="\"color\": \"" + color + "\",";
			jsonString +="\"indent\": \"" + indent + "\",";
			jsonString +="\"item_order\": \"" + item_order + "\"";
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

	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 */
	public void addItem(String content) {
		data.addItem(content, this);
	}
	
	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 * @param indent The indent
	 */
	public void addItem(String content, int indent) {
		data.addItem(content, this, indent);
	}
	
	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 * @param indent The indent
	 * @param priority The priority of the item
	 */
	public void addItem(String content, int indent, int priority) {
		data.addItem(content, this, indent, priority);
	}
	
	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 */
	public void addItem(String content, int indent, int priority, String date_string, Date due_date) {
		data.addItem(content, this, indent, priority, date_string, due_date);
	}
	
	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 * @param item_order The item_order of this item
	 */
	public void addItem(String content, int indent, int priority, String date_string, Date due_date, int item_order) {
		data.addItem(content, this, indent, priority, date_string, due_date, item_order);
	}
	
	/**
	 * Add a item with the given characteristics to this project
	 * @param content The content (text) for the item
	 * @param indent The indent
	 * @param priority The priority of the item
	 * @param date_string The date string for the due date
	 * @param due_date The due date
	 * @param assigned_by_user The uid of the user to which this task is assigned to
	 * @param responsible_uid The uid of the user who is responsible for this task
	 * @param item_order The item_order of this item
	 */
	public void addItem(String content, int indent, int priority, String date_string, Date due_date, int assigned_by_user, int responsible_uid, int item_order) {
		data.addItem(content, this, indent, priority, date_string, due_date, assigned_by_user, responsible_uid, item_order);
	}
	
	/**
	 * Receives the item with the given text in this project
	 * @param content
	 * @return The item
	 */
	public Item getItem(String content) {
		return data.getItem(content, this);
	}
	
	public boolean isShared() {
		return shared;
	}
	
	public void setShared(boolean shared) {
		if (this.shared!=shared) {
			this.shared = shared;
			this.updated();
		}
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
	public Date getArchive_date() {
		return archive_date;
	}
	public void setArchive_date(Date archive_date) {
		if (!this.archive_date.equals(archive_date)) {
			this.archive_date = archive_date;
			this.updated();
		}
	}
	public boolean isIs_archived() {
		return is_archived;
	}
	public void setIs_archived(boolean is_archived) {
		if (this.is_archived!=is_archived) {
			this.is_archived = is_archived;
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
	public boolean isDeleted() {
		return is_deleted;
	}
	public void setDeleted(boolean is_deleted) {
		if (this.is_deleted != is_deleted) {
			this.is_deleted = is_deleted;
			this.updated();
		}
	}
	public int getItem_order() {
		return item_order;
	}
	public void setItem_order(int item_order) {
		if (this.item_order != item_order) {
			this.item_order = item_order;
			this.updated();
		}
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		if (this.color != color) {
			this.color = color;
			this.updated();
		}
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		if (!this.name.equals(name)) {
			this.name = name;
			this.updated();
		}
	}
	public int getArchivedTimestamp() {
		return archived_timestamp;
	}
	public void setArchivedTimestamp(int archived_timestamp) {
		if (this.archived_timestamp != archived_timestamp) {
			this.archived_timestamp = archived_timestamp;
			this.updated();
		}
	}
	public double getLast_updated() {
		return last_updated;
	}
	public void setLastUpdated(double last_updated) {
		if (this.last_updated != last_updated) {
			this.last_updated = last_updated;
			this.updated();
		}
	}
	public int getUserId() {
		return user_id;
	}
	public void setUserId(int user_id) {
		if (this.user_id != user_id) {
			this.user_id = user_id;
			this.updated();
		}
	}
	public int getCacheCount() {
		return cache_count;
	}
	public void setCacheCount(int cache_count) {
		if (this.cache_count != cache_count) {
			this.cache_count = cache_count;
			this.updated();
		}
	}
	public long getId() {
		if (id != -1) {
			return id;
		} else {
			return temp_id;
		}
	}
	
	/**
	 * Returns all the items of this project
	 */
	public ArrayList<Item> getItems() {
		ArrayList<Item> items = new ArrayList<Item>();
		for (Item item: data.items) {
			if (item.getProject().equals(this)) {
				items.add(item);
			}
		}
		return items;
	}
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Project)) {
			return false;
		}
		Project other = (Project)o;
		if (this.temp_id != -1) {
			return false;
		}
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
		return "(" + id + ") " + name + "(" + color + "), deleted: " + (is_deleted ? "yes" : "no");
	}
}
