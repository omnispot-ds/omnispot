package com.kesdip.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.kesdip.business.communication.ServerProtocolHandler;

@SuppressWarnings("serial")
public class CommandServlet extends HttpServlet {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(CommandServlet.class);
	
	XmlWebApplicationContext ctx;
	
	@Override
	public void init() throws ServletException {
		ctx = new XmlWebApplicationContext();
		ctx.setServletContext(getServletContext());
		ctx.setConfigLocations(new String[] { "/WEB-INF/spring/application-context.xml" });
		ctx.refresh();
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		ServerProtocolHandler handler = (ServerProtocolHandler)ctx.getBean("ServerProtocolHandler");
		try {
			handler.handleRequest(req, resp);
		} catch (Exception e) {
			logger.error(e);
		}
	}

}
