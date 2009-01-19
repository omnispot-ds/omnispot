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
	 * Start the player.
	 */
	short START = 1;
	
	/**
	 * Stop the player.
	 */
	short STOP = 2;
	
	/**
	 * Reboot the machine.
	 */
	short REBOOT = 3;
	
	/**
	 * Reconfigure the player.
	 */
	short RECONFIGURE = 4;
	
	/**
	 * Fetch player logs.
	 */
	short FETCH_LOGS = 5;
	
	/**
	 * Deploy new content package
	 */
	short DEPLOY = 6;
}
