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
 * Base class for all unchecked exceptions.
 * @author gerogias
 */
public abstract class BaseUncheckedException extends RuntimeException {

	/**
	 * Default constructor.
	 */
	public BaseUncheckedException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public BaseUncheckedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BaseUncheckedException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BaseUncheckedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
