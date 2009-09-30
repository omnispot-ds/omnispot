/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.kesdip.common.util.StringUtils;

/**
 * DTO to configure an {@link MPlayer} instance.
 * <p>
 * Once the object is passed to a {@link MPlayer} instance, a clone is created.
 * </p>
 * 
 * @author gerogias
 */
public abstract class MPlayerConfiguration implements Serializable {

	/**
	 * Serialization version.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Protected constructor.
	 */
	protected MPlayerConfiguration() {
		// do nothing
	}

	/**
	 * Is the player full-screen?
	 */
	private boolean fullScreen = false;

	/**
	 * The native component id into which to render the content. Ignored if
	 * {@link #fullScreen} is <code>true</code>.
	 */
	private long windowId = -1;

	/**
	 * The color onto which to render content, default is {@link Color#BLACK}.
	 * Ignored if {@link #fullScreen} is <code>true</code>.
	 */
	private Color colorKey = Color.BLACK;

	/**
	 * An identifying playerName for this instance, used in logging etc.
	 */
	private String playerName = "mplayer";

	/**
	 * The absolute path to the player.
	 */
	private String playerExecutable = null;
	
	/**
	 * The listeners for playbqack events.
	 */
	private List<MPlayerEventListener> listeners = new ArrayList<MPlayerEventListener>();

	/**
	 * @return the fullScreen
	 */
	public boolean isFullScreen() {
		return fullScreen;
	}

	/**
	 * Is the player full-screen?
	 * 
	 * @param fullScreen
	 *            the fullScreen to set
	 */
	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}

	/**
	 * @return the windowId
	 */
	public long getWindowId() {
		return windowId;
	}

	/**
	 * The native component id into which to render the content. Ignored if
	 * {@link #fullScreen} is <code>true</code>.
	 * 
	 * @param windowId
	 *            the windowId to set
	 */
	public void setWindowId(long windowId) {
		this.windowId = windowId;
	}

	/**
	 * @return the colorKey
	 */
	public Color getColorKey() {
		return colorKey;
	}

	/**
	 * The color onto which to render content, default is {@link Color#BLACK}.
	 * Ignored if {@link #fullScreen} is <code>true</code>.
	 * 
	 * @param colorKey
	 *            the colorKey to set
	 */
	public void setColorKey(Color colorKey) {
		this.colorKey = colorKey;
	}

	/**
	 * Utility method for descendants to populate their clones with the fields
	 * defined in the parent.
	 * 
	 * @param clone
	 *            the clone to update
	 */
	protected void updateClone(MPlayerConfiguration clone) {
		clone.colorKey = this.colorKey;
		clone.fullScreen = this.fullScreen;
		clone.windowId = this.windowId;
		clone.playerName = this.playerName;
		clone.playerExecutable = this.playerExecutable;
	}

	/**
	 * Template metod for descendants.
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public abstract MPlayerConfiguration clone();

	/**
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * @param playerName
	 *            the playerName to set
	 */
	public void setPlayerName(String name) {
		this.playerName = name;
	}

	/**
	 * Adds a listener to the list.
	 * 
	 * @param listener
	 *            to add
	 */
	public void addListener(MPlayerEventListener listener) {
		if (listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	/**
	 * @param listener
	 *            to remove
	 */
	public void removeListener(MPlayerEventListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return boolean <code>true</code> if the values actually make sense
	 */
	public boolean isValid() {
		if (!fullScreen && windowId < 0) {
			return false;
		}
		if (StringUtils.isEmpty(playerName)) {
			return false;
		}
		if (colorKey == null) {
			return false;
		}
		return true;
	}

	/**
	 * @return the listeners
	 */
	public List<MPlayerEventListener> getListeners() {
		return listeners;
	}

	/**
	 * @return the playerExecutable
	 */
	public String getPlayerExecutable() {
		return playerExecutable;
	}

	/**
	 * @param playerExecutable the playerExecutable to set
	 */
	public void setPlayerExecutable(String playerExecutable) {
		this.playerExecutable = playerExecutable;
	}

}
