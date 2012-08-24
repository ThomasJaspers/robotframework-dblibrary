@echo off

set CLASSPATH=%CLASSPATH%;./dblibrary-2.0-server.jar;./mysql-connector-java-5.1.6.jar

java org.robot.database.server.RemoteServer --port 8271
