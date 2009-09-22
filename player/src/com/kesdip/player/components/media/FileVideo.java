/*
 * Disclaimer:
 * Copyright 2008 - KESDIP E.P.E & Stelios Gerogiannakis - All rights reserved.
 * eof Disclaimer
 * 
 * Date: 22 Σεπ 2009
 * @author <a href="mailto:sgerogia@gmail.com">Stelios Gerogiannakis</a>
 */

package com.kesdip.player.components.media;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.components.Resource;
import com.kesdip.player.registry.ContentRegistry;

/**
 * Renders video files through MPlayer.
 * 
 * @author gerogias
 */
public class FileVideo extends AbstractMPlayerVideo implements InitializingBean {

	/**
	 * The logger.
	 */
	private static final Logger logger = Logger.getLogger(FileVideo.class);

	/**
	 * The files to play.
	 */
	private List<Resource> contents = null;

	/**
	 * If the file list should be repeated.
	 */
	private boolean repeat = false;

	/**
	 * @see com.kesdip.player.components.media.AbstractMPlayerVideo#getPlayerConfiguration()
	 */
	@Override
	protected MPlayerConfiguration getPlayerConfiguration() {
		VideoConfiguration config = new VideoConfiguration();
		config.setPlayerName(super.id);
		config.setColorKey(getWindowComponent().getBackground());
		config.setFullScreen(false);
		config.setLoop(repeat);
		config.setWindowId(com.sun.jna.Native.getComponentID(getWindowComponent()));
		return config;
	}

	/**
	 * Check that at least one resource has been specified.
	 * 
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		if (contents == null || contents.size() == 0) {
			throw new Exception("FileVideo component must have a non-empty "
					+ "contents property.");
		}
	}

	/**
	 * @return Set the resource list as a set
	 * @see com.kesdip.player.components.AbstractComponent#gatherResources()
	 */
	@Override
	public Set<Resource> gatherResources() {
		HashSet<Resource> retVal = new HashSet<Resource>();
		retVal.addAll(contents);
		return retVal;
	}

	/**
	 * @see com.kesdip.player.components.AbstractComponent#isComplete()
	 */
	@Override
	public CompletionStatus isComplete() {
		if (repeat) {
			return super.isComplete();
		}
		return CompletionStatus.COMPLETE;
//		if (completed != null && completed.get()) {
//			return CompletionStatus.COMPLETE;
//		} else {
//			return CompletionStatus.INCOMPLETE;
//		}
	}

	/**
	 * Run a scheduled resource.
	 * 
	 * @see com.kesdip.player.components.AbstractComponent#runResource(com.kesdip.player.components.Resource)
	 */
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
			ContentRegistry registry = ContentRegistry.getContentRegistry();
			String videoFilename = registry.getResourcePath(resource, true);

			getMPlayer().playFile(videoFilename);
		} catch (Exception e) {
			logger.error("Unable to reschedule video", e);
		}
	}

}
