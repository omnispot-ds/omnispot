package com.kesdip.player.components.clock;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.AbstractComponent;
import com.kesdip.player.components.Component;
import com.kesdip.player.components.ComponentException;

/**
 * Clock component.
 * 
 * @author gkorilas
 */
public class Clock extends AbstractComponent implements Runnable {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Clock.class);

	private ClockPanel panel = new ClockPanel();
	private int timeSeconds;
	private Color bgColor;
	private String image;

	private JPanel fingersPanel = null;
	private JPanel clockPanel;
	
	protected Font font;
	protected Color foregroundColor;
	
	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public void setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
	}

	private String dateFormat;

	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	private boolean digital = true;
	
	public boolean isDigital() {
		return digital;
	}

	public void setDigital(boolean digital) {
		this.digital = digital;
	}
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
		panel.setImage(new ImageIcon(image).getImage());
	}

	public Color getBgColor() {
		logger.debug("getbgcolor called");
		return bgColor;
	}

	public void setBgColor(Color bgColor) {
		logger.debug("Setbgcolor called");
		this.bgColor = bgColor;
	}

	private boolean keepOnLooping;
	private Thread localThread;

	public void setSize(String size) {
		int s = Integer.parseInt(size);
		panel.setSize(s, s);
	}

	private String size;

	private boolean clockPainted;

	public String getSize() {
		return size;
	}

	public void run() {
		logger.info("Clock thread started!");
		while (this.keepOnLooping) {
			try {
				GregorianCalendar localGregorianCalendar = new GregorianCalendar();
				int i = localGregorianCalendar.get(10);
				int j = localGregorianCalendar.get(12);
				int k = localGregorianCalendar.get(13);

				localGregorianCalendar = null;
				if (k != this.timeSeconds) {
					panel.setTimeHours(i);
					panel.setTimeMinutes(j);
					panel.setTimeSeconds(k);

					panel.setTimeMillis(0);
					refresh();
				}
				Thread.sleep(250L);
			} catch (Exception localException) {
				logger.error("Error in Clock loop", localException);
				this.keepOnLooping = false;
			}
		}
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("Clock component is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return panel;
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		logger.debug("clock init called!");
		clockPanel = isDigital()?new DigitalClockPanel():new AnalogClockFacePanel();
		if (isDigital()) {
			((DigitalClockPanel)clockPanel).setDateFormat(getDateFormat());
			((DigitalClockPanel)clockPanel).setFont(getFont());
			((DigitalClockPanel)clockPanel).setForegroundColor(getForegroundColor());
			
		}
		panel.add(clockPanel, 0);

		if (!isDigital()) {
			fingersPanel = new AnalogClockFingersPanel();
			fingersPanel.setOpaque(false);
			panel.add(fingersPanel, 1);
		}
		panel.setPrefX(200);
		panel.setPrefY(200);

		panel.setFaceDark(new Color(10, 20, 21));
		panel.setFaceLight(new Color(181, 196, 215));
		panel.setFaceText(new Color(240, 228, 230));

		// inherit component size and location
		handleSizes();

		panel.setClockTitle("");
		panel.setBackground(getBgColor());

		panel.setSizeStuff(width, height);
		
		this.keepOnLooping = true;
		parent.add(this);
		logger.info("Adding clock");
		localThread = new Thread(this);
		localThread.start();
	}

	private void handleSizes() {
		panel.setLocation(x, y);
		if (backgroundColor != null) {
			panel.setBackground(backgroundColor);
		} else {
			panel.setOpaque(false);
		}
		panel.setSize(width, height);
		panel.setPreferredSize(new Dimension(width, height));

		// panel.clockPanel.setLocation(x, y);
		if (backgroundColor != null) {
			clockPanel.setBackground(backgroundColor);
		} else {
			clockPanel.setOpaque(false);
		}
		clockPanel.setSize(new Dimension(width, height));
		clockPanel.setPreferredSize(new Dimension(width, height));

		if (!isDigital()) {
			//panel.getFingersPanel().setLocation(x, y);
			if (backgroundColor != null) {
				fingersPanel.setBackground(backgroundColor);
			} else {
				fingersPanel.setOpaque(false);
			}
			fingersPanel.setSize(new Dimension(width, height));
			fingersPanel.setPreferredSize(new Dimension(width, height));
		}
	}

	@Override
	public void repaint() throws ComponentException {
		if (!localThread.isAlive()) {
			localThread.start();
		}
	}

	@Override
	public void releaseResources() {
		keepOnLooping = false;
	}
	
	protected void refresh() {
		if (!clockPainted) {
			clockPanel.repaint();
			clockPainted = true;
		}
		if (!isDigital()) {
			panel.moveToFront(fingersPanel);
			fingersPanel.repaint();
		}
	}
}
