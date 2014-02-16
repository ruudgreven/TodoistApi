package nl.rgonline.lib.todoist;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

public class LoginTest {

	@Test
	public void testLoginValidCredetentials() throws FileNotFoundException, IOException {
		Properties p = new Properties();
		try {
			InputStream is = ClassLoader.getSystemResourceAsStream("nl/rgonline/lib/todoist/login.properties");
			p.load(is);
			
			TodoistApi api = new TodoistApi();
			assertTrue(api.login(p.getProperty("username"), p.getProperty("password")));
		} catch (FileNotFoundException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		} catch (IOException e) {
			System.out.println("Create a properties file with the login.properties to test login features");
			throw e;
		}
	}

}
