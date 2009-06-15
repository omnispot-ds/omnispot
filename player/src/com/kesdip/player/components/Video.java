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
import com.kesdip.player.registry.ContentRegistry;

/**
 * Represents a component that renders a video (through the VLC client
 * interface).
 * 
 * @author Pafsanias Ftakas
 */
public class Video extends AbstractVideo implements InitializingBean {
	private static final Logger logger = Logger.getLogger(Video.class);

	/* SPRING STATE */
	private List<Resource> contents;
	private boolean repeat = false;

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

	protected void loadMedia(Resource resource, boolean actualRepeat)
			throws Exception {
		// clear playlist
		clearPlaylist();

		ContentRegistry registry = ContentRegistry.getContentRegistry();
		if (resource != null) {
			logger.trace("Loading a single video");
			String videoFilename = registry.getResourcePath(resource, true);

			libVlcClass.getMethod("libvlc_playlist_add", libVlcInstanceClass,
					String.class, String.class, libVlcExceptionClass).invoke(
					libVlc, libvlc_instance_t, videoFilename, null, exception);
			assertOnException("loadMedia.libvlc_playlist_add");

		} else {
			logger.trace("Loading a video playlist");
			for (Resource res : contents) {
				// do not put in the list videos with a CRON expression
				if (!StringUtils.isEmpty(res.getCronExpression())) {
					continue;
				}
				String videoFilename = registry.getResourcePath(res, true);

				libVlcClass.getMethod("libvlc_playlist_add",
						libVlcInstanceClass, String.class, String.class,
						libVlcExceptionClass).invoke(libVlc, libvlc_instance_t,
						videoFilename, null, exception);
				assertOnException("loadMedia.libvlc_playlist_add");
			}
		}

		if (actualRepeat) {
			libVlcClass.getMethod("libvlc_playlist_loop", libVlcInstanceClass,
					int.class, libVlcExceptionClass).invoke(libVlc,
					libvlc_instance_t, 1, exception);
			assertOnException("loadMedia.libvlc_playlist_loop");
		} else {
			libVlcClass.getMethod("libvlc_playlist_play", libVlcInstanceClass,
					int.class, int.class, String[].class, libVlcExceptionClass)
					.invoke(libVlc, libvlc_instance_t, -1, 0, null, exception);
			assertOnException("loadMedia.libvlc_playlist_play");
		}
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
		// check if video is marked as full-screen and initialize accordingly
		initVLC(PlayerUtils.isResourceFullScreen(resource));
		stillStarting = true;
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
				int v = ((Integer) libVlcClass.getMethod(
						"libvlc_playlist_isplaying", libVlcInstanceClass,
						libVlcExceptionClass).invoke(libVlc, libvlc_instance_t,
						exception)).intValue();
				assertOnException("repaint.libvlc_playlist_isplaying");
				if (stillStarting) {
					if (v == 1) {
						stillStarting = false;
					}
				} else {
					if (!repeat) {
						completed.set(v == 0);
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
			releaseResources();

			logger.info("Starting scheduled video from resource: "
					+ resource.getIdentifier());

			startVideo(resource, false);
		} catch (Exception e) {
			logger.error("Unable to reschedule video", e);
		}
	}

}
