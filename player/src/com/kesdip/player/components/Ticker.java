/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import com.kesdip.player.components.ticker.TickerPanel;
import com.kesdip.player.components.ticker.TickerSource;

/**
 * Represents a component that renders a ticker.
 * 
 * @author Pafsanias Ftakas
 */
public class Ticker extends AbstractComponent {
	protected TickerSource source;
	protected Font font;
	protected Color foregroundColor;
	protected double speed;
	
	public void setSource(TickerSource source) {
		this.source = source;
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
	public void init(Component parent) throws ComponentException {
		panel = new TickerPanel(font, foregroundColor, speed, source, width, height);
		panel.setLocation(x, y);
		if (backgroundColor != null)
			panel.setBackground(backgroundColor);
		else
			panel.setOpaque(false);
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		parent.add(this);
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
		panel.repaint();
	}

}
