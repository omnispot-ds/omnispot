/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.kesdip.common.util.StringUtils;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.PlayerUtils;

/**
 * Represents a component that renders video files (through the VLC client
 * interface).
 * 
 * @author Pafsanias Ftakas
 * @author Stelios Gerogiannakis
 */
public class Video extends AbstractVideo implements InitializingBean {
	private static final Logger logger = Logger.getLogger(Video.class);

	/* SPRING STATE */
	private List<Resource> contents;
	private boolean repeat = false;
	/**
	 * Flag to indicate we have a scheduled resource.
	 */
	protected boolean scheduledResource = false;
	/**
	 * The different playlists of the resources which have not been scheduled.
	 */
	private List<List<Resource>> playlists = null;
	/**
	 * The index of the currently playing playlist in <code>playlists</code>.
	 */
	private int currentPlaylistIndex = -1;

	/**
	 * Default constructor.
	 */
	public Video() {
		playlists = new ArrayList<List<Resource>>();
	}

	public void setContents(List<Resource> content) {
		this.contents = content;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (contents == null || contents.size() == 0) {
			throw new Exception("Video component must have a non-empty "
					+ "contents property.");
		}
	}

	/* TRANSIENT STATE */
	protected boolean stillStarting;

	/**
	 * Loads the given resource in the player and starts playing.
	 * <p>
	 * If the resource is <code>null</code>, it is interpreted as
	 * "load all the items in the resource list".
	 * </p>
	 * 
	 * @param resource
	 *            the resource or <code>null</code>
	 * @throws Exception
	 *             on error
	 */
	protected void loadMedia(Resource resource) throws Exception {
		// populate the internal playlist structure if necessary
		if (playlists.isEmpty()) {
			populatePlaylists();
		}

		if (resource != null) {
			// most probably a scheduled resource
			logger.trace("Loading a single video");
			addResourceToVlcPlaylist(resource);
			// start
			libVlc.libvlc_playlist_play(libvlc_instance_t, -1, 0, null,
					exception);
			assertOnException("loadMedia.libvlc_playlist_play");
			stillStarting = true;
		} else {
			// check if we should loop
			if (currentPlaylistIndex >= playlists.size() - 1) {
				currentPlaylistIndex = -1;
			}
			// increase current index
			currentPlaylistIndex++;
			if (logger.isTraceEnabled()) {
				logger.trace("Current playlist index: " + currentPlaylistIndex);
			}
			// this is definitely not a scheduled resource
			scheduledResource = false;
			if (currentPlaylistIndex < playlists.size()) {
				// populate VLC playlist
				addResourcesToVlcPlaylist(playlists.get(currentPlaylistIndex));
				// start
				libVlc.libvlc_playlist_play(libvlc_instance_t, -1, 0, null,
						exception);
				assertOnException("loadMedia.libvlc_playlist_play");
				stillStarting = true;
			}
		}
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		super.init(parent, timingMonitor, player);

		// populate the internal playlist structure if necessary
		if (playlists.isEmpty()) {
			populatePlaylists();
		}

		try {
			startVideo(null);

			scheduleResources(timingMonitor, contents);
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}

	protected void startVideo(Resource resource) throws Exception {
		// clear playlist
		// clearVlcPlaylist();
		releaseResources();
		// check if video or next playlist are marked as full-screen and
		// initialize accordingly
		initVLC(decideFullScreenStatus(resource));
		loadMedia(resource);
		completed = new AtomicBoolean(false);
	}

	private AtomicBoolean completed;

	@Override
	public void repaint() throws ComponentException {
		super.repaint();

		try {
			boolean playing = isPlaying();
			if (stillStarting) {
				// playing or a finished scheduled resource
				if (playing || scheduledResource) {
					stillStarting = false;
				}
			} else if (!playing) {
				// we can only complete if no repeat and this was not a
				// scheduled resource
				if (!repeat && !scheduledResource) {
					completed.set(!playing);
				} else {
					logger.info("Starting video sequence.");
					firstTime = true;
					startVideo(null);
				}
			}

			if (completed.get()) {
				logger.debug("The video component has completed.");
			}
		} catch (Exception e) {
			throw new ComponentException("Unable to query playlist status", e);
		}
	}

	@Override
	public CompletionStatus isComplete() {
		if (repeat) {
			return super.isComplete();
		}

		if (completed != null && completed.get()) {
			return CompletionStatus.COMPLETE;
		} else {
			return CompletionStatus.INCOMPLETE;
		}
	}

	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.addAll(contents);
		return retVal;
	}

	@Override
	public synchronized void runResource(Resource resource) {
		if (!contents.contains(resource)) {
			throw new RuntimeException("Resource: " + resource.getIdentifier()
					+ " not in the contents of the video component.");
		}

		try {
			if (logger.isInfoEnabled()) {
				logger.info("Starting scheduled video from resource: "
						+ resource.getIdentifier());
			}
			scheduledResource = true;
			startVideo(resource);
		} catch (Exception e) {
			logger.error("Unable to reschedule video", e);
		}
	}

	/**
	 * Check if the video player is playing at the moment.
	 * 
	 * @return boolean <code>true</code> if the media player is playing
	 */
	protected boolean isPlaying() throws Exception {
		int v = 0;
		if (libVlc != null) {
			v = libVlc.libvlc_playlist_isplaying(libvlc_instance_t, exception);
		}
		assertOnException("isPlaying.libvlc_playlist_isplaying");
		return v == 1;
	}

	/**
	 * @return the scheduledResource
	 */
	public boolean isScheduledResource() {
		return scheduledResource;
	}

	/**
	 * @param scheduledResource
	 *            the scheduledResource to set
	 */
	public void setScheduledResource(boolean scheduledResource) {
		this.scheduledResource = scheduledResource;
	}

	/**
	 * Populate the internal <code>playlists</code> structure from the defined
	 * <code>contents</code>.
	 * <p>
	 * While iterating the contents, whenever a fullscreen flag changes value
	 * between 2 consequent {@link Resources}, it is a signal for a new sublist
	 * inside <code>playlists</code>.
	 * </p>
	 * <p>
	 * Resources with a CRON expression are ignored.
	 * </p>
	 */
	protected void populatePlaylists() {
		Resource previousRes = null;
		List<Resource> subList = null;
		for (Resource res : contents) {
			// do not put in the lists videos with a CRON expression
			if (!StringUtils.isEmpty(res.getCronExpression())) {
				continue;
			}
			// change of flag -> new sublist
			if (previousRes == null
					|| PlayerUtils.isResourceFullScreen(res) != PlayerUtils
							.isResourceFullScreen(previousRes)) {
				subList = new ArrayList<Resource>();
				playlists.add(subList);
			}
			subList.add(res);
			previousRes = res;
		}
	}

	/**
	 * Utility method to decide how VLC should start (f-s or not).
	 * <p>
	 * If <code>resource != null</code>, it checks the resource's attributes.
	 * </p>
	 * Otherwise, it peeks into <code>playlists[currentPlaylistIndex + 1]</code>
	 * to see if it contains fullscreen videos.
	 * <p>
	 * If <code>currentPlaylistIndex == playlists.length - 1</code>, then
	 * <code>currentPlaylistIndex = 0</code>.
	 * </p>
	 * 
	 * @return boolean <code>true</code> if it contains fullscreen videos
	 */
	private boolean decideFullScreenStatus(Resource resource) {
		if (resource != null) {
			return PlayerUtils.isResourceFullScreen(resource);
		}
		// sanity check in case this is the last item in the list
		int indexToUse = currentPlaylistIndex < playlists.size() - 1 ? currentPlaylistIndex + 1
				: 0;
		List<Resource> subList = playlists.get(indexToUse);
		// just check the 1st one, they are all the same
		return PlayerUtils.isResourceFullScreen(subList.get(0));
	}
}
