/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Μαϊ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.constenum;

/**
 * Enumeration of the different message parameters.
 * 
 * @author gerogias
 */
public interface IMessageParamsEnum {

	/**
	 * Client installation id field.
	 */
	String INSTALLATION_ID = "installationId";
	
	/**
	 * Flag to indicate if player process is alive.
	 */
	String PLAYER_PROC_ALIVE = "playerProcAlive";
	
	/**
	 * Field containing all the actions.
	 */
	String SERIALIZED_ACTIONS = "serializedActions";
	
	/**
	 * Contains the screenshot.
	 */
	String SCREENSHOT = "screenshot";
}
