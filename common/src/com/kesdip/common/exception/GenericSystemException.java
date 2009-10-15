/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.common.exception;

/**
 * Indicates a general error during code execution.
 * @author gerogias
 */
public class GenericSystemException extends BaseUncheckedException {

	/**
	 * Serialization version.
	 */
	static final long serialVersionUID = 1234567L;
	
	public GenericSystemException() {
		super();
	}

	public GenericSystemException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GenericSystemException(String arg0) {
		super(arg0);
	}

	public GenericSystemException(Throwable arg0) {
		super(arg0);
	}

	
}
