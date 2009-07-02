/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.content.ContentRetriever;
import com.kesdip.bootstrap.content.TimingHandler;

/**
 * Encapsulates the handling of the continuation message: A message added to
 * the message pump to handle graceful restart.
 * 
 * @author Pafsanias Ftakas
 */
public class ContinuationMessage extends Message {
	private static final Logger logger =
		Logger.getLogger(ContinuationMessage.class);

	public void process() throws Exception {
		logger.info("Starting processing of continuation message.");
		
		// TODO: What other task needs to be performed during restart from
		// a crash?
		
		ContentRetriever.getSingleton().addTask(new TimingHandler());
	}

	public String toMessageString() {
		return "[ContinuationMessage]";
	}

	@Override
	public String getActionId() {
		return null;
	}

}
