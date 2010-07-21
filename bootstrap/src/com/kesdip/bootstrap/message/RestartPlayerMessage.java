/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.ProcessUtils;
import com.kesdip.common.util.process.StreamLogger;

/**
 * Encapsulates the handling of a restart player message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class RestartPlayerMessage extends Message {
	/**
	 * The logger.
	 */
	private static final Logger logger = Logger
			.getLogger(RestartPlayerMessage.class);

	private String actionId;
	private static Process playerProcess;

	@Override
	public String getActionId() {
		return actionId;
	}

	public RestartPlayerMessage(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * Query the player process to see if it is alive.
	 * 
	 * @return True iff the player is still alive.
	 */
	public static boolean isPlayerProcessAlive() {
		return PlayerProcessHelper.getInstance().isPlayerRunning();
	}

	public void process() throws Exception {
		logger.info("Restarting player");

		// kill all orphan media players
		ProcessUtils.killAll("mplayer.exe");
		ProcessUtils.killAll("vlc.exe");

		// create the temp dir if it does not exist
		createTempDir();

		// Set up the command line
		List<String> cmdArray = new ArrayList<String>();

		// the following 2 are to enable debug
		// cmdArray.add("-Xdebug");
		// cmdArray
		// .add("-Xrunjdwp:transport=dt_socket,server=y,address=12999,suspend=n");
		// memory args
		cmdArray.add("-Xms32m");
		cmdArray.add("-Xmx128m");
		// classpath
		cmdArray.add("-cp");
		cmdArray.add(Config.getSingleton().getPlayerClasspath());
		// system properties (tmp dir etc)
		cmdArray.add("-Djava.io.tmpdir=\""
				+ Config.getSingleton().getPlayerTmpDir() + '\"');
		// main class
		cmdArray.add(Config.getSingleton().getPlayerMainClass());

		String workingDir = Config.getSingleton().getPlayerWorkingDir();

		if (logger.isDebugEnabled()) {
			logger.debug("Player CP: "
					+ Config.getSingleton().getPlayerClasspath());
			logger.debug("Player main class: "
					+ Config.getSingleton().getPlayerMainClass());
			logger.debug("Player workdir: " + workingDir);
		}

		// See Bugzilla#9 for the following line
		ProcessUtils.killAll("explorer.exe");
		// kill any remaining players
		PlayerProcessHelper.getInstance().killAllPlayers();
		playerProcess = PlayerProcessHelper.getInstance().startPlayer(
				cmdArray.toArray(new String[cmdArray.size()]), null,
				new File(workingDir));

		StreamLogger playerOut = new StreamLogger("Player Output", Level.INFO);
		playerOut.setInputStream(playerProcess.getInputStream());
		playerOut.start();
		StreamLogger playerErr = new StreamLogger("Player Error", Level.ERROR);
		playerErr.setInputStream(playerProcess.getErrorStream());
		playerErr.start();
	}

	public String toMessageString() {
		return "[RestartPlayer]";
	}

	/**
	 * @return Process the running player process.
	 */
	public static Process getPlayerProcess() {
		return playerProcess;
	}

	/**
	 * Create the player's temp directory.
	 */
	private final void createTempDir() {
		try {
			FileUtils.getFolder(Config.getSingleton().getPlayerTmpDir());
		} catch (Exception e) {
			// do nothing
		}
	}
}
