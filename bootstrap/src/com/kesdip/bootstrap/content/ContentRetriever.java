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
 * A singleton that allows the bootstrap app to manage content retrieval from
 * the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ContentRetriever {
	private static final Logger logger =
		Logger.getLogger(ContentRetriever.class);
	
	private static ContentRetriever singleton = new ContentRetriever();
	
	public static ContentRetriever getSingleton() {
		return singleton;
	}
	
	private ExecutorService pool;
	
	private ContentRetriever() {
		this.pool = Executors.newCachedThreadPool();
	}
	
	public void addTask(ContentHandler handler) {
		logger.info("Starting task for: " + handler.toMessageString());
		pool.execute(handler);
	}
	
}
