/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

/**
 * Listener for {@link MPlayer} events.
 * 
 * @author gerogias
 */
public interface MPlayerEventListener {

	/**
	 * Playback of the current item has completed.
	 * 
	 * @param playerName
	 *            name of the player
	 */
	void playbackCompleted(String playerName);

	/**
	 * Player has switched to fullscreen/normal mode.
	 * 
	 * @param playerName
	 *            name of the player
	 * @param newStatus
	 *            the new status; <code>true</code> means the player is now in
	 *            fullscreen
	 */
	void fullScreenStatusChanged(String playerName, boolean newStatus);
}
