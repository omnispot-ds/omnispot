/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 16 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.tomcat;

import java.io.Serializable;

/**
 * Encapsulates information for Tomcat Manager application. This information
 * includes URL, credentials etc.
 * 
 * @author gerogias
 */
public class TomcatManagerInfo implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The base server URL.
	 */
	private String serverUrl = null;

	/**
	 * Administrator username.
	 */
	private String userName = null;

	/**
	 * Administrator password.
	 */
	private String password = null;

	/**
	 * Default constructor.
	 * 
	 * @param serverUrl
	 *            the base server URL
	 * @param userName
	 *            the administrator user
	 * @param password
	 *            the admin's password
	 */
	public TomcatManagerInfo(String serverUrl, String userName, String password) {
		this.serverUrl = serverUrl.endsWith("/") ? serverUrl : serverUrl + '/';
		this.userName = userName;
		this.password = password;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @return String the undeployment url
	 */
	public String getUndeployUrl() {
		return serverUrl + "manager/undeploy";
	}

	/**
	 * @return String the deployment url
	 */
	public String getDeployUrl() {
		return serverUrl + "manager/deploy";
	}

	/**
	 * @return the serverUrl
	 */
	public String getServerUrl() {
		return serverUrl;
	}
}
