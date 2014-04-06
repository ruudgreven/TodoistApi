package nl.rgonline.lib.todoist;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.*;

public class TodoistNetworking {	
	protected static JSONObject doGetRequest(String url) {
		return doRequest(url, null, false);
	}
	
	protected static JSONObject doPostRequest(String url) {
		return doRequest(url, null, true);
	}
	
	protected static JSONObject doPostRequest(String url, ArrayList<NameValuePair> params) {
		return doRequest(url, params, true);
	}
	
	private static JSONObject doRequest(String url, ArrayList<NameValuePair> params, boolean post) {
		HttpClient client = new DefaultHttpClient();
		
		HttpUriRequest request = null;
		if (post) {	
			try {
				request = new HttpPost(url);
				((HttpPost)request).setEntity(new UrlEncodedFormEntity(params));
			} catch (UnsupportedEncodingException e) {
				TodoistLogger.error("TodoistNetworking", "doRequest", "Error encoding post params");
				e.printStackTrace();
			}
			if (params!=null) {
				TodoistLogger.debug("TodoistNetworking", "doRequest", "POST Request with params to URL: " + url);
			} else {
				TodoistLogger.debug("TodoistNetworking", "doRequest", "POST Request to URL: " + url);
			}
		} else {
			request= new HttpGet(url);
			TodoistLogger.debug("TodoistNetworking", "doRequest", "GET Request to URL: " + url);
		}

		if (request!=null) {
			String outputString = "";
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				
				//Read JSON
				StringBuilder builder = new StringBuilder();
				BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				
				outputString = builder.toString();
				JSONObject retVal = new JSONObject();
				try {
					JSONObject obj = new JSONObject(outputString);
					retVal.put("status", "ok");
					retVal.put("response", obj);
				} catch (JSONException ex) {
					try {
						JSONArray obj = new JSONArray(outputString);
						retVal.put("status", "ok");
						retVal.put("response", obj);
					} catch (JSONException ex2) {
						retVal.put("status", "error");
						retVal.put("message", outputString);
					}
				}
				return retVal;
			} catch (Exception e) {
				TodoistLogger.error("TodoistNetworking", "doRequest", "doRequest(): Error getting data, answer from server:" + outputString);
				e.printStackTrace();
			}
		}
		return null;
	}
	

}
