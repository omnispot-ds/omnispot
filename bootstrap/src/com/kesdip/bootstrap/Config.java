/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

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
	 * The preferences object. Relying on good old Java preferences for
	 * making these persistent.
	 */
	private static Preferences prefs = 
		Preferences.systemRoot().node(Config.class.getName());

	/**
	 * The configuratino singleton object.
	 */
	private static Config singleton = new Config();
	
	/**
	 * Accessor to the configuration singleton object.
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
	
	/**
	 * The deployment path points to a directory on the local machine, where
	 * downloaded deployment descriptors will be placed.
	 * @return The deployment path directory.
	 */
	public String getDeploymentPath() {
		return prefs.get("deployment_path", "_NOT_SET_");
	}
	
	/**
	 * The resource path points to a directory on the local machine, where
	 * downloaded resource files will be placed.
	 * @return The resource path directory.
	 */
	public String getResourcePath() {
		return prefs.get("resource_path", "_NOT_SET_");
	}
	
	/**
	 * The JDBC URL to use to contact the database.
	 * @return The JDBC URL.
	 */
	public String getJDBCUrl() {
		return prefs.get("jdbc_url", "_NOT_SET_");
	}
	
	/**
	 * The driver class for the database that contains the actual schema to
	 * use in order to provide persistence.
	 * @return The driver class to use.
	 */
	public String getDriverClass() {
		return prefs.get("driver_class", "_NOT_SET_");
	}
	
	/**
	 * The timing handler is supposed to sleep for a certain period of time
	 * (e.g. 5 minutes). This configuration setting allows one to change the
	 * sleep duration for the timing handler.
	 * @return The timing handler's sleep duration.
	 */
	public String getTimingHandlerSleepPeriod() {
		return prefs.get("timing_handler_sleep_period", "_NOT_SET_");
	}
	
	/**
	 * Get the maximum number of attempts to make downloading a resource from
	 * the server, before we give up.
	 * @return The maximum number of retries to download a resource.
	 */
	public int getResourceRetryLimit() {
		return prefs.getInt("resource_retry_limit", 3);
	}
	
	/**
	 * Main method used to dump or set the preferences. With no arguments,
	 * preferences are dumped to the log4j appenders, otherwise the first
	 * argument is considered to be the path to a properties file whose
	 * contents are set in the preferences for this node.
	 * 
	 * @param args The command line arguments.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			logger.info("You specified no arguments. Dumping preferences.");
			try {
				for (String key : prefs.keys()) {
					logger.info("Mapping: " + key + " -> " + prefs.get(key, "DEFAULT") + ".");
				}
			} catch (Exception e) {
				logger.error("Error dumping preferences", e);
			}
			return;
		}
		
		try {
			prefs.clear();
			
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(args[0]);
			props.load(fis);
			fis.close();
			
			for (Object key : props.keySet()) {
				String keyString = (String) key;
				prefs.put(keyString, props.getProperty(keyString));
			}
			
			prefs.flush();
			
			logger.info("Preferences have been set.");
		} catch (Exception e) {
			logger.error("Unable to modify preferences.", e);
		}
	}
}
