/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 29, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.config;

import com.kesdip.common.exception.BaseUncheckedException;

/**
 * Signals an error while loading application settings.
 * @author gerogias
 */
public class SettingsLoadingException extends BaseUncheckedException {

	/**
	 * Serialization version.
	 */
	private final static long serialVersionUID = 1L;
	
	/**
	 * Default constructor.
	 */
	public SettingsLoadingException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SettingsLoadingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SettingsLoadingException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SettingsLoadingException(Throwable arg0) {
		super(arg0);
	}

	
}
