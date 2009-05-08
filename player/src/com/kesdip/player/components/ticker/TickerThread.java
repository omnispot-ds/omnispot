package com.kesdip.player.components.ticker;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

public class TickerThread extends Thread {
	private static final Logger logger = Logger.getLogger(TickerThread.class);
	private TickerPanel tickerPanel;
	private AtomicBoolean stillRunning;
	private long lastRunTime;
	private long sleepInterval;

	public TickerThread(TickerPanel tickerPanel, int sleepInterval) {
		super("TickerThread");
		this.tickerPanel = tickerPanel;
		this.stillRunning = new AtomicBoolean(true);
		this.lastRunTime = 0;
		this.sleepInterval = sleepInterval;
	}
	
	public void stopRunning() {
		stillRunning.set(false);
	}
	
	@Override
	public void run() {
		while (stillRunning.get()) {
			// Get the whole ball rolling
			if (lastRunTime == 0) {
				tickerPanel.repaint();
				lastRunTime = System.currentTimeMillis();
			}
			
			// Sleep for sleepInterval milliseconds
			while (true) {
				long millisToSleepFor =
					lastRunTime + sleepInterval - System.currentTimeMillis();
				if (logger.isTraceEnabled())
					logger.trace("Sleeping for " + millisToSleepFor + "ms");
				try {
					Thread.sleep(millisToSleepFor);
				} catch (InterruptedException ie) {
					// Ignored.
				}
				if (lastRunTime + sleepInterval <= System.currentTimeMillis())
					break;
			}
			
			// Paint the ticker panel
			tickerPanel.repaint();
			lastRunTime = System.currentTimeMillis();
		}
		
		logger.info("TickerThread will now exit.");
	}
}
