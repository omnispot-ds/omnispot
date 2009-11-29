/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.kesdip.player.DeploymentSettings;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.helpers.PlayerUtils;

/**
 * An implementation of component that represents the full screen, when the
 * player is working in full screen mode. This class is not meant to be used
 * in the deployment descriptor for the player. It is instead automatically
 * created by the player infrastructure, when it is decided that the player
 * will work in full screen mode for a particular deployment.
 * 
 * @author Pafsanias Ftakas
 */
public class FullScreenComponent extends AbstractComponent
		implements ApplicationContextAware {
	private static final Logger logger =
		Logger.getLogger(FullScreenComponent.class);

	/* SPRING CONFIGURATION STATE */
	private ApplicationContext ctx;
	private DeploymentSettings settings;

	/* TRANSIENT STATE */
	private GraphicsDevice gd;
	private Frame frame;

	/**
	 * Helper method to set the system on full screen mode.
	 */
	private void setFullScreenMode() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		
		if (!gd.isFullScreenSupported())
			throw new UnsupportedOperationException("Default screen device does not " +
					"support full screen mode. Player will exit.");
		
		frame = new Frame(gd.getDefaultConfiguration());
		frame.setUndecorated(true);
		frame.setIgnoreRepaint(true);
		frame.setResizable(false);
		frame.addKeyListener(PlayerUtils.getExitKeyListener(player));
		frame.addMouseListener(PlayerUtils.getExitMouseListener(player));
		frame.setCursor(PlayerUtils.getNoCursor());
		frame.setBackground(Color.BLACK);
		
		gd.setFullScreenWindow(frame);
		if (gd.isDisplayChangeSupported())
			chooseDisplayMode(gd);
	}

	/**
	 * Helper method to find the display mode that matches the size and bit
	 * depth selected in the deployment description.
	 * @param device The device to query the display modes of.
	 * @return The matching display mode, or null if none was found.
	 */
	private DisplayMode getDisplayMode(GraphicsDevice device) {
        DisplayMode[] modes = device.getDisplayModes();
        for (int i = 0; i < modes.length; i++) {
            if (modes[i].getWidth() == settings.getWidth() &&
            	modes[i].getHeight() == settings.getHeight() &&
            	modes[i].getBitDepth() == settings.getBitDepth()) {
            	if (logger.isDebugEnabled()) {
	            	logger.debug("Choosing display (" + modes[i].getWidth() + ", " +
	            			modes[i].getHeight() + ") and " + modes[i].getBitDepth() +
	            			" bit depth.");
            	}
                return modes[i];
            }
        }
        return null;
    }
    
	/**
	 * Helper method to select the display mode that matches the settings.
	 * @param device The device to set the display mode on.
	 */
    private void chooseDisplayMode(GraphicsDevice device) {
        DisplayMode best = getDisplayMode(device);
        if (best != null) {
            device.setDisplayMode(best);
        }
    }
    
	@Override
	public void setApplicationContext(ApplicationContext ctx)
			throws BeansException {
		this.ctx = ctx;
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		settings = (DeploymentSettings) ctx.getBean("deploymentSettings");
		setPlayer(player);
		setFullScreenMode();
	}

	@Override
	public void add(Component component) throws ComponentException {
		if (frame == null)
			return;
		
		java.awt.Component windowComponent = component.getWindowComponent();
		if (windowComponent == null)
			return;
		
		frame.add(windowComponent);
	}
	
	@Override
	public java.awt.Component getWindowComponent() {
		return frame;
	}

	/**
	 * Stop working in full screen mode. Remove the single full screen frame
	 * from the graphics display.
	 */
	public void destroy() {
		gd.setFullScreenWindow(null);
	}

	@Override
	public void repaint() throws ComponentException {
		// Intentionally empty.
	}

}
