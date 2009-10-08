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
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.kesdip.common.util.ui.RepaintWorker;

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

	/**
	 * The repaint utility class.
	 */
	private RepaintWorker repaintWorker = null;

	/**
	 * The panel's bounds, used for repainting.
	 */
	private Rectangle panelBounds = null;

	/**
	 * Width of 5 'm' letters, used as a comparison value.
	 */
	private int fiveEmWidth = Integer.MIN_VALUE;

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
		this.repaintWorker = new RepaintWorker(this);
		panelBounds = new Rectangle(getX(), getY(), width, height);
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
		if (logger.isTraceEnabled()) {
			logger.trace("TickerPanel.paintComponent() called");
		}

		super.paintComponent(g);

		// initialize emWidth
		if (fiveEmWidth == Integer.MIN_VALUE) {
			fiveEmWidth = -5 * g.getFontMetrics().charWidth('m');
		}

		Color originalColor = g.getColor();
		Font originalFont = g.getFont();

		g.setColor(foregroundColor);
		g.setFont(font);

		// Initialize once
		if (!positionInitialized.get()) {
			currentXPos = 0;
			currentYPos = (height + g.getFontMetrics().getHeight()) / 2
					- g.getFontMetrics().getDescent();
			positionInitialized.set(true);
			// sanity check for null
		} else if (oldContent != null) {
			// Draw the fade out image in the old location.
			Graphics2D gFade = (Graphics2D) g.create();
			AlphaComposite newComposite = AlphaComposite.getInstance(
					AlphaComposite.SRC_OVER, (float) 0.3);
			gFade.setComposite(newComposite);
			gFade.drawString(oldContent, ((int) oldXPos), oldYPos);
			gFade.dispose();
		}

		// Now write the content
		String content = getTickerContent(g);
		if (logger.isTraceEnabled()) {
			logger.trace("(" + ((int) Math.round(currentXPos)) + ", "
					+ currentYPos + "): '" + content + "'");
		}
		g.drawString(content, (int) Math.round(currentXPos), currentYPos);

		oldContent = content;
		oldXPos = currentXPos;
		oldYPos = currentYPos;

		// If we have moved far enough along the edge that the leading character
		// is
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

	/**
	 * Move the ticker along calculating the new text position and forcing a
	 * repaint. The inputs are:
	 * <ul>
	 * <li>sleep: the number of milliseconds that the since we last painted the
	 * ticker.
	 * </li>
	 * <li>speed: the number of pixels per second that the ticker should move
	 * along.
	 * </li>
	 * </ul>
	 * The formula is: -1.0 * (sleep / 1000.0) * speed;
	 */
	public void timingEvent() {
		long currentTime = System.currentTimeMillis();

		double sleep = currentTime - oldTotalElapsedTime;
		if (logger.isTraceEnabled()) {
			logger.trace("The time lapse since we last repainted the "
					+ "ticker is: " + sleep + "ms");
		}
		currentXPos += (-1.0 * (sleep / 1000.0) * speed);
		// sanity check: if currentXPos is larger than 5 em, set it to zero
		// m is the widest letter, so a value larger than this means that
		// something is wrong
		if (currentXPos <= fiveEmWidth) {
			currentXPos = 0;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("New currentXPos: " + currentXPos);
		}
		oldTotalElapsedTime = currentTime;
		SwingUtilities.invokeLater(repaintWorker);
	}
}
