/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 1, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */
package com.kesdip.common.util;

/**
 * Class offering string utility methods on top of those offered by Commons
 * Utilities.
 * 
 * @author gerogias
 */
public class StringUtils extends org.apache.commons.lang.StringUtils {

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

}
