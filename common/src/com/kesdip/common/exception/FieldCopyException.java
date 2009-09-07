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
 * Signals the failure to copy a field.
 * @author gerogias
 */
public class FieldCopyException extends BaseUncheckedException {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 123456L;
	
	/**
	 * Default constructor.
	 */
	public FieldCopyException() {
	}

	/**
	 * @param arg0
	 */
	public FieldCopyException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public FieldCopyException(Throwable arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public FieldCopyException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
