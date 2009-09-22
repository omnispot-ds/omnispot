/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.process;

import java.util.ArrayList;
import java.util.List;

/**
 * Detects when a process is finished and invokes the associated listeners.
 * <p>
 * Based on code found <a href=
 * "http://beradrian.wordpress.com/2008/11/03/detecting-process-exit-in-java/"
 * >here</a>.
 * </p>
 * 
 * @see ProcessExitListener
 * @author gerogias
 */
public class ProcessExitDetector extends Thread {

	/**
	 * The process for which we have to detect the end.
	 */
	private Process process;

	/**
	 * The user object to identify the process.
	 */
	private Object userObject = null;
	
	/**
	 * The associated listeners to be invoked at the end of the process.
	 */
	private List<ProcessExitListener> listeners = new ArrayList<ProcessExitListener>();

	/**
	 * Starts the detection for the given process
	 * 
	 * @param process
	 *            the process for which we have to detect when it is finished
	 * @param userObject
	 *            an optional user object to help identify the process
	 */
	public ProcessExitDetector(Process process, Object userObject) {
		try {
			// test if the process is finished
			process.exitValue();
			throw new IllegalArgumentException("The process is already ended");
		} catch (IllegalThreadStateException exc) {
			this.process = process;
			this.userObject = userObject;
		}
	}

	/** @return the process that it is watched by this detector. */
	public Process getProcess() {
		return process;
	}

	
	
	/**
	 * Processing logic.
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		try {
			// wait for the process to finish
			process.waitFor();
			// invokes the listeners
			for (ProcessExitListener listener : listeners) {
				listener.processFinished(process, userObject);
			}
		} catch (InterruptedException e) {
			// do nothing
		}
	}

	/**
	 * Adds a process listener.
	 * 
	 * @param listener
	 *            the listener to be added
	 */
	public void addProcessListener(ProcessExitListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a process listener.
	 * 
	 * @param listener
	 *            the listener to be removed
	 */
	public void removeProcessListener(ProcessExitListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the userObject
	 */
	public Object getUserObject() {
		return userObject;
	}

	/**
	 * @param userObject the userObject to set
	 */
	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}
}
