/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Robot;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.kesdip.common.util.DBUtils;
import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.FullScreenComponent;
import com.kesdip.player.components.RootContainer;
import com.kesdip.player.configure.DeploymentConfigurer;
import com.kesdip.player.constenum.SystemPropertiesKeys;
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
 * <p>
 * The player instance also listens on the standard in for reconfiguration
 * messages of the form <code>expression=value</code>. These refer to beans of
 * the currently playing deployment.
 * </p>
 * 
 * @author Pafsanias Ftakas
 */
public class Player implements Runnable {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(Player.class);

	protected static Properties props = new Properties();

	protected TimingMonitor monitor;

	protected AtomicBoolean stopRunning;

	/**
	 * The current deployment Spring application context.
	 */
	private ApplicationContext deploymentContext;

	private DeploymentSettings settings;

	private DeploymentContents contents;

	/* TRANSIENT STATE */
	protected FullScreenComponent fullScreen;

	protected boolean completeDeployment;

	protected boolean completeLayout;

	protected DeploymentLayout nextLayout;

	protected JFrame backgroundFrame;

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
		String sysProp = System
				.getProperty(SystemPropertiesKeys.KESDIP_EPE_DESIGNER_VLC_PATH);
		if (sysProp != null) {
			return sysProp;
		}
		return props.getProperty("vlc_path");
	}

	public static String getMPlayerFile() {
		String sysProp = System
				.getProperty(SystemPropertiesKeys.KESDIP_EPE_DESIGNER_MPLAYER_FILE);
		if (sysProp != null) {
			return sysProp;
		}
		return props.getProperty("mplayer_file");
	}

	/**
	 * Default constructor.
	 * 
	 * @throws SchedulerException
	 *             on error creating the schedulers
	 */
	public Player() throws SchedulerException {
		this.monitor = new TimingMonitor(this);
	}

	public void initialize() throws SchedulerException {
		new Thread(this.monitor, "monitor").start();
		this.completeDeployment = false;
		this.completeLayout = false;
		this.stopRunning = new AtomicBoolean(false);
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
	 */
	public synchronized void startDeployment(ApplicationContext ctx,
			DeploymentSettings settings, DeploymentContents contents) {
		this.deploymentContext = ctx;
		this.settings = settings;
		if (logger.isDebugEnabled()) {
			logger.debug("Deployment sleep interval is: "
					+ settings.getSleepInterval());
		}
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
		return false; // TODO revisit this later...
		// return layout.getContentRoots().size() == 1;
	}

	public int getSleepInterval() {
		return settings.getSleepInterval();
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
			fullScreen.setApplicationContext(deploymentContext);
			fullScreen.init(null, monitor, this);
		}

		try {
			// Create the resources for each root container.
			for (RootContainer container : layout.getContentRoots()) {
				container.setPlayer(this);

				if (isFullScreenMode(layout)) {
					container.createFullScreenResources();
				} else {
					container.createWindowedResources();
				}

				container.init(fullScreen, monitor, this);
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
					// do nothing
				}

				// Decide whether the layout should exit.
				boolean shouldBreak = false;
				for (RootContainer container : layout.getContentRoots()) {
					switch (container.isComplete()) {
					case COMPLETE: {
						shouldBreak = true;
						break;
					}
					case INCOMPLETE:
					case DONT_CARE:
						// Do nothing
						break;
					default:
						throw new RuntimeException("Unexpected completion "
								+ "state: " + container.isComplete());
					}
					if (shouldBreak) {
						break;
					}
				}
				if (shouldBreak) {
					if (logger.isInfoEnabled()) {
						logger
								.info("The current layout ("
										+ layout.getName()
										+ ") has completed. Moving on to the next one.");
					}
					break;
				}
				if (stopRunning.get()) {
					logger
							.info("The stop running flag has been raised. Player is exiting.");
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Error in the layout loop", e);
		} finally {
			if (logger.isInfoEnabled()) {
				logger.info("Completed layout: " + layout.getName() + ".");
			}
			if (isFullScreen) {
				for (RootContainer container : layout.getContentRoots()) {
					container.releaseResources();
				}
				fullScreen.destroy();
				fullScreen = null;
			} else {
				for (RootContainer container : layout.getContentRoots()) {
					container.destroyWindowedResources();
					container.releaseResources();
				}
			}
			// caused problems in deployment preview
			// playerExited();
		}
	}

	/**
	 * Hook to be overridden by PlayerPreview in order to exit the process.
	 */
	protected void playerExited() {
	}

	/**
	 * Helper to create a background frame to hide the screen contents in
	 * multi-frame mode.
	 */
	private void createBackgroundFrame() {
		backgroundFrame = new JFrame();
		backgroundFrame.setUndecorated(true);
		backgroundFrame.setResizable(false);
		backgroundFrame.addKeyListener(PlayerUtils.getExitKeyListener(this));
		backgroundFrame
				.addMouseListener(PlayerUtils.getExitMouseListener(this));
		// hide the cursor and put it in the lower right corner
		// even if it becomes visible again, it will be out of sight
		backgroundFrame.setCursor(PlayerUtils.getNoCursor());
		try {
			Robot robot = new Robot();
			robot.mouseMove(settings.getWidth(), settings.getHeight());
		} catch (AWTException e) {
			logger.error("Error trying to move mouse off-screen", e);
		}

		backgroundFrame.setLocation(0, 0);
		backgroundFrame.setPreferredSize(new Dimension(settings.getWidth(),
				settings.getHeight()));
		backgroundFrame.setBackground(Color.BLACK);
		backgroundFrame.getContentPane().setBackground(Color.BLACK);

		backgroundFrame.pack();
		backgroundFrame.setVisible(true);
		backgroundFrame.requestFocus();
	}

	/**
	 * Utility method. Brings the background frame to the foreground, hiding the
	 * frame containing the layout. A case when this is useful is to hide the
	 * active layout when full-screen videos are playing.
	 * @see Bug#150
	 */
	public void hideActiveLayout() {
		if (backgroundFrame.getState() != Frame.NORMAL) {
			backgroundFrame.setState(Frame.NORMAL);
		}
		backgroundFrame.setAlwaysOnTop(true);
		backgroundFrame.toFront();
		backgroundFrame.repaint();
	}

	/**
	 * Utility method. Puts the background frame to the background, showing the
	 * active content frame again.
	 */
	public void unhideActiveLayout() {
		if (backgroundFrame.getState() != Frame.NORMAL) {
			backgroundFrame.setState(Frame.NORMAL);
		}
		backgroundFrame.setAlwaysOnTop(false);
		backgroundFrame.toBack();
	}

	private void destroyBackgroundFrame() {
		backgroundFrame.setVisible(false);
		backgroundFrame.dispose();
		backgroundFrame = null;
	}

	public void stopPlaying() {
		stopRunning.set(true);
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
					// do nothing
				}
			}

			createBackgroundFrame();
			logger.info("Created background frame");

			try {
				DeploymentConfigurer configurer = new DeploymentConfigurer(this);
				new Thread(configurer, "playerConfigurer").start();
				logger.info("Created the configurer");
			} catch (Exception e) {
				logger.error("Error creating the configurer", e);
			}

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
						logger.info("Starting layout " + nextLayout.getName());
						monitor.startingLayout(nextLayout);
						runSingleLayout(nextLayout);
					} catch (Exception e) {
						logger.error("Error while playing layout with ID: "
								+ nextLayout.getName()
								+ ". Will continue with next " + "layout.", e);
					}

					if (stopRunning.get()) {
						logger
								.info("The stop running flag has been raised. Player is exiting.");
						break;
					}
				}
				if (stopRunning.get()) {
					logger
							.info("The stop running flag has been raised. Player is exiting.");
					break;
				} else {
					logger.info("Completing deployment flag has been raised.");
				}
			}

			destroyBackgroundFrame();
			playerExited();
		} catch (Throwable t) {
			logger.error("Error during the player run method.", t);
		}
	}

	public static void main(String[] args) {
		Player player = null;
		try {
			player = new Player();
			player.initialize();
			new Thread(player, "player").start();
		} catch (Exception e) {
			logger.error("Error in the main Player method", e);
			return;
		}

	}

	/**
	 * @return the deploymentContext
	 */
	public ApplicationContext getDeploymentContext() {
		return deploymentContext;
	}
}
