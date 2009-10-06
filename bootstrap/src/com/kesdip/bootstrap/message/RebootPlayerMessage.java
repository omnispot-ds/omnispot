package com.kesdip.bootstrap.message;

import org.apache.log4j.Logger;

import com.kesdip.common.util.ProcessUtils;

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
		
		ProcessUtils.restartSystem();
	}

	@Override
	public String toMessageString() {
		return "[RebootPlayer]";
	}

}
