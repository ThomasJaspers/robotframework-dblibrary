@echo off

REM
REM Please download and copy the latest MySQL JDBC Driver-JAR from here:
REM http://dev.mysql.com/downloads/connector/j/
REM
REM Copy the JAR-File to the Sample-Directory and modify the Classpath set below
REM according to the name of that JAR!
REM

set CLASSPATH=%CLASSPATH%;./dblibrary-2.0.jar;./mysql-connector-java-5.1.6.jar
jybot --variable PORT:3306 --outputdir c:\ --output DBLibraryDemo_MySql_Output.xml --log DBLibraryDemo_MySql_Log.html --report DBLibraryDemo_MySql_Report.html DBLibraryDemo_MySql.html
