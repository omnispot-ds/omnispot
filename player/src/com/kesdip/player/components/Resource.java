/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

/**
 * Represents a resource that can be specified in the spring configuration
 * as part of a particular components description. E.g. an image component
 * might specify a list of images to be displayed. Each image would be
 * represented by an instance of this class.
 * 
 * @author Pafsanias Ftakas
 */
public class Resource {
	private String identifier;
	private String checksum;
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getChecksum() {
		return checksum;
	}
	
	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}
}
