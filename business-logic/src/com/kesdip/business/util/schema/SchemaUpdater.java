/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 18 Μαϊ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.util.schema;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import com.kesdip.business.exception.SchemaUpdateFailedException;
import com.kesdip.common.util.DBUtils;

/**
 * Utility class which takes care of updating the schema to the latest version.
 * <p>
 * Contains a list of all versions so far. If it does not find table
 * <code>SCHEMA_VERSION</code>, it starts executing scripts from the first
 * member of the list. Otherwise, it starts from the next in order.
 * </p>
 * <p>
 * The package where SQL files are located is given as a startup argument.
 * </p>
 * 
 * @author gerogias
 */
public class SchemaUpdater {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(SchemaUpdater.class);

	/**
	 * Location of SQL files to use.
	 */
	private String sqlPackage = null;

	/**
	 * The list of known schema version to update to.
	 */
	private String[] schemaVersions = null;

	/**
	 * The classloader to use.
	 */
	private ClassLoader classLoader = null;

	/**
	 * Constructor.
	 * 
	 * @param basePackage
	 *            the package where SQL files are located in path notation (i.e.
	 *            /foo/bar)
	 * @param clazzLoader
	 *            the classloader to use; if <code>null</code> the current
	 *            classloader is used
	 * @param knownVersions
	 *            the list of known schema versions for the calling module
	 */
	public SchemaUpdater(String basePackage, ClassLoader clazzLoader,
			String[] knownVersions) {
		this.sqlPackage = basePackage;
		this.schemaVersions = knownVersions;
		if (sqlPackage == null) {
			sqlPackage = "/";
		}
		if (!sqlPackage.endsWith("/")) {
			sqlPackage += "/";
		}
		this.classLoader = (clazzLoader != null) ? clazzLoader
				: SchemaUpdater.class.getClassLoader();
	}

	/**
	 * Main logic method.
	 * 
	 * @param hibernateTemplate
	 *            the template to use. For some reason, dependency injection did
	 *            not work!
	 * @throws SchemaUpdateFailedException
	 *             on error
	 */
	@Transactional(isolation = Isolation.READ_COMMITTED)
	public final void updateSchema(HibernateTemplate hibernateTemplate)
			throws SchemaUpdateFailedException {
		Session session = hibernateTemplate.getSessionFactory().openSession();
		SQLQuery query = session
				.createSQLQuery("select VERSION_NUM from SCHEMA_VERSION");
		String version = null;
		try {
			version = (String) query.uniqueResult();
			logger.info("Schema version: " + version);
		} catch (HibernateException e) {
			// this exception may happen, if the table does not exist
			logger.warn("Could not locate VERSION_NUM", e);
		}

		// iterate the known versions and execute only for those greater than
		// the known version
		Connection connection = session.connection();
		try {
			connection.setAutoCommit(false);
		} catch (SQLException se) {
			logger.error("Failed to set autocommit to false", se);
		}
		String updateSql = null, sqlName = null;
		for (String knownVersion : schemaVersions) {
			if (version == null || knownVersion.compareTo(version) > 0) {
				logger.info("Executing SQL for version " + knownVersion);
				try {
					sqlName = sqlPackage + knownVersion + ".sql";
					updateSql = com.kesdip.common.util.StreamUtils
							.readResource(classLoader.getResource(sqlName));
				} catch (IOException ioe) {
					logger
							.fatal("Failed to read " + knownVersion + ".sql",
									ioe);
					throw new SchemaUpdateFailedException("Failed to read "
							+ knownVersion + ".sql", ioe);
				}
				try {
					DBUtils.executeBatchUpdate(connection, updateSql);
					if (logger.isInfoEnabled()) {
						logger.info("Committing transaction for "
								+ knownVersion);
					}
					connection.commit();
				} catch (Exception e) {
					logger.fatal("Failed while executing " + knownVersion
							+ ".sql", e);
					if (e instanceof SQLException) {
						SQLException nextEx = (SQLException)e;
						while ((nextEx = nextEx.getNextException()) != null) {
							logger.fatal("Next exception", nextEx);
						}
					}
					try {
						logger.info("About to roll-back changes");
						connection.rollback();
					} catch (Exception e1) {
						logger.error("Error while rolling back batch update",
								e1);
					}
					throw new SchemaUpdateFailedException(
							"Failed while executing " + knownVersion + ".sql",
							e);
				}
			}
		}
		try {
			session.close();
		} catch (Exception e) {
			// do nothing
		}
	}
}
