package nl.rgonline.lib.todoist;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import org.junit.Test;

public class SyncTest {

	@Test
	public void testGet() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("nl/rgonline/lib/todoist/login.properties");
			p.load(is);
			
			TodoistApi api = new TodoistApi();
			assertTrue(api.login(p.getProperty("username"), p.getProperty("password")));
			try {
				TodoistData data = api.get();
				System.out.println(data);
			} catch (TodoistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		} catch (IOException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		}
	}
 /**	
	@Test
	public void testSyncAndGetUpdated() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("nl/rgonline/lib/todoist/login.properties");
			p.load(is);
			
			TodoistApi api = new TodoistApi();
			assertTrue(api.login(p.getProperty("username"), p.getProperty("password")));
			try {
				//Gets data
				TodoistData data = api.get();
				long unixtime = (int) (System.currentTimeMillis() / 1000L);
				
				data.addLabel("My favorite label" + unixtime);
				data.addProject("New Testproject" + unixtime);
				api.syncAndGetUpdated();
				
				Project testproject = data.getProject("New Testproject" + unixtime);
				Label mylabel = data.getLabel("My favorite label" + unixtime);
				
				testproject.addItem("NEW First item");
				testproject.addItem("NEW Second item");
				testproject.addItem("NEW Third item");
				api.syncAndGetUpdated();
				
				testproject.getItem("NEW First item").addLabel(mylabel);
				api.syncAndGetUpdated();
				
				System.out.println(data);
			} catch (TodoistException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		} catch (IOException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		}
	}
	**/
}
