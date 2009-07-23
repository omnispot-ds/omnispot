package com.kesdip.bootstrap.message;

import org.apache.log4j.Logger;

/**
 * Encapsulates the handling of a reboot player message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class RebootPlayerMessage extends Message {
	private static final Logger logger =
		Logger.getLogger(RebootPlayerMessage.class);
	
	private String actionId;

	@Override
	public String getActionId() {
		return actionId;
	}
	
	public RebootPlayerMessage(String actionId) {
		this.actionId = actionId;
	}

	@Override
	public void process() throws Exception {
		logger.info("Shuting down now!");
		
		String[] cmdArray = new String[4];
		cmdArray[0] = "shutdown";
		cmdArray[1] = "/r";
		cmdArray[2] = "/t";
		cmdArray[3] = "0";
		
		Runtime.getRuntime().exec(cmdArray, null, null);
	}

	@Override
	public String toMessageString() {
		return "[RebootPlayer]";
	}

}
