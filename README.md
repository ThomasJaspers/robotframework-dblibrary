# Database library for Robot Framework
Introduction
------------
The Robot Framework Database Library is a library which provides common functionality for testing database contents.

* More information about this library can be found in the
  [Keyword Documentation](http://search.maven.org/remotecontent?filepath=com/github/hi-fi/robotframework-dblibrary/3.0/robotframework-dblibrary-3.0.html).
* For keyword completion in RIDE you can download this
  [Library Specs](http://search.maven.org/remotecontent?filepath=com/github/hi-fi/robotframework-dblibrary/3.0/robotframework-dblibrary-3.0.xml)
  and place it in your PYTHONPATH.

Usage
-----
If you are using the [robotframework-maven-plugin](http://robotframework.org/MavenPlugin/) you can
use this library by adding the following dependency to 
your pom.xml:

    <dependency>
        <groupId>com.github.hi-fi</groupId>
        <artifactId>robotframework-dblibrary</artifactId>
        <version>3.0</version>
    </dependency>
    
With Gradle, library can be use by importing it as a dependency in build.gradle:

    runtime('com.github.hi-fi:robotframework-dblibrary:3.0')
    
Library import in Robot tests can be done with:

|                    |                                 |
| ----------------   | ------------------------------- | 
| *** Settings ***   |                                 |                 
| Library            | DatabaseLibrary                 |   
   
Example
-------
Usage examples can be found at [Tests-folder](/src/test/robotframework/acceptance) for both local and remote usage.
