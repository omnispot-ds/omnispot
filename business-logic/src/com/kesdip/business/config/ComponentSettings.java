/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 29, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.config;

import org.apache.commons.configuration.XMLConfiguration;

/**
 * Base class for all KeSDiP component settings objects.
 * @author gerogias
 */
public abstract class ComponentSettings {

	/**
	 * @return String the name of the object.
	 */
	public abstract String getName();
	
	/**
	 * Loads the settings 
	 * @param configuration
	 */
	abstract void load(XMLConfiguration configuration);
}