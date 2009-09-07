/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 8, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.propertyeditor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.kesdip.common.util.StringUtils;

/**
 * Converts an array of strings into an array of File objects.
 * 
 * @author gerogias
 */
public class FileArrayEditor extends BaseTypeEditor {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(FileArrayEditor.class);
	
	/**
	 * Parses the string and creates the array.
	 * 
	 * @see java.beans.PropertyEditorSupport#setAsText(java.lang.String)
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isEmpty(text)) {
			super.setValue(null);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Parsing " + text);
		}
		String[] parts = text.split("\\,");
		List<File> files = new ArrayList<File>();
		for (String part : parts) {
			files.add(new File(part));
		}
		setValue(files.toArray(new File[files.size()]));
	}
}
