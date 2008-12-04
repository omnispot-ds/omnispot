package com.kesdip.bootstrap.message;

import org.apache.log4j.Logger;

public class ContinuationMessage implements Message {
	private static final Logger logger =
		Logger.getLogger(ContinuationMessage.class);

	@Override
	public void process() throws Exception {
		logger.info("Starting processing of continuation message.");
		
		// TODO: Scan the database for pending resources
		// TODO: What other task needs to be performed during restart from
		// a crash?
	}

	@Override
	public String toMessageString() {
		return "[ContinuationMessage]";
	}

}
