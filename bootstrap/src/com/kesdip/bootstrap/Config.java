/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.kesdip.common.util.DBUtils;

/**
 * Singleton to hold configuration settings. Settings are kept as preferences.
 * 
 * @author Pafsanias Ftakas
 */
public class Config {
	private static final Logger logger = Logger.getLogger(Config.class);

	/**
	 * The properties object. Relying on good old Java properties for making
	 * these persistent.
	 */
	private static Properties props;

	static {
		try {
			props = new Properties();
			InputStream is = Config.class
					.getResourceAsStream("/bootstrap.properties");
			props.load(is);
			is.close();
		} catch (Exception e) {
			logger.error("Unable to load properties from "
					+ "bootstrap.properties", e);
		}
	}

	/**
	 * The configuratino singleton object.
	 */
	private static Config singleton = new Config();

	/**
	 * Accessor to the configuration singleton object.
	 * 
	 * @return The singleton Config instance.
	 */
	public static Config getSingleton() {
		return singleton;
	}

	/**
	 * Private initializing constructor. Loads a driver class if it has been
	 * set, and sets the connection pool up with the driver manager using the
	 * appropriate JDBC URL.
	 */
	private Config() {
		try {
			String driverClass = getDriverClass();
			if ("_NOT_SET_".equals(driverClass))
				return;

			Class.forName(getDriverClass());

			DBUtils.setupDriver(getJDBCUrl());
		} catch (Exception e) {
			logger.error("Unable to setup connection pooling", e);
		}
	}

	public String getinstallationId() {
		return props.getProperty("installation_id", "_NOT_SET_");
	}

	/**
	 * The deployment path points to a directory on the local machine, where
	 * downloaded deployment descriptors will be placed.
	 * 
	 * @return The deployment path directory.
	 */
	public String getDeploymentPath() {
		return props.getProperty("deployment_path", "_NOT_SET_");
	}

	/**
	 * The resource path points to a directory on the local machine, where
	 * downloaded resource files will be placed.
	 * 
	 * @return The resource path directory.
	 */
	public String getResourcePath() {
		return props.getProperty("resource_path", "_NOT_SET_");
	}

	/**
	 * The JDBC URL to use to contact the database.
	 * 
	 * @return The JDBC URL.
	 */
	public String getJDBCUrl() {
		return props.getProperty("jdbc_url", "_NOT_SET_");
	}

	/**
	 * The driver class for the database that contains the actual schema to use
	 * in order to provide persistence.
	 * 
	 * @return The driver class to use.
	 */
	public String getDriverClass() {
		return props.getProperty("driver_class", "_NOT_SET_");
	}

	/**
	 * The timing handler is supposed to sleep for a certain period of time
	 * (e.g. 5 minutes). This configuration setting allows one to change the
	 * sleep duration for the timing handler.
	 * 
	 * @return The timing handler's sleep duration.
	 */
	public String getTimingHandlerSleepPeriod() {
		return props.getProperty("timing_handler_sleep_period", "_NOT_SET_");
	}

	/**
	 * Get the maximum number of attempts to make downloading a resource from
	 * the server, before we give up.
	 * 
	 * @return The maximum number of retries to download a resource.
	 */
	public int getResourceRetryLimit() {
		return Integer.parseInt(props.getProperty("resource_retry_limit", "3"));
	}

	/**
	 * Get the classpath for the player executable.
	 * 
	 * @return The player classpath.
	 */
	public String getPlayerClasspath() {
		return props.getProperty("player_classpath", "_NOT_SET_");
	}

	/**
	 * Get the player main class.
	 * 
	 * @return The player main class.
	 */
	public String getPlayerMainClass() {
		return props.getProperty("player_main_class", "_NOT_SET_");
	}

	/**
	 * Get player working directory.
	 * 
	 * @return The player working directory.
	 */
	public String getPlayerWorkingDir() {
		return props.getProperty("player_working_dir", "_NOT_SET_");
	}

	/**
	 * Get the interval that the manager thread sleeps before communicating with
	 * the server
	 * 
	 * @return The interval in seconds
	 */
	public String getCommunicationInterval() {
		return props.getProperty("communication_interval", "_NOT_SET_");
	}

	/**
	 * Get the server http URL
	 * 
	 * @return The server url
	 */
	public String getServerURL() {
		return props.getProperty("server_url", "_NOT_SET_");
	}

	/**
	 * Get the interval in seconds between screendumps
	 * 
	 * @return The screendump interval
	 */
	public String getScreenDumpInterval() {
		return props.getProperty("screendump_interval", "_NOT_SET_");
	}

	/**
	 * Get the size of the screen shot
	 * 
	 * @return The screenshot size in pixels
	 */
	public String getScreenShotSize() {
		return props.getProperty("screendump_size", "_NOT_SET_");
	}

	/**
	 * Get the size of the screen shot
	 * 
	 * @return The screenshot size in pixels
	 */
	public String getScreenShotStorageLocation() {
		return props.getProperty("screendump_storage_path", "_NOT_SET_");
	}
}
