/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

import java.util.EventListener;

/**
 * Listener for {@link ProcessExitDetector} events.
 * 
 * @author gerogias
 */
public interface ProcessExitListener extends EventListener {

	/**
	 * Notified that a process has finished.
	 * 
	 * @param process
	 *            the finished instance
	 * @param userObject
	 *            an object passed to {@link ProcessExitDetector} to help
	 *            identify the process; may be <code>null</code>
	 */
	void processFinished(Process process, Object userObject);
}
