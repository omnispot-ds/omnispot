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
	 * Package containing SQLs for schema updating. 
	 */
	public static final String SQL_PKG = "com/kesdip/admin/web/util/schema/";
	
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
		ApplicationContext context = WebApplicationContextUtils
				.getWebApplicationContext(contextEvent.getServletContext());
		SchemaUpdater schemaUpdater = new SchemaUpdater(SQL_PKG);
		schemaUpdater.updateSchema((HibernateTemplate) context
				.getBean("hibernateTemplate"));
	}
}
