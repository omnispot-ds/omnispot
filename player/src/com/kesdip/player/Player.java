/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.awt.Color;
import java.awt.Dimension;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.kesdip.common.util.DBUtils;
import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.FullScreenComponent;
import com.kesdip.player.components.RootContainer;
import com.kesdip.player.helpers.PlayerUtils;

/**
 * The main class of the KESDIP player. Contains the main method and all the
 * bootstrap logic.
 * 
 * The run method of the player works as follows:
 * 
 * <pre>
 * wait for the initial deployment
 * create a black background window to hide the screen
 * forever {
 *   while (no new deployment) {
 *     pick next layout // either a scheduled layout, or the next layout in the contents
 *     &quot;play&quot; the layout contents
 *   }
 * }
 * </pre>
 * 
 * The timing monitor thread sends asynchronous messages for:
 * <ul>
 * <li>Starting a new deployment.</li>
 * <li>Completing the current layout because the duration has expired.</li>
 * <li>Starting a new scheduled layout.</li>
 * </ul>
 * 
 * @author Pafsanias Ftakas
 */
public class Player implements Runnable {
	private static final Logger logger = Logger.getLogger(Player.class);

	private static Properties props = new Properties();

	static {
		try {
			InputStream is = Player.class
					.getResourceAsStream("/player.properties");
			props.load(is);
			is.close();

			Class.forName(props.getProperty("driver_class"));

			DBUtils.setupDriver(props.getProperty("jdbc_url"));
		} catch (Exception e) {
			logger.error("Unable to read player properties file", e);
		}
	}

	public static String getVlcPath() {
		return props.getProperty("vlc_path");
	}

	private TimingMonitor monitor;

	/* SPRING CONFIGURATION STATE */
	private ApplicationContext ctx;

	private DeploymentSettings settings;

	private DeploymentContents contents;

	/**
	 * Initializing constructor.
	 * 
	 * @throws SchedulerException
	 */
	public Player() throws SchedulerException {
		this.monitor = new TimingMonitor(this);
		initialize();
	}
	
	public void initialize() {
		new Thread(this.monitor, "monitor").start();
		this.completeDeployment = false;
		this.completeLayout = false;
	}

	/**
	 * Starts a particular deployment descriptor.
	 * 
	 * @param ctx
	 *            The application context of the deployment descriptor.
	 * @param settings
	 *            The settings object.
	 * @param contents
	 *            The contents object.
	 * @throws Exception
	 */
	public synchronized void startDeployment(ApplicationContext ctx,
			DeploymentSettings settings, DeploymentContents contents)
			throws Exception {
		this.ctx = ctx;
		this.settings = settings;
		this.contents = contents;
		this.completeDeployment = true;
		this.completeLayout = true;
	}

	/**
	 * Set the flag to stop the currently running layout at the player's
	 * earliest possible convenience.
	 */
	public synchronized void completeLayout() {
		this.completeLayout = true;
	}

	/**
	 * Set the flags to start the supplied deployment layout at the player's
	 * earliest possible convenience. Called for "scheduled" layout invocations.
	 * 
	 * @param layout
	 *            The layout to start.
	 */
	public synchronized void startLayout(DeploymentLayout layout) {
		this.completeLayout = true;
		this.nextLayout = layout;
	}

	/* TRANSIENT STATE */
	private FullScreenComponent fullScreen;

	private boolean completeDeployment;

	private boolean completeLayout;

	private DeploymentLayout nextLayout;

	/**
	 * Helper to query the flags to see whether the current deployment should
	 * finish "playing".
	 * 
	 * @return True iff the currently running deployment should finish.
	 */
	private synchronized boolean isDeploymentComplete() {
		if (completeDeployment) {
			completeDeployment = false;
			completeLayout = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper method to check the flags to see if a scheduled layout is to be
	 * the next deployment layout "playing".
	 * 
	 * @return The scheduled layout to play next, or null if no such layout
	 *         exists.
	 */
	private synchronized DeploymentLayout getNextLayout() {
		if (nextLayout != null) {
			DeploymentLayout retVal = nextLayout;
			nextLayout = null;
			return retVal;
		}

		return null;
	}

	/**
	 * Helper to query the flags to see whether the current layout should finish
	 * "playing".
	 * 
	 * @return True iff the currently running layout should finish.
	 */
	private synchronized boolean isLayoutComplete() {
		if (completeLayout) {
			completeLayout = false;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Helper to decide iff the full screen mode will be requested from the
	 * graphics subsystem.
	 * 
	 * @param layout
	 *            The deployment layout for which the question must be answered.
	 * @return True iff the configuration asks for full screen mode.
	 */
	private boolean isFullScreenMode(DeploymentLayout layout) {
		return layout.getContentRoots().size() == 1;
	}

	/**
	 * Helper to run a single layout.
	 * 
	 * @param layout
	 *            the layout to run.
	 * @throws ComponentException
	 *             iff something goes wrong.
	 */
	private void runSingleLayout(DeploymentLayout layout)
			throws ComponentException {
		boolean isFullScreen = isFullScreenMode(layout);
		if (isFullScreen) {
			fullScreen = new FullScreenComponent();
			fullScreen.setApplicationContext(ctx);
			fullScreen.init(null, monitor);
		}

		try {
			// Create the resources for each root container.
			for (RootContainer container : layout.getContentRoots()) {
				if (isFullScreenMode(layout))
					container.createFullScreenResources();
				else
					container.createWindowedResources();

				container.init(fullScreen, monitor);
			}

			// The main loop of the layout
			while (!isLayoutComplete()) {
				// Call repaint on all the components
				for (RootContainer container : layout.getContentRoots()) {
					try {
						container.repaint();
					} catch (ComponentException ce) {
						logger.error("Unable to repaint component", ce);
					}
				}

				// Sleep a little.
				try {
					Thread.sleep(settings.getSleepInterval());
				} catch (InterruptedException ie) {
				}

				// Decide whether the layout should exit.
				boolean shouldBreak = false;
				for (RootContainer container : layout.getContentRoots()) {
					switch (container.isComplete()) {
					case COMPLETE:
						shouldBreak = true;
						break;
					case INCOMPLETE:
					case DONT_CARE:
						// Do nothing
						break;
					default:
						throw new RuntimeException("Unexpected completion "
								+ "state: " + container.isComplete());
					}
					if (shouldBreak)
						break;
				}
				if (shouldBreak) {
					logger.info("The current layout (" + layout.getName()
							+ ") has completed. Moving on to the next one.");
					break;
				}
			}
		} finally {
			logger.info("Completed layout: " + layout.getName() + ".");
			if (isFullScreen) {
				for (RootContainer container : layout.getContentRoots()) {
					container.releaseResources();
				}
				fullScreen.destroy();
				fullScreen = null;
			} else {
				for (RootContainer container : layout.getContentRoots()) {
					container.destroyWindowedResources();
				}
			}
		}
	}

	/**
	 * Helper to create a background frame to hide the screen contents in
	 * multi-frame mode.
	 */
	private void createBackgroundFrame() {
		JFrame frame = new JFrame();
		frame.setUndecorated(true);
		frame.setResizable(false);
		frame.addKeyListener(PlayerUtils.getExitKeyListener());
		frame.setCursor(PlayerUtils.getNoCursor());

		frame.setLocation(0, 0);
		frame.setPreferredSize(new Dimension(settings.getWidth(), settings
				.getHeight()));
		frame.setBackground(Color.BLACK);
		frame.getContentPane().setBackground(Color.BLACK);

		frame.pack();
		frame.setVisible(true);
		frame.requestFocus();
	}

	@Override
	public void run() {
		try {
			// Wait for the first deployment to go through
			while (true) {
				if (isDeploymentComplete()) {
					logger.info("Initial deployment has gone through");
					break;
				}

				// Sleep a little.
				try {
					Thread.sleep(10);
				} catch (InterruptedException ie) {
				}
			}

			createBackgroundFrame();

			while (true) {
				List<DeploymentLayout> layouts = contents.getLayouts();
				int layoutsIndex = 0;
				while (!isDeploymentComplete()) {
					DeploymentLayout nextLayout = getNextLayout();
					while (nextLayout == null) {
						nextLayout = layouts.get(layoutsIndex++);
						if (nextLayout.getCronExpression() != null) {
							// Skip this layout because it is being scheduled
							// through a Cron expression.
							nextLayout = null;
						}
						if (layoutsIndex >= layouts.size()) {
							layoutsIndex = 0;
						}
					}

					try {
						logger.info("Staring layout " + nextLayout.getName());
						monitor.startingLayout(nextLayout);
						runSingleLayout(nextLayout);
					} catch (Exception e) {
						logger.error("Error while playing layout with ID: "
								+ nextLayout.getName()
								+ ". Will continue with next " + "layout.", e);
					}
				}
				logger.info("Completing deployment flag has been raised.");
			}
		} catch (Throwable t) {
			logger.error("Error during the player run method.", t);
		}
	}

	public static void main(String[] args) {
		try {
			Player player = new Player();
			new Thread(player, "player").start();
		} catch (Exception e) {
			logger.error("Error in the main Player method", e);
		}
	}

}
