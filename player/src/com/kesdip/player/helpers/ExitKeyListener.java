/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.helpers;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

/**
 * A KeyListener implementation that exits the JVM when the escape key is
 * pressed.
 * 
 * @author Pafsanias Ftakas
 */
public class ExitKeyListener implements KeyListener {
	private static final Logger logger = Logger.getLogger(ExitKeyListener.class);

	@Override
	public void keyPressed(KeyEvent event) {
		// Intentionally left empty.
	}

	@Override
	public void keyReleased(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_ESCAPE) {
			System.exit(0);
		} else if (event.getKeyCode() == KeyEvent.VK_F10) {
			try {
				Robot robot = new Robot();
				BufferedImage screenShot = 
					robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
				ImageIO.write(screenShot, "JPG", new File("C:\\tmp\\screenShot.jpg"));
			} catch (Exception e) {
				logger.error("Unable to capture screen shot.", e);
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent event) {
		// Intentionally left empty.
	}
	
}
