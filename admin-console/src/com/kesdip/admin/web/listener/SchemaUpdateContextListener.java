/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 18 Μαϊ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kesdip.business.util.schema.SchemaUpdater;

/**
 * J2EE context listener which takes care of updating the DB schema.
 * <p>
 * Important: Declaration of this listener in <code>web.xml</code> must follow
 * that of Spring's.
 * </p>
 * 
 * @author gerogias
 */
public class SchemaUpdateContextListener implements ServletContextListener {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(SchemaUpdateContextListener.class);

	/**
	 * Package containing SQLs for schema updating.
	 */
	public static final String SQL_PKG = "com/kesdip/admin/web/util/schema/";

	/**
	 * The list of admin console schema versions.
	 */
	private final String[] VERSIONS = { "1.0", "1.1", "1.2", "1.3" };

	/**
	 * Empty implementation.
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// Do nothing
	}

	/**
	 * Access Spring context, get <code>schemaUpdater</code> and call it.
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent contextEvent) {
		logger.info("Getting Spring ApplicationContext");
		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(contextEvent.getServletContext());
		logger.info("Init SchemaUpdater");
		SchemaUpdater schemaUpdater = new SchemaUpdater(SQL_PKG, getClass()
				.getClassLoader(), VERSIONS);
		logger.info("Update schema");
		schemaUpdater.updateSchema((HibernateTemplate) context
				.getBean("hibernateTemplate"));
	}
}
