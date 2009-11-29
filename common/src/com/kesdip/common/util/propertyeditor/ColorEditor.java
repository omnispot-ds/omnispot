/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 09 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.propertyeditor;

import java.awt.Color;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.kesdip.common.util.StringUtils;

/**
 * Property editor between {@link java.awt.Color} and String. Supports strings
 * of the form <code>(R, G, B)</code>, with R, G, B values in the range [0-255].
 * Spaces between commas are optional.
 * 
 * @author gerogias
 */
public class ColorEditor extends BaseTypeEditor {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(ColorEditor.class);

	/**
	 * The parse regular expression.
	 */
	private static final Pattern pattern = Pattern
			.compile("\\((\\d+)\\,\\s?(\\d+)\\,\\s?(\\d+)\\)");

	/**
	 * @return String the value as a string or an empty string
	 * @see java.beans.PropertyEditorSupport#getAsText()
	 */
	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		Color color = (Color) getValue();
		return "(" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + ")";
	}

	/**
	 * Parses the input to generate a {@link Color} object.
	 * 
	 * @param text
	 *            the text to parse
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isEmpty(text)) {
			setValue(null);
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Parsing " + text);
		}
		Matcher matcher = pattern.matcher(text);
		if (!matcher.matches()) {
			logger.error("Invalid input: " + text);
			throw new IllegalArgumentException("Invalid input: " + text);
		}
		String redStr = matcher.group(1);
		String greenStr = matcher.group(2);
		String blueStr = matcher.group(3);
		int red = 0, green = 0, blue = 0;
		try {
			red = Integer.valueOf(redStr);
			green = Integer.valueOf(greenStr);
			blue = Integer.valueOf(blueStr);
		} catch (NumberFormatException nfe) {
			logger.error("Invalid input: " + text);
			throw new IllegalArgumentException("Invalid input: " + text);
		}
		if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0
				|| blue > 255) {
			logger.error("Invalid input: " + text);
			throw new IllegalArgumentException("Invalid input: " + text);
		}
		super.setValue(new Color(red, green, blue));
	}
}
