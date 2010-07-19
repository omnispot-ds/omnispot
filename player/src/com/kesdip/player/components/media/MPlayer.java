/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.kesdip.common.util.BufferedLineReadListener;
import com.kesdip.common.util.BufferedLineReader;
import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;
import com.kesdip.common.util.process.ProcessExitDetector;
import com.kesdip.common.util.process.ProcessExitListener;
import com.kesdip.common.util.process.StreamLogger;
import com.kesdip.player.components.media.VideoConfiguration.Playlist;
import com.kesdip.player.components.media.loglistener.FullScreenLogReader;
import com.kesdip.player.components.media.loglistener.PlaybackCompletedLogReader;
import com.kesdip.player.constenum.VideoQualityTypes;

/**
 * Wrapper around a single MPlayer instance.
 * <p>
 * Initializes an MPlayer in slave mode and maintains a reference to its
 * process. It communicates with it via its command-line protocol.
 * </p>
 * <p>
 * A single instance can render in a single area of the screen (AWT component,
 * fullscreen).
 * </p>
 * 
 * @see http://www.mplayerhq.hu/DOCS/tech/slave.txt
 * @see http://www.mplayerhq.hu/DOCS/man/en/mplayer.1.html
 * @author gerogias
 */
public class MPlayer implements ProcessExitListener {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(MPlayer.class);

	/**
	 * The character to surround file paths to prevent errors due to spaces. In
	 * Windows it is a double quote, in *X systems a single quote.
	 */
	private final char FILE_PATH_CHAR = '\\' == File.separatorChar ? '"' : '\'';

	/**
	 * The MPlayer instance command-line input. Never use this property directly
	 * to pass commands, as the process may be dead. Instead call
	 * {@link #getMPlayerIn()}.
	 */
	private PrintStream mplayerIn = null;

	/**
	 * The configuration.
	 */
	private MPlayerConfiguration config = null;

	/**
	 * Is the player paused?
	 */
	private boolean paused = false;

	/**
	 * Is the player stopped?
	 */
	private boolean stopped = false;

	/**
	 * Private constructor.
	 */
	private MPlayer() {
		// do nothing
	}

	/**
	 * Factory method.
	 * 
	 * @param config
	 *            the configuration
	 * @return MPlayerWrapper the instance
	 * @throws IOException
	 *             on error creating the instance
	 */
	public static MPlayer getInstance(MPlayerConfiguration config)
			throws IOException {
		if (config == null || !config.isValid()) {
			throw new IllegalArgumentException(
					"Configuration is null or invalid");
		}
		MPlayer instance = new MPlayer();
		instance.config = config.clone();
		// force-init the native player
		instance.getMPlayerIn();
		return instance;
	}

	/**
	 * Returns the input stream for the player after checking the process
	 * health. If the player is not started, it is created at this point.
	 * 
	 * @return PrintStream the stream to write to the player
	 */
	private final PrintStream getMPlayerIn() throws IOException {
		if (mplayerIn == null) {
			if (logger.isInfoEnabled()) {
				logger.info("Creating player instance '"
						+ config.getPlayerName() + '\'');
			}
			String cmdLine = createCommandLine(config);

			Process process = Runtime.getRuntime().exec(cmdLine);
			mplayerIn = new PrintStream(process.getOutputStream());
			createTrackers(process, this.config);
		}
		return mplayerIn;
	}

	/**
	 * Creates a temporary {@link MPlayer} instance. Used in the
	 * "play scheduled media" use case.
	 * 
	 * @param videoConfig
	 *            the configuration to use
	 */
	private final void createTempMPlayer(VideoConfiguration videoConfig)
			throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("Creating temporary player instance '"
					+ videoConfig.getPlayerName() + '\'');
		}
		String cmdLine = createCommandLine(videoConfig);

		Process process = Runtime.getRuntime().exec(cmdLine);
		mplayerIn = new PrintStream(process.getOutputStream());
		createTrackers(process, videoConfig);
	}

	/**
	 * @param configuration
	 *            the configuration to consider
	 * @return String a command line created from the passed
	 *         {@link MPlayerConfiguration}.
	 */
	final String createCommandLine(MPlayerConfiguration configuration) {
		StringBuilder cmd = new StringBuilder();
		// path to MPlayer
		cmd.append(config.getPlayerExecutable());
		// always slave process
		cmd.append(" -slave");
		// no log output
		// TODO: why StreamLogger does not consume process output fast enough?
		// we should have -quiet here...
		cmd.append(" -quiet");
		// do not capture mouse
		cmd.append(" -nomouseinput");
		if (!configuration.isFullScreen()) {
			// stay alive after playback finish; never for
			// fullscreen/non-looping instance
			if ((configuration instanceof VideoConfiguration)
					&& ((VideoConfiguration) configuration).isLoop()) {
				cmd.append(" -idle");
			}
			// stretch video/tv in window mode
			cmd.append(" -nokeepaspect");
		}
		// fullscreen or native control with colorkey
		if (!configuration.isFullScreen()) {
			cmd.append(" -wid ").append(configuration.getWindowId());
			cmd.append(" -colorkey ").append(
					StringUtils.toHexString(configuration.getColorKey()));
		} else {
			cmd.append(" -fs");
		}
		// no DirectX acceleration by default (Bug#126, Bug#145)
		if ((configuration instanceof VideoConfiguration)
				&& VideoQualityTypes.NORMAL
						.equalsIgnoreCase(((VideoConfiguration) configuration)
								.getQuality())) {
			cmd.append(" -vo directx:noaccel");
		}
		// extra command line args
		if (configuration.getExtraArgs() != null) {
			for (String arg : configuration.getExtraArgs()) {
				cmd.append(' ').append(arg);
			}
		}
		if (configuration instanceof VideoConfiguration) {
			// add files to the queue, if any
			VideoConfiguration video = (VideoConfiguration) configuration;
			// loop
			if (video.isLoop()) {
				cmd.append(" -loop 0");
			}
			if (video.getPlaylists().size() > 0) {
				cmd.append(' ').append(createPlaylists(video));
			} else {
				cmd.append(" foo.avi");
			}
		} else if (configuration instanceof AnalogTVConfiguration) {
			AnalogTVConfiguration analogTv = (AnalogTVConfiguration) configuration;
			// audio device has been defined
			if (analogTv.getAudioDevice() != -1) {
				cmd.append(" -tv adevice=").append(analogTv.getAudioDevice());
			}
			cmd.append(" tv://").append(analogTv.getChannel());
		} else if (configuration instanceof DVBTConfiguration) {
			DVBTConfiguration dvbt = (DVBTConfiguration) configuration;
			cmd.append(" -dvbin file=\"").append(dvbt.getChannelsConfFile())
					.append('"');
			cmd.append(" dvb://\"").append(dvbt.getStreamName()).append('"');
		}
		if (logger.isDebugEnabled()) {
			logger.debug("MPlayer command line: " + cmd);
		}

		return cmd.toString();
	}

	/**
	 * Immediately stops playback and kills the underlying MPlayer process.
	 * 
	 * @return boolean <code>true</code> if the process was terminated or
	 *         <code>false</code> if the process was found to be dead
	 */
	public boolean terminate() {
		if (mplayerIn != null) {
			mplayerIn.print("quit 0\n");
			mplayerIn.flush();
			mplayerIn = null;
			return true;
		} else {
			return false;
		}

	}

	/**
	 * Pause/unpause playback.
	 * 
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void pause() {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'pause' is only allowed for a video file player");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("pause\n");
			in.flush();
			paused = !paused;
		} catch (IOException e) {
			logger.error("Error pausing player", e);
		}
	}

	/**
	 * Stops playback.
	 * 
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void stop() {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'stop' is only allowed for a video file player");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("stop\n");
			in.flush();
			stopped = true;
		} catch (IOException e) {
			logger.error("Error stopping player", e);
		}
	}

	/**
	 * Interrupts playback and plays the given file only once.
	 * <p>
	 * The player is terminated and a new, temporary player instance is created.
	 * If the file is not fullscreen, then the new instance will re-use the same
	 * native component as this one. Upon completion of the file's playback, a
	 * new "normal" player is created again.
	 * 
	 * @param fileName
	 *            full path to the file to play
	 * @param fullScreen
	 *            is the file fullscreen?
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void playFile(String fileName, boolean fullScreen) {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'playFile' is only allowed for a video file player");
		}
		// TODO: Check with future versions of MPlayer if stop/start work as
		// expected; terminate is overkill
		terminate();
		// create temp player instance
		VideoConfiguration tempConfig = new VideoConfiguration();
		tempConfig.setFullScreen(fullScreen);
		tempConfig.setLoop(false);
		if (!fullScreen) {
			// re-use existing native component
			tempConfig.setColorKey(config.getColorKey());
			tempConfig.setWindowId(config.getWindowId());
		}
		tempConfig.setPlayerName(config.getPlayerName() + "_cron");
		Playlist playlist = new Playlist("cron");
		playlist.addFile(fileName);
		tempConfig.addPlaylist(playlist);
		try {
			createTempMPlayer(tempConfig);
		} catch (IOException e) {
			logger.error("Error starting scheduled video", e);
			// try to resume playback on error
			try {
				this.mplayerIn = MPlayer.this.getMPlayerIn();
			} catch (IOException ex) {
				logger
						.error("Error resuming from a failed scheduled video",
								ex);
			}
		}
	}

	/**
	 * @return boolean <code>true</code> if playback has completed and/or the
	 *         player process has terminated
	 */
	public boolean isPlaybackCompleted() {
		return mplayerIn == null;
	}

	/**
	 * Ask the MPlayer for the progress of the playback. It will cause a call to
	 * {@link MPlayerEventListener#playbackCompleted()}, if the current movie
	 * has completed.
	 */
	public void pollProgress() {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'pollProgress' is only allowed for a video file player");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("get_property percent_pos\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error polling progress", e);
		}
	}

	/**
	 * Ask the MPlayer if it is currently in fullscreen. It will cause a
	 * callback to all registered {@link MPlayerFullScreenStatusTracker}s.
	 */
	public void pollFullScreen() {
		try {
			PrintStream in = getMPlayerIn();
			in.print("get_property fullscreen\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error polling fullscreen", e);
		}
	}

	/**
	 * @see com.kesdip.common.util.process.ProcessExitListener#processFinished(java.lang.Process,
	 *      java.lang.Object)
	 */
	@Override
	public void processFinished(Process process, Object userObject) {
		logger.warn("Player '" + config.getPlayerName() + "' finished");
		mplayerIn = null;
	}

	/**
	 * Creates temporary playlist files (containing one media file per line)
	 * with the contents of the given config's playlists.
	 * 
	 * @param config
	 *            the configuration to use
	 * @return String the playlist string with all playback hints
	 */
	private final String createPlaylists(VideoConfiguration config) {
		StringBuilder cmd = new StringBuilder();
		for (Playlist playlist : config.getPlaylists()) {
			cmd.append(' ').append(
					createPlaylist(config.getPlayerName(), playlist));
		}
		return cmd.toString();
	}

	/**
	 * Creates a temporary playlist file (containing one media file per line)
	 * with the contents of the given playlist.
	 * 
	 * @param name
	 *            the name prefix of the playlist file
	 * @param playlist
	 *            the playlist to use
	 * @return String the playlist string with all playback hints or an empty
	 *         string in case of error
	 */
	private final String createPlaylist(String name, Playlist playlist) {
		PrintStream out = null;
		File file = null;
		try {
			file = FileUtils.createUniqueFile(name + '_' + playlist.getName(),
					true);
			out = new PrintStream(file);
			String normalizedName = null;
			for (String fileName : playlist.getFileList()) {
				normalizedName = fileName.replace('/', File.separatorChar);
				out.print(normalizedName + "\n");
			}
			out.flush();
			return "-playlist " + FILE_PATH_CHAR + file.getCanonicalPath()
					+ FILE_PATH_CHAR + (playlist.isFullScreen() ? " -fs" : "");
		} catch (IOException ex) {
			logger.error("Error creating playlist", ex);
		} finally {
			try {
				out.close();
			} catch (Exception e) {
				// do nothing
			}
		}
		return "";
	}

	/**
	 * @return the paused
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * @return the stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * Last-resort method to cleanup MPlayer resources.
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		terminate();
	}

	/**
	 * Utility method to create the various status trackers attached to the
	 * external <code>Process</code> object.
	 * 
	 * @param process
	 *            the process
	 */
	private final void createTrackers(Process process,
			MPlayerConfiguration config) {
		ProcessExitDetector exitDetector = new ProcessExitDetector(process,
				config.getPlayerName());
		exitDetector.addProcessListener(this);
		exitDetector.start();
		// listeners for process info
		Collection<BufferedLineReadListener> infoListeners = new ArrayList<BufferedLineReadListener>();
		infoListeners.add(new StreamLogger(config.getPlayerName(), Level.INFO));
		infoListeners.add(new FullScreenLogReader(config.getListeners(), config
				.getPlayerName()));
		infoListeners.add(new PlaybackCompletedLogReader(config.getListeners(),
				config.getPlayerName()));
		MPlayerProcessReadingLoop infoReadingLoop = new MPlayerProcessReadingLoop(
				process.getInputStream(), infoListeners);
		infoReadingLoop.start();
		// listeners for error info
		Collection<BufferedLineReadListener> errorListeners = new ArrayList<BufferedLineReadListener>();
		errorListeners
				.add(new StreamLogger(config.getPlayerName(), Level.WARN));
		MPlayerProcessReadingLoop errorReadingLoop = new MPlayerProcessReadingLoop(
				process.getErrorStream(), errorListeners);
		errorReadingLoop.start();
	}

	/**
	 * A utility class with an endless loop to read an MPlayer process output
	 * (out or err).
	 * 
	 * @author gerogias
	 */
	private final class MPlayerProcessReadingLoop extends Thread {

		/**
		 * The stream.
		 */
		private InputStream inputStream;
		/**
		 * The listeners to notify.
		 */
		private Collection<BufferedLineReadListener> listeners;

		/**
		 * Constructor.
		 * 
		 * @param input
		 *            the input stream
		 * @param logger
		 *            the logger
		 */
		private MPlayerProcessReadingLoop(InputStream input,
				Collection<BufferedLineReadListener> listeners) {
			this.inputStream = input;
			this.listeners = listeners;
		}

		/**
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			BufferedLineReader reader = null;
			try {
				reader = new BufferedLineReader(inputStream);
				for (BufferedLineReadListener listener : listeners) {
					reader.addListener(listener);
				}
				while (true) {
					reader.read();
				}
			} catch (Exception e) {
				try {
					reader.close();
				} catch (Exception ex) {
					// do nothing
				}
			}
		}
	}
}
