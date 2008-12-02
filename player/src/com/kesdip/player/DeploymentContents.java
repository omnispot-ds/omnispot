/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.util.List;

/**
 * Holder of the deployment contents (all the layouts are contained in
 * an instance of this class). This helps provide some structure to the
 * deployment descriptor.
 * 
 * @author Pafsanias Ftakas
 */
public class DeploymentContents {
	private List<DeploymentLayout> layouts;
	
	public void setLayouts(List<DeploymentLayout> layouts) {
		this.layouts = layouts;
	}
	
	public List<DeploymentLayout> getLayouts() {
		return layouts;
	}
}
