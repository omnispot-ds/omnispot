/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: May 19, 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.admin.web.listener;

import java.io.File;
import java.net.URL;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import com.kesdip.business.config.ApplicationSettings;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;

/**
 * Applications listener which loads the application settings.
 * 
 * @author gerogias
 */
public class SettingsLoadingContextListener implements ServletContextListener {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(SettingsLoadingContextListener.class);

	/**
	 * Classpath to settings file.
	 */
	public static final String SETTINGS = "console-settings.xml";

	/**
	 * System property which holds a path to the location of the file.
	 */
	public static final String SYSTEM_PROP = "console-settings";

	/**
	 * Empty implementation.
	 * 
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// Do nothing
	}

	/**
	 * Initialize settings.
	 * 
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent contextEvent) {
		logger.info("Reading system property");
		String sysProp = System.getProperty(SYSTEM_PROP);
		if (logger.isInfoEnabled()) {
			logger.info("System property value: " + sysProp);
		}
		URL settingsUrl = null;
		if (!StringUtils.isEmpty(sysProp)) {
			File sysFile = new File(sysProp);
			if (sysFile.isFile()) {
				if (logger.isInfoEnabled()) {
					logger.info("Loading application settings from file: "
							+ sysFile);
				}
				settingsUrl = FileUtils.toUrl(sysFile);
			}
		}
		if (settingsUrl != null) {
			ApplicationSettings settings = ApplicationSettings
					.getInstance(settingsUrl);

		} else {
			logger.info("Loading application settings");
			ApplicationSettings settings = ApplicationSettings
					.getInstance(getClass().getClassLoader().getResource(
							SETTINGS));
		}
	}

}
