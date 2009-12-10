/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.message.ContinuationMessage;
import com.kesdip.bootstrap.message.IMessage;
import com.kesdip.bootstrap.message.RestartPlayerMessage;

/**
 * This is the thread that handles the messages that are coming through the
 * communication with the Ke.S.Di.P. E.P.E. server.
 * 
 * @author Pafsanias Ftakas
 */
public class MessagePump extends Thread {
	private final static Logger logger = Logger.getLogger(MessagePump.class);
	
	/* MESSAGE PUMP STATE */
	private BlockingQueue<IMessage> messageQueue;
	private boolean running;
	
	/**
	 * Initializing constructor.
	 */
	public MessagePump() {
		super("message_pump");
		
		this.messageQueue = new LinkedBlockingQueue<IMessage>();
		this.running = true;
		
		// Make sure any static code in the Config class has been called, so
		// that we can be sure that the initialization has been performed prior
		// to the main thread of the message pump having started.
		Config.getSingleton();
		
		// Schedule a continuation message. This will check up front whatever
		// tasks need to be performed that are the result of tasks from a
		// previous incarnation of the bootstrap app that crashed.
		addMessage(new ContinuationMessage());
		
		// Start the player playing. This will launch a subprocess that will
		// run the JVM and start the main player class.
		addMessage(new RestartPlayerMessage(null));
	}
	
	/**
	 * Add a message to the message queue. The control thread will call this
	 * method to add messages to the queue.
	 * 
	 * @param msg The message to add to the queue of tasks to perform.
	 */
	public void addMessage(IMessage msg) {
		// Do not use the put() i/f, because we want the queue to throw an
		// exception if the capacity (currently Integer.MAX_VALUE) is reached.
		messageQueue.add(msg);
	}
	
	/**
	 * Request the message pump thread to stop running.
	 */
	public synchronized void stopRunning() {
		running = false;
		interrupt(); // In case the thread is blocked in the take() method.
	}
	
	/**
	 * The message pump thread runs until someone calls the stopRunning()
	 * method that turns the running flag to false.
	 * 
	 * @return True iff the running flag is still true.
	 */
	private synchronized boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		while (isRunning()) {
			try {
				IMessage msg = messageQueue.take(); // potentially blocks
				
				try {
					msg.handle();
					if (logger.isInfoEnabled()) {
						logger.info("Completed processing of message: " +
								msg.toMessageString());
					}
				} catch (Exception e) {
					logger.error("Error processing message " +
							msg.toMessageString(), e);
				}
			} catch (Throwable t) {
				logger.error("Throwable caught in message pump's main method.", t);
			}
		}
	}

}
