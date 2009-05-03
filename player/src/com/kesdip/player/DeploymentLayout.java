/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.util.List;

import com.kesdip.player.components.RootContainer;

/**
 * Holder of the contents (all the content roots are contained in an instance
 * of this class) for a single deployment layout.
 * 
 * @author Pafsanias Ftakas
 */
public class DeploymentLayout {
	/**
	 * The completion status of a deployment. Normally a DONT_CARE response
	 * is applicable, but when videos are the target, then completion "means"
	 * the video sequence has finished its run.
	 * 
	 * @author Pafsanias Ftakas
	 */
	public enum CompletionStatus {
		COMPLETE,
		INCOMPLETE,
		DONT_CARE
	};
	
	private List<RootContainer> contentRoots;
	private String name;
	private String cronExpression;
	private int duration;
	
	public void setContentRoots(List<RootContainer> contentRoots) {
		this.contentRoots = contentRoots;
	}
	
	public List<RootContainer> getContentRoots() {
		return contentRoots;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	
	public String getCronExpression() {
		return cronExpression;
	}
	
	public void setDuration(int duration) {
		this.duration = duration;
	}
	
	public int getDuration() {
		return duration;
	}
	
	public void setShowGrid(boolean showGrid) {
		// Only used by the designer
	}
	
	public void setSnapToGeometry(boolean snapToGeometry) {
		// Only used by the designer
	}
	
	public CompletionStatus isComplete() {
		for (RootContainer root : contentRoots) {
			switch (root.isComplete()) {
			case COMPLETE:
				return CompletionStatus.COMPLETE;
			case INCOMPLETE:
				return CompletionStatus.INCOMPLETE;
			case DONT_CARE:
				// Do nothing
			}
		}
		
		return CompletionStatus.DONT_CARE;
	}

}
