package com.github.hi_fi.dblibrary.keywords;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.robotframework.javalib.annotation.ArgumentNames;
import org.robotframework.javalib.annotation.RobotKeyword;
import org.robotframework.javalib.annotation.RobotKeywords;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RobotKeywords
public class FileHandling {
	
	Query queryRunner;
	
	public FileHandling() {
		queryRunner = new Query();
	}

	@RobotKeyword("Executes the given SQL compares the result to expected results stored in "
			+ "a file. Results are stored as strings separated with pipes ('|') with a "
			+ "pipe following the last column. Rows are separated with a newline. " + "\n\n"
			+ "To ensure compares work correctly The SQL query should a) specify an "
			+ "order b) convert non-string fields (especially dates) to a specific " + "format " + "\n\n"
			+ "storeQueryResultToFile can be used to generate expected result files " + "\n\n"
			+ "*NOTE*: If using keyword remotely, file need to be trasfered to server some "
			+ "other way; this library is not doing the transfer." + "\n\n" + "Example: \n"
			+ "| Compare Query Result To File | Select phone, email from addresses where last_name = 'Johnson' | query_result.txt | ")
	@ArgumentNames({ "Query to execute", "File to compare results with" })
	public void compareQueryResultToFile(String sqlString, String fileName)
			throws SQLException, DatabaseLibraryException, FileNotFoundException {

		Statement stmt = DatabaseConnection.getConnection().createStatement();
		int numDiffs = 0;
		int maxDiffs = 10;
		String diffs = "";
		try {
			stmt.execute(sqlString);
			ResultSet rs = (ResultSet) stmt.getResultSet();
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			String actRow;
			String expRow;

			int row = 0;
			while (rs.next() && (numDiffs < maxDiffs)) {
				actRow = "";
				row++;
				for (int i = 1; i <= numberOfColumns; i++) {
					actRow += rs.getString(i) + '|';
				}
				expRow = br.readLine();
				if (!actRow.equals(expRow)) {
					numDiffs++;
					diffs += "Row " + row + " does not match:\nexp: " + expRow + "\nact: " + actRow + "\n";
				}
			}
			if (br.ready() && numDiffs < maxDiffs) {
				numDiffs++;
				diffs += "More rows in expected file than in query result\n";
			}
			br.close();
		} catch (FileNotFoundException e) {
			throw e;
		} catch (IOException e) {
			numDiffs++;
			diffs += "Fewer rows in expected file than in query result\n";
		} finally {
			// stmt.close() automatically takes care of its ResultSet, so no
			// rs.close()
			stmt.close();
			if (numDiffs > 0)
				throw new DatabaseLibraryException(diffs);
		}
	}
	
	@RobotKeyword("Exports the data from the given table into a file that is stored at the "
			+ "given location. The where-clause can (and should) be used to narrow the "
			+ "amount of rows that is exported this way. The file is stored in some "
			+ "simple XML-format and can be imported again to the database using the "
			+ "\"Import Data From File\" keyword. This way it is possible to store a set "
			+ "of testdata permanently retrieving it for example from some Live- or "
			+ "Demosystem. This keyword will probably have some issues if millions of "
			+ "rows are exported/imported using it. " + "\n\n"
			+ "*NOTE*: If using keyword remotely, file need to be trasfered to server some "
			+ "other way; this library is not doing the transfer.\n\n"
			+ "The keyword returns the amount of rows written to the XML-file. " + "\n\n"
			+ "Example: | ${ROWSEXPORTED}= | MySampleTable | /tmp/mysampletable.xml | Timestamp > sysdate-50 |")
	@ArgumentNames({ "Table name", "Export file path (including name)", "Where clause=''" })
	public int exportDataFromTable(String tableName, String filePath, String... whereClause)
			throws SQLException, DatabaseLibraryException, ParserConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		new Assert().tableMustExist(tableName);
		String query = "Select * from " + tableName;
		if (whereClause.length > 0) {
			query += " where " + whereClause[0];
		}
		List<HashMap<String, Object>> data = queryRunner.executeSql(query);
		return writeQueryResultsToFile(tableName, filePath, data);
	}
	
	@RobotKeyword("This keyword reads data from a XML-file and stores the corresponding data "
			+ "to the database. The file must have been created using the "
			+ "\"Export Data From Table\" keyword or it must be created manually in the "
			+ "exact format. The XML-file contains not only the data as such, but also "
			+ "the name of the schema and table from which the data was exported. The "
			+ "same information is used for the import. " + "\n\n"
			+ "*NOTE*: If using keyword remotely, file need to be trasfered from server some "
			+ "other way; this library is not doing the transfer.\n\n"
			+ "The keyword returns the amount of rows that have been successfully stored " + "to the database table. "
			+ " " + "Example: | ${ROWSIMPORTED}= | /tmp/mysampletable.xml | ")
	@ArgumentNames({ "File containing XML data to be imported" })
	public int importDataFromFile(String filePath) throws ParserConfigurationException, SAXException, IOException, SQLException {
		Document doc = this.parseXMLDocumentFromFile(filePath);
		String table = ((Element) doc.getElementsByTagName("Export").item(0)).getAttribute("table");
		NodeList rows = doc.getElementsByTagName("Row");
		String query = "INSERT INTO "+table+" VALUES ";
		List<String> insertList = new ArrayList<String>();
		for (int rowIndex = 0; rowIndex < rows.getLength(); rowIndex++) {
			List<String> dataList = new ArrayList<String>();
			NodeList rowData = rows.item(rowIndex).getChildNodes();
			for (int dataIndex = 0; dataIndex < rowData.getLength(); dataIndex++) {
				if (rowData.item(dataIndex).getNodeType() == Node.ELEMENT_NODE) {
					dataList.add(rowData.item(dataIndex).getTextContent());
				}
			}
			insertList.add("('"+StringUtils.join(dataList, "', '")+"')");
		}
		query += StringUtils.join(insertList, ", ");
		queryRunner.executeSql(query);
		return insertList.size();
	}
	
	@RobotKeyword("Executes the given SQL without any further modifications and stores the "
			+ "result in a file. The SQL query must be valid for the database that is "
			+ "used. The main purpose of this keyword is to generate expected result "
			+ "sets for use with keyword compareQueryResultToFile " + "\n\n"
			+ "*NOTE*: If using keyword remotely, file need to be trasfered from server some "
			+ "other way; this library is not doing the transfer." + "\n\n" + "Example: \n"
			+ "| Store Query Result To File | Select phone, email from addresses where last_name = 'Johnson' | query_result.txt | ")
	@ArgumentNames({ "Query to execute", "File to save results" })
	public void storeQueryResultToFile(String sqlString, String fileName) throws SQLException, IOException {

		Statement stmt = DatabaseConnection.getConnection().createStatement();
		try {
			stmt.execute(sqlString);
			ResultSet rs = (ResultSet) stmt.getResultSet();
			ResultSetMetaData rsmd = rs.getMetaData();
			int numberOfColumns = rsmd.getColumnCount();
			FileWriter fstream = new FileWriter(fileName);
			BufferedWriter out = new BufferedWriter(fstream);
			while (rs.next()) {
				for (int i = 1; i <= numberOfColumns; i++) {
					rs.getString(i);
					out.write(rs.getString(i) + '|');
				}
				out.write("\n");
			}
			out.close();
		} finally {
			// stmt.close() automatically takes care of its ResultSet, so no
			// rs.close()
			stmt.close();
		}
	}
	
	private Document parseXMLDocumentFromFile(String pathToFile) throws ParserConfigurationException, SAXException, IOException {
		File fXmlFile = new File(pathToFile);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(fXmlFile);
	}
	
	private int writeQueryResultsToFile(String tableName, String filePath, List<HashMap<String, Object>> data)
			throws ParserConfigurationException, TransformerConfigurationException,
			TransformerFactoryConfigurationError, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.newDocument();
		Element export = doc.createElement("Export");
		export.setAttribute("table", tableName);
		doc.appendChild(export);
		Element results = doc.createElement("Rows");

		int rowNumber = 0;
		for (HashMap<String, Object> hashMap : data) {
			Element row = doc.createElement("Row");
			results.appendChild(row);
			for (Entry<String, Object> entry : hashMap.entrySet()) {
				Element node = doc.createElement(entry.getKey());
				node.appendChild(doc.createTextNode(entry.getValue().toString()));
				row.appendChild(node);
			}
			rowNumber++;
		}
		export.appendChild(results);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File(filePath));
		Source input = new DOMSource(doc);

		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(input, output);
		return rowNumber;
	}
}
