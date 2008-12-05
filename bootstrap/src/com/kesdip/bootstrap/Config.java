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
	
	private static Preferences prefs = 
		Preferences.systemRoot().node(Config.class.getName());

	private static Config singleton = new Config();
	
	public static Config getSingleton() {
		return singleton;
	}
	
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
	
	public String getDeploymentPath() {
		return prefs.get("deployment_path", "_NOT_SET_");
	}
	
	public String getResourcePath() {
		return prefs.get("resource_path", "_NOT_SET_");
	}
	
	public String getJDBCUrl() {
		return prefs.get("jdbc_url", "_NOT_SET_");
	}
	
	public String getDriverClass() {
		return prefs.get("driver_class", "_NOT_SET_");
	}
	
	public String getTimingHandlerSleepPeriod() {
		return prefs.get("timing_handler_sleep_period", "_NOT_SET_");
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
