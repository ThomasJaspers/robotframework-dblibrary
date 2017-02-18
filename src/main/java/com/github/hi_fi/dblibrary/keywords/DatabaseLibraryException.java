package com.github.hi_fi.dblibrary.keywords;


/**
 * The sole purpose of this Exception class is that it can be used to throw
 * errors indicating that executing a keyword failed due to database content
 * and arguments in comparison to additional (SQL)Exceptions that will indicate
 * potential problems in the implementation as such.
 * 
 * @author thomasjaspers
 *
 */
public class DatabaseLibraryException extends Exception {
	

	private static final long serialVersionUID = 1L;

	public DatabaseLibraryException(String msg) {
	    super(msg);
	  }
}
