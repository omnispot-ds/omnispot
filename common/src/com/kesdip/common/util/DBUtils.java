/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

public class DBUtils {
	private static final Logger logger = Logger.getLogger(DBUtils.class);
	
	private static boolean driverSetup = false;
	
	/**
	 * Helper method to get a connection from the connection pool.
	 * @return A connection from the connection pool.
	 * @throws SQLException iff something goes wrong.
	 */
	public static Connection getConnection() throws Exception {
		if (!driverSetup)
			throw new Exception("Driver has not been set up with a call to setupDriver().");
		
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:local");
	}
	
	/**
	 * Helper to set up a connection pool with the driver manager, so as to 
	 * be used by the getConnection() method above.
	 * 
	 * @param jdbcUrl The JDBC URL to use to get the actual connections
	 * to the database.
	 * @throws Exception iff something goes wrong.
	 */
	public static void setupDriver(String jdbcUrl) throws Exception {
		if (driverSetup)
			return;
		
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
        	new DriverManagerConnectionFactory(jdbcUrl, null);

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
        
        driverSetup = true;
    }


}
