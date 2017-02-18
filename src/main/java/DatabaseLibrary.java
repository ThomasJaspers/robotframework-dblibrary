import org.robotframework.javalib.library.AnnotationLibrary;

public class DatabaseLibrary extends AnnotationLibrary {
	public static final String ROBOT_LIBRARY_SCOPE = "GLOBAL";

	public DatabaseLibrary() {
		super("com/github/hi_fi/dblibrary/keywords/**");
	}

	@Override
	public String getKeywordDocumentation(String keywordName) {
		if (keywordName.equals("__intro__"))
			return "This library supports database-related testing using the Robot Framework. It"
					+ "allows to establish a connection to a certain database to perform tests on"
					+ "the content of certain tables and/or views in that database. A possible"
					+ "scenario for its usage is a Web-Application that is storing data to the"
					+ "database based on some user actions (probably a quite common scenario). The"
					+ "actions in the Web-Application could be triggered using some tests based on"
					+ "Selenium and in the same test it will then be possible to check if the proper"
					+ "data has ended up in the database as expected. Of course there are various"
					+ "other scenarios where this library might be used." + "\n\n"
					+ "As this library is written in Java support for a lot of different database"
					+ "systems is possible. This only requires the corresponding driver-classes"
					+ "(usually in the form of a JAR from the database provider) and the knowledge"
					+ "of a proper JDBC connection-string." + "\n\n"
					+ "The following table lists some examples of drivers and connection strings"
					+ "for some popular databases. \n"
					+ "| *Database* | *Driver Name* | *Sample Connection String* | *Download Driver* | *Maven dependencies*  | \n"
					+ "| MySql | com.mysql.jdbc.Driver | jdbc:mysql://servername/dbname | http://dev.mysql.com/downloads/connector/j/ | http://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22mysql%22%20AND%20a%3A%22mysql-connector-java%22 | \n"
					+ "| Oracle | oracle.jdbc.driver.OracleDriver | jdbc:oracle:thin:@servername:port:dbname | http://www.oracle.com/technology/tech/java/sqlj_jdbc/htdocs/jdbc_faq.html | https://blogs.oracle.com/dev2dev/entry/oracle_maven_repository_instructions_for | \n"
					+ "\n\n" + "The examples in the description of the keywords is based on a database table"
					+ "named \"MySampleTable\" that has the following layout:" + "\n\n" + "MySampleTable:\n "
					+ "| *COLUMN* | *TYPE* |\n" + "| Id | Number |\n" + "| Name | String | \n" + "| EMail | String |\n"
					+ "| Postings | Number | \n" + "| State | Number | \n" + "| LastPosting | Timestamp |\n" + "\n\n"
					+ "*NOTE*: A lot of keywords that are targeted for Tables will work equally with "
					+ "Views as this is often no difference if Select-statements are performed." + "\n\n"
					+ "*Remote Library Support*\n\n"
					+ "At release 3.0 remote functionalities were removed from the library itself. "
					+ "Library can be used directly with jrobotremoteserver (https://github.com/ombre42/jrobotremoteserver/wiki/Getting-Started). "
					+ "Example can be found at project's acceptance tests: https://github.com/Hi-Fi/robotframework-dblibrary/tree/master/src/test/robotframework/acceptance";
		return super.getKeywordDocumentation(keywordName);
	}
}