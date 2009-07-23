/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 4, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.util;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Simple request wrapper; makes sure that encoding is not re-set.
 * @author gerogias
 */
public class SimpleRequestWrapper extends HttpServletRequestWrapper {

	/**
	 * Constructor from superclass.
	 * @param req
	 */
	public SimpleRequestWrapper(HttpServletRequest req) {
		super(req);
	}

	/**
	 * Empty method.
	 * @see javax.servlet.ServletRequestWrapper#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String enc) throws UnsupportedEncodingException {
		
	}
	
}
