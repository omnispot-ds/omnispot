package com.kesdip.bootstrap.message;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.ProcessUtils;

/**
 * Helper class that is also a Thread in order to use as a Shutdown Hook with
 * the runtime as well.
 * 
 * @author n.giamouris
 */
public class PlayerProcessHelper extends Thread {
	/**
	 * The logger.
	 */
	private final static Logger logger = Logger
			.getLogger(PlayerProcessHelper.class);
	/**
	 * Singleton instance.
	 */
	private final static PlayerProcessHelper singleton = new PlayerProcessHelper();

	private File javaHome;
	private File javaExe;
	private File playerJava;

	private final static String PLAYER_EXE = "javaw-kesdiplayer.exe";

	private Process playerProcess;

	private PlayerProcessHelper() {

		String envJavaHome = System.getenv("JAVA_HOME");
		javaHome = new File(envJavaHome);
		playerJava = new File(javaHome, "bin/" + PLAYER_EXE);
		if (!playerJava.exists()) {
			javaExe = new File(javaHome, "bin/javaw.exe");
			try {
				FileUtils.copyFile(javaExe, playerJava);
				if (logger.isInfoEnabled()) {
					logger.info("Created player java executable: "
							+ playerJava.getAbsolutePath());
				}
			} catch (IOException ex) {
				logger.error("Error initializing PlayerProcessHelper", ex);
				throw new RuntimeException(ex);
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("Found player java executable: "
						+ playerJava.getAbsolutePath());
			}
		}
		// add this as shutdown hook
		Runtime.getRuntime().addShutdownHook(this);
	}

	@Override
	public void run() {
		logger.info("Player shutdown hook activated");
		boolean result = killAllPlayers();
		if (result) {
			logger.info("Player shutdown hook successfully completed");
		} else {
			logger.warn("Player shutdown hook failed");
		}
	}

	public static PlayerProcessHelper getInstance() {
		return singleton;
	}

	/**
	 * Utility method to check whether the Player process is still running.
	 * 
	 * @return true if the Player process is running
	 */
	public boolean isPlayerRunning() {
		if (playerProcess == null) {
			return false;
		}

		try {
			if (logger.isDebugEnabled()) {
				logger
						.debug("The player is not running. The player has exited with exit value:"
								+ playerProcess.exitValue());
			}
			return false;
		} catch (IllegalThreadStateException ex) {
			logger.debug("The player is still running");
			return true;
		}
	}

	/**
	 * Starts the Player process with the specified arguments (to be sincere,
	 * for now it starts whatever you've specified in cmdArray)
	 * 
	 * @param cmdArray
	 *            The command along with its arguments.
	 * @param envp
	 *            The environment (can be null)
	 * @param dir
	 *            The working directory
	 * 
	 * @return the newly launched java.lang.Process
	 * @throws IOException
	 */
	public Process startPlayer(String[] args, String[] envp, File dir)
			throws IOException {
		// prepare the cmdArray
		String[] cmdArray = new String[args.length + 1];
		cmdArray[0] = "\"" + playerJava.getAbsolutePath() + "\"";
		for (int i = 0; i < args.length; i++) {
			cmdArray[i + 1] = args[i];
		}

		if (logger.isDebugEnabled()) {
			StringBuilder cmdLine = new StringBuilder();
			for (String token : cmdArray) {
				cmdLine.append(token).append(' ');
			}
			logger.debug("Player command line: " + cmdLine);
		}

		// launch the player process
		playerProcess = Runtime.getRuntime().exec(cmdArray, envp, dir);
		if (logger.isInfoEnabled()) {
			logger.info("Started Player process with pid:"
					+ getPlayerProcessId());
		}
		return playerProcess;
	}

	public boolean killAllPlayers() {
		boolean result = ProcessUtils.killAll(PLAYER_EXE);
		if (result) {
			logger
					.info("Any existing player processes were successfully terminated");
		} else {
			logger
					.warn("Unable to stop any stray player processes. These should appear in the task manager as "
							+ PLAYER_EXE);
		}
		return result;
	}

	/**
	 * Nice utility method to retrieve the Player process id in case we need to
	 * kill it retrospectively.
	 * 
	 * @return the Player process id
	 */
	private long getPlayerProcessId() {
		return ProcessUtils.getPid(playerProcess);
	}

}
