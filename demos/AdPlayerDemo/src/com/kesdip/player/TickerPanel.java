package com.kesdip.player;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

public class TickerPanel extends JPanel {
	private static final Logger logger = Logger.getLogger(TickerPanel.class);

	private static final long serialVersionUID = -6606550558755678181L;

	/* Initializing constructor controlled state */
	protected Color foregroundColor;
	protected double speed;
	protected TickerSource source;
	protected Font font;
	protected int width;
	protected int height;

	/* TRANSIENT RUN-TIME STATE */
	private AtomicBoolean positionInitialized = new AtomicBoolean(false);
	private double currentXPos;
	private int currentYPos;
	
	public TickerPanel(Font font, Color fgColor, double speed,
			TickerSource source, int width, int height) {
		this.font = font;
		this.foregroundColor = fgColor;
		this.setOpaque(false);
		this.speed = speed;
		this.source = source;
		this.width = width;
		this.height = height;
	}

	private String getTickerContent(Graphics g) {
		while (true) {
			String retVal = source.getCurrentContent();
			Rectangle2D r = g.getFontMetrics().getStringBounds(retVal, g);
			if (r.getWidth() < currentXPos + width)
				source.addTrailingChar();
			else
				return retVal;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (logger.isDebugEnabled())
			logger.debug("TickerPanel.paintComponent() called");
		
		super.paintComponent(g);
		
		// Initialize once
		if (!positionInitialized.get()) {
			currentXPos = 0;
			currentYPos = (height + g.getFontMetrics().getHeight()) / 2 -
				g.getFontMetrics().getDescent();
			positionInitialized.set(true);
		}
		
		// Now write the content
		g.setColor(foregroundColor);
		g.setFont(font);
		String content = getTickerContent(g);
		if (logger.isDebugEnabled())
			logger.debug("(" + ((int) currentXPos) + ", " + currentYPos +
					"): '" + content + "'");
		g.drawString(content, ((int) currentXPos), currentYPos);
		
		// Now move the ticker along
		currentXPos += (-1.0 * speed);
		if (logger.isDebugEnabled())
			logger.debug("New currentXPos: " + currentXPos);
		int trail = 0 - (int) currentXPos;
		if (trail >= g.getFontMetrics().charWidth(content.charAt(0))) {
			source.dropLeadingChar();
			currentXPos = 0;
		}
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void reset() {
		positionInitialized.set(false);
		source.reset();
	}
}
