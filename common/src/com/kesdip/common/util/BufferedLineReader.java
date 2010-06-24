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

import com.kesdip.common.util.process.ProcessOutputListener;

/**
 * A special class imitating the behavior of {@link BufferedReader}. It was
 * created because of contention problems with {@link Process} input streams.
 * Therefore, it is advised to use this class mainly for process streams. <br/>
 * Note: The stream is always assumed to be in "UTF-8" encoding.
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
	 * Default constructor.
	 * 
	 * @param in
	 *            the stream to read
	 * @throws IOException
	 *             on error
	 */
	public BufferedLineReader(InputStream in) throws IOException {
		this.reader = new InputStreamReader(in, "UTF-8");
		this.stringBuilder = new StringBuilder(BUFFER_SIZE);
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
}