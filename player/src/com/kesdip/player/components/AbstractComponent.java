/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Color;

import com.kesdip.player.DeploymentLayout.CompletionStatus;

/**
 * Abstract class that encapsulates the common functionality and state that
 * all components should have. In particular, it contains the location and size
 * of the component and its background color.
 * 
 * @author Pafsanias Ftakas
 */
public abstract class AbstractComponent implements Component {
	protected int x;
	protected int y;
	protected int width;
	protected int height;
	protected Color backgroundColor;
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public void setWidth(int width) {
		this.width = width;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	@Override
	public CompletionStatus isComplete() {
		return CompletionStatus.DONT_CARE;
	}

	@Override
	public void releaseResources() {
		// Do nothing. Subclasses should override if they need to do something
		// here. Check the documentation of the Component interface.
	}
}
