/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

/**
 * The interface that message implementations should implement.
 * 
 * @author Pafsanias Ftakas
 */
public interface Message {
	/**
	 * Process a particular message from the server. Implementors should note
	 * that execution of this method is performed in the MessagePump thread, so
	 * for long lasting operations, one should consider using a separate thread.
	 * 
	 * @throws Exception Iff something goes wrong.
	 */
	void process() throws Exception;
	
	/**
	 * Helper to print a friendly version of the message for logging purposes.
	 * 
	 * @return A string representation of the message.
	 */
	String toMessageString();
}
