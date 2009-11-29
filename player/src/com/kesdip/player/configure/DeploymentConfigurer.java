/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 07 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.configure;

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
	
	private final static int BUFFER_SIZE = 512;
	private final static int THREAD_CHECK_INTERVAL = 100;

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
		InputStreamReader isr = null;
		
		char[] cb = new char[BUFFER_SIZE];
		StringBuilder s = new StringBuilder(BUFFER_SIZE);
		String line = null;
		
		try {
			isr = new InputStreamReader(System.in, "UTF-8");
			while (true) {
				
				// wait until there is some input
				while (!isr.ready()) {
					Thread.sleep(THREAD_CHECK_INTERVAL);
				}
				
				int n = isr.read(cb, 0, BUFFER_SIZE);
				int nlIdx = -1;
				int nextToBeConsumed = 0;
				
				// check if our newly received input has new lines in it and process them. 
				for (int i = 0 ; i < n ; i++) {
					if (cb[i] == '\n') {
						
						// remove CR if it exists
						if (i > 0 && cb[i-1] == '\r') {
							nlIdx = i - 1;
						} else {
							nlIdx = i;
						}
						
						// assemble and process the line
						s.append(cb, nextToBeConsumed, nlIdx - nextToBeConsumed);
						line = s.toString();
						
						//logger.trace("consuming line |" + line + "|");
						processLine(line);
						
						// empty the buffer
						s.setLength(0);
						
						// remember where we are in processing the buffer
						nextToBeConsumed = i + 1;
					} 
				}
				
				// append the remainder of the buffer to the string builder
				if (nextToBeConsumed < n)
					s.append(cb, nextToBeConsumed, n - nextToBeConsumed);
				
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				isr.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}
	
	private void processLine(String line) {
		// not a configuration line
		if (StringUtils.isEmpty(line)) {
			return;
		}
		if (line.indexOf('=') == -1) {
			logger.warn("Ignoring line: " + line);
			return;
		}
		ApplicationContext context = player.getDeploymentContext();
		if (context == null) {
			logger.warn("Deployment context not yet initialized");
			return;
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
	
}
