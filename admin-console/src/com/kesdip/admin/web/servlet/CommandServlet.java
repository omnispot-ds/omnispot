package com.kesdip.admin.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.kesdip.business.communication.ServerProtocolHandler;
import com.kesdip.business.constenum.IMessageParamsEnum;

@SuppressWarnings("serial")
public class CommandServlet extends BaseSpringContextServlet {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(CommandServlet.class);

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String playerUuid = req.getParameter(IMessageParamsEnum.INSTALLATION_ID);
		// unauthenticated player
		if (!isPlayerAuthenticated(playerUuid)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		ServerProtocolHandler handler = (ServerProtocolHandler) getSpringContext()
				.getBean("ServerProtocolHandler");
		try {
			handler.handleRequest(req, resp);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

}
