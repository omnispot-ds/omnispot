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
 * Base class for all business logic exceptions.
 * @author gerogias
 */
public abstract class BaseCheckedException extends Exception {

	/**
	 * Default constructor.
	 */
	public BaseCheckedException() {
	}

	/**
	 * @param arg0
	 */
	public BaseCheckedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public BaseCheckedException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public BaseCheckedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
