/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util;

/**
 * Wraps a business error (validation or other).
 * @author gerogias
 */
public class Error {
	
	private String messageKey = null;
	
	private String field = null;
	
	/**
	 * Creates an error for a field.
	 * @param field
	 * @param messageKey
	 */
	public Error(String field, String messageKey) {
		this.messageKey = messageKey;
		this.field = field;
	}
	
	/**
	 * Creates an error for the object.
	 * @param messageKey
	 */
	public Error(String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * @return the field
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}
}
