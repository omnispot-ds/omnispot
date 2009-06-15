package com.kesdip.player.components;

import java.awt.Color;
import java.awt.Dimension;
import java.util.GregorianCalendar;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;

public class Clock extends AbstractComponent implements Runnable {
	private static final Logger logger = Logger.getLogger(Clock.class);
	
	private ClockPanel panel = new ClockPanel();
	private int timeSeconds;
	private Color bgColor;
	private String image;
	
	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
		panel.image = new ImageIcon(image).getImage();
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
	private Thread localThread ;

	

	public void setSize(String size){
		int s = Integer.parseInt(size);
		panel.setSize(s,s);
	}
	private String size;

	public String getSize() {
		return size;
	}
	
	public void run()
	{
		logger.info("Clock thread started!");
		while (this.keepOnLooping)
		{
			try
			{
				GregorianCalendar localGregorianCalendar = new GregorianCalendar();
				int i = localGregorianCalendar.get(10);
				int j = localGregorianCalendar.get(12);
				int k = localGregorianCalendar.get(13);

				localGregorianCalendar = null;
				if (k != this.timeSeconds) {
					panel.timeHours = i;
					panel.timeMinutes = j;
					panel.timeSeconds = k;

					panel.timeMillis = 0;
					panel.refresh();
				}
				Thread.sleep(250L);
			}
			catch (Exception localException)
			{
				localException.printStackTrace();
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
	public void init(Component parent, TimingMonitor timingMonitor, Player player)
	throws ComponentException {
		logger.debug("clock init called!");
		panel.prefX = 200;
		panel.prefY = 200;

		panel.faceDark = new Color(10, 20, 21);
		panel.faceLight = new Color(181, 196, 215);
		panel.faceText = new Color(240, 228, 230);

		this.keepOnLooping = true;

		//inherit component size and location
		handleSizes();
		
		panel.clockTitle = "";
		panel.setBackground(getBgColor());

		panel.setSizeStuff(width, height);
		
		parent.add(this);
		logger.info("Adding clock");
		localThread = new Thread(this);
		localThread.start();

	}

	private void handleSizes() {
		panel.setLocation(x, y);
		if (backgroundColor != null)
			panel.setBackground(backgroundColor);
		else
			panel.setOpaque(false);
		panel.setSize(new Dimension(width, height));
		panel.setPreferredSize(new Dimension(width, height));
		
		//panel.clockPanel.setLocation(x, y);
		if (backgroundColor != null)
			panel.clockPanel.setBackground(backgroundColor);
		else
			panel.clockPanel.setOpaque(false);
		panel.clockPanel.setSize(new Dimension(width, height));
		panel.clockPanel.setPreferredSize(new Dimension(width, height));
		
		//panel.fingersPanel.setLocation(x, y);
		if (backgroundColor != null)
			panel.fingersPanel.setBackground(backgroundColor);
		else
			panel.fingersPanel.setOpaque(false);
		panel.fingersPanel.setSize(new Dimension(width, height));
		panel.fingersPanel.setPreferredSize(new Dimension(width, height));
	}

	@Override
	public void repaint() throws ComponentException {
		if (!localThread.isAlive())
			localThread.start();
	}
	
	@Override
	public void releaseResources() {
		keepOnLooping = false;
	}
}
