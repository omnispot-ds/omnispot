/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Feb 2, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.config.FileStorageSettings;
import com.kesdip.business.constenum.IMessageParamsEnum;
import com.kesdip.business.domain.generated.Content;
import com.kesdip.business.logic.ContentLogic;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;

/**
 * Serves static content as requested by clients. The serlvet only supports GET
 * requests.
 * <p>
 * Contents serving is done using a cascading approach
 * <ol>
 * <li>the path info is treated as a file name under
 * {@link FileStorageSettings#getContentFolder()}</li>
 * <li>if not found, it is treated as a {@link Content#getUrl()}</li>
 * <li>if not found, a {@link HttpServletResponse#SC_NOT_FOUND} is returned</li>
 * </ol>
 * </p>
 * <p>
 * The player's UUID should be part of the URL and correspond to a valid player,
 * or the request is forbidden.
 * </p>
 * 
 * @author gerogias
 */
public class ContentServlet extends BaseSpringContextServlet {


	/**
	 * The logger.
	 */
	private final static Logger logger = Logger.getLogger(ContentServlet.class);

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Service method.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String pathInfo = req.getPathInfo();
		String playerUuid = req.getParameter(IMessageParamsEnum.INSTALLATION_ID);
		if (logger.isInfoEnabled()) {
			logger.info("Received request '" + pathInfo + "' from player '"
					+ playerUuid + "'");
		}
		// unauthenticated player
		if (!isPlayerAuthenticated(playerUuid)) {
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return;
		}
		// 1. treat as file
		File file = getFileByName(pathInfo);
		if (file != null) {
			logger.debug("Located file");
			StreamUtils.streamFile(file, resp.getOutputStream());
			return;
		}
		// 2. treat as UUID
		file = getFileByUuid(pathInfo);
		if (file != null) {
			logger.debug("Located UUID");
			StreamUtils.streamFile(file, resp.getOutputStream());
			return;
		}
		logger.debug("Not found");
		resp.sendError(HttpServletResponse.SC_NOT_FOUND);
	}

	/**
	 * Attempts to match the filename under
	 * {@link FileStorageSettings#getContentFolder()}. For security reasons, it
	 * cuts off any preeceding path info.
	 * 
	 * @param pathInfo
	 *            the path information
	 * @return File the file or <code>null</code>
	 */
	final File getFileByName(String pathInfo) {
		FileStorageSettings storageSettings = ApplicationSettings.getInstance()
				.getFileStorageSettings();
		String fileName = FileUtils.getName(pathInfo);
		File targetFile = new File(storageSettings.getContentFolder(), fileName);
		return targetFile.isFile() ? targetFile : null;
	}

	/**
	 * Attempts to match the pathInfo as a {@link Content#getUrl()}.
	 * 
	 * @param pathInfo
	 *            the path info
	 * @return File the matched file or <code>null</code>
	 */
	final File getFileByUuid(String pathInfo) {
		ContentLogic contentLogic = (ContentLogic) getSpringContext().getBean(
				"contentLogic");
		// cut-off initial '/'
		pathInfo = pathInfo.startsWith("/") ? pathInfo.substring(1) : pathInfo;
		Content content = contentLogic.getContentByUrl(pathInfo);
		if (content == null) {
			return null;
		}
		FileStorageSettings storageSettings = ApplicationSettings.getInstance()
				.getFileStorageSettings();
		File targetFile = new File(storageSettings.getContentFolder(), content
				.getLocalFile());
		return targetFile.isFile() ? targetFile : null;
	}

	/**
	 * Check if a player with this UUID exists.
	 * 
	 * @param playerUuid
	 *            the UUID
	 * @return boolean <code>true</code> if the player exists
	 */

	/* ******* Unsupported HTTP methods ******* */

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doHead(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doHead(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doOptions(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

	/**
	 * Unsupported.
	 * 
	 * @see javax.servlet.http.HttpServlet#doTrace(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doTrace(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}

}
