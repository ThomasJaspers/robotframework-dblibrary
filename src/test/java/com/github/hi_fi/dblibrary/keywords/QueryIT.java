package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class QueryIT {

	private Query query = new Query();
	private Assert asserter = new Assert(); 
	private static ConnectionHelper ch = new ConnectionHelper();
	

	// ========================================================
	//
	// Setup and Teardown on class-level
	//
	// ========================================================

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ch.createTestDB();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ch.deleteTestDB();
	}
	
	@Before
	public void setUpTest() throws Exception {
		ch.initTestTables();
		ch.initDatabaseLibrary();
	}

	//
	// Check Execute SQL
	//
	@Test
	public void checkExecuteSQL() throws Exception {
		query.executeSql("CREATE TABLE TestTable (Num Integer)");
		asserter.tableMustExist("TESTTABLE");
	}
	
	// ========================================================
	//
	// Check Read Single Value from Table
	//
	// ========================================================

	@Test
	public void checkReadSingleValueFromTable() throws Exception {
		String name = query.readSingleValueFromTable("MySampleTable", "Name", "id=1");
		System.out.println("Single Value Fetched: " + name);
		assertEquals("Wrong value fetched", "Donny Darko", name);
	}
	
	@Test
	public void checkReadSingleValueFromTableReturnsEmptyStringIfNoMatch() throws SQLException, DatabaseLibraryException {
		String name = query.readSingleValueFromTable("MySampleTable", "Name", "id=23");
		System.out.println("Single Value Fetched: " + name);
		assertEquals("Value found", "", name);
	}
	
	// ========================================================
	//
	// Check Delete all Rows from Table
	//
	// ========================================================

	@Test
	public void checkdelete_all_rows_from_table() throws Exception {
		try {
			// Check first that table is not empty
			asserter.tableMustBeEmpty("MySampleTable");
			fail();
		} catch (DatabaseLibraryException e) {
			query.deleteAllRowsFromTable("MySampleTable");
			asserter.tableMustBeEmpty("MySampleTable");
		}
	}		
}
