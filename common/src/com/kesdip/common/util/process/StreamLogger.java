/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

import java.io.InputStream;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.kesdip.common.util.BufferedLineReadListener;
import com.kesdip.common.util.BufferedLineReader;

/**
 * Utility class to redirect incoming lines to a logger.
 * <p>
 * The class can work both a listener in the context of another thread or be
 * used as a Thread. In the latter case, it creates a {@link BufferedLineReader}
 * inside an endless loop adding itself as a listener. Before using it as a
 * thread, the {@link StreamLogger#setInputStream(InputStream)} and
 * {@link #setEncoding(String)} (default "UTF-8") must be called.
 * </p>
 * <p>
 * Based on code found <a
 * href="http://beradrian.wordpress.com/2008/01/30/jmplayer/">here</a>.
 * </p>
 * 
 * @author gerogias
 */
public class StreamLogger extends Thread implements BufferedLineReadListener {

	/**
	 * Default logger to use if no other is defined.
	 */
	private static final Logger defaultLogger = Logger
			.getLogger(StreamLogger.class);

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
	 * Only when the class is used as a thread.
	 */
	private InputStream inputStream;

	/**
	 * The encoding for the stream.
	 */
	private String encoding = "UTF-8";

	/**
	 * Default constructor is private.
	 */
	private StreamLogger() {
		// do nothing
	}

	/**
	 * Logs the output to the given logger with the specified level.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param logger
	 *            the logger
	 * @param level
	 *            the level to use
	 */
	public StreamLogger(String name, Logger logger, Level level) {
		this.name = name;
		this.logger = logger;
		this.logLevel = level;
	}

	/**
	 * Logs the output to the given logger with a log level of
	 * {@link Level#INFO}.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param logger
	 *            the logger
	 */
	public StreamLogger(String name, Logger logger) {
		this.name = name;
		this.logger = logger;
		this.logLevel = Level.INFO;
	}

	/**
	 * Logs the output to the default logger with the given log level.
	 * 
	 * @param name
	 *            the name of the logger
	 * @param logger
	 *            the logger
	 */
	public StreamLogger(String name, Level level) {
		this.name = name;
		this.logger = defaultLogger;
		this.logLevel = level;
	}

	/**
	 * Logs the output to the default logger with a log level of
	 * {@link Level#INFO}.
	 * 
	 * @param name
	 *            the name of the logger
	 */
	public StreamLogger(String name) {
		this.name = name;
		this.logger = defaultLogger;
		this.logLevel = Level.INFO;
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#canProcessLine(java.lang.String)
	 */
	@Override
	public boolean canProcessLine(String line) {
		return logger.isEnabledFor(logLevel);
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#processLine(java.lang.String)
	 */
	@Override
	public void processLine(String line) {
		logger.log(logLevel, name + ": " + line);
	}

	/**
	 * @return the inputStream
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * @param inputStream
	 *            the inputStream to set
	 */
	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            the encoding to set
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * Stream copying pump.
	 * 
	 * @see java.lang.Thread#run()
	 * @throws IllegalStateException
	 *             if the inputstream is not set
	 */
	public void run() throws IllegalStateException {

		BufferedLineReader reader = null;
		try {
			reader = new BufferedLineReader(inputStream, encoding);
			reader.addListener(this);
			while (true) {
				reader.read();
			}
		} catch (Exception ioe) {
//			defaultLogger.error("Error reading line", ioe);
			try {
				reader.close();
			} catch (Exception e) {
				// do nothing
			}
		}
	}

}
