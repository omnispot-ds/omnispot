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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import com.kesdip.common.util.StreamUtils;

/**
 * Singleton class reading and encapsulating application-wide settings.
 * <p>
 * The values are read from an XML file, adhering to Commons Configuration
 * format. The file should be named <code>monitor-settings.xml</code>. The
 * folder it is located in is given in system variable
 * <code>config.folder</code>. If the property does not exist, the default
 * settings are loaded from the classpath.
 * </p>
 * 
 * @author gerogias
 */
public class ApplicationSettings {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(ApplicationSettings.class);

	/**
	 * The name of the settings file.
	 */
	public final static String SETTINGS_FILE = "console-settings.xml";

	/**
	 * The settings resource.
	 */
	public final static String SETTINGS_RESOURCE = "com/kesdip/business/config/"
			+ SETTINGS_FILE;

	/**
	 * The system property for the config folder.
	 */
	public final static String ROOT_FOLDER_PROPERTY = "config.folder";

	/**
	 * The instance.
	 */
	private static ApplicationSettings instance = null;

	/**
	 * File storage settings.
	 */
	private FileStorageSettings fileStorageSettings = null;

	/**
	 * Server settings.
	 */
	private ServerSettings serverSettings = null;
	
	/**
	 * The logger.
	 */
	private final static Logger LOGGER = Logger
			.getLogger(ApplicationSettings.class);

	/**
	 * Private constructor.
	 */
	private ApplicationSettings() {
		// do nothing
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return {@link ApplicationSettings} the instance
	 */
	public static ApplicationSettings getInstance() {

		if (instance == null) {
			instance = new ApplicationSettings();
			instance.loadSettings();
		}
		return instance;
	}

	/**
	 * Loads the settings from the configuration.
	 */
	final void loadSettings() throws SettingsLoadingException {

		URL url = createUrl();
		XMLConfiguration configuration = null;

		try {
			LOGGER.debug("Loading XML configuration");
			configuration = new XMLConfiguration(url);
		} catch (ConfigurationException ce) {
			LOGGER.error("Error loading settings", ce);
			throw new SettingsLoadingException(ce);
		}

		LOGGER.debug("Loading file storage settings");
		fileStorageSettings = new FileStorageSettings();
		fileStorageSettings.load(configuration);
		LOGGER.debug("Loading server settings");
		serverSettings = new ServerSettings();
		serverSettings.load(configuration);
	}

	/**
	 * Loads the XML from the config folder. If it does not exist, it falls back
	 * to the one in the classpath.
	 * 
	 * @return String the loaded XML
	 * @throws SettingsLoadingException
	 *             is an error occurs
	 */
	static String loadXml() throws SettingsLoadingException {

		String root = System.getProperty(ROOT_FOLDER_PROPERTY);
		String xml = null;
		if (root != null) {
			File file = new File(root, SETTINGS_FILE);
			if (!file.isFile()) {
				throw new SettingsLoadingException("File '"
						+ file.getAbsolutePath() + "' does not exist");
			}

			try {
				InputStream in = new FileInputStream(file);
				xml = StreamUtils.readString(in);
			} catch (IOException ioe) {
				throw new SettingsLoadingException("Error reading '"
						+ file.getAbsolutePath() + "'", ioe);
			}
		} else {
			try {
				InputStream in = ApplicationSettings.class.getClassLoader()
						.getResourceAsStream(SETTINGS_RESOURCE);
				xml = StreamUtils.readString(in);
			} catch (IOException ioe) {
				throw new SettingsLoadingException("Error reading '"
						+ SETTINGS_RESOURCE + "'", ioe);
			}
		}
		return xml;
	}

	/**
	 * Creates the URL from the config folder. If it does not exist, it falls
	 * back to the one in the classpath.
	 * 
	 * @return URL the URL pointing to the expected location of the XML
	 * @throws SettingsLoadingException
	 *             is an error occurs
	 */
	static URL createUrl() throws SettingsLoadingException {

		String root = System.getProperty(ROOT_FOLDER_PROPERTY);
		if (root != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Root folder " + root);
			}
			File file = new File(root, SETTINGS_FILE);
			if (!file.isFile()) {
				logger.error("File '" + file.getAbsolutePath()
						+ "' does not exist");
				throw new SettingsLoadingException("File '"
						+ file.getAbsolutePath() + "' does not exist");
			}
			try {
				return new URL("file:///" + file.getAbsolutePath());
			} catch (MalformedURLException mue) {
				logger.error(mue);
				throw new SettingsLoadingException(mue);
			}
		} else {
			logger.debug("Loading from classpath");
			return ApplicationSettings.class.getClassLoader().getResource(
					SETTINGS_RESOURCE);
		}
	}

	/**
	 * @return the fileStorageSettings
	 */
	public FileStorageSettings getFileStorageSettings() {
		return fileStorageSettings;
	}

	/**
	 * @return the serverSettings
	 */
	public ServerSettings getServerSettings() {
		return serverSettings;
	}
}
