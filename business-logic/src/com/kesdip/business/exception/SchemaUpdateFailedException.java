/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 18 Μαϊ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.exception;

import com.kesdip.common.exception.BaseUncheckedException;

/**
 * Signals a failure to update the schema.
 * 
 * @author gerogias
 */
public class SchemaUpdateFailedException extends BaseUncheckedException {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SchemaUpdateFailedException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public SchemaUpdateFailedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public SchemaUpdateFailedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public SchemaUpdateFailedException(Throwable arg0) {
		super(arg0);
	}

}
