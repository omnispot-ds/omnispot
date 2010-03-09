/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import java.util.HashSet;
import java.util.Set;
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

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(ContentRetriever.class);

	/**
	 * The singleton content retriever instance.
	 */
	private static ContentRetriever singleton = new ContentRetriever();

	/**
	 * The set of active handlers.
	 */
	private Set<String> handlers = null;

	/**
	 * Accessor to the singleton content retriever.
	 * 
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
		handlers = new HashSet<String>();
	}

	/**
	 * Given the content handler parameter, this method will pick a thread from
	 * the pool of threads and fire off the execution of the content handler in
	 * that thread.
	 * 
	 * @param handler
	 *            The content handler to execute asynchronously.
	 */
	public void addTask(ContentHandler handler) {
		if (logger.isTraceEnabled()) {
			logger.trace("Starting task for: " + handler.toMessageString());
		}
		handler.setContentRetriever(this);
		pool.execute(handler);
	}

	/**
	 * Add a handler to the set.
	 * 
	 * @param url
	 *            the handler url
	 */
	void addHandler(String url) {
		handlers.add(url);
	}

	/**
	 * Remove a handler from the set.
	 * 
	 * @param url
	 *            the handler url
	 */
	void removeHandler(String url) {
		handlers.remove(url);
	}

	/**
	 * Check if the set of handlers contains the one identified by this url.
	 * 
	 * @param url
	 *            the handler's url
	 * @return boolean <code>true</code> if the handler is in the set
	 */
	public boolean isHandlerActive(String url) {
		return handlers.contains(url);
	}
}
