package nl.rgonline.lib.todoist;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class TodoistApi {
	private static final String BASE_URL = "https://api.todoist.com";
	private String username;
	private String password;
	private String token;
	
	public TodoistApi() {
		
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
}
