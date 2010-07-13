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
 * for change in the fullscreen status.
 * 
 * @author gerogias
 */
public class FullScreenLogReader implements BufferedLineReadListener {

	/**
	 * Pattern for the video progress percent.
	 */
	private final java.util.regex.Pattern VIDEO_FS_PATTERN = Pattern
			.compile("ANS_fullscreen\\=(\\w+?)");
	/**
	 * The event listeners.
	 */
	private Collection<MPlayerEventListener> listeners;
	/**
	 * The player name.
	 */
	private String playerName;
	/**
	 * The last known status for the player.
	 */
	private boolean fullScreenStatus = false;

	/**
	 * Constructor.
	 * 
	 * @param listeners
	 *            the listeners to notify
	 * @param playerName
	 *            the player name to pass as argument
	 */
	public FullScreenLogReader(Collection<MPlayerEventListener> listeners,
			String playerName) {
		this.listeners = listeners;
		this.playerName = playerName;
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#canProcessLine(java.lang.String)
	 */
	@Override
	public boolean canProcessLine(String line) {

		return VIDEO_FS_PATTERN.matcher(line).matches();
	}

	/**
	 * @see com.kesdip.common.util.BufferedLineReadListener#processLine(java.lang.String)
	 */
	@Override
	public void processLine(String line) {

		Matcher matcher = VIDEO_FS_PATTERN.matcher(line);
		matcher.matches();
		boolean status = "YES".equalsIgnoreCase(matcher.group(1));
		if (status != fullScreenStatus) {
			fullScreenStatus = status;
			if (!listeners.isEmpty()) {
				for (MPlayerEventListener listener : listeners) {
					listener.fullScreenStatusChanged(playerName, status);
				}
			}
		}
	}
}
