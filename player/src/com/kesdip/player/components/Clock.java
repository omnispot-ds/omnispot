package com.kesdip.player.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;

/**
 * Clock component.
 * 
 * @author gerogias
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

	/**
	 * Default constructor.
	 */
	public Clock() {
		// do nothing
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
					panel.refresh();
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
		panel.setPrefX(200);
		panel.setPrefY(200);

		panel.setFaceDark(new Color(10, 20, 21));
		panel.setFaceLight(new Color(181, 196, 215));
		panel.setFaceText(new Color(240, 228, 230));

		this.keepOnLooping = true;

		// inherit component size and location
		handleSizes();

		panel.setClockTitle("");
		panel.setBackground(getBgColor());

		panel.setSizeStuff(width, height);

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
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));

		// panel.clockPanel.setLocation(x, y);
		if (backgroundColor != null) {
			panel.getClockPanel().setBackground(backgroundColor);
		} else {
			panel.getClockPanel().setOpaque(false);
		}
		panel.getClockPanel().setSize(new Dimension(width, height));
		panel.getClockPanel().setPreferredSize(new Dimension(width, height));

		// panel.fingersPanel.setLocation(x, y);
		if (backgroundColor != null) {
			panel.getFingersPanel().setBackground(backgroundColor);
		} else {
			panel.getFingersPanel().setOpaque(false);
		}
		panel.getFingersPanel().setSize(new Dimension(width, height));
		panel.getFingersPanel().setPreferredSize(new Dimension(width, height));
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
}
