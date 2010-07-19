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
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.kesdip.common.util.FileUtils;
import com.kesdip.common.util.StringUtils;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.components.Component;
import com.kesdip.player.components.ComponentException;
import com.kesdip.player.components.Resource;
import com.kesdip.player.components.media.VideoConfiguration.Playlist;
import com.kesdip.player.constenum.VideoQualityTypes;
import com.kesdip.player.helpers.PlayerUtils;

/**
 * Renders video files through MPlayer.
 * 
 * @author gerogias
 */
public class FileVideo extends AbstractMPlayerVideo implements
		InitializingBean, MPlayerEventListener {

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
	 * Play content in high/normal quality.
	 */
	private String quality = VideoQualityTypes.NORMAL;

	/**
	 * Timer task to poll the full-screen status of the player.
	 */
	private Timer fullScreenPollingTimer = null;

	/**
	 * @see com.kesdip.player.components.media.AbstractMPlayerVideo#getPlayerConfiguration()
	 */
	@Override
	protected MPlayerConfiguration getPlayerConfiguration() {
		VideoConfiguration config = new VideoConfiguration();
		config.setPlayerExecutable(FileUtils.getNativePathName(Player
				.getMPlayerFile()));
		config.setPlayerName(getPlayerName());
		config.setColorKey(getWindowComponent().getBackground());
		config.setFullScreen(false);
		config.setLoop(repeat);
		config.setWindowId(com.sun.jna.Native
				.getComponentID(getWindowComponent()));
		config.setQuality(quality);
		config.addListener(this);
		config.setExtraArgs(StringUtils.toArgArray(getExtraArgs(), "\\|"));
		// split resources into playlists
		preparePlaylists(config);
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
		return getMPlayer().isPlaybackCompleted() ? CompletionStatus.COMPLETE
				: CompletionStatus.INCOMPLETE;
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
			String videoFilename = getResourcePath(resource);
			boolean fullScreen = PlayerUtils.isResourceFullScreen(resource);
			getMPlayer().playFile(videoFilename, fullScreen);
		} catch (Exception e) {
			logger.error("Unable to play scheduled video", e);
		}
	}

	/**
	 * Populate the internal <code>playlists</code> structure from the defined
	 * <code>contents</code>.
	 * <p>
	 * While iterating the contents, whenever a fullscreen flag changes value
	 * between 2 consequent {@link Resource}s, it is a signal for a new sublist
	 * inside <code>playlists</code>.
	 * </p>
	 * <p>
	 * Resources with a CRON expression are ignored.
	 * </p>
	 * 
	 * @param config
	 *            the configuration object to populate
	 */
	protected void preparePlaylists(VideoConfiguration config) {
		logger.info("Preparing playlists");
		Resource previousRes = null;
		Playlist playlist = null;
		int count = 1;
		for (Resource res : contents) {
			// do not put in the lists videos with a CRON expression
			if (!StringUtils.isEmpty(res.getCronExpression())) {
				continue;
			}
			boolean resFs = PlayerUtils.isResourceFullScreen(res);
			boolean prevResFs = PlayerUtils.isResourceFullScreen(previousRes);
			// change of flag -> new list
			if (previousRes == null || resFs != prevResFs) {
				String playlistId = (super.id != null ? super.id : "Playlist")
						+ '_' + count++;
				playlist = new Playlist(playlistId);
				playlist.setFullScreen(resFs);
				config.addPlaylist(playlist);
			}
			if (logger.isDebugEnabled()) {
				logger
						.debug("Adding video to playlist: "
								+ res.getIdentifier());
			}
			playlist.addFile(getResourcePath(res));
			previousRes = res;
		}
	}

	/**
	 * Schedule all resources with a CRON expression.
	 * 
	 * @see com.kesdip.player.components.media.AbstractMPlayerVideo#init(com.kesdip.player.components.Component,
	 *      com.kesdip.player.TimingMonitor, com.kesdip.player.Player)
	 */
	@Override
	public void init(Component parent, TimingMonitor timingMonitor,
			Player player) throws ComponentException {
		super.init(parent, timingMonitor, player);
		try {
			scheduleResources(timingMonitor, contents);
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
		// see Bug#150
		fullScreenPollingTimer = new Timer(getPlayerName());
		fullScreenPollingTimer.scheduleAtFixedRate(new MPlayerPoller(), 2, 500);
	}

	/**
	 * @return the repeat
	 */
	public boolean isRepeat() {
		return repeat;
	}

	/**
	 * @param repeat
	 *            the repeat to set
	 */
	public void setRepeat(boolean repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the contents
	 */
	public List<Resource> getContents() {
		return contents;
	}

	/**
	 * @param contents
	 *            the contents to set
	 */
	public void setContents(List<Resource> contents) {
		this.contents = contents;
	}

	/**
	 * @return the quality
	 */
	public String getQuality() {
		return quality;
	}

	/**
	 * @param quality
	 *            the quality to set
	 */
	public void setQuality(String quality) {
		this.quality = quality;
	}

	/**
	 * Toggle the background frame front and back in case there is a full-screen
	 * video playing.
	 * 
	 * @see com.kesdip.player.components.media.MPlayerEventListener#fullScreenStatusChanged(java.lang.String,
	 *      boolean)
	 * @see Bug#150
	 */
	@Override
	public void fullScreenStatusChanged(String playerName, boolean newStatus) {
		if (newStatus) {
			this.player.hideActiveLayout();
			System.out.println("-------------Hiding layout");
		} else {
			this.player.unhideActiveLayout();
			System.out.println("-------------Showing layout");
		}
	}

	/**
	 * Not implemented.
	 * 
	 * @see com.kesdip.player.components.media.MPlayerEventListener#playbackCompleted(java.lang.String)
	 */
	@Override
	public void playbackCompleted(String playerName) {
		// We are not interested in this method
	}

	/**
	 * Terminates the full-screen polling thread.
	 * 
	 * @see com.kesdip.player.components.media.AbstractMPlayerVideo#releaseResources()
	 */
	@Override
	public void releaseResources() {
		super.releaseResources();

		if (fullScreenPollingTimer != null) {
			fullScreenPollingTimer.cancel();
		}
		// just in case we finished with the layout hidden
		super.player.unhideActiveLayout();
	}

	/**
	 * Performs polling of the MPlayer instance for changes in the full-screen
	 * status.
	 * 
	 * @author gerogias
	 * @see Bug#150
	 */
	private final class MPlayerPoller extends TimerTask {

		/**
		 * @see java.util.TimerTask#run()
		 */
		@Override
		public void run() {
			MPlayer player = FileVideo.this.getMPlayer();
			if (player != null) {
				player.pollFullScreen();
			}
		}

	}

	/**
	 * @return String the player's name
	 */
	private final String getPlayerName() {
		return super.id != null ? super.id : "FileVideo";
	}
}
