package com.github.hi_fi.dblibrary.keywords;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class AssertIT {

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
	// Check Check Content for row identified by Rownum
	//
	// ========================================================

	@Test
	public void checkCheckContentIdentifiedbyRownum() throws Exception {
		asserter.checkContentForRowIdentifiedByRownum("Id,Name,Postings", "1|Donny Darko|1001", "MySampleTable", "1");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkCheckContentIdentifiedbyRownum_WrongValues() throws DatabaseLibraryException, Exception {
		asserter.checkContentForRowIdentifiedByRownum("Id,Name,Postings", "1|Donny Dar|1001", "MySampleTable", "1");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkCheckContentIdentifiedbyRownum_NoRecordFound() throws DatabaseLibraryException, Exception {
		asserter.checkContentForRowIdentifiedByRownum("Id,Name,Postings", "1|Donny Dar|1001", "MySampleTable", "100");
	}

	// ========================================================
	//
	// Check Check Content for row identified by where-clause
	//
	// ========================================================

	@Test
	public void checkCheckContentIdentifiedbyWhereClause() throws Exception {
		asserter.checkContentForRowIdentifiedByWhereClause("Id,Name,Postings", "1|Donny Darko|1001", "MySampleTable",
				"id=1");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkCheckContentIdentifiedbyWhereClause_WrongValues() throws DatabaseLibraryException, Exception {
		asserter.checkContentForRowIdentifiedByWhereClause("Id,Name,Postings", "1|Donny Darko|100", "MySampleTable",
				"id=1");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkCheckContentIdentifiedbyWhereClause_NoRecordFound() throws DatabaseLibraryException, Exception {
		asserter.checkContentForRowIdentifiedByWhereClause("Id,Name,Postings", "1|Donny Darko|100", "MySampleTable",
				"id=100");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkCheckContentIdentifiedbyWhereClause_MoreThanOneRecordFound()
			throws DatabaseLibraryException, Exception {
		asserter.checkContentForRowIdentifiedByWhereClause("Id,Name,Postings", "1|Donny Darko|100", "MySampleTable",
				"id=1 or id=2");
	}

	// ========================================================
	//
	// Check Table Must be Empty
	//
	// ========================================================

	@Test
	public void checktable_must_be_empty_OnEmptyTable() throws Exception {
		asserter.tableMustBeEmpty("EmptyTable");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_be_empty_OnTableNotEmpty() throws DatabaseLibraryException, Exception {
		asserter.tableMustBeEmpty("MySampleTable");
	}

	// ========================================================
	//
	// Check Table must Exists
	//
	// ========================================================

	@Test
	public void checkTableMustExist_ThatExists() throws Exception {
		asserter.tableMustExist("EMPTYTABLE");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkTableMustExist_ThatDoesNotExist() throws DatabaseLibraryException, Exception {
		asserter.tableMustExist("WRONG_NAME");
	}

	// ========================================================
	//
	// Check Table Must contain less than number of rows
	//
	// ========================================================

	@Test
	public void checktable_must_contain_less_than_number_of_rows() throws Exception {
		asserter.tableMustContainLessThanNumberOfRows("MySampleTable", "3");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_contain_less_than_number_of_rows_SameNumbers()
			throws DatabaseLibraryException, Exception {
		asserter.tableMustContainLessThanNumberOfRows("MySampleTable", "2");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_contain_less_than_number_of_rows_LessNumbers()
			throws DatabaseLibraryException, Exception {
		asserter.tableMustContainLessThanNumberOfRows("MySampleTable", "1");
	}

	// ========================================================
	//
	// Check Table Must contain more than number of rows
	//
	// ========================================================

	@Test
	public void checktable_must_contain_more_than_number_of_rows() throws Exception {
		asserter.tableMustContainMoreThanNumberOfRows("MySampleTable", "1");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_contain_more_than_number_of_rows_SameNumbers()
			throws DatabaseLibraryException, Exception {
		asserter.tableMustContainMoreThanNumberOfRows("MySampleTable", "2");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_contain_more_than_number_of_rows_MoreNumbers() throws Exception {
		asserter.tableMustContainMoreThanNumberOfRows("MySampleTable", "200");
	}

	// ========================================================
	//
	// Check Table Must contain number of rows
	//
	// ========================================================

	@Test
	public void checktable_must_contain_number_of_rows() throws Exception {
		asserter.tableMustContainNumberOfRows("MySampleTable", "2");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checktable_must_contain_number_of_rows_WrongNumber() throws DatabaseLibraryException, Exception {
		asserter.tableMustContainNumberOfRows("MySampleTable", "5");
	}

	// ========================================================
	//
	// Check Tables Must contain same amount of rows
	//
	// ========================================================

	@Test
	public void checkTablesMustContainSameAmountOfRows() throws Exception {
		asserter.tablesMustContainSameAmountOfRows("MySampleTable", "ReferenceTable");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkTablesMustContainSameAmountOfRows_ButTheyDoNot() throws DatabaseLibraryException, Exception {
		asserter.tablesMustContainSameAmountOfRows("MySampleTable", "EmptyTable");
	}

	@Test
	public void checkVerifyNumberOfRowsMatchingWhereSuccessOneMatch() throws Exception {
		asserter.verifyNumberOfRowsMatchingWhere("MySampleTable", "EMail='donny.darko@robot.org'", "1");
	}

	@Test
	public void checkVerifyNumberOfRowsMatchingWhereSuccessNoMatch() throws Exception {
		asserter.verifyNumberOfRowsMatchingWhere("MySampleTable", "EMail='batman@robot.org'", "0");
	}

	@Test(expected = DatabaseLibraryException.class)
	public void checkVerifyNumberOfRowsMatchingWhereFailure() throws DatabaseLibraryException, Exception {
		asserter.verifyNumberOfRowsMatchingWhere("MySampleTable", "Postings > 0", "1");
	}
	
	@Test
	public void checktransactionIsolationLevelMustBe() throws Exception {
		asserter.transactionIsolationLevelMustBe("TRANSACTION_READ_COMMITTED");
	}	
	
	@Test(expected=DatabaseLibraryException.class)
	public void checktransactionIsolationLevelMustBe_WithWrongLevelName() throws DatabaseLibraryException, Exception {
		asserter.transactionIsolationLevelMustBe("TRANSACTION_REPEATABLE_READ");
	}
	
	// ========================================================
	//
	// Check Primary Key Column Information
	//
	// ========================================================	
	
	@Test
	public void checkCheckPrimaryKeyColumnsForTable() throws Exception {
		asserter.checkPrimaryKeyColumnsForTable("MYSAMPLETABLE", "Id");
	}			

	@Test(expected=DatabaseLibraryException.class)
	public void checkCheckPrimaryKeyColumnsForTable_NoMatch() throws DatabaseLibraryException, Exception {
		asserter.checkPrimaryKeyColumnsForTable("MYSAMPLETABLE", "Ids");
	}			
	
	@Test(expected=DatabaseLibraryException.class)
	public void checkCheckPrimaryKeyColumnsForTable_WrongTableName() throws DatabaseLibraryException, Exception {
		asserter.checkPrimaryKeyColumnsForTable("WrongTable", "Id");
	}
	
	// Tests for "Row Should Not Exist In Table"
	@Test
	public void checkRowShouldNotExistInTable() throws SQLException, DatabaseLibraryException {
		asserter.rowShouldNotExistInTable("MySampleTable", "Name='John Doe'");
	}
	
	@Test
	public void checkRowShouldNotExistInTableFailsIfRowExists() throws SQLException, DatabaseLibraryException {
		try {
			asserter.rowShouldNotExistInTable("MySampleTable", "Name='Darth Vader'");
			fail();
		} catch(DatabaseLibraryException e) {
			assertEquals("Row exists (but should not) for where-clause: Name='Darth Vader' in table: MySampleTable", e.getMessage());
		}
	}
}
