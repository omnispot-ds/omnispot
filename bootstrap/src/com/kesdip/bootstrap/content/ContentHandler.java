/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import org.apache.log4j.Logger;

/**
 * The interface that all content handlers must implement. A handler is
 * basically an implementation of Runnable that can be executed by the thread
 * pool of the ContentRetriever singleton. The idea is that the handler i/f is
 * to be used by implementors of long running tasks (like downloading resources
 * off the web) that should be handled asynchronously.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class ContentHandler implements Runnable {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(ContentHandler.class);

	/**
	 * The url this handler is fetching.
	 */
	private String url = null;

	/**
	 * The parent content retriever.
	 */
	private ContentRetriever retriever = null;

	/**
	 * Default constructor. Defines the url of the handler.
	 * 
	 * @param url
	 *            the unique identifier
	 */
	public ContentHandler(String url) {
		this.url = url;
	}

	/**
	 * Helper to print a friendly version of the handler for logging purposes.
	 * 
	 * @return A string representation of the handler.
	 */
	protected abstract String toMessageString();

	/**
	 * Template method for descendants. All content fetching logic happens
	 * inside this method. All exception handling should occur inside it as
	 * well.
	 */
	protected abstract void contentHandlingLogic();

	/**
	 * Wrapper implementation for all descendants. Marks
	 * 
	 * @see java.lang.Runnable#run()
	 * @see Bug#140 We need to keep track of which content is downloaded and
	 *      which not
	 */
	@Override
	public final void run() {
		if (logger.isInfoEnabled()) {
			logger.info("Executing handler with url '" + url + "'");
		}
		retriever.addHandler(url);
		try {
			contentHandlingLogic();
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("Removing from retriever list: '" + url + "'");
			}
			retriever.removeHandler(url);
		}
	}

	/**
	 * @param retriever
	 *            the retriever to set
	 */
	public void setContentRetriever(ContentRetriever retriever) {
		this.retriever = retriever;
	}

}
