*** Settings ***
Documentation     This is a sample Robot Library testsuite demonstrating the usage of the Database Library (https://github.com/Hi-Fi/robotframework-dblibrary).
...
...               Test uses in-memory H2 database, but changing of database just needs driver to classpath and change of settings in connection.
...
...               Tests are executed through Remote server to test library in remte usage, too.
Suite Setup       Start Remote and H2 server
Suite Teardown    Stop Remote and H2 server
Resource          common.robot

*** Test Cases ***
Create Test Table
    [Documentation]    Creates the test table used for testing throughout the demo.
    Execute SQL    CREATE TABLE DemoTable (Id INT NOT NULL, Name VARCHAR(255))
    Execute SQL    ALTER TABLE DemoTable ADD PRIMARY KEY (Id);

Basic Checks
    [Documentation]    Performs some basic checks on the created DemoTable. You can check the generated Robot Framework log file to see the transactions isolation level fetched.
    ...
    ...    H2 creates all tables with uppercase, so in some keywors it's needed to be added as DEMOTABLE.
    Table Must Exist    DEMOTABLE
    Table Must Be Empty    DEMOTABLE
    Activate Database Connection    secondConnection
    Table Must Exist    DEMOTABLE
    Table Must Be Empty    DEMOTABLE
    Activate Database Connection
    Check Primary Key Columns For Table    DEMOTABLE    Id
    ${TI_LEVEL}=    Get Transaction Isolation Level
    Log    ${TI_LEVEL}

Add Content To Table
    [Documentation]    Add some records to the DemoTable for further checks.
    Execute SQL    INSERT INTO DemoTable VALUES(1, 'Donny Darko')
    Execute SQL    INSERT INTO DemoTable VALUES(2, 'Darth Vader')

Check Number of Rows
    [Documentation]    This testcase checks the functionality of the keywords "Store Query Result To File" and "Compare Query Result To File".
    Store Query Result To File    SELECT * FROM DemoTable    remoteTest.tmp
    Compare Query Result To File    SELECT * FROM DemoTable    remoteTest.tmp

Content Check
    [Documentation]    Checks for specific content in the DemoTable. You can inspect the log file to see the value fetched by the "Read Single Value From Table" keyword.
    Check Content for Row Identified by Rownum    Id,Name    1|Donny Darko    DemoTable    1
    Check Content for Row Identified by WhereClause    Id,Name    2|Darth Vader    DemoTable    id=2
    ${VALUE}=    Read Single Value From Table    DemoTable    Name    id=1
    Log    ${VALUE}

Content Check with Execute SQL
    [Documentation]    Checks that query execution returns correct amount of results.
    ${data}    Execute SQL    Select * from DemoTable    
    Log    ${data}
    ${value}    Get From Dictionary    ${data[0]}    NAME
    Should Be Equal As Strings    ${value}    Donny Darko
    ${length}    Get Length    ${data}
    Should Be Equal As Integers    ${length}    2

Empty results with Execute SQL
    [Documentation]    Checks that query execution works when there's 0 lines returned.
    ${data}    Execute SQL    Select * from DemoTable where NAME='not found'
    Log    ${data}
    ${length}    Get Length    ${data}
    Should Be Equal As Integers    ${length}    0
    
Delete Rows From Database
    [Documentation]    Delete all rows from the database.
    Table Must Contain Number of Rows    DemoTable    2
    Delete all Rows From Table    DemoTable
    Table Must Contain Number of Rows    DemoTable    0

Drop Test Table
    [Documentation]    Clean up by dropping the DemoTable again.
    Execute SQL    DROP TABLE DemoTable
