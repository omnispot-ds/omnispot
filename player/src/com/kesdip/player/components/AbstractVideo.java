package com.kesdip.player.components;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.videolan.jvlc.internal.LibVlc;
import org.videolan.jvlc.internal.LibVlc.LibVlcInstance;
import org.videolan.jvlc.internal.LibVlc.LibVlcLog;
import org.videolan.jvlc.internal.LibVlc.LibVlcLogIterator;
import org.videolan.jvlc.internal.LibVlc.libvlc_exception_t;
import org.videolan.jvlc.internal.LibVlc.libvlc_log_message_t;

import com.kesdip.common.util.ProcessUtils;
import com.kesdip.player.Player;
import com.kesdip.player.TimingMonitor;
import com.kesdip.player.DeploymentLayout.CompletionStatus;
import com.kesdip.player.helpers.PlayerUtils;
import com.kesdip.player.registry.ContentRegistry;

/**
 * Base class for all components rendering media through the VLC client
 * interface.
 * 
 * @author Pafsanias Ftakas
 * @author Stelios Gerogiannakis
 */
public abstract class AbstractVideo extends AbstractComponent {
	/**
	 * Class logger.
	 */
	private static final Logger logger = Logger.getLogger(AbstractVideo.class);
	/**
	 * VLC native message logger.
	 */
	private static final Logger vlcLogger = Logger.getLogger(LibVlc.class);
	/**
	 * Mutex for VLC initialization.
	 */
	private Object mutex = new Object();
	
	/* TRANSIENT STATE */
	protected LibVlc libVlc;
	protected libvlc_exception_t exception;
	protected LibVlcInstance libvlc_instance_t;
	protected LibVlcLog libvlc_log;
	protected Canvas canvas;
	/**
	 * Flag to indicate if the current instance is full-screen.
	 */
	protected boolean fullScreen = false;

	/**
	 * Initialize a VLC instance.
	 * 
	 * @param fullscreen
	 *            if <code>true</code> the instance is initialized as
	 *            full-screen
	 * @throws Exception
	 *             on error
	 * @see #createVLCInstance(Resource, boolean)
	 */
	protected void initVLC(boolean fullscreen) throws Exception {
		synchronized (mutex) {
			// set instance flag
			this.fullScreen = fullscreen;

			libVlc = LibVlc.SYNC_INSTANCE;
	
			logger.info("Starting VLC");
			if (logger.isDebugEnabled()) {
				logger.debug("Fullscreen: " + fullscreen);
				logger.debug("Version: " + libVlc.libvlc_get_version());
				logger.debug("Changeset: " + libVlc.libvlc_get_changeset());
				logger.debug("Compiler: " + libVlc.libvlc_get_compiler());
			}
	
			exception = new LibVlc.libvlc_exception_t();
			libVlc.libvlc_exception_init(exception);
			assertOnException("initVLC.libvlc_exception_init");
	
			createVLCInstance(fullscreen);
	
			libvlc_log = libVlc.libvlc_log_open(libvlc_instance_t, exception);
			assertOnException("initVLC.libvlc_log_open");
		}
	}

	/**
	 * Creates a new VLC instance.
	 * <p>
	 * The instance is stored as a member variable and must be released using
	 * {@link #releaseResources()}.
	 * </p>
	 * 
	 * @throws Exception
	 *             on error
	 */
	protected void createVLCInstance(boolean fullscreen) throws Exception {
		File pluginsPath = new File(Player.getVlcPath() + File.separator
				+ "plugins");
		List<String> args = new ArrayList<String>();
		if (logger.isDebugEnabled()) {
			logger.debug("Creating LibVLC instance.");
		}
		// detailed logging
		if (logger.isTraceEnabled()) {
			args.add("-vvv");
		}
		// no annoying video title
		args.add("--no-video-title-show");
		// reduce bulk VLC log output
		args.add("--quiet-synchro");
		// force aspect ratio so that there are no black borders
		args.add("--aspect-ratio=" + width + ":" + height);
		// no overlays
		args.add("--no-overlay");
		// path to plugins folder
		args.add("--plugin-path=\"" + pluginsPath.getAbsolutePath() + '"');
		// full-screen mode
		args.add(fullscreen ? "--fullscreen" : "--no-fullscreen");
		// init native component
		String[] ma = args.toArray(new String[args.size()]);
		libvlc_instance_t = libVlc.libvlc_new(ma.length, ma, exception);
		assertOnException("createVLCInstance.libvlc_new");
		logger.debug("Initialized LibVLC instance");
	}

	/**
	 * Checks if VLC native code has thrown an exception.
	 * 
	 * @param context
	 *            a message to put the error message into context. This can be a
	 *            descriptive text, the name of the method,...
	 * @throws Exception
	 *             if there was an error
	 */
	protected void assertOnException(String context) throws Exception {
		Integer status = (Integer) libVlc.libvlc_exception_raised(exception);
		if (status.intValue() == 1) {
			String msg = (String) libVlc
					.libvlc_exception_get_message(exception);
			throw new Exception(context + ": " + msg);
		}
	}

	protected void startVideoOnCanvas() throws Exception {
		// only attach to a canvas if not fullscreen
		if (!fullScreen) {
			int drawable = (int) com.sun.jna.Native.getComponentID(canvas);

			if (logger.isInfoEnabled()) {
				logger.info("Drawable retrieved from underlying window ("
						+ drawable + ")");
			}

			libVlc.libvlc_video_set_parent(libvlc_instance_t, drawable,
					exception);
			assertOnException("startVideoOnCanvas.libvlc_video_set_parent");

			logger.debug("Attached the player to the drawable");
		} else {
			libVlc.libvlc_video_set_parent(libvlc_instance_t, 0, exception);
			assertOnException("startVideoOnCanvas.libvlc_video_set_parent");

			logger.debug("Detached the player from any drawable");
		}

		libVlc.libvlc_playlist_play(libvlc_instance_t, -1, 0, null, exception);
		assertOnException("startVideoOnCanvas.libvlc_playlist_play");

		logger.debug("Started media playing");
	}

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

			firstTime = true;
		} catch (Exception e) {
			throw new ComponentException("Unable to initialize component", e);
		}
	}

	@Override
	public void add(Component component) throws ComponentException {
		throw new RuntimeException("Video component is not a container.");
	}

	@Override
	public java.awt.Component getWindowComponent() {
		return canvas;
	}

	protected boolean firstTime;
	protected int countRepaints;

	@Override
	public void repaint() throws ComponentException {
		if (firstTime) {
			countRepaints = 1;
			try {
				startVideoOnCanvas();
			} catch (Exception e) {
				throw new ComponentException("Unable to start video", e);
			}
			firstTime = false;
		}

		if (countRepaints++ % 30 != 0) {
			return;
		}

		countRepaints = 1;
		logger.trace("Starting VLC log dump");
		try {
			LibVlcLogIterator libvlc_log_iterator = libVlc
					.libvlc_log_get_iterator(libvlc_log, exception);
			assertOnException("repaint.libvlc_log_get_iterator");
			try {
				while (true) {
					int hasNext = libVlc.libvlc_log_iterator_has_next(
							libvlc_log_iterator, exception);
					assertOnException("repaint.libvlc_log_iterator_has_next");
					if (hasNext == 0) {
						break;
					}
					libvlc_log_message_t message = new LibVlc.libvlc_log_message_t();
					libVlc.libvlc_log_iterator_next(libvlc_log_iterator,
							message, exception);
					assertOnException("repaint.libvlc_log_iterator_next");
					String msg = message.psz_message;
					int severity = message.i_severity;
					switch (severity) {
					case 0:
						vlcLogger.info(msg);
						break;
					case 1:
						vlcLogger.error(msg);
						break;
					case 2:
						vlcLogger.warn(msg);
						break;
					case 3:
						vlcLogger.debug(msg);
						break;
					default:
						vlcLogger.info(msg + " (Unexpected severity: "
								+ severity + ")");
					}
				}
				libVlc.libvlc_log_clear(libvlc_log, exception);
				assertOnException("repaint.libvlc_log_clear");
			} finally {
				if (libvlc_log_iterator != null) {
					libVlc.libvlc_log_iterator_free(libvlc_log_iterator,
							exception);
					assertOnException("repaint.libvlc_log_iterator_free");
				}
			}
		} catch (Exception e) {
			logger.error("Unable to gather VLC logs", e);
		}
		logger.trace("Completed VLC log dump");
	}

	@Override
	public CompletionStatus isComplete() {
		return super.isComplete();
	}

	@Override
	public void releaseResources() {
		if (libVlc == null) {
			return;
		}
		stopPlayer();
		try {
			// close the log
			// FIXME Commented out because it kills the JVM
			// libVlc.libvlc_log_close(libvlc_log, exception);
			// assertOnException("releaseResources.libvlc_log_close");
			// release the instance
			libVlc.libvlc_release(libvlc_instance_t);
			assertOnException("releaseVLCInstance.libvlc_release");
		} catch (Exception e) {
			logger.error("Unable to release resources. Possible memory leak.",
					e);
		}
		// last resort clean-up, just in case
		ProcessUtils.killAll("vlc.exe");
	}

	/**
	 * @return boolean if the current instance of the VLC player is fullscreen
	 */
	public boolean isFullScreen() {
		return fullScreen;
	}

	/**
	 * Utility method which stops the player.
	 */
	protected void stopPlayer() {
		try {
			// stop player
			libVlc.libvlc_playlist_stop(libvlc_instance_t, exception);
			assertOnException("stopPlayer.libvlc_playlist_stop");
		} catch (Exception e) {
			logger.warn("Could not stop player: " + e.getMessage());
		}
	}

	/**
	 * Utility method to clear the VLC internal playlist.
	 */
	protected void clearVlcPlaylist() {
		try {
			libVlc.libvlc_playlist_clear(libvlc_instance_t, exception);
			assertOnException("releaseVLCInstance.libvlc_playlist_clear");
		} catch (Exception e) {
			logger.warn("Could not clear playlist: " + e.getMessage());
		}
	}

	/**
	 * Adds the given resource to the current VLC internal playlist.
	 * <p>
	 * Adds any playback hint necessary while adding (e.g. fullscreen).
	 * </p>
	 * 
	 * FIXME Hints require the use of <code>libvlc_playlist_add_extended</code>.
	 * 
	 * @param resource
	 *            the resource to add
	 */
	protected void addResourceToVlcPlaylist(Resource resource) throws Exception {
		ContentRegistry registry = ContentRegistry.getContentRegistry();
		String videoFilename = registry.getResourcePath(resource, true);
		// create hint string (unused for now)
		StringBuilder hints = new StringBuilder();
		if (PlayerUtils.isResourceFullScreen(resource)) {
			hints.append("fullscreen").append(' ');
		}
		libVlc.libvlc_playlist_add(libvlc_instance_t, videoFilename,
				videoFilename, exception);
		assertOnException("addResourceToPlaylist.libvlc_playlist_add");

	}

	/**
	 * Adds the given list of resources to the current VLC internal playlist.
	 * <p>
	 * Adds any playback hint necessary while adding (e.g. fullscreen).
	 * </p>
	 * 
	 * FIXME Hints require the use of <code>libvlc_playlist_add_extended</code>.
	 * 
	 * @param resources
	 *            the resources to add
	 */
	protected void addResourcesToVlcPlaylist(List<Resource> resources)
			throws Exception {
		ContentRegistry registry = ContentRegistry.getContentRegistry();
		for (Resource res : resources) {
			String videoFilename = registry.getResourcePath(res, true);
			// create hint string
			StringBuilder hints = new StringBuilder();
			if (PlayerUtils.isResourceFullScreen(res)) {
				hints.append("fullscreen").append(' ');
			}
			libVlc.libvlc_playlist_add(libvlc_instance_t, videoFilename, hints
					.toString(), exception);
			assertOnException("addResourceToPlaylist.libvlc_playlist_add");
		}
	}

	/**
	 * @param fullScreen the fullScreen to set
	 */
	public void setFullScreen(boolean fullScreen) {
		this.fullScreen = fullScreen;
	}
}
