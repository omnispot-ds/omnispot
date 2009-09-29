/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.PlayerUtils;
import com.sun.jna.examples.WindowUtils;

/**
 * This component is a container that can either be rendered on a separate
 * JFrame (when z-order functionality in accelerated graphics objects is
 * required), or a JPanel when a more lightweight approach can be taken.
 * 
 * @author Pafsanias Ftakas
 */
public class RootContainer extends AbstractComponent {
	private static final Logger logger = Logger.getLogger(RootContainer.class);
	
	protected String name;
	protected List<Component> contents;
	protected boolean isTransparent;
	
	public void setName(String name) {
		this.name = name;
	}
	public void setContents(List<Component> contents) {
		this.contents = contents;
	}
	
	public void setIsTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
	}
	
	/* TRANSIENT STATE */
	protected Frame fullScreenFrame;
	protected JFrame frame;
	protected int lastLayer;

	public void createFullScreenResources() {
		// Intentionally left empty
	}

	public void createWindowedResources() {
		frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.addKeyListener(PlayerUtils.getExitKeyListener(player));
		frame.addMouseListener(PlayerUtils.getExitMouseListener(player));
		frame.setCursor(PlayerUtils.getNoCursor());
		
		frame.setLocation(x, y);
		frame.setPreferredSize(new Dimension(width, height));
		lastLayer = 0;
		
		if (isTransparent) {
			System.setProperty("sun.java2d.noddraw", "true");
			WindowUtils.setWindowTransparent(frame, true);
			frame.setIgnoreRepaint(true);
		} else {
			frame.getContentPane().setBackground(backgroundColor);
		}
	}
	
	public void destroyWindowedResources() {
		releaseResources();
		if (frame == null) {
			return; // Something went wrong during the initialization process...
		}
		
		frame.setVisible(false);
		frame.dispose();
		frame = null;
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		setPlayer(player);
		
		if (parent != null) {
			fullScreenFrame = (Frame) parent.getWindowComponent();
			for (Component component : contents) {
				component.init(this, timingMonitor, player);
			}
			return;
		}

		// We should be working in normal (non-full-screen) mode. Make sure
		// that is the case.
		if (frame == null) {
			throw new RuntimeException("Misconfiguration. Working in " +
					"non-full-screen mode, but initialized incorrectly.");
		}
		
		for (Component component : contents) {
			component.init(this, timingMonitor, player);
		}

		frame.pack();
		frame.setVisible(true);
		frame.requestFocus();
	}

	@Override
	public void add(Component component) throws ComponentException {
		java.awt.Component windowComponent = component.getWindowComponent();
		if (windowComponent == null) {
			return;
		}
		
		if (frame == null) {
			if (logger.isInfoEnabled()) {
				logger.info("Adding component at: (" + windowComponent.getX() +
					", " + windowComponent.getY() + "), size: (" +
					windowComponent.getWidth() + ", " + windowComponent.getHeight() + ")");
			}
			fullScreenFrame.add(windowComponent);
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Adding component at: (" + windowComponent.getX() +
					", " + windowComponent.getY() + "), size: (" +
					windowComponent.getWidth() + ", " + windowComponent.getHeight() +
					") at depth: " + lastLayer);
			}
			frame.getLayeredPane().add(windowComponent, new Integer(lastLayer++));
		}
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return frame;
	}

	@Override
	public void repaint() throws ComponentException {
		frame.repaint();
		for (Component component : contents) {
			component.repaint();
		}
	}

	@Override
	public CompletionStatus isComplete() {
		for (Component component : contents) {
			switch (component.isComplete()) {
			case COMPLETE:
				return CompletionStatus.COMPLETE;
			case INCOMPLETE:
				return CompletionStatus.INCOMPLETE;
			case DONT_CARE:
				// Do nothing
				break;
			default:
				throw new RuntimeException("Unexpected completion " +
						"state: " + component.isComplete());
			}
		}
		
		return CompletionStatus.DONT_CARE;
	}

	@Override
	public void releaseResources() {
		for (Component component : contents) {
			component.releaseResources();
		}
	}
	
	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		for (Component component : contents) {
			retVal.addAll(component.gatherResources());
		}
		return retVal;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
