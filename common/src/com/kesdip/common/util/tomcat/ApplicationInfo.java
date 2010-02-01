/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 18 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.tomcat;

import java.io.Serializable;

/**
 * Object wrapping information on the application to handle via the Tomcat
 * Manager.
 * 
 * @author gerogias
 */
public class ApplicationInfo implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The target app's context path.
	 */
	private String contextPath = null;

	/**
	 * The previous tag, if any.
	 */
	private String previousTag = null;

	/**
	 * The new tag to create.
	 */
	private String newTag = null;

	/**
	 * Default constructor.
	 * 
	 * @param contextPath
	 *            the context path
	 * @param newTag
	 *            the new tag
	 * @param previousTag
	 *            the previous tag, ignored if <code>null</code>
	 */
	public ApplicationInfo(String contextPath, String newTag, String previousTag) {
		this.contextPath = contextPath;
		this.newTag = newTag;
		this.previousTag = previousTag;
	}

	/**
	 * @return the contextPath
	 */
	public String getContextPath() {
		return contextPath;
	}

	/**
	 * @return the previousTag
	 */
	public String getPreviousTag() {
		return previousTag;
	}

	/**
	 * @return the newTag
	 */
	public String getNewTag() {
		return newTag;
	}

}
