/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 27 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.helpers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.kesdip.player.Player;

/**
 * Listener for player window mouse events. Used to exit the player via specific
 * click combinations.
 * 
 * @author gerogias
 */
public class ExitMouseListener extends MouseAdapter {

	/**
	 * The player for which to listen events.
	 */
	private Player player;

	/**
	 * Constructor.
	 * 
	 * @param player
	 *            the parent player
	 */
	public ExitMouseListener(Player player) {
		this.player = player;
	}

	/**
	 * Terminate the player on double click.
	 * 
	 * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent event) {
		if (event.getClickCount() > 1) {
			player.stopPlaying();
			if (player.getClass().getName().equals(Player.class.getName())) {
				System.exit(0);
			}
		}
	}

}
