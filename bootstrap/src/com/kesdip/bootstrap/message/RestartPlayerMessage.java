/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;
import com.kesdip.common.util.ProcessUtils;

/**
 * Encapsulates the handling of a restart player message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class RestartPlayerMessage extends Message {
	private static final Logger logger = Logger
			.getLogger(RestartPlayerMessage.class);

	private String actionId;
	private static Process playerProcess;
	private static LoggerThread playerOutputLoggingThread;
	private static LoggerThread playerErrorLoggingThread;

	@Override
	public String getActionId() {
		return actionId;
	}

	public RestartPlayerMessage(String actionId) {
		this.actionId = actionId;
	}

	/**
	 * Helper class to log the process output to the loggers of this class.
	 * 
	 * @author Pafsanias Ftakas
	 */
	private class LoggerThread extends Thread {
		private BufferedReader reader;
		private boolean running;

		private LoggerThread(String output, BufferedReader reader) {
			super(output);

			this.reader = reader;
			this.running = true;
		}

		public synchronized void stopRunning() {
			this.running = false;
		}

		public synchronized boolean isRunning() {
			return running;
		}

		@Override
		public void run() {
			while (isRunning()) {
				try {
					String line = reader.readLine();
					if (line == null) {
						// The player has probably died...
						stopRunning();
					}
					if (logger.isDebugEnabled()) {
						logger.debug(line);
					}
				} catch (Throwable t) {
					logger.error("Exception while logging player "
							+ "process output.", t);
				}
			}
		}
	}

	/**
	 * Query the player process to see if it is alive.
	 * 
	 * @return True iff the player is still alive.
	 */
	public static boolean isPlayerProcessAlive() {
		try {
			if (playerProcess == null) {
				throw new IllegalStateException("Player process is null!?!?");
			}
			int exitValue = playerProcess.exitValue();
			logger.info("The player process has exited with status: "
					+ exitValue);
			return false;
		} catch (IllegalThreadStateException itse) {
			return true;
		}
	}

	public void process() throws Exception {
		logger.info("Restarting player");

		// Destroy the old player process if it exists.
		if (playerProcess != null) {
			playerProcess.destroy();
			playerOutputLoggingThread.stopRunning();
			playerErrorLoggingThread.stopRunning();
		}
		
		// kill all orphan media players
		ProcessUtils.killAll("mplayer.exe");
		ProcessUtils.killAll("vlc.exe");

		// Set up the command line
		List<String> cmdArray = new ArrayList<String>();
		cmdArray.add("javaw");
		// the following 2 are to enable debug
		cmdArray.add("-Xdebug");
		cmdArray
				.add("-Xrunjdwp:transport=dt_socket,server=y,address=12999,suspend=n");
		cmdArray.add("-cp");
		cmdArray.add(Config.getSingleton().getPlayerClasspath());
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
		// ProcessUtils.killAll("explorer.exe");

		playerProcess = Runtime.getRuntime().exec(
				cmdArray.toArray(new String[cmdArray.size()]), null,
				new File(workingDir));

		playerOutputLoggingThread = new LoggerThread("Player Output",
				new BufferedReader(new InputStreamReader(playerProcess
						.getInputStream())));
		playerOutputLoggingThread.start();
		playerErrorLoggingThread = new LoggerThread("Player Error",
				new BufferedReader(new InputStreamReader(playerProcess
						.getErrorStream())));
		playerErrorLoggingThread.start();
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
}
