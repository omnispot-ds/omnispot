/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 4, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.admin.web.util;

import java.util.Locale;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Simple wrapper; makes sure locale is not set again.
 * 
 * @author gerogias
 */
public class SimpleResponseWrapper extends HttpServletResponseWrapper {

	/**
	 * Constructor from superclass.
	 * 
	 * @param res
	 */
	public SimpleResponseWrapper(HttpServletResponse res) {
		super(res);
	}

	/**
	 * Empty method.
	 * 
	 * @see javax.servlet.ServletResponseWrapper#setLocale(java.util.Locale)
	 */
	public void setLocale(Locale loc) {

	}

}
