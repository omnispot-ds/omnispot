/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 7, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.bootstrap.derby;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Properties;

import org.apache.derby.drda.NetworkServerControl;
import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;

/**
 * Utility class to launch the Derby server.
 * 
 * @author gerogias
 */
public class LaunchDerby {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(LaunchDerby.class);

	/**
	 * Main method.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {

		Config config = Config.getSingleton(false);
		String serverName = config.getDbHost();
		String port = config.getDbPort();
		String databaseLocation = config.getDbLocation();
		String databaseURL = "jdbc:derby://" + serverName + ":" + port + "/"
				+ databaseLocation + ";create=true";
		if (logger.isDebugEnabled()) {
			logger.debug("Database URL: " + databaseURL);
		}
		try {
			logger.info("Starting Derby database.....");
			startDatabase(serverName, port);
			logger.info("Derby database started.....");
			logger.info("Check if database exists....");
			pokeDatabase(databaseURL);
			logger.info("Database is up and running!");
		} catch (Throwable ex) {
			logger.error(ex);
			return;
		}
		// now sleep indefinitely
		while (true) {
			try {
				Thread.sleep(240000);
			} catch (InterruptedException i) {
				// do nothing
			}
		}
	}

	/**
	 * Execute a trivial query against the database to make sure it is started.
	 * 
	 * @param databaseURL
	 *            the URL
	 * @throws Exception
	 *             on error
	 */
	private static void pokeDatabase(String databaseURL) throws Exception {
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
		} catch (Exception e) {
			logger.error("Failed to load org.apache.derby.jdbc.ClientDriver");
			return;
		}
		Properties prop = new Properties();
		prop.put("user", "kesdipepe");
		prop.put("password", "kesdipepe");
		Connection c = null;
		ResultSet rs = null;
		try {
			c = DriverManager.getConnection(databaseURL, prop);
			rs = c.getMetaData().getTables(
					null,
					null,
					"ACTION",
					new String[] { "TABLE"});
			if (!rs.next()) {
				logger.warn("Schema does not exist");
			}
		} finally {
			// clean up
			try {
				rs.close();
			} catch (Exception e) {
				// do nothing
			}
			try {
				c.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Start the DB server. Blocks until the server is verified to have started.
	 * 
	 * @param serverName
	 * @param port
	 * @throws Exception
	 */
	private static void startDatabase(String serverName, String port)
			throws Exception {
		System.setProperty("derby.drda.startNetworkServer", "true");
		System.setProperty("derby.drda.host", "0.0.0.0");
		System.setProperty("derby.storage.pageCacheSize", "10000");
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
		NetworkServerControl dbserver = new NetworkServerControl(InetAddress
				.getByName(serverName), Integer.parseInt(port));
		// attempt to connect to server
		for (int i = 0; i < 10; i++) {
			try {
				Thread.currentThread();
				Thread.sleep(2000L);
				dbserver.ping();
			} catch (Exception e) {
				if (i == 9) {
					logger
							.fatal("Giving up trying to connect to Derby Network Server!");
					throw e;
				}
			}
		}
	}

}
