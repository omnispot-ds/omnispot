package com.kesdip.player;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.FullScreenComponent;
import com.kesdip.player.components.RootContainer;

/**
 * The main class of the KESDIP player. Contains the main method and all the
 * bootstrap logic.
 * 
 * TODO: Right now the player start whatever deployment is in the appContext.xml
 * file in the classpath. How do we deploy new deployments? Does the bootstrap
 * app restart the player with a new application context? Is there some sort
 * of communication with the player? Inquiring minds want to know...
 * 
 * @author Pafsanias Ftakas
 */
public class Player implements Runnable {
	private static final Logger logger = Logger.getLogger(Player.class);
	
	/* SPRING CONFIGURATION STATE */
	private DeploymentSettings settings;
	private ApplicationContext ctx;
	private DeploymentContents contents;
	
	public Player(ApplicationContext ctx, DeploymentSettings settings,
			DeploymentContents contents) {
		this.ctx = ctx;
		this.settings = settings;
		this.contents = contents;
	}
	
	/* TRANSIENT STATE */
	private FullScreenComponent fullScreen;
	
	/**
	 * Helper to decide iff the full screen mode will be requested from the
	 * graphics subsystem.
	 * @return True iff the configuration asks for full screen mode.
	 */
	private boolean isFullScreenMode() {
		return contents.getContentRoots().size() == 1;
	}
	
	@Override
	public void run() {
		try {
			if (isFullScreenMode()) {
				fullScreen = new FullScreenComponent();
				fullScreen.setApplicationContext(ctx);
				fullScreen.init(null);
			}
			
			try {
				// Create the resources for each root container.
				for (RootContainer container : contents.getContentRoots()) {
					if (isFullScreenMode())
						container.createFullScreenResources();
					else
						container.createWindowedResources();
					
					container.init(fullScreen);
				}
				
				// The main loop of the player
				while (true) {
					// Call repaint on all the components
					for (RootContainer container : contents.getContentRoots()) {
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
				}
			} finally {
				if (isFullScreenMode())
					fullScreen.destroy();
			}
		} catch (Throwable t) {
			logger.error("Error during the player run method.", t);
		}
	}

	public static void main(String[] args) {
		try {
			ApplicationContext ctx = new ClassPathXmlApplicationContext(
					"appContext.xml");
			DeploymentSettings settings =
				(DeploymentSettings) ctx.getBean("deploymentSettings");
			DeploymentContents contents =
				(DeploymentContents) ctx.getBean("deploymentContents");
			if (settings == null)
				throw new BeanInitializationException("The application context " +
						"factory should contain a bean with ID 'deploymentSettings'.");
			if (contents == null)
				throw new BeanInitializationException("The application context " +
						"factory should contain a bean with ID 'deploymentContents'.");
			Player player = new Player(ctx, settings, contents);
			new Thread(player, "player").start();
		} catch (Exception e) {
			logger.error("Error in the main Player method", e);
		}
	}

}
