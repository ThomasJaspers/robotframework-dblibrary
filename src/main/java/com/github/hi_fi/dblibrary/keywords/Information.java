package com.github.hi_fi.dblibrary.keywords;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class Information {

	@RobotKeyword("Returns a comma-separated list of the primary keys defined for the given "
			+ "table. The list if ordered by the name of the columns. " + "\n\n"
			+ "*NOTE*: Some database expect the table names to be written all in upper " + "case letters to be found. "
			+ "\n\n" + "Example: \n" + "| ${KEYS}= | Get Primary Key Columns For Table | MySampleTable | ")
	@ArgumentNames({ "Table name" })
	public String getPrimaryKeyColumnsForTable(String tableName) throws SQLException {

		String ret = "";

		DatabaseMetaData dbm = DatabaseConnection.getConnection().getMetaData();
		ResultSet rs = dbm.getPrimaryKeys(null, null, tableName);
		try {
			while (rs.next()) {
				ret = rs.getString("COLUMN_NAME") + ",";
			}
		} finally {
			rs.close();
		}

		// Remove the last ","
		if (ret.length() > 0) {
			ret = ret.substring(0, ret.length() - 1);
		}

		return ret;
	}
	
	@RobotKeyword("Returns a String value that contains the name of the transaction "
			+ "isolation level of the connection that is used for executing the tests. "
			+ "Possible return values are: TRANSACTION_READ_UNCOMMITTED, "
			+ "TRANSACTION_READ_COMMITTED, TRANSACTION_REPEATABLE_READ, "
			+ "TRANSACTION_SERIALIZABLE or TRANSACTION_NONE. " + "\n\n" + "Example: \n"
			+ "| ${TI_LEVEL}= | Get Transaction Isolation Level | ")
	public String getTransactionIsolationLevel() throws SQLException {

		String ret = "";

		int transactionIsolation = DatabaseConnection.getConnection().getTransactionIsolation();

		switch (transactionIsolation) {

		case Connection.TRANSACTION_NONE:
			ret = "TRANSACTION_NONE";
			break;

		case Connection.TRANSACTION_READ_COMMITTED:
			ret = "TRANSACTION_READ_COMMITTED";
			break;

		case Connection.TRANSACTION_READ_UNCOMMITTED:
			ret = "TRANSACTION_READ_UNCOMMITTED";
			break;

		case Connection.TRANSACTION_REPEATABLE_READ:
			ret = "TRANSACTION_REPEATABLE_READ";
			break;

		case Connection.TRANSACTION_SERIALIZABLE:
			ret = "TRANSACTION_SERIALIZABLE";
			break;
		}

		return ret;
	}
}
