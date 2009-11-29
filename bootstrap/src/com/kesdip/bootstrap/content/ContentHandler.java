/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

/**
 * The interface that all content handlers must implement. A handler is
 * basically an implementation of Runnable that can be executed by the thread
 * pool of the ContentRetriever singleton. The idea is that the handler i/f
 * is to be used by implementors of long running tasks (like downloading
 * resources off the web) that should be handled asynchronously.
 * 
 * @author Pafsanias Ftakas
 */
public interface ContentHandler extends Runnable {
	/**
	 * Helper to print a friendly version of the handler for logging purposes.
	 * 
	 * @return A string representation of the handler.
	 */
	String toMessageString();
}
