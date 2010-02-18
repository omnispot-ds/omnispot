package com.kesdip.player.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import org.apache.log4j.Logger;

@SuppressWarnings("serial")
public class AnalogClockFingersPanel extends AnalogClockPanel {
	
	private static final Logger logger = Logger.getLogger(AnalogClockFingersPanel.class);
	
	private static final BasicStroke handStroke = new BasicStroke(1.0F);
	
	@Override
	protected void paintComponent(Graphics g) {
		paintTime((Graphics2D) g);
	}
	
	void paintTime(Graphics2D paramGraphics2D) {
		logger.trace("Painting time");
		paramGraphics2D.drawImage(clockPanel.baseImage, null, 0, 0);

		if (clockPanel.antialias) {
			paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (clockPanel.outerDiameter > 50) {
			// draw hours finger
			paramGraphics2D.setStroke(handStroke);

			double d1 = clockPanel.timeHours + clockPanel.timeMinutes / 60.0D;
			double d2 = clockPanel.timeMinutes + clockPanel.timeSeconds / 60.0D;

			paramGraphics2D.setPaint(Color.black);
			double d3 = clockPanel.outerDiameter / 4.5D;
			double d4 = d3 / 2.0D;

			double d5 = (d1 + 3.0D) / 12.0D * 2.0D * 3.141592653589793D;
			double d6 = d5 - 0.1D;
			double d7 = d5 + 0.1D;

			double d8 = d3 * Math.cos(d5);
			double d9 = d3 * Math.sin(d5);

			double d10 = d4 * Math.cos(d6);
			double d11 = d4 * Math.sin(d6);
			double d12 = d4 * Math.cos(d7);
			double d13 = d4 * Math.sin(d7);

			int[] arrayOfInt1 = { clockPanel.centerXY, clockPanel.centerXY - (int) d10,
					clockPanel.centerXY - (int) d8, clockPanel.centerXY - (int) d12 };
			int[] arrayOfInt2 = { clockPanel.centerXY, clockPanel.centerXY - (int) d11,
					clockPanel.centerXY - (int) d9, clockPanel.centerXY - (int) d13 };

			paramGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2,
					arrayOfInt1.length);

			// draw minutes finger
			paramGraphics2D.setPaint(Color.black);
			d3 = clockPanel.outerDiameter / 2.5D;
			d4 = d3 / 2.0D;

			d5 = (d2 + 15.0D) / 60.0D * 2.0D * 3.141592653589793D;
			d6 = d5 - 0.06D;
			d7 = d5 + 0.06D;

			d8 = d3 * Math.cos(d5);
			d9 = d3 * Math.sin(d5);

			d10 = d4 * Math.cos(d6);
			d11 = d4 * Math.sin(d6);
			d12 = d4 * Math.cos(d7);
			d13 = d4 * Math.sin(d7);

			arrayOfInt1 = new int[] { clockPanel.centerXY, clockPanel.centerXY - (int) d10,
					clockPanel.centerXY - (int) d8, clockPanel.centerXY - (int) d12 };
			arrayOfInt2 = new int[] { clockPanel.centerXY, clockPanel.centerXY - (int) d11,
					clockPanel.centerXY - (int) d9, clockPanel.centerXY - (int) d13 };

			paramGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2,
					arrayOfInt1.length);

			// show clock seconds finger
			if (clockPanel.showSecondHand) {
				d3 = clockPanel.outerDiameter / 2.5D;

				d4 = (clockPanel.timeSeconds + 15 + clockPanel.timeMillis / 1000.0D) / 60.0D * 2.0D * 3.141592653589793D;
				d5 = d3 * Math.cos(d4);
				d6 = d3 * Math.sin(d4);

				paramGraphics2D.setPaint(Color.blue);
				paramGraphics2D.drawLine(clockPanel.centerXY, clockPanel.centerXY,
						clockPanel.centerXY - (int) d5, clockPanel.centerXY - (int) d6);

				// finger as image
				// Image inputImage = new
				// ImageIcon("d:\\finger.jpg").getImage();
				//
				// AffineTransform at = new AffineTransform();
				// BufferedImage sourceBI = new
				// BufferedImage(inputImage.getWidth(null), inputImage
				// .getHeight(null), BufferedImage.TYPE_INT_ARGB);
				// Graphics2D g = (Graphics2D) sourceBI.getGraphics();
				// // g.drawImage(inputImage, 0, 0, null);
				//
				// // scale image
				// //at.scale(2.0, 2.0);
				//
				// // rotate
				// at.rotate(d4-90*Math.PI/180 , sourceBI.getWidth() ,
				// sourceBI.getHeight() );
				// BufferedImageOp bio;
				// bio = new AffineTransformOp(at,
				// AffineTransformOp.TYPE_BILINEAR);
				//
				// BufferedImage destinationBI = bio.filter(sourceBI, null);
				// paramGraphics2D.drawImage(destinationBI, clockPanel.centerXY -
				// (int)d5,clockPanel.centerXY - (int)d6, null);
			}
		}
	}
}
