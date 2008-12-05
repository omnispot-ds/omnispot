/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player;

import java.awt.Color;
import java.awt.Dimension;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JFrame;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDriver;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;

import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.FullScreenComponent;
import com.kesdip.player.components.RootContainer;
import com.kesdip.player.helpers.PlayerUtils;
import com.kesdip.player.registry.ContentRegistry;

/**
 * The main class of the KESDIP player. Contains the main method and all the
 * bootstrap logic.
 * 
 * The run method of the player works as follows:
 * <pre>
 * wait for the initial deployment
 * create a black background window to hide the screen
 * forever {
 *   while (no new deployment) {
 *     pick next layout // either a scheduled layout, or the next layout in the contents
 *     "play" the layout contents
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
	
	private TimingMonitor monitor;
	
	/* SPRING CONFIGURATION STATE */
	private ApplicationContext ctx;
	private DeploymentSettings settings;
	private DeploymentContents contents;
	
	/**
	 * Initializing constructor.
	 * @throws SchedulerException
	 */
	public Player() throws SchedulerException {
		this.monitor = new TimingMonitor(this);
		new Thread(this.monitor, "monitor").start();
		this.completeDeployment = false;
		this.completeLayout = false;
	}
	
	/**
	 * Starts a particular deployment descriptor.
	 * @param ctx The application context of the deployment descriptor.
	 * @param settings The settings object.
	 * @param contents The contents object.
	 */
	public synchronized void startDeployment(ApplicationContext ctx,
			DeploymentSettings settings, DeploymentContents contents) {
		this.ctx = ctx;
		this.settings = settings;
		this.contents = contents;
		ContentRegistry.getContentRegistry().setPlayer(this);
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
	 * @param layout The layout to start.
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
	 * @return The scheduled layout to play next, or null if no such layout
	 * exists.
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
	 * Helper to query the flags to see whether the current layout should
	 * finish "playing".
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
	 * @param layout The deployment layout for which the question must be
	 * answered.
	 * @return True iff the configuration asks for full screen mode.
	 */
	private boolean isFullScreenMode(DeploymentLayout layout) {
		return layout.getContentRoots().size() == 1;
	}
	
	/**
	 * Helper to run a single layout.
	 * @param layout the layout to run.
	 * @throws ComponentException iff something goes wrong.
	 */
	private void runSingleLayout(DeploymentLayout layout)
			throws ComponentException {
		boolean isFullScreen = isFullScreenMode(layout);
		if (isFullScreen) {
			fullScreen = new FullScreenComponent();
			fullScreen.setApplicationContext(ctx);
			fullScreen.init(null);
		}
		
		try {
			// Create the resources for each root container.
			for (RootContainer container : layout.getContentRoots()) {
				if (isFullScreenMode(layout))
					container.createFullScreenResources();
				else
					container.createWindowedResources();
				
				container.init(fullScreen);
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
				} catch (InterruptedException ie) { }
				
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
						throw new RuntimeException("Unexpected completion " +
								"state: " + container.isComplete());
					}
					if (shouldBreak)
						break;
				}
				if (shouldBreak) {
					logger.info("The current layout (" + layout.getName() +
							") has completed. Moving on to the next one.");
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
		frame.setPreferredSize(new Dimension(settings.getWidth(), settings.getHeight()));
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
				} catch (InterruptedException ie) { }
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
						logger.error("Error while playing layout with ID: " +
								nextLayout.getName() + ". Will continue with next " +
										"layout.", e);
					}
				}
				logger.info("Completing deployment flag has been raised.");
			}
		} catch (Throwable t) {
			logger.error("Error during the player run method.", t);
		}
	}
	
	boolean driverSetup = false;
	
	/**
	 * Helper method to get a connection from the connection pool.
	 * @return A connection from the connection pool.
	 * @throws SQLException iff something goes wrong.
	 */
	public Connection getConnection() throws Exception {
		if (!driverSetup) {
			setupDriver();
			driverSetup = true;
		}
		return DriverManager.getConnection("jdbc:apache:commons:dbcp:local");
	}
	
	/**
	 * Helper to set up a connection pool with the driver manager, so as to 
	 * be used by the getConnection() method above.
	 * 
	 * @param connectURI The JDBC URL to use to get the actual connections
	 * to the database.
	 * @throws Exception iff something goes wrong.
	 */
	private void setupDriver() throws Exception {
        //
        // First, we'll need a ObjectPool that serves as the
        // actual pool of connections.
        //
        // We'll use a GenericObjectPool instance, although
        // any ObjectPool implementation will suffice.
        //
        ObjectPool connectionPool = new GenericObjectPool(null);

        //
        // Next, we'll create a ConnectionFactory that the
        // pool will use to create Connections.
        // We'll use the DriverManagerConnectionFactory,
        // using the connect string passed in the command line
        // arguments.
        //
        ConnectionFactory connectionFactory =
        	new DriverManagerConnectionFactory(settings.getJdbcUrl(), null);

        //
        // Now we'll create the PoolableConnectionFactory, which wraps
        // the "real" Connections created by the ConnectionFactory with
        // the classes that implement the pooling functionality.
        //
        @SuppressWarnings("unused")
		PoolableConnectionFactory poolableConnectionFactory =
			new PoolableConnectionFactory(
					connectionFactory, connectionPool, null, null, false, false);

        //
        // Finally, we create the PoolingDriver itself...
        //
        Class.forName("org.apache.commons.dbcp.PoolingDriver");
        PoolingDriver driver = (PoolingDriver)
        		DriverManager.getDriver("jdbc:apache:commons:dbcp:");

        //
        // ...and register our pool with it.
        //
        driver.registerPool("local", connectionPool);

        //
        // Now we can just use the connect string "jdbc:apache:commons:dbcp:local"
        // to access our pool of Connections.
        //
        
        logger.info("We have registered connection pool " +
        		"at jdbc:apache:commons:dbcp:local");
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
