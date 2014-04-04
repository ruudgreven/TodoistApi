package nl.rgonline.lib.todoist;

import java.util.Observable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Represents a todoist Label
 * @author ruudgreven
 *
 */
public class Label extends Observable {
	private TodoistData data;
	private boolean updated = false;
	
	private int uid;
	private long id;
	private long temp_id;
	private boolean is_deleted;
	private int color;
	private String name;
	
	protected Label(String name, int color) {
		this.name = name;
		this.color = color;
		
		long unixtime = (int) (System.currentTimeMillis() / 1000L);
		temp_id = Long.parseLong("325" + unixtime);
	}
	
	protected Label(TodoistData data, JSONObject obj) throws JSONException {
		this.data = data;
		
		uid = obj.getInt("uid");
		id = obj.getInt("id");
		is_deleted = obj.getInt("is_deleted") == 1 ? true : false;
		color = obj.getInt("color");
		name = obj.getString("name");
	}
	
	protected JSONObject writeJson() {
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
	}

	public long getId() {
		return id;
	}
	
	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		if (this.uid != uid) {
			this.uid = uid;
			this.updated();
		}
	}

	public boolean isDeleted() {
		return is_deleted;
	}

	public void setDeleted(boolean is_deleted) {
		if (this.is_deleted!=is_deleted) {
			this.is_deleted = is_deleted;
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
	
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Label)) {
			return false;
		}
		Label other = (Label)o;
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
