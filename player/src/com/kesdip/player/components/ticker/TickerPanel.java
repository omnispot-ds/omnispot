/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components.ticker;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

/**
 * A panel that displays a ticker. It is associated with a ticker source. The
 * rest of the properties have to do with the look and feel of the ticker (font,
 * size, dimensions, color, speed, etc.).
 * 
 * @author Pafsanias Ftakas
 */
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
	private String oldContent;
	private double oldXPos;
	private int oldYPos;
	private long oldTotalElapsedTime;
	
	public TickerPanel(Font font, Color fgColor, double speed,
			TickerSource source, int width, int height) {
		super(true);
		this.font = font;
		this.foregroundColor = fgColor;
		this.speed = speed;
		this.source = source;
		this.width = width;
		this.height = height;
		this.oldTotalElapsedTime = 0;
	}

	private String getTickerContent(Graphics g) {
		while (true) {
			String retVal = source.getCurrentContent();
			Rectangle2D r = g.getFontMetrics().getStringBounds(retVal, g);
			if (r.getWidth() < Math.abs(currentXPos) + width) {
				source.addTrailingChar();
			} else {
				return retVal;
			}
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		if (logger.isTraceEnabled())
			logger.trace("TickerPanel.paintComponent() called");
		
		super.paintComponent(g);
		
		Color originalColor = g.getColor();
		Font originalFont = g.getFont();
		
		g.setColor(foregroundColor);
		g.setFont(font);

		// Initialize once
		if (!positionInitialized.get()) {
			currentXPos = 0;
			currentYPos = (height + g.getFontMetrics().getHeight()) / 2 -
				g.getFontMetrics().getDescent();
			positionInitialized.set(true);
		} else {
			// Draw the fade out image in the old location.
			Graphics2D gFade = (Graphics2D) g.create();
			AlphaComposite newComposite = 
			    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 
			                               (float) 0.3);
			gFade.setComposite(newComposite);
			gFade.drawString(oldContent, ((int) oldXPos), oldYPos);
			gFade.dispose();
		}
		
		// Now write the content
		String content = getTickerContent(g);
		if (logger.isTraceEnabled())
			logger.trace("(" + ((int) Math.round(currentXPos)) + ", " + currentYPos +
					"): '" + content + "'");
		g.drawString(content, (int) Math.round(currentXPos), currentYPos);
		
		oldContent = content;
		oldXPos = currentXPos;
		oldYPos = currentYPos;
		
		// If we have moved far enough along the edge that the leading character is
		// no longer visible, drop the leading character.
		int firstCharWidth = g.getFontMetrics().charWidth(content.charAt(0));
		if (firstCharWidth < Math.abs(currentXPos)) {
			source.dropLeadingChar();
			currentXPos = currentXPos + ((double) firstCharWidth);
		}
		
		// Reset the original state
		g.setColor(originalColor);
		g.setFont(originalFont);
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
	
	public void reset() {
		positionInitialized.set(false);
		source.reset();
	}
	
	public void tickerStarting() {
		oldTotalElapsedTime = System.currentTimeMillis();
	}

	public void timingEvent() {
		long currentTime = System.currentTimeMillis();
		
		// Now move the ticker along
		// The inputs are:
		// sleep -> the number of milliseconds that the since we last painted the ticker.
		// speed -> the number of pixels per second that the ticker should move along.
		// The formula is:
		// -1.0 * (sleep / 1000.0) * speed;
		double sleep = currentTime - oldTotalElapsedTime;
		if (logger.isTraceEnabled())
			logger.trace("The time lapse since we last repainted the " +
					"ticker is: " + sleep + "ms");
		currentXPos += (-1.0 * (sleep / 1000.0) * speed);
		if (logger.isTraceEnabled())
			logger.trace("New currentXPos: " + currentXPos);
		oldTotalElapsedTime = currentTime;
		
		repaint();
	}
}
