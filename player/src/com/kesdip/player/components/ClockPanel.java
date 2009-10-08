package com.kesdip.player.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.kesdip.common.util.ui.RepaintWorker;

@SuppressWarnings("serial")
public class ClockPanel extends JLayeredPane {

	private static final Logger logger = Logger.getLogger(Clock.class);

	private int timeHours;
	private int timeMinutes;
	private int timeSeconds;
	private int timeMillis;
	private int prefX;
	private int prefY;
	private Color faceDark;
	private Color faceLight;
	private Color faceText;
	private BufferedImage baseImage;
	private String clockTitle;
	private boolean showSecondHand = true;
	private boolean antialias = true;
	// private Color bgColor;
	private boolean clockPainted = false;

	private int outerDiameter;
	private int centerXY;
	private static final BasicStroke handStroke = new BasicStroke(1.0F);
	private Image image;

	private JPanel clockPanel = new JPanel() {

		@Override
		protected void paintComponent(Graphics g) {
			paintClock((Graphics2D) g);
		}

	};
	private JPanel fingersPanel = new JPanel() {

		@Override
		protected void paintComponent(Graphics g) {
			paintTime((Graphics2D) g);
		}

	};

	/**
	 * Clock face repaint class.
	 */
	private RepaintWorker clockWorker = null;

	/**
	 * Clock fingers repaint class.
	 */
	private RepaintWorker fingersWorker = null;

	public ClockPanel() {
		logger.debug("ClockPanel Constructor called!");
		fingersPanel.setOpaque(false);
		add(clockPanel, 0);
		add(fingersPanel, 1);
		clockWorker = new RepaintWorker(clockPanel);
		fingersWorker = new RepaintWorker(fingersPanel);
	}

	public Dimension getPreferredSize() {
		return new Dimension(this.prefX, this.prefY);
	}

	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	public Dimension getMaximumSize() {
		return new Dimension(1000, 1000);
	}

	public void setSize(Dimension paramDimension) {
		setSize(paramDimension.width, paramDimension.height);
	}

	public void setSize(int paramInt1, int paramInt2) {
		super.setSize(paramInt1, paramInt2);
		int i = paramInt1;
		int j = paramInt2;

		i = (i > 1000) ? 1000 : i;
		i = (i < 100) ? 100 : i;
		j = (j > 1000) ? 1000 : j;
		j = (j < 100) ? 100 : j;

		if ((i != this.prefX) || (j != this.prefY)) {
			setSizeStuff(i, j);
		}
	}

	public void setSizeStuff(int paramInt1, int paramInt2) {
		int i = (paramInt2 > paramInt1) ? paramInt1 : paramInt2;
		outerDiameter = (i - 10);
		prefX = (this.prefY = i);
		if (outerDiameter < 10) {
			outerDiameter = 10;
		}
		centerXY = (i / 2);
	}

	protected void refresh() {
		// if (!clockPainted) {
		clockPanel.repaint();
		clockPainted = true;
		// }
		moveToFront(fingersPanel);
		fingersPanel.repaint();
	}

	void paintClock(Graphics2D paramGraphics2D) {
		logger.trace("Painting clock face");
		// antialias....
		if (this.outerDiameter > 50) {
			double d1, d2, d3, d4, d5, d6, d7;
			if (this.antialias) {
				paramGraphics2D.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
			} else {
				paramGraphics2D.setRenderingHint(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_OFF);
			}

			// draw clock outline
			int i = outerDiameter / 15;
			int k = outerDiameter / 2 - (i * 2);
			for (int j = 0; j < i; ++j) {
				if (j < i / 2) {
					k = 100 + 200 * j / i;
				} else {
					k = 100 + 200 * (i - j) / i;
				}

				Color localColor = new Color(k, k, k);
				paramGraphics2D.setColor(localColor);

				paramGraphics2D.fillOval(j + 5, j + 5, this.outerDiameter
						- (j * 2), this.outerDiameter - (j * 2));
			}

			// fill clock
			GradientPaint localGradientPaint = new GradientPaint(5.0F, 5.0F,
					this.faceDark, this.outerDiameter / 2,
					this.outerDiameter / 2, this.faceLight, true);
			paramGraphics2D.setPaint(localGradientPaint);
			paramGraphics2D.fillOval(5 + i, 5 + i,
					this.outerDiameter - (i * 2), this.outerDiameter - (i * 2));

			Ellipse2D ellipse = new Ellipse2D.Float();
			ellipse.setFrame(5 + i, 5 + i, this.outerDiameter - (i * 2),
					this.outerDiameter - (i * 2));
			paramGraphics2D.setClip(ellipse);

			// Draw background image
			if (image != null) {
				paramGraphics2D.drawImage(image, 0, 0, null);
			}

			// draw minute marks
			paramGraphics2D.setStroke(handStroke);
			paramGraphics2D.setPaint(Color.darkGray);

			int l = outerDiameter / 2 - i * 2;
			for (int i1 = 1; i1 <= 60; i1++) {
				d1 = l;
				d2 = l - i / 3;
				d3 = (double) i1 / 60.0D * 2.0D * 3.141592653589793D;

				d4 = d1 * Math.cos(d3);
				d5 = d1 * Math.sin(d3);

				d6 = d2 * Math.cos(d3);
				d7 = d2 * Math.sin(d3);

				paramGraphics2D.drawLine((int) (this.centerXY + d4),
						(int) (this.centerXY + d5), (int) (this.centerXY + d6),
						(int) (this.centerXY + d7));
				// draw 5-min markers
				if (i1 % 5 == 0) {
					paramGraphics2D.setStroke(new BasicStroke(2.0F));
					paramGraphics2D.drawLine((int) (this.centerXY + d4),
							(int) (this.centerXY + d5),
							(int) (this.centerXY + d6),
							(int) (this.centerXY + d7));
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
				paramGraphics2D.drawLine((int) (this.centerXY + d4),
						(int) (this.centerXY + d5), (int) (this.centerXY + d6),
						(int) (this.centerXY + d7));
			}

			// draw fingers base spot
			paramGraphics2D.setColor(Color.black);
			paramGraphics2D.fillOval(this.centerXY - (i / 3), this.centerXY
					- (i / 3), i * 2 / 3, i * 2 / 3);

			// set text 'clock title' for display
			if (clockTitle != null) {
				Font localFont = new Font("Optima", 1, i);
				paramGraphics2D.setFont(localFont);
				paramGraphics2D.setColor(this.faceDark);
				FontMetrics localFontMetrics = paramGraphics2D.getFontMetrics();
				int i1 = localFontMetrics.stringWidth(this.clockTitle);

				int i2 = i / 15;

				paramGraphics2D.drawString(this.clockTitle, this.centerXY
						- (i1 / 2) + i2, this.centerXY
						- (this.outerDiameter / 6) + i2);

				paramGraphics2D.setColor(this.faceText);
				paramGraphics2D.drawString(this.clockTitle, this.centerXY
						- (i1 / 2), this.centerXY - (this.outerDiameter / 6));
			}
		}
	}

	void paintTime(Graphics2D paramGraphics2D) {
		logger.trace("Painting time");
		paramGraphics2D.drawImage(this.baseImage, null, 0, 0);

		if (this.antialias) {
			paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
		} else {
			paramGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
		}

		if (this.outerDiameter > 50) {
			// draw hours finger
			paramGraphics2D.setStroke(handStroke);

			double d1 = this.timeHours + this.timeMinutes / 60.0D;
			double d2 = this.timeMinutes + this.timeSeconds / 60.0D;

			paramGraphics2D.setPaint(Color.black);
			double d3 = this.outerDiameter / 4.5D;
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

			int[] arrayOfInt1 = { this.centerXY, this.centerXY - (int) d10,
					this.centerXY - (int) d8, this.centerXY - (int) d12 };
			int[] arrayOfInt2 = { this.centerXY, this.centerXY - (int) d11,
					this.centerXY - (int) d9, this.centerXY - (int) d13 };

			paramGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2,
					arrayOfInt1.length);

			// draw minutes finger
			paramGraphics2D.setPaint(Color.black);
			d3 = this.outerDiameter / 2.5D;
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

			arrayOfInt1 = new int[] { this.centerXY, this.centerXY - (int) d10,
					this.centerXY - (int) d8, this.centerXY - (int) d12 };
			arrayOfInt2 = new int[] { this.centerXY, this.centerXY - (int) d11,
					this.centerXY - (int) d9, this.centerXY - (int) d13 };

			paramGraphics2D.fillPolygon(arrayOfInt1, arrayOfInt2,
					arrayOfInt1.length);

			// show clock seconds finger
			if (this.showSecondHand) {
				d3 = this.outerDiameter / 2.5D;

				d4 = (this.timeSeconds + 15 + this.timeMillis / 1000.0D) / 60.0D * 2.0D * 3.141592653589793D;
				d5 = d3 * Math.cos(d4);
				d6 = d3 * Math.sin(d4);

				paramGraphics2D.setPaint(Color.blue);
				paramGraphics2D.drawLine(this.centerXY, this.centerXY,
						this.centerXY - (int) d5, this.centerXY - (int) d6);

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
				// paramGraphics2D.drawImage(destinationBI, this.centerXY -
				// (int)d5,this.centerXY - (int)d6, null);
			}
		}
	}

	/**
	 * @return the timeHours
	 */
	public int getTimeHours() {
		return timeHours;
	}

	/**
	 * @param timeHours the timeHours to set
	 */
	public void setTimeHours(int timeHours) {
		this.timeHours = timeHours;
	}

	/**
	 * @return the timeMinutes
	 */
	public int getTimeMinutes() {
		return timeMinutes;
	}

	/**
	 * @param timeMinutes the timeMinutes to set
	 */
	public void setTimeMinutes(int timeMinutes) {
		this.timeMinutes = timeMinutes;
	}

	/**
	 * @return the timeSeconds
	 */
	public int getTimeSeconds() {
		return timeSeconds;
	}

	/**
	 * @param timeSeconds the timeSeconds to set
	 */
	public void setTimeSeconds(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	/**
	 * @return the timeMillis
	 */
	public int getTimeMillis() {
		return timeMillis;
	}

	/**
	 * @param timeMillis the timeMillis to set
	 */
	public void setTimeMillis(int timeMillis) {
		this.timeMillis = timeMillis;
	}

	/**
	 * @return the prefX
	 */
	public int getPrefX() {
		return prefX;
	}

	/**
	 * @param prefX the prefX to set
	 */
	public void setPrefX(int prefX) {
		this.prefX = prefX;
	}

	/**
	 * @return the prefY
	 */
	public int getPrefY() {
		return prefY;
	}

	/**
	 * @param prefY the prefY to set
	 */
	public void setPrefY(int prefY) {
		this.prefY = prefY;
	}

	/**
	 * @return the clockTitle
	 */
	public String getClockTitle() {
		return clockTitle;
	}

	/**
	 * @param clockTitle the clockTitle to set
	 */
	public void setClockTitle(String clockTitle) {
		this.clockTitle = clockTitle;
	}

	/**
	 * @return the showSecondHand
	 */
	public boolean isShowSecondHand() {
		return showSecondHand;
	}

	/**
	 * @param showSecondHand the showSecondHand to set
	 */
	public void setShowSecondHand(boolean showSecondHand) {
		this.showSecondHand = showSecondHand;
	}

	/**
	 * @return the image
	 */
	public Image getImage() {
		return image;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(Image image) {
		this.image = image;
	}

	/**
	 * @return the faceDark
	 */
	public Color getFaceDark() {
		return faceDark;
	}

	/**
	 * @param faceDark the faceDark to set
	 */
	public void setFaceDark(Color faceDark) {
		this.faceDark = faceDark;
	}

	/**
	 * @return the faceLight
	 */
	public Color getFaceLight() {
		return faceLight;
	}

	/**
	 * @param faceLight the faceLight to set
	 */
	public void setFaceLight(Color faceLight) {
		this.faceLight = faceLight;
	}

	/**
	 * @return the faceText
	 */
	public Color getFaceText() {
		return faceText;
	}

	/**
	 * @param faceText the faceText to set
	 */
	public void setFaceText(Color faceText) {
		this.faceText = faceText;
	}

	/**
	 * @return the clockPanel
	 */
	public JPanel getClockPanel() {
		return clockPanel;
	}

	/**
	 * @return the fingersPanel
	 */
	public JPanel getFingersPanel() {
		return fingersPanel;
	}

}
