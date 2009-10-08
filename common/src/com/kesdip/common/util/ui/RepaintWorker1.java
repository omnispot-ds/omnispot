/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 08 Οκτ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.common.util.ui;

import java.awt.Component;

import javax.swing.SwingUtilities;

/**
 * A simple class to repaint a component in a thread-like context. Useful when
 * postponing repaints using {@link SwingUtilities}.
 * 
 * @author gerogias
 */
public class RepaintWorker1 implements Runnable {

	/**
	 * The component.
	 */
	private Component component = null;

	/**
	 * Constructor.
	 * 
	 * @param component
	 *            the component to handle
	 */
	public RepaintWorker1(Component c) {
		this.component = c;
	}

	/**
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		component.requestFocus();
		component.repaint();
	}

}
