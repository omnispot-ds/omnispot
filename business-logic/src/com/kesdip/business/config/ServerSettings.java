/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 29, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.config;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Encapsulate server settings.
 * 
 * @author gerogias
 */
public class ServerSettings extends ComponentSettings {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(ServerSettings.class);

	/**
	 * Virtual path under which all content is located.
	 */
	private String contentBase = null;

	/**
	 * Virtual path under which all printscreens are located.
	 */
	private String printScreenBase = null;

	/**
	 * The URL for the printscreen servlet.
	 */
	private transient String printScreenBaseUrl = null;

	/**
	 * The base URL for the content servlet.
	 */
	private transient String contentBaseUrl = null;

	/**
	 * The host name or IP of the server.
	 */
	private String host = null;

	/**
	 * The port of the server.
	 */
	private String port = null;

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public String getPort() {
		return port;
	}

	/**
	 * @return String the name of the settings component
	 * @see gr.panouepe.monitor.common.settings.ComponentSettings#getName()
	 */
	public String getName() {
		return "Server Settings";
	}

	/**
	 * Package-private factory method.
	 * 
	 * @param configuration
	 *            the configuration to load from.
	 */
	void load(XMLConfiguration configuration) {

		logger.trace("Loading server.content-base");
		this.contentBase = configuration.getString("server.content-base");
		logger.trace("Loading server.printScreen-base");
		this.printScreenBase = configuration
				.getString("server.printScreen-base");

		logger.trace("Loading server.host");
		this.host = configuration.getString("server.host");
		logger.trace("Loading server.port");
		this.port = configuration.getString("server.port");
	}

	/**
	 * @return the contentBase
	 */
	public String getContentBase() {
		return contentBase;
	}

	/**
	 * @return the printScreenBase
	 */
	public String getPrintScreenBase() {
		return printScreenBase;
	}

	/**
	 * @return the printScreenBaseUrl
	 */
	public String getPrintScreenBaseUrl() {
		if (printScreenBaseUrl == null) {
			printScreenBaseUrl = printScreenBase;
		}
		return printScreenBaseUrl;
	}

	/**
	 * @return the contenBaseUrl
	 */
	public String getContentBaseUrl() {
		if (contentBaseUrl == null) {
			contentBaseUrl = "http://" + host + ":" + port + contentBase;
		}
		return contentBaseUrl;
	}

}
