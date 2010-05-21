/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 2, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.kesdip.business.logic.InstallationLogic;

/**
 * Base class for all servlets wishing to have a hold of the current spring
 * context.
 * 
 * @author gerogias
 */
@SuppressWarnings("serial")
public abstract class BaseSpringContextServlet extends HttpServlet {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BaseSpringContextServlet.class);

	/**
	 * The Spring context.
	 */
	private XmlWebApplicationContext springContext = null;

	@Override
	public void init() throws ServletException {
		springContext = new XmlWebApplicationContext();
		springContext.setServletContext(getServletContext());
		springContext
				.setConfigLocations(new String[] { "/WEB-INF/spring/application-context.xml" });
		springContext.refresh();
	}

	/**
	 * @return the springContext
	 */
	protected XmlWebApplicationContext getSpringContext() {
		return springContext;
	}

	/**
	 * Utility method to check if a player exists.
	 * 
	 * @param playerUuid
	 * @return
	 */
	final boolean isPlayerAuthenticated(String playerUuid) {
		InstallationLogic logic = (InstallationLogic) getSpringContext()
				.getBean("installationLogic");
		return logic.getInstallationByUuid(playerUuid) != null;
	}

	/**
	 * Close the open spring context to make sure there are no leaks.
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		try {
			springContext.close();
		} catch (Exception e) {
			logger.warn(e);
		}
	}}
