package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.github.hi_fi.dblibrary.keywords.DatabaseConnection;

/**
 * Tests related to connecting to the database
 */
public class DatabaseLibraryConnectionIT {

	private DatabaseConnection databaseConnection;
	private Assert asserter;

	@Before
	public void setUpTest() throws Exception {
		databaseConnection = new DatabaseConnection();
		asserter = new Assert();
	}
	
	// ========================================================
	//
	// Database Connection 
	//
	// ========================================================

	@Test
	public void checkConnectToDatabase() throws Exception {
		databaseConnection.connectToDatabase(ConnectionHelper.H2_DRIVER_CLASSNAME,
				ConnectionHelper.H2_URL, ConnectionHelper.H2_USER, ConnectionHelper.H2_PASSWORD);
	}

	@Test
	public void checkConnectToDatabaseWithWrongUsername() {
		try {
			databaseConnection.connectToDatabase(ConnectionHelper.H2_DRIVER_CLASSNAME,
					ConnectionHelper.H2_URL, "xyz", ConnectionHelper.H2_PASSWORD);
		} catch (SQLException e) {
			if (!e.getMessage().contains("Wrong user name or password")) {
				e.printStackTrace();
				fail();
			}
		} catch (Exception e2) {
			e2.printStackTrace();
			fail();
		}
	}

	@Test
	public void checkDisconnectFromDatabase() throws Exception {
		databaseConnection.connectToDatabase(ConnectionHelper.H2_DRIVER_CLASSNAME,
				ConnectionHelper.H2_URL, ConnectionHelper.H2_USER, ConnectionHelper.H2_PASSWORD);
		databaseConnection.disconnectFromDatabase();
	}

	@Test(expected=IllegalStateException.class)
	public void checkIllegalStateExceptionWithoutConnect() throws Exception {
		asserter.tableMustBeEmpty("NoConnection");
	}

}
