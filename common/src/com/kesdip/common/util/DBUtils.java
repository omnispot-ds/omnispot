/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * Wrapper around common DB operations.
 * 
 * @author gerogias
 */
public class DBUtils {
	private static final Logger logger = Logger.getLogger(DBUtils.class);

	private static boolean driverSetup = false;

	/**
	 * Helper method to get a connection from the connection pool.
	 * 
	 * @return A connection from the connection pool.
	 * @throws SQLException
	 *             iff something goes wrong.
	 */
	public static Connection getConnection() throws Exception {
		if (!driverSetup) {
			throw new Exception(
					"Driver has not been set up with a call to setupDriver().");
		}

		return DriverManager.getConnection("jdbc:apache:commons:dbcp:local");
	}

	/**
	 * Helper to set up a connection pool with the driver manager, so as to be
	 * used by the getConnection() method above.
	 * 
	 * @param jdbcUrl
	 *            The JDBC URL to use to get the actual connections to the
	 *            database.
	 * @throws Exception
	 *             iff something goes wrong.
	 */
	public static void setupDriver(String jdbcUrl) throws Exception {
		if (driverSetup) {
			return;
		}

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
		ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
				jdbcUrl, null);

		//
		// Now we'll create the PoolableConnectionFactory, which wraps
		// the "real" Connections created by the ConnectionFactory with
		// the classes that implement the pooling functionality.
		//
		@SuppressWarnings("unused")
		PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(
				connectionFactory, connectionPool, null, null, false, false);

		//
		// Finally, we create the PoolingDriver itself...
		//
		Class.forName("org.apache.commons.dbcp.PoolingDriver");
		PoolingDriver driver = (PoolingDriver) DriverManager
				.getDriver("jdbc:apache:commons:dbcp:");

		//
		// ...and register our pool with it.
		//
		driver.registerPool("local", connectionPool);

		//
		// Now we can just use the connect string
		// "jdbc:apache:commons:dbcp:local"
		// to access our pool of Connections.
		//

		logger.info("We have registered connection pool "
				+ "at jdbc:apache:commons:dbcp:local");

		driverSetup = true;

		// get the 1st connection to initialize whatever was left to initialize
		Connection c = null;
		try {
			c = getConnection();
		} catch (Exception e) {
			logger.error("Error initializing connection", e);
		} finally {
			try {
				c.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

	/**
	 * Executes a sequence of batch statements. The statements must one on each
	 * line and can contain a semi-colon (;) or not.
	 * 
	 * @param con
	 *            the connection to use
	 * @param batchQuery
	 *            the query string
	 * @throws Exception
	 *             on error
	 */
	public static void executeBatchUpdate(Connection con, String batchQuery)
			throws Exception {
		Statement stm = null;
		BufferedReader reader = null;
		try {
			stm = con.createStatement();
			reader = new BufferedReader(new StringReader(batchQuery));
			String line = null;
			while ((line = reader.readLine()) != null) {
				// ignore empty lines and comments
				if (StringUtils.isEmpty(line.trim()) || line.startsWith("--")) {
					continue;
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Adding batch [" + line + "]");
				}
				// trim any semi-colons
				line = line.replace(";", " ");
				stm.addBatch(line);
			}
			stm.executeBatch();
		} catch (SQLException e) {
			logger.error(e, e);
			throw e;
		} catch (IOException e) {
			logger.error(e, e);
			throw e;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					logger.error(e, e);
				}
			}
			if (stm != null) {
				try {
					stm.close();
				} catch (SQLException e) {
					logger.error(e, e);
				}
			}
		}
	}

	/**
	 * Executes the given query using the given parameters.
	 * 
	 * @param con
	 *            the connection to use
	 * @param query
	 *            the query string
	 * @param params
	 *            the parameters
	 * @return int the number of updated rows
	 * @throws SQLException
	 *             on error
	 */
	public static int executeUpdate(Connection con, String query,
			Object... params) throws SQLException {
		int updateCount = 0;
		PreparedStatement stm = null;
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Executing [" + query + "]");
			}
			stm = con.prepareStatement(query);
			setBoundVariables(stm, params);
			updateCount = stm.executeUpdate();
		} catch (SQLException e) {
			logger.error(e, e);
			throw e;
		} finally {
			if (stm != null) {
				try {
					stm.close();
				} catch (SQLException e) {
					logger.debug(e, e);
				}
			}
		}
		return updateCount;
	}

	/**
	 * Set the parameters of the given prepared statements.
	 * 
	 * @param stm
	 *            The statement.
	 * @param params
	 *            The parameters.
	 * @throws SQLException
	 *             If an error occurs.
	 */
	private static void setBoundVariables(PreparedStatement stm,
			Object... params) throws SQLException {
		if (params == null)
			return;
		for (int i = 0; i < params.length; i++) {
			if (params[i] == null) {
				stm.setNull(i + 1, Types.VARCHAR);
			} else if (params[i] instanceof Date) {
				stm.setDate(i + 1, new java.sql.Date(((Date) params[i])
						.getTime()));
			} else {
				stm.setObject(i + 1, params[i]);
			}
		}
	}

}
