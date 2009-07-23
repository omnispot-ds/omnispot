/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

/**
 * A singleton that allows the bootstrap app to manage content retrieval of
 * content from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ContentRetriever {
	private static final Logger logger =
		Logger.getLogger(ContentRetriever.class);
	
	/**
	 * The singleton content retriever instance.
	 */
	private static ContentRetriever singleton = new ContentRetriever();
	
	/**
	 * Accessor to the singleton content retriever.
	 * @return The signleton content retriever.
	 */
	public static ContentRetriever getSingleton() {
		return singleton;
	}
	
	/* CONTENT RETRIEVER STATE */
	private ExecutorService pool;
	
	/**
	 * Initializing constructor.
	 */
	private ContentRetriever() {
		this.pool = Executors.newCachedThreadPool();
	}
	
	/**
	 * Given the content handler parameter, this method will pick a thread
	 * from the pool of threads and fire off the execution of the content
	 * handler in that thread.
	 * 
	 * @param handler The content handler to execute asynchronously.
	 */
	public void addTask(ContentHandler handler) {
		logger.info("Starting task for: " + handler.toMessageString());
		pool.execute(handler);
	}
	
}
