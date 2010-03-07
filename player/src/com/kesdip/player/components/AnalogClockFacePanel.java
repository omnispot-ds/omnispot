package com.kesdip.player.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class AnalogClockFacePanel extends AnalogClockPanel {
	
	private static final Logger logger = Logger.getLogger(AnalogClockFacePanel.class);
			
	private static final BasicStroke handStroke = new BasicStroke(1.0F);		
			
	@Override
	protected void paintComponent(Graphics g) {
		paintClock((Graphics2D) g);
	}
	
	
	
	void paintClock(Graphics2D paramGraphics2D) {
		logger.trace("Painting clock face");
		// antialias....
		if (clockPanel.outerDiameter > 50) {
			double d1, d2, d3, d4, d5, d6, d7;
			if (clockPanel.antialias) {
				paramGraphics2D.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			} else {
				paramGraphics2D.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
			}

			// draw clock outline
			int i = clockPanel.outerDiameter / 15;
			int k = clockPanel.outerDiameter / 2 - (i * 2);
			for (int j = 0; j < i; ++j) {
				if (j < i / 2) {
					k = 100 + 200 * j / i;
				} else {
					k = 100 + 200 * (i - j) / i;
				}

				Color localColor = new Color(k, k, k);
				paramGraphics2D.setColor(localColor);

				paramGraphics2D.fillOval(j + 5, j + 5, clockPanel.outerDiameter
						- (j * 2), clockPanel.outerDiameter - (j * 2));
			}

			// fill clock
			GradientPaint localGradientPaint = new GradientPaint(5.0F, 5.0F,
					clockPanel.faceDark, clockPanel.outerDiameter / 2,
					clockPanel.outerDiameter / 2, clockPanel.faceLight, true);
			paramGraphics2D.setPaint(localGradientPaint);
			paramGraphics2D.fillOval(5 + i, 5 + i,
					clockPanel.outerDiameter - (i * 2), clockPanel.outerDiameter - (i * 2));

			Ellipse2D ellipse = new Ellipse2D.Float();
			ellipse.setFrame(5 + i, 5 + i, clockPanel.outerDiameter - (i * 2),
					clockPanel.outerDiameter - (i * 2));
			paramGraphics2D.setClip(ellipse);

			// Draw background image
			if (clockPanel.image != null) {
				paramGraphics2D.drawImage(clockPanel.image, 0, 0, null);
			}

			// draw minute marks
			paramGraphics2D.setStroke(handStroke);
			paramGraphics2D.setPaint(Color.darkGray);

			int l = clockPanel.outerDiameter / 2 - i * 2;
			for (int i1 = 1; i1 <= 60; i1++) {
				d1 = l;
				d2 = l - i / 3;
				d3 = (double) i1 / 60.0D * 2.0D * 3.141592653589793D;

				d4 = d1 * Math.cos(d3);
				d5 = d1 * Math.sin(d3);

				d6 = d2 * Math.cos(d3);
				d7 = d2 * Math.sin(d3);

				paramGraphics2D.drawLine((int) (clockPanel.centerXY + d4),
						(int) (clockPanel.centerXY + d5), (int) (clockPanel.centerXY + d6),
						(int) (clockPanel.centerXY + d7));
				// draw 5-min markers
				if (i1 % 5 == 0) {
					paramGraphics2D.setStroke(new BasicStroke(2.0F));
					paramGraphics2D.drawLine((int) (clockPanel.centerXY + d4),
							(int) (clockPanel.centerXY + d5),
							(int) (clockPanel.centerXY + d6),
							(int) (clockPanel.centerXY + d7));
					paramGraphics2D.setStroke(handStroke);
				}
			}

			// mark quarters
			paramGraphics2D.setStroke(new BasicStroke(3.0F));
			paramGraphics2D.setPaint(Color.black);
			for (int i1 = 1; i1 <= 4; ++i1) {
				d1 = l;
				d2 = l - (i / 2);
				d3 = (double) i1 / 4.0D * 2.0D * 3.141592653589793D;

				d4 = d1 * Math.cos(d3);
				d5 = d1 * Math.sin(d3);

				d6 = d2 * Math.cos(d3);
				d7 = d2 * Math.sin(d3);
				paramGraphics2D.drawLine((int) (clockPanel.centerXY + d4),
						(int) (clockPanel.centerXY + d5), (int) (clockPanel.centerXY + d6),
						(int) (clockPanel.centerXY + d7));
			}

			// draw fingers base spot
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.fillOval(clockPanel.centerXY - (i / 3), clockPanel.centerXY
					- (i / 3), i * 2 / 3, i * 2 / 3);

			// set text 'clock title' for display
			if (clockPanel.clockTitle != null) {
				Font localFont = new Font("Optima", 1, i);
				paramGraphics2D.setFont(localFont);
				paramGraphics2D.setColor(clockPanel.faceDark);
				FontMetrics localFontMetrics = paramGraphics2D.getFontMetrics();
				int i1 = localFontMetrics.stringWidth(clockPanel.clockTitle);

				int i2 = i / 15;

				paramGraphics2D.drawString(clockPanel.clockTitle, clockPanel.centerXY
						- (i1 / 2) + i2, clockPanel.centerXY
						- (clockPanel.outerDiameter / 6) + i2);

				paramGraphics2D.setColor(clockPanel.faceText);
				paramGraphics2D.drawString(clockPanel.clockTitle, clockPanel.centerXY
						- (i1 / 2), clockPanel.centerXY - (clockPanel.outerDiameter / 6));
			}
		}
	}
}
