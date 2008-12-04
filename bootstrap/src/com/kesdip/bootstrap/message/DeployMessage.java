/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

/**
 * Represents a deployment message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class DeployMessage implements Message {
	private String descriptorUrl;
	
	public DeployMessage(String descriptorUrl) {
		this.descriptorUrl = descriptorUrl;
	}
	
	public String getDescriptorUrl() {
		return descriptorUrl;
	}

	@Override
	public void process() throws Exception {
		// TODO Implement
		
		// Schedule the deployment descriptor download from the server.
	}

	@Override
	public String toMessageString() {
		return "[Deploy:" + descriptorUrl + "]";
	}
}
