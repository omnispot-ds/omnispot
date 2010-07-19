/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.common.util;

import java.awt.Color;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Class offering string utility methods on top of those offered by Commons
 * Utilities.
 * 
 * @author gerogias
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

	/**
	 * Delimiter for action message lines.
	 */
	private static final String ACTION_MESSAGE_DELIM = "=-=-=";

	/**
	 * The system's line separator.
	 */
	private final static String LINE_SEPARATOR = System
			.getProperty("line.separator");

	/**
	 * Checks if the given string starts with a protocol identifier. The
	 * recognized protocols are:
	 * <ul>
	 * <li>http://</li>
	 * <li>ftp://</li>
	 * </ul>
	 * 
	 * @param name
	 *            the name to check
	 * @return boolean <code>true</code> if the name begins with any if the
	 *         given protocols
	 */
	public static final boolean isURL(String name) {
		if (isEmpty(name)) {
			return false;
		}
		String trimmedName = name.trim();
		return trimmedName.startsWith("http://")
				|| trimmedName.startsWith("ftp://");
	}

	/**
	 * Extracts the crc part from a composite CRC value.
	 * <p>
	 * If the string contains a dash (-), it returns the part before the first
	 * dash. Otherwise, it returns the string as is.
	 * </p>
	 * 
	 * @param value
	 *            the value to examine
	 * @return String the CRC part or the value as-is
	 */
	public static final String extractCrc(String value) {
		if (isEmpty(value)) {
			return value;
		}
		if (!value.contains("-")) {
			return value;
		}
		int pos = value.indexOf('-');
		return value.substring(0, pos);
	}

	/**
	 * Extracts the size part from a composite CRC value.
	 * <p>
	 * If the string contains a dash (-), it returns the part after the first
	 * dash. Otherwise, it returns <code>null</code>.
	 * </p>
	 * 
	 * @param value
	 *            the value to examine
	 * @return String the CRC part or <code>null</code>
	 */
	public static final String extractSize(String value) {
		if (isEmpty(value)) {
			return null;
		}
		if (!value.contains("-")) {
			return null;
		}
		int pos = value.indexOf('-');
		return value.substring(pos + 1);
	}

	/**
	 * Process the final part of a URL to escape any illegal characters.
	 * <p>
	 * If the URL does not contain slashes (/), it is returned as is.
	 * </p>
	 * 
	 * @param url
	 *            the url
	 * @return String the initial URL with the file name URL-encoded or
	 *         <code>null</code>/empty if the url was such
	 */
	public static final String encodeFileName(String url) {
		if (StringUtils.isEmpty(url)) {
			return url;
		}
		int pos = url.lastIndexOf('/');
		if (pos == -1) {
			return url;
		}
		String filename = null;
		try {
			filename = URLEncoder.encode(url.substring(pos + 1), "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			// will not happen
		}
		return url.substring(0, pos + 1) + filename;
	}

	/**
	 * Converts a color to its hex representation (0xFFFFFF).
	 * 
	 * @param color
	 *            the color
	 * @return String its string representation or <code>null</code>
	 */
	public static final String toHexString(Color color) {
		if (color == null) {
			return null;
		}
		StringBuilder str = new StringBuilder("0x");
		if (color.getRed() < 16) {
			str.append('0');
		}
		str.append(Integer.toHexString(color.getRed()));
		if (color.getGreen() < 16) {
			str.append('0');
		}
		str.append(Integer.toHexString(color.getGreen()));
		if (color.getBlue() < 16) {
			str.append('0');
		}
		str.append(Integer.toHexString(color.getBlue()));

		return str.toString();
	}

	/**
	 * Trims a string, if not <code>null</code>.
	 * 
	 * @param value
	 *            the value to trim
	 * @return String the trimmed value or <code>null</code> if the value was
	 *         <code>null</code>
	 */
	public static final String trim(String value) {
		return value != null ? value.trim() : null;
	}

	/**
	 * Replaces all occurrences of newlines with the char sequence
	 * {@link #ACTION_MESSAGE_DELIM}. Required to have the message all in one
	 * line.
	 * 
	 * @param message
	 *            message to process
	 * @return String the converted string or an empty string
	 */
	public static final String convertToActionMessage(String message) {
		if (isEmpty(message)) {
			return "";
		}
		// go for both Windows and Unix newlines
		String msg = message.replace("\r\n", ACTION_MESSAGE_DELIM);
		return msg.replace("\n", ACTION_MESSAGE_DELIM);
	}

	/**
	 * Does the reverse of {@link #convertToActionMessage(String)}, replacing
	 * all occurrences of {@link #ACTION_MESSAGE_DELIM} with the platform's line
	 * separator.
	 * 
	 * @param message
	 *            to process
	 * @return String the converted string or an empty string
	 */
	public static final String convertFromActionMessage(String message) {
		if (isEmpty(message)) {
			return "";
		}
		return message.replace(ACTION_MESSAGE_DELIM, LINE_SEPARATOR);
	}

	/**
	 * Split the given argValue using the splitExpression as delimiter.
	 * 
	 * @param argValue
	 *            the arg value
	 * @param splitExpr
	 *            the delimiter
	 * @return String[] the split value or an empty array if the value is
	 *         null/empty
	 * @throws IllegalArgumentException
	 *             if the splitExpr is null/empty
	 */
	public static String[] toArgArray(String argValue, String splitExpr) {

		if (isEmpty(splitExpr)) {
			throw new IllegalArgumentException(
					"Split expression cannot be empty");
		}
		if (isEmpty(argValue)) {
			return new String[] {};
		}
		return argValue.split(splitExpr);
	}
}
