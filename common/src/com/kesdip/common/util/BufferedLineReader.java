/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 27 Μαϊ 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * A special class imitating the behavior of {@link BufferedReader}.
 * <p>
 * It was created because of contention problems with {@link Process} input
 * streams. Therefore, it is advised to use this class mainly for process
 * streams.
 * </p>
 * <p>
 * The class defines a {@link #read()} method. This is a blocking method,
 * supposed to be called inside an endless loop, outside of the main processing
 * thread. If there is no available input the method forces its parent thread to
 * sleep for {@link #THREAD_CHECK_INTERVAL} millis. The executing endless loop
 * should contain minimal processing other than calling this method, to avoid
 * causing contention problems.
 * </p>
 * 
 * <br/>
 * The class is not thread-safe. It is the responsibility of the calling code to
 * close the stream.
 * 
 * @author gerogias
 * @see Process#getInputStream()
 */
public final class BufferedLineReader {

	/**
	 * Size of the byte buffer to read.
	 */
	private final static int BUFFER_SIZE = 512;
	/**
	 * Interval between consecutive checks.
	 */
	private final static int THREAD_CHECK_INTERVAL = 100;

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(BufferedLineReader.class);

	/**
	 * The stream.
	 */
	private InputStreamReader reader;

	/**
	 * The string buffer.
	 */
	private StringBuilder stringBuilder;

	/**
	 * Listeners for read lines.
	 */
	private List<BufferedLineReadListener> listeners = new ArrayList<BufferedLineReadListener>();
	/**
	 * Character buffer for reading the stream.
	 */
	private char[] cb;

	/**
	 * Constructor. The stream is assumed to be in "UTF-8" encoding
	 * 
	 * @param in
	 *            the stream to read
	 * @throws IOException
	 *             on error
	 */
	public BufferedLineReader(InputStream in) throws IOException {
		this.reader = new InputStreamReader(in, "UTF-8");
		this.stringBuilder = new StringBuilder(BUFFER_SIZE);
		cb = new char[BUFFER_SIZE];
	}

	/**
	 * Constructor.
	 * 
	 * @param in
	 *            the stream to read
	 * @param encoding
	 *            the stream's encoding
	 * @throws IOException
	 *             on error
	 */
	public BufferedLineReader(InputStream in, String encoding)
			throws IOException {
		this.reader = new InputStreamReader(in, encoding);
		this.stringBuilder = new StringBuilder(BUFFER_SIZE);
		cb = new char[BUFFER_SIZE];
	}

	/**
	 * Main processing method. Read as many characters as possible. If a line
	 * end is detected, notify the listeners.
	 * 
	 * @throws Exception
	 *             on error
	 */
	public void read() throws Exception {

		String line;
		while (!reader.ready()) {
			Thread.sleep(THREAD_CHECK_INTERVAL);
		}

		int n = reader.read(cb, 0, BUFFER_SIZE);
		int nlIdx = -1;
		int nextToBeConsumed = 0;

		// check if our newly received input has new lines in it and process
		// them.
		for (int i = 0; i < n; i++) {
			if (cb[i] == '\n') {

				// remove CR if it exists
				if (i > 0 && cb[i - 1] == '\r') {
					nlIdx = i - 1;
				} else {
					nlIdx = i;
				}

				// assemble and process the line
				stringBuilder.append(cb, nextToBeConsumed, nlIdx
						- nextToBeConsumed);
				line = stringBuilder.toString();

				// logger.trace("consuming line |" + line + "|");
				notifyListeners(line);

				// empty the buffer
				stringBuilder.setLength(0);

				// remember where we are in processing the buffer
				nextToBeConsumed = i + 1;
			}
		}

		// append the remainder of the buffer to the string builder
		if (nextToBeConsumed < n) {
			stringBuilder.append(cb, nextToBeConsumed, n - nextToBeConsumed);
		}

	}

	/**
	 * Calls all listeners to process the read line, if matching.
	 * 
	 * @param line
	 *            the read line
	 */
	private final void notifyListeners(String line) {
		for (BufferedLineReadListener listener : listeners) {
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
	public void addListener(BufferedLineReadListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            to remove
	 */
	public void removeListener(BufferedLineReadListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Close the wrapped reader.
	 */
	public void close() {
		try {
			reader.close();
		} catch (Exception e) {
			// do nothing
		}
	}
}