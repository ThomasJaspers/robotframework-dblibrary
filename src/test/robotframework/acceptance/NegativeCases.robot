*** Settings ***
Suite Setup       Start H2 server
Suite Teardown    Stop H2 Server
Resource          common.robot

*** Test Cases ***
Connection to non-existent database
    Run Keyword And Expect Error    *    Connect To Database    org.h2.Driver    jdbc:h2:tcp://localhost:1234/~/test    sa    sa

Query against non-existing table
    Run Keyword And Expect Error    *    Execute SQL    Select * from user

Try to activate non-existing connection
    Run Keyword And Expect Error    *    Activate Database Connection    non_existent