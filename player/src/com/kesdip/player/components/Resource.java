/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

/**
 * <p>Represents a resource that can be specified in the spring configuration
 * as part of a particular components description. E.g. an image component
 * might specify a list of images to be displayed. Each image would be
 * represented by an instance of this class.</p>
 * <p>Each resource also has associated with it an optional cron expression that
 * defines (if present) the triggering times of the resource in the component
 * in which it is contained. Components must honor this.</p>
 * 
 * @author Pafsanias Ftakas
 */
public class Resource {
	private String identifier;
	private String checksum;
	private String cronExpression;
	
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
	
	public String getCronExpression() {
		return cronExpression;
	}
	
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
}
