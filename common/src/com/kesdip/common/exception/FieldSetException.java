/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.exception;

import com.kesdip.common.configure.ApplicationContextBeanSetter;

/**
 * Signals an error directly setting the value of a field. Used in cases of
 * indirect or expression setting.
 * 
 * @author gerogias
 * @see ApplicationContextBeanSetter
 */
public class FieldSetException extends BaseCheckedException {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public FieldSetException() {
		super();
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            the message
	 * @param exc
	 *            the wrapped exception
	 */
	public FieldSetException(String message, Throwable exc) {
		super(message, exc);
	}

	/**
	 * Constructor.
	 * 
	 * @param message
	 *            the message
	 */
	public FieldSetException(String message) {
		super(message);
	}

	/**
	 * Constructor.
	 * 
	 * @param exc
	 *            the wrapped exception
	 */
	public FieldSetException(Throwable exc) {
		super(exc);
	}

}
