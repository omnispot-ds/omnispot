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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.business.config.FileStorageSettings;
import com.kesdip.business.domain.admin.generated.Content;
import com.kesdip.business.logic.ContentLogic;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StreamUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Serves static content as requested by clients. The servlet only supports GET
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
 * <p>
 * The servlet also supports the <code>Range</code> header for partial download
 * resume. Only the first part of the range is taken into account. E.g. for a
 * header <code>Range: bytes=500-1000</code>, the servlet will start serving
 * from byte 500 until the end of the file, not until byte 1000. If multiple
 * ranges are requested, only the first entry is taken into account. If the
 * header is malformed, it is ignored.
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
	 * Pattern to select a bytes pattern range.
	 */
	private static final Pattern BYTES_PATTERN = Pattern
			.compile("byte\\=(\\d+)\\-.*");

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
		if (logger.isTraceEnabled()) {
			logger.trace("Received request '" + pathInfo + "'");
		}
		// 1. treat as file
		File file = getFileByName(pathInfo);
		if (file != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Located file: '" + file.getAbsolutePath() + "'");
			} 
		} else {
			// 2. treat as UUID
			file = getFileByUuid(pathInfo);
			if (file != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Located UUID: '" + pathInfo + "'");
				}
			}
		}
		// nothing found, return
		if (file == null) {
			logger.debug("Not found");
			resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		// file size header
		addFileSizeHeader(file, resp);

		// get the byte to start from
		int bytePosition = extractRangeStart(req.getHeader("Range"));

		StreamUtils.streamFile(file, resp.getOutputStream(), bytePosition);
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
		String fileName = null;
		try {
			fileName = FileUtils.getName(URLDecoder.decode(pathInfo, "UTF-8"));
		} catch (UnsupportedEncodingException uee) {
			// do nothing
		}
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
	 * Gets a file's length and adds it as a response header.
	 * 
	 * @param file
	 *            the file
	 * @param response
	 *            the response
	 */
	private final void addFileSizeHeader(File file, HttpServletResponse response) throws IOException {
		int size = FileUtils.getSize(file);
		response.setHeader("Content-Length", String.valueOf(size));
	}

	/**
	 * Extract the range start byte index, if the <code>Range</code> header was
	 * included.
	 * 
	 * @param rangeString
	 *            the <code>Range</code> header value
	 * @return int the range start, or 0 if the <code>Range</code> header was
	 *         not present or was malformed
	 */
	final int extractRangeStart(String rangeString) {
		if (StringUtils.isEmpty(rangeString)) {
			return 0;
		}
		Matcher rangeMatcher = BYTES_PATTERN.matcher(rangeString);
		if (!rangeMatcher.matches()) {
			return 0;
		}
		String rangeStart = rangeMatcher.group(1);
		int rangeStartValue = 0;
		try {
			rangeStartValue = Integer.valueOf(rangeStart);
		} catch (NumberFormatException nfe) {
			// do nothing
		}
		return rangeStartValue;
	}

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
