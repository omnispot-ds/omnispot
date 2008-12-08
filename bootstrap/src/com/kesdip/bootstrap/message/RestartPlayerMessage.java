/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.bootstrap.message;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.kesdip.bootstrap.Config;

/**
 * Encapsulates the handling of a restart player message from the server.
 * 
 * @author Pafsanias Ftakas
 */
public class RestartPlayerMessage implements Message {
	private static final Logger logger =
		Logger.getLogger(RestartPlayerMessage.class);

	/**
	 * Helper class to log the process output to the loggers of this class.
	 * 
	 * @author Pafsanias Ftakas
	 */
	private class LoggerThread extends Thread {
		private BufferedReader reader;
		private boolean running;
		
		public LoggerThread(String output, BufferedReader reader) {
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
					if (line == null)
						stopRunning(); // The player has probably died...
					if (logger.isDebugEnabled())
						logger.debug(line);
				} catch (Throwable t) {
					logger.error("Exception while logging player " +
							"process output.", t);
				}
			}
		}
	}
	
	private static Process playerProcess;
	private static LoggerThread playerOutputLoggingThread;
	private static LoggerThread playerErrorLoggingThread;
	
	/**
	 * Query the player process to see if it is alive.
	 * @return True iff the player is still alive.
	 */
	public static boolean isPlayerProcessAlive() {
		try {
			int exitValue = playerProcess.exitValue();
			logger.info("The player process has exited with status: " +
					exitValue);
			return false;
		} catch (IllegalThreadStateException itse) {
			return true;
		}
	}

	@Override
	public void process() throws Exception {
		logger.info("Restarting player");
		
		// Destroy the old player process if it exists.
		if (playerProcess != null) {
			playerProcess.destroy();
			playerOutputLoggingThread.stopRunning();
			playerErrorLoggingThread.stopRunning();
		}
		
		// Set up the command line 
		String[] cmdArray = new String[4];
		cmdArray[0] = "java";
		cmdArray[1] = "-cp";
		cmdArray[2] = Config.getSingleton().getPlayerClasspath();
		cmdArray[3] = Config.getSingleton().getPlayerMainClass();
		String workingDir = Config.getSingleton().getPlayerWorkingDir();
		
		playerProcess = Runtime.getRuntime().exec(
				cmdArray, null, new File(workingDir));
		
		playerOutputLoggingThread = new LoggerThread("Player Output",
				new BufferedReader(new InputStreamReader(
						playerProcess.getInputStream())));
		playerOutputLoggingThread.start();
		playerErrorLoggingThread = new LoggerThread("Player Error",
				new BufferedReader(new InputStreamReader(
						playerProcess.getErrorStream())));
		playerErrorLoggingThread.start();
	}

	@Override
	public String toMessageString() {
		return "[RestartPlayer]";
	}

}
