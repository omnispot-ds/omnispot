/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Utility class to redirect a stream's out to a logger. The stream is assumed
 * to contain lines of output. The class is mainly intended to capture Process
 * output.
 * <p>
 * Based on code found <a
 * href="http://beradrian.wordpress.com/2008/01/30/jmplayer/">here</a>.
 * </p>
 * 
 * @author gerogias
 */
public class StreamLogger extends Thread {

	/**
	 * Default logger to use if no other is defined.
	 */
	private static final Logger defaultLogger = Logger
			.getLogger(StreamLogger.class);

	/**
	 * The output of the process.
	 */
	private BufferedReader input = null;

	/**
	 * The logger to use, external or default.
	 */
	private Logger logger = null;

	/**
	 * The log level to use.
	 */
	private Level logLevel = null;

	/**
	 * The name of the logger.
	 */
	private String name = null;

	/**
	 * Listeners for read lines.
	 */
	private List<ProcessOutputListener> listeners = new ArrayList<ProcessOutputListener>();

	/**
	 * Default constructor is private.
	 */
	private StreamLogger() {
		// do nothing
	}

	/**
	 * Logs the output of the given stream to the given logger with the
	 * specified level.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param inputStream
	 *            the input
	 * @param logger
	 *            the logger
	 * @param level
	 *            the level to use
	 */
	public StreamLogger(String name, InputStream inputStream, Logger logger,
			Level level) {
		this.name = name;
		// buffer is large enough to prevent parent thread from blocking 
		input = new BufferedReader(new InputStreamReader(inputStream), 256 * 1024);
		this.logger = logger;
		this.logLevel = level;
	}

	/**
	 * Logs the output of the given stream to the given logger with a log level
	 * of {@link Level#INFO}.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param inputStream
	 *            the stream
	 * @param logger
	 *            the logger
	 */
	public StreamLogger(String name, InputStream inputStream, Logger logger) {
		this.name = name;
		input = new BufferedReader(new InputStreamReader(inputStream));
		this.logger = logger;
		this.logLevel = Level.INFO;
	}

	/**
	 * Logs the output of the given stream to the default logger with the given
	 * log level.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param inputStream
	 *            the stream
	 * @param logger
	 *            the logger
	 */
	public StreamLogger(String name, InputStream inputStream, Level level) {
		this.name = name;
		input = new BufferedReader(new InputStreamReader(inputStream));
		this.logger = defaultLogger;
		this.logLevel = level;
	}

	/**
	 * Logs the output of the given stream to the default logger with a log
	 * level of {@link Level#INFO}.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param inputStream
	 *            the stream
	 */
	public StreamLogger(String name, InputStream inputStream) {
		this.name = name;
		input = new BufferedReader(new InputStreamReader(inputStream));
		this.logger = defaultLogger;
		this.logLevel = Level.INFO;
	}

	/**
	 * Stream copying pump.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			String line = null;
			// read line by line
			while ((line = input.readLine()) != null) {
				if (logger.isEnabledFor(logLevel)) {
					logger.log(logLevel, name + ": " + line);
				}
				if (!listeners.isEmpty()) {
					notifyListeners(line);
				}
			}
		} catch (IOException ioe) {
			defaultLogger.error("Error reading line", ioe);
		}
	}

	/**
	 * Calls all listeners to process the read line, if matching.
	 * 
	 * @param line
	 *            the read line
	 */
	private final void notifyListeners(String line) {
		for (ProcessOutputListener listener : listeners) {
			if (listener.canProcessLine(line)) {
				try {
					listener.processLine(line);
				} catch (Exception e) {
					logger.error("Error processing line", e);
				}
			}
		}
	}

	/**
	 * @param listener
	 *            to add
	 */
	public void addListener(ProcessOutputListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            to remove
	 */
	public void removeListener(ProcessOutputListener listener) {
		listeners.remove(listener);
	}
}
