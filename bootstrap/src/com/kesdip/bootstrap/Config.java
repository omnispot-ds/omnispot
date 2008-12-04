/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.prefs.Preferences;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

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
			
			setupDriver(getJDBCUrl());
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
	
	/**
	 * Helper method to get a connection from the connection pool.
	 * @return A connection from the connection pool.
	 * @throws SQLException iff something goes wrong.
	 */
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:local");
	}
	
	private static void setupDriver(String connectURI) throws Exception {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool = new GenericObjectPool(null);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
        	new DriverManagerConnectionFactory(connectURI, null);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        @SuppressWarnings("unused")
		PoolableConnectionFactory poolableConnectionFactory =
			new PoolableConnectionFactory(
					connectionFactory, connectionPool, null, null, false, false);

        //
        // Finally, we create the PoolingDriver itself...
        //
        Class.forName("org.apache.commons.dbcp.PoolingDriver");
        PoolingDriver driver = (PoolingDriver)
        		DriverManager.getDriver("jdbc:apache:commons:dbcp:");

        //
        // ...and register our pool with it.
        //
        driver.registerPool("local", connectionPool);

        //
        // Now we can just use the connect string "jdbc:apache:commons:dbcp:local"
        // to access our pool of Connections.
        //
        
        logger.info("We have registered connection pool " +
        		"at jdbc:apache:commons:dbcp:local");
    }

	
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
