/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: Dec 29, 2008
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.business.config;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

/**
 * Singleton class reading and encapsulating application-wide settings.
 * <p>
 * The values are read from an XML file, adhering to Commons Configuration
 * format. The file should be located in the classpath; its name is given with
 * the first call to the factory method.
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
	 * The settings resource.
	 */
	private URL settingsResource = null;

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
	private ApplicationSettings(URL settingsResource) {
		this.settingsResource = settingsResource;
	}

	/**
	 * Get the singleton instance after initializing with the given resource
	 * path.
	 * 
	 * @param settingsResource
	 *            the settings resource URL
	 * @return {@link ApplicationSettings} the instance
	 */
	public static ApplicationSettings getInstance(URL settingsResource) {

		instance = new ApplicationSettings(settingsResource);
		instance.loadSettings();
		return instance;
	}

	/**
	 * Get the singleton instance
	 * 
	 * @return {@link ApplicationSettings} the instance or <code>null</code> if
	 *         {@link #getInstance(String)} has not been called yet
	 */
	public static ApplicationSettings getInstance() {

		return instance;
	}

	/**
	 * Loads the settings from the configuration.
	 */
	final void loadSettings() throws SettingsLoadingException {

		XMLConfiguration configuration = null;

		try {
			LOGGER.debug("Loading XML configuration");
			configuration = new XMLConfiguration(settingsResource);
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
