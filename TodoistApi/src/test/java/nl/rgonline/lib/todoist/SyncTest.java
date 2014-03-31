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

	/**
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
	**/
	
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
				
				//Updates some item
				ArrayList<Item> items = data.getItems();
				for (Item item: items) {
					if (Math.random() > 0.7) {
						item.setContent(item.getContent() + " - updated");
					}
				}
				
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
}