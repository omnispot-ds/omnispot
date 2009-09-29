/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 15 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;

import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.components.AbstractComponent;
import com.kesdip.player.components.Component;
import com.kesdip.player.components.ComponentException;
import com.kesdip.player.helpers.PlayerUtils;

/**
 * Base class for all media-rendering components (TV, video) using MPlayer.
 * 
 * @author gerogias
 */
public abstract class AbstractMPlayerVideo extends AbstractComponent {

	/**
	 * The drawable canvas area.
	 */
	private Canvas canvas = null;

	/**
	 * The wrapped player instance.
	 */
	private MPlayer mPlayer = null;

	/**
	 * @throws UnsupportedOperationException
	 *             always
	 * @see com.kesdip.player.components.Component#add(com.kesdip.player.components.Component)
	 */
	@Override
	public void add(Component component) throws ComponentException {
		throw new UnsupportedOperationException(
				"Video component is not a container.");
	}

	/**
	 * @return Component the drawable canvas area
	 * @see com.kesdip.player.components.Component#getWindowComponent()
	 */
	@Override
	public java.awt.Component getWindowComponent() {
		return canvas;
	}

	/**
	 * Initialize the wrapped player instance, the canvas and add the canvas to
	 * the parent.
	 * 
	 * @param parent
	 *            the parent component
	 * @param timingMonitor
	 *            the global timing monitor
	 * @param player
	 *            the player instance
	 * @see com.kesdip.player.components.Component#init(com.kesdip.player.components.Component,
	 *      com.kesdip.player.TimingMonitor, com.kesdip.player.Player)
	 */
	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		setPlayer(player);

		try {
			canvas = new Canvas();
			canvas.setCursor(PlayerUtils.getNoCursor());
			canvas.setLocation(x, y);
			canvas.setSize(new Dimension(width, height));
			canvas.setPreferredSize(new Dimension(width, height));
			canvas.addKeyListener(PlayerUtils.getExitKeyListener(player));
			canvas.addMouseListener(PlayerUtils.getExitMouseListener(player));
			canvas.setBackground(Color.BLACK);

			parent.add(this);
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}

	/**
	 * @see com.kesdip.player.components.Component#repaint()
	 */
	@Override
	public void repaint() throws ComponentException {
		if (mPlayer == null) {
			try {
				mPlayer = MPlayer.getInstance(getPlayerConfiguration());
			} catch (IOException e) {
				throw new ComponentException("Error initializing MPlayer", e);
			}
		}
	}

	/**
	 * Template method for descendants.
	 * 
	 * @return {@link MPlayerConfiguration} to initialize the player instance
	 */
	protected abstract MPlayerConfiguration getPlayerConfiguration();

	/**
	 * Release the native MPlayer instance.
	 * 
	 * @see com.kesdip.player.components.AbstractComponent#releaseResources()
	 */
	@Override
	public void releaseResources() {
		super.releaseResources();
		if (mPlayer != null) {
			mPlayer.terminate();
			mPlayer = null;
		}
	}

	/**
	 * @return the mPlayer
	 */
	public MPlayer getMPlayer() {
		return mPlayer;
	}
}
