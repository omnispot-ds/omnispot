/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.content;

import org.apache.log4j.Logger;

/**
 * A handler for retrieving descriptors from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class DescriptorHandler implements ContentHandler {
	private static final Logger logger =
		Logger.getLogger(DescriptorHandler.class);
	
	private String descriptorUrl;
	
	public DescriptorHandler(String descriptorUrl) {
		this.descriptorUrl = descriptorUrl;
	}

	@Override
	public String toMessageString() {
		return "[DescriptorHandler:" + descriptorUrl + "]";
	}

	@Override
	public void run() {
		try {
			// TODO Download the descriptor locally from its URL
			// TODO Load the descriptor as a spring configuration
			// TODO Scan the contents for resource instances
			// TODO For each resource instance:
			// TODO Check if the resource exists in the local store
			// TODO If not, start a new ResourceHandler
		} catch (Throwable t) {
			logger.error("Throwable while retrieving descriptor: " + descriptorUrl, t);
		}
	}

}
