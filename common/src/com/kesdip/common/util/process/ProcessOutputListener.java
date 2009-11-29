/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

/**
 * Listener for process line output. Implementations of the class can be
 * notified by {@link StreamLogger} for incoming lines of input.
 * 
 * @author gerogias
 */
public interface ProcessOutputListener {

	/**
	 * Whether the format of the line is supported by the listener or not.
	 * 
	 * @param line
	 *            the read line, w/o the trailing newline
	 * @return boolean <code>true</code> if the line seems valid for the
	 *         listener
	 */
	boolean canProcessLine(String line);

	/**
	 * Called by the {@link StreamLogger} to process a read line. The method is
	 * only called if {@link #canProcessLine(String)} returned <code>true</code>
	 * . It should be short in its processing as it is called inline.
	 * 
	 * @param line
	 *            the line to process
	 */
	void processLine(String line);
}
