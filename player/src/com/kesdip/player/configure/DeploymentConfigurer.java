/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 07 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.configure;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;

import com.kesdip.common.configure.ApplicationContextBeanSetter;
import com.kesdip.common.exception.FieldSetException;
import com.kesdip.common.util.StringUtils;
import com.kesdip.player.Player;

/**
 * A class to configure the currently playing deployment, based on input in the
 * standard in.
 * <p>
 * The class runs an endless loop, polling the standard in, as long as the
 * {@link Player} itself is running. The class reads lines of the form
 * <code>expression=value</code> from the standard in and updates the currently
 * playing deployment.
 * </p>
 * 
 * @author gerogias
 */
public class DeploymentConfigurer implements Runnable {

	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(DeploymentConfigurer.class);

	/**
	 * The wrapped player instance.
	 */
	private Player player = null;

	/**
	 * Constructor.
	 * 
	 * @param player
	 *            the player instance
	 */
	public DeploymentConfigurer(Player player) {
		this.player = player;
	}

	/**
	 * Endless loop reading from the standard in.
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(System.in,
					"UTF-8"));
			while (true) {
				String line = reader.readLine();
				// not a configuration line
				if (StringUtils.isEmpty(line)) {
					continue;
				}
				if (line.indexOf('=') == -1) {
					logger.warn("Ignoring line: " + line);
					continue;
				}
				ApplicationContext context = player.getDeploymentContext();
				if (context == null) {
					logger.warn("Deployment context not yet initialized");
					continue;
				}
				String[] parts = line.split("\\=");
				ApplicationContextBeanSetter setter = new ApplicationContextBeanSetter(
						context);
				try {
					if (!setter.setValue(parts[0], parts[1])) {
						logger.warn("Error setting expression: " + parts[0]);
					}
				} catch (FieldSetException fse) {
					logger.warn("Error setting expression: " + parts[0]);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
}
