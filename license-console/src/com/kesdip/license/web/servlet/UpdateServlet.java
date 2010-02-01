/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 09 Ιαν 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.license.web.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.kesdip.business.domain.license.generated.Customer;
import com.kesdip.common.util.DateUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Implements the update allow/block logic.
 * <p>
 * It makes the following checks:
 * <ul>
 * <li>checks if the <code>user-agent</code> is from a program and not a browser
 * </li>
 * <li>queries the DB to check if the UUID exists and its support has not expire
 * </li>
 * <li>if the requested file is <code>site.xml</code>, it logs the action</li>
 * </ul>
 * If the conditions are not true, it returns a <code>403</code> code.
 * </p>
 * 
 * @author gerogias
 */
public class UpdateServlet extends HttpServlet {

	/**
	 * The update request logger.
	 */
	private static final Logger updateRequestLogger = Logger
			.getLogger("com.kesdip.license.UpdateRequestLogger");

	/**
	 * The normal logger.
	 */
	private static final Logger logger = Logger.getLogger(UpdateServlet.class);

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The Hibernate wrapper to use.
	 */
	private HibernateTemplate hibernateTemplate = null;

	/**
	 * The servlet context.
	 */
	private ServletContext servletContext = null;

	/**
	 * The actual update root.
	 */
	private String actualUpdateRoot = null;

	/**
	 * Message for <code>403</code> code.
	 */
	private static final String FORBIDDEN_MESSAGE = "You are not authorized to view the contents of the update site";

	/**
	 * Name of the <code>site.xml</code> file.
	 */
	private static final String SITE_XML = "site.xml";

	/**
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res)
			throws ServletException, IOException {

		// make sure this is not a browser
		String userAgent = req.getHeader("user-agent");
		if (!userAgent.startsWith("Java")) {
			if (logger.isDebugEnabled()) {
				logger.debug("'" + userAgent + "' forbidden");
			}
			res.sendError(HttpServletResponse.SC_FORBIDDEN, FORBIDDEN_MESSAGE);
			return;
		}
		// get the customer UUID
		String uuid = req.getRemoteUser();
		if (StringUtils.isEmpty(uuid)) {
			logger.debug("Empty customer uuid");
			res.sendError(HttpServletResponse.SC_FORBIDDEN, FORBIDDEN_MESSAGE);
			return;
		}
		// if requesting site.xml or the root (Eclipse does both), check the DB
		String uri = req.getRequestURI();
		String servletPath = req.getServletPath();
		if (uri.endsWith(servletPath) || uri.endsWith(SITE_XML)) {
			if (!supportEnabled(uuid)) {
				logger.warn("Update denied for '" + uuid + "'");
				res.sendError(HttpServletResponse.SC_FORBIDDEN,
						FORBIDDEN_MESSAGE);
				return;
			}
		}
		// if requesting site.xml, log the request
		if (uri.endsWith(SITE_XML)) {
			logUpdateRequest(uuid, req.getRemoteAddr(), userAgent);
		}
		// all OK, forward to the actual file
		String translatedUri = uri.substring(req.getContextPath().length())
				.replace(servletPath, actualUpdateRoot);
		if (logger.isTraceEnabled()) {
			logger.trace("Forwarding to '" + translatedUri + "'");
		}
		RequestDispatcher rd = servletContext
				.getRequestDispatcher(translatedUri);
		rd.forward(req, res);
	}

	/**
	 * Initialize {@link HibernateTemplate} and the actualUpdateRoot.
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {

		servletContext = config.getServletContext();

		WebApplicationContext springCtx = WebApplicationContextUtils
				.getRequiredWebApplicationContext(servletContext);

		hibernateTemplate = (HibernateTemplate) springCtx
				.getBean("hibernateTemplate");
		actualUpdateRoot = config.getInitParameter("updateSiteRoot");
	}

	/**
	 * Check if the given customer has support enabled.
	 * 
	 * @param uuid
	 *            the customer uuid
	 * @return boolean <code>true</code> if support is enabled
	 */
	@SuppressWarnings("unchecked")
	private final boolean supportEnabled(String uuid) {
		List<Customer> results = hibernateTemplate.find("select c from "
				+ Customer.class.getName() + " c "
				+ "where c.uuid=? and c.supportExpiryDate>=? and active=true",
				new Object[] { uuid, new Date() });
		return !results.isEmpty();
	}

	/**
	 * Logs the update request using Log4J.
	 * 
	 * @param uuid
	 *            the customer UUID
	 * @param remoteIp
	 *            the IP of the remote machine
	 * @param userAgent
	 *            the requesting user agent
	 */
	private final void logUpdateRequest(String uuid, String remoteIp,
			String userAgent) {
		StringBuilder line = new StringBuilder(uuid).append('\t').append(
				remoteIp).append('\t').append(userAgent).append('\t').append(
				new SimpleDateFormat(DateUtils.DATE_TIME_FORMAT)
						.format(new Date()));
		updateRequestLogger.info(line);
	}

}
