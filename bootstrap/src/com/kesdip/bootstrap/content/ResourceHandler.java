/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import org.apache.log4j.Logger;

/**
 * A handler for retrieving resources from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class ResourceHandler implements ContentHandler {
	private static final Logger logger =
		Logger.getLogger(ResourceHandler.class);
	
	private String resourceUrl;
	
	public ResourceHandler(String resourceUrl) {
		this.resourceUrl = resourceUrl;
	}

	@Override
	public String toMessageString() {
		return "[ResourceHandler:" + resourceUrl + "]";
	}

	@Override
	public void run() {
		logger.info("Starting download of resource: " + resourceUrl);
		
		try {
			// TODO Download the resource locally from its URL
			// TODO Do what is necessary in the database
			// TODO If this is the last pending resource for a deployment do
			// what is necessary to signal to the player that a new deployment
			// is available.
		} catch (Throwable t) {
			logger.error("Throwable while retrieving resource: " + resourceUrl, t);
		}
	}

}
