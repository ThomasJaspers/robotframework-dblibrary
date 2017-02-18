*** Settings ***
Library           DatabaseLibrary
Library           Process
Library           DateTime
Library           Collections

*** Keywords ***
Start H2 server
    Start Process    java    -cp    ${maven.test.classpath}    org.h2.tools.Server    -tcp    -tcpAllowOthers
    Connect To Database    org.h2.Driver    jdbc:h2:mem:robotTest;DB_CLOSE_DELAY=-1    sa    ${EMPTY}    secondConnection
    Connect To Database    org.h2.Driver    jdbc:h2:mem:robotTest;DB_CLOSE_DELAY=-1    sa    ${EMPTY}

Stop H2 server
    Disconnect From Database    secondConnection
    Disconnect From Database
    Run Process    java    -cp    ${maven.test.classpath}    org.h2.tools.Server    -tcpShutdown    tcp://localhost:9092

Start Remote and H2 server
    Start remote server
    Import Library    Remote    http://127.0.0.1:62022/    WITH NAME    RemoteDatabaseLibrary
    Set Library Search Order    RemoteDatabaseLibrary    DatabaseLibrary
    Start H2 Server

Stop Remote and H2 server
    Stop H2 Server
    Set Library Search Order    DatabaseLibrary    RemoteDatabaseLibrary

Start remote server
    ${process}    Start Process    java    -cp    ${maven.test.classpath}    org.robotframework.remoteserver.RemoteServer    --library
    ...    DatabaseLibrary:/    --port    62022
