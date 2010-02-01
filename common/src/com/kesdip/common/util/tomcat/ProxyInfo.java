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
 * Wraps information on a proxy to use.
 * 
 * @author gerogias
 */
public class ProxyInfo implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The proxy host.
	 */
	private String host = null;

	/**
	 * The proxy port.
	 */
	private int port = 0;

	/**
	 * Default constructor.
	 * 
	 * @param host
	 *            the host
	 * @param port
	 *            the port
	 */
	public ProxyInfo(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

}
