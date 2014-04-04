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
	private long id;
	private long temp_id;
	private boolean is_deleted;
	private int item_order;
	private int color;
	private String name;
	private int archived_timestamp;
	private double last_updated;
	private int user_id;
	private int cache_count;
	
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
		return null;
		/**
		if (temp_id != 0) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"label_register\",\"timestamp\": " + unixtime + ",\"args\": {";
			jsonString +="\"name\": \"" + name + "\",";
			jsonString +="\"color\": \"" + color + "\"";
			jsonString += "}}";
			return new JSONObject(jsonString);
			
		} else if (updated) {
			long unixtime = (int) (System.currentTimeMillis() / 1000L);
			String jsonString = "{\"type\": \"label_update\",\"timestamp\": " + unixtime + ",\"args\": {";
			jsonString +="\"name\": \"" + name + "\",";
			jsonString +="\"color\": \"" + color + "\"";
			jsonString += "}}";
			return new JSONObject(jsonString);
			
		} else {
			return null;
		}
		**/
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
		return id;
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
