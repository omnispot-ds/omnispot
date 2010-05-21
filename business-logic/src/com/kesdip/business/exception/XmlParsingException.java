/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 02 Μαρ 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.exception;

import com.kesdip.common.exception.BaseCheckedException;

/**
 * Indicates an XML parsing error.
 * 
 * @author gerogias
 */
public class XmlParsingException extends BaseCheckedException {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public XmlParsingException() {
		super();
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public XmlParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public XmlParsingException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 */
	public XmlParsingException(Throwable arg0) {
		super(arg0);
	}

}
