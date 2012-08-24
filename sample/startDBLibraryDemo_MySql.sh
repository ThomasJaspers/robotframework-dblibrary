#!/bin/bash

# Please download and copy the latest MySQL JDBC Driver-JAR from here:
# http://dev.mysql.com/downloads/connector/j/
#
# Copy the JAR-File to the Sample-Directory and modify the Classpath set below
# according to the name of that JAR!

CLASSPATH=$CLASSPATH:./dblibrary-1.0.jar:./mysql-connector-java-5.1.12-bin.jar
export CLASSPATH

jybot --variable PORT:8889 --outputdir /tmp --output DBLibraryDemo_MySql_Output.xml --log DBLibraryDemo_MySql_Log.html --report DBLibraryDemo_MySql_Report.html DBLibraryDemo_MySql.html
