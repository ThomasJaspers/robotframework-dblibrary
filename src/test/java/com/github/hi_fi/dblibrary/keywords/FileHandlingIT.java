package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileHandlingIT {

	private FileHandling fileHandling = new FileHandling(); 
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
	
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	// ========================================================
	//
	// Compare Query Result To File
	//
	// ========================================================
	
	@Test 
	public void checkCompareQueryResultToFileWhereFileMatches() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";	
		FileWriter fstream = new FileWriter(myFileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Donny Darko|donny.darko@robot.org|\n");
	    out.write("Darth Vader|darth.vader@starwars.universe|\n");
	    out.close();
	    fileHandling.compareQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	}

	@Test (expected=DatabaseLibraryException.class)
	public void checkCompareQueryResultToFileWhereFileHasTooFewRows() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";	
		FileWriter fstream = new FileWriter(myFileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Donny Darko|donny.darko@robot.org|\n");
	    out.close();
	    fileHandling.compareQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	}

	@Test (expected=DatabaseLibraryException.class)
	public void checkCompareQueryResultToFileWhereFileHasTooManyRows() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";	
		FileWriter fstream = new FileWriter(myFileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Donny Darko|donny.darko@robot.org|\n");
	    out.write("Darth Vader|darth.vader@starwars.universe|\n");
	    out.write("Darth Vader|darth.vader@starwars.universe|\n");
	    out.close();
	    fileHandling.compareQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	}

	@Test (expected=DatabaseLibraryException.class)
	public void checkCompareQueryResultToFileWhereFileHasInvalidRow() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";	
		FileWriter fstream = new FileWriter(myFileName);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write("Donny Darko|donny.darko@robot.org|\n");
	    out.write("Darth Vader|lukes.father@starwars.universe|\n");
	    out.close();
	    fileHandling.compareQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	}

	@Test (expected=FileNotFoundException.class)
	public void checkCompareQueryResultToFileWhereFileNotFound() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";	
		fileHandling.compareQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	}
	
	// ========================================================
	//
	// Store Query Result To File
	//
	// ========================================================
	
	@Test 
	public void checkStoreQueryResultToFile() throws Exception {
		folder.getRoot();
		String myFileName = folder.getRoot().getPath() + File.separator + "myFile.txt";
		fileHandling.storeQueryResultToFile("select name, email from mySampleTable order by Id", 
				myFileName);
	    FileReader fr = new FileReader(myFileName); 
	    BufferedReader br = new BufferedReader(fr);
	    try {
	    assertEquals("Wrong value written to file","Donny Darko|donny.darko@robot.org|",br.readLine());
	    assertEquals("Wrong value written to file","Darth Vader|darth.vader@starwars.universe|",br.readLine());
	    assertEquals("File is longer than expected",false, br.ready());
	    } finally {
	    	br.close();
	    }
	}
}
