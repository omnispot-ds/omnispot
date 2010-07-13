/*
 * Disclaimer:
 * Copyright 2008-2010 - Omni-Spot E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 13 Ιουλ 2010
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media.loglistener;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kesdip.common.util.BufferedLineReadListener;
import com.kesdip.player.components.media.MPlayerEventListener;

/**
 * Translates read log entries into {@link MPlayerEventListener} method calls
 * for video playback completion.
 * 
 * @author gerogias
 * 
 */
public class PlaybackCompletedLogReader implements BufferedLineReadListener {

	/**
	 * Pattern for the video progress percent.
	 */
	private final java.util.regex.Pattern VIDEO_POS_PATTERN = Pattern
			.compile("ANS_percent_pos\\=(\\d+?)");

	/**
	 * The event listeners.
	 */
	private Collection<MPlayerEventListener> listeners;
	/**
	 * The player name.
	 */
	private String playerName;

	/**
	 * Constructor.
	 * 
	 * @param listeners
	 *            the listeners to notify
	 * @param playerName
	 *            the player name to pass as argument
	 */
	public PlaybackCompletedLogReader(
			Collection<MPlayerEventListener> listeners, String playerName) {
		this.listeners = listeners;
		this.playerName = playerName;
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#canProcessLine(java.lang.String)
	 */
	@Override
	public boolean canProcessLine(String line) {

		return VIDEO_POS_PATTERN.matcher(line).matches();
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#processLine(java.lang.String)
	 */
	@Override
	public void processLine(String line) {
		Matcher matcher = VIDEO_POS_PATTERN.matcher(line);
		matcher.matches();
		int progress = Integer.valueOf(matcher.group(1));
		if (progress == 100 && !listeners.isEmpty()) {
			for (MPlayerEventListener listener : listeners) {
				listener.playbackCompleted(playerName);
			}
		}
	}

}
