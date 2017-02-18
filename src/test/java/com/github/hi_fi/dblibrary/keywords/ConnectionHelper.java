package com.github.hi_fi.dblibrary.keywords;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ConnectionHelper {
	
	public static final String H2_DRIVER_CLASSNAME = "org.h2.Driver";
	public static final String H2_URL = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
	public static final String H2_USER = "sa";
	public static final String H2_PASSWORD = "";
	
	public void createTestDB() throws Exception {
		Class.forName(H2_DRIVER_CLASSNAME).newInstance();
		Connection con = DriverManager.getConnection(H2_URL,
				H2_USER, H2_PASSWORD);

		Statement stmt = con.createStatement();
		stmt
				.execute("CREATE TABLE MySampleTable (Id Integer NOT NULL, Name VARCHAR(256), "
						+ "EMail VARCHAR(256), "
						+ "Postings Integer, State Integer, LastPosting Timestamp)");
		stmt.execute("CREATE TABLE EmptyTable (Id Integer, Name VARCHAR(256))");
		stmt.execute("CREATE TABLE ReferenceTable (Num Integer)");

		stmt.execute("ALTER TABLE MySampleTable ADD CONSTRAINT MyUniqKey UNIQUE (Id)");
		stmt.execute("ALTER TABLE MySampleTable ADD CONSTRAINT MyPrimKey PRIMARY KEY (Id)");
		
		stmt.close();
		con.close();
	}
	
	public void deleteTestDB() throws Exception {
		Class.forName(H2_DRIVER_CLASSNAME).newInstance();
		Connection con = DriverManager.getConnection(H2_URL,
				H2_USER, H2_PASSWORD);

		Statement stmt = con.createStatement();
		stmt.execute("DROP TABLE MySampleTable");
		stmt.execute("DROP TABLE EmptyTable");
		stmt.execute("DROP TABLE ReferenceTable");
		
		stmt.close();
		con.close();
	}

	public void initTestTables() throws Exception {
		Class.forName(H2_DRIVER_CLASSNAME).newInstance();
		Connection con = DriverManager.getConnection(H2_URL,
				H2_USER, H2_PASSWORD);

		Statement stmt = con.createStatement();
		stmt.execute("DELETE FROM MySampleTable");
		stmt.execute("DELETE FROM EmptyTable");
		stmt.execute("DELETE FROM ReferenceTable");
		stmt
				.executeUpdate("INSERT INTO MySampleTable VALUES(1, 'Donny Darko', "
						+ "'donny.darko@robot.org', 1001, 1, '2010-02-26 12:42:58.0000')");
		stmt
				.executeUpdate("INSERT INTO MySampleTable VALUES(2, 'Darth Vader', "
						+ "'darth.vader@starwars.universe', 123, 2, '2010-02-27 11:41:45.0000')");

		stmt.executeUpdate("INSERT INTO ReferenceTable VALUES(1)");
		stmt.executeUpdate("INSERT INTO ReferenceTable VALUES(2)");

		stmt.close();
		con.close();
	}
	
	public DatabaseConnection initDatabaseLibrary() throws Exception {
		DatabaseConnection databaseLibrary = new DatabaseConnection();
		databaseLibrary.connectToDatabase(H2_DRIVER_CLASSNAME,
				H2_URL, H2_USER, H2_PASSWORD);
		return databaseLibrary;
	}	
}
