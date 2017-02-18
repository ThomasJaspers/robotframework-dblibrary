package com.github.hi_fi.dblibrary.keywords;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class Assert {
	
	@RobotKeyword("This keyword can be used to check for proper content inside a specific "
			+ "row in a database table. For this it is possible to give a "
			+ "comma-separated list of column names in the first parameter and a "
			+ "pipe-separated list of values in the second parameter. Then the name of "
			+ "the table and the rownum to check must be passed to this keyword. The "
			+ "corresponding values are then read from that row in the given table and "
			+ "compared to the expected values. If all values match the teststep will "
			+ "pass, otherwise it will fail. " + "\n\n" + "Example: \n"
			+ "| Check Content for Row Identified by Rownum | Name,EMail | John Doe|john.doe@x-files | MySampleTable | 4 | ")
	@ArgumentNames({ "Column names (comma separated)", "Expected values (pipe separated)", "Table name",
			"Number of row to check" })
	public void checkContentForRowIdentifiedByRownum(String columnNames, String expectedValues, String tableName,
			String rowNumValue) throws SQLException, DatabaseLibraryException {

		long rowNum = Long.valueOf(rowNumValue);

		String sqlString = "select " + columnNames + " from " + tableName;

		String[] columns = columnNames.split(",");
		String[] values = expectedValues.split("\\|");

		Statement stmt = DatabaseConnection.getConnection().createStatement();
		try {
			stmt.executeQuery(sqlString);
			ResultSet rs = (ResultSet) stmt.getResultSet();

			long count = 0;
			while (rs.next()) {

				count++;
				if (count == rowNum) {

					for (int i = 0; i < columns.length; i++) {
						String fieldValue = rs.getString(columns[i]);
						System.out.println(columns[i] + " -> " + fieldValue);

						if (values[i].equals("(NULL)")) {
							values[i] = "";
						}

						if (!fieldValue.equals(values[i])) {
							throw new DatabaseLibraryException(
									"Value found: '" + fieldValue + "'. Expected: '" + values[i] + "'");
						}
					}
					break;
				}
			}

			// Rownum does not exist
			if (count != rowNum) {
				throw new DatabaseLibraryException("Given rownum does not exist for statement: " + sqlString);
			}

		} finally {
			// stmt.close() automatically takes care of its ResultSet, so no
			// rs.close()
			stmt.close();
		}
	}
	
	@RobotKeyword("This keyword can be used to check for proper content inside a specific "
			+ "row in a database table. For this it is possible to give a "
			+ "comma-separated list of column names in the first parameter and a "
			+ "pipe-separated list of values in the second parameter. Then the name of "
			+ "the table and a statement used in the where-clause to identify a concrete "
			+ "row. The corresponding values are then read from the row identified this "
			+ "way and compared to the expected values. If all values match the teststep "
			+ "will pass, otherwise it will fail. " + "\n\n"
			+ "If the where-clause will select more or less than exactly one row the " + "test will fail. " + "\n\n"
			+ "Example: \n"
			+ "| Check Content for Row Identified by WhereClause | Name,EMail | John Doe|john.doe@x-files | MySampleTable | Postings=14 | ")
	@ArgumentNames({ "Column names (comma separated)", "Expected values (pipe separated)", "Table name",
			"Where clause to identify the row" })
	public void checkContentForRowIdentifiedByWhereClause(String columnNames, String expectedValues, String tableName,
			String whereClause) throws SQLException, DatabaseLibraryException {

		String sqlString = "select " + columnNames + " from " + tableName + " where " + whereClause;

		String[] columns = columnNames.split(",");
		String[] values = expectedValues.split("\\|");

		Statement stmt = DatabaseConnection.getConnection().createStatement();
		try {
			stmt.executeQuery(sqlString);
			ResultSet rs = (ResultSet) stmt.getResultSet();

			long count = 0;
			while (rs.next()) {
				count++;
				if (count == 1) {

					for (int i = 0; i < columns.length; i++) {
						String fieldValue = rs.getString(columns[i]);
						System.out.println(columns[i] + " -> " + fieldValue);

						if (values[i].equals("(NULL)")) {
							values[i] = "";
						}

						if (!fieldValue.equals(values[i])) {
							throw new DatabaseLibraryException(
									"Value found: '" + fieldValue + "'. Expected: '" + values[i] + "'");
						}
					}
				}

				// Throw exception if more than one row is selected by the given
				// "where-clause"
				if (count > 1) {
					throw new DatabaseLibraryException(
							"More than one row fetched by given where-clause for statement: " + sqlString);
				}
			}

			// Throw exception if no row was fetched by given where-clause
			if (count == 0) {
				throw new DatabaseLibraryException("No row fetched by given where-clause for statement: " + sqlString);
			}

		} finally {
			stmt.close();
		}
	}
	
	@RobotKeyword("Checks that the primary key columns of a given table match the columns "
			+ "given as a comma-separated list. Note that the given list must be ordered "
			+ "by the name of the columns. Upper and lower case for the columns as such "
			+ "is ignored by comparing the values after converting both to lower case. " + "\n\n"
			+ "*NOTE*: Some database expect the table names to be written all in upper " + "case letters to be found. "
			+ "\n\n" + "Example: \n" + "| Check Primary Key Columns For Table | MySampleTable | Id,Name |")
	@ArgumentNames({ "Table name", "Comma separated list of primary key columns to check" })
	public void checkPrimaryKeyColumnsForTable(String tableName, String columnList)
			throws SQLException, DatabaseLibraryException {

		String keys = new Information().getPrimaryKeyColumnsForTable(tableName);

		columnList = columnList.toLowerCase();
		keys = keys.toLowerCase();

		if (!columnList.equals(keys)) {
			throw new DatabaseLibraryException("Given column list: " + columnList + " Keys found: " + keys);
		}
	}
	
	@RobotKeyword("This keyword can be used to check the inexistence of content inside a "
			+ "specific row in a database table defined by a where-clause. This can be "
			+ "used to validate an exclusion of specific data from a table. " + "\n\n" + "Example: \n"
			+ "| Row Should Not Exist In Table | MySampleTable | Name='John Doe' | " + "\n\n"
			+ "This keyword was introduced in version 1.1. ")
	@ArgumentNames({ "Table to check", "Where clause" })
	public void rowShouldNotExistInTable(String tableName, String whereClause)
			throws SQLException, DatabaseLibraryException {

		String sql = "select * from " + tableName + " where " + whereClause;
		Statement stmt = DatabaseConnection.getConnection().createStatement();
		try {
			stmt.executeQuery(sql);
			ResultSet rs = (ResultSet) stmt.getResultSet();
			if (rs.next() == true) {
				throw new DatabaseLibraryException(
						"Row exists (but should not) for where-clause: " + whereClause + " in table: " + tableName);
			}
		} finally {
			stmt.close();
		}
	}
	
	@RobotKeyword("Checks that the given table has no rows. It is a convenience way of using "
			+ "the 'Table Must Contain Number Of Rows' with zero for the amount of rows.\n\n" + "Example: \n"
			+ "| Table Must Be Empty | MySampleTable |")
	@ArgumentNames({ "Table name" })
	public void tableMustBeEmpty(String tableName) throws SQLException, DatabaseLibraryException {
		tableMustContainNumberOfRows(tableName, "0");
	}
	
	@RobotKeyword("This keyword checks that a given table contains less than the given "
			+ "amount of rows. For the example this means that the table \"MySampleTable\"\n\n"
			+ "must contain anything between 0 and 1000 rows, otherwise the teststep " + "will fail. " + "\n\n"
			+ "Example: \n" + "| Table Must Contain Less Than Number Of Rows | MySampleTable | 1001 | ")
	@ArgumentNames({ "Table name", "Number of rows too high" })
	public void tableMustContainLessThanNumberOfRows(String tableName, String rowNumValue)
			throws SQLException, DatabaseLibraryException {

		long rowNum = Long.valueOf(rowNumValue);

		long num = getNumberOfRows(tableName, rowNum);
		if (num >= rowNum) {
			throw new DatabaseLibraryException("Expecting less than" + rowNum + " rows, fetched: " + num);
		}
	}
	
	@RobotKeyword("This keyword checks that a given table contains more than the given "
			+ "amount of rows. For the example this means that the table \"MySampleTable\""
			+ "must contain 100 or more rows, otherwise the teststep will fail. " + "\n\n" + "Example: \n"
			+ "| Table Must Contain More Than Number Of Rows | MySampleTable | 99 | ")
	@ArgumentNames({ "Table name", "Number of rows too low" })
	public void tableMustContainMoreThanNumberOfRows(String tableName, String rowNumValue)
			throws SQLException, DatabaseLibraryException {

		long rowNum = Long.valueOf(rowNumValue);

		long num = getNumberOfRows(tableName, rowNum + 1);
		if (num <= rowNum) {
			throw new DatabaseLibraryException("Expecting more than" + rowNum + " rows, fetched: " + num);
		}
	}
	
	@RobotKeyword("This keyword checks that a given table contains a given amount of rows. "
			+ "For the example this means that the table \"MySampleTable\" must contain "
			+ " exactly 14 rows, otherwise the teststep will fail.\n\n" + " Example: \n"
			+ "| Table Must Contain Number Of Rows | MySampleTable | 14 |")
	@ArgumentNames({ "Table name", "Amount of rows expected" })
	public void tableMustContainNumberOfRows(String tableName, String rowNumValue)
			throws SQLException, DatabaseLibraryException {

		long rowNum = Long.valueOf(rowNumValue);

		long num = getNumberOfRows(tableName, rowNum + 1);
		if (num != rowNum) {
			throw new DatabaseLibraryException("Expecting " + rowNum + " rows, fetched: " + num);
		}
	}

	@RobotKeyword("This keyword checks that two given database tables have the same amount " + "of rows. " + "\n\n"
			+ "Example: \n" + "| Tables Must Contain Same Amount Of Rows | MySampleTable | MyCompareTable | ")
	@ArgumentNames({ "First table name", "Second table name" })
	public void tablesMustContainSameAmountOfRows(String firstTableName, String secondTableName)
			throws SQLException, DatabaseLibraryException {

		long firstNum = getNumberOfRows(firstTableName);
		long secondNum = getNumberOfRows(secondTableName);

		if (firstNum != secondNum) {
			throw new DatabaseLibraryException("Expecting same amount of rows, but table " + firstTableName + " has "
					+ firstNum + " rows and table " + secondTableName + " has " + secondNum + " rows!");
		}
	}
	
	@RobotKeyword("Checks that a table with the given name exists. If the table does not exist the test will fail.\n\n"
			+ "*NOTE*: Some database expect the table names to be written all in upper case letters to be found.\n\n"
			+ "Example: \n " + "| Table Must Exist | MySampleTable |")
	@ArgumentNames({ "Table name" })
	public void tableMustExist(String tableName) throws SQLException, DatabaseLibraryException {

		DatabaseMetaData dbm = DatabaseConnection.getConnection().getMetaData();
		ResultSet rs = dbm.getTables(null, null, tableName, null);
		try {
			if (!rs.next()) {
				throw new DatabaseLibraryException("Table: " + tableName + " was not found");
			}
		} finally {
			rs.close();
		}
	}
	
	@RobotKeyword("Can be used to check that the database connection used for executing "
			+ "tests has the proper transaction isolation level. The string parameter "
			+ "accepts the following values in a case-insensitive manner: "
			+ "TRANSACTION_READ_UNCOMMITTED, TRANSACTION_READ_COMMITTED, "
			+ "TRANSACTION_REPEATABLE_READ, TRANSACTION_SERIALIZABLE or " + "TRANSACTION_NONE. " + "\n\n"
			+ "Example: \n" + "| Transaction Isolation Level Must Be | TRANSACTION_READ_COMMITTED | ")
	@ArgumentNames({ "Isolation level" })
	public void transactionIsolationLevelMustBe(String levelName) throws SQLException, DatabaseLibraryException {

		String transactionName = new Information().getTransactionIsolationLevel();

		if (!transactionName.equals(levelName)) {
			throw new DatabaseLibraryException(
					"Expected Transaction Isolation Level: " + levelName + " Level found: " + transactionName);
		}

	}
	
	@RobotKeyword("This keyword checks that a given table contains a given amount of rows "
			+ "matching a given WHERE clause. " + "\n\n"
			+ "For the example this means that the table \"MySampleTable\" must contain "
			+ "exactly 2 rows matching the given WHERE, otherwise the teststep will " + "fail. " + "\n\n"
			+ "Example: \n" + "| Verify Number Of Rows Matching Where | MySampleTable | email=x@y.net | 2 | ")
	@ArgumentNames({ "Table to check", "Where clause", "Expected number of rows" })
	public void verifyNumberOfRowsMatchingWhere(String tableName, String where, String rowNumValue)
			throws SQLException, DatabaseLibraryException {

		long rowNum = Long.valueOf(rowNumValue);

		long num = getNumberOfRows(tableName, where, (rowNum + 1));
		if (num != rowNum) {
			throw new DatabaseLibraryException("Expecting " + rowNum + " rows, fetched: " + num);
		}
	}

	private long getNumberOfRows(String tableName) throws SQLException {
		return getNumberOfRows(tableName, Long.MAX_VALUE);
	}
	
	private long getNumberOfRows(String tableName, long limit) throws SQLException {
		return getNumberOfRows(tableName, null, limit);
	}
	
	private long getNumberOfRows(String tableName, String where, long limit) throws SQLException {

		// Let's first try with count(*), but this is not supported by all
		// databases.
		// In this case an exception will be thrown and we will read the amount
		// of records the "hard way", but luckily limited by the amount of rows
		// expected,
		// so that this might not be too bad.
		long num = -1;
		try {
			String sql = "select count(*) from " + tableName;
			if (where != null) {
				sql = sql + " where " + where;
			}
			Statement stmt = DatabaseConnection.getConnection().createStatement();
			try {
				stmt.executeQuery(sql);
				ResultSet rs = (ResultSet) stmt.getResultSet();
				rs.next();
				num = rs.getLong("count(*)");
			} finally {
				// stmt.close() automatically takes care of its ResultSet, so no
				// rs.close()
				stmt.close();
			}
		} catch (SQLException e) {
			String sql = "select * from " + tableName;
			if (where != null) {
				sql = sql + " where " + where;
			}
			Statement stmt = DatabaseConnection.getConnection().createStatement();
			try {
				stmt.executeQuery(sql);
				ResultSet rs = (ResultSet) stmt.getResultSet();
				num = 0;
				while ((rs.next()) && (num < limit)) {
					num++;
				}
			} finally {
				// stmt.close() automatically takes care of its ResultSet, so no
				// rs.close()
				stmt.close();
			}
		}
		return num;
	}
}
