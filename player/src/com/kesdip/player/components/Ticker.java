/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.ticker.TickerPanel;
import com.kesdip.player.components.ticker.TickerSource;
import com.kesdip.player.components.ticker.TickerThread;

/**
 * Represents a component that renders a ticker.
 * 
 * @author Pafsanias Ftakas
 */
public class Ticker extends AbstractComponent {
	private static final Logger logger = Logger.getLogger(Ticker.class);
	
	protected TickerSource tickerSource;
	protected Font font;
	protected Color foregroundColor;
	protected double speed;
	protected TickerThread tickerThread;
	
	public void setTickerSource(TickerSource tickerSource) {
		this.tickerSource = tickerSource;
	}
	
	public void setFont(Font font) {
		this.font = font;
	}
	
	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}
	
	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	/* TRANSIENT STATE */
	private TickerPanel panel;

	@Override
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
			throws ComponentException {
		setPlayer(player);
		
		panel = new TickerPanel(player, font, foregroundColor, speed, tickerSource, width, height);
		panel.setLocation(x, y);
		if (backgroundColor != null)
			panel.setBackground(backgroundColor);
		else
			panel.setOpaque(false);
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		logger.info("About to add ticker at: (" + x + "," + y +
				") with size: (" + width + "," + height + ")");
		parent.add(this);
		
		tickerThread = new TickerThread(panel, player.getSleepInterval());
	}
	
	@Override
	public void releaseResources() {
		try {
			tickerThread.stopRunning();
		} catch (Exception e) {
			logger.error("Error releasing resources", e);
		}
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("Ticker component is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return panel;
	}

	@Override
	public void repaint() throws ComponentException {
		// TickerThread handles the repaint to give a smoother L&F.
		if (!tickerThread.isAlive())
			tickerThread.start();
	}

}
