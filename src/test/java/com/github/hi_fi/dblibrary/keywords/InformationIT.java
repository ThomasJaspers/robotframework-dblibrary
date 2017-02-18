package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class InformationIT {
	
	private Information info = new Information();
	private static ConnectionHelper ch = new ConnectionHelper();

	// ========================================================
	//
	// Setup and Teardown on class-level
	//
	// ========================================================

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		ch.createTestDB();
		ch.initDatabaseLibrary();
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
	
	// ========================================================
	//
	// Check Transaction Isolation Level
	//
	// ========================================================

	@Test
	public void checkGetTransactionIsolationLevel() throws Exception {
		String level = info.getTransactionIsolationLevel();
		System.out.println("Transaction Isolation Level: " + level);
		
		if ((level == null) || (level.equals(ConnectionHelper.H2_PASSWORD))) {
			fail("Empty Transaction Isolation Level");
		}
	}
	
	@Test
	public void checkGetPrimaryKeyColumnsForTable() throws Exception {
		String keys = info.getPrimaryKeyColumnsForTable("MYSAMPLETABLE");
		System.out.println("Primary Keys: " + keys);
		
		if ((keys == null) || (keys.equals(ConnectionHelper.H2_PASSWORD))) {
			fail("Empty Primary Key");
		}
	}	

}
