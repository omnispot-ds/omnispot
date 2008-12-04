/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.message.Message;

/**
 * This is the thread that handles the messages that are coming through the
 * communication with the Ke.S.Di.P. E.P.E. server.
 * 
 * @author Pafsanias Ftakas
 */
public class MessagePump extends Thread {
	private final static Logger logger = Logger.getLogger(MessagePump.class);
	
	private BlockingQueue<Message> messageQueue;
	private boolean running;
	
	public MessagePump() {
		super("message_pump");
		
		this.messageQueue = new LinkedBlockingQueue<Message>();
		this.running = true;
	}
	
	/**
	 * Add a message to the message queue. The control thread will call this
	 * method to add messages to the queue.
	 * 
	 * @param msg The message to add to the queue of tasks to perform.
	 */
	public void addMessage(Message msg) {
		// Do not use the put() i/f, because we want the queue to throw an
		// exception if the capacity (currently Integer.MAX_VALUE) is reached.
		messageQueue.add(msg);
	}
	
	public synchronized void stopRunning() {
		running = false;
		interrupt(); // In case the thread is blocked in the take() method.
	}
	
	public synchronized boolean isRunning() {
		return running;
	}

	@Override
	public void run() {
		while (isRunning()) {
			try {
				Message msg = messageQueue.take(); // potentially blocks
				
				try {
					msg.process();
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
