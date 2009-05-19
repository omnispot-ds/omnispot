/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 29, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.config;

import java.io.File;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Encapsulate file storage settings.
 * 
 * @author gerogias
 */
public class FileStorageSettings extends ComponentSettings {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(FileStorageSettings.class);

	/**
	 * Folder under which all printscreens are located.
	 */
	private String printScreenFolder = null;

	/**
	 * Folder under which all content for deployment is located.
	 */
	private String contentFolder = null;

	/**
	 * The name of the current printscreen.
	 */
	private String printScreenName = null;

	/**
	 * Folder for all temporary files.
	 */
	private String tempFolder = null;
	
	/**
	 * @return String the name of the settings component
	 * @see gr.panouepe.monitor.common.settings.ComponentSettings#getName()
	 */
	public String getName() {
		return "File Storage Settings";
	}

	/**
	 * Package-private factory method.
	 * 
	 * @param configuration
	 *            the configuration to load from.
	 */
	void load(XMLConfiguration configuration) {

		logger.trace("Loading file-storage.ps-root-folder");
		this.printScreenFolder = configuration
				.getString("file-storage.ps-root-folder");
		logger.trace("Loading file-storage.content-root-folder");
		this.contentFolder = configuration
				.getString("file-storage.content-root-folder");
		logger.trace("Loading file-storage.printScreen-name");
		this.printScreenName = configuration
				.getString("file-storage.printScreen-name");
		logger.trace("Loading file-storage.temp-folder");
		this.tempFolder = configuration
				.getString("file-storage.temp-folder");
		createFolders();
	}

	/**
	 * Make sure the folders exist.
	 */
	private final void createFolders() {
		File psFolder = new File(printScreenFolder);
		if (!psFolder.isDirectory() && !psFolder.mkdirs()) {
			logger.error(psFolder + " does not exist and could not be created");
			throw new IllegalStateException(psFolder
					+ " does not exist and could not be created");
		}
		File cntFolder = new File(contentFolder);
		if (!cntFolder.isDirectory() && !cntFolder.mkdirs()) {
			logger
					.error(cntFolder
							+ " does not exist and could not be created");
			throw new IllegalStateException(cntFolder
					+ " does not exist and could not be created");
		}
		File tmpFolder = new File(tempFolder);
		if (!tmpFolder.isDirectory() && !tmpFolder.mkdirs()) {
			logger
					.error(tmpFolder
							+ " does not exist and could not be created");
			throw new IllegalStateException(tmpFolder
					+ " does not exist and could not be created");
		}
	}

	/**
	 * @return the contentFolder
	 */
	public String getContentFolder() {
		return contentFolder;
	}

	/**
	 * @return the printScreenFolder
	 */
	public String getPrintScreenFolder() {
		return printScreenFolder;
	}

	/**
	 * @return the printScreenName
	 */
	public String getPrintScreenName() {
		return printScreenName;
	}

	/**
	 * @return the tempFolder
	 */
	public String getTempFolder() {
		return tempFolder;
	}
}
