/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.helpers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * A KeyListener implementation that exits the JVM when the escape key is
 * pressed.
 * 
 * @author Pafsanias Ftakas
 */
public class ExitKeyListener implements KeyListener {

	@Override
	public void keyPressed(KeyEvent event) {
		// Intentionally left empty.
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// Intentionally left empty.
	}
	
}
