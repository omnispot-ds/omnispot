/*
 * Disclaimer:
 * Copyright 2008 - Ke.S.Di.P. E.P.E - All rights reserved.
 * eof Disclaimer
 */
package com.kesdip.player.components;

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

	public void setContents(List<Resource> content) {
		this.contents = content;
	}

	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (contents == null || contents.size() == 0)
			throw new Exception("Video component must have a non-empty "
					+ "contents property.");
	}

	/* TRANSIENT STATE */
	protected boolean stillStarting;
	protected boolean currentRepeat;

	/**
	 * Loads the given resource in the player and starts playing.
	 * <p>
	 * If the resource is <code>null</code>, it is interpreted as
	 * "load all the items in the resource list".
	 * </p>
	 * 
	 * @param resource
	 *            the resource or <code>null</code>
	 * @param actualRepeat
	 *            if the content should loop or not
	 * @throws Exception
	 *             on error
	 */
	protected void loadMedia(Resource resource, boolean actualRepeat)
			throws Exception {
		// clear playlist
		clearPlaylist();

		if (resource != null) {
			logger.trace("Loading a single video");
			addResourceToPlaylist(resource);

		} else {
			logger.trace("Loading a video playlist");
			// this is definitely not a scheduled resource
			scheduledResource = false;
			for (Resource res : contents) {
				// do not put in the list videos with a CRON expression
				if (!StringUtils.isEmpty(res.getCronExpression())) {
					continue;
				}
				addResourceToPlaylist(res);
			}
		}
		if (actualRepeat) {
			libVlc.libvlc_playlist_loop(libvlc_instance_t, 1, exception);
			assertOnException("loadMedia.libvlc_playlist_loop");
		} else {
			libVlc.libvlc_playlist_play(libvlc_instance_t, -1, 0, null,
					exception);
			assertOnException("loadMedia.libvlc_playlist_play");
		}
		stillStarting = true;
	}

	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		super.init(parent, timingMonitor, player);

		try {
			startVideo(null, repeat);

			scheduleResources(timingMonitor, contents);
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}

	protected void startVideo(Resource resource, boolean actualRepeat)
			throws Exception {
		releaseResources();
		// check if video is marked as full-screen and initialize accordingly
		initVLC(PlayerUtils.isResourceFullScreen(resource));
		loadMedia(resource, actualRepeat);
		currentRepeat = actualRepeat;
		completed = new AtomicBoolean(false);
	}

	private AtomicBoolean completed;

	@Override
	public void repaint() throws ComponentException {
		super.repaint();

		if (!currentRepeat) {
			try {
				boolean playing = isPlaying();
				if (stillStarting) {
					// playing or a finished scheduled resource
					if (playing || scheduledResource) {
						stillStarting = false;
					}
				} else if (!playing) {
					if (!repeat) {
						completed.set(!playing);
					} else {
						logger.info("Scheduled video completed, restarting "
								+ "normal video sequence.");
						firstTime = true;
						startVideo(null, repeat);
					}
				}

				if (completed.get()) {
					logger.debug("The video component has completed.");
				}
			} catch (Exception e) {
				throw new ComponentException("Unable to query playlist status",
						e);
			}
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
			logger.info("Starting scheduled video from resource: "
					+ resource.getIdentifier());
			scheduledResource = true;
			startVideo(resource, false);
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
}
