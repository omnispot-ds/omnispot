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
 * Enumeration of the different status values of an {@link Action}.
 * 
 * @author gerogias
 */
public interface IActionStatusEnum {

	/**
	 * Action completed successfully.
	 */
	short OK = 2;
	
	/**
	 * Action is in progress, awaiting installation response.
	 */
	short IN_PROGRESS = 1;
	
	/**
	 * Action failed, the system is retrying.
	 */
	short RETRYING = -1;
	
	/**
	 * Action failed permanently.
	 */
	short FAILED = -2;
	
	/**
	 * Action is scheduled.
	 */
	short SCHEDULED = 0;
	
	short SENT = 3;
}
