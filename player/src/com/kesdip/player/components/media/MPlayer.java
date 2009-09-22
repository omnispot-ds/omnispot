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
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;
import com.kesdip.common.util.process.ProcessExitDetector;
import com.kesdip.common.util.process.ProcessExitListener;
import com.kesdip.common.util.process.ProcessOutputListener;
import com.kesdip.common.util.process.StreamLogger;
import com.kesdip.player.components.media.VideoConfiguration.Playlist;

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
public class MPlayer implements ProcessExitListener, ProcessOutputListener {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(MPlayer.class);

	/**
	 * The location of the normal MPlayer executable.
	 */
	private final String MPLAYER_EXE = System.getProperty("MPLAYER_EXE");

	/**
	 * The location of the TV-enabled MPlayer executable.
	 */
	private final String MPLAYER_TV_EXE = System.getProperty("MPLAYER_TV_EXE");

	/**
	 * Pattern for the video progress percent.
	 */
	private final Pattern VIDEO_POS_PATTERN = Pattern
			.compile("ANS_percent_pos\\=(\\d+?)");

	/**
	 * The MPlayer instance command-line input. Never use this property
	 * directly, as the process may be dead. Instead call
	 * {@link #getMPlayerIn()}.
	 */
	private PrintStream mplayerIn = null;

	/**
	 * The configuration.
	 */
	private MPlayerConfiguration config = null;

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
	static MPlayer getInstance(MPlayerConfiguration config) throws IOException {
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
			String cmdLine = createCommandLine();

			Process process = Runtime.getRuntime().exec(cmdLine);

			mplayerIn = new PrintStream(process.getOutputStream());
			ProcessExitDetector exitDetector = new ProcessExitDetector(process,
					config.getPlayerName());
			exitDetector.addProcessListener(this);
			exitDetector.start();
			new StreamLogger(config.getPlayerName(), process.getInputStream(),
					Level.INFO).start();
			new StreamLogger(config.getPlayerName(), process.getInputStream(),
					Level.WARN).start();

		}
		return mplayerIn;
	}

	/**
	 * @return String a command line created from the passed
	 *         {@link MPlayerConfiguration}.
	 */
	final String createCommandLine() {
		StringBuilder cmd = new StringBuilder();
		// different executable for TV
		if (config instanceof VideoConfiguration) {
			cmd.append(MPLAYER_EXE);
		} else {
			cmd.append(MPLAYER_TV_EXE);
		}
		// always slave process
		cmd.append(" -slave");
		if (!config.isFullScreen()) {
			// idle after finish; never for fullscreen
			cmd.append(" -idle");
			// stretch video/tv in window mode
			cmd.append(" -nokeepaspect");
		}
		// fullscreen or native control with colorkey
		if (!config.isFullScreen()) {
			cmd.append(" -wid ").append(config.getWindowId());
			cmd.append(" -colorkey ").append(
					StringUtils.toHexString(config.getColorKey()));
		} else {
			cmd.append(" -fs");
		}
		if (config instanceof VideoConfiguration) {
			// add files to the queue, if any
			VideoConfiguration video = (VideoConfiguration) config;
			// loop
			if (video.isLoop()) {
				cmd.append(" -loop 0");
			}
			if (video.getPlaylists().size() > 0) {
				cmd.append(' ').append(createPlaylists(video));
			} else {
				cmd.append(" foo.avi");
			}
		} else if (config instanceof AnalogTVConfiguration) {
			AnalogTVConfiguration analogTv = (AnalogTVConfiguration) config;
			cmd.append(" tv://").append(analogTv.getChannel());
		} else if (config instanceof DVBTConfiguration) {
			DVBTConfiguration dvbt = (DVBTConfiguration) config;
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
	 * Pause playback.
	 * 
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void pause() {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'pause' is only allowed for video playback");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("pause\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error pausing player", e);
		}
	}

	/**
	 * Does not interrupt playback and adds a file to the player's internal
	 * playlist.
	 * 
	 * @param fileName
	 *            full path to the file to add
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void addFile(String fileName) {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'addFile' is only allowed for video playback");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("loadfile \"" + fileName + "\" 1\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error playing file", e);
		}
	}

	/**
	 * Interrupts playback, clears the player's internal playlist and plays the
	 * file. If looping is not enabled, the player will show nothing upon
	 * completion of playback.
	 * 
	 * @param fileName
	 *            full path to the file to play
	 * @throws IllegalStateException
	 *             if this is not a video player
	 */
	public void playFile(String fileName) {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'playFile' is only allowed for video playback");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("loadfile \"" + fileName + "\" 0\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error playing file", e);
		}
	}

	/**
	 * Ask the MPlayer for the progress of the playback. It will cause a call to
	 * {@link MPlayerEventListener#playbackCompleted()}, if the current movie
	 * has completed.
	 */
	public void pollProgress() {
		if (!(config instanceof VideoConfiguration)) {
			throw new IllegalStateException(
					"'pollProgress' is only allowed for video playback");
		}
		try {
			PrintStream in = getMPlayerIn();
			in.print("get_property percent_pos\n\n");
			in.flush();
		} catch (IOException e) {
			logger.error("Error polling progress", e);
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
	 * @return boolean <code>true</code> if this is a progress line
	 * @see com.kesdip.common.util.process.ProcessOutputListener#canProcessLine(java.lang.String)
	 */
	@Override
	public boolean canProcessLine(String line) {
		return VIDEO_POS_PATTERN.matcher(line).matches();
	}

	/**
	 * @param line
	 *            the line to read the progress percentage from
	 * @see com.kesdip.common.util.process.ProcessOutputListener#processLine(java.lang.String)
	 */
	@Override
	public void processLine(String line) {
		Matcher matcher = VIDEO_POS_PATTERN.matcher(line);
		matcher.matches();
		int progress = Integer.valueOf(matcher.group(1));
		if (progress == 100 && !config.getListeners().isEmpty()) {
			for (MPlayerEventListener listener : config.getListeners()) {
				listener.playbackCompleted(config.getPlayerName());
			}
		}
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
			cmd.append(' ').append(createPlaylist(playlist));
		}
		return cmd.toString();
	}

	/**
	 * Creates a temporary playlist file (containing one media file per line)
	 * with the contents of the given playlist.
	 * 
	 * @param playlist
	 *            the playlist to use
	 * @return String the playlist string with all playback hints or an empty
	 *         string in case of error
	 */
	private final String createPlaylist(Playlist playlist) {
		PrintStream out = null;
		File file = null;
		try {
			file = FileUtils.createUniqueFile(config.getPlayerName(), true);
			out = new PrintStream(file);
			String normalizedName = null;
			for (String fileName : playlist.getFileList()) {
				normalizedName = fileName.replace('/', File.separatorChar);
				out.print(normalizedName + "\n");
			}
			out.flush();
			return "-playlist " + file.getCanonicalPath()
					+ (playlist.isFullScreen() ? " -fs" : "");
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
}
