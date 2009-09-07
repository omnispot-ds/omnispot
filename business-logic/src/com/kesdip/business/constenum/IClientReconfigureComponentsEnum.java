/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 03 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.constenum;

import com.kesdip.common.configure.ApplicationContextBeanSetter;

/**
 * Enumeration of the different component names available for configuration at
 * the client.
 * 
 * @author gerogias
 * @see ReconfigureMessage
 * @see ApplicationContextBeanSetter
 */
public interface IClientReconfigureComponentsEnum {

	/**
	 * The bootstrap manager module.
	 */
	String MANAGER = "manager";
	
	/**
	 * The player module.
	 */
	String PLAYER = "player";
}
