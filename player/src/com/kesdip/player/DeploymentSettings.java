/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.util.Date;

/**
 * Helper container for all the deployment descriptor information and properties
 * that describe the whole deployment.
 * 
 * @author Pafsanias Ftakas
 */
public class DeploymentSettings {
	private int width;
	private int height;
	private int bitDepth;
	private String id;
	private Date startTime;
	private int sleepInterval = 20;
	
	public int getWidth() {
		return width;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public int getBitDepth() {
		return bitDepth;
	}
	
	public void setBitDepth(int bitDepth) {
		this.bitDepth = bitDepth;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public int getSleepInterval() {
		return sleepInterval;
	}
	
	public void setSleepInterval(int sleepInterval) {
		this.sleepInterval = sleepInterval;
	}
}
