/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.exception;

import com.kesdip.common.exception.BaseUncheckedException;

/**
 * An invalid entity identifier has been passed to the persistence layer.
 * @author gerogias
 */
public class InvalidEntityIdentifierException extends BaseUncheckedException {

	/**
	 * Serialization version.
	 */
	static final long serialVersionUID = 1234567L;
	
	public InvalidEntityIdentifierException() {
		super();
	}

	public InvalidEntityIdentifierException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidEntityIdentifierException(String arg0) {
		super(arg0);
	}

	public InvalidEntityIdentifierException(Throwable arg0) {
		super(arg0);
	}

	
}
