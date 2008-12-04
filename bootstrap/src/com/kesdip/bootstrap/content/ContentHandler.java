/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

/**
 * The interface that all content handlers must implement.
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
