/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.propertyeditor;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.kesdip.common.util.DateUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Resolves a string to a {@link Date}, using {@link DateUtils#DATE_FORMAT}.
 * 
 * @author gerogias
 */
public class DateEditor extends BaseTypeEditor {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(DateEditor.class);

	/**
	 * @return String the date representation
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		return new SimpleDateFormat(DateUtils.DATE_FORMAT).format(getValue());
	}

	/**
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 * @throws IllegalArgumentException
	 *             if the expressino is not correct
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isEmpty(text)) {
			setValue(null);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Parsing " + text);
		}
		try {
			setValue(new SimpleDateFormat(DateUtils.DATE_FORMAT).parse(text));
		} catch (ParseException e) {
			logger.error(e);
			throw new IllegalArgumentException(e);
		}
	}
}
