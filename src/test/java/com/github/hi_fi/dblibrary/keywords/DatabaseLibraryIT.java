package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.github.hi_fi.dblibrary.keywords.DatabaseConnection;
import com.github.hi_fi.dblibrary.keywords.DatabaseLibraryException;

/**
 * Tests that use a database connection
 */
public class DatabaseLibraryIT {

	private DatabaseConnection databaseLibrary;
	private static Assert asserter;
	private static ConnectionHelper ch;
	

	// ========================================================
	//
	// Setup and Teardown on class-level
	//
	// ========================================================

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		asserter = new Assert();
		ch = new ConnectionHelper();
		ch.createTestDB();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		ch.deleteTestDB();
	}

	// ========================================================
	//
	// Setup and Teardown on method-level
	//
	// ========================================================
	
	@Before
	public void setUpTest() throws Exception {
		ch.initTestTables();
		databaseLibrary = ch.initDatabaseLibrary();
	}	
}
