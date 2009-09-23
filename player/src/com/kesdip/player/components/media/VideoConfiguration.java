/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration settings for a video rendering {@link MPlayer}.
 * 
 * @author gerogias
 */
public class VideoConfiguration extends MPlayerConfiguration {

	/**
	 * Serialization id.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The list of playlists to play. In full-screen mode there must be at least
	 * one file defined.
	 */
	private Map<String, Playlist> playlists = null;

	/**
	 * Loop the file list?
	 */
	private boolean loop = false;

	/**
	 * Default constructor.
	 */
	public VideoConfiguration() {
		playlists = new HashMap<String, Playlist>();
		setPlayerName("video");
	}

	/**
	 * @param playlist
	 *            the playlist to add
	 */
	public void addPlaylist(Playlist playlist) {
		playlists.put(playlist.getName(), playlist);
	}

	/**
	 * @param playlist
	 *            to remove
	 */
	public void removePlaylist(Playlist playlist) {
		playlists.remove(playlist.getName());
	}
	
	/**
	 * Remove all playlists.
	 */
	public void clearPlaylists() {
		playlists.clear();
	}

	/**
	 * @return List unmodifiable list
	 */
	public List<Playlist> getPlaylists() {
		return Collections.unmodifiableList(new ArrayList<Playlist>(playlists
				.values()));
	}

	/**
	 * @return {@link MPlayerConfiguration} a video config instance
	 * @see com.kesdip.player.components.media.MPlayerConfiguration#clone()
	 */
	@Override
	public MPlayerConfiguration clone() {
		VideoConfiguration config = new VideoConfiguration();
		updateClone(config);
		config.loop = this.loop;
		config.playlists.putAll(this.playlists);
		return config;
	}

	/**
	 * @return the loop
	 */
	public boolean isLoop() {
		return loop;
	}

	/**
	 * @param loop
	 *            the loop to set
	 */
	public void setLoop(boolean loop) {
		this.loop = loop;
	}

	/**
	 * @see com.kesdip.player.components.media.MPlayerConfiguration#isValid()
	 */
	@Override
	public boolean isValid() {
		boolean res = super.isValid();
		if (!res) {
			return false;
		}
		if (isFullScreen() && playlists.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Represents a playlist of files. A playlist is composed of a list of files
	 * and playback hints. The supported hints are
	 * <ul>
	 * <li>fullScreen</li>
	 * </ul>
	 * 
	 * @author gerogias
	 */
	public static final class Playlist implements Serializable {

		/**
		 * Serialization version.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * An identifier for the playlist.
		 */
		private String name = null;

		/**
		 * Is fullscreen playback?
		 */
		private boolean fullScreen = false;

		/**
		 * List of files to play.
		 */
		private List<String> fileList = null;

		/**
		 * Constructor.
		 * 
		 * @param name
		 *            the name of the playlist
		 */
		public Playlist(String name) {
			this.name = name;
			fileList = new ArrayList<String>();
		}

		/**
		 * @return the fullScreen
		 */
		public boolean isFullScreen() {
			return fullScreen;
		}

		/**
		 * @param fullScreen
		 *            the fullScreen to set
		 */
		public void setFullScreen(boolean fullScreen) {
			this.fullScreen = fullScreen;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @param fileName
		 *            to add
		 */
		public void addFile(String fileName) {
			if (!fileList.contains(fileName)) {
				fileList.add(fileName);
			}
		}

		/**
		 * @param fileName
		 *            to remove
		 */
		public void removeFile(String fileName) {
			fileList.remove(fileName);
		}

		/**
		 * @return String[] the file list
		 */
		public String[] getFileList() {
			String[] files = new String[fileList.size()];
			return fileList.toArray(files);
		}
	}
}
