package nl.rgonline.lib.todoist;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TodoistApi {
	private static final String BASE_URL = "https://api.todoist.com";
	private String username;
	private String password;
	private String token;
	
	private int seq_no = 0;
	private TodoistData data;
	
	public TodoistApi() {
		data = new TodoistData();
	}
	
	/**
	 * Log in to todoist
	 * @param username
	 * @param password
	 */
	public boolean login(String username, String password) {
		this.username = username;
		this.password = password;
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("email", username));
		params.add(new BasicNameValuePair("password", password));
		
		JSONObject response = TodoistNetworking.doPostRequest(BASE_URL + "/API/login", params);
		try {
			if (response.getString("status").equals("ok")) {
				token = response.getJSONObject("response").getString("token");
				TodoistLogger.debug("TodoistApi", "login()", "Login OK. Token: " + token);
				return true;
			} else {
				TodoistLogger.error("TodoistApi", "login()", "Login error. Invalid credentials?");
				
				return false;
			}
		} catch (JSONException e) {
			TodoistLogger.error("TodoistApi", "login()", "JSON Exception in answer. Unexpected answer from the server");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This methods syncs all data from todoist with the get method from the TodoistSync API.
	 * @return The TodoistData object, containing all the data
	 * @throws TodoistException When an error occurs
	 */
	public TodoistData get() throws TodoistException {
		if (token==null) {
			TodoistLogger.error("TodoistApi", "syncGet()", "Not logged in");
			throw new TodoistException("Not logged in");
		}
		
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_token", token));
		params.add(new BasicNameValuePair("seq_no", "" + seq_no));
		JSONObject srvresponse = TodoistNetworking.doPostRequest(BASE_URL + "/TodoistSync/v5.1/get", params);
		handleResponse(srvresponse);
		
		return data;
	}
	
	/**
	 * This methods syncs all data from and to todoist with the syncAndGetUpdated method from the TodoistSync API.
	 * @return The updated TodoistData
	 * @throws TodoistException When an error occurs
	 */
	public void syncAndGetUpdated() throws TodoistException {
		if (token==null) {
			TodoistLogger.error("TodoistApi", "syncGet()", "Not logged in");
			throw new TodoistException("Not logged in");
		}
		
		JSONArray itemsToSync = new JSONArray();
		for (Item item: data.items) {
			JSONObject obj = item.writeJson();
			if (obj!=null) {
				itemsToSync.put(obj);
			}
		}
		for (Label label: data.labels) {
			JSONObject obj = label.writeJson();
			if (obj!=null) {
				itemsToSync.put(obj);
			}
		}
		for (Project project: data.projects) {
			JSONObject obj = project.writeJson();
			if (obj!=null) {
				itemsToSync.put(obj);
			}
		}
		
		TodoistLogger.debug("TodoistApi", "syncAndGetUpdated()", "Going to sync, send text: " + itemsToSync.toString());
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("api_token", token));
		params.add(new BasicNameValuePair("seq_no", "" + seq_no));
		params.add(new BasicNameValuePair("items_to_sync", "" + itemsToSync.toString()));
		JSONObject srvresponse = TodoistNetworking.doPostRequest(BASE_URL + "/TodoistSync/v5.1/syncAndGetUpdated", params);
		handleResponse(srvresponse);
	}
	
	
	private void handleResponse(JSONObject srvresponse) throws TodoistException {
		try {
			TodoistLogger.debug("TodoistApi", "syncGet()", "Login OK. Response: " + srvresponse);
			JSONObject response = srvresponse.getJSONObject("response");
			seq_no = response.getInt("seq_no");
			
			//Items
			JSONArray items = response.getJSONArray("Items");
			for (int i=0; i < items.length(); i++) {
				Item item = new Item(data, items.getJSONObject(i));
				data.addItem(item);
			}
			
			//Labels
			JSONArray labels = response.getJSONArray("Labels");
			for (int i=0; i < labels.length(); i++) {
				Label label = new Label(data, labels.getJSONObject(i));
				data.addLabel(label);
			}
			
			//Projects
			JSONArray projects = response.getJSONArray("Projects");
			for (int i=0; i < projects.length(); i++) {
				Project project = new Project(data, projects.getJSONObject(i));
				data.addProject(project);
			}
		} catch (JSONException e) {
			TodoistLogger.error("TodoistApi", "syncGet()", "JSON Exception in answer. Unexpected answer from the server");
			throw new TodoistException("JSON Exception in answer. Unexpected answer from the server", e);
		}
	}
	
}
