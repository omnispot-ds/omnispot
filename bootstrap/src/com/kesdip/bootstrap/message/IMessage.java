/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

/**
 * The interface that message implementations should implement. A message is
 * essentially a container of some functionality that we want the message pump
 * to deal with sequentially. The idea is that the thread that handles
 * communications with the main server, will create message instances and pass
 * them on to the message pump, as it accepts commands from the main server.
 * 
 * @author Pafsanias Ftakas
 */
public interface IMessage {
	/**
	 * Process a particular message from the server. Implementors should note
	 * that execution of this method is performed in the MessagePump thread, so
	 * for long lasting operations, one should consider using a separate thread.
	 * 
	 * @throws Exception Iff something goes wrong.
	 */
	void handle() throws Exception;
	
	/**
	 * Helper to print a friendly version of the message for logging purposes.
	 * 
	 * @return A string representation of the message.
	 */
	String toMessageString();
}
