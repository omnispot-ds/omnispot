/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 18, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.constenum;

/**
 * Enumeration of the different {@link Action} types.
 * 
 * @author gerogias
 */
public interface IActionTypesEnum {

	/**
	 * Make a deployment.
	 */
	short DEPLOY = 1;
	
	/**
	 * Reboot the machine.
	 */
	short REBOOT = 2;
	
	/**
	 * Restart the player.
	 */
	short RESTART = 4;
	
	/**
	 * Reconfigure the player.
	 */
	short RECONFIGURE = 5;
	
	/**
	 * Fetch player logs.
	 */
	short FETCH_LOGS = 6;
}
