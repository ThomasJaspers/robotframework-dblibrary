package com.github.hi_fi.dblibrary.keywords;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;

@RobotKeywords
public class DatabaseConnection {

	private static String defaultAlias = "default";
	private static String currentConnectionAlias = "";

	private static Map<String, Connection> connectionMap = new HashMap<String, Connection>();

	public DatabaseConnection() {
	}

	@RobotKeyword("Activates the database connection with given alias. \n"
			+ "Please note that connection has to be opened earlier.\n\n" + "Example: \n"
			+ "| Activate Database Connection | ownAlias |")
	@ArgumentNames({ "Database alias=default" })
	public void activateDatabaseConnection(String... aliasParam) {
		String alias = aliasParam.length > 0 ? aliasParam[0] : defaultAlias;
		if (DatabaseConnection.connectionMap.containsKey(alias)) {
			DatabaseConnection.currentConnectionAlias = alias;
		} else {
			throw new IllegalStateException(String.format(
					"No connection open with alias %s. Did you forget to run 'Connect To Database' before?", alias));
		}
	}

	@RobotKeyword("Establish the connection to the database. This is mandatory before any of"
			+ "the other keywords can be used and should be ideally done during the "
			+ "suite setup phase. To avoid problems ensure to close the connection again "
			+ "using the disconnect-keyword.\n\n"
			+ "It must be ensured that the JAR-file containing the given driver can be "
			+ "found from the CLASSPATH when starting robot. Furthermore it must be "
			+ "noted that the connection string is database-specific and must be valid of course.\n\n"
			+ "If alias is given, connection can be later referred with that. If alias was in use, existing connection "
			+ "is replaced with new one\n\n" + "" + "Example: \n"
			+ "| Connect To Database | com.mysql.jdbc.Driver | jdbc:mysql://my.host.name/myinstance | UserName | ThePassword | default |")
	@ArgumentNames({ "Driver class name", "Connection string", "Database username", "Database password",
			"Database alias=default" })
	public void connectToDatabase(String driverClassName, String connectString, String dbUser, String dbPassword,
			String... aliasParam)
			throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		String alias = aliasParam.length > 0 ? aliasParam[0] : defaultAlias;
		Class.forName(driverClassName).newInstance();
		setConnection(DriverManager.getConnection(connectString, dbUser, dbPassword), alias);
	}

	@RobotKeyword("Releases the existing connection to the database. In addition this"
			+ "keyword will log any SQLWarnings that might have been occurred on the connection.\n"
			+ "If current connection is closed and there's still some open, you have to activate that manually.\n"
			+ "Example:\n" + "| Disconnect from Database | default |")
	@ArgumentNames({ "Database alias=default" })
	public void disconnectFromDatabase(String... aliasParam) throws SQLException {
		String alias = aliasParam.length > 0 ? aliasParam[0] : defaultAlias;
		Connection disconnectingConnection = getConnection(alias);

		System.out.println(String.format("SQL Warnings on this connection (%s): %s", alias,
				disconnectingConnection.getWarnings()));
		disconnectingConnection.close();
		DatabaseConnection.connectionMap.remove(alias);
		if (alias.equals(DatabaseConnection.currentConnectionAlias)) {
			DatabaseConnection.currentConnectionAlias = "";
		}
	}

	private void setConnection(Connection connection, String alias) {
		DatabaseConnection.connectionMap.put(alias, connection);
		DatabaseConnection.currentConnectionAlias = alias;
	}

	public static Connection getConnection() {
		return getConnection(currentConnectionAlias);
	}

	public static Connection getConnection(String alias) {
		if (!DatabaseConnection.connectionMap.containsKey(alias)) {
			throw new IllegalStateException(String.format(
					"No connection open with alias %s. Did you forget to run 'Connect To Database' before?", alias));
		}
		return DatabaseConnection.connectionMap.get(alias);
	}
}