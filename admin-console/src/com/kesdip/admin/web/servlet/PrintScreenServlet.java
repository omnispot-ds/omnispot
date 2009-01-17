/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Jan 17, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.config.FileStorageSettings;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Serves print-screens for the requested installation.
 * 
 * @author gerogias
 */
public class PrintScreenServlet extends HttpServlet {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(PrintScreenServlet.class);
	
	/**
	 * File storage settings.
	 */
	private FileStorageSettings storageSettings = null;

	/**
	 * Serves back the printscreen for the requested installation. If it does
	 * not exist, it returns a default image.
	 * 
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String pathInfo = request.getPathInfo();
		if (logger.isTraceEnabled()) {
			logger.trace("Called with pathInfo: " + pathInfo);
		}
		String uuid = "";
		if (!StringUtils.isEmpty(pathInfo)) {
			// remove leading '/'
			uuid = pathInfo.substring(1);
		}
		File psFile = getPrintScreenFile(uuid);
		if (logger.isTraceEnabled()) {
			logger.trace("PrintScreen file: " + psFile.getAbsolutePath());
		}
		FileInputStream input = null;
		try {
			input = new FileInputStream(psFile);
			response.setContentLength(input.available());
			response.setContentType(getServletContext().getMimeType(
					FileUtils.getName(psFile.getAbsolutePath())));
			StreamUtils.copyStream(input, response.getOutputStream());
		} catch (Exception e) {
			logger.error("Error streaming file", e);
			throw new ServletException("Error streaming file", e);
		} finally {
			StreamUtils.close(input);
		}
	}

	/**
	 * Initialization logic.
	 * 
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		storageSettings = ApplicationSettings.getInstance()
				.getFileStorageSettings();
	}

	/**
	 * Return the printscreen file for this UUID. The method checks for file
	 * <em>folderBase/uuid/printScreen.jpg</em> and returns the file. If it
	 * does not exist, it returns the <em>defaultPrintScreen</em>.
	 * 
	 * @param uuid
	 *            the uuid
	 * @param storageSettings
	 *            the file storage settings
	 * @return File the file
	 */
	private final File getPrintScreenFile(String uuid) {
		String fileName = storageSettings.getPrintScreenFolder() + uuid
				+ File.separatorChar + storageSettings.getPrintScreenName();
		File psFile = new File(fileName);
		return psFile.isFile() ? psFile : new File(storageSettings
				.getDefaultPrintScreen());
	}

}
